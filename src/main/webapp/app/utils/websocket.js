import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export function connectVideoSocket(videoCallback) {
    const socket = new SockJS('ws://localhost:8080/ws');
    console.log('WebSocket连接建立');
    
    socket.onopen = () => console.log('WebSocket连接已打开');
    socket.onclose = (e) => console.log('WebSocket连接关闭:', e);
    socket.onerror = (e) => console.error('WebSocket错误:', e);
    
    const stompClient = new Client({
        debug: function(str) {
            console.log('STOMP:', str);
        },
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      console.log('STOMP连接成功，订阅/topic/video');
      stompClient.subscribe('/topic/video', (message) => {
        console.log('收到视频帧数据，长度:', message.body.length);
        videoCallback(message.body);
      });
    },
    onStompError: (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
    }
  });

  stompClient.activate();
  return stompClient;
}
