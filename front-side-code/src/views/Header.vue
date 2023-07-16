<template>
    <div class="avatarBox">
        <el-avatar :src="avatar" :size="40"></el-avatar>
        <p style="font-weight: 600;margin-left: 10px;">{{ username }}</p>
    </div>
    <div class="middle">
        {{ title }}
    </div>
    <div class="rightInfo">
        <el-popconfirm title="确定退出吗?" @confirm="exit">
            <template #reference>
                <el-button type="danger">退出</el-button>
            </template>
        </el-popconfirm>
    </div>
</template>

<script>
import { dailyMessage } from '@/utils/message';


export default {
    name: "Header",
    data() {
        return {
            title: "",
            avatar: "",
            username: "",
        }
    },
    methods: {
        // 随机语录
        initDailyMessage() {
            this.title = dailyMessage[Math.floor(Math.random() * dailyMessage.length)];
        },
        exit() {
            sessionStorage.clear();
            this.$router.replace("/login");
        },
        setUser() {
            this.avatar = this.$server + sessionStorage.getItem("avatar");
            this.username = JSON.parse(sessionStorage.getItem("user")).username;
        }
    },
    created() {
        this.initDailyMessage();
        this.setUser();
    },
    //监听vuex中的avatar和username
    watch:{
        '$store.state.avatarUrl': function(){
            this.avatar = this.$store.state.avatarUrl;
        },
        '$store.state.user': function(){
            this.username = this.$store.state.user.username;
        }
    }
}

</script>
<style scoped>
.avatarBox {
    width: fit-content;
    display: flex;
    align-items: center;
    justify-content: space-around;
}

.middle {
    font-size: 22px;
    font-weight: 400;
    line-height: 60px;
}

.rightInfo {
    text-align: center;
    /* border: 1px solid #000; */
    line-height: 60px;
}

.username {
    font-size: 16px;
    font-weight: 400;
    margin-right: 10px;
}
</style>