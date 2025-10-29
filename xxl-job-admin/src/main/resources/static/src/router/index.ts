import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HelloWorld.vue'),
    },
    {
      path: '/hello',
      name: 'Hello',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/HelloWorld.vue'),
    },
    {
      path: '/executor/list',
      name: 'executor_list',
      component: () => import('../views/executor/list.vue'),
    },
    {
      path: '/task/list',
      name: 'task_list',
      component: () => import('../views/task/list.vue'),
    },
    {
      path: '/log/list',
      name: 'log_list',
      component: () => import('../views/log/list.vue'),
    },
    {
      path: '/user/perm',
      name: 'user_perm',
      component: () => import('../views/user/perm.vue'),
    }
  ],
})

export default router
