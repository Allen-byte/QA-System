<template>
    <div class="userBox">
        <div class="avatarBox">
            <div class="top">
                <el-avatar :size="size" :src="avatarUrl" />
            </div>
            <!-- 用户名和性别、uid等信息 -->
            <div class="metaBox">
                <p>
                    <span class="username">{{ user.username }}</span>
                    <span class="sex" v-if="user.gender === '0'">
                        <el-icon :size="20" color="blue">
                            <Male />
                        </el-icon>
                    </span>
                    <span class="sex" v-else>
                        <el-icon :size="20" color="pink">
                            <Female />
                        </el-icon>
                    </span>
                </p>
                <p class="uid">
                    UID: {{ user.uid.toString().padStart(5, '0') }}
                </p>
            </div>

        </div>
        <div class="dataBox">
            <div class="row info">
                <p class="accountBox box">
                    <span class="account">账号</span>
                    <span>{{ user.account }}</span>
                </p>
                <p class="loginTimeBox box">
                    <span class="loginTime">上次登录</span>
                    <span>{{ user.loginTime }}</span>
                </p>
                <p class="registerBox box">
                    <span class="registerTime">您已加入</span>
                    <span>{{ days }} 天</span>
                </p>
            </div>
            <div class="row btnGroup">
                <el-button type="primary" style="width: 20%;" @click="edit" plain>编辑资料</el-button>
                <el-button type="primary" style="width: 20%; " @click="updateAvatar" plain>修改头像</el-button>
            </div>

        </div>
        <!-- 资料编辑框 -->
        <el-dialog center v-model="dialogVisible" :showClose="false" title="资料修改" width="40%">
            <el-form :rules="updateRules" :model="user" ref="updateForm" label-width="80px">
                <el-form-item prop="account" label="账号">
                    <el-input v-model="user.account" disabled></el-input>
                </el-form-item>
                <el-form-item prop="username" label="用户名">
                    <el-input v-model="user.username" clearable></el-input>
                </el-form-item>
                <el-form-item prop="password" label="密码">
                    <el-input show-password auto-complete="false" clearable v-model="user.password" placeholder="请输入新密码，如不更改可忽略"></el-input>
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
                    <el-button type="primary" @click="handleUpdate">
                        确认修改
                    </el-button>
                    <el-button @click="updateCancel">取消</el-button>
                </span>
            </template>
        </el-dialog>
        <!-- 头像编辑框 -->
        <el-dialog class="avatarDialog" :showClose="false" center v-model="avatarDialogVisible" title="更新头像">
            <el-upload ref="upload" class="avatar-uploader" :before-upload="beforeAvatarUpload" action="#"
                :http-request="uploadFile" :auto-upload="false" multiple list-type="picture-card" :file-list="fileList"
                :on-change="fileChange">
                <el-icon>
                    <Plus />
                </el-icon>
                <template #file="{ file }">
                    <img :src="file.url" alt="" />
                </template>
            </el-upload>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="handleUpdateAvatar">
                        确认更新
                    </el-button>
                    <el-button @click="cancel">取消</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
    <el-divider />
    <!-- 提问和回答数据 -->
    <Data :uid="user.uid" />
    <!-- 我的提问记录、回答记录（和关注记录） -->
    <records :uid="user.uid" />
</template>

<script>
import {
    Female,
    Male
} from "@element-plus/icons-vue";

import Data from "@/components/Data";
import records from "@/components/records";
import { mapState } from "vuex";

export default {
    name: "workbench",
    components: {
        Data,
        records
    },
    computed: {
        ...mapState({
            menu: "menu"
        })
    },
    data() {
        return {
            password: "",             //临时记录用户密码md5加密结果
            days: "",                  //距离注册时间的天数
            fileList: [],              //上传的文件列表
            dialogVisible: false,      //资料对话框显示控制
            avatarDialogVisible: false,   //头像框显示控制
            size: 150,                 //头像尺寸
            user: {},
            avatarUrl: "",
            updateRules: {
                username: [
                    {
                        required: true,
                        trigger: "blur",
                        message: "用户名不能为空哦",
                    },
                    {
                        trigger: "blur",
                        min: 2,
                        max: 16,
                        message: "用户名长度为2-16个字符"
                    }
                ],
                password: [
                    {
                        trigger: "blur",
                        min: 6,
                        max: 16,
                        message: "密码长度为6-16个字符"
                    }
                ],
            }
        }
    },
    created() {
        this.readlocalUser();
    },
    methods: {
        readlocalUser() {
            let user = JSON.parse(sessionStorage.getItem("user"));
            let avatar = sessionStorage.getItem("avatar");
            this.user = user;
            this.password = this.user.password;
            this.user.password = "";            //密码置空，因为返回的是md5加密的结果,不便给用户展示
            this.avatarUrl = this.$server + avatar;
            this.user.gender += '';
            //计算注册时间的天数
            this.days = this.$utils.dateDiff(this.user.registerTime);
        },
        edit() {
            this.dialogVisible = true;
        },
        // 自定义上传图片方法,参数必须为params(头像)
        uploadFile(params) {
            const file = params.file;
            var forms = new FormData();
            forms.append("file", file);
            forms.append("updatedTime", this.$utils.getNowFormatTime());
            forms.append("uid", this.user.uid);
            this.$axios({
                method: "post",
                url: "/avatar/upload",
                data: forms,
            }).then(res => {
                if (res.data.code == "200") {
                    this.$message({
                        message: '上传成功',
                        type: 'success'
                    })
                    this.avatarDialogVisible = false;
                    this.avatarUrl = this.$server + res.data.data;        //后端返回图像的地址
                    //不管本地有没有记录，有就修改，没有就添加
                    sessionStorage.setItem("avatar", res.data.data);
                    this.$store.commit("setAvatar", this.avatarUrl);
                } else {
                    this.$message({
                        message: '上传失败',
                        type: 'error'
                    })
                }
            }).catch(err => {
                console.log("上传文件时发生错误: " + err);
            })
        },
        // 上传图片时检查
        beforeAvatarUpload(file) {
            //上传文件的格式
            const idJPG = (file.type === 'image/jpeg') || (file.type === 'image/png') || (file.type === 'image/gif');
            //上传文件的大小
            const isLt2M = (file.size / 1024 / 1024) < 2;
            if (!idJPG) {
                this.$message.error("上传的图片只能是jpg或png格式");
                return false;
            }
            if (!isLt2M) {
                this.$message.error("上传的图片大小不能超过2MB");
                return false;
            }
            console.log("检查通过");
            return true;
        },
        //文件改变时的钩子函数,覆盖掉之前的图片，只能上传一张
        fileChange(file, fileList) {
            this.file = file.raw;
            if (fileList.length > 0) {
                this.fileList = [fileList[fileList.length - 1]];
            }
        },
        // 头像框显示
        updateAvatar() {
            this.avatarDialogVisible = true;
        },
        // 确认修改头像
        handleUpdateAvatar() {
            this.$refs['upload'].submit();
            this.avatarDialogVisible = false;
            this.$refs['upload'].clearFiles();
        },
        // 取消上传头像
        cancel() {
            this.avatarDialogVisible = false;
            this.$refs['upload'].clearFiles();
        },
        //取消修改资料
        updateCancel() {
            this.dialogVisible = false;
            this.$refs['updateForm'].resetFields();
        },
        //资料框确认修改
        handleUpdate() {
            //根据this.user.password判断用户是否输入新密码
            //为""说明未输入新密码，则将之前的密码重新赋值
            if(this.user.password==""){     
                this.user.password = this.password;
            }
            //否则直接将新密码上传
            this.$refs['updateForm'].validate((valid) => {
                if (valid) {
                    this.$axios({
                        method: "post",
                        url: "/user/update",
                        data: this.user
                    }).then(res => {
                        if (res.data.code == "200") {
                            this.dialogVisible = false;
                            this.$store.commit("setUser", this.user);    //设置用户名，用于顶部监听
                            sessionStorage.setItem("user", JSON.stringify(this.user));   //修改保存到本地
                            this.$message.success("更新成功");
                        } else {
                            console.log(res.data.msg);
                        }
                    }).catch(err => {
                        this.$message.error("更新信息时发生错误: " + err)
                    })
                }
            })
        }
    }
}

</script>
<style scoped>
.metaBox span {
    display: inline-block;
}

.metaBox .username {
    margin-right: 10px;
    font-size: 18px;
    font-weight: 600;
}

p.uid {
    font-size: 14px;
    color: #fff;
    font-weight: 500;
    text-align: center;
    padding: 0 15px;
    width: fit-content;
    height: 20px;
    border-radius: 15px;
    line-height: 20px;
    background-color: #00BFFF;
}


.avatarDialog {
    text-align: center;
}

.avatar-uploader {
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    text-align: center;
}

.userBox {
    display: flex;
}


.avatarBox {
    width: 40%;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
}

.dataBox {
    width: 60%;
    text-align: center;
    padding-bottom: 20px;
    padding-top: 40px;
}

.box {
    width: 100%;
    text-align: left;
}

.box span:first-child {
    margin-right: 15px;
    display: inline-block;
    width: 80px;
    color: #606266;
    font-weight: 600;
}

.box span:last-child {
    display: inline-block;
    text-align: center;
    width: 200px;
    height: 25px;
    line-height: 25px;
    border-radius: 5px;
    background-color: #606266;
    color: #fff;
}

.info{
    display: flex;
    flex-direction: column;
}

.box{
    display: flex;
    justify-content: center;
}

.btnGroup {
    justify-content: space-evenly;
    margin-top: 20px;
    display: flex;
}
</style>