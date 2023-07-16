<template>
    <div class="questionBox">
        <p class="title">
            {{ title }} <el-tag v-if="localUid == uid">我的提问</el-tag>
        </p>
        <div class="userInfoBox">
            <p class="poseInfo">
                <el-avatar :size="30" :src="avatar" />
                <span class="username">{{ username }}</span>
                <el-tag v-if="permission==1" type="success">管理员</el-tag>
            </p>
            <p class="poseTime">
                {{ poseTime }}
            </p>
        </div>
        <el-divider border-style="dashed" />
        <!-- 描述内容显示 -->
        <p class="description">
            <MdPreview :modelValue="description" />
        </p>
        <div class="btnGroup" v-if="permission==0">
            <el-button type="primary" @click="writeAnswer">
                <el-icon>
                    <Edit />
                </el-icon>
                <span class="writeText">写回答</span>
            </el-button>
            <el-button @click="showComments" type="info" plain>
                <el-icon>
                    <ChatRound />
                </el-icon>
                <span class="writeText" v-if="count == 0">暂无回答</span>
                <span class="writeText" v-else>{{ count }} 条回答</span>
            </el-button>
        </div>
    </div>
    <!-- 回答框 -->
    <el-dialog :show-close="false" v-model="visible" width="70%" top="30px" destroy-on-close>
        <p slot="title">
        <p style="font-size: 20px; font-weight: 600; margin-top: -40px;">{{ title }}</p>
        </p>
        <MdEditor v-model="content" :toolbars="toolbarsA" :align="align" placeholder="请简单描述您的问题,右侧为预览区" />
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="handleAnswer">
                    发布回答
                </el-button>
                <el-button @click="cancel">取消</el-button>
            </span>
        </template>
    </el-dialog>
    <el-drawer v-model="answersShowFlag" :show-close="false" size="45%" :direction="direction" destroy-on-close>
        <answerCard v-if="comments.length != 0" v-for="item in comments" :key="item.id" :id="item.id"
            :username="item.username" :quid="uid" :permission="item.permission" :answer="item.answer" :answerTime="item.answerTime" :avatar="item.avatar" />
        <p v-else>暂无回答</p>
        <div class="pagination">
            <el-pagination :hide-on-single-page="value" small v-model:current-page="pageNum" v-model:page-size="pageSize" layout="prev, pager, next"
                :total="total" @current-change="handleCurrentChange" />
        </div>
    </el-drawer>
</template>

<script>
import {
    Edit,
    ChatRound
} from "@element-plus/icons-vue";

import answerCard from "@/components/answerCard"
import { MdEditor, MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

export default {
    name: "questionCard",
    components: {
        answerCard,
        MdEditor,
        MdPreview
    },
    emits: ['setAnswers'],
    created() {
        this.count = this.countInit;
    },
    props: {
        uid: Number,
        title: String,
        poseTime: String,
        username: String,
        avatar: String,
        qid: Number,
        permission: Number,
        countInit: Number,
        description: String
    },
    data() {
        return {
            value: true,
            direction: "rtl",           //drawer出现的方向
            align: "left",              //MdEditor文字方向
            toolbarsA: ['italic', 'bold', 'underline', '-', 'quote', 'unorderedList', 'orderedList', '-', 'code', 'codeRow', 'link', '-', 'revoke', 'next', '=', 'pageFullscreen', 'preview'],
            localUid: JSON.parse(sessionStorage.getItem("user")).uid,
            comments: [],                 //回答内容数组
            pageSize: 5,
            pageNum: 1,
            total: 0,
            answersShowFlag: false,        //是否显示回答区
            content: "",
            visible: false,
            dest: [],
            count: 0,
        }
    },
    methods: {
        //获取5个回答
        loadComments() {
            this.$axios({
                method: "post",
                url: "/answers/query",
                data: {
                    "qid": this.qid,
                    "pageSize": this.pageSize,
                    "pageNum": this.pageNum
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.comments = res.data.data;
                    this.count = res.data.data.length;
                    this.total = res.data.total;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("获取回答时错误: " + err);
            })
        },
        // 页码改变时触发
        handleCurrentChange(val) {
            this.pageNum = val;
            this.loadComments();
        },
        //处理用户的回答提交
        handleAnswer() {
            if (this.content == "") {
                this.$message.error("回答不能为空");
                return;
            }
            this.dest = [];
            this.$axios({
                method: "post",
                url: "/answers/add",
                data: {
                    "qid": this.qid,
                    "uid": JSON.parse(sessionStorage.getItem("user")).uid,
                    "answer": this.content,
                    "answerTime": this.$utils.getNowFormatTime(),
                    "answerDate": this.$utils.getNowFormatTime().split(" ")[0]
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("发布成功！");
                    this.visible = false;
                    this.count++;
                }
            }).catch(err => {
                console.log("提交回答时发生错误: " + err);
            })
        },
        delImages() {
            //取消则根据dest去后端删除图片
            this.$axios({
                method: "post",
                url: "/questions/delImages",
                data: {
                    "imagePaths": this.dest
                }
            }).then(res => {
                if (res.data.code == "200") {
                    console.log("图片删除成功");
                } else {
                    console.log(res.data.msg);
                }
            }).catch(err => {
                console.log("删除图片时错误: " + err);
            })
        },
        cancel() {
            this.visible = false;
            this.content = "";
            if (this.dest.length != 0) {
                this.delImages();
            }
        },
        //显示回答对话框
        writeAnswer() {
            this.visible = true;
        },
        //显示和关闭评论区
        showComments() {
            if (this.count != 0) {
                if (!this.answersShowFlag) {
                    this.loadComments();
                }
                this.answersShowFlag = !this.answersShowFlag;
            }
        },
        //上传图片
        handleImageUpload(event, insertImage, files) {
            let file = files[0];          //拿到图片
            var forms = new FormData();
            forms.append("file", file);
            forms.append("updatedTime", this.$utils.getNowFormatTime());
            forms.append("uid", JSON.parse(sessionStorage.getItem("user")).uid);
            this.$axios({
                method: "post",
                url: "/questions/saveImage",
                data: forms
            }).then(res => {
                if (res.data.code == "200") {
                    let url = res.data.data.showUrl;
                    this.dest.push(res.data.data.dest);
                    insertImage({
                        url: url,
                        width: "400",
                        height: "auto"
                    });
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("上传图片时发生错误: " + err);
            })
        }
    }
}

</script>
<style scoped>
.questionBox {
    width: 100%;
    margin-bottom: 30px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
}

.btnGroup {
    display: flex;
    justify-content: space-evenly;
    width: 20%;
}

p.title {
    text-align: left;
    font-size: 22px;
    font-weight: 600;
}

.poseInfo {
    display: flex;
    align-items: center;
}

.poseInfo .username {
    display: inline-block;
    font-size: 13px;
    margin: 0 10px;
}

.poseTime {
    font-size: 14px;
    color: #ccc;
    text-align: left;
    font-style: italic;
}

.description {
    text-align: left;
    padding: 0;
}

.pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.bottomBox {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

p.answerTitle {
    text-align: left;
    font-size: 16px;
    font-weight: 600;
}
</style>