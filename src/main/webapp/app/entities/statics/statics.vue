<template>
  <div>
    <h2 id="page-heading" data-cy="StaticsHeading">
      <span v-text="t$('uavLocateApp.statics.home.title')" id="statics-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('uavLocateApp.statics.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'StaticsCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-statics"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('uavLocateApp.statics.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && statics && statics.length === 0">
      <span v-text="t$('uavLocateApp.statics.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="statics && statics.length > 0">
      <table class="table table-striped" aria-describedby="statics">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('uavLocateApp.statics.time')"></span></th>
            <th scope="row"><span v-text="t$('uavLocateApp.statics.posX')"></span></th>
            <th scope="row"><span v-text="t$('uavLocateApp.statics.posY')"></span></th>
            <th scope="row"><span v-text="t$('uavLocateApp.statics.posZ')"></span></th>
            <th scope="row"><span v-text="t$('uavLocateApp.statics.windSpeed')"></span></th>
            <th scope="row"><span v-text="t$('uavLocateApp.statics.windDirection')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="statics in statics" :key="statics.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'StaticsView', params: { staticsId: statics.id } }">{{ statics.id }}</router-link>
            </td>
            <td>{{ formatDateShort(statics.time) || '' }}</td>
            <td>{{ statics.posX }}</td>
            <td>{{ statics.posY }}</td>
            <td>{{ statics.posZ }}</td>
            <td>{{ statics.windSpeed }}</td>
            <td>{{ statics.windDirection }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'StaticsView', params: { staticsId: statics.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'StaticsEdit', params: { staticsId: statics.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(statics)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="uavLocateApp.statics.delete.question" data-cy="staticsDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-statics-heading" v-text="t$('uavLocateApp.statics.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-statics"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeStatics()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./statics.component.ts"></script>
