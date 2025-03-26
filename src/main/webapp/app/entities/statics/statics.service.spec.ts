import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import StaticsService from './statics.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Statics } from '@/shared/model/statics.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Statics Service', () => {
    let service: StaticsService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new StaticsService();
      currentDate = new Date();
      elemDefault = new Statics(123, currentDate, 0, 0, 0, 0, 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { time: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Statics', async () => {
        const returnedFromService = { id: 123, time: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { time: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Statics', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Statics', async () => {
        const returnedFromService = {
          time: dayjs(currentDate).format(DATE_TIME_FORMAT),
          posX: 1,
          posY: 1,
          posZ: 1,
          windSpeed: 1,
          windDirection: 'BBBBBB',
          ...elemDefault,
        };

        const expected = { time: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Statics', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Statics', async () => {
        const patchObject = { posZ: 1, windDirection: 'BBBBBB', ...new Statics() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { time: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Statics', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Statics', async () => {
        const returnedFromService = {
          time: dayjs(currentDate).format(DATE_TIME_FORMAT),
          posX: 1,
          posY: 1,
          posZ: 1,
          windSpeed: 1,
          windDirection: 'BBBBBB',
          ...elemDefault,
        };
        const expected = { time: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Statics', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Statics', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Statics', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
