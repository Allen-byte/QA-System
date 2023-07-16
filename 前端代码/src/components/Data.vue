<template>
    <div class="wrapper">
        <el-card class="questions">
            <template #header>
                <div class="card-header">
                    <h3>已发布</h3>
                    <count-to :start-val="startVal" :end-val="totalQ" :duration="1500" class="count-number" />
                </div>
            </template>
            <p v-if="emptyShowQ" style="color: #909399;font-style: italic;">暂无提问信息,快去提问吧</p>
            <div class="contentQ" ref="contentQ" style="width: 100%;height: 400px;">

            </div>
        </el-card>
        <el-card class="answers">
            <template #header>
                <div class="card-header">
                    <h3>已回答</h3>
                    <count-to :start-val="startVal" :end-val="totalA" :duration="1500" class="count-number" />
                </div>
            </template>
            <p v-if="emptyShowA" style="color: #909399;font-style: italic;">暂无回答信息,快去回答提问吧</p>
            <div class="contentA" ref="contentA" style="width: 100%;height: 400px;">
                
            </div>
        </el-card>
    </div>
</template>

<script>
import { CountTo } from "vue3-count-to";

export default {
    name: "Data",
    components: {
        CountTo
    },
    props: {
        uid: Number
    },
    data() {
        // 以下：带Q表示提问相关的数据，带A表示回答相关的数据
        return {
            startVal: 0,
            totalQ: 0,
            totalA: 0,
            chartQ: "",
            chartA: "",
            emptyShowQ: true,         //空状态展示
            emptyShowA: true,
        }
    },
    mounted() {
        this.initCharts();
    },
    methods: {
        //绘制左侧提问图表
        drawQuestions(chart, dates, counts, year) {
            chart.setOption({
                title: {
                    text: `最近提问(${year}年)`,
                    subtext: "",        //副标题
                    x: "center",           //x轴对齐方式
                    y: "bottom"
                },
                tooltip: {
                    show: true,
                    trigger: "axis",
                    axisPointer: {
                        type: "line"
                    }
                },
                xAxis: {
                    type: "category",
                    data: dates,     //x轴数据
                    name: "日期",
                    //x轴标题样式
                    // nameTextStyle: {
                    //     fontWeight: 600,
                    //     fontSize: 18
                    // }
                },
                yAxis: {
                    type: "value",
                    name: "提问次数"
                },
                //图注设置
                legend: {
                    orient: "vertical",
                    x: "right",
                    y: "top",
                    data: ["提问次数"]
                },
                series: [
                    {
                        name: "提问情况",
                        data: counts,
                        type: "line",
                        smooth: true,
                        color: "#529b2e",
                        itemStyle : { normal: {label : {show: true}}},   //在每个折点上方显示数值
                    }
                ]
            })
        },
        //绘制右侧回答图表
        drawAnswers(chart, dates, counts, year){
            chart.setOption({
                title: {
                    text: `最近回答(${year}年)`,
                    subtext: "",        //副标题
                    x: "center",           //x轴对齐方式
                    y: "bottom"
                },
                tooltip: {
                    show: true,
                    trigger: "axis",
                    axisPointer: {
                        type: "line"
                    }
                },
                xAxis: {
                    type: "category",
                    data: dates,     //x轴数据
                    name: "日期",
                },
                yAxis: {
                    type: "value",
                    name: "回答次数"
                },
                //图注设置
                legend: {
                    orient: "vertical",
                    x: "right",
                    y: "top",
                    data: ["回答次数"]
                },
                series: [
                    {
                        name: "回答情况",
                        data: counts,
                        type: "line",
                        smooth: true,
                        color: "#73767a",
                        itemStyle : { normal: {label : {show: true}}},   //在每个折点上方显示数值
                    }
                ]
            })
        },
        initCharts() {
            //初始化左侧提问容器实例
            this.chartQ = this.$echarts.init(this.$refs['contentQ']);
            //初始化右侧回答容器实例
            this.chartA = this.$echarts.init(this.$refs['contentA']);
            //请求获取问题信息
            this.$axios({
                method: "post",
                url: "/questions/getQuestionsInfo",
                data: {
                    "uid": this.uid
                }
            }).then(res => {
                if (res.data.code == "200") {
                    let data = res.data.data;
                    this.totalQ = data.total;
                    this.drawQuestions(this.chartQ, data.dates, data.counts, data.year);
                    this.emptyShowQ = false;
                }else{
                    this.emptyShowQ = true;
                }
            }).catch(err => {
                this.$message.error("[Status Code: 500]后台服务出错");
                console.log("获取图表数据时出错：" + err);
            })
            //请求获取回答信息
            this.$axios({
                method: "post",
                url: "/answers/getAnswersInfo",
                data: {
                    "uid": this.uid
                }
            }).then(res => {
                if (res.data.code == "200") {
                    let data = res.data.data;
                    this.totalA = data.total;
                    this.drawAnswers(this.chartA, data.dates, data.counts, data.year);
                    this.emptyShowA = false;
                }else{
                    this.emptyShowA = true;
                }
            }).catch(err => {
                this.$message.error("[Status Code: 500]后台服务出错");
                console.log("获取图表数据时出错：" + err);
            })
        }
    }
}

</script>
<style scoped>
.wrapper {
    display: flex;
    justify-content: space-between;
}

.card-header {
    display: flex;
    justify-content: space-between;
}

.card-header span {
    font-size: 4rem;
    font-weight: 600;
    display: inline-block;
}

.el-card.questions::v-deep .el-card__header {
    background-color: #c6e2ff;
}

.el-card.answers::v-deep .el-card__header {
    background-color: #fab6b6;
}

.el-card {
    width: 48%;
}
</style>