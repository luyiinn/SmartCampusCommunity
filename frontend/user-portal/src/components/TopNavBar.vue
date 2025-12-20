<template>
  <div class="nav-content">
    <div class="nav-left">
      <img src="../assets/TCLogo.png" alt="" width="100px" />
      <span>迹云校园社区</span>

      <el-menu
        :default-active="activeIndex"
        mode="horizontal"
        :ellipsis="false"
        @select="handleSelect"
        class="nav-menu"
        active-text-color="#009688"
      >
        <el-menu-item index="/square">广场</el-menu-item>
        <el-menu-item index="/profile">个人主页</el-menu-item>
        <el-menu-item index="/diary">日志</el-menu-item>
        <el-menu-item index="/about">关于</el-menu-item>
      </el-menu>
    </div>
    <div class="nav-right">
      <div v-if="user.id">
        <el-dropdown @command="handleCommand">
          <div class="user-info">
            <el-avatar
              :src="user.avatar || DEFAULT_AVATAR"
              :size="36"
              @error="handleAvatarError"
              :alt="'用户头像：' + user.userName"
            />
            <span class="username">{{ user.userName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon>
                  <User />
                </el-icon>
                个人主页
              </el-dropdown-item>
              <el-dropdown-item command="message">
                <el-icon>
                  <Message />
                </el-icon>
                消息
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon>
                  <Setting />
                </el-icon>
                设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon>
                  <SwitchButton />
                </el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div v-else class="auth-actions">
        <el-button
          size="small"
          class="register-btn"
          @click="showRegisterDialog = true"
          >注册</el-button
        >
        <el-button
          size="small"
          class="login-btn"
          @click="showLoginDialog = true"
          >登录</el-button
        >
      </div>
    </div>

    <!-- 登录对话框组件 -->
    <LoginDialog
      v-model:visible="showLoginDialog"
      @login-success="handleLoginSuccess"
    />

    <!-- 注册对话框组件 -->
    <RegisterDialog
      v-model:visible="showRegisterDialog"
      @register-success="handleRegisterSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ref, reactive } from "vue";
import { User, Message, Setting, SwitchButton } from "@element-plus/icons-vue";
import LoginDialog from "./LoginDialog.vue";
import RegisterDialog from "./RegisterDialog.vue";
import { useUserStore } from "../stores/userStore";

// 保持与userStore一致的接口命名
interface User {
  id: number | null;
  avatar: string;
  userName: string;
}
const userStore = useUserStore();

// 默认头像地址
const DEFAULT_AVATAR =
  "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";

const user = reactive<Partial<User>>({
  id: null,
  avatar: DEFAULT_AVATAR,
  userName: "",
});

// 同步 Pinia 的用户数据到本地展示状态（初始化 + 变化时）
watch(
  () => userStore.user,
  (val) => {
    user.id = val?.id ?? null;
    user.userName = val?.userName ?? "";
    // 确保头像为空时使用默认头像，同时处理URL前缀问题
    if (val?.avatar) {
      // 检查是否已经是完整URL
      if (
        val.avatar.startsWith("http://") ||
        val.avatar.startsWith("https://")
      ) {
        user.avatar = val.avatar;
      }
      // 优先处理/uploads开头的路径，确保直接使用而不添加/api前缀
      else if (val.avatar.startsWith("/uploads")) {
        user.avatar = val.avatar;
      }
      // 检查是否是uploads开头（没有前导斜杠）
      else if (val.avatar.startsWith("uploads")) {
        user.avatar = `/${val.avatar}`;
      }
      // 处理/api开头的路径
      else if (val.avatar.startsWith("/api")) {
        user.avatar = val.avatar;
      } else {
        // 对于其他情况，添加/api前缀并处理斜杠
        user.avatar = `/api${val.avatar.startsWith("/") ? "" : "/"}${
          val.avatar
        }`;
      }
    } else {
      user.avatar = DEFAULT_AVATAR;
    }
  },
  { deep: true, immediate: true }
);

// 处理头像加载失败
const handleAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  if (target) {
    // 使用项目内的error.png作为占位头像
    target.src = "/src/assets/placeholders/error.png";
    target.alt = "默认头像";
    console.error("头像加载失败，使用默认头像");
  }
};

const showLoginDialog = computed({
  get: () => userStore.authModalVisible,
  set: (v: boolean) => userStore.setAuthModalVisible(v),
});
const showRegisterDialog = ref(false);

const route = useRoute();
const router = useRouter();

defineOptions({
  name: "TopNavBar",
});

const activeIndex = computed(() => route.path);

const handleSelect = (key: string) => {
  router.push(key);
};

const handleCommand = (command: string) => {
  if (command === "logout") {
    userStore.logout();
    user.id = null;
    user.userName = "";
    try {
      localStorage.removeItem("user");
      localStorage.removeItem("user-store");
      localStorage.removeItem("token");
      localStorage.removeItem("rememberedUser");
    } catch {}
    ElMessage({ message: "已退出登录", type: "success", duration: 1500 });
    router.push("/square");
    // 刷新页面以确保状态完全重置
    window.location.reload();
  } else if (command === "profile") {
    // 跳转到个人主页
    router.push("/profile");
  } else {
    // 处理其他命令逻辑
    console.log(`执行命令: ${command}`);
  }
};

// 处理登录成功
const handleLoginSuccess = () => {
  // 更新用户状态
  user.id = userStore.user.id;
  user.userName = userStore.user.userName;

  // 处理头像URL
  if (userStore.user.avatar) {
    // 检查是否已经是完整URL
    if (
      userStore.user.avatar.startsWith("http://") ||
      userStore.user.avatar.startsWith("https://")
    ) {
      user.avatar = userStore.user.avatar;
    }
    // 优先处理/uploads开头的路径，确保直接使用而不添加/api前缀
    else if (userStore.user.avatar.startsWith("/uploads")) {
      user.avatar = userStore.user.avatar;
    }
    // 检查是否是uploads开头（没有前导斜杠）
    else if (userStore.user.avatar.startsWith("uploads")) {
      user.avatar = `/${userStore.user.avatar}`;
    }
    // 处理/api开头的路径
    else if (userStore.user.avatar.startsWith("/api")) {
      user.avatar = userStore.user.avatar;
    } else {
      // 对于其他情况，添加/api前缀并处理斜杠
      user.avatar = `/api${userStore.user.avatar.startsWith("/") ? "" : "/"}${
        userStore.user.avatar
      }`;
    }
  } else {
    user.avatar = DEFAULT_AVATAR;
  }

  // 可以在这里添加登录成功后的其他逻辑，如存储token等
  console.log("登录成功", userStore.user);

  // 显示成功提示
  ElMessage({
    message: `欢迎回来，${userStore.user.userName}！`,
    type: "success",
    duration: 2000,
  });

  // 如果需要，可以进行页面跳转
  // router.push('/dashboard');
};

// 处理注册成功
const handleRegisterSuccess = (_data: Record<string, any>) => {
  // 更新用户状态
  user.id = userStore.user.id;
  user.userName = userStore.user.userName;

  // 处理头像URL
  if (userStore.user.avatar) {
    // 检查是否已经是完整URL
    if (
      userStore.user.avatar.startsWith("http://") ||
      userStore.user.avatar.startsWith("https://")
    ) {
      user.avatar = userStore.user.avatar;
    }
    // 优先处理/uploads开头的路径，确保直接使用而不添加/api前缀
    else if (userStore.user.avatar.startsWith("/uploads")) {
      user.avatar = userStore.user.avatar;
    }
    // 检查是否是uploads开头（没有前导斜杠）
    else if (userStore.user.avatar.startsWith("uploads")) {
      user.avatar = `/${userStore.user.avatar}`;
    }
    // 处理/api开头的路径
    else if (userStore.user.avatar.startsWith("/api")) {
      user.avatar = userStore.user.avatar;
    } else {
      // 确保正确添加/api前缀并处理斜杠
      user.avatar = `/api${userStore.user.avatar.startsWith("/") ? "" : "/"}${
        userStore.user.avatar
      }`;
    }
  } else {
    user.avatar = DEFAULT_AVATAR;
  }

  // 可以在这里添加注册成功后的其他逻辑，如存储token等
  console.log("注册成功", userStore);

  // 显示成功提示
  ElMessage({
    message: `注册成功，欢迎 ${userStore.user.userName}！`,
    type: "success",
    duration: 2000,
  });

  // 如果需要，可以进行页面跳转
  // router.push('/dashboard');
};

// 导入Element Plus的Message组件
import { ElMessage } from "element-plus";
</script>

<style scoped>
/* 导航栏整体容器样式 */
.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  padding: 0 16%;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1000;
}

/* 左侧内容区域 */
.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* Logo和系统名称 */
.nav-left img {
  height: 40px;
  width: auto;
  object-fit: contain;
}

.nav-left span {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

/* 导航菜单样式 */
.nav-menu {
  border-bottom: none;
  margin-left: 32px;
}

.nav-menu .el-menu-item {
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  padding: 0 20px;
  height: 64px;
  line-height: 64px;
  transition: all 0.3s ease;
}

.nav-menu .el-menu-item:hover {
  color: #d7f0c9;
  background-color: #f3f4f6;
}

.nav-menu .el-menu-item.is-active {
  color: #d7f0c9;
  background-color: #d7f0c9;
  font-weight: 600;
  position: relative;
}

.nav-menu .el-menu-item.is-active::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #d7f0c9;
}

/* 右侧内容区域 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 搜索框样式 */
.search-input {
  width: 240px;
  transition: all 0.3s ease;
}

.search-input .el-input__wrapper {
  border-radius: 8px;
  transition: box-shadow 0.3s ease;
}

.search-input .el-input__wrapper:hover {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.search-input .el-input__wrapper:focus-within {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  border: none;
  outline: none;
}

.user-info:hover {
  background-color: #f3f4f6;
  border: none;
  outline: none;
}

/* 覆盖Element Plus dropdown组件的默认边框样式 */
:deep(.el-dropdown:hover) .user-info {
  border: none;
  outline: none;
}

:deep(.el-dropdown) {
  border: none;
  outline: none;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.auth-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.auth-actions :deep(.el-button) {
  font-size: 14px;
  padding: 6px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.auth-actions :deep(.register-btn) {
  background-color: transparent;
  color: #009688;
  border: 1px solid #009688;
}

.auth-actions :deep(.register-btn:hover) {
  background-color: #f0fdfa;
  transform: translateY(-1px);
}

.auth-actions :deep(.login-btn) {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.25);
}

.auth-actions :deep(.login-btn:hover) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-1px);
}

/* 下拉菜单样式优化 */
.el-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  padding: 4px 0;
}

.el-dropdown-menu__item {
  font-size: 14px;
  padding: 10px 20px;
  transition: background-color 0.2s ease;
}

.el-dropdown-menu__item:hover {
  background-color: #f3f4f6;
}

.el-dropdown-menu__item--divided {
  margin-top: 4px;
  padding-top: 4px;
  border-top: 1px solid #e5e7eb;
}

.el-dropdown-menu__item .el-icon {
  margin-right: 8px;
}

/* 图标样式 */
:deep(.el-icon) {
  font-size: 16px;
}
</style>