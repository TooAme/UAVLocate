<template>
  <div class="weather-container">
    <h2 v-if="weatherData">德阳当前天气</h2>
    <div v-if="loading" class="alert alert-info">加载中...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="weatherData" class="weather-card">
      <div class="weather-details">
        <p>温度: {{ weatherData.temp }}°C</p>
        <p>体感温度: {{ weatherData.feelsLike }}°C</p>
        <p>天气状况: {{ weatherData.text }}</p>
        <p>风速: {{ weatherData.windSpeed }} km/h</p>
        <p>风向: {{ weatherData.windDir }} ({{ weatherData.wind360 }}°)</p>
        <p>更新时间: {{ formatTime(weatherData.obsTime) }}</p>
      </div>
    </div>

    <button @click="refreshWeather" class="btn btn-primary mt-3">刷新数据</button>
  </div>
</template>

<script>
import axios from 'axios';
import { format } from 'date-fns';

export default {
  name: 'Weather',
  data() {
    return {
      weatherData: null,
      loading: false,
      error: null,
    };
  },
  created() {
    this.fetchWeather();
  },
  methods: {
    async fetchWeather() {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.get(`api/weather/101272001`);
        this.weatherData = response.data.now;
      } catch (err) {
        console.error('获取天气数据失败:', err);
        this.error = '获取天气数据失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
    refreshWeather() {
      this.fetchWeather();
    },
    formatTime(timestamp) {
      return format(new Date(timestamp), 'yyyy-MM-dd HH:mm');
    },
    // getWeatherIcon(weatherText) {
    //   const icons = {
    //     晴: 'sunny.png',
    //     多云: 'cloudy.png',
    //     雨: 'rainy.png',
    //     扬沙: 'sand.png',
    //     // 添加更多天气图标映射
    //   };
    //   return `/content/images/weather/${icons[weatherText] || 'default.png'}`;
    // },
  },
};
</script>

<style scoped>
.weather-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.weather-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  margin-top: 20px;
}

.weather-icon img {
  width: 80px;
  height: 80px;
}

.weather-details {
  flex: 1;
}

.weather-details p {
  margin: 5px 0;
}
</style>
