import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from "./routes"


const router = createRouter({
  base: process.env.BASE_URL,
  history: createWebHashHistory(),
  routes
})

export default router
