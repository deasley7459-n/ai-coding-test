import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/users'
    },
    {
      path: '/users',
      name: 'UserList',
      component: () => import('@/views/UserListView.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/users'
    }
  ]
})

export default router
