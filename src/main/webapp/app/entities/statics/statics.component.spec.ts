import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Statics from './statics.vue';
import StaticsService from './statics.service';
import AlertService from '@/shared/alert/alert.service';

type StaticsComponentType = InstanceType<typeof Statics>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Statics Management Component', () => {
    let staticsServiceStub: SinonStubbedInstance<StaticsService>;
    let mountOptions: MountingOptions<StaticsComponentType>['global'];

    beforeEach(() => {
      staticsServiceStub = sinon.createStubInstance<StaticsService>(StaticsService);
      staticsServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          staticsService: () => staticsServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        staticsServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Statics, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(staticsServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.statics[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: StaticsComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Statics, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        staticsServiceStub.retrieve.reset();
        staticsServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        staticsServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeStatics();
        await comp.$nextTick(); // clear components

        // THEN
        expect(staticsServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(staticsServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
