package com.chenhy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns("*") // 允许所有来源
            .setHandshakeHandler(new DefaultHandshakeHandler())
            .addInterceptors(new HttpSessionHandshakeInterceptor())
            .withSockJS(); // 添加SockJS支持
        System.out.println("WebSocketConfig.registerStompEndpoints:" + registry.getClass()); // 获得后端ws路径
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
            .setMessageSizeLimit(8192) // 设置消息大小限制
            .setSendBufferSizeLimit(512 * 1024) // 设置发送缓冲区大小限制
            .setSendTimeLimit(20 * 1000); // 设置发送超时时间
    }
}
