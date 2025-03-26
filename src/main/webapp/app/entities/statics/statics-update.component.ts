import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import StaticsService from './statics.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IStatics, Statics } from '@/shared/model/statics.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StaticsUpdate',
  setup() {
    const staticsService = inject('staticsService', () => new StaticsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const statics: Ref<IStatics> = ref(new Statics());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveStatics = async staticsId => {
      try {
        const res = await staticsService().find(staticsId);
        res.time = new Date(res.time);
        statics.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.staticsId) {
      retrieveStatics(route.params.staticsId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      time: {},
      posX: {},
      posY: {},
      posZ: {},
      windSpeed: {},
      windDirection: {},
    };
    const v$ = useVuelidate(validationRules, statics as any);
    v$.value.$validate();

    return {
      staticsService,
      alertService,
      statics,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: statics }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.statics.id) {
        this.staticsService()
          .update(this.statics)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('uavLocateApp.statics.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.staticsService()
          .create(this.statics)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('uavLocateApp.statics.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
