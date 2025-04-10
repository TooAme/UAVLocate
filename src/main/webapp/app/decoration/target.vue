<template>
  <div class="target-container">
    <div class="target-title">实时降落点二维直观视图</div>
    <div class="target-background">
      <!-- 靶子背景图 -->
      <div class="target-image"></div>

      <!-- 坐标轴 -->
      <div class="coordinate-system">
        <div class="x-axis"></div>
        <div class="y-axis"></div>

        <!-- 原点标记 -->
        <div class="origin-point">
          <div class="origin-point-inner"></div>
        </div>
      </div>

      <!-- 偏移点标记 -->
      <div
        class="offset-point"
        :style="{
          left: `${50 + (xOffset / 20000) * 90}%`,
          top: `${50 - (yOffset / 20000) * 70}%`,
        }"
      >
        <div class="point-label">({{ xOffset.toFixed(2) }}, {{ yOffset.toFixed(2) }})</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'Target',
  props: {
    xOffset: {
      type: Number,
      required: true,
      default: 0,
    },
    yOffset: {
      type: Number,
      required: true,
      default: 0,
    },
  },
});
</script>

<style scoped>
.target-container {
  position: fixed;
  top: 365px;
  left: 280px;
  transform: translateY(-50%);
  width: calc(300 * 0.072vw);
  height: calc(300 * 0.072vw);
  background: linear-gradient(to bottom, #000c27aa, #000000e2);
  border-radius: 6;
  overflow: hidden;
  z-index: 1050;
  pointer-events: none;
  padding-top: calc(25 * 0.072vw);
}

.target-title {
  position: absolute;
  top: calc(13 * 0.072vw);
  width: 100%;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  z-index: 1051;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 5px;
}

.target-background {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.target-image {
  width: 90%;
  height: 90%;
  background-image: url('@/decoration/images/target.jpg');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  position: absolute;
  opacity: 0.8;
}

.coordinate-system {
  width: 90%;
  height: 77%;
  position: absolute;
  pointer-events: none;
}

.x-axis {
  position: absolute;
  width: 100%;
  height: 1.2px;
  background: rgba(0, 71, 186, 0.7);
  top: 50%;
  left: 0;
}

.y-axis {
  position: absolute;
  width: 1.2px;
  height: 100%;
  background: rgba(0, 71, 186, 0.7);
  left: 50%;
  top: 0;
}

.origin-point {
  position: absolute;
  width: calc(8 * 0.072vw);
  height: calc(8 * 0.072vw);
  background: #0066ffaf;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9;
  pointer-events: none;
}

.origin-point::before {
  content: '';
  position: absolute;
  width: calc(16 * 0.072vw);
  height: calc(16 * 0.072vw);
  border: 2px solid #006affaf;
  border-radius: 50%;
  top: calc(-4 * 0.072vw);
  left: calc(-4 * 0.072vw);
  animation: pulse 1.5s infinite;
}

.offset-point {
  position: absolute;
  width: calc(8 * 0.072vw);
  height: calc(8 * 0.072vw);
  background: #ff0000af;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.3s ease;
  z-index: 10;
  pointer-events: none;
}

.offset-point::before {
  content: '';
  position: absolute;
  width: calc(16 * 0.072vw);
  height: calc(16 * 0.072vw);
  border: 2px solid #ff0000af;
  border-radius: 50%;
  top: calc(-4 * 0.072vw);
  left: calc(-4 * 0.072vw);
  animation: pulse 1.5s infinite;
}

.point-label {
  position: absolute;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: calc(4 * 0.072vw) calc(8 * 0.072vw);
  border-radius: 4px;
  font-size: calc(10 * 0.072vw);
  top: calc(-25 * 0.072vw);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}
</style>
