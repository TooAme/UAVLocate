<template>
  <div class="final-data-container">
    <div class="final-result">
      <div class="result-title">当前偏移数据</div>
      <div class="result-row">
        <span class="result-label">X轴偏移距离:</span>
        <span class="result-value">{{ finalData?.xOffset?.toFixed(2) || '--' }}</span>
      </div>
      <div class="result-row">
        <span class="result-label">Y轴偏移距离:</span>
        <span class="result-value">{{ finalData?.yOffset?.toFixed(2) || '--' }}</span>
      </div>
      <div class="result-row">
        <span class="result-label">Z轴高度距离:</span>
        <span class="result-value">{{ finalData?.zOffset?.toFixed(2) || '--' }}</span>
      </div>
      <div class="result-row">
        <span class="result-label">更新时间:</span>
        <span class="result-value">{{ lastUpdateTime }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import type { IStatics } from '@/shared/model/statics.model';

// Alert service interface
interface AlertService {
  showInfo: (message: string, options?: { variant: string }) => void;
}

// Since we can't find the actual service, we'll create a simple implementation
const useAlertService = (): AlertService => {
  return {
    showInfo: (message: string, options?: { variant: string }) => {
      console.log(`Alert [${options?.variant || 'info'}]: ${message}`);
    },
  };
};

interface FinalData {
  xOffset: number;
  yOffset: number;
  zOffset: number;
}

export default defineComponent({
  name: 'FinalData',
  setup() {
    const finalData = ref<FinalData | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const lastUpdateTime = ref('--');
    const alertService = useAlertService();
    let refreshTimer: number | null = null;

    // 获取最终数据计算函数
    const countData = (posX: number, posY: number, posZ: number, windSpeed: number, windDirection: number | string): FinalData => {
      // 转换风向360为数字
      const windDir = typeof windDirection === 'string' ? parseFloat(windDirection) : windDirection;

      // 根据风向角度计算x和y方向分量
      let windX = 0;
      let windY = 0;

      // 降落速度先设为定值：1000毫米每秒
      let landSpeed = 1000;

      let targetPosX;
      let targetPosY;
      let targetPosZ;

      targetPosX = 0;
      targetPosY = 0;
      targetPosZ = 0;

      // 将角度转换为弧度进行计算
      const degToRad = (degrees: number) => (degrees * Math.PI) / 180;
      const theta = degToRad(windDir);

      // 根据风向范围计算分量
      if (windDir >= 0 && windDir < 90) {
        // 第一象限: 0-90度
        // x方向风速 = v * sin(θ)
        // y方向风速 = v * cos(θ)
        windX = windSpeed * Math.sin(theta);
        windY = windSpeed * Math.cos(theta);
        console.log(`风向:${windDir}°(0-90°), X向风速:${windX.toFixed(2)}, Y向风速:${windY.toFixed(2)}`);
      } else if (windDir >= 90 && windDir < 180) {
        // 第二象限: 90-180度
        // x方向风速 = v * cos(θ-90°)
        // y方向风速 = -v * sin(θ-90°)
        const adjustedTheta = degToRad(windDir - 90);
        windX = windSpeed * Math.cos(adjustedTheta);
        windY = -windSpeed * Math.sin(adjustedTheta);
        console.log(`风向:${windDir}°(90-180°), X向风速:${windX.toFixed(2)}, Y向风速:${windY.toFixed(2)}`);
      } else if (windDir >= 180 && windDir < 270) {
        // 第三象限: 180-270度
        // x方向风速 = -v * sin(θ-180°)
        // y方向风速 = -v * cos(θ-180°)
        const adjustedTheta = degToRad(windDir - 180);
        windX = -windSpeed * Math.sin(adjustedTheta);
        windY = -windSpeed * Math.cos(adjustedTheta);
        console.log(`风向:${windDir}°(180-270°), X向风速:${windX.toFixed(2)}, Y向风速:${windY.toFixed(2)}`);
      } else if (windDir >= 270 && windDir <= 360) {
        // 第四象限: 270-360度
        // x方向风速 = -v * cos(θ-270°)
        // y方向风速 = v * sin(θ-270°)
        const adjustedTheta = degToRad(windDir - 270);
        windX = -windSpeed * Math.cos(adjustedTheta);
        windY = windSpeed * Math.sin(adjustedTheta);
        console.log(`风向:${windDir}°(270-360°), X向风速:${windX.toFixed(2)}, Y向风速:${windY.toFixed(2)}`);
      }

      // 计算最终偏移值
      // 为了解决未赋值前使用变量的问题，先声明再赋值
      let calculatedXOffset = 0;
      let calculatedYOffset = 0;
      let calculatedZOffset = 0;

      calculatedXOffset = Math.abs(targetPosX - posX) + windX * (posZ / landSpeed);
      calculatedYOffset = Math.abs(targetPosY - posY) + windY * (posZ / landSpeed);
      calculatedZOffset = Math.abs(posZ - targetPosZ);

      return {
        xOffset: calculatedXOffset,
        yOffset: calculatedYOffset,
        zOffset: calculatedZOffset,
      };
    };

    const updateData = async () => {
      loading.value = true;
      error.value = null;

      try {
        // 获取所有statics数据
        const response = await axios.get<IStatics[]>('api/statics');

        if (response.data && response.data.length > 0) {
          // 筛选posZ不为0的最新数据
          const validData = response.data
            .filter(item => item.posZ !== null && item.posZ !== 0)
            .sort((a, b) => {
              // 按时间倒序排序，获取最新的数据
              const dateA = a.time ? new Date(a.time).getTime() : 0;
              const dateB = b.time ? new Date(b.time).getTime() : 0;
              return dateB - dateA;
            });

          if (validData.length > 0) {
            const latestData = validData[0];
            console.log('获取到的最新数据:', latestData);

            // 使用countData函数计算最终值
            finalData.value = countData(
              latestData.posX || 0,
              latestData.posY || 0,
              latestData.posZ || 0,
              latestData.windSpeed || 0,
              latestData.windDirection || '0',
            );

            // 更新最后更新时间
            const now = new Date();
            lastUpdateTime.value = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`;
          } else {
            error.value = '没有找到有效的位置数据（Z轴不为0）';
            alertService.showInfo(error.value, { variant: 'warning' });
          }
        } else {
          error.value = '没有找到任何位置数据';
          alertService.showInfo(error.value, { variant: 'warning' });
        }
      } catch (err) {
        console.error('获取数据失败:', err);
        error.value = '获取数据失败，请稍后再试';
        alertService.showInfo(error.value, { variant: 'danger' });
      } finally {
        loading.value = false;
      }
    };

    // 组件挂载时开始定时刷新
    onMounted(() => {
      updateData(); // 立即执行一次
      refreshTimer = window.setInterval(updateData, 500); // 每2秒刷新一次
    });

    // 组件卸载时清除定时器
    onUnmounted(() => {
      if (refreshTimer !== null) {
        clearInterval(refreshTimer);
      }
    });

    return {
      finalData,
      loading,
      error,
      lastUpdateTime,
    };
  },
});
</script>

<style scoped>
.final-data-container {
  position: fixed;
  top: 38.5%;
  left: 35%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.final-result {
  background: linear-gradient(to bottom, #000c27aa, #000000e2);
  border-radius: 6px;
  padding: 15px;
  color: white;
  width: 250px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}

.result-title {
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 5px;
}

.result-row {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
}

.result-label {
  font-size: 13px;
}

.result-value {
  font-size: 14px;
  font-weight: bold;
  background: linear-gradient(to bottom, #bfdcf7 40%, #1dc7ed);
  -webkit-background-clip: text;
  color: transparent;
}
</style>
