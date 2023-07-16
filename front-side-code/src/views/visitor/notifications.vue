<template>
    <p class="top">
        <el-button type="success" plain @click="getMessages">
            刷新 <el-icon>
                <Refresh />
            </el-icon>
        </el-button>
        <span>你有{{ unreadCount }}条消息未读</span>
    </p>
    <el-divider border-style="dashed" />
    <notiCard v-if="notificationsData.length != 0" v-for="item in notificationsData" :id="item.id" :key="item.id"
        :time="item.time" :content="item.message" :flag="item.flag" />
    <el-empty description="暂无消息通知" v-else />

    <div class="pagination">
        <el-pagination :hide-on-single-page="value" v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
            layout="prev, pager, next" :total="total" @current-change="handleCurrentChange" />
    </div>
</template>

<script>
import notiCard from "@/components/notiCard.vue";
import { mapState } from "vuex";

export default {
    name: "notifications",
    components: {
        notiCard,
    },
    //获取消息通知
    created() {
        this.getUnread();
        this.getMessages();
    },
    computed: {
        ...mapState({
            unreadCount: "unreadCount"
        })
    },
    data() {
        return {
            value: true,               //设置单个分页时隐藏页码
            uid: JSON.parse(sessionStorage.getItem("user")).uid,
            notificationsData: [],                 //全部消息通知，包括已读和未读
            pageNum: 1,
            pageSize: 10,
            total: 0,
            background: true
        }
    },
    methods: {
        //获取全部消息
        getMessages() {
            this.$axios({
                method: "post",
                url: "/messages/getMessages",
                data: {
                    "uid": this.uid,
                    "pageNum": this.pageNum,
                    "pageSize": this.pageSize
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.notificationsData = res.data.data;
                    this.total = res.data.total;
                } else {
                    this.$message.info(res.data.msg);
                }
            }).catch(err => {
                console.log("获取消息时错误: " + err);
            })
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.getMessages();
        },
        //获取未读消息个数
        getUnread() {
            this.$axios({
                method: "get",
                url: `/messages/getUnread/${this.uid}`,
            }).then(res => {
                if (res.data.code == "200") {
                    //设置全局使用
                    this.$store.commit("setCount", res.data.data);
                } else {
                    console.log(res.data.msg);
                }
            }).catch(err => {
                console.log("获取消息时错误: " + err);
            })
        }
    }
}

</script>
<style scoped>
.top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    color: #73767a;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>