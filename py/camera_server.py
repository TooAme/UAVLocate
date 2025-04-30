import cv2
import numpy as np
from openni import openni2
import websockets
import asyncio
import base64
import logging
import os
from websockets.exceptions import InvalidStatusCode, ConnectionClosed
import requests

# 获取JWT token示例
auth_url = "http://localhost:8080/api/authenticate"
credentials = {
    "username": "admin",
    "password": "admin"
}
response = requests.post(auth_url, json=credentials)
token = response.json().get("id_token")  # 根据实际API响应调整
# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger('OrbbecDepthStream')

async def send_depth_frame(ws, frame_data):
    """发送深度帧数据"""
    try:
        # 归一化深度数据并转换为伪彩色
        normalized_depth = cv2.normalize(frame_data, None, 0, 255, cv2.NORM_MINMAX)
        colored_depth = cv2.applyColorMap(np.uint8(normalized_depth), cv2.COLORMAP_JET)

        # 压缩图像
        _, buffer = cv2.imencode('.jpg', colored_depth, [
            cv2.IMWRITE_JPEG_QUALITY, 85,
            cv2.IMWRITE_JPEG_PROGRESSIVE, 1
        ])

        # 修改消息目标路径为/video
        message = (
            "SEND\n"
            "destination:/app/video\n"  # 匹配@MessageMapping("/video")
            "content-type:image/jpeg\n\n" +
            base64.b64encode(buffer).decode() +
            "\x00"
        )
        await ws.send(message)
    except Exception as e:
        logger.error(f"发送深度帧失败: {str(e)}")

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
                timeout=15,
                # Remove all extra headers temporarily
            ) as ws:
                logger.info("WebSocket连接成功")
                # STOMP握手(添加SockJS心跳配置)
                await ws.send("CONNECT\naccept-version:1.2\nheart-beat:10000,10000\nhost:localhost\n\n\x00")
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
