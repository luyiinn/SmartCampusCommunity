<template>
  <div class="post-card" @click="handleCardClick">
    <!-- 用户信息区域 -->
    <div class="user-info">
      <el-avatar :src="getAvatarUrl(post.user.avatarUrl)" :size="40" class="user-avatar clickable-avatar"
        :icon="userIcon" @error="handleAvatarError" @click.stop="navigateToUserProfile"></el-avatar>
      <div class="user-details">
        <h3 class="user-name clickable-name" @click.stop="navigateToUserProfile">{{ post.user.name }}</h3>
        <p class="post-time">{{ formatTime(post.createTime) }}</p>
      </div>
    </div>

    <!-- 帖子内容区域 -->
    <div class="post-content">
      <h2 class="post-title">{{ post.title }}</h2>
      <p class="post-text">{{ post.content }}</p>

      <!-- 图片展示区域 -->
      <div v-if="post.images && post.images.length > 0" class="post-images">
        <div v-for="(image, index) in post.images" :key="index" class="post-image-container">
          <img :src="getAvatarUrl(image)" :alt="`图片 ${index + 1}`" class="post-image"
            @error="handleImageError($event, image)" @load="handleImageLoad($event, image)"
            @click="image && getAvatarUrl(image) && viewImage(getAvatarUrl(image)!)" />
        </div>
      </div>
    </div>

    <!-- 帖子标签区域 -->
    <div v-if="post.tags && post.tags.length > 0" class="post-tags">
      <el-tag v-for="(tag, index) in post.tags" :key="index" size="small" effect="light">
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
        <span class="stat-text">{{ post.viewCount || 0 }}</span>
      </div>
      <div class="stat-item" @click.stop="toggleComments">
        <el-icon class="stat-icon">
          <ChatDotRound />
        </el-icon>
        <span class="stat-text">{{ post.commentCount || 0 }}</span>
      </div>
    </div>

    <!-- 评论组件区域 -->
    <div v-if="showComments" class="comments-section" @click.stop>
      <!-- 评论输入框 -->
      <div class="comment-input-area">
        <el-avatar :src="currentUserAvatar" :size="32" class="comment-input-avatar" :icon="userIcon"></el-avatar>
        <div class="comment-input-wrapper">
          <el-input v-model="commentText" type="textarea" placeholder="写下你的评论..." :rows="2" resize="none"
            class="comment-input"></el-input>
          <div class="comment-input-actions">
            <el-button size="small" @click.stop="submitAnonymousComment" :disabled="!commentText.trim()"
              style="margin-left: 8px; color: white">
              匿名发送
            </el-button>
            <el-button type="primary" size="small" @click.stop="submitComment" :disabled="!commentText.trim()">
              发送
            </el-button>
          </div>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-avatar :src="getAvatarUrl(comment.user.avatarUrl)" :size="32" class="comment-avatar"
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
                <el-button size="small" @click.stop="
                  () => {
                    console.log('取消按钮被点击，调用cancelReply');
                    cancelReply();
                  }
                ">
                  取消
                </el-button>
                <el-button type="primary" size="small" @click.stop="
                  () => {
                    console.log('发送回复按钮被点击，调用submitReply');
                    submitReply(comment);
                  }
                " :disabled="!replyText.trim()">
                  发送回复
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch, onMounted, onUnmounted, h } from "vue";
import {
  ElAvatar,
  ElIcon,
  ElTag,
  ElInput,
  ElButton,
  ElMessage,
  ElMessageBox,
  ElImage,
} from "element-plus";
import { Star, View, ChatDotRound, UserFilled } from "@element-plus/icons-vue";
import axios from "axios";
import { useUserStore } from "../stores/userStore";
import { useRouter } from "vue-router";
// 在组件中使用时需要调用函数获取store实例
const userStore = useUserStore();

// 定义用户信息接口
interface UserInfo {
  id: number;
  name: string;
  avatarUrl?: string | null;
}

// 定义帖子数据接口
interface PostData {
  id: number;
  user: UserInfo;
  title: string;
  content: string;
  createTime: string | Date;
  likeCount?: number;
  viewCount?: number;
  commentCount?: number;
  tags?: string[];
  isLike?: number; // 0表示未点赞，1表示已点赞
  images?: string[]; // 帖子图片数组
}

// 定义评论数据接口
interface CommentData {
  id: number;
  user: UserInfo;
  content: string;
  createTime: string | Date;
  likeCount?: number;
  isLiked?: boolean;
  isAnonymous?: boolean;
  // 添加回复相关字段
  replyToUserId?: number; // 回复的用户ID
  replyToUserName?: string; // 回复的用户名
}

// 定义组件props
const props = defineProps<{
  post: PostData;
}>();

// 创建路由实例
const router = useRouter();

// 跳转到用户主页
const navigateToUserProfile = () => {
  if (props.post.user && props.post.user.id) {
    router.push({
      path: `/user/${props.post.user.id}`,
      query: {
        name: props.post.user.name,
        avatar: props.post.user.avatarUrl
      }
    });
  }
};

// 默认用户图标
const userIcon = computed(() => UserFilled);

// 评论相关状态
const showComments = ref(false);
const commentText = ref("");
const replyText = ref("");
const replyingTo = ref<number | null>(null);
const comments = ref<CommentData[]>([]);

// 获取当前用户头像
const currentUserAvatar = computed(() => {
  // 从用户状态管理中获取真实的当前用户头像，并使用getAvatarUrl函数处理
  return getAvatarUrl(userStore.user.avatar);
});

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
    return `${date.getMonth() + 1}月${date.getDate()}日`;
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

// 图片加载错误处理
const handleImageError = (event: Event, imageUrl: string) => {
  // 确保event.target是HTML元素
  if (event.target instanceof HTMLImageElement) {
    // 使用项目内的error.png作为占位图
    event.target.src = "/src/assets/placeholders/error.png";
    event.target.alt = "图片加载失败";
    console.error("图片加载失败:", imageUrl);
  }
};

// 头像加载错误处理
const handleAvatarError = (event: Event) => {
  // 确保event.target是HTML元素
  if (event.target instanceof HTMLImageElement) {
    // 使用项目内的error.png作为占位头像
    event.target.src = "/src/assets/placeholders/error.png";
    event.target.alt = "默认头像";
    console.error("头像加载失败，使用默认头像");
  }
};

// 图片加载成功处理
const handleImageLoad = (_event: Event, _imageUrl: string) => { };

// 查看大图功能
const viewImage = (imageUrl: string) => {
  console.log("查看图片:", imageUrl);
  // 使用Element Plus的MessageBox实现图片预览功能
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
    // Element Plus 2.x中MessageBox不支持width和maxWidth属性，通过customClass和样式实现
  }).catch(() => {
    // 捕获关闭事件，不做任何处理
  });
};

// 切换评论区域显示/隐藏
const toggleComments = () => {
  showComments.value = !showComments.value;
  // 如果显示评论，这里可以加载评论列表数据
  if (showComments.value) {
    loadComments();
  }
};

// 加载评论数据
const loadComments = async () => {
  try {
    // 发送请求获取评论列表
    const response = await axios.get(`/comment/list?postId=${props.post.id}`, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    if (response.data.code === 1) {
      // 确保data存在且是数组
      if (Array.isArray(response.data.data)) {
        // 转换API返回的数据格式为前端使用的CommentData格式
        comments.value = response.data.data.map((item: any) => {
          const isAnonymous = item.isAnonymous === 1;
          return {
            id: item.id,
            user: {
              id: isAnonymous ? -1 : item.userId, // 匿名用户使用特殊ID
              name: isAnonymous ? "匿名用户" : item.userName, // 匿名用户显示统一名称
              avatarUrl: isAnonymous
                ? "/anonymous-avatar.png"
                : item.avatar || null, // 匿名用户使用默认头像
            },
            content: item.content,
            createTime: item.createAt, // 使用createAt字段而不是createTime
            likeCount: item.likeCount || 0,
            isLiked: item.isLike === 1,
            isAnonymous: isAnonymous, // 将数字转换为布尔值
            // 添加回复相关字段
            replyToUserId: item.replyUserId, // 回复的用户ID
            replyToUserName: item.replyUserName, // 回复的用户名
          };
        });
      }
    }
    // 输出评论列表到控制台，方便调试
    console.log("加载的评论列表:", comments.value);
  } catch (error) {
    console.error("加载评论失败:", error);
    // 静默处理错误，避免不必要的报错信息
    ElMessage.error("加载评论失败");
    // 如果加载失败，可以使用一些模拟数据作为备选
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
        isLiked: false,
      },
      {
        id: 3,
        user: {
          id: 4,
          name: "王五",
          avatarUrl: null,
        },
        content: "说得对，支持！",
        createTime: new Date(Date.now() - 10800000),
        likeCount: 1,
        isLiked: false,
        replyToUserId: 2,
        replyToUserName: "张三",
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
  console.log("submitCommentOrReply 函数被调用");
  console.log("提交参数:", params);

  if (!params.content.trim()) {
    console.log("内容为空，不提交");
    return;
  }

  // 验证内容长度
  if (params.content.trim().length > 200) {
    console.log("内容超过200字，不提交");
    ElMessage.warning("内容不能超过200字");
    return;
  }

  try {
    // 构造CommentDTO请求体
    const commentDTO = {
      postId: props.post.id, // 帖子ID
      content: params.content.trim(), // 内容
      isAnonymous: params.isAnonymous, // 是否匿名
      replyUserId: params.replyUserId, // 回复人ID（评论时为null，回复时为被回复人的ID）
      replyCommentId: params.replyCommentId, // 回复评论ID（评论时为null，回复时为被回复的评论ID）
    };

    console.log("准备发送请求到/comment/add接口");
    console.log("请求参数:", commentDTO);
    console.log("请求头:", { token: ` ${userStore.token}` });

    // 发送请求到/comment/add接口（评论和回复使用同一个接口）
    const response = await axios.post("/comment/add", commentDTO, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    console.log("请求成功，响应数据:", response.data);

    // 验证响应状态（根据后端约定，code=1表示成功）
    if (response.data.code === 1) {
      console.log("操作成功，显示成功消息:", params.successMessage);
      ElMessage.success(params.successMessage);

      console.log("重新加载评论列表");
      // 重新加载评论列表
      await loadComments();

      console.log("调用clearText函数清除输入内容");
      // 清除输入内容
      params.clearText();
    } else {
      console.log(
        "操作失败，后端返回错误:",
        response.data.message || "操作失败"
      );
      ElMessage.error(response.data.message || "操作失败");
    }
  } catch (error) {
    console.error("提交失败，发生异常:", error);
    ElMessage.error((error as any).message || "提交失败，请重试");
  }
};

// 提交评论
const submitComment = async () => {
  await submitCommentOrReply({
    content: commentText.value,
    isAnonymous: 0, // 非匿名
    replyUserId: null, // 评论时不指定回复用户
    replyCommentId: null, // 评论时不指定回复评论
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
    isAnonymous: 1, // 匿名
    replyUserId: null, // 评论时不指定回复用户
    replyCommentId: null, // 评论时不指定回复评论
    successMessage: "匿名评论成功",
    clearText: () => {
      commentText.value = "";
    },
  });
};

// 切换评论点赞状态
const toggleCommentLike = async (comment: CommentData) => {
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
const startReply = (comment: CommentData) => {
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

// 点击页面其他地方关闭评论
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  // 检查点击是否在卡片外部且评论区域是打开的
  const postCard = target.closest(".post-card");
  if (!postCard && showComments.value) {
    showComments.value = false;
  }
};

// 组件挂载时添加点击事件监听
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

// 组件卸载时移除点击事件监听
onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

// 创建本地状态用于乐观更新，避免直接修改props
const localLikeStatus = ref<{
  isLike: number;
  likeCount: number;
}>({
  isLike: props.post.isLike || 0,
  likeCount: props.post.likeCount || 0,
});

// // 添加console.log来调试点赞状态
// console.log("PostCard 初始化点赞状态:", {
//   propsPost: props.post,
//   localLikeStatus: localLikeStatus.value,
// });

// 监听props变化，更新本地状态
watch(
  () => props.post,
  (newPost) => {
    // console.log("PostCard props.post变化:", {
    //   newPost: newPost,
    //   newPostIsLike: newPost.isLike,
    //   newPostLikeCount: newPost.likeCount,
    // });
    localLikeStatus.value = {
      isLike: newPost.isLike || 0,
      likeCount: newPost.likeCount || 0,
    };
  },
  { deep: true }
);

// 切换点赞状态
const toggleLike = async () => {
  // 先检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再点赞");
    return;
  }

  // 预先保存原始状态，确保在catch块中也能访问
  const oldIsLike = localLikeStatus.value.isLike;
  const oldLikeCount = localLikeStatus.value.likeCount;
  const newIsLike = oldIsLike === 1 ? 0 : 1;

  try {
    // 更新本地UI状态（乐观更新）
    localLikeStatus.value.isLike = newIsLike;
    localLikeStatus.value.likeCount = oldLikeCount + (newIsLike === 1 ? 1 : -1);

    // 调用后端API - 使用PUT请求和新的URL格式
    const response = await axios.put(
      `/post/like/${props.post.id}`,
      {},
      {
        headers: {
          token: ` ${userStore.token}`,
        },
      }
    );

    // 验证响应状态
    if (response.data.code !== 1) {
      // 后端失败，回滚本地状态
      localLikeStatus.value.isLike = oldIsLike;
      localLikeStatus.value.likeCount = oldLikeCount;
      ElMessage.error(response.data.message || "操作失败，请重试");
    } else {
      // 操作成功，通知父组件更新点赞状态
      emit("update:like-status", props.post.id, newIsLike);
    }
  } catch (error) {
    console.error("点赞操作失败:", error);
    // 发生错误，回滚本地状态
    localLikeStatus.value.isLike = oldIsLike;
    localLikeStatus.value.likeCount = oldLikeCount;
    ElMessage.error((error as any).message || "网络错误，请稍后重试");
  }
};

// 提交回复
const submitReply = async (comment: CommentData) => {
  console.log("submitReply 函数被调用");
  console.log("当前回复内容:", replyText.value);
  console.log("被回复的评论:", comment);

  await submitCommentOrReply({
    content: replyText.value,
    isAnonymous: 0, // 默认非匿名回复
    replyUserId: comment.user.id, // 回复人ID（被回复人的ID）
    replyCommentId: comment.id, // 回复评论ID（被回复的评论ID）
    successMessage: "回复成功",
    clearText: () => {
      console.log("回复成功，清除输入内容和回复状态");
      replyText.value = "";
      replyingTo.value = null;
    },
  });
};

// 定义事件
const emit = defineEmits<{
  click: [post: PostData];
  "update:like-status": [id: number, isLike: number];
}>();

// 处理卡片点击事件
const handleCardClick = () => {
  // 如果点击卡片其他区域，关闭评论区域
  if (showComments.value) {
    showComments.value = false;
    return;
  }
  emit("click", props.post);
  // 跳转到帖子详情页
  router.push({ path: `/post/${props.post.id}` });
};
</script>

<style scoped>
.post-card {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  user-select: none;
}

/* 评论点赞状态样式 */
.comment-action .comment-action-icon.liked {
  color: #ff4d4f !important;
}

.comment-action .comment-action-text.liked-text {
  color: #ff4d4f !important;
}

.post-card:active {
  transform: scale(0.98);
  background-color: #f8f9fa;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 用户信息区域样式 */
.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-avatar {
  border-radius: 50%;
  margin-right: 12px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 4px 0;
  color: #333333;
}

.post-time {
  font-size: 12px;
  color: #999999;
  margin: 0;
}

/* 帖子内容区域样式 */
.post-content {
  margin-bottom: 12px;
}

.post-title {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 帖子标签区域样式 */
.post-tags {
  margin-bottom: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 帖子统计区域样式 */
.post-stats {
  display: flex;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: auto;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-right: 24px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.stat-item:hover {
  color: #409eff;
}

.stat-icon {
  margin-right: 4px;
  font-size: 14px;
  transition: color 0.3s ease;
  color: #666666;
  /* 默认颜色 */
}

/* 提高优先级，确保已点赞状态颜色正确显示 */
.stat-item .stat-icon.liked {
  color: #ff4d4f !important;
  /* 已点赞状态颜色，使用!important确保优先级 */
}

.stat-text {
  font-size: 14px;
  color: #666666;
  /* 默认颜色 */
}

/* 提高优先级，确保已点赞状态文字颜色正确显示 */
.stat-item .stat-text.liked-text {
  color: #ff4d4f !important;
  /* 已点赞状态文字颜色，使用!important确保优先级 */
}

/* 评论区域样式 */
.comments-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
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

/* 图片展示区域样式 */
.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.post-image-container {
  position: relative;
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.post-image:hover {
  transform: scale(1.05);
}

/* 评论输入区域样式 */
.comment-input-area {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.comment-input-avatar {
  margin-right: 12px;
  flex-shrink: 0;
}

.comment-input-wrapper {
  flex: 1;
}

.comment-input {
  border-radius: 8px;
  margin-bottom: 8px;
}

.comment-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  resize: none;
  border-color: #e0e0e0;
  transition: border-color 0.3s ease;
}

.comment-input :deep(.el-textarea__inner:focus) {
  border-color: #009688;
  box-shadow: 0 0 0 2px rgba(0, 150, 136, 0.1);
}

.comment-input-actions {
  display: flex;
  justify-content: flex-end;
}

.comment-input-actions :deep(.el-button) {
  border-radius: 8px;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
  transition: all 0.3s ease;
}

.comment-input-actions :deep(.el-button:hover:not(:disabled)) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-1px);
}

.comment-input-actions :deep(.el-button:disabled) {
  background: #e0e0e0;
  color: #999;
}

/* 评论列表样式 */
.comments-list {
  margin-bottom: 16px;
}

.comment-item {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.comment-avatar {
  margin-right: 12px;
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
  gap: 6px;
}

.comment-user {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.comment-reply-to {
  font-size: 11.2px;
  color: #999;
  opacity: 0.65;
  font-weight: normal;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

/* 评论操作样式 */
.comment-actions {
  display: flex;
  gap: 20px;
}

.comment-action {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: color 0.3s ease;
  font-size: 13px;
  color: #999;
}

.comment-action:hover {
  color: #009688;
}

.comment-action-icon {
  margin-right: 4px;
  font-size: 13px;
}

.comment-action-text {
  font-size: 13px;
}

/* 回复输入框样式 */
.reply-input-area {
  margin-top: 12px;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 8px;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.reply-input {
  margin-bottom: 10px;
}

.reply-input :deep(.el-textarea__inner) {
  border-radius: 6px;
  resize: none;
  border-color: #e0e0e0;
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
  font-size: 13px;
}

.reply-input-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
}

.reply-input-actions :deep(.el-button--primary:hover:not(:disabled)) {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-card {
    padding: 12px;
  }

  .comment-input-area,
  .comment-item {
    flex-direction: column;
  }

  .comment-input-avatar,
  .comment-avatar {
    margin-right: 0;
    margin-bottom: 8px;
    align-self: flex-start;
  }

  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .comment-actions {
    gap: 16px;
  }
}
</style>