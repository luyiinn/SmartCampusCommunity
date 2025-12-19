<template>
  <el-dialog v-model="visibleProxy" title="发布帖子" width="520px">
    <el-form
      label-width="80px"
      :model="formData"
      :rules="formRules"
      ref="formRef"
      class="publish-form"
    >
      <!-- 标题输入框 -->
      <el-form-item label="标题" prop="title" class="form-item">
        <el-input
          v-model="formData.title"
          placeholder="请输入帖子标题"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <!-- 标签选择框 -->
      <el-form-item label="标签" prop="tags" class="form-item">
        <el-select
          v-model="formData.tags"
          placeholder="请选择标签"
          multiple
          style="width: 100%"
        >
          <el-option
            v-for="tag in tagOptions"
            :key="tag.value"
            :label="tag.label"
            :value="tag.value"
          />
        </el-select>
      </el-form-item>

      <!-- 匿名发布选项 -->
      <el-form-item label="是否匿名" prop="anonymous" class="form-item">
        <el-radio-group v-model="formData.anonymous">
          <el-radio :label="false">否</el-radio>
          <el-radio :label="true">是</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 内容输入框 -->
      <el-form-item label="内容" prop="content" class="form-item">
        <el-input
          v-model="formData.content"
          type="textarea"
          rows="6"
          placeholder="说点什么..."
        />
      </el-form-item>

      <!-- 图片上传 -->
      <el-form-item label="图片" prop="images" class="form-item">
        <el-upload
          v-model:file-list="fileList"
          action=""
          :auto-upload="false"
          :on-change="handleImageChange"
          :before-upload="beforeImageUpload"
          list-type="picture-card"
          :limit="9"
          :on-exceed="handleExceed"
          :disabled="isSubmitting"
        >
          <el-icon><Plus /></el-icon>
          <template #file="{ file }">
            <div class="image-upload-item">
              <img :src="file.url" alt="图片预览" class="image-preview" />
              <div class="image-upload-mask">
                <el-icon
                  class="image-upload-icon"
                  @click.stop="handleRemove(file)"
                  ><Delete
                /></el-icon>
              </div>
            </div>
          </template>
        </el-upload>
        <div class="upload-hint">最多上传9张图片，单张不超过2MB</div>
        <!-- 图片上传错误信息 -->
        <transition name="error-message-fade">
          <div v-if="imageUploadError" class="image-upload-error">
            <el-icon class="error-icon"><CircleClose /></el-icon>
            <span>{{ imageUploadError }}</span>
          </div>
        </transition>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button
        type="primary"
        :loading="isSubmitting"
        :disabled="isSubmitting || !isFormValid"
        @click="handleSubmit"
        >发布</el-button
      >
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, reactive } from "vue";
import { useTagStore } from "../stores/tagStore";
import { useUserStore } from "../stores/userStore";
import axios from "axios";
import { ElMessage } from "element-plus";
import { Plus, Delete, CircleClose } from "@element-plus/icons-vue";

// 定义组件属性
const props = defineProps<{
  visible: boolean;
}>();

// 定义组件事件
const emit = defineEmits<{
  "update:visible": [boolean];
  submit: [{ title: string; tags: number[]; content: string }];
}>();

// 表单引用
const formRef = ref();

// 对话框可见性代理
const visibleProxy = computed({
  get: () => props.visible,
  set: (v) => emit("update:visible", v),
});

// 表单数据
const formData = reactive({
  title: "",
  tags: [] as number[], // 修改为数字数组，存储标签id
  content: "",
  anonymous: false,
  images: [] as string[], // 新增图片字段，存储图片路径
});

// 图片上传相关
const fileList = ref<Array<{ name: string; url: string; raw?: File }>>([]);
const isImageUploading = ref(false);
const imageUploadError = ref("");

// 使用tagStore获取标签选项
const tagStore = useTagStore();
const userStore = useUserStore();
// 修改标签选项计算属性，确保value是标签id
const tagOptions = computed(() => {
  return tagStore.tags
    .filter((tag) => tag.id !== 0) // 排除"全部"标签
    .map((tag) => ({
      label: tag.name,
      value: tag.id, // 使用标签id作为value
    }));
});

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: "请输入帖子标题", trigger: "blur" },
    {
      min: 2,
      max: 100,
      message: "标题长度在 2 到 100 个字符之间",
      trigger: "blur",
    },
  ],
  tags: [
    { required: true, message: "请至少选择一个标签", trigger: "change" },
    {
      validator: (_rule: any, value: number[], callback: Function) => {
        if (value.length > 6) {
          callback(new Error("最多只能选择 6 个标签"));
        } else {
          callback();
        }
      },
      trigger: "change",
    },
  ],
  anonymous: [
    {
      type: "boolean",
      message: "请选择发布方式",
      trigger: "change",
    },
  ],
  content: [
    { required: true, message: "请输入帖子内容", trigger: "blur" },
    { min: 5, message: "内容至少需要 5 个字符", trigger: "blur" },
  ],
};

// 提交状态
const isSubmitting = ref(false);

// 表单有效性计算
const isFormValid = computed(() => {
  return (
    formData.title.trim().length >= 2 &&
    formData.tags.length > 0 &&
    formData.tags.length <= 6 &&
    formData.content.trim().length >= 5
  );
});

// 验证图片文件
const beforeImageUpload = (file: File) => {
  // 验证文件类型
  const isValidType = file.type === "image/jpeg" || file.type === "image/png";
  if (!isValidType) {
    imageUploadError.value = "只支持 JPG 或 PNG 格式的图片";
    return false;
  }

  // 验证文件大小（2MB）
  const isValidSize = file.size / 1024 / 1024 < 2;
  if (!isValidSize) {
    imageUploadError.value = "图片大小不能超过 2MB";
    return false;
  }

  imageUploadError.value = "";
  return true;
};

// 处理图片选择
const handleImageChange = (_file: any, updatedFileList: any[]) => {
  // 清除之前的错误信息
  imageUploadError.value = "";

  // 更新fileList
  fileList.value = updatedFileList;
};

// 处理图片超出限制
const handleExceed = () => {
  ElMessage.warning("最多只能上传9张图片");
};

// 处理删除图片
const handleRemove = (file: any) => {
  const index = fileList.value.findIndex((f) => f.url === file.url);
  if (index !== -1) {
    fileList.value.splice(index, 1);
  }
};

// 上传单张图片
const uploadImage = async (file: File | undefined) => {
  if (!file) {
    throw new Error("文件不存在");
  }
  try {
    isImageUploading.value = true;
    imageUploadError.value = "";

    const formData = new FormData();
    formData.append("file", file);

    // 调用上传接口 - 使用正确的代理路径
    const response = await axios.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    // 从响应中提取URL并返回，确保正确处理/uploads路径
    if (response.data && response.data.url) {
      let url = response.data.url;

      // 处理URL格式，确保正确的/uploads路径格式
      if (url.startsWith("http://") || url.startsWith("https://")) {
        // 完整URL保持不变
        return url;
      } else if (url.startsWith("/uploads")) {
        // 已经是正确的/uploads开头路径
        return url;
      } else if (url.startsWith("uploads")) {
        // 添加前导斜杠
        return `/${url}`;
      } else {
        // 对于其他情况，确保以/uploads开头
        return url.includes("uploads")
          ? url.startsWith("/")
            ? url
            : `/${url}`
          : `/uploads/${url}`;
      }
    } else {
      throw new Error("上传成功但未返回图片URL");
    }
  } catch (error: any) {
    console.error("图片上传失败:", error);
    throw new Error(error?.response?.data?.message || "图片上传失败，请重试");
  } finally {
    isImageUploading.value = false;
  }
};

// 上传所有图片
const uploadAllImages = async () => {
  const uploadedImageUrls: string[] = [];

  // 过滤出需要上传的文件（只有raw属性存在的文件需要上传）
  const filesToUpload = fileList.value.filter((file) => file.raw);

  if (filesToUpload.length === 0) {
    return uploadedImageUrls;
  }

  try {
    for (const file of filesToUpload) {
      const imageUrl = await uploadImage(file.raw as File);
      uploadedImageUrls.push(imageUrl);
    }
    return uploadedImageUrls;
  } catch (error: any) {
    imageUploadError.value = error.message;
    throw error;
  }
};

// 处理取消
const handleCancel = () => {
  // 重置表单
  resetForm();
  // 关闭对话框
  visibleProxy.value = false;
};

// 处理提交
const handleSubmit = async () => {
  try {
    // 验证表单
    await formRef.value.validate();

    // 设置提交状态
    isSubmitting.value = true;

    // 上传图片
    const uploadedImages = await uploadAllImages();

    // 构建请求数据（按接口要求格式）
    const requestData = {
      title: formData.title.trim(),
      content: formData.content.trim(),
      isAnonymous: formData.anonymous ? 1 : 0,
      status: 1, // 状态硬编码为1
      tags: formData.tags, // 已经是数字数组，不需要再转换
      images: uploadedImages, // 添加图片路径数组
    };

    // 发送请求到/post/add接口
    const response = await axios.post("/post/add", requestData, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    // 处理成功响应
    if (response.data && response.data.code === 1) {
      console.log(response.data.data);
      ElMessage.success(response.data.data || "帖子发布成功");

      // 重置表单
      resetForm();

      // 关闭对话框
      visibleProxy.value = false;

      // 刷新页面
      window.location.reload();
    }
  } catch (error) {
    // 处理错误
    console.error("发布失败:", error);
    const anyError = error as any;
    // 处理401未授权错误
    if (anyError?.response?.status === 401) {
      ElMessage.error("请先登录");
      // 触发登录对话框显示
      userStore.setAuthModalVisible(true);
      // 关闭当前对话框
      visibleProxy.value = false;
    } else {
      ElMessage.error(anyError?.message || "发布失败，请重试");
    }
  } finally {
    // 重置提交状态
    isSubmitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  // 手动清空数据（确保彻底重置）
  formData.title = "";
  formData.tags = [];
  formData.content = "";
  formData.anonymous = false;
  formData.images = [];
  // 清空文件列表
  fileList.value = [];
  // 清空错误信息
  imageUploadError.value = "";
};
</script>

<style scoped>
.el-dialog__body {
  padding-top: 20px;
  padding-bottom: 15px;
}

.publish-form {
  width: 100%;
}

.form-item {
  margin-bottom: 20px;
}

/* 标签选择器的样式优化 */
:deep(.el-select__tags) {
  flex-wrap: wrap;
}

:deep(.el-tag) {
  margin-bottom: 5px;
}

/* 字数统计样式调整 */
:deep(.el-input__count) {
  bottom: 6px;
  font-size: 12px;
}

/* 图片上传样式 */
.image-upload-item {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-upload-item:hover .image-upload-mask {
  opacity: 1;
}

.image-upload-icon {
  color: #fff;
  font-size: 16px; /* 缩小删除图标大小 */
  cursor: pointer;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.image-upload-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 8px;
  color: #dc2626;
  font-size: 12px;
}

.error-icon {
  color: #dc2626;
  font-size: 14px;
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

/* 覆盖Element Plus的上传组件样式 - 使用更具体的选择器和更高优先级 */
:deep(.el-upload--picture-card .el-upload-dragger) {
  width: 80px !important; /* 缩小上传按钮尺寸 */
  height: 80px !important;
  margin: 0 8px 8px 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* 调整上传按钮的Plus图标大小 */
:deep(.el-upload--picture-card .el-icon) {
  font-size: 20px !important; /* 缩小图标大小 */
}

/* 确保上传按钮整体容器大小正确 */
:deep(.el-upload--picture-card) {
  width: 80px !important;
  height: 80px !important;
}

/* 缩小已上传图片项尺寸 */
:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 80px !important;
  height: 80px !important;
  margin: 0 8px 8px 0 !important;
}

/* 确保图片预览容器大小正确 */
:deep(.el-upload-list__item-thumbnail) {
  width: 80px !important;
  height: 80px !important;
}
</style>