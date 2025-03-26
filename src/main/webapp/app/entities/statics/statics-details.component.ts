import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import StaticsService from './statics.service';
import { useDateFormat } from '@/shared/composables';
import { type IStatics } from '@/shared/model/statics.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StaticsDetails',
  setup() {
    const dateFormat = useDateFormat();
    const staticsService = inject('staticsService', () => new StaticsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const statics: Ref<IStatics> = ref({});

    const retrieveStatics = async staticsId => {
      try {
        const res = await staticsService().find(staticsId);
        statics.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.staticsId) {
      retrieveStatics(route.params.staticsId);
    }

    return {
      ...dateFormat,
      alertService,
      statics,

      previousState,
      t$: useI18n().t,
    };
  },
});
