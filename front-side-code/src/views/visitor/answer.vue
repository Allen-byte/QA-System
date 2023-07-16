<template>
    <el-table :data="tableData" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ 'text-align': 'center' }"
        style="width: 100%;">
        <el-table-column prop="id" label="ID" />
        <el-table-column prop="title" label="问题" />
        <el-table-column prop="posedtime" label="提问时间" />
        <el-table-column prop="answer" label="回答" />
        <el-table-column prop="solvedtime" label="回答时间" />
        <el-table-column prop="flag" label="是否回答">
            <template #default="scope">
                <el-tag :type="scope.row.flag === 1 ? 'success' : 'warning'" effect="dark" disable-transition>
                    {{ scope.row.flag === 1 ? '已回答' : '未回答' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作">
            <template #default="scope">
                <el-button type="primary" @click="handleClick(scope.row)">
                    {{ scope.row.flag === 1 ? '修改' : '回答' }}
                </el-button>
                <el-popconfirm title="确定删除吗?" @confirm="handleDel(scope.row.id)">
                    <template #reference>
                        <el-button type="danger">删除</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>
    <div class="pagination">
        <!-- 分页组件 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :disabled="disabled"
            :background="background" layout="total, sizes, prev, pager, next, jumper" :total="total"
            @size-change="handleSizeChange" :page-sizes="[5, 10, 20, 30]" @current-change="handleCurrentChange" />
    </div>
    <!-- 当点击回答或修改按钮时，弹出对话框来书写回答 -->
    <el-dialog v-model="dialogVisible" title="回答问题" width="30%">
        <el-form ref="form" :model="form" label-width="80px">
            <el-form-item label="问题" prop="title">
                <el-input type="textarea" rows="5" v-model="form.title" style="width: 80%;" disabled></el-input>
            </el-form-item>
            <el-form-item label="提问时间" prop="posedtime">
                <el-input v-model="form.posedtime" style="width: 80%;" disabled></el-input>
            </el-form-item>
            <el-form-item label="回答" prop="answer">
                <el-input v-model="form.answer" type="textarea" rows="5" style="width: 80%;" maxlength="300"
                    show-word-limit></el-input>
            </el-form-item>
        </el-form>
        <!-- 底部按钮 -->
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="cancel">取消</el-button>
                <el-button type="primary" @click="submit">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
export default {
    name: 'answer',
    mounted() {
        this.loadAll();
    },
    data() {
        return {
            tableData: [],
            dialogVisible: false,
            background: true,
            disabled: false,
            total: 0,         //接收总的查询数量
            pageSize: 20,     //回答页面，每页显示的问题个数可以稍微多一点
            pageNum: 1,       //当前页码
            form: {
                id: '',
                title: '',
                posedtime: '',
                answer: '',
                solvedtime: '',
                flag: '0'
            }
        }
    },
    methods: {
        // 点击回答按钮事件
        handleClick(row) {
            this.dialogVisible = true;        //显示对话框
            this.form.id = row.id;                  //展示行数据
            this.form.title = row.title;
            this.form.posedtime = row.posedtime;
            this.form.flag = row.flag + '';   //注意row.flag是数字，需要转为字符串
        },
        // 处理删除事件
        handleDel(id) {
            this.$axios({
                method: "get",
                url: `/questions/del/${id}`,
            }).then(res => {
                this.$message.success("删除成功");
                this.loadAll();                      //重新加载数据
            }).catch(err=>{
                console.log("删除时出现错误: " + err);
            })
        },
        // 提交修改数据(主要是回答内容)
        submit() {
            this.$axios({
                method: "post",
                url: "/questions/submit",
                data: {
                    id: this.form.id,
                    answer: this.form.answer,
                    solvedtime: this.$utils.getNowFormatTime(),
                    flag: 1,
                }
            }).then(res => {
                this.dialogVisible = false;
                this.$refs['form'].resetFields();
                this.$message.success("回答成功");
                this.loadAll();                      //重新加载数据
            }).catch(err => {
                console.log("提交回答时出现错误: " + err);
            })
        },
        //取消对话框，清除数据
        cancel() {
            this.dialogVisible = false;
            this.$refs['form'].resetFields();
        },
        //加载全部问题
        loadAll() {
            this.$axios({
                method: "get",
                url: "/questions/all",
                params: {
                    pageSize: this.pageSize,
                    pageNum: this.pageNum,
                    value: ""
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.tableData = res.data.data;
                    this.total = res.data.total;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log(err);
            })
        },
        // 页码改变时触发
        handleCurrentChange(val){
            this.pageNum = val;
            this.loadAll();
        },
        //页数改变时触发
        handleSizeChange(val){
            this.pageSize = val;
            this.pageNum = 1;
            this.loadAll();
        }
    }
}

</script>


<style scoped>
    .pagination{
        margin-top: 30px;
        display: flex;
        justify-content: center;
    }

</style>