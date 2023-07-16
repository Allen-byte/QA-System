<template>
  <router-view></router-view>
</template>


<script>

export default {
  data() {
    return {
    }
  },
  created() {
    //登陆时刷新的路由跳转
    this.routeJump();
  },
  methods: {
    routeJump() {
      let menu = JSON.parse(sessionStorage.getItem("menu"));
      if (menu != null) {
        this.$store.commit("setMenu", menu);
        let user = sessionStorage.getItem("user");
        if (user !== "") {
          let permission = JSON.parse(user).permission;
          if (permission === 0) {
            this.$router.replace("/workbench");
          } else {
            this.$router.replace("/userManage");
          }
        } else {
          this.$router.replace("/login");
        }
      }
    }
  }
}
</script>
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
