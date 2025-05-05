import cv2
import numpy as np
from openni import openni2
import websockets
import asyncio
import base64
import logging
import os
from websockets.exceptions import InvalidStatusCode, ConnectionClosed

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger('OrbbecDepthStream')

async def send_depth_frame(ws, frame_data):
    try:
        # 将深度数据转换为8位灰度图像
        frame_normalized = cv2.normalize(frame_data, None, 0, 255, cv2.NORM_MINMAX)
        frame_8bit = np.uint8(frame_normalized)

        # 编码为JPEG格式
        _, buffer = cv2.imencode('.jpg', frame_8bit)
        jpg_as_text = base64.b64encode(buffer.tobytes()).decode('utf-8')  # 添加.tobytes()转换

        headers = (
            "SEND\n"
            "destination:/app/video\n"
            "content-type:text/plain\n"
            "content-length:" + str(len(jpg_as_text)) + "\n"
            "\n"
        ).encode('utf-8')

        message = headers + jpg_as_text.encode('utf-8') + b"\x00"
        logger.info(f"准备发送数据，长度: {len(buffer)}")  # 添加发送前日志
        await ws.send(message)
        logger.info("数据已发送")  # 确认发送完成
    except Exception as e:
        logger.error(f"发送失败详情: {str(e)}", exc_info=True)

async def depth_streamer():
    """深度图像流主逻辑"""
    dev = None
    depth_stream = None
    retry_count = 0
    max_retries = 5

    while retry_count < max_retries:
        try:
            logger.info("开始初始化Orbbec设备...")
            openni2.initialize()
            dev = openni2.Device.open_any()
            logger.info("设备打开成功")
            depth_stream = dev.create_depth_stream()
            depth_stream.start()
            logger.info("深度流启动成功")

            # WebSocket连接配置
            logger.info("尝试连接WebSocket...")
            async with websockets.connect(
                'ws://localhost:8080/ws/websocket',
                ping_interval=30,
                timeout=15
            ) as ws:
                logger.info("WebSocket连接成功")
                # 修改STOMP握手消息，移除认证头
                await ws.send(
                    "CONNECT\n"
                    "accept-version:1.0,1.1,1.2\n"
                    "heart-beat:10000,10000\n"
                    "host:localhost\n\n"
                    "\x00"
                )
                retry_count = 0

                # 主循环
                while True:
                    try:
                        frame = depth_stream.read_frame()
                        frame_data = np.frombuffer(frame.get_buffer_as_uint16(), dtype=np.uint16)
                        frame_data = frame_data.reshape((frame.height, frame.width))

                        await send_depth_frame(ws, frame_data)
                        await asyncio.sleep(0.03)  # ~30fps

                    except ConnectionClosed:
                        logger.warning("连接断开，尝试重连...")
                        break

        except Exception as e:
            retry_count += 1
            logger.error(f"深度流错误({retry_count}/{max_retries}): {str(e)}", exc_info=True)
            await asyncio.sleep(5)  # 等待5秒后重试

        finally:
            if depth_stream:
                depth_stream.stop()
            if dev:
                openni2.unload()
            logger.info("深度流资源已释放")

    logger.error(f"达到最大重试次数({max_retries})，程序终止")

if __name__ == "__main__":
    try:
        if os.name == 'nt':
            asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
        asyncio.get_event_loop().run_until_complete(depth_streamer())
    except Exception as e:
        logger.error(f"主程序异常: {str(e)}", exc_info=True)
        input("按Enter键退出...")
