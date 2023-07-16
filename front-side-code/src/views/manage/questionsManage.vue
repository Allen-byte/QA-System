<template>
    <div class="funcArea">
        <el-input v-model="usernameQuery" placeholder="请输入用户名(支持模糊查询)" clearable style="width: 260px;margin-right: 20px;" />
        <el-input v-model="content" clearable placeholder="请输入问题内容(支持模糊查询)" style="width: 260px;margin-right: 20px;"
            suffix-icon="el-icon-search" />
        <el-date-picker v-model="date" format="YYYY/MM/DD" value-format="YYYY-MM-DD" type="date" placeholder="选择查询日期"
            :size="size" />
        <el-button type="primary" style="margin-left: 10px;" @click="load(content, date, usernameQuery)" plain>查询</el-button>
    </div>
    <p class="top" style="text-align: left;color: #73767a;font-size: 16px;">
        提问：{{ total }}
    </p>
    <el-table v-if="tableData.length != 0" :data="tableData" height="600" stripe style="width: 100%;"
        :cell-style="{ 'text-align': 'center' }" :header-cell-style="{ 'background': '#f4f3f9', 'text-align': 'center' }"
        :row-style="{ height: '100px' }">
        <el-table-column prop="question" label="标题" width="350px" />
        <el-table-column prop="username" label="题主" />
        <el-table-column prop="account" label="题主账号" />
        <el-table-column prop="poseTime" label="提问时间" width="200px"/>
        <el-table-column label="已提问天数">
            <template #default="scope">
                {{ this.$utils.dateDiff(scope.row.poseTime.split(" ")[0]) }}
            </template>
        </el-table-column>
        <el-table-column prop="count" label="回答数量" />
        <el-table-column label="操作" class="operation" width="200px">
            <template #default="scope">
                <el-button type="primary" size="small" @click="questionEdit(scope.row)">
                    <el-icon>
                        <View />
                    </el-icon>
                    查看
                </el-button>
                <el-popconfirm title="确认删除吗" @confirm="questionDel(scope.row.qid)">
                    <template #reference>
                        <el-button type="danger" size="small">删除</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>
    <el-empty description="暂无提问数据" v-else />
    <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
            layout="prev, pager, next, jumper" :hide-on-single-page="hide" :total="total"
            @current-change="handleCurrentChange" @size-change="handleSizeChange" />
    </div>
    <el-dialog :show-close="false" title="查看提问" width="60%" top="30px" v-model="editVisible">
        <el-scrollbar max-height="500px">
            <el-form ref="questionForm" :model="questions" labelPosition="top">
                <el-form-item prop="question" label="标题">
                    <h2>
                        {{ questions.question }}
                    </h2>
                </el-form-item>
                <el-form-item label="问题描述">
                    <MdPreview :modelValue="questions.description" />
                </el-form-item>
                <el-form-item prop="posedTime" label="提问时间">
                    <h3>
                        {{ questions.poseTime }}
                    </h3>
                </el-form-item>
            </el-form>
        </el-scrollbar>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="cancelEdit">关闭</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';


export default {
    name: "questionsManage",
    components: {
        MdPreview,
    },
    data() {
        return {
            hide: true,          //单个页面时不显示分页
            content: "",        //筛选title内容
            usernameQuery: "",
            size: 'default',
            date: "",             //选择的日期
            editVisible: false,
            tableData: [],
            pageSize: 10,
            pageNum: 1,
            total: 0,
            background: true,
            questions: {
                qid: "",
                uid: "",
                question: "",
                poseTime: "",
                poseDate: "",
                description: "",
                count: "",
                avatar: ""
            },
        }
    },
    created() {
        this.load();
    },
    methods: {
        handleSizeChange(val) {
            this.pageNum = 1;
            this.pageSize = val;
            this.load();
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.load();
        },
        load(content = '', date = '', username = '') {
            this.$axios({
                method: "get",
                url: "/questions/all",
                params: {                             //post使用data，get使用params
                    "pageSize": this.pageSize,
                    "pageNum": this.pageNum,
                    "content": content,
                    "date": date,
                    "username": username
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.tableData = res.data.data;
                    this.total = res.data.total;
                } else {
                    this.$message.warning(res.data.msg);
                }
            }).catch(err => {
                console.log("请求分页时发生错误: " + err);
            })
        },
        questionEdit(row) {
            this.editVisible = true;
            this.questions = row;
        },
        //关闭对话框
        cancelEdit() {
            this.editVisible = false;
        },
        questionDel(id) {
            this.$axios({
                method: "get",
                url: `/questions/del/${id}`
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("删除成功");
                    this.load();
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("删除提问时错误: " + err);
            })
        }
    }
}

</script>
<style scoped>
.pagination {
    display: flex;
    justify-content: center;
    margin-top: 30px;
}

.funcArea {
    margin-bottom: 20px;
}
</style>