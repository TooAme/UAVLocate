import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Statics = () => import('@/entities/statics/statics.vue');
const StaticsUpdate = () => import('@/entities/statics/statics-update.vue');
const StaticsDetails = () => import('@/entities/statics/statics-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'statics',
      name: 'Statics',
      component: Statics,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'statics/new',
      name: 'StaticsCreate',
      component: StaticsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'statics/:staticsId/edit',
      name: 'StaticsEdit',
      component: StaticsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'statics/:staticsId/view',
      name: 'StaticsView',
      component: StaticsDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
