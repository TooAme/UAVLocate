<template>
  <div>
    <h1>WebSocket Example</h1>
    <input v-model="name" placeholder="Enter your name" />
    <button @click="sendMessage">Send</button>
    <div>
      <p v-for="(message, index) in messages" :key="index">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default {
  data() {
    return {
      name: '',
      messages: [],
      client: null,
    };
  },
  mounted() {
    this.client = Stomp.over(new SockJS('http://localhost:8080/ws'));
    this.client.connect({}, frame => {
      console.log('Connected: ' + frame);
      this.client.subscribe('/topic/greetings', message => {
        this.messages.push(JSON.parse(message.body).content);
      });
    });
  },
  methods: {
    sendMessage() {
      this.client.send('/app/hello', {}, JSON.stringify({ name: this.name }));
    },
  },
  beforeUnmount() {
    if (this.client) {
      this.client.disconnect();
    }
  },
};
</script>
