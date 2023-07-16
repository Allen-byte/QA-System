<template>
   <div class="funcArea">
      <el-input v-model="usernameQuery" placeholder="请输入用户名(支持模糊查询)" clearable style="width: 260px;margin-right: 20px;" />
      <el-date-picker v-model="date" format="YYYY/MM/DD" value-format="YYYY-MM-DD" type="date" placeholder="选择查询日期"
         :size="size" />
      <el-button type="primary" style="margin-left: 10px;" @click="load(date, usernameQuery)" plain>查询</el-button>
   </div>
   <p class="top" style="text-align: left;color: #73767a;font-size: 16px;">
   <p>
      消息：{{ total }}
   </p>
   <p class="right">
      <el-button type="primary" @click="addMessage">添加消息</el-button>
   </p>
   </p>

   <el-table v-if="tableData.length != 0" :data="tableData" height="600" stripe style="width: 100%;"
      :cell-style="{ 'text-align': 'center' }" :header-cell-style="{ 'background': '#f4f3f9', 'text-align': 'center' }"
      :row-style="{ height: '100px' }">
      <el-table-column prop="username" label="接收对象" />
      <el-table-column prop="account" label="账号" />
      <el-table-column prop="message" label="消息内容" width="300px" />
      <el-table-column prop="flag" label="状态">
         <template #default="scope">
            <el-tag :type="scope.row.flag === 0 ? 'danger' : ''">
               {{ scope.row.flag === 0 ? "未读" : "已读" }}
            </el-tag>
         </template>
      </el-table-column>
      <el-table-column prop="time" label="回答时间" />
      <el-table-column label="操作" class="operation">
         <template #default="scope">
            <el-popconfirm title="确认删除吗" @confirm="messageDel(scope.row.mid)">
               <template #reference>
                  <el-button type="danger" size="small">删除</el-button>
               </template>
            </el-popconfirm>
         </template>
      </el-table-column>
   </el-table>
   <el-empty description="暂无消息数据" v-else />
   <div class="pagination">
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
         layout="prev, pager, next, jumper" :hide-on-single-page="hide" :total="total"
         @current-change="handleCurrentChange" @size-change="handleSizeChange" />
   </div>
   <!-- 发送消息对话框 -->
   <el-dialog :show-close="false" title="发送消息" width="60%" top="30px" v-model="editVisible">
      <el-scrollbar max-height="500px">
         <el-form ref="messageForm" :model="messages" labelPosition="top">
            <el-form-item prop="account" label="发送对象账号">
               <el-autocomplete :debounce="0" :fetch-suggestions="queryAccount" v-model="messages.account"
                  style="width:80%;" />
            </el-form-item>
            <el-form-item prop="message" label="消息内容">
               <el-input type="textarea" :rows="3" v-model="messages.message" placeholder="【管理员】" maxlength="200"
                  show-word-limit />
            </el-form-item>
         </el-form>
      </el-scrollbar>
      <template #footer>
         <span class="dialog-footer">
            <el-button @click="confirm" type="primary">发送</el-button>
            <el-button @click="cancelEdit">关闭</el-button>
         </span>
      </template>
   </el-dialog>
</template>

<script>
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';


export default {
   name: "messagesManage",
   components: {
      MdPreview,
   },
   data() {
      return {
         hide: true,          //单个页面时不显示分页
         usernameQuery: "",     //根据用户名查询
         size: 'default',
         date: "",             //选择的日期
         editVisible: false,
         tableData: [],
         pageSize: 10,
         pageNum: 1,
         total: 0,
         background: true,
         messages: {
            account: "",
            message: "",
            time: "",
         },
         accounts: []        //保存查询的全部账号
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
      load(date = '', username = '') {
         this.$axios({
            method: "post",
            url: "/messages/all",
            data: {                             //post使用data，get使用params
               "pageSize": this.pageSize,
               "pageNum": this.pageNum,
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
      //关闭对话框
      cancelEdit() {
         this.editVisible = false;
         this.$refs['messageForm'].resetFields();
      },
      messageDel(id) {
         this.$axios({
            method: "get",
            url: `/messages/remove/${id}`
         }).then(res => {
            if (res.data.code == "200") {
               this.$message.success("删除成功");
               this.load();
            } else {
               this.$message.error(res.data.msg);
            }
         }).catch(err => {
            console.log("删除消息时错误: " + err);
         })
      },
      // 添加消息
      addMessage() {
         this.editVisible = true;
      },
      //确认发送
      confirm() {
         if (this.messages.message != "" && this.messages.account != "") {
            this.messages.time = this.$utils.getNowFormatTime();
            this.$axios({
               method: "post",
               url: '/messages/add',
               data: this.messages
            }).then(res => {
               if (res.data.code == "200") {
                  this.$message.success("添加成功");
                  this.editVisible = false;
                  this.$refs['messageForm'].resetFields();
                  this.load();
               } else {
                  this.$message.error(res.data.msg);
               }
            }).catch(err => {
               console.log("添加消息时错误: " + err);
            })
         }
      },
      //获取全部account用于展示
      queryAccount(queryString, cb) {
         this.$axios({
            method: "post",
            url: "/messages/allAccounts",
         }).then(res => {
            if (res.data.code == 200) {
               if (this.accounts.length != 0) {
                  this.accounts = [];           //置空是因为之前的选项会被保存，出现多个重复的选项
               }
               for (let item of res.data.data) {
                  this.accounts.push({ "value": item.account })
               }
               cb(this.accounts);
            }else{
               this.$message.error(res.data.msg);
            }
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

.top {
   display: flex;
   justify-content: space-between;
   align-items: center;
}
</style>