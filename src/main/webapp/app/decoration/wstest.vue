<template>
  <div class="ws-container">
    <h1 class="ws-title">WebSocket</h1>
    <div class="input-group">
      <input v-model="message" placeholder="输入测试消息" class="ws-input" />
      <button @click="sendMessage" class="ws-button">发送</button>
      <button @click="sendTestMessage" class="ws-button">发送测试消息</button>
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

<style scoped>
.ws-container {
  background: linear-gradient(to bottom, #000c27aa, #000000e2);
  border-radius: 8px;
  padding: 15px;
  width: 300px;
  color: white;
  font-family: '微软雅黑';
  position: fixed;
  max-height: 30%;
  left: 46.8%;
  transform: translateY(150%);
}

.ws-title {
  font-size: 16px;
  text-align: center;
  margin-bottom: 15px;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 5px;
}

.input-group {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.ws-input {
  flex: 1;
  padding: 6px 10px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  color: white;
}

.ws-input:focus {
  outline: none;
  border-color: #62e1f1;
}

.ws-button {
  background: linear-gradient(to bottom, #194480, #2174b8);
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  color: white;
  font-size: 13px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.ws-button:hover {
  background: linear-gradient(to bottom, #215596, #2a8bd8);
}

.message-box {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 10px;
}

.message {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 13px;
}

.timestamp {
  color: #ccc;
  margin-right: 10px;
}

.status-indicator {
  font-size: 12px;
  text-align: center;
  padding: 4px;
  border-radius: 4px;
}

.status-connected {
  background: rgba(40, 167, 69, 0.2);
  color: #28a745;
}

.status-disconnected {
  background: rgba(220, 53, 69, 0.2);
  color: #dc3545;
}
</style>
