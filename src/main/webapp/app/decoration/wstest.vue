<template>
  <div>
    <h1>WebSocket</h1>
    <div class="input-group">
      <input v-model="message" placeholder="输入测试消息" />
      <button @click="sendMessage">发送</button>
      <button @click="sendTestMessage">发送测试消息</button>
    </div>
    <div class="message-box">
      <div v-for="(msg, index) in messages" :key="index" class="message">
        <span class="timestamp">{{ msg.timestamp }}</span>
        <span class="content">{{ msg.content }}</span>
      </div>
    </div>
    <div :class="'status-' + connectionStatus" class="status-indicator">
      连接状态: {{ connectionStatus === 'connected' ? '已连接' : connectionStatus === 'disconnected' ? '已断开' : '连接中' }}
    </div>
  </div>
</template>

<script>
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default {
  data() {
    return {
      message: '',
      messages: [],
      client: null,
      connectionStatus: 'disconnected',
    };
  },
  mounted() {
    this.client = Stomp.over(new SockJS('http://localhost:8080/ws'));
    this.client.connect(
      {},
      frame => {
        console.log('Connected: ' + frame);
        this.connectionStatus = 'connected';
        this.addMessage('系统', '已连接到WebSocket服务器');
        this.client.subscribe('/topic/greetings', message => {
          this.messages.push({
            sender: '服务器',
            content: JSON.parse(message.body).content,
            timestamp: new Date().toLocaleTimeString(),
          });
        });
      },
      error => {
        console.error('连接错误:', error);
        this.connectionStatus = 'disconnected';
        this.addMessage('系统', '连接失败: ' + error.message);
      },
    );
  },
  methods: {
    sendMessage() {
      if (!this.message.trim()) {
        this.addMessage('系统', '消息不能为空');
        return;
      }

      if (this.connectionStatus !== 'connected') {
        this.addMessage('系统', '发送失败: 未连接到服务器');
        return;
      }

      try {
        this.client.send('/app/hello', {}, JSON.stringify({ content: this.message }));
        this.addMessage('我', this.message);
        this.message = '';
      } catch (error) {
        console.error('发送消息失败:', error);
        this.addMessage('系统', '发送失败: ' + error.message);
      }
    },

    sendTestMessage() {
      const testMessages = ['你在看吗', '你在看对吧', '你真的在看吗'];

      if (this.connectionStatus !== 'connected') {
        this.addMessage('系统', '发送失败: 未连接到服务器');
        return;
      }

      testMessages.forEach(msg => {
        try {
          this.client.send('/app/hello', {}, JSON.stringify({ content: msg }));
          this.addMessage('测试', msg);
        } catch (error) {
          console.error('发送测试消息失败:', error);
          this.addMessage('系统', '发送测试消息失败: ' + error.message);
        }
      });
    },

    addMessage(sender, content) {
      this.messages.push({
        sender,
        content,
        timestamp: new Date().toLocaleTimeString(),
      });
    },
  },
  beforeUnmount() {
    if (this.client) {
      this.client.disconnect();
      this.connectionStatus = 'disconnected';
    }
  },
};
</script>
