import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export function connectVideoSocket(videoCallback) {
  const socket = new SockJS('ws://localhost:8080/ws'); // 对应后端Endpoint
  const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient.subscribe('/topic/video', (message) => {
        videoCallback(message.body); // 接收帧数据
      });
    },
    onStompError: (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
    }
  });

  stompClient.activate();
  return stompClient;
}
