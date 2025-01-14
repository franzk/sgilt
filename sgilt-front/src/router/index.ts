import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SearchView from '@/views/SearchView.vue'
import PartnerView from '@/views/PartnerView.vue'
import ErrorNotFound from '@/views/ErrorNotFound.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/search',
      name: 'search',
      component: SearchView,
    },
    {
      path: '/:id',
      name: 'partner',
      component: PartnerView,
    },
    {
      path: '/404',
      name: 'error404',
      component: ErrorNotFound,
    },
  ],
})

export default router
