package com.chenhy.web.websocket;

import com.chenhy.service.StaticsService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    private final StaticsService staticsService;

    public WebSocketController(StaticsService staticsService) {
        this.staticsService = staticsService;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        log.debug("Received message: {}", message);
        Thread.sleep(1000); // 模拟延迟
        return "Server received: " + message;
    }

    @MessageMapping("/video")
    @SendTo("/topic/video")
    public String handleFrame(String frameData) {
        log.debug("收到消息，长度: {}", frameData.length());  // 添加接收日志
        if(frameData.length() < 100) {
            log.debug("消息片段: {}", frameData.substring(0, 50));
        }
        return frameData;
    }

    @MessageMapping("/data")
    public void handleData(String data) {
        log.info("收到坐标数据: {}", data);
        try {
            String[] coords = data.split(",");
            if(coords.length == 3) {
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                double z = Double.parseDouble(coords[2]);

                // 跳过所有坐标为0.00的数据
                if(x == 0.00 && y == 0.00 && z == 0.00) {
                    log.debug("跳过无效坐标数据: {}", data);
                    return;
                }

                log.info("解析后坐标 - X: {}, Y: {}, Z: {}", x, y, z);
                staticsService.addNewStatics(x,y,z);
            }
        } catch (Exception e) {
            log.error("坐标数据解析错误: " + data, e);
        }
    }
}
