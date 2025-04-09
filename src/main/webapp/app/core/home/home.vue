<template>
  <div class="home">
    <header-image class="header-image" />
    <div class="sidebar left-sidebar">
      <div class="left-sidebar-under"></div>
      <monitor-window class="monitor-window" />
      <test-statics class="test-statics" />
      <weather-window class="weather-window" />
      <final-data class="final-data" />
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
</style>
