import { createRouter as createVueRouter, createWebHistory } from 'vue-router';

const Home = () => import('@/core/home/home.vue');
const Error = () => import('@/core/error/error.vue');
const ReadmeView = () => import('@/core/readme/readme.vue');
import account from '@/router/account';
import admin from '@/router/admin';
import entities from '@/router/entities';
import pages from '@/router/pages';
import Weather from '@/decoration/Weather.vue';

export const createRouter = () =>
  createVueRouter({
    history: createWebHistory(),
    routes: [
      {
        path: '/',
        name: 'Home',
        component: Home,
      },
      {
        path: '/forbidden',
        name: 'Forbidden',
        component: Error,
        meta: { error403: true },
      },
      {
        path: '/not-found',
        name: 'NotFound',
        component: Error,
        meta: { error404: true },
      },
      {
        path: '/weather',
        name: 'Weather',
        component: Weather,
      },
      {
        path: '/README.html',
        name: 'Readme',
        component: ReadmeView,
      },
      ...account,
      ...admin,
      entities,
      ...pages,
      {
        path: '/:pathMatch(.*)*',
        redirect: '/not-found',
      },
    ],
  });

const router = createRouter();

router.beforeResolve(async (to, from, next) => {
  if (!to.matched.length) {
    next({ path: '/not-found' });
    return;
  }
  next();
});

export default router;
