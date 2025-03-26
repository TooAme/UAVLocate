import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import StaticsDetails from './statics-details.vue';
import StaticsService from './statics.service';
import AlertService from '@/shared/alert/alert.service';

type StaticsDetailsComponentType = InstanceType<typeof StaticsDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const staticsSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Statics Management Detail Component', () => {
    let staticsServiceStub: SinonStubbedInstance<StaticsService>;
    let mountOptions: MountingOptions<StaticsDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      staticsServiceStub = sinon.createStubInstance<StaticsService>(StaticsService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          staticsService: () => staticsServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        staticsServiceStub.find.resolves(staticsSample);
        route = {
          params: {
            staticsId: `${123}`,
          },
        };
        const wrapper = shallowMount(StaticsDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.statics).toMatchObject(staticsSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        staticsServiceStub.find.resolves(staticsSample);
        const wrapper = shallowMount(StaticsDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
