<template>
  <div class="home">
    <header-image class="header-image" />
    <div class="sidebar left-sidebar">
      <div class="left-sidebar-under"></div>
    </div>
    <div class="sidebar right-sidebar">
      <div class="right-sidebar-under"></div>
      <scroll-box class="scroll-box" />
    </div>
    <!-- <get-winds class="winds-position" /> -->

    <!-- <div class="content row">
      <div class="col-md-3"></div>
      <span class="hipster img-fluid rounded"></span>
      <span class="img2"></span>
      <span class="img3"></span>
      <span class="img4"></span>

      <div class="col-md-20">
        <div class="msgblock">
          <h1 class="msg" v-text="'无人机降落点X轴偏移:  5.3CM'"></h1>
          <h1 class="msg" v-text="'无人机降落点Y轴偏移:  -8.4CM'"></h1>
          <h1 class="msg" v-text="'无人机相对降落点(Z轴)高度:  46CM'"></h1>
          <h1 class="msg" v-text="'当前风向:  北偏西32度   风速:  3km/h'"></h1>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import HeaderImage from '@/decoration/headerImage.vue';
import GetWinds from '@/decoration/getwinds.vue';
import ScrollBox from '@/decoration/scrollBox.vue';

export default defineComponent({
  name: 'Home',
  components: {
    HeaderImage,
    GetWinds,
    ScrollBox,
  },
});
</script>

<style scoped>
.home {
  height: 100vh;
  /* overflow: hidden;禁止滚动 */
  position: fixed; /* 固定位置 */
  width: 100%; /* 确保宽度占满 */
  /* top: 0;固定在顶部 */
  /* left: 0;固定在左侧 */
}

.content {
  position: relative;
  padding: 20px;
}

.msgblock {
  position: absolute;
  top: 90px;
  left: 25px;
  background: border-box rgb(46, 46, 46);
  border-radius: 5px;
}

.msg {
  font-size: 1.65em;
  font-family: Arial, sans-serif;
  font-weight: bolder;
  font-style: italic;
  color: rgba(255, 255, 255, 0.915);
  margin: 25px 15px 25px 10px;
  padding: 0.6rem;
}

.img2 {
  display: inline-block;
  width: 263px;
  height: 263px;
  background: url('/content/images/flat.svg') no-repeat center top;
  background-size: contain;
  position: absolute;
  top: 240px;
  left: 550px;
}

.img3 {
  display: inline-block;
  width: 313px;
  height: 313px;
  background: url('/content/images/target.svg') no-repeat center top;
  background-size: contain;
  position: absolute;
  top: 85px;
  left: 930px;
}

.img4 {
  display: inline-block;
  width: 15px;
  height: 15px;
  background: url('/content/images/redpoint.png') no-repeat center top;
  background-size: contain;
  position: absolute;
  top: 310px;
  left: 1110px;
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

.sidebar {
  color: rgba(255, 255, 255, 0.767);
  display: flex;
  z-index: 1000;
  pointer-events: none;
  background-blend-mode: multiply;
} /* 侧边栏 */
.left-sidebar-under {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 32.4vw;
  left: 0;
  background-image: url('images/最底下矩形框.png');
  background-size: cover;
} /* 左侧边栏下层 */
.left-sidebar {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 31.4vw;
  left: 0;
  background-image: url('images/第二层矩形框翻转.png');
  background-size: cover;
} /* 左侧边栏上层 */
.right-sidebar-under {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 32.4vw;
  right: 0;
  background-image: url('images/最底下矩形框翻转.png');
  background-size: cover;
} /* 右侧边栏下层 */
.right-sidebar {
  position: fixed;
  top: 160px;
  bottom: 0;
  width: 31.4vw;
  right: 0;
  background-image: url('images/第二层矩形框.png');
  background-size: cover;
} /* 右侧边栏上层 */
</style>
