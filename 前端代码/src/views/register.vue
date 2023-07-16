<template>
    <div class="wrapper">
        <div class="titleBox">
            <p class="left">
                {{ leftText }}
            </p>
            <p class="right">
                {{ rightText }}
            </p>
        </div>
        <el-form :rules="regRules" :model="regForm" labelPosition="top" ref="regForm" class="regContainer">
            <el-form-item prop="account" label="账号">
                <el-input type="text" auto-complete="false" v-model="regForm.account" placeholder="账号长度6-10位，由数字和字母组成" />
            </el-form-item>
            <el-form-item prop="username" label="用户名">
                <el-input type="text" auto-complete="false" v-model="regForm.username" placeholder="您的用户名"></el-input>
            </el-form-item>
            <el-form-item prop="password" label="密码">
                <el-input show-password v-model="regForm.password" placeholder="您的密码"></el-input>
            </el-form-item>
            <el-form-item prop="gender" label="性别">
                <el-radio-group v-model="regForm.gender">
                    <el-radio label="0" size="large" border>男</el-radio>
                    <el-radio label="1" size="large" border>女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item>
                <el-button style="width: 100%;letter-spacing: 10px;" type="primary" @click="RegSubmit"
                    :disabled="confirm_disabled">提交</el-button>
            </el-form-item>
            <el-form-item>
                <el-link type="primary" href="/#/login">已有账号?去登录</el-link>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>

export default {
    name: "register",
    data() {
        //账号长度检测
        const lengthvalidation = (rule, value, callback) => {
            let pat = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
            if (!pat.test(value)) {
                callback(new Error("账号格式不正确"));
            } else {
                callback();
            }
        };
        //账号唯一性检测
        const singlevalidation = (rule, value, callback) => {
            this.$axios({
                method: "get",
                url: "/user/check",
                params: {
                    account: value
                }
            }).then(res => {
                if (res.data.code == "200") {
                    callback();
                } else {
                    callback(new Error("用户名已被注册"));
                }
            }).catch(err => {
                console.log("请求用户唯一性检测时发生错误: " + err);
            })
        };
        return {
            leftText: "Hello，",           //注册页面左侧标语
            rightText: "欢迎注册。",              //右侧标语
            confirm_disabled: false,
            regForm: {
                account: "",      //账号要进行唯一性检测和格式检查，只能由字母和数字组成
                username: "",
                password: "",
                gender: "0",
                permission: "0",
                registerTime: this.$utils.getNowFormatTime(),
                loginTime: this.$utils.getNowFormatTime()
            },
            regRules: {
                account: [
                    {
                        required: true,
                        message: "账号不能为空",
                        trigger: "blur"
                    },
                    {
                        trigger: "blur",
                        validator: lengthvalidation
                    },
                    {
                        trigger: "blur",
                        validator: singlevalidation
                    }
                ],
                username: [
                    {
                        required: true,
                        message: "用户名不能为空",
                        trigger: "blur"
                    },
                    {
                        trigger: "blur",
                        min: 2,
                        max: 16,
                        message: "用户名长度为2-16位",
                    }
                ],
                password: [
                    {
                        required: true,
                        message: "密码不能为空",
                        trigger: "blur"
                    },
                    {
                        trigger: "blur",
                        min: 6,
                        max: 16,
                        message: "密码长度为6-16位"
                    }
                ],
            }
        }
    },
    methods: {
        RegSubmit() {
            this.$refs['regForm'].validate((valid) => {
                if (valid) {
                    this.confirm_disabled = true;
                    this.$axios({
                        method: "post",
                        url: "/user/register",
                        data: this.regForm
                    }).then(res => {
                        if (res.data.code == "200") {
                            this.$message({
                                message: "注册成功!",
                                type: "success",
                                duration: 2500
                            })
                            sessionStorage.setItem("user", JSON.stringify(this.regForm));
                            this.$router.replace("/login");
                        } else {
                            this.$message({
                                message: "注册失败",
                                type: "error",
                                duration: 2500
                            })
                        }
                    }).catch(err => {
                        console.log("用户注册提交数据时发生错误: " + err);
                    })
                }
            })
        }
    }
}

</script>
<style scoped>
.titleBox {
    position: absolute;
    width: 100%;
    z-index: -1;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    /* border: 1px solid #000; */
}

.left {
    width: 55%;
    /* border: 1px solid #000; */
    text-align: center;
}

.right {
    width: 45%;
    /* border: 1px solid #000; */
    text-align: center;
}

.left,
.right {
    font-size: 3rem;
    font-weight: 700;
    background-image: linear-gradient(to right, #59c173, #a17fe0, #5d26c1);
    background-clip: text;
    color: transparent;

}

.regContainer {
    border-radius: 15px;
    background-clip: padding-box;
    z-index: 999;
    margin: 120px auto;
    width: 350px;
    padding: 25px 40px 15px 30px;
    background: #fff;
    border: 1px solid #eae;
    box-shadow: 0 0 25px #cac6c6;
}
</style>