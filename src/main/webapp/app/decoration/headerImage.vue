<template>
  <div class="header-image"></div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'HeaderImage',
  components: {},
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
  background-image: linear-gradient(to bottom, rgba(0, 13, 36, 0), rgba(63, 74, 92, 0)), url('images/游泳馆.png');
  /*background-color: rgba(0, 13, 36, 0.75);*/
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
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
}
</style>
