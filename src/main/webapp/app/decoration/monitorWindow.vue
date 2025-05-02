<template>
  <div class="monitor-container">
    <!-- 视频播放按钮 -->
    <button @click="toggleVideoPlayer" class="monitor-button">
      {{ showVideo ? '关闭监控面板' : '打开监控面板' }}
    </button>

    <!-- Canvas浮层 -->
    <div v-if="showVideo" class="video-overlay">
      <div class="video-player">
        <div class="video-header">
          <h3>无人机位置实时监控</h3>
          <button @click="toggleVideoPlayer" class="close-button">&times;</button>
        </div>
        <canvas ref="canvas" width="640" height="480" class="video-element"></canvas>
        <div class="connection-status">
          连接状态: {{ 
            connectionStatus === 'connected' ? '已连接' : 
            connectionStatus === 'connecting' ? '连接中' : '已断开' 
          }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default defineComponent({
  name: 'MonitorWindow',
  setup() {
    const showVideo = ref(false);
    const canvas = ref<HTMLCanvasElement | null>(null);
    const connectionStatus = ref<'disconnected' | 'connecting' | 'connected'>('disconnected');
    let client: Client | null = null;

    const connectVideoSocket = (videoCallback: (frameData: string) => void) => {
      const socket = new SockJS('ws://localhost:8080/ws');
      console.log('WebSocket连接建立');
      
      // 添加详细的WebSocket状态监听
      socket.onopen = () => {
        console.log('Web Socket Opened');
        connectionStatus.value = 'connecting';
      };
      socket.onclose = () => {
        console.log('Web Socket Closed');
        connectionStatus.value = 'disconnected';
      };
      socket.onerror = (e: Event) => {  // 添加明确的Event类型声明
        console.error('Web Socket Error:', e);
        connectionStatus.value = 'disconnected';
      };

      const stompClient = new Client({
        connectHeaders: {
          'accept-version': '1.2,1.1,1.0',
          'heart-beat': '10000,10000'
        },
        debug: function(str) {
          console.log('STOMP:', str);
        },
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log('STOMP连接成功，订阅/topic/video');
          connectionStatus.value = 'connected';
          stompClient.subscribe('/topic/video', (message) => {
            console.log('收到视频帧数据，长度:', message.body.length);
            videoCallback(message.body);
          });
        },
        onStompError: (frame) => {
          console.error('Broker reported error: ' + frame.headers['message']);
          connectionStatus.value = 'disconnected';
        },
        onWebSocketClose: () => {
          console.log('WebSocket连接关闭');
          connectionStatus.value = 'disconnected';
        },
        // 添加连接失败回调
        onDisconnect: () => {
          console.log('STOMP连接断开');
          connectionStatus.value = 'disconnected';
        }
      });

      stompClient.activate();
      return stompClient;
    };

    const toggleVideoPlayer = () => {
      showVideo.value = !showVideo.value;

      if (showVideo.value && canvas.value) {
        connectionStatus.value = 'connecting';
        const ctx = canvas.value.getContext('2d');
        const img = new Image();

        client = connectVideoSocket((frameData) => {
            console.log("收到数据:", frameData.length);
            connectionStatus.value = 'connected';
            img.onload = () => {
              ctx?.clearRect(0, 0, canvas.value!.width, canvas.value!.height);
              ctx?.drawImage(img, 0, 0, canvas.value!.width, canvas.value!.height);
            };
            img.src = 'data:image/jpeg;base64,' + frameData;
          });

          client.onWebSocketError = () => {
            connectionStatus.value = 'disconnected';
            console.error('WebSocket连接错误');
          };
      } else if (client) {
        client.deactivate();
        connectionStatus.value = 'disconnected';
      }
    };

    onUnmounted(() => {
      if (client) {
        client.deactivate();
      }
    });

    return {
      showVideo,
      canvas,
      toggleVideoPlayer,
      connectionStatus,
    };
  },
});
</script>

<style scoped>
.monitor-container {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1050;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.monitor-button {
  background: linear-gradient(to bottom, #194480, #2174b8);
  border: none;
  border-radius: 3px;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  font-size: 14px;
  font-weight: bold;
  /* font-family: "幼圆"; */
  padding: 4px 8px;
  cursor: pointer;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  pointer-events: auto;
  border-bottom: 2px solid #122e76d1;
  border-top: 2px solid #122e76d1;
}

.monitor-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
}

.monitor-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 3px rgba(0, 0, 0, 0.1);
}

.video-overlay {
  position: relative;
  top: 1vh;
  left: 2.7vw;
  width: 26vw;
  height: 10vh;
  /*background-color: rgba(0, 0, 0, 0.8);
  display: flex;*/
  justify-content: center;
  align-items: center;
  z-index: 2000;
  pointer-events: auto;
}

.video-player {
  width: 80%;
  max-width: 800px;
  background-color: #1e1e1e;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
}

.video-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #111;
  color: white;
}

.video-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.close-button {
  background: none;
  border: none;
  color: #ccc;
  font-size: 24px;
  cursor: pointer;
  padding: 0 5px;
  line-height: 1;
}

.close-button:hover {
  color: white;
}

.video-element {
  width: 100%;
  display: block;
  background-color: black;
}
</style>
