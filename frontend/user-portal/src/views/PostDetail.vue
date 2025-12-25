<template>
  <div class="post-detail-page">
    <!-- 帖子详情内容 -->
    <div class="post-detail-container">
      <!-- 返回按钮 -->
      <div class="back-button-container">
        <el-button type="text" @click="goBack" class="back-button">
          <el-icon>
            <ArrowLeft />
          </el-icon>
          返回
        </el-button>
      </div>

      <!-- 帖子详情卡片 -->
      <div class="post-detail-card" v-if="postDetail">
        <!-- 用户信息区域 -->
        <div class="user-info">
          <el-avatar :src="getAvatarUrl(postDetail.avatar)" :size="48" class="user-avatar clickable-avatar"
            :icon="userIcon" @error="handleAvatarError" @click="navigateToUserProfile"></el-avatar>
          <div class="user-details">
            <h3 class="user-name clickable-name" @click="navigateToUserProfile">{{ getUserName(postDetail) }}</h3>
            <p class="post-time">{{ formatTime(postDetail.createdAt) }}</p>
          </div>
        </div>

        <!-- 帖子内容区域 -->
        <div class="post-content">
          <h1 class="post-title">{{ postDetail.title }}</h1>
          <div class="post-text">{{ postDetail.content }}</div>

          <!-- 图片展示区域 -->
          <div v-if="postDetail.images && postDetail.images.length > 0" class="post-images">
            <div v-for="(image, index) in postDetail.images" :key="index" class="post-image-container">
              <img :src="getAvatarUrl(image)" :alt="`图片 ${index + 1}`" class="post-image"
                @error="handleImageError($event, image)" @load="handleImageLoad($event, image)"
                @click="getAvatarUrl(image) && viewImage(getAvatarUrl(image)!)" />
            </div>
          </div>
        </div>

        <!-- 帖子标签区域 -->
        <div v-if="postDetail.tags && postDetail.tags.length > 0" class="post-tags">
          <el-tag v-for="(tag, index) in postDetail.tags" :key="index" size="large" effect="light">
            {{ tag }}
          </el-tag>
        </div>

        <!-- 帖子统计区域 -->
        <div class="post-stats">
          <div class="stat-item" @click.stop="toggleLike">
            <el-icon class="stat-icon" :class="{ liked: localLikeStatus.isLike === 1 }">
              <Star />
            </el-icon>
            <span class="stat-text" :class="{ 'liked-text': localLikeStatus.isLike === 1 }">
              {{ localLikeStatus.likeCount }}
            </span>
          </div>
          <div class="stat-item">
            <el-icon class="stat-icon">
              <View />
            </el-icon>
            <span class="stat-text">{{ postDetail.viewCount || 0 }}</span>
          </div>
          <div class="stat-item" @click.stop="toggleComments">
            <el-icon class="stat-icon">
              <ChatDotRound />
            </el-icon>
            <span class="stat-text">{{ postDetail.commentCount || 0 }}</span>
          </div>
        </div>

        <!-- 评论组件区域 -->
        <div class="comments-section">
          <!-- 评论标题 -->
          <div class="comments-header">
            <h2 class="comments-title">
              评论 ({{ postDetail.commentCount || 0 }})
            </h2>
          </div>

          <!-- 评论输入框 -->
          <div class="comment-input-area">
            <el-avatar :src="currentUserAvatar" :size="40" class="comment-input-avatar" :icon="userIcon"></el-avatar>
            <div class="comment-input-wrapper">
              <el-input v-model="commentText" type="textarea" placeholder="写下你的评论..." :rows="3" resize="none"
                class="comment-input"></el-input>
              <div class="comment-input-actions">
                <el-button size="small" @click="submitAnonymousComment" :disabled="!commentText.trim()"
                  style="margin-left: 8px; color: #ccc">
                  匿名发送
                </el-button>
                <el-button type="primary" size="small" @click="submitComment" :disabled="!commentText.trim()">
                  发送
                </el-button>
              </div>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <div v-if="comments.length === 0" class="no-comments">
              暂无评论，快来发表第一条评论吧！
            </div>
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <el-avatar :src="getAvatarUrl(comment.user.avatarUrl)" :size="40" class="comment-avatar"
                :icon="userIcon"></el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <div class="comment-user-wrapper">
                    <span class="comment-user">{{ comment.user.name }}</span>
                    <span v-if="comment.replyToUserName" class="comment-reply-to">
                      回复 {{ comment.replyToUserName }}
                    </span>
                  </div>
                  <span class="comment-time">{{
                    formatTime(comment.createTime)
                    }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <div class="comment-action" @click.stop="toggleCommentLike(comment)">
                    <el-icon class="comment-action-icon" :class="{ liked: comment.isLiked }">
                      <Star />
                    </el-icon>
                    <span class="comment-action-text" :class="{ 'liked-text': comment.isLiked }">
                      {{ comment.likeCount || 0 }}
                    </span>
                  </div>
                  <div class="comment-action" @click.stop="startReply(comment)">
                    <span class="comment-action-text">回复</span>
                  </div>
                </div>

                <!-- 回复输入框 -->
                <div v-if="replyingTo === comment.id" class="reply-input-area">
                  <el-input v-model="replyText" type="textarea" :placeholder="`回复 ${comment.user.name}...`" :rows="2"
                    resize="none" class="reply-input" :class="`reply-input-${comment.id}`"></el-input>
                  <div class="reply-input-actions">
                    <el-button size="small" @click.stop="cancelReply">
                      取消
                    </el-button>
                    <el-button type="primary" size="small" @click.stop="submitReply(comment)"
                      :disabled="!replyText.trim()">
                      发送回复
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-else-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>

      <!-- 加载失败 -->
      <div v-else class="error-container">
        <el-empty description="加载失败，请重试" />
        <el-button type="primary" @click="loadPostDetail">重新加载</el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted, watch, h } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  ElAvatar,
  ElIcon,
  ElTag,
  ElInput,
  ElButton,
  ElMessage,
  ElImage,
  ElMessageBox,
  ElSkeleton,
  ElEmpty,
} from "element-plus";
import {
  Star,
  View,
  ChatDotRound,
  UserFilled,
  ArrowLeft,
} from "@element-plus/icons-vue";
import axios from "axios";
import { useUserStore } from "../stores/userStore";

// 路由和用户状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 默认用户图标
const userIcon = computed(() => UserFilled);

// 跳转到用户主页
const navigateToUserProfile = () => {
  if (postDetail.value && postDetail.value.userId) {
    router.push({
      path: `/user/${postDetail.value.userId}`,
      query: {
        name: getUserName(postDetail.value),
        avatar: postDetail.value.avatar
      }
    });
  }
};

// 帖子详情数据
const postDetail = ref<any>(null);
const loading = ref(true);
const error = ref(false);

// 评论相关状态
const showComments = ref(true); // 详情页默认显示评论
const commentText = ref("");
const replyText = ref("");
const replyingTo = ref<number | null>(null);
const comments = ref<any[]>([]);

// 本地点赞状态
const localLikeStatus = ref({
  isLike: 0,
  likeCount: 0,
});

// 获取当前用户头像
const currentUserAvatar = computed(() => {
  return getAvatarUrl(userStore.user.avatar);
});

// 定义帖子详情接口
interface PostDetailVO {
  id: number;
  userId: number;
  userName: string;
  avatar: string;
  title: string;
  content: string;
  isAnonymous: number;
  status: number;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string | Date;
  tags: string[];
  isLike: number;
  images: string[];
}

// 定义评论数据接口 - 用于类型提示
// interface CommentData {
//   id: number;
//   user: {
//     id: number;
//     name: string;
//     avatarUrl?: string | null;
//   };
//   content: string;
//   createTime: string | Date;
//   likeCount?: number;
//   isLiked?: boolean;
//   isAnonymous?: boolean;
//   replyToUserId?: number;
//   replyToUserName?: string;
// }

// 格式化时间函数
const formatTime = (time: string | Date): string => {
  const date = typeof time === "string" ? new Date(time) : time;

  // 获取当前时间
  const now = new Date();
  // 计算时间差（毫秒）
  const diff = now.getTime() - date.getTime();

  // 转换为秒
  const seconds = Math.floor(diff / 1000);
  // 转换为分钟
  const minutes = Math.floor(seconds / 60);
  // 转换为小时
  const hours = Math.floor(minutes / 60);
  // 转换为天
  const days = Math.floor(hours / 24);

  // 根据时间差返回不同格式的时间
  if (days > 0) {
    // 超过一天显示具体日期
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
      2,
      "0"
    )}-${String(date.getDate()).padStart(2, "0")}`;
  } else if (hours > 0) {
    // 超过一小时显示小时数
    return `${hours}小时前`;
  } else if (minutes > 0) {
    // 超过一分钟显示分钟数
    return `${minutes}分钟前`;
  } else {
    // 少于一分钟显示"刚刚"
    return "刚刚";
  }
};

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

// 获取用户名（处理匿名情况）
const getUserName = (post: PostDetailVO): string => {
  return post.isAnonymous === 1 ? "匿名用户" : post.userName;
};

// 图片加载错误处理
const handleImageError = (event: Event, imageUrl: string) => {
  if (event.target instanceof HTMLImageElement) {
    const img = event.target;
    // 使用项目内的error.png作为占位图
    img.src = "/src/assets/placeholders/error.png";
    img.alt = "图片加载失败";
    console.error("图片加载失败:", imageUrl);
  }
};

// 图片加载成功处理
const handleImageLoad = (_event: Event, imageUrl: string) => {
  console.log("图片加载成功:", imageUrl);
};

// 头像加载错误处理
const handleAvatarError = (event: Event) => {
  if (event.target instanceof HTMLImageElement) {
    const img = event.target;
    // 使用项目内的error.png作为占位头像
    img.src = "/src/assets/placeholders/error.png";
    img.alt = "默认头像";
    console.error("头像加载失败，使用默认头像");
  }
};

// 查看大图功能
const viewImage = (imageUrl: string) => {
  ElMessageBox({
    title: "图片预览",
    message: h(
      "div",
      {
        style: {
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          padding: "10px",
          maxHeight: "85vh",
          overflow: "auto",
        },
      },
      [
        h(ElImage, {
          src: imageUrl,
          fit: "contain",
          style: {
            maxWidth: "100%",
            maxHeight: "80vh",
            minWidth: "300px",
            minHeight: "200px",
          },
          previewSrcList: [imageUrl],
        }),
      ]
    ),
    customClass: "image-preview-dialog",
    showConfirmButton: false,
    closeOnClickModal: true,
    closeOnPressEscape: true,
  }).catch(() => {
    // 捕获关闭事件，不做任何处理
  });
};

// 加载帖子详情
const loadPostDetail = async () => {
  loading.value = true;
  error.value = false;

  try {
    const postId = route.params.id;
    const response = await axios.get(`/post/detail/${postId}`, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    if (response.data.code === 1) {
      postDetail.value = response.data.data;
      // 更新本地点赞状态
      localLikeStatus.value = {
        isLike: postDetail.value.isLike || 0,
        likeCount: postDetail.value.likeCount || 0,
      };
      // 加载评论
      await loadComments();
    } else {
      ElMessage.error(response.data.message || "加载帖子失败");
      error.value = true;
    }
  } catch (err) {
    console.error("加载帖子详情失败:", err);
    ElMessage.error("网络错误，请稍后重试");
    error.value = true;
  } finally {
    loading.value = false;
  }
};

// 加载评论数据
const loadComments = async () => {
  try {
    const postId = route.params.id;
    const response = await axios.get(`/comment/list?postId=${postId}`, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    if (response.data.code === 1) {
      if (Array.isArray(response.data.data)) {
        comments.value = response.data.data.map((item: any) => {
          const isAnonymous = item.isAnonymous === 1;
          return {
            id: item.id,
            user: {
              id: isAnonymous ? -1 : item.userId,
              name: isAnonymous ? "匿名用户" : item.userName,
              avatarUrl: isAnonymous
                ? "/anonymous-avatar.png"
                : item.avatar || null,
            },
            content: item.content,
            createTime: item.createAt,
            likeCount: item.likeCount || 0,
            isLiked: item.isLike === 1,
            isAnonymous: isAnonymous,
            replyToUserId: item.replyUserId,
            replyToUserName: item.replyUserName,
          };
        });
      }
    } else {
      ElMessage.error(response.data.message || "加载评论失败");
    }
  } catch (err) {
    console.error("加载评论失败:", err);
    // 使用模拟数据作为备选
    comments.value = [
      {
        id: 1,
        user: {
          id: 2,
          name: "张三",
          avatarUrl: null,
        },
        content: "这是一条很棒的内容，感谢分享！",
        createTime: new Date(Date.now() - 3600000),
        likeCount: 5,
        isLiked: false,
      },
      {
        id: 2,
        user: {
          id: 3,
          name: "李四",
          avatarUrl: null,
        },
        content: "我也有同感，期待更多精彩内容！",
        createTime: new Date(Date.now() - 7200000),
        likeCount: 2,
        isLiked: true,
      },
    ];
  }
};

// 通用的评论/回复提交函数
const submitCommentOrReply = async (params: {
  content: string;
  isAnonymous: number;
  replyUserId: number | null;
  replyCommentId: number | null;
  successMessage: string;
  clearText: () => void;
}) => {
  if (!params.content.trim()) {
    return;
  }

  // 验证内容长度
  if (params.content.trim().length > 200) {
    ElMessage.warning("内容不能超过200字");
    return;
  }

  try {
    const commentDTO = {
      postId: postDetail.value.id,
      content: params.content.trim(),
      isAnonymous: params.isAnonymous,
      replyUserId: params.replyUserId,
      replyCommentId: params.replyCommentId,
    };

    const response = await axios.post("/comment/add", commentDTO, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    if (response.data.code === 1) {
      ElMessage.success(params.successMessage);
      await loadComments();
      params.clearText();

      // 更新评论数
      if (postDetail.value) {
        postDetail.value.commentCount =
          (postDetail.value.commentCount || 0) + 1;
      }
    } else {
      ElMessage.error(response.data.message || "操作失败");
    }
  } catch (err) {
    console.error("提交失败:", err);
    ElMessage.error((err as any).message || "提交失败，请重试");
  }
};

// 提交评论
const submitComment = async () => {
  await submitCommentOrReply({
    content: commentText.value,
    isAnonymous: 0,
    replyUserId: null,
    replyCommentId: null,
    successMessage: "评论成功",
    clearText: () => {
      commentText.value = "";
    },
  });
};

// 匿名提交评论
const submitAnonymousComment = async () => {
  await submitCommentOrReply({
    content: commentText.value,
    isAnonymous: 1,
    replyUserId: null,
    replyCommentId: null,
    successMessage: "匿名评论成功",
    clearText: () => {
      commentText.value = "";
    },
  });
};

// 切换评论点赞状态
const toggleCommentLike = async (comment: any) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再点赞");
    return;
  }

  const oldIsLiked = comment.isLiked;
  const oldLikeCount = comment.likeCount;
  const newIsLiked = !oldIsLiked;

  try {
    // 乐观更新UI
    comment.isLiked = newIsLiked;
    comment.likeCount = (comment.likeCount || 0) + (newIsLiked ? 1 : -1);

    const response = await axios.put(
      `/comment/like/${comment.id}`,
      {},
      {
        headers: {
          token: ` ${userStore.token}`,
        },
      }
    );

    if (response.data.code !== 1) {
      // 回滚本地状态
      comment.isLiked = oldIsLiked;
      comment.likeCount = oldLikeCount;
      ElMessage.error(response.data.message || "操作失败，请重试");
    }
  } catch (err) {
    console.error("评论点赞操作失败:", err);
    // 回滚本地状态
    comment.isLiked = oldIsLiked;
    comment.likeCount = oldLikeCount;
    ElMessage.error((err as any).message || "网络错误，请稍后重试");
  }
};

// 开始回复
const startReply = (comment: any) => {
  replyingTo.value = comment.id;
  // 聚焦回复输入框
  setTimeout(() => {
    const replyInput = document.querySelector(
      `.reply-input-${comment.id}`
    ) as HTMLTextAreaElement;
    if (replyInput) replyInput.focus();
  }, 100);
};

// 取消回复
const cancelReply = () => {
  replyingTo.value = null;
  replyText.value = "";
};

// 提交回复
const submitReply = async (comment: any) => {
  await submitCommentOrReply({
    content: replyText.value,
    isAnonymous: 0,
    replyUserId: comment.user.id,
    replyCommentId: comment.id,
    successMessage: "回复成功",
    clearText: () => {
      replyText.value = "";
      replyingTo.value = null;
    },
  });
};

// 切换点赞状态
const toggleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再点赞");
    return;
  }

  const oldIsLike = localLikeStatus.value.isLike;
  const oldLikeCount = localLikeStatus.value.likeCount;
  const newIsLike = oldIsLike === 1 ? 0 : 1;

  try {
    // 乐观更新UI
    localLikeStatus.value.isLike = newIsLike;
    localLikeStatus.value.likeCount = oldLikeCount + (newIsLike === 1 ? 1 : -1);

    const response = await axios.put(
      `/post/like/${postDetail.value.id}`,
      {},
      {
        headers: {
          token: ` ${userStore.token}`,
        },
      }
    );

    if (response.data.code !== 1) {
      // 回滚本地状态
      localLikeStatus.value.isLike = oldIsLike;
      localLikeStatus.value.likeCount = oldLikeCount;
      ElMessage.error(response.data.message || "操作失败，请重试");
    }
  } catch (err) {
    console.error("点赞操作失败:", err);
    // 回滚本地状态
    localLikeStatus.value.isLike = oldIsLike;
    localLikeStatus.value.likeCount = oldLikeCount;
    ElMessage.error((err as any).message || "网络错误，请稍后重试");
  }
};

// 切换评论区域显示/隐藏
const toggleComments = () => {
  showComments.value = !showComments.value;
  if (showComments.value && comments.value.length === 0) {
    loadComments();
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 监听路由参数变化，重新加载帖子详情
watch(
  () => route.params.id,
  () => {
    loadPostDetail();
  }
);

// 组件挂载时加载数据
onMounted(() => {
  loadPostDetail();
});
</script>

<style scoped>
.post-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-button-container {
  margin-bottom: 20px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #009688;
  font-size: 14px;
}

.post-detail-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 用户信息区域样式 */
.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-avatar {
  border-radius: 50%;
  margin-right: 16px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #1f2937;
}

.post-time {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

/* 帖子内容区域样式 */
.post-content {
  margin-bottom: 24px;
}

.post-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.post-text {
  font-size: 16px;
  color: #4b5563;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 20px;
}

/* 图片展示区域样式 */
.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 20px;
}

.post-image-container {
  position: relative;
  width: calc(50% - 6px);
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-image:hover {
  transform: scale(1.05);
}

/* 帖子标签区域样式 */
.post-tags {
  margin-bottom: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.post-tags .el-tag {
  background-color: #f0fdf4;
  border-color: #dcfce7;
  color: #166534;
}

/* 帖子统计区域样式 */
.post-stats {
  display: flex;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 0;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-right: 32px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.stat-item:hover {
  color: #009688;
}

.stat-icon {
  margin-right: 6px;
  font-size: 18px;
  transition: color 0.3s ease;
  color: #6b7280;
}

.stat-item .stat-icon.liked {
  color: #ff4d4f !important;
}

.stat-text {
  font-size: 16px;
  color: #6b7280;
}

.stat-item .stat-text.liked-text {
  color: #ff4d4f !important;
}

/* 评论区域样式 */
.comments-section {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.comments-header {
  margin-bottom: 20px;
}

.comments-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

/* 评论输入区域样式 */
.comment-input-area {
  display: flex;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.comment-input-avatar {
  margin-right: 16px;
  flex-shrink: 0;
}

.comment-input-wrapper {
  flex: 1;
}

.comment-input {
  margin-bottom: 12px;
}

.comment-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  resize: none;
  border-color: #e5e7eb;
  transition: border-color 0.3s ease;
  min-height: 80px;
}

.comment-input :deep(.el-textarea__inner:focus) {
  border-color: #009688;
  box-shadow: 0 0 0 2px rgba(0, 150, 136, 0.1);
}

.comment-input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.comment-input-actions :deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.comment-input-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
}

.comment-input-actions :deep(.el-button--primary:hover:not(:disabled)) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-1px);
}

.comment-input-actions :deep(.el-button:disabled) {
  background: #e0e0e0;
  color: #999;
}

/* 评论列表样式 */
.comments-list {
  margin-bottom: 24px;
}

.no-comments {
  text-align: center;
  color: #9ca3af;
  padding: 40px 0;
  font-size: 16px;
}

.comment-item {
  display: flex;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.comment-avatar {
  margin-right: 16px;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-user {
  font-size: 15px;
  font-weight: 500;
  color: #1f2937;
}

.comment-reply-to {
  font-size: 13px;
  color: #9ca3af;
}

.comment-time {
  font-size: 13px;
  color: #9ca3af;
}

.comment-text {
  font-size: 15px;
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 12px;
  word-break: break-word;
}

/* 评论操作样式 */
.comment-actions {
  display: flex;
  gap: 24px;
}

.comment-action {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: color 0.3s ease;
  font-size: 14px;
  color: #9ca3af;
}

.comment-action:hover {
  color: #009688;
}

.comment-action-icon {
  margin-right: 6px;
  font-size: 14px;
}

.comment-action-text {
  font-size: 14px;
}

/* 评论点赞状态样式 */
.comment-action .comment-action-icon.liked {
  color: #ff4d4f !important;
}

.comment-action .comment-action-text.liked-text {
  color: #ff4d4f !important;
}

/* 回复输入框样式 */
.reply-input-area {
  margin-top: 16px;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
  animation: fadeIn 0.2s ease;
}

.reply-input {
  margin-bottom: 12px;
}

.reply-input :deep(.el-textarea__inner) {
  border-radius: 6px;
  resize: none;
  border-color: #e5e7eb;
  background-color: #ffffff;
}

.reply-input :deep(.el-textarea__inner:focus) {
  border-color: #009688;
  box-shadow: 0 0 0 2px rgba(0, 150, 136, 0.1);
}

.reply-input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.reply-input-actions :deep(.el-button) {
  border-radius: 6px;
  font-size: 14px;
}

.reply-input-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
}

.reply-input-actions :deep(.el-button--primary:hover:not(:disabled)) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
}

/* 加载和错误状态 */
.loading-container,
.error-container {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-detail-container {
    padding: 16px;
  }

  .post-detail-card {
    padding: 16px;
  }

  .post-title {
    font-size: 24px;
  }

  .post-image-container {
    width: calc(50% - 6px);
  }

  .stat-item {
    margin-right: 24px;
  }

  .comment-input-area,
  .comment-item {
    flex-direction: column;
  }

  .comment-input-avatar,
  .comment-avatar {
    margin-right: 0;
    margin-bottom: 12px;
    align-self: flex-start;
  }
}

@media (max-width: 480px) {
  .post-title {
    font-size: 20px;
  }

  .post-text {
    font-size: 15px;
  }

  .post-image-container {
    width: 100%;
  }

  .post-stats {
    justify-content: space-around;
  }

  .stat-item {
    margin-right: 0;
  }
}

.clickable-avatar {
  cursor: pointer;
  transition: opacity 0.2s;
}

.clickable-avatar:hover {
  opacity: 0.8;
}

.clickable-name {
  cursor: pointer;
  transition: color 0.2s;
}

.clickable-name:hover {
  color: #009688;
  text-decoration: underline;
}
</style>
