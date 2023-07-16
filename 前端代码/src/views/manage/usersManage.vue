<template>
    <div class="funcArea">
        <el-input v-model="username" placeholder="请输入用户名(支持模糊查询)" clearable style="width: 240px;margin-right: 20px;"
            suffix-icon="el-icon-search"></el-input>
        <el-select v-model="gender" placeholder="请选择性别" filterable style="margin-left: 5px;">
            <el-option v-for="item in genders" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
        </el-select>
        <el-button type="primary" style="margin-left: 10px;" @click="load(username, gender)" plain>查询</el-button>
    </div>
    <p class="top" style="text-align: left;color: #73767a;font-size: 16px;">
        用户：{{ total }}
    </p>
    <el-table v-if="tableData.length != 0" :data="tableData" height="600" stripe :cell-style="{ 'text-align': 'center' }"
        :header-cell-style="{ 'background': '#f4f3f9', 'text-align': 'center' }" :row-style="{ height: '100px' }">
        <el-table-column prop="account" label="账号" />
        <el-table-column prop="avatar" label="头像">
            <template #default="scope">
                <el-avatar :src="this.$server + scope.row.avatar" :size="60" />
            </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="gender" label="性别">
            <template #default="scope">
                <el-tag :type="scope.row.gender === '0' ? '' : 'success'">
                    {{ scope.row.gender === '0' ? '男' : '女' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="loginTime" label="最近登录" />
        <el-table-column label="注册天数">
            <template #default="scope">
                {{ this.$utils.dateDiff(scope.row.registerTime.split(' ')[0]) }}
            </template>
        </el-table-column>

        <el-table-column label="操作" class="operation">
            <template #default="scope">
                <el-button type="primary" size="small" @click="userEdit(scope.row)">编辑</el-button>
                <el-popconfirm title="确认删除吗" @confirm="userDel(scope.row.uid)">
                    <template #reference>
                        <el-button type="danger" size="small">删除</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>
    <el-empty description="暂无用户数据" v-else />
    <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
            layout="prev, pager, next, jumper" :hide-on-single-page="hide" :total="total"
            @current-change="handleCurrentChange" @size-change="handleSizeChange" />
    </div>
    <!-- 用户编辑对话框 -->
    <el-dialog :show-close="false" title="用户信息编辑" width="40%" top="30px" v-model="editVisible">
        <el-form ref="userForm" :model="user" labelPosition="top">
            <el-form-item prop="avatar" label="头像">
                <el-avatar :src="this.$server + user.avatar" :size="100" />
            </el-form-item>
            <el-form-item prop="account" label="账号">
                <el-input :disabled="disabled" v-model="user.account" />
            </el-form-item>
            <el-form-item prop="username" label="用户名">
                <el-input v-model="user.username" />
            </el-form-item>
            <el-form-item prop="gender" label="性别">
                <el-radio-group v-model="user.gender">
                    <el-radio label="0" size="large" border>男</el-radio>
                    <el-radio label="1" size="large" border>女</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="confirm">提交修改</el-button>
                <el-button @click="cancelEdit">关闭</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
export default {
    name: "usersManage",
    data() {
        return {
            hide: true,
            username: "",           //根据用户名查询
            gender: "",             //根据性别筛选
            genders: [
                {
                    label: "男",
                    value: "0"
                },
                {
                    label: "女",
                    value: "1"
                }
            ],
            disabled: true,
            editVisible: false,
            tableData: [],
            pageSize: 10,
            pageNum: 1,
            total: 0,
            background: true,
            user: {
                account: '',
                username: '',
                avatar: '',
                gender: '',
                loginTime: "",
                registerTime: ""
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
        //这里的username和gender用于后面的用户查询和根据性别筛选结果
        load(username = '', gender = '') {
            this.$axios({
                method: "post",
                url: "/user/all",
                data: {                             //post使用data，get使用params
                    "pageSize": this.pageSize,
                    "pageNum": this.pageNum,
                    "username": username,
                    "gender": gender,
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
        userEdit(row) {
            this.editVisible = true;
            this.user = row;
        },
        //取消修改
        cancelEdit() {
            this.editVisible = false;
            this.$refs['userForm'].resetFields();
        },
        //提交修改
        confirm() {
            this.$axios({
                method: "post",
                url: "/user/update",
                data: this.user
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("修改成功");
                } else {
                    this.$message.error(res.data.msg);
                }
                this.editVisible = false;
            }).catch(err => {
                console.log("修改用户信息时出错: " + err);
            })
        },
        //删除用户
        userDel(id) {
            this.$axios({
                method: "get",
                url: `/user/del/${id}`
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message.success("删除成功");
                    this.load();
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("删除用户时错误: " + err);
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