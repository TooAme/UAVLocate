<template>
  <div class="test-statics-container">
    <div class="form-title">无人机位置（测试数据）写入</div>
    <div class="form-container">
      <div class="form-row">
        <label>&nbsp;&nbsp;&nbsp;&nbsp;X 轴坐标:</label>
        <input v-model="posX" type="number" step="0.01" class="form-input" />
      </div>
      <div class="form-row">
        <label>&nbsp;&nbsp;&nbsp;&nbsp;Y 轴坐标:</label>
        <input v-model="posY" type="number" step="0.01" class="form-input" />
      </div>
      <div class="form-row">
        <label>&nbsp;&nbsp;&nbsp;&nbsp;Z 轴坐标:</label>
        <input v-model="posZ" type="number" step="0.01" class="form-input" />
      </div>
      <button @click="saveData" class="save-button">写入坐标</button>
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import StaticsService from '@/entities/statics/statics.service';
import { Statics } from '@/shared/model/statics.model';
import axios from 'axios';

export default defineComponent({
  name: 'TestStatics',
  setup() {
    const posX = ref<number | null>(0);
    const posY = ref<number | null>(0);
    const posZ = ref<number | null>(1);
    const message = ref('');
    const messageType = ref('');
    const staticsService = new StaticsService();
    const weatherData = ref<WeatherNow | null>(null);
    const weatherError = ref<string | null>(null);

    const saveData = async () => {
      if (posX.value === null || posY.value === null || posZ.value === null) {
        message.value = '请填写所有坐标';
        messageType.value = 'error';
        return;
      }

      try {
        message.value = '保存中...';
        messageType.value = 'info';
        const response = await axios.get<WeatherResponse>('api/weather/101272001');
        if (response.data && response.data.now) {
          weatherData.value = response.data.now;
        } else {
          weatherError.value = '获取天气数据格式不正确';
        }
        // 创建 Statics 实体对象
        const staticsData = new Statics(
          undefined, // id，新创建的不需要id
          new Date(), // time
          Math.round(posX.value), // posX
          Math.round(posY.value), // posY
          Math.round(posZ.value), // posZ
          weatherData.value?.windSpeed || 0, // windSpeed
          weatherData.value?.wind360 || 0, // windDirection
        );

        console.log('准备保存的坐标:', staticsData);

        // 使用 staticsService 保存数据
        const result = await staticsService.create(staticsData);
        console.log('保存成功，响应:', result);

        // 更新消息
        message.value = '坐标写入成功';
        messageType.value = 'success';

        // 3秒后清除消息
        setTimeout(() => {
          message.value = '';
        }, 3000);
      } catch (error: any) {
        console.error('保存坐标失败:', error);
        // 错误处理
        let errorMsg = '保存失败，请重试';
        if (error.message) {
          errorMsg = `保存失败: ${error.message}`;
        }
        // 如果是认证错误，提示用户登录
        if (error.response && error.response.status === 401) {
          errorMsg = '您未登录或会话已过期，请重新登录后再试';
        }

        message.value = errorMsg;
        messageType.value = 'error';
      }
    };

    return {
      posX,
      posY,
      posZ,
      message,
      messageType,
      saveData,
    };
  },
});
</script>

<style scoped>
.test-statics-container {
  position: fixed;
  bottom: 20px;
  left: 35%;
  transform: translateX(-50%);
  width: 250px;
  /* background: rgba(0, 11, 34, 0.7); */
  background: linear-gradient(to bottom, #000c27aa, #000000e2);
  border-radius: 6px;
  padding: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  color: white;
  z-index: 1040;
  pointer-events: auto;
}

.form-title {
  font-size: 14px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 5px;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-row label {
  font-size: 12px;
  flex: 1;
}

.form-input {
  width: 80px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  color: white;
  font-size: 12px;
}

.form-input:focus {
  outline: none;
  border-color: #62e1f1;
  background: rgba(255, 255, 255, 0.15);
}

.save-button {
  margin-top: 10px;
  background: linear-gradient(to bottom, #194480, #2174b8);
  border: none;
  border-radius: 4px;
  padding: 6px 0;
  color: white;
  font-size: 13px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-button:hover {
  background: linear-gradient(to bottom, #215596, #2a8bd8);
}

.message {
  margin-top: 8px;
  padding: 5px;
  border-radius: 4px;
  font-size: 12px;
  text-align: center;
}

.success {
  background: rgba(40, 167, 69, 0.7);
}

.error {
  background: rgba(220, 53, 69, 0.7);
}

.info {
  background: rgba(23, 162, 184, 0.7);
}
</style>
