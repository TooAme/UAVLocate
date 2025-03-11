<template>
  <div class="home row">
    <div class="col-md-3"></div>
    <span class="hipster img-fluid rounded"></span>

    <div class="col-md-20">
      <div class="msgblock">
        <h1 class="msg" v-text="'无人机相对降落点X轴距:  5.3CM'"></h1>
        <h1 class="msg" v-text="'无人机相对降落点Y轴距:  46.0CM'"></h1>
        <h1 class="msg" v-text="'无人机相对降落点Z轴距:  8.4CM'"></h1>
        <h1 class="msg" v-text="'当前风向:  北偏西32度   风速:  13km/h'"></h1>
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
}
.msg {
  font-size: 1.65em;
  font-family: Arial, sans-serif;
  font-weight: bolder;
  font-style: italic;
  color: rgba(255, 255, 255, 0.493);
  border: 0.7mm rgba(255, 255, 255, 0.808);
  border-style: outset;

  /* outline: 0.5rem solid khaki; */
  /* box-shadow: 0 0 0 2rem skyblue; */
  border-radius: 2px;
  /* font: bold 1rem sans-serif; */
  margin: 1rem;
  padding: 0.3rem;
  /* outline-offset: 5rem; */
}
</style>
