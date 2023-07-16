<template>
    <div class="messageBox">
        <div class="contentBox">
            <p>
                {{ content.split(" ")[0] }} <b>{{ content.split(' ')[1] }}</b> {{ content.split(" ")[2] }}
            </p>
            <p class="time">{{ time }}</p>
        </div>
        <div class="checkBox">
            <el-button v-if="flag == 0" type="primary" :disabled="disabled" @click="done" plain>{{ text }}</el-button>
            <el-button :disabled="true" type="primary" plain v-else>已读</el-button>
            <el-button type="danger" @click="delMessage">删除</el-button>
        </div>
    </div>
</template>

<script>

export default {
    name: "notiCard",
    props: {
        id: Number,             //消息的id，用于更新
        time: String,           //消息提醒时间
        content: String,            //消息内容
        flag: Number            //是否已读
    },
    created() {

    },
    data() {
        return {
            disabled: false,
            text: "标为已读",
        }
    },
    methods: {
        done() {
            this.$axios({
                method: "get",
                url: `/messages/update/${this.id}`
            }).then(res => {
                if (res.data.code == "200") {
                    this.disabled = true;
                    this.text = "已读";
                    this.$store.commit("countMinus");     //消息显示-1
                } else {
                    console.log("更新失败");
                }
            }).catch(err => {
                console.log("更新消息状态时错误: " + err);
            })
        },
        //删除消息
        delMessage(){
            this.$axios({
                method: "get",
                url: `/messages/del/${this.id}`
            }).then(res=>{
                if(res.data.code == "200"){
                    this.$notify({
                        title: "删除成功",
                        type: "success"
                    });
                    this.$parent.getMessages();
                }else{
                    this.$notify({
                        title: res.data.msg,
                        type: "error"
                    });
                }
            }).catch(err=>{
                console.log("删除消息时出错: " + err);
            })
        }
    }
}

</script>
<style scoped>
.messageBox {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.contentBox {
    width: 80%;
    text-align: left;
}

p.time {
    color: #909399;
    font-size: 15px;
}
</style>