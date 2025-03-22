import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/test',
    name: 'XssTest',
    component: () => import('../views/XssTest.vue')
  },
  {
    path: '/logs',
    name: 'AttackLogs',
    component: () => import('../views/AttackLogs.vue')
  },
  {
    path: '/config',
    name: 'DefenseConfig',
    component: () => import('../views/DefenseConfig.vue')
  },
  {
    path: '/dbinfo',
    name: 'DatabaseInfo',
    component: () => import('../views/DatabaseInfo.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 