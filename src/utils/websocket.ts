import { Client } from '@stomp/stompjs';

export function connectVideoSocket(callback: (data: string) => void): Client {
  const client = new Client({
    brokerURL: 'ws://localhost:8080/ws',
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = () => {
    client.subscribe('/topic/video', (message) => {
      callback(message.body);
    });
  };

  client.activate();
  return client;
}