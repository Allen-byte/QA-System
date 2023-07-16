<template>
   <div>

   </div>
</template>

<script>
export default {
    name: "index",
    created(){
        this.jump();           //未登录时访问的路由跳转
    },
    methods: {
        jump(){
            let user = sessionStorage.getItem("user");
            if(user==null){
                this.$router.replace("/login");
            }else{
                let permission = JSON.parse(user).permission;
                if(permission===0){
                    this.$router.replace("/workbench");
                }else{
                    this.$router.replace("/userManage");
                }
                
            }
        }
    }
}

</script>
<style scoped>

</style>