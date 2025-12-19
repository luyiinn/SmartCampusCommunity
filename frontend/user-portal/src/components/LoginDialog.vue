<template>
  <el-dialog
    v-model="dialogVisible"
    width="440px"
    :before-close="handleClose"
    custom-class="login-dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    center
    destroy-on-close
  >
    <!-- 登录表单头部装饰 -->
    <div class="login-header">
      <div class="login-icon">
        <img src="../assets/TCLogo.png" alt="logo" class="avatar-logo" />
      </div>
      <h2 class="login-title">欢迎回来</h2>
    </div>

    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      label-width="0"
      class="login-form"
    >
      <el-form-item prop="username" class="login-form-item">
        <!-- 简约输入框：移除左侧图标，统一宽度与交互效果 -->
        <div class="form-input-wrapper">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :validate-event="false"
            clearable
            class="no-label-input"
            @keyup.enter="handleLogin"
            :class="{ 'input-focused': focusedField === 'username' }"
            @focus="focusedField = 'username'"
            @blur="focusedField = ''"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon">
                <User />
              </el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <el-form-item prop="password" class="login-form-item">
        <!-- 简约输入框：移除左侧图标，保留密码可见性切换 -->
        <div class="form-input-wrapper">
          <el-input
            v-model="loginForm.password"
            placeholder="请输入密码"
            type="password"
            :validate-event="false"
            class="no-label-input"
            show-password
            @keyup.enter="handleLogin"
            :class="{
              'input-focused': focusedField === 'password',
              'password-toggle-animation': isAnimatingPassword,
            }"
            @focus="onPasswordFocus"
            @blur="focusedField = ''"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon">
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <!-- 记住密码和忘记密码选项 -->
      <div class="login-options">
        <el-checkbox v-model="rememberMe" class="remember-me"
          >记住密码</el-checkbox
        >
        <el-button type="text" class="forgot-password">忘记密码？</el-button>
      </div>

      <!-- 错误提示信息 -->
      <transition name="error-message-fade">
        <div v-if="errorMessage" class="error-message">
          <el-icon class="error-icon">
            <svg
              viewBox="0 0 1024 1024"
              width="1em"
              height="1em"
              fill="currentColor"
            >
              <path
                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm-32 664c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v48zm32-168c-26.5 0-48 21.5-48 48s21.5 48 48 48 48-21.5 48-48-21.5-48-48-48z"
              ></path>
            </svg>
          </el-icon>
          <span>{{ errorMessage }}</span>
        </div>
      </transition>

      <p class="login-subtitle">请输入您的账号信息</p>
      <!-- 登录按钮 -->
      <el-button
        type="primary"
        @click="handleLogin"
        :loading="isLoading"
        class="login-button"
        :disabled="isLoading"
        icon="View"
      >
        {{ isLoading ? "登录中..." : "登录" }}
      </el-button>

      <!-- 已移除：第三方登录入口，保持界面简洁一致 -->
    </el-form>

    <template #footer>
      <!-- 隐藏默认的页脚 -->
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, defineProps, defineEmits, computed } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import { useUserStore } from "../stores/userStore";
import axios from "axios";
import { useRouter } from "vue-router";

// 定义组件属性
const props = defineProps<{
  visible: boolean;
}>();

// 定义组件事件
const emit = defineEmits<{
  "update:visible": [value: boolean];
  "login-success": [userInfo: { username: string; id: number }];
}>();

// 对话框可见性状态（使用computed保持与props的同步）
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit("update:visible", val),
});

// 表单数据
const loginForm = reactive({
  username: "",
  password: "",
});

// 表单引用
const loginFormRef = ref();

// 记住密码状态
const rememberMe = ref(false);

// 加载状态
const isLoading = ref(false);

// 当前焦点字段
const focusedField = ref("");

// 错误信息
const errorMessage = ref("");

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 20,
      message: "用户名长度在 3 到 20 个字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于 6 个字符", trigger: "blur" },
  ],
};

const isAnimatingPassword = ref(false);
const onPasswordFocus = () => {
  focusedField.value = "password";
  isAnimatingPassword.value = true;
  setTimeout(() => {
    isAnimatingPassword.value = false;
  }, 300);
};

// 处理登录
const userStore = useUserStore();
const router = useRouter();

const handleLogin = async () => {
  try {
    // 验证表单
    await loginFormRef.value.validate();

    // 设置加载状态
    isLoading.value = true;
    errorMessage.value = "";
    const payload = {
      username: loginForm.username,
      password: loginForm.password,
    };
    const res = await axios.post("/user/login", payload, {
      headers: { "Content-Type": "application/json" },
    });
    const data = res.data.data as {
      id: number;
      userName: string;
      avatar?: string | null;
      token?: string;
    };
    console.log("data来自Login组件", data);

    userStore.loginSuccess({
      id: Number(data.id),
      userName: data.userName,
      avatar: data.avatar || "",
      token: data.token || "",
      remember: rememberMe.value,
    });
    // console.log("userStore.data来自Login组件", userStore.user);

    emit("login-success", { username: data.userName, id: Number(data.id) });
    dialogVisible.value = false;
    resetForm();
    const target = userStore.consumePendingRoute();
    if (target) router.push(target);
  } catch (error) {
    const anyErr = error as any;
    if (anyErr?.response?.data?.message)
      errorMessage.value = anyErr.response.data.message;
    else if (anyErr?.message) errorMessage.value = anyErr.message;
    else errorMessage.value = "网络错误";
  } finally {
    // 重置加载状态
    isLoading.value = false;
  }
};

// 处理对话框关闭
const handleClose = () => {
  // 重置表单
  resetForm();
  // 关闭对话框
  dialogVisible.value = false;
};

// 重置表单
const resetForm = () => {
  if (loginFormRef.value) {
    loginFormRef.value.resetFields();
  }
  errorMessage.value = "";
  focusedField.value = "";
};

// 从 Pinia 中恢复记住的用户信息
const initRememberedUser = () => {
  const remembered = userStore.rememberedUser;
  if (remembered) {
    loginForm.username = remembered.username;
    rememberMe.value = true;
  }
};

// 初始化
initRememberedUser();
</script>

<style scoped>
/* 登录对话框样式 */
.login-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  animation: dialogSlideIn 0.3s ease-out;
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-dialog :deep(.el-dialog__header) {
  display: none;
}

.login-dialog :deep(.el-dialog__body) {
  padding: 28px; /* 紧凑内容间距 */
  background: transparent;
}

.login-dialog :deep(.el-dialog__footer) {
  display: none;
}

/* 登录表单头部 */
.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-icon {
  width: 68px;
  height: 68px;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: none;
  transition: transform 0.3s ease;
}

.login-icon:hover {
  transform: scale(1.05);
}

.avatar-icon {
  font-size: 40px;
  color: white;
}

.avatar-logo {
  width: 54px;
  height: 54px;
  object-fit: contain;
}

.login-title {
  font-size: 20px;
  font-weight: 600;
  color: #0f766e;
  letter-spacing: 0.4px;
  line-height: 1.2;
  margin: 0 0 8px;
}

.login-title::after {
  content: "";
  display: block;
  width: 40px;
  height: 2px;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border-radius: 2px;
  margin: 6px auto 0;
}

.login-subtitle {
  text-align: center;
  font-size: 13px;
  color: #b9b9b9;
  letter-spacing: 0.2px;
  line-height: 1.6;
  margin: 0 0 10px;
}

/* 登录表单样式 */
.login-form {
  width: 100%;
}

.login-form-item {
  margin-bottom: 14px; /* 更紧凑的间距 */
}

/* 统一每个表单项内容宽度，确保输入框等宽 */
.login-form-item :deep(.el-form-item__content) {
  width: 100%;
}

.form-input-wrapper {
  position: relative;
  border-radius: 12px;
  background-color: #ffffff; /* 简约白底 */
  transition: all 0.2s ease;
  border: none; /* 去除外层边框 */
  width: 100%; /* 填满容器宽度 */
}

.form-input-wrapper:hover {
  background-color: #f7faf9; /* 悬停：浅绿协调，无边框 */
}

.form-input-wrapper:focus-within {
  background-color: #f0fdfa; /* 聚焦：浅绿色背景提示 */
}

/* 已移除输入框左侧图标相关样式 */

.no-label-input {
  padding: 0 16px !important; /* 统一左右内边距 */
  border: none !important;
  background: transparent !important;
  border-radius: 12px !important;
  font-size: 14px;
  height: 48px; /* 减小输入高度 */
  transition: all 0.2s ease;
  width: 100%; /* 输入组件全宽 */
}

/* 统一 Element Plus 输入内部宽度与边框（EP 使用 box-shadow 作为边框） */
.no-label-input :deep(.el-input__wrapper) {
  width: 100%;
  box-shadow: none; /* 去除内部伪边框 */
  background-color: transparent; /* 与外层白底协调 */
}

/* 聚焦时不出现边框，改用轻背景提示 */
.no-label-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: none;
  background-color: #f0fdfa;
}

.no-label-input:focus-within {
  box-shadow: none !important;
}

.no-label-input :deep(.el-input__prefix) {
  color: #6aa9a1;
  margin-right: 8px;
}

.input-prefix-icon {
  font-size: 18px;
}

.input-focused {
  font-weight: 500;
}

.password-toggle-animation {
  animation: passwordToggle 0.3s ease;
}

.password-toggle-animation :deep(.el-input__wrapper) {
  animation: passwordToggle 0.3s ease;
}

@keyframes passwordToggle {
  0% {
    transform: scaleX(1);
  }
  50% {
    transform: scaleX(1.05);
  }
  100% {
    transform: scaleX(1);
  }
}

/* 登录选项 */
.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-me {
  font-size: 14px;
  color: #6b7280;
}

.forgot-password {
  font-size: 14px;
  color: #4f46e5;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #4338ca;
  text-decoration: underline;
}

/* 错误信息样式 */
.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 8px;
  color: #dc2626;
  font-size: 14px;
  margin-bottom: 20px;
}

.error-icon {
  color: #dc2626;
  font-size: 15px;
}

.error-message-fade-enter-active,
.error-message-fade-leave-active {
  transition: all 0.3s ease;
}

.error-message-fade-enter-from,
.error-message-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 登录按钮样式 */
.login-button {
  width: 100%;
  height: 48px; /* 减小按钮高度 */
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(
    135deg,
    #00b09b 0%,
    #009688 100%
  ); /* 采用浅绿色主题 */
  border: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 150, 136, 0.25);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  background: linear-gradient(135deg, #b2dfdb 0%, #80cbc4 100%);
  cursor: not-allowed;
}

/* 响应式样式 */
@media (max-width: 640px) {
  .login-dialog {
    width: 96% !important;
    margin: 0 auto;
    border-radius: 12px;
  }

  .login-dialog :deep(.el-dialog__body) {
    padding: 20px; /* 移动端更紧凑 */
  }

  .login-icon {
    width: 56px;
    height: 56px;
  }

  .avatar-icon {
    font-size: 32px;
  }

  .avatar-logo {
    width: 42px;
    height: 42px;
  }

  .login-title {
    font-size: 17px;
  }

  /* 已移除社交登录相关响应式样式 */
}

@media (max-width: 480px) {
  .login-dialog :deep(.el-dialog__body) {
    padding: 20px;
  }

  .login-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
// 密码输入动画状态
