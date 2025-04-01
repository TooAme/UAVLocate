import { type Ref, defineComponent, inject, onMounted, onUnmounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import axios from 'axios';

import StaticsService from './statics.service';
import { type IStatics } from '@/shared/model/statics.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Statics',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const staticsService = inject('staticsService', () => new StaticsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const statics: Ref<IStatics[]> = ref([]);
    const isFetching = ref(false);
    let refreshInterval: number | null = null;

    const clear = () => {
      if (refreshInterval) {
        window.clearInterval(refreshInterval);
        refreshInterval = null;
      }
    };

    const retrieveStaticss = async () => {
      if (isFetching.value) return; // 防止重复请求
      isFetching.value = true;
      try {
        const res = await staticsService().retrieve();
        statics.value = res.data;
      } catch (err: unknown) {
        if (axios.isAxiosError(err)) {
          alertService.showHttpError(err.response);
        }
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveStaticss();
    };

    onMounted(async () => {
      await retrieveStaticss();
      // 每1秒自动刷新一次数据
      refreshInterval = window.setInterval(retrieveStaticss, 1000);
    });

    onUnmounted(() => {
      clear();
    });

    const removeId = ref<number | null>(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IStatics) => {
      if (instance.id) {
        removeId.value = instance.id;
        removeEntity.value.show();
      }
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeStatics = async () => {
      if (!removeId.value) return;
      try {
        await staticsService().delete(removeId.value);
        const message = t$('uavLocateApp.statics.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveStaticss();
        closeDialog();
      } catch (error: unknown) {
        if (axios.isAxiosError(error)) {
          alertService.showHttpError(error.response);
        }
      }
    };

    return {
      statics,
      handleSyncList,
      isFetching,
      retrieveStaticss,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeStatics,
      t$,
    };
  },
});
