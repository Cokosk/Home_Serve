import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Deploy from '../views/Deploy.vue'
import Docs from '../views/Docs.vue'
import Pricing from '../views/Pricing.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/deploy', name: 'Deploy', component: Deploy },
  { path: '/docs', name: 'Docs', component: Docs },
  { path: '/pricing', name: 'Pricing', component: Pricing }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router