<template>
  <div class="settings-page">
    <el-card
      class="settings-card"
      shadow="hover"
      :body-style="{ padding: '24px' }"
    >
      <template #header>
        <div class="card-header">
          <h2 class="card-title">个人设置</h2>
          <p class="card-desc">管理您的个人信息</p>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        label-position="top"
        label-width="100px"
        :rules="rules"
        class="settings-form"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            type="email"
          />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入手机号"
            type="tel"
            maxlength="11"
          />
        </el-form-item>

        <!-- 学号 -->
        <el-form-item label="学号" prop="studentId">
          <el-input
            v-model="formData.studentId"
            placeholder="请输入学号"
            type="number"
          />
        </el-form-item>

        <!-- 头像 -->
        <el-form-item label="头像">
          <div class="avatar-uploader-wrapper">
            <el-upload
              v-model:file-list="avatarFileList"
              class="avatar-uploader"
              :action="''"
              :before-upload="beforeAvatarUpload"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/jpeg,image/png"
              @change="handleAvatarChange"
            >
              <img
                v-if="formData.avatar"
                :src="getAvatarUrl(formData.avatar)"
                class="avatar"
                :alt="formData.username"
              />
              <div v-else class="avatar-placeholder">
                {{ formData.username?.charAt(0) || "用" }}
              </div>
              <div class="avatar-upload-overlay">
                <el-icon class="avatar-upload-icon"><Camera /></el-icon>
                <span class="avatar-upload-text">更换头像</span>
              </div>
            </el-upload>
            <div v-if="avatarUploadError" class="avatar-upload-error">
              {{ avatarUploadError }}
            </div>
          </div>
        </el-form-item>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button type="default" @click="resetForm">重置</el-button>
          <el-button type="primary" @click="saveSettings" :loading="loading"
            >保存设置</el-button
          >
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "../stores/userStore";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules, UploadFile } from "element-plus";
import { Camera } from "@element-plus/icons-vue";

const router = useRouter();
const userStore = useUserStore();
const formRef = ref<FormInstance | null>(null);
const loading = ref(false);
const avatarLoading = ref(false);

// 存储上次获取的用户信息，用于重置表单
const lastFetchedUserInfo = ref<any>(null);

// 用户信息表单数据
const formData = reactive({
  username: "",
  email: "",
  phone: "",
  studentId: undefined,
  avatar: "",
});

// 头像上传相关
const avatarFileList = ref<UploadFile[]>([]);
const avatarUploadError = ref<string>("");

// 表单验证规则
const rules = reactive<FormRules>({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 2,
      max: 20,
      message: "用户名长度在 2 到 20 个字符",
      trigger: "blur",
    },
  ],
  email: [
    {
      type: "email",
      message: "请输入有效的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入有效的手机号",
      trigger: ["blur", "change"],
    },
  ],
});

// 处理头像URL，确保正确格式
const getAvatarUrl = (avatarUrl?: string | null): string | undefined => {
  if (!avatarUrl) return undefined;

  // 检查是否已经是完整的URL
  if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) {
    return avatarUrl;
  }

  // 特殊处理匿名用户头像
  if (avatarUrl === "/anonymous-avatar.png") {
    return avatarUrl;
  }

  // 确保路径以/uploads开头
  if (avatarUrl.startsWith("/uploads")) {
    return avatarUrl;
  } else if (avatarUrl.startsWith("uploads")) {
    return `/${avatarUrl}`;
  }

  return `/uploads/${avatarUrl}`;
};

// 头像上传前验证
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

// 处理头像选择
const handleAvatarChange = (file: UploadFile) => {
  // 清除之前的错误信息
  avatarUploadError.value = "";

  // 如果有新文件，上传并更新头像
  if (file.status === "ready" && file.raw) {
    updateAvatar(file.raw);
  }
};

// 上传头像
const updateAvatar = async (file: File) => {
  try {
    avatarLoading.value = true;
    avatarUploadError.value = "";

    // 调用userStore中的上传方法
    const avatarUrl = await userStore.uploadImage(file);

    // 只更新表单中的头像信息
    formData.avatar = avatarUrl;

    ElMessage.success("头像上传成功");
  } catch (error: any) {
    console.error("头像上传失败:", error);
    avatarUploadError.value = error.message || "头像上传失败，请重试";
    ElMessage.error(avatarUploadError.value);
  } finally {
    avatarLoading.value = false;
    // 清空文件列表
    avatarFileList.value = [];
  }
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  // 使用上次获取的用户信息重新填充数据
  if (lastFetchedUserInfo.value) {
    Object.assign(formData, {
      username: lastFetchedUserInfo.value.username || "",
      email: lastFetchedUserInfo.value.email || "",
      phone: lastFetchedUserInfo.value.phone || "",
      studentId: lastFetchedUserInfo.value.studentId || undefined,
      avatar: lastFetchedUserInfo.value.avatar || "",
    });
  }
};

// 保存设置
const saveSettings = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    loading.value = true;

    // 准备更新数据
    const updateData = {
      userName: formData.username.trim(),
      email: formData.email?.trim() || undefined,
      phone: formData.phone?.trim() || undefined,
      studentId: formData.studentId || undefined,
      avatar: formData.avatar || undefined,
    };

    // 调用 userStore 中的更新方法
    const success = await userStore.updateUserInfo(updateData);

    if (success) {
      ElMessage({
        message: "保存成功",
        type: "success",
        duration: 1500,
      });

      // 更新 lastFetchedUserInfo 为当前表单数据，以便在重置时使用最新信息
      lastFetchedUserInfo.value = {
        ...lastFetchedUserInfo.value,
        ...updateData,
      };
      lastFetchedUserInfo.value.username = updateData.userName;

      // 触发页面刷新
      userStore.triggerRefresh();
    } else {
      ElMessage({
        message: "保存失败",
        type: "error",
        duration: 1500,
      });
    }
  } catch (error: any) {
    console.error("保存设置失败:", error);
    if (error instanceof Error) {
      ElMessage({
        message: `保存失败: ${error.message}`,
        type: "error",
        duration: 1500,
      });
    } else {
      ElMessage({
        message: "保存失败，请稍后重试",
        type: "error",
        duration: 1500,
      });
    }
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取最新用户信息
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage({
      message: "请先登录",
      type: "warning",
      duration: 1500,
    });
    router.push("/square");
    return;
  }

  try {
    // 获取最新用户信息
    const userData = await userStore.fetchUserInfo();
    if (userData) {
      // 保存到 lastFetchedUserInfo 用于重置表单
      lastFetchedUserInfo.value = userData;
      // 更新表单数据
      Object.assign(formData, {
        username: userData.username ?? "",
        email: userData.email ?? "",
        phone: userData.phone ?? "",
        studentId: userData.studentId ?? undefined,
        avatar: userData.avatar ?? "",
      });
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    ElMessage({
      message: "获取用户信息失败，请刷新页面重试",
      type: "error",
      duration: 1500,
    });
  }
});
</script>

<style scoped>
/* 设置页面容器样式 */
.settings-page {
  max-width: 800px;
  margin: 24px auto;
  padding: 0 16px;
}

/* 卡片样式 */
.settings-card {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.card-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

/* 表单样式 */
.settings-form {
  margin-top: 20px;
}

/* 表单字段容器 */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 表单标签 */
:deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  border-color: #3b82f6;
}

/* 头像上传样式 */
.avatar-uploader-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-uploader {
  position: relative;
  display: inline-block;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  border: 2px solid #e5e7eb;
  transition: all 0.2s ease;
}

.avatar-uploader:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
  color: #6b7280;
}

.avatar-upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.avatar-uploader:hover .avatar-upload-overlay {
  opacity: 1;
}

.avatar-upload-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.avatar-upload-text {
  font-size: 12px;
  font-weight: 500;
}

.avatar-upload-error {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 4px;
}

/* 表单操作按钮区域 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
}

/* 按钮样式 */
:deep(.el-button) {
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.25);
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-1px);
}

:deep(.el-button--default) {
  border: 1px solid #d1d5db;
  color: #374151;
}

:deep(.el-button--default:hover) {
  background-color: #f3f4f6;
  border-color: #9ca3af;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-page {
    padding: 0 12px;
    margin: 16px auto;
  }

  .settings-card {
    :body-style {
      padding: 16px;
    }
  }

  .card-title {
    font-size: 18px;
  }

  .form-actions {
    flex-direction: column;
  }

  :deep(.el-button) {
    width: 100%;
  }
}
</style>