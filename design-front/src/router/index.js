import Vue from 'vue'
import VueRouter from 'vue-router'
import DeepFakeDetector from '../views/DeepFakeDetector.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'DeepFakeDetector',
    component: DeepFakeDetector
  },
  {
    path: '/normalDetector',
    name: 'NormalDetector',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/NormalDetector.vue')
  }
]

const router = new VueRouter({
  routes
})

export default router
