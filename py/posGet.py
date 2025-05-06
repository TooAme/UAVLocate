import cv2
import numpy as np
import json
import websockets
import asyncio
import time
from ultralytics import YOLO
from openni import openni2

class DepthObjectDetector:
    def __init__(self, camera_matrix, websocket_url='ws://localhost:8080/ws/websocket'):
        print("正在加载YOLOv8模型...")
        try:
            self.model = YOLO('yolov8n.pt')
            print("YOLOv8模型加载成功")
        except Exception as e:
            print(f"模型加载失败: {str(e)}")
            raise
        self.camera_matrix = camera_matrix
        self.websocket_url = websocket_url

    async def process_frame(self, rgb_frame, depth_frame):
        """处理单帧图像并返回检测结果"""
        # 预处理Astra Pro深度帧
        depth_frame = cv2.normalize(depth_frame, None, 0, 255, cv2.NORM_MINMAX)
        depth_frame = np.uint8(depth_frame)

        # 使用YOLOv8检测物体
        results = self.model(rgb_frame)
        detections = []

        # 创建显示窗口并设置属性
        cv2.namedWindow('Object Detection', cv2.WINDOW_NORMAL)
        cv2.setWindowProperty('Object Detection', cv2.WND_PROP_TOPMOST, 1)

        # 添加检测结果日志
        print(f"YOLOv8检测结果 - 检测到{len(results[0].boxes)}个物体")

        if len(results[0].boxes) == 0:
            print(f"[{time.strftime('%H:%M:%S')}] 未检测到物体")
            cv2.imshow('Object Detection', rgb_frame)
            cv2.waitKey(1) & 0xFF
            return detections
            
        # 找出置信度最高的检测框
        max_conf_box = max(results[0].boxes, key=lambda box: float(box.conf[0]))
        bbox = max_conf_box.xyxy[0].cpu().numpy()
        cls_id = int(max_conf_box.cls[0])
        conf = float(max_conf_box.conf[0])

        # 绘制边界框和标签
        cv2.rectangle(rgb_frame, (int(bbox[0]), int(bbox[1])),
                     (int(bbox[2]), int(bbox[3])), (0, 255, 0), 2)
        label = f"{self.model.names[cls_id]}: {conf:.2f}"
        cv2.putText(rgb_frame, label, (int(bbox[0]), int(bbox[1]) - 10),
                   cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

        # 计算三维坐标
        x, y, z = self._calculate_3d_coordinates(depth_frame, bbox)
        z = z / 1000.0

        detections.append({
            'class_id': cls_id,
            'class_name': self.model.names[cls_id],
            'confidence': conf,
            'bbox': bbox.tolist(),
            'position_3d': [x, y, z]
        })

        # 添加坐标日志输出
        print(f"检测到最高置信度物体: {self.model.names[cls_id]}")
        print(f"二维坐标: ({int((bbox[0] + bbox[2]) / 2)}, {int((bbox[1] + bbox[3]) / 2)})")
        print(f"三维坐标: X={x:.2f}单位距离, Y={y:.2f}单位距离, Z={z:.2f}单位距离")
        print(f"置信度: {conf:.2%}")
        print("-" * 40)

        # 显示结果并确保窗口刷新
        cv2.imshow('Object Detection', rgb_frame)
        cv2.waitKey(1) & 0xFF

        return detections

    def _calculate_3d_coordinates(self, depth_map, bbox):
        """计算物体中心点的三维坐标"""
        x_center = int((bbox[0] + bbox[2]) / 2)
        y_center = int((bbox[1] + bbox[3]) / 2)

        # 从深度图获取深度值
        depth = depth_map[y_center, x_center]

        # 相机内参
        fx = self.camera_matrix[0, 0]
        fy = self.camera_matrix[1, 1]
        cx = self.camera_matrix[0, 2]
        cy = self.camera_matrix[1, 2]

        # 转换为三维坐标
        Z = depth
        X = (x_center - cx) * Z / fx
        Y = (y_center - cy) * Z / fy

        return X, Y, Z

    async def send_to_backend(self, data):
        """通过WebSocket发送数据到后端，使用STOMP协议"""
        try:
            async with websockets.connect(
                self.websocket_url,
                ping_interval=30,
                timeout=15
            ) as websocket:
                # 完整的STOMP握手
                await websocket.send(
                    "CONNECT\n"
                    "accept-version:1.0,1.1,1.2\n"
                    "heart-beat:10000,10000\n"
                    "host:localhost\n\n"
                    "\x00"
                )
                
                # 等待CONNECTED帧响应
                response = await websocket.recv()
                if "CONNECTED" not in response:
                    raise Exception("STOMP握手失败")
                
                if data and len(data) > 0:
                    position = data[0]['position_3d']
                    coord_str = f"{position[0]:.2f},{position[1]:.2f},{position[2]:.2f}"
                    
                    headers = (
                        "SEND\n"
                        "destination:/app/data\n"
                        "content-type:text/plain\n"
                        "content-length:" + str(len(coord_str)) + "\n"
                        "\n"
                    )
                    message = headers + coord_str + "\x00"
                    await websocket.send(message.encode('utf-8'))
                    print(f"[{time.strftime('%H:%M:%S')}] 数据发送成功")
        except Exception as e:
            print(f"[{time.strftime('%H:%M:%S')}] 发送失败: {str(e)}")

# 示例用法
if __name__ == "__main__":
    # 示例相机内参矩阵(需要根据实际相机校准)
    camera_matrix = np.array([
        [640, 0, 320],
        [0, 480, 240],
        [0, 0, 1]
    ])

    detector = DepthObjectDetector(camera_matrix)

    # 实际使用时应该从相机获取实时帧
    # 这里只保留框架，实际应用时需要替换为相机捕获代码
    async def process_and_send():
        try:
            print(f"[{time.strftime('%H:%M:%S')}] 正在初始化OpenNI2...")
            openni2.initialize()
            print("OpenNI2初始化成功")

            dev = openni2.Device.open_any()
            print(f"已连接设备: {dev.get_device_info().name}")

            color_stream = dev.create_color_stream()
            depth_stream = dev.create_depth_stream()
            color_stream.start()
            depth_stream.start()
            print("视频流已启动")

            # 创建显示窗口
            cv2.namedWindow('Object Detection', cv2.WINDOW_NORMAL)
            cv2.resizeWindow('Object Detection', 800, 600)

            last_detection_time = time.time()

            while True:
                # 获取帧数据后添加日志
                print(f"[{time.strftime('%H:%M:%S')}] 正在获取视频帧...")
                cap = cv2.VideoCapture(0)
                ret, rgb_frame = cap.read()
                print(f"[{time.strftime('%H:%M:%S')}] color帧获取完成")
                depth_frame = depth_stream.read_frame()
                depth_frame = depth_frame.get_buffer_as_uint16()
                depth_frame = np.frombuffer(depth_frame, dtype=np.uint16).reshape((480, 640))
                print(f"[{time.strftime('%H:%M:%S')}] 深度帧获取完成")


                # 添加帧数据检查
                if rgb_frame.size == 0:
                    print(f"[{time.strftime('%H:%M:%S')}] 警告: 获取到空的RGB帧")
                    continue

                # 处理帧并显示
                detections = await detector.process_frame(rgb_frame, depth_frame)
                
                # 发送数据到后端
                if detections:
                    await detector.send_to_backend(detections)

                # 未检测到物体且超过3秒时输出提示
                if len(detections) == 0 and time.time() - last_detection_time > 3:
                    print("未检测到物体")
                    last_detection_time = time.time()
                elif len(detections) > 0:
                    last_detection_time = time.time()

                # 显示处理后的帧
                try:
                    cv2.imshow('Object Detection', rgb_frame)
                    print(f"[{time.strftime('%H:%M:%S')}] 窗口刷新成功")
                except Exception as e:
                    print(f"[{time.strftime('%H:%M:%S')}] 窗口刷新失败: {str(e)}")

                # 控制帧率并检测退出键
                if cv2.waitKey(30) & 0xFF == 27:
                    break

        except Exception as e:
            print(f"发生错误: {str(e)}")
        finally:
            cv2.destroyAllWindows()
            color_stream.stop()
            depth_stream.stop()
            openni2.unload()
            print("程序退出")

    asyncio.run(process_and_send())
