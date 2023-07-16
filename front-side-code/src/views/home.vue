<template>
    <el-container>
        <el-aside width="200px">
            <Aside></Aside>
        </el-aside>

        <el-container>
            <el-header style="background-color: #CDD0D6;display: flex;justify-content: space-between;">
                <Header></Header>
            </el-header>
            <el-main style="height: 90vh;">
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
import Header from "./Header.vue"
import Aside from "./Aside.vue"


export default {
    name: "home",
    components: {
        Header,
        Aside
    },
    methods: {
        
    }
}

</script>
<style scoped></style>