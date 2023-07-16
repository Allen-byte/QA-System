<template>
    <div class="editorBox">
        <el-input class="title" v-model="title" placeholder="叙述清楚的标题更容易被别人回答（必填）"
            style="height: 60px; font-size: 20px;font-weight: 600;" maxlength="100" show-word-limit />
            <MdEditor v-model="content" :toolbars="toolbars" :align="align" placeholder="请简单描述您的问题,右侧为预览区（选填）" @onUploadImg="upload"/>
    </div>
    <div class="btnGroup">
        <el-button type="primary" class="publishBtn" @click="publish">发布</el-button>
        <el-button type="danger" class="cancelBtn" @click="cancel">取消</el-button>
    </div>
</template>

<script>
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

export default {
    name: "publish",
    components: {
        MdEditor
    },
    data() {
        return {
            width: "450px",             //发布的图片宽度
            align: "left",
            toolbars: ['italic', 'bold', 'underline', '-', 'quote', 'unorderedList', 'orderedList', '-', 'code', 'codeRow', 'link', 'image', '-', 'revoke', 'next', '=', 'pageFullscreen', 'preview'],
            title: "",         //标题
            posedtime: "",    //提交时间
            content: "",      //问题描述
            dest: [],          //图片路径数组（可能有多个），如果用户取消发布则根据该路径删除
        }
    },
    methods: {
        //上传图片
        upload(files){
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
                    let url = this.$server + res.data.data.showUrl;
                    this.dest.push(res.data.data.dest);
                    this.content += `<img src='${url}' width=${this.width}>`;
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(err => {
                console.log("上传图片时发生错误: " + err);
            })
        },
        //添加问题
        publish() {
            if (this.title && this.content) {
                this.posedtime = this.$utils.getNowFormatTime();
                this.dest = [];                       //清空待删除列表
                this.$axios({
                    method: "post",
                    url: "/questions/add",
                    data: {
                        "uid": JSON.parse(sessionStorage.getItem("user")).uid,
                        "title": this.title,
                        "description": this.content,
                        "posedtime": this.posedtime,
                        "poseDate": this.posedtime.split(" ")[0],
                    }
                }).then(res => {
                    if (res.data.code == "200") {
                        this.$message({
                            message: "发布成功!",
                            type: "success",
                            duration: 2000
                        });
                        this.title = "";
                        this.content = "";
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).catch(err => {
                    console.log(err);
                })
            } else {
                this.$message.error("标题或问题描述不能为空！")
            }
        },
        //删除用户选中的图片（在用户取消上传时）
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
            this.title = "";
            this.content = "";
            if (this.dest.length != 0) {
                this.delImages();
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
.editorBox {
    height: fit-content;
}

.tooltip {
    cursor: pointer;
    text-align: left;
}

.btnGroup {
    margin-top: 40px;
    display: flex;
    justify-content: center;
}

.publishBtn,
.cancelBtn {
    margin: 0 40px;
    width: 15%;
    font-weight: 600;
    font-size: 16px;
    letter-spacing: 2px;
}

.title {
    margin-bottom: 20px;
}
</style>