import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import StaticsUpdate from './statics-update.vue';
import StaticsService from './statics.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type StaticsUpdateComponentType = InstanceType<typeof StaticsUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const staticsSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<StaticsUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Statics Management Update Component', () => {
    let comp: StaticsUpdateComponentType;
    let staticsServiceStub: SinonStubbedInstance<StaticsService>;

    beforeEach(() => {
      route = {};
      staticsServiceStub = sinon.createStubInstance<StaticsService>(StaticsService);
      staticsServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          staticsService: () => staticsServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(StaticsUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(StaticsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.statics = staticsSample;
        staticsServiceStub.update.resolves(staticsSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(staticsServiceStub.update.calledWith(staticsSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        staticsServiceStub.create.resolves(entity);
        const wrapper = shallowMount(StaticsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.statics = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(staticsServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        staticsServiceStub.find.resolves(staticsSample);
        staticsServiceStub.retrieve.resolves([staticsSample]);

        // WHEN
        route = {
          params: {
            staticsId: `${staticsSample.id}`,
          },
        };
        const wrapper = shallowMount(StaticsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.statics).toMatchObject(staticsSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        staticsServiceStub.find.resolves(staticsSample);
        const wrapper = shallowMount(StaticsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
