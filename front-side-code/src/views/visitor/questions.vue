<template>
    <div v-if="cardData">
        <questionCard v-for="item in cardData" :key="item.qid" :uid="item.uid" :title="item.question"
        :qid="item.qid" :username="item.username" :poseTime="item.poseTime" :avatar="this.$server + item.avatar"
        :description="item.description" :countInit="item.count" :permission="item.permission"/>
    </div>
    <el-empty description="广场很空旷，来做第一个提问的人吧!" v-else />
    <el-backtop v-if="pageSize >= 10" :right="100" :bottom="100" />
    <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
            layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
            :page-sizes="[5, 10, 20, 50]" @current-change="handleCurrentChange" />
    </div>
</template>

<script>
import questionCard from '@/components/questionCard';


export default {
    name: "questions",
    components: {
        questionCard
    },
    created() {
        this.loadAll();           //加载全部问题
    },
    components: { questionCard },
    data() {
        return {
            background: true,     //分页的背景色
            total: 0,         //接收总的查询数量
            pageSize: 5,     //每页显示的问题个数
            pageNum: 1,       //当前页码
            cardData: [],
        }
    },
    methods: {
        //加载全部问题
        loadAll() {
            //查询问题信息
            this.$axios({
                method: "get",
                url: "/questions/all",
                params: {
                    pageSize: this.pageSize,
                    pageNum: this.pageNum,
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.cardData = res.data.data;
                    this.total = res.data.total;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log(err);
            })
        },

        // 页码改变时触发
        handleCurrentChange(val) {
            this.pageNum = val;
            this.loadAll();
        },
        //页数改变时触发
        handleSizeChange(val) {
            this.pageSize = val;
            this.pageNum = 1;
            this.loadAll();
        },
    }
}

</script>
<style scoped>
.pagination {
    display: flex;
    justify-content: center;
}
</style>