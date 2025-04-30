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
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
import { connectVideoSocket } from '@/utils/websocket';

export default defineComponent({
  name: 'MonitorWindow',
  setup() {
    const showVideo = ref(false);
    const canvas = ref<HTMLCanvasElement | null>(null);
    let client: Client | null = null;

    const toggleVideoPlayer = () => {
      showVideo.value = !showVideo.value;

      if (showVideo.value && canvas.value) {
        const ctx = canvas.value.getContext('2d');
        const img = new Image();

        client = connectVideoSocket((frameData) => {
          img.onload = () => {
            ctx?.clearRect(0, 0, canvas.value!.width, canvas.value!.height);
            ctx?.drawImage(img, 0, 0);
          };
          img.src = 'data:image/jpeg;base64,' + frameData;
        });
      } else if (client) {
        client.deactivate();
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
