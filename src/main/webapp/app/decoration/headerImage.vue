<template>
  <div class="header-image" :style="currentImageStyle"></div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
// 导入图片
import SwimmingPool from '@/decoration/images/游泳馆.png';
import CloudSea from '@/decoration/images/云海之中.jpg';
import flowwer from '@/decoration/images/花.png';

export default defineComponent({
  name: 'HeaderImage',
  setup() {
    const images = [
      SwimmingPool,
      // CloudSea,
      // flowwer
    ];

    const currentIndex = ref(0);
    let timer: number | null = null;

    const currentImageStyle = ref({
      backgroundImage: `linear-gradient(to bottom, rgba(0, 13, 36, 0), rgba(63, 74, 92, 0)), url(${images[0]})`,
      opacity: 1,
    });

    const nextImage = () => {
      // 渐变过渡效果
      currentImageStyle.value.opacity = 1;

      setTimeout(() => {
        currentIndex.value = (currentIndex.value + 1) % images.length;
        currentImageStyle.value = {
          backgroundImage: `linear-gradient(to bottom, rgba(0, 13, 36, 0), rgba(63, 74, 92, 0)), url(${images[currentIndex.value]})`,
          opacity: 1,
        };
      }, 5000); // 500ms后切换到下一张图片
    };

    onMounted(() => {
      // 每4秒切换一次图片
      timer = window.setInterval(nextImage, 4000);
    });

    onUnmounted(() => {
      if (timer) {
        clearInterval(timer);
      }
    });

    return {
      currentImageStyle,
    };
  },
});
</script>

<style scoped>
@media (min-width: 100%) {
  .header-image {
    width: 50px;
  }
}

.header-image {
  position: fixed;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: rgba(0, 13, 36, 0.75);
  width: 100vw;
  height: 100vh;
  top: 70px;
  left: 0;
  z-index: 2011;
  pointer-events: none;
  font-size: 2vw;
  font-family: '微软雅黑';
  font-weight: 600;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  text-shadow: 0.144vw 0.144vw 0.116vw rgba(0, 0, 0, 0.337);
  letter-spacing: calc(1.2 * 0.072vw);
  transition: opacity 0.5s ease-in-out;
}
</style>
