<template>
  <div class="dqsj">
    <div class="dqsjtext">当前数据</div>
    <div class="lbg-container">
      <div class="lbgmc"></div>
      <div class="lbgmctext">
        序号&nbsp;&nbsp;&nbsp;&nbsp;时间&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;X轴&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Y轴&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Z轴&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;风速&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;风向
      </div>
    </div>
    <vue3ScrollSeamless class="scroll-wrap" :classOptions="classOptions" :dataList="formattedList">
      <div v-if="formattedList.length > 0">
        <el-row v-for="(item, i) of formattedList" :key="i">
          <el-col :span="10" class="lbg">
            <div>{{ item }}</div>
          </el-col>
        </el-row>
      </div>
    </vue3ScrollSeamless>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue';
import { vue3ScrollSeamless } from 'vue3-scroll-seamless';
import StaticsService from '@/entities/statics/statics.service';
import { useDateFormat } from '@/shared/composables';

const dateFormat = useDateFormat();
const staticsService = new StaticsService();
const statics = ref([]);

const formattedList = computed(() => {
  return statics.value.map((item, index) => {
    const time = dateFormat.formatDateShort(item.time);
    return `\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${index + 1}\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${time}\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${item.posX}\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${item.posY}\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${item.posZ}\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${item.windSpeed}\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0${item.windDirection}\xa0\xa0\xa0\xa0\xa0\xa0`;
  });
});

const classOptions = reactive({
  step: 0.5,
  limitMoveNum: 10,
  direction: 1,
});

const retrieveStaticss = async () => {
  try {
    const res = await staticsService.retrieve();
    statics.value = res.data;
  } catch (err) {
    console.error('Error fetching statics:', err);
  }
};

onMounted(async () => {
  await retrieveStaticss();
  // 每1秒刷新一次数据
  setInterval(retrieveStaticss, 1000);
});
</script>

<style scoped>
.dqsj {
  background-image: url('images/设备框-03.png');
  background-size: contain;
  background-repeat: no-repeat;
  width: calc(440 * 0.072vw);
  height: calc(690 * 0.13vh);
  position: absolute;
  top: calc(4 * 0.13vh);
  left: calc(6 * 0.072vw);
  z-index: 1010;
}

.dqsjtext {
  font-size: calc(15 * 0.072vw);
  font-weight: bold;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  display: flex;
  position: relative;
  left: calc(60 * 0.072vw);
  top: calc(12 * 0.13vh);
  font-style: italic;
}

.lbgmc {
  top: calc(40 * 0.13vh);
  position: relative;
  color: rgb(226, 254, 255);
  height: calc(30 * 0.13vh);
  background-image: url('images/列表格.png');
  background-repeat: no-repeat;
  background-size: contain;
  width: 95%;
  font-size: calc(10 * 0.072vw);
  font-family: '黑体';
  font-weight: bold;
  display: flex;
  justify-content: center;
}

.lbgmctext {
  position: relative;
  font-size: calc(10 * 0.072vw);
  font-weight: bold;
  background: linear-gradient(to bottom, #fff 40%, #62e1f1);
  -webkit-background-clip: text;
  color: transparent;
  display: flex;
  top: calc(7 * 0.13vh);
  justify-content: center;
}

.lbg {
  position: relative;
  color: rgb(236, 254, 255);
  height: calc(40 * 0.13vh);
  background-image: url('images/列表格.png');
  background-repeat: no-repeat;
  background-size: contain;
  width: 95%;
  font-size: calc(9 * 0.072vw);
  display: flex;
  justify-content: center;
}

.scroll-wrap {
  position: relative;
  top: calc(35 * 0.13vh);
  width: 100%;
  height: calc(466 * 0.13vh);
  margin: 0 auto;
  overflow: hidden;
}
</style>
