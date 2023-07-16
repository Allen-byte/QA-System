export const routes = [
  {
    path: "/",
    name: "index",
    component: () => import("@/views/index.vue")
  },
  //登陆界面
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login.vue")
  },
  //注册界面
  {
    path: "/register",
    name: "register",
    component: () => import("@/views/register.vue")
  },
  //主页
  {
    path: "/home",
    name: "home",
    component: () => import("@/views/home.vue"),
    children: []
  }
]