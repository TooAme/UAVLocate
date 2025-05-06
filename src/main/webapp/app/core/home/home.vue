<template>
  <div class="home">
    <div class="text-link">
      <a href="../../../../../../README.html">更新日志</a>
    </div>
    <header-image class="header-image" />
    <ws-test class="ws-test" />
    <test-statics class="test-statics" />
    <final-data class="final-data" />
    <div class="sidebar left-sidebar">
      <div class="left-sidebar-under"></div>
      <monitor-window class="monitor-window" />
      <weather-window class="weather-window" />
    </div>
    <div class="sidebar right-sidebar">
      <div class="right-sidebar-under"></div>
      <scroll-box class="scroll-box" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import HeaderImage from '@/decoration/headerImage.vue';
import GetWinds from '@/decoration/getwinds.vue';
import ScrollBox from '@/decoration/scrollBox.vue';
import WeatherWindow from '@/decoration/weatherWindow.vue';
import MonitorWindow from '@/decoration/monitorWindow.vue';
import TestStatics from '@/decoration/testStatics.vue';
import FinalData from '@/decoration/finalData.vue';
import WsTest from '@/decoration/wstest.vue';

export default defineComponent({
  name: 'Home',
  components: {
    HeaderImage,
    GetWinds,
    ScrollBox,
    WeatherWindow,
    MonitorWindow,
    TestStatics,
    FinalData,
    WsTest,
  },
});
</script>

<style scoped>
.home {
  height: 100vh;
  position: fixed;
  width: 100%;
}

.header-image {
  pointer-events: none;
  z-index: 1000;
}

.text-link {
  position: fixed;
  top: 32px;
  left: 355px;
  z-index: 1001;
  font-size: 10px;
  color: #002742;
}

.text-link a {
  color: #1395c1;
  text-decoration: none;
  font-family: '幼圆';
}

.text-link a:hover {
  text-decoration: underline;
}

.winds-position {
  position: fixed;
  top: 200px;
  right: 20px;
  z-index: 1001;
}

.scroll-box {
  position: absolute;
  top: 0.9vh;
  left: 0.7vw;
  z-index: 1010;
}

.weather-window {
  z-index: 1020;
}

.monitor-window {
  z-index: 1030;
}

.test-statics {
  z-index: 1040;
}

.final-data {
  z-index: 1050;
  pointer-events: auto;
}

.sidebar {
  color: rgba(255, 255, 255, 0.767);
  display: flex;
  z-index: 1000;
  pointer-events: none;
  background-blend-mode: multiply;
}

.left-sidebar-under {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 23.9vw;
  left: 0;
  background-image: url('images/最底下矩形框.png');
  background-size: cover;
}

.left-sidebar {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 22.9vw;
  left: 0;
  background-image: url('images/第二层矩形框翻转.png');
  background-size: cover;
}

.right-sidebar-under {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 32.4vw;
  right: 0;
  background-image: url('images/最底下矩形框翻转.png');
  background-size: cover;
}

.right-sidebar {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 31.4vw;
  right: 0;
  background-image: url('images/第二层矩形框.png');
  background-size: cover;
}

.ws-test {
  position: fixed;
  top: 160px;
  right: 40%;
  z-index: 10000;
}
</style>
