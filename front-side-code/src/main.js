import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import ElementPlus from 'element-plus';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import { utils } from '@/utils/index.js'     //导入自定义工具方法
import echarts from "echarts";               //导入echarts绘图 




// 注册全部图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


const app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

let server = "http://101.43.183.236:9001";
axios.defaults.baseURL = server; 
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$utils = utils;
app.config.globalProperties.$echarts = echarts;      //挂载全局
app.config.globalProperties.$server = server;    //后端服务地址 

app.use(store);
app.use(router);
app.use(ElementPlus, {
    locale: zhCn,
})

app.mount("#app");

