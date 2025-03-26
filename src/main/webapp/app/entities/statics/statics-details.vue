<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="statics">
        <h2 class="jh-entity-heading" data-cy="staticsDetailsHeading">
          <span v-text="t$('uavLocateApp.statics.detail.title')"></span> {{ statics.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('uavLocateApp.statics.time')"></span>
          </dt>
          <dd>
            <span v-if="statics.time">{{ formatDateLong(statics.time) }}</span>
          </dd>
          <dt>
            <span v-text="t$('uavLocateApp.statics.posX')"></span>
          </dt>
          <dd>
            <span>{{ statics.posX }}</span>
          </dd>
          <dt>
            <span v-text="t$('uavLocateApp.statics.posY')"></span>
          </dt>
          <dd>
            <span>{{ statics.posY }}</span>
          </dd>
          <dt>
            <span v-text="t$('uavLocateApp.statics.posZ')"></span>
          </dt>
          <dd>
            <span>{{ statics.posZ }}</span>
          </dd>
          <dt>
            <span v-text="t$('uavLocateApp.statics.windSpeed')"></span>
          </dt>
          <dd>
            <span>{{ statics.windSpeed }}</span>
          </dd>
          <dt>
            <span v-text="t$('uavLocateApp.statics.windDirection')"></span>
          </dt>
          <dd>
            <span>{{ statics.windDirection }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="statics.id" :to="{ name: 'StaticsEdit', params: { staticsId: statics.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./statics-details.component.ts"></script>
