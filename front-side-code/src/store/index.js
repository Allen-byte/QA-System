import { createStore } from 'vuex'
import router from "@/router"




function addNewRoutes(menuList) {
  let routes = router.options.routes;
  routes.forEach(item => {
    if (item.path === "/home") {
      menuList.forEach(menu => {
        let childrenRoute = {
          path: "/" + menu.menuroute,
          name: menu.menuname,
          component: () => import("@/views" + menu.menucomponentpath)
        };
        item.children.push(childrenRoute);
      })
    }
  });
  routes.forEach(route => {
    router.addRoute(route);                  //更新到路由表中
  })
}

export default createStore({
  state: {
    user: {},
    account: "",
    avatarUrl: "",             //登陆时使用vuex保存
    menu: [],
    messages: [],
    unreadCount: 0,           //未读消息数量 
  },
  getters: {
    getAccount() {
      return state.account;
    },
    getAvatar() {
      return state.avatarUrl;
    },
    getUser() {
      return state.user;
    },
    getMenu() {
      return state.menu;
    },
  },
  mutations: {
    setAccount(state, account) {
      state.account = account;
    },
    setUser(state, user) {
      state.user = user;
    },
    setMenu(state, menu) {
      state.menu = menu;
      addNewRoutes(menu);
    },
    setAvatar(state, avatar){
      state.avatarUrl = avatar;
    },
    setCount(state, count) {
      state.unreadCount = count;
    },
    countMinus(state){
      state.unreadCount--;
    },
    setMessages(state, data){
      state.messages = data;
    },
    setStatus(state, status){
      state.status = status;
    },
  },
  actions: {
  },
  modules: {
  }
})
