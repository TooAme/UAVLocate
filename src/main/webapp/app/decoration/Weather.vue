<template>
  <div class="weather-container">
    <div v-if="loading" class="alert alert-info">加载中...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="weatherData" class="weather-card">
      <div class="weather-details">
        <p>温度: {{ weatherData.temp || '暂无数据' }}°C</p>
        <p>体感温度: {{ weatherData.feelsLike || '暂无数据' }}°C</p>
        <p>天气状况: {{ weatherData.text || '暂无数据' }}</p>
        <p>风速: {{ weatherData.windSpeed || '暂无数据' }} km/h</p>
        <p>风向: {{ weatherData.windDir || '暂无数据' }} ({{ weatherData.wind360 || '暂无数据' }}°)</p>
        <p>更新时间: {{ weatherData.obsTime || '暂无数据' }}</p>
      </div>
    </div>
    <div v-else class="alert alert-warning">暂无天气数据</div>

    <button @click="refreshWeather" class="btn btn-primary mt-3">刷新数据</button>
  </div>
</template>

<script>
import axios from 'axios';

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
  background: #242424;
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
