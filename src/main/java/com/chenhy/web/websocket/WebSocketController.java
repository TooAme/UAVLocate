package com.chenhy.web.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

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
}
