<template>
    <div class="topBox">
        <p class="user">
            <el-avatar :size="40" :src="this.$server + avatar" style="margin-right: 10px;" />
            <span class="name" style="display: inline-block;margin-right: 5px;">
                {{ username }} <el-tag v-if="id==localUid">我</el-tag>
            </span>
            <span style="display: inline-block;margin-right: 5px;">
                <el-tag v-if="permission==1" type="success">管理员</el-tag>
            </span>
            <span style="display: inline-block;margin-right: 5px;">
                <el-tag v-if="quid==id" type="info">提问者</el-tag>
            </span>
        </p>
        <p class="answerTime">{{ answerTime.split(" ")[0] }}</p>
    </div>
    <p class="answer">
        <MdPreview :modelValue="answer"/>
    </p>
    <el-divider border-style="dashed" />
</template>

<script>
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

export default {
    name: "answerCard",
    components: {
        MdPreview
    },
    props: {
        quid: Number,        //提问者id
        id: Number,
        answer: String,
        username: String,
        answerTime: String,
        avatar: String,
        permission: Number
    },
    data() {
        return {
            localUid: JSON.parse(sessionStorage.getItem("user")).uid,
        }
    }
}

</script>
<style scoped>
.topBox{
    display: flex;
    justify-content: space-between;
}

.answerTime{
    font-size: 14px;
    color: #73767a;
}

.answer{
    text-align: left;
}

.user{
    display: flex;
    align-items: center;
}

.user .name{
    display: inline-block;
    font-size: 16px;
    font-weight: 600;
    text-align: left;
    margin-left: 10px;
}


</style>