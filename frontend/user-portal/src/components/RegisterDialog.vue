<template>
  <el-dialog
    v-model="dialogVisible"
    width="440px"
    :before-close="handleClose"
    custom-class="register-dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    center
    destroy-on-close
  >
    <div class="register-header">
      <div class="register-icon">
        <img src="../assets/TCLogo.png" alt="logo" class="avatar-logo" />
      </div>
      <h2 class="register-title">注册新用户</h2>
    </div>

    <!-- 头像上传区域 -->
    <div class="avatar-upload-container">
      <el-upload
        class="avatar-uploader"
        action=""
        :show-file-list="false"
        :auto-upload="false"
        :on-change="handleAvatarChange"
        :before-upload="beforeAvatarUpload"
        :disabled="isAvatarUploading"
      >
        <img
          v-if="imageUrl"
          :src="imageUrl"
          class="avatar-preview"
          alt="用户头像"
        />
        <div
          v-else-if="isAvatarUploading"
          class="avatar-upload-placeholder uploading"
        >
          <el-icon class="avatar-upload-icon"><Loading /></el-icon>
          <div class="avatar-upload-text">上传中...</div>
        </div>
        <div v-else class="avatar-upload-placeholder">
          <el-icon class="avatar-upload-icon"><UploadFilled /></el-icon>
          <div class="avatar-upload-text">上传头像</div>
        </div>
      </el-upload>
      <div class="avatar-upload-hint">
        点击上传或更换头像 (JPG, PNG格式，最大2MB)
      </div>
      <!-- 头像上传错误信息 -->
      <transition name="error-message-fade">
        <div v-if="avatarUploadError" class="avatar-upload-error">
          <el-icon class="error-icon"><CircleClose /></el-icon>
          <span>{{ avatarUploadError }}</span>
        </div>
      </transition>
    </div>

    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      label-width="0"
      class="register-form"
    >
      <el-form-item prop="username" class="register-form-item">
        <div class="form-input-wrapper">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            clearable
            class="no-label-input"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon"><User /></el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <el-form-item prop="email" class="register-form-item">
        <div class="form-input-wrapper">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            clearable
            class="no-label-input"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon"><Message /></el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <el-form-item prop="phone" class="register-form-item">
        <div class="form-input-wrapper">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            clearable
            class="no-label-input"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon"><Cellphone /></el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <el-form-item prop="studentId" class="register-form-item">
        <div class="form-input-wrapper">
          <el-input
            v-model="registerForm.studentId"
            placeholder="请输入学号"
            clearable
            class="no-label-input"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon"><Tickets /></el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <el-form-item prop="password" class="register-form-item">
        <div class="form-input-wrapper">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            class="no-label-input"
            show-password
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <el-form-item prop="confirmPassword" class="register-form-item">
        <div class="form-input-wrapper">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            class="no-label-input"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon class="input-prefix-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </div>
      </el-form-item>

      <div class="register-gap"></div>
      <p class="register-subtitle">请填写以上信息完成注册</p>

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

      <el-button
        type="primary"
        class="register-button"
        :loading="isLoading"
        :disabled="isLoading"
        @click="handleRegister"
      >
        {{ isLoading ? "注册中..." : "注册" }}
      </el-button>
    </el-form>

    <template #footer></template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from "vue";
import axios from "axios";
import {
  User,
  Message,
  Cellphone,
  Tickets,
  Lock,
  UploadFilled,
  Loading,
  CircleClose,
} from "@element-plus/icons-vue";
const props = defineProps<{ visible: boolean }>();
const emit = defineEmits<{
  "update:visible": [boolean];
  "register-success": [Record<string, any>];
}>();
const dialogVisible = computed({
  get: () => props.visible,
  set: (v) => emit("update:visible", v),
});
const registerFormRef = ref();
const registerForm = reactive({
  username: "",
  email: "",
  phone: "",
  studentId: "",
  password: "",
  confirmPassword: "",
  avatar: "", // 新增头像字段
});

// 头像预览URL
const imageUrl = ref("");
const isAvatarUploading = ref(false);
const avatarUploadError = ref("");

// 验证头像文件
const beforeAvatarUpload = (file: File) => {
  // 验证文件类型
  const isValidType = file.type === "image/jpeg" || file.type === "image/png";
  if (!isValidType) {
    avatarUploadError.value = "只支持 JPG 或 PNG 格式的图片";
    return false;
  }

  // 验证文件大小（2MB）
  const isValidSize = file.size / 1024 / 1024 < 2;
  if (!isValidSize) {
    avatarUploadError.value = "图片大小不能超过 2MB";
    return false;
  }

  avatarUploadError.value = "";
  return true;
};

// 处理头像选择和上传
const handleAvatarChange = (file: any) => {
  // 清除之前的错误信息
  avatarUploadError.value = "";

  // 验证文件
  if (beforeAvatarUpload(file.raw)) {
    // 显示临时预览
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target?.result as string;
    };
    reader.readAsDataURL(file.raw);

    // 上传头像
    uploadAvatar(file.raw);
  }
};

// 上传头像文件到服务器
const uploadAvatar = async (file: File) => {
  try {
    isAvatarUploading.value = true;
    avatarUploadError.value = "";

    const formData = new FormData();
    formData.append("file", file);

    // 调用上传接口 - 使用正确的代理路径
    const response = await axios.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    // 从响应中提取URL并更新avatar字段，确保正确处理/uploads路径
    if (response.data && response.data.url) {
      let url = response.data.url;

      // 处理URL格式，确保正确的/uploads路径格式
      if (url.startsWith("http://") || url.startsWith("https://")) {
        // 完整URL保持不变
        registerForm.avatar = url;
      } else if (url.startsWith("/uploads")) {
        // 已经是正确的/uploads开头路径
        registerForm.avatar = url;
      } else if (url.startsWith("uploads")) {
        // 添加前导斜杠
        registerForm.avatar = `/${url}`;
      } else {
        // 对于其他情况，确保以/uploads开头
        registerForm.avatar = url.includes("uploads")
          ? url.startsWith("/")
            ? url
            : `/${url}`
          : `/uploads/${url}`;
      }

      console.log("头像上传成功，URL:", registerForm.avatar);
    } else {
      throw new Error("上传成功但未返回图片URL");
    }
  } catch (error: any) {
    console.error("头像上传失败:", error);
    avatarUploadError.value =
      error?.response?.data?.message || "头像上传失败，请重试";
    // 清空预览和avatar字段
    imageUrl.value = "";
    registerForm.avatar = "";
  } finally {
    isAvatarUploading.value = false;
  }
};

const isLoading = ref(false);
const errorMessage = ref("");

// 注册表单验证规则
const registerRules = {
  username: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 20, message: "账号长度在 3 到 20 个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "邮箱格式不正确", trigger: ["blur", "change"] },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "手机号格式不正确",
      trigger: ["blur", "change"],
    },
  ],
  studentId: [
    { required: true, message: "请输入学号", trigger: "blur" },
    { min: 10, max: 10, message: "学号长度必须为 10 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于 6 个字符", trigger: "blur" },
    {
      pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/,
      message: "密码必须包含字母和数字",
      trigger: ["blur", "change"],
    },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (_: any, value: string, callback: any) => {
        if (value !== registerForm.password)
          callback(new Error("两次密码输入不一致"));
        else callback();
      },
      trigger: ["blur", "change"],
    },
  ],
};
// 处理注册提交
const handleRegister = async () => {
  console.log("handleRegister called");
  try {
    await registerFormRef.value.validate();
    console.log("Validation passed");
    isLoading.value = true;
    errorMessage.value = "";
    const payload = {
      username: registerForm.username,
      email: registerForm.email,
      phone: registerForm.phone,
      studentId: registerForm.studentId,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      avatar: registerForm.avatar, // 包含头像字段
    };
    console.log("发送请求到: /api/user/regis with payload:", payload);
    const res = await axios.post(`/user/regis`, payload, {
      headers: { "Content-Type": "application/json" },
    });
    // console.log('注册成功:', res.data);
    emit("register-success", res.data);
    dialogVisible.value = false;
  } catch (err: any) {
    if (err?.response?.data?.message)
      errorMessage.value = err.response.data.message;
    else if (err?.message) errorMessage.value = err.message;
    else errorMessage.value = "网络错误";
  } finally {
    isLoading.value = false;
  }
};
// 处理关闭对话框
const handleClose = () => {
  dialogVisible.value = false;
};
</script>

<style scoped>
.register-dialog {
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

.register-dialog :deep(.el-dialog__header) {
  display: none;
}

.register-dialog :deep(.el-dialog__body) {
  padding: 28px;
  background: transparent;
}

.register-dialog :deep(.el-dialog__footer) {
  display: none;
}

.register-header {
  text-align: center;
  margin-bottom: 24px;
}

.register-icon {
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

.register-icon:hover {
  transform: scale(1.05);
}

.avatar-logo {
  width: 54px;
  height: 54px;
  object-fit: contain;
}

.register-title {
  font-size: 20px;
  font-weight: 600;
  color: #0f766e;
  letter-spacing: 0.4px;
  line-height: 1.2;
  margin: 0 0 8px;
}

.register-title::after {
  content: "";
  display: block;
  width: 40px;
  height: 2px;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border-radius: 2px;
  margin: 6px auto 0;
}

.register-subtitle {
  text-align: center;
  font-size: 13px;
  color: #b9b9b9;
  letter-spacing: 0.2px;
  line-height: 1.6;
  margin: 0 0 10px;
}

.register-form {
  width: 100%;
}

.register-form-item {
  margin-bottom: 14px; /* 更紧凑的间距 */
}

.register-form-item :deep(.el-form-item__content) {
  width: 100%;
}

.form-input-wrapper {
  position: relative;
  border-radius: 12px;
  background-color: #ffffff;
  transition: all 0.2s ease;
  border: none;
  width: 100%;
}

.form-input-wrapper:hover {
  background-color: #f7faf9;
}

.form-input-wrapper:focus-within {
  background-color: #f0fdfa;
}

.no-label-input {
  padding: 0 16px !important;
  border: none !important;
  background: transparent !important;
  border-radius: 12px !important;
  font-size: 14px;
  height: 48px; /* 减小输入高度 */
  transition: all 0.2s ease;
  width: 100%;
}

.no-label-input :deep(.el-input__wrapper) {
  width: 100%;
  box-shadow: none;
  background-color: transparent;
}

.no-label-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: none;
  background-color: #f0fdfa;
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

.register-gap {
  height: 40px; /* 更紧凑的间隙 */
}

.register-button {
  width: 100%;
  height: 54px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.register-button::before {
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

.register-button:hover::before {
  left: 100%;
}

.register-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 150, 136, 0.25);
}

.register-button:active:not(:disabled) {
  transform: translateY(0);
}

.register-button:disabled {
  background: linear-gradient(135deg, #b2dfdb 0%, #80cbc4 100%);
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .register-dialog {
    width: 96% !important;
    margin: 0 auto;
    border-radius: 12px;
  }

  .register-dialog :deep(.el-dialog__body) {
    padding: 20px;
  }

  .register-icon {
    width: 56px;
    height: 56px;
  }

  .avatar-logo {
    width: 42px;
    height: 42px;
  }

  .register-title {
    font-size: 17px;
  }
}

/* 头像上传相关样式 */
.avatar-upload-container {
  text-align: center;
  margin-bottom: 24px;
  position: relative;
}

.avatar-uploader {
  display: inline-block;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-uploader:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.avatar-uploader:deep(.el-upload) {
  width: 100%;
  height: 100%;
  margin: 0;
  border: none;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-uploader:deep(.el-upload:focus) {
  outline: none;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.avatar-uploader:hover .avatar-preview {
  transform: scale(1.05);
}

.avatar-upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0fdfa 0%, #e0f2fe 100%);
  border: 2px dashed #94a3b8;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-uploader:hover .avatar-upload-placeholder {
  background: linear-gradient(135deg, #ecfeff 0%, #f0f9ff 100%);
  border-color: #60a5fa;
}

.avatar-upload-placeholder.uploading {
  opacity: 0.8;
  cursor: not-allowed;
}

.avatar-upload-icon {
  font-size: 24px;
  color: #64748b;
  margin-bottom: 8px;
  transition: color 0.3s ease;
}

.avatar-uploader:hover .avatar-upload-icon {
  color: #3b82f6;
}

.avatar-upload-text {
  font-size: 12px;
  color: #64748b;
  margin: 0;
  line-height: 1.4;
}

.avatar-upload-hint {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 8px;
  line-height: 1.5;
}

.avatar-upload-error {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 8px;
  color: #dc2626;
  font-size: 12px;
  max-width: 300px;
  margin-left: auto;
  margin-right: auto;
}

.avatar-upload-error .error-icon {
  font-size: 14px;
  color: #dc2626;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .avatar-uploader {
    width: 80px;
    height: 80px;
  }

  .avatar-upload-icon {
    font-size: 20px;
  }

  .avatar-upload-text {
    font-size: 11px;
  }

  .avatar-upload-hint {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .register-dialog :deep(.el-dialog__body) {
    padding: 20px;
  }
}
</style>