<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="uavLocateApp.statics.home.createOrEditLabel"
          data-cy="StaticsCreateUpdateHeading"
          v-text="t$('uavLocateApp.statics.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="statics.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="statics.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('uavLocateApp.statics.time')" for="statics-time"></label>
            <div class="d-flex">
              <input
                id="statics-time"
                data-cy="time"
                type="datetime-local"
                class="form-control"
                name="time"
                :class="{ valid: !v$.time.$invalid, invalid: v$.time.$invalid }"
                :value="convertDateTimeFromServer(v$.time.$model)"
                @change="updateInstantField('time', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('uavLocateApp.statics.posX')" for="statics-posX"></label>
            <input
              type="number"
              class="form-control"
              name="posX"
              id="statics-posX"
              data-cy="posX"
              :class="{ valid: !v$.posX.$invalid, invalid: v$.posX.$invalid }"
              v-model.number="v$.posX.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('uavLocateApp.statics.posY')" for="statics-posY"></label>
            <input
              type="number"
              class="form-control"
              name="posY"
              id="statics-posY"
              data-cy="posY"
              :class="{ valid: !v$.posY.$invalid, invalid: v$.posY.$invalid }"
              v-model.number="v$.posY.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('uavLocateApp.statics.posZ')" for="statics-posZ"></label>
            <input
              type="number"
              class="form-control"
              name="posZ"
              id="statics-posZ"
              data-cy="posZ"
              :class="{ valid: !v$.posZ.$invalid, invalid: v$.posZ.$invalid }"
              v-model.number="v$.posZ.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('uavLocateApp.statics.windSpeed')" for="statics-windSpeed"></label>
            <input
              type="number"
              class="form-control"
              name="windSpeed"
              id="statics-windSpeed"
              data-cy="windSpeed"
              :class="{ valid: !v$.windSpeed.$invalid, invalid: v$.windSpeed.$invalid }"
              v-model.number="v$.windSpeed.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('uavLocateApp.statics.windDirection')" for="statics-windDirection"></label>
            <input
              type="text"
              class="form-control"
              name="windDirection"
              id="statics-windDirection"
              data-cy="windDirection"
              :class="{ valid: !v$.windDirection.$invalid, invalid: v$.windDirection.$invalid }"
              v-model="v$.windDirection.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./statics-update.component.ts"></script>
