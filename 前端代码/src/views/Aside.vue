<template>
    <el-scrollbar>
        <el-menu active-text-color="#ffd04b" background-color="#545c64" class="el-menu-vertical-demo"
            :default-active="defaultActive" text-color="#fff" router>
            <!-- 根据权限生成菜单 -->
            <el-menu-item v-for="item in menu" :index="'/' + item.menuroute" :key="item.id">
                <template #title>
                    <el-icon>
                        <component :is="item.menuicon" />
                    </el-icon>
                    {{ item.menuname }}
                </template>
            </el-menu-item>
        </el-menu>
    </el-scrollbar>
</template>

<script>
import { mapState } from 'vuex';


export default {
    name: "Aside",
    computed: {
        ...mapState({
            menu: "menu"
        })
    },
    data() {
        return {
            defaultActive: ""           //菜单默认选中的选项，解决页面刷新时的bug
        }
    },
    mounted() {
        this.defaultActive = this.$route.path;
    },
    methods: {
    }
}

</script>
<style scoped>
.el-menu-vertical-demo {
    height: 100vh;
}

.badge{
    border: 1px solid #000;
}
</style>