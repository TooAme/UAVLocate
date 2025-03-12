<template>
  <div class="home row">
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
    <!--      <p class="lead" v-text="t$('home.subtitle')"></p>-->

    <!--      <div>-->
    <!--        <div class="alert alert-success" v-if="authenticated">-->
    <!--          <span v-if="username" v-text="t$('home.logged.message', { username: username })"></span>-->
    <!--        </div>-->

    <!--        <div class="alert alert-warning" v-if="!authenticated">-->
    <!--          <span v-text="t$('global.messages.info.authenticated.prefix')"></span>-->
    <!--          <a class="alert-link" @click="openLogin()" v-text="t$('global.messages.info.authenticated.link')"></a-->
    <!--          ><span v-html="t$('global.messages.info.authenticated.suffix')"></span>-->
    <!--        </div>-->
    <!--        <div class="alert alert-warning" v-if="!authenticated">-->
    <!--          <span v-text="t$('global.messages.info.register.noaccount')"></span>&nbsp;-->
    <!--          <router-link class="alert-link" to="/register" v-text="t$('global.messages.info.register.link')"></router-link>-->
    <!--        </div>-->
    <!--      </div>-->

    <!--      <p v-text="t$('home.question')"></p>-->

    <!--      <ul>-->
    <!--        <li><a href="https://www.jhipster.tech/" target="_blank" rel="noopener noreferrer" v-text="t$('home.link.homepage')"></a></li>-->
    <!--        <li>-->
    <!--          <a-->
    <!--            href="https://stackoverflow.com/tags/jhipster/info"-->
    <!--            target="_blank"-->
    <!--            rel="noopener noreferrer"-->
    <!--            v-text="t$('home.link.stackoverflow')"-->
    <!--          ></a>-->
    <!--        </li>-->
    <!--        <li>-->
    <!--          <a-->
    <!--            href="https://github.com/jhipster/generator-jhipster/issues?state=open"-->
    <!--            target="_blank"-->
    <!--            rel="noopener noreferrer"-->
    <!--            v-text="t$('home.link.bugtracker')"-->
    <!--          ></a>-->
    <!--        </li>-->
    <!--        <li>-->
    <!--          <a-->
    <!--            href="https://gitter.im/jhipster/generator-jhipster"-->
    <!--            target="_blank"-->
    <!--            rel="noopener noreferrer"-->
    <!--            v-text="t$('home.link.chat')"-->
    <!--          ></a>-->
    <!--        </li>-->
    <!--        <li>-->
    <!--          <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer" v-text="t$('home.link.follow')"></a>-->
    <!--        </li>-->
    <!--      </ul>-->

    <!--      <p>-->
    <!--        <span v-text="t$('home.like')"></span>-->
    <!--        <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer" v-text="t$('home.github')"></a>!-->
    <!--      </p>-->
    <!--    </div>-->
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

<style scoped>
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
  /* border: 0.9mm rgba(255, 255, 255, 0.808);
  border-style: outset; */

  /* outline: 0.5rem solid khaki; */
  /* box-shadow: 0 0 0 2rem skyblue; */
  /* border-radius: 5px; */
  /* font: bold 1rem sans-serif; */
  margin: 25px 15px 25px 10px;
  padding: 0.6rem;
  /* outline-offset: 5rem; */
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
</style>
