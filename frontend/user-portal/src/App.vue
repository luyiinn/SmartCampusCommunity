<template>
  <div id="app">
    <div class="common-layout">
      <el-container>
        <el-header style="padding: 0;">
          <!-- 顶部导航栏组件 -->
          <TopNavBar />
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue';
import { useRouter } from 'vue-router';
import TopNavBar from "./components/TopNavBar.vue";
import { useUserStore } from "./stores/userStore";

const userStore = useUserStore();
const router = useRouter();

// 监听用户刷新标志，当用户登录或退出登录时触发页面刷新
watch(
  () => userStore.refreshFlag,
  () => {
    console.log('检测到用户状态变化，刷新当前页面数据');
    // 获取当前路由
    const currentRoute = router.currentRoute.value;
    // 重新加载当前路由对应的组件
    router.replace({
      path: currentRoute.path,
      query: { ...currentRoute.query, _t: Date.now().toString() },
    });
  }
);
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
}
</style>

<style scoped>
/* 组件特定样式 */
#app {
  height: 100%;
  margin: 0;
  padding: 0;
  background-color: #efefef;
}
</style>
