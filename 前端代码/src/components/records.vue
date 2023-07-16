<template>
    <div class="radioGroup">
        <el-radio-group v-model="radio" @change="handleChange">
            <el-radio-button label="0">我的提问</el-radio-button>
            <el-radio-button label="1">我的回答</el-radio-button>
        </el-radio-group>
    </div>
    <!-- 我的提问表格 -->
    <el-table v-if="radio == '0'" :data="dataQ" height="600" stripe style="width: 100%;" empty-text="暂无提问数据"
        :cell-style="{ 'text-align': 'center' }" :header-cell-style="{ 'background': '#f4f3f9', 'text-align': 'center' }"
        :row-style="{ height: '100px' }">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="posedtime" label="发布时间" />
        <el-table-column label="操作" class="operation">
            <template #default="scope">
                <el-button type="info" size="small" @click="view(scope.row)">查看回答</el-button>
                <el-button type="primary" size="small" @click="questionEdit(scope.row)">编辑</el-button>
                <el-popconfirm title="确认删除吗" @confirm="questionDel(scope.row.id)">
                    <template #reference>
                        <el-button type="danger" size="small">删除</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>
    <!-- 回答表格 -->
    <el-table v-if="radio == '1'" :data="dataA" height="600" stripe style="width: 100%;" empty-text="暂无回答数据"
        :cell-style="{ 'text-align': 'center' }" :header-cell-style="{ 'background': '#f4f3f9', 'text-align': 'center' }">
        <el-table-column prop="title" label="问题" />
        <el-table-column prop="answer" label="回答内容" width="500">
            <template #default="scope">
                <MdPreview :modelValue="scope.row.answer" />
            </template>
        </el-table-column>
        <el-table-column prop="answerTime" label="回答时间" />
        <el-table-column label="操作" class="operation">
            <template #default="scope">
                <el-button type="primary" size="small" @click="answerEdit(scope.row)">编辑</el-button>
                <el-popconfirm title="确认删除吗" @confirm="answerDel(scope.row.id + '')">
                    <template #reference>
                        <el-button type="danger" size="small">删除</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>
    <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
            layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
            :page-sizes="[5, 10, 20, 30]" @current-change="handleCurrentChange" />
    </div>
    <!-- 查看回答对话框,改成抽屉展示 -->
    <el-drawer v-model="visible" :show-close="false" size="40%" :title="'全部回答(' + answers.length + ')'"
        :direction="direction" destroy-on-close>
        <el-scrollbar max-height="480px">
            <answerCard v-if="answers.length != 0" v-for="item in answers" :key="item.id" :id="item.id"
                :username="item.username" :answer="item.answer" :answerTime="item.answerTime" :avatar="item.avatar" />
            <p v-else>暂无回答</p>
        </el-scrollbar>
        <div class="paginationInner">
            <el-pagination v-model:current-page="pageNumInner" v-model:page-size="pageSizeInner"
                layout="total, prev, pager, next" :total="totalInner" @current-change="handleCurrentChangeInner" />
        </div>
    </el-drawer>
    <!-- 问题编辑对话框 -->
    <el-dialog :show-close="false" title="问题编辑" width="70%" top="30px" v-model="editQVisible" destroy-on-close>
        <el-form ref="questionForm" :model="questionObject" labelPosition="top">
            <el-form-item prop="title" label="标题">
                <el-input class="editTitle" maxlength="100" show-word-limit v-model="questionObject.title"
                    style="height: 60px; font-size: 20px;font-weight: 600;" />
            </el-form-item>
            <el-form-item prop="description" label="问题描述">
                <MdEditor v-model="questionObject.description" :toolbars="toolbars" :align="align"
                    placeholder="请简单描述您的问题,右侧为预览区" @onUploadImg="upload" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="confirmQ">提交修改</el-button>
                <el-button @click="cancelQ">关闭</el-button>
            </span>
        </template>
    </el-dialog>
    <!-- 回答编辑对话框 -->
    <el-dialog :show-close="false" :title="answerTitle" width="70%" top="30px" v-model="editAVisible" destroy-on-close>
        <el-form ref="answerForm" :model="answerObject">
            <el-form-item prop="answer">
                <MdEditor v-model="answerObject.answer" :toolbars="toolbarsA" :align="align"
                    placeholder="请简单描述您的问题,右侧为预览区" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="confirmA">提交修改</el-button>
                <el-button @click="cancelA">关闭</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
import answerCard from './answerCard.vue';
import { MdEditor, MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

export default {
    name: "records",
    props: {
        uid: Number
    },
    components: {
        answerCard,
        MdEditor,
        MdPreview
    },
    created() {
        //获取我的提问信息
        this.loadQTable();
    },
    data() {
        return {
            width: "400",           //发布问题时设置图片的宽度
            align: "left",
            toolbars: ['italic', 'bold', 'underline', '-', 'quote', 'unorderedList', 'orderedList', '-', 'code', 'codeRow', 'link', 'image', '-', 'revoke', 'next', '=', 'pageFullscreen', 'preview'],
            toolbarsA: ['italic', 'bold', 'underline', '-', 'quote', 'unorderedList', 'orderedList', '-', 'code', 'codeRow', 'link', '-', 'revoke', 'next', '=', 'pageFullscreen', 'preview'],
            direction: "rtl",
            dest: [],
            answerTitle: "",
            editQVisible: false,
            editAVisible: false,
            visible: false,
            radio: "0",
            background: true,     //分页的背景色
            total: 0,         //接收总的查询数量
            pageSize: 5,     //每页显示的问题个数
            pageNum: 1,       //当前页码
            totalInner: 0,
            pageSizeInner: 5,   //每页5个回答
            pageNumInner: 1,
            dataQ: [],
            dataA: [],
            answers: [],         //查询回答
            questionObject: {   //更新问题对象
                uid: "",
                id: "",
                title: "",
                description: "",
                posedtime: "",
                poseDate: ""
            },
            answerObject: {
                id: "",
                qid: "",
                uid: "",
                answer: "",
                answerTime: "",
                answerDate: ""
            },
        }
    },
    methods: {
        //每点击单选就发一次请求
        handleChange() {
            if (this.radio == "0") {
                this.loadQTable();
            }
            if (this.radio == "1") {
                this.loadATable();
            }
        },
        loadQTable() {
            this.$axios({
                method: "get",
                url: "/questions/myquestions",
                params: {
                    "pageSize": this.pageSize,
                    "pageNum": this.pageNum,
                    "uid": JSON.parse(sessionStorage.getItem("user")).uid
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.dataQ = res.data.data;
                    this.total = res.data.total;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("获取我的提问时出错: " + err);
            })
        },
        loadATable() {
            this.$axios({
                method: "get",
                url: "/answers/myanswers",
                params: {
                    "pageSize": this.pageSize,
                    "pageNum": this.pageNum,
                    "uid": JSON.parse(sessionStorage.getItem("user")).uid
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.dataA = res.data.data;
                    this.total = res.data.total;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("获取我的回答时出错: " + err);
            })
        },
        // 页码改变时触发
        handleCurrentChange(val) {
            this.pageNum = val;
            if (this.radio == "0") {
                this.loadQTable();
            }
            if (this.radio == "1") {
                this.loadATable();
            }
        },
        //页数改变时触发
        handleSizeChange(val) {
            this.pageSize = val;
            this.pageNum = 1;
            if (this.radio == "0") {
                this.loadQTable();
            }
            if (this.radio == "1") {
                this.loadATable();
            }
        },
        //回答内部的页数改变时触发
        handleCurrentChangeInner(val) {
            this.pageSizeInner = val;
            this.pageNumInner = 1;
            this.view();
        },
        //查看提问下的回答
        view(row) {
            this.visible = true;
            this.$axios({
                method: "post",
                url: "/answers/query",
                data: {
                    "qid": row.id,
                    "pageSize": this.pageSizeInner,
                    "pageNum": this.pageNumInner
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.answers = res.data.data;
                    this.totalInner = res.data.total
                } else {
                    this.answers = [];
                    console.log(res.data.msg);
                }
            }).catch(err => {
                console.log("获取回答时错误: " + err);
            })
        },
        // 问题编辑显示
        questionEdit(row) {
            this.questionObject.uid = row.uid + "";
            this.questionObject.id = row.id + "";
            this.questionObject = row;
            this.editQVisible = true;
        },
        //问题删除
        questionDel(id) {
            this.$axios({
                method: "get",
                url: `/questions/del/${id}`,
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("删除成功");
                    this.loadQTable();                      //重新加载数据
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("删除时出现错误: " + err);
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
        //问题编辑框取消
        cancelQ() {
            this.editQVisible = false;
            this.$refs['questionForm'].resetFields();
            if (this.dest.length != 0) {
                this.delImages();
            }
        },
        //回答编辑框取消
        cancelA() {
            this.editAVisible = false;
            this.$refs['answerForm'].resetFields();
            if (this.dest.length != 0) {
                this.delImages();
            }
        },
        //回答编辑
        answerEdit(row) {
            this.answerTitle = row.title;
            this.editAVisible = true;
            this.answerObject.id = row.id + "";
            this.answerObject.uid = row.uid + "";
            this.answerObject.qid = row.uid + "";
            this.answerObject = row;
        },
        //回答删除
        answerDel(id) {
            ;
            this.$axios({
                method: "get",
                url: `/answers/del/${id}`,
            }).then(res => {
                this.$message.success("删除成功");
                this.loadATable();                      //重新加载数据
            }).catch(err => {
                console.log("删除时出现错误: " + err);
            })
        },
        //提交修改
        confirmQ() {
            if (this.questionObject.title == "" || this.questionObject.description == "") {
                this.$message.error("标题和描述不能为空!");
                return;
            }
            if (this.dest.length != 0) {
                this.dest = [];
            }
            this.questionObject.posedtime = this.$utils.getNowFormatTime();
            this.questionObject.poseDate = this.$utils.getNowFormatTime().split(" ")[0];
            this.$axios({
                method: "post",
                url: "/questions/update",
                data: this.questionObject
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("修改成功");
                    this.editQVisible = false;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("修改提问时出错: " + err);
            })
        },
        confirmA() {
            if (this.answerObject.answer == "") {
                this.$message.error("回答不能为空!");
                return;
            }
            if (this.dest.length != 0) {
                this.dest = [];
            }
            this.answerObject.answerTime = this.$utils.getNowFormatTime();
            this.answerObject.answerDate = this.$utils.getNowFormatTime().split(" ")[0];
            this.$axios({
                method: "post",
                url: "/answers/update",
                data: this.answerObject
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("修改成功");
                    this.editAVisible = false;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("修改回答时出错: " + err);
            })
        },
        //上传图片
        upload(files) {
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
                    this.content += `<img src='${url}' width=${this.width}>`;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("上传图片时发生错误: " + err);
            })
        },
    }
}

</script>
<style scoped>
.radioGroup {
    text-align: left;
    margin: 30px 0;
}

.operation {
    display: flex;
    justify-content: space-evenly;
}

.pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.paginationInner {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.editTitle {
    margin-bottom: 20px;
}

/* 设置内容超过3行省略 */
.descStyle {
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-all;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
}</style>