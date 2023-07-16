<template>
   <div class="funcArea">
      <el-input v-model="usernameQuery" placeholder="请输入用户名(支持模糊查询)" clearable style="width: 260px;margin-right: 20px;" />
      <el-input v-model="content" placeholder="请输入回答内容(支持模糊查询)" clearable style="width: 260px;margin-right: 20px;" />
      <el-date-picker v-model="date" format="YYYY/MM/DD" value-format="YYYY-MM-DD" type="date" placeholder="选择查询日期"
         :size="size" />
      <el-button type="primary" style="margin-left: 10px;" @click="load(content, date, usernameQuery)" plain>查询</el-button>
   </div>
   <p class="top" style="text-align: left;color: #73767a;font-size: 16px;">
      回答：{{ total }}
   </p>
   <el-table v-if="tableData.length != 0" :data="tableData" height="600" stripe style="width: 100%;"
      :cell-style="{ 'text-align': 'center' }" :header-cell-style="{ 'background': '#f4f3f9', 'text-align': 'center' }"
      :row-style="{ height: '100px' }">
      <el-table-column prop="answer" label="回答内容" width="300px">
         <template #default="scope">
            {{ scope.row.answer.length > contentLength?scope.row.answer.slice(0, contentLength) + ". . .":scope.row.answer }}
         </template>
      </el-table-column>
      <el-table-column prop="title" label="所属提问" />
      <el-table-column prop="username" label="答主" />
      <el-table-column prop="account" label="答主账号" />
      <el-table-column prop="answerTime" label="回答时间" />
      <el-table-column label="操作" class="operation">
         <template #default="scope">
            <el-button type="primary" size="small" @click="answerEdit(scope.row)">
               <el-icon>
                  <View />
               </el-icon>
               查看
            </el-button>
            <el-popconfirm title="确认删除吗" @confirm="answerDel(scope.row.id)">
               <template #reference>
                  <el-button type="danger" size="small">删除</el-button>
               </template>
            </el-popconfirm>
         </template>
      </el-table-column>
   </el-table>
   <el-empty description="暂无回答数据" v-else />
   <div class="pagination">
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :background="background"
         layout="prev, pager, next, jumper" :hide-on-single-page="hide" :total="total"
         @current-change="handleCurrentChange" @size-change="handleSizeChange" />
   </div>
   <el-dialog :show-close="false" title="查看回答" width="60%" top="30px" v-model="editVisible">
      <el-scrollbar max-height="500px">
         <el-form ref="answerForm" :model="answers" labelPosition="top">
            <el-form-item prop="answer" label="回答内容">
               <MdPreview :modelValue="answers.answer" />
            </el-form-item>
            <el-form-item label="所属提问">
               <h3>
                  {{ answers.title }}
               </h3>
            </el-form-item>
            <el-form-item prop="username" label="答主">
               <h3>
                  {{ answers.username }}
               </h3>
            </el-form-item>
            <el-form-item prop="answerTime" label="回答时间">
               <h3>
                  {{ answers.answerTime }}
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
   name: "answersManage",
   components: {
      MdPreview,
   },
   data() {
      return {
         contentLength: 50,      //截取回答的前50个字符
         hide: true,          //单个页面时不显示分页
         content: "",        //筛选title内容,
         usernameQuery: "",     //根据用户名查询
         size: 'default',
         date: "",             //选择的日期
         editVisible: false,
         tableData: [],
         pageSize: 10,
         pageNum: 1,
         total: 0,
         background: true,
         answers: {
            title: "",
            answer: '',
            answerDate: "",
            answerTime: "",
            username: "",
            account: ""
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
            url: "/answers/all",
            params: {                             //post使用data，get使用params
               "pageSize": this.pageSize,
               "pageNum": this.pageNum,
               "content": content,
               "date": date,
               "username": username
            }
         }).then(res => {
            if(res.data.code == "200"){
               this.tableData = res.data.data;
               this.total = res.data.total;
            }else{
               this.$message.warning(res.data.msg);
            }
         }).catch(err => {
            console.log("请求分页时发生错误: " + err);
         })
      },
      answerEdit(row) {
         this.editVisible = true;
         this.answers = row;
      },
      //关闭对话框
      cancelEdit() {
         this.editVisible = false;
      },
      answerDel(id) {
         this.$axios({
            method: "get",
            url: `/answers/remove/${id}`
         }).then(res => {
            if (res.data.code == "200") {
               this.$message.success("删除成功");
               this.load();
            } else {
               this.$message.error(res.data.msg);
            }
         }).catch(err => {
            console.log("删除回答时错误: " + err);
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