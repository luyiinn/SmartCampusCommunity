import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/userStore";

// 定义路由配置
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/square",
  },
  {
    path: "/square",
    component: () => import("../views/Square.vue"),
    meta: { title: "广场" },
  },
  {
    path: "/diary",
    component: () => import("../views/DiaryPage.vue"),
    meta: { title: "日记" },
  },
  {
    path: "/profile",
    component: () => import("../views/UserProfile.vue"),
    meta: { title: "个人主页", requiresAuth: true },
  },
  {
    path: "/user/:id",
    component: () => import("../views/UserProfile.vue"),
    meta: { title: "用户主页" },
  },
  {
    path: "/about",
    component: () => import("../views/About.vue"),
    meta: { title: "关于" },
  },
  {
    path: "/post/:id",
    component: () => import("../views/PostDetail.vue"),
    meta: { title: "帖子详情" },
  },
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, _from, next) => {
  if (to.meta && (to.meta as any).title) {
    document.title = (to.meta as any).title as string;
  }

  const whiteList = ["/square", "/about"];
  const userStore = useUserStore();
  const isLoggedIn = userStore.loggedIn || !!userStore.user.id;

  if (isLoggedIn) {
    next();
    return;
  }

  if (whiteList.includes(to.path)) {
    next();
    return;
  }

  // 允许访问动态路由 /user/:id 和 /post/:id 以及 /diary
  if (
    to.path.startsWith("/user/") ||
    to.path.startsWith("/post/") ||
    to.path === "/diary"
  ) {
    next();
    return;
  }

  ElMessage({ message: "请登录", type: "warning", duration: 1500 });
  userStore.requireLogin(to.fullPath);
  next("/square");
});

export default router;
