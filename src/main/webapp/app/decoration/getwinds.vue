<template>
  <div class="winds-container">
    <div v-if="weatherData" class="winds-info">
      <div class="wind-item">
        <span class="label">风速:</span>
        <span class="value">{{ weatherData.now.windSpeed }} km/h</span>
      </div>
      <div class="wind-item">
        <span class="label">风向:</span>
        <span class="value">{{ weatherData.now.wind360 }}°</span>
      </div>
    </div>
    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-else class="loading-message">加载中...</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import axios, { AxiosError } from 'axios';

interface WeatherNow {
  windSpeed: string;
  wind360: string;
}

interface WeatherData {
  now: WeatherNow;
}

interface ApiError {
  message: string;
}

export default defineComponent({
  name: 'GetWinds',
  setup() {
    const weatherData = ref<WeatherData | null>(null);
    const error = ref<string | null>(null);

    const fetchWeather = async () => {
      try {
        console.log('开始获取天气数据...');
        const response = await axios.get<WeatherData>('/api/weather/101272001');
        console.log('天气数据响应:', response.data);

        if (!response.data || !response.data.now) {
          throw new Error('天气数据格式不正确');
        }

        weatherData.value = response.data;
        error.value = null;
      } catch (err) {
        console.error('获取天气数据失败:', err);
        if (axios.isAxiosError(err)) {
          const axiosError = err as AxiosError<ApiError>;
          error.value = axiosError.response?.data?.message || '获取天气数据失败';
        } else {
          error.value = '获取天气数据失败';
        }
      }
    };

    onMounted(() => {
      console.log('组件已挂载，开始获取天气数据');
      fetchWeather();
      // 每5分钟更新一次数据
      setInterval(fetchWeather, 5 * 60 * 1000);
    });

    return {
      weatherData,
      error,
    };
  },
});
</script>

<style scoped>
.winds-container {
  background: rgba(0, 0, 0, 0.7);
  border-radius: 8px;
  padding: 15px;
  color: white;
  font-family: '微软雅黑';
  min-width: 200px;
}

.winds-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.wind-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.label {
  color: #ccc;
  font-weight: 500;
}

.value {
  color: #fff;
  font-weight: 600;
}

.error-message {
  color: #ff4444;
  text-align: center;
  padding: 10px;
  font-size: 14px;
}

.loading-message {
  color: #ccc;
  text-align: center;
  padding: 10px;
}
</style>
