package com.chenhy.web.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        Thread.sleep(1000); // 模拟延迟
        return "Server received: " + message;
    }
    @MessageMapping("/video")  // 接收/app/video的消息
    @SendTo("/topic/video")    // 转发到前端订阅的地址
    public String handleFrame(String frameData) {
        return frameData;
    }
}
