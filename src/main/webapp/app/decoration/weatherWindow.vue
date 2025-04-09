<template>
  <div class="weather-container">
    <div v-if="weatherData" class="weather-data">
      <div class="weather-item">
        <span>位置:</span>
        <span>中国民用航空飞行学院</span>
      </div>
      <div class="weather-item">
        <span>风速:</span>
        <span>{{ weatherData.windSpeed }} km/h</span>
      </div>
      <div class="weather-item">
        <span>风向:</span>
        <span>{{ weatherData.wind360 }}°</span>
      </div>
      <div class="weather-item">
        <span>更新时间:</span>
        <span>{{ weatherData.obsTime }}</span>
      </div>
    </div>
    <div v-else-if="weatherError" class="weather-error">
      {{ weatherError }}
    </div>
    <div v-else-if="isLoading" class="weather-loading">加载中...</div>
    <button @click="fetchWeatherData" class="weather-button">刷新风力数据</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

interface WeatherNow {
  temp: string;
  windSpeed: string;
  wind360: string;
  obsTime: string;
  [key: string]: any;
}

interface WeatherResponse {
  now: WeatherNow;
  [key: string]: any;
}

export default defineComponent({
  name: 'WeatherWindow',
  setup() {
    const weatherData = ref<WeatherNow | null>(null);
    const weatherError = ref<string | null>(null);
    const isLoading = ref(false);

    const fetchWeatherData = async () => {
      isLoading.value = true;
      weatherError.value = null;

      try {
        const response = await axios.get<WeatherResponse>('api/weather/101272001');
        if (response.data && response.data.now) {
          weatherData.value = response.data.now;
        } else {
          weatherError.value = '获取天气数据格式不正确';
        }
      } catch (error) {
        console.error('获取天气数据失败:', error);
        weatherError.value = '获取天气数据失败，请稍后重试';
      } finally {
        isLoading.value = false;
      }
    };

    return {
      weatherData,
      weatherError,
      isLoading,
      fetchWeatherData,
    };
  },
});
</script>

<style scoped>
.weather-container {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1050;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 240px;
}

.weather-button {
  background: linear-gradient(to bottom, #194480, #2174b8);
  border: none;
  border-radius: 3px;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  font-size: 14px;
  font-weight: bold;
  padding: 4px 8px;
  cursor: pointer;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  pointer-events: auto;
  margin-top: 10px;
  border-bottom: 2px solid #122e76d1;
  border-top: 2px solid #122e76d1;
}

.weather-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
}

.weather-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 3px rgba(0, 0, 0, 0.1);
}

.weather-data {
  background: rgba(0, 0, 0, 0.7);
  border-radius: 6px;
  padding: 10px;
  width: 100%;
  color: white;
  margin-bottom: 5px;
  font-size: 13px;
}

.weather-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.weather-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.weather-error {
  background: rgba(220, 53, 69, 0.8);
  color: white;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
  width: 100%;
  margin-bottom: 5px;
  font-size: 13px;
}

.weather-loading {
  color: white;
  text-align: center;
  width: 100%;
  margin-bottom: 5px;
  font-size: 13px;
}
</style>
