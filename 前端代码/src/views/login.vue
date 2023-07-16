<template>
    <div class="loginBox">
        <div class="titleBox">
            <p class="left">
                {{ leftText }}
            </p>
            <p class="right">
                {{ rightText }}
            </p>
        </div>
        <el-form :rules="loginRules" ref="loginForm" :model="loginForm" labelPosition="top" class="loginContainer"
            @keyup.enter.native="loginSubmit">
            <div class="avatarBox">
                <el-avatar :src="avatarUrl" :size="120" />
            </div>
            <el-form-item prop="account" label="账号">
                <el-input clearable type="text" auto-complete="false" v-model="loginForm.account" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item prop="password" label="密码">
                <el-input clearable show-password auto-complete="false" v-model="loginForm.password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item style="text-align: center;">
                <el-button style="width: 100%;" :disabled="confirm_disabled" type="primary"
                    @click="loginSubmit">登录</el-button>
            </el-form-item>
            <el-form-item>
                <el-link type="primary" href="/#/register">没有账号?去注册</el-link>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>

export default {
    name: "login",
    data() {
        // 查找用户的头像
        const getAvatarValidation = (rule, value, callback) => {
            this.$axios({
                method: "get",
                url: "/avatar/getAvatar",
                params: {
                    "account": value
                }
            }).then(res => {
                if (res.data.code == "200") {
                    this.avatarUrl = this.$server + res.data.data;
                    sessionStorage.setItem("avatar", res.data.data);
                    callback();
                } else {
                    this.avatarUrl = "https://s1.ax1x.com/2023/07/01/pCBg3Ie.jpg";
                    sessionStorage.setItem("avatar", "/static/default.jpg");
                    callback();
                }

            }).catch(err => {
                this.$message.error("获取头像时出错: " + err);
                callback();
            })
        }
        return {
            leftText: "Hi，",           //登陆页面左侧标语
            rightText: "很高兴见到你。",              //右侧标语
            avatarUrl: "https://s1.ax1x.com/2023/07/01/pCBg3Ie.jpg",
            loginForm: {
                account: '',
                password: "",
                loginTime: this.$utils.getNowFormatTime()
            },
            confirm_disabled: false,
            timer: "",
            loginRules: {
                account: [
                    {
                        required: true,
                        message: "账号不能为空",
                        trigger: "blur",
                    },
                    {
                        min: 2,
                        max: 20,
                        message: "账号长度为2-16位",
                        trigger: "blur"
                    }, {
                        trigger: "blur",
                        validator: getAvatarValidation
                    }
                ],
                password: [
                    {
                        required: true,
                        message: "密码不能为空",
                        trigger: "blur"
                    },
                    {
                        min: 6,
                        max: 16,
                        message: "密码长度为6-16位",
                        trigger: "blur"
                    }
                ]
            }
        }
    },
    methods: {
        // 登录确认
        loginSubmit() {
            this.$refs['loginForm'].validate((valid) => {
                if (valid) {
                    this.$axios({
                        method: "post",
                        url: "/user/login",
                        data: this.loginForm
                    }).then(res => {
                        if (res.data.code == "200") {
                            let data = res.data.data;
                            //保存本地信息，防止页面刷新导致信息丢失
                            sessionStorage.setItem("user", JSON.stringify(data.user));
                            sessionStorage.setItem("menu", JSON.stringify(data.menu));
                            this.$store.commit("setMenu", data.menu);
                            this.$message({
                                message: "登陆成功",
                                type: "success",
                                duration: 2500
                            });
                            this.confirm_disabled = true;
                            let permission = JSON.parse(sessionStorage.getItem("user")).permission;
                            if (permission === 0) {
                                this.$router.replace("/workbench");
                            }
                            if (permission === 1) {
                                this.$router.replace("/userManage");
                            }
                            this.timer = setTimeout(() => {              //登陆成功5s后显示消息通知
                                this.getMessages(data.user.uid);
                            }, 5000);

                        } else {
                            this.$message({
                                message: res.data.msg,
                                type: "error",
                                duration: 2500
                            });
                        }
                    }).catch(err => {
                        console.log("登陆时发生错误: " + err);
                    })
                }
            })
        },
        //获取消息通知
        getMessages(uid) {
            this.$axios({
                method: "get",
                url: `/messages/getUnread/${uid}`,
            }).then(res => {
                if (res.data.code == "200") {
                    let unread = Number(res.data.data);
                    if (unread != 0) {
                        this.$notify({
                            title: "消息通知",
                            message: "你有" + unread + "条新消息",
                            duration: 6000
                        })
                    }
                } else {
                    console.log("暂无消息通知");
                }
            })
        }
    }
}

</script>

<style scoped>
.avatarBox {
    margin-bottom: 5px;
}

.loginContainer {
    z-index: 999;
    border-radius: 15px;
    background-clip: padding-box;
    margin: 120px auto;
    width: 350px;
    padding: 25px 40px;
    background: #fff;
    border: 1px solid #eae;
    box-shadow: 0 0 25px #cac6c6;
    background: transparent;
}

.titleBox {
    /* border: 1px solid #000; */
    position: absolute;
    width: 100%;
    z-index: -1;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    display: flex;
}

.left {
    width: 60%;
    text-align: center;
}

.right {
    width: 40%;
    text-align: center;
}

.left,
.right {
    font-size: 3rem;
    font-weight: 700;
    background-image: linear-gradient(to right, #12c2e9, #c471ed, #f64f59);
    background-clip: text;
    color: transparent;

}
</style>