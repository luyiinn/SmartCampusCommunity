<template>
  <div class="profile-page">
    <!-- 个人信息头部 -->
    <el-card
      class="profile-header"
      shadow="hover"
      :body-style="{ padding: '16px 24px' }"
    >
      <div class="profile-header-content">
        <!-- 用户头像 -->
        <div class="profile-avatar">
          <img
            v-if="userStore.user.avatar"
            :src="userStore.user.avatar"
            :alt="userStore.user.userName"
            loading="lazy"
          />
          <div v-else class="avatar-placeholder">
            {{ userStore.user.userName?.charAt(0) || "用" }}
          </div>
        </div>

        <!-- 用户信息 -->
        <div class="profile-info">
          <h1 class="name">{{ userStore.user.userName || "个人主页" }}</h1>
          <p class="desc">展示个人信息与发布的动态</p>
        </div>
      </div>
    </el-card>

    <!-- 导航标签 -->
    <div class="profile-nav">
      <button
        v-for="(navItem, index) in navItems"
        :key="index"
        class="nav-btn"
        :class="{ active: activeNav === index }"
        @click="switchNav(index)"
      >
        {{ navItem }}
      </button>
    </div>

    <!-- 内容区域 -->
    <el-card
      v-if="!userStore.isLoggedIn"
      shadow="hover"
      :body-style="{ padding: '24px', textAlign: 'center' }"
    >
      <el-empty description="请先登录以查看个人主页" />
      <el-button
        type="primary"
        style="margin-top: 16px"
        @click="userStore.requireLogin()"
        >去登录</el-button
      >
    </el-card>

    <el-card
      v-else
      class="profile-content"
      shadow="hover"
      :body-style="{ padding: '0' }"
    >
      <!-- 帖子列表 -->
      <PostList
        v-if="activeNav === 0"
        :title="'我的动态'"
        :items="userPosts"
        :loading="loading"
        :auto-fetch="true"
        :has-more="hasMore"
        :loading-more="loadingMore"
        @fetch="fetchUserPosts"
        @fetch-more="fetchMorePosts"
        @select="handleSelectPost"
        @update:like-status="handleLikeStatusUpdate"
      />

      <!-- 收藏列表 -->
      <PostList
        v-else-if="activeNav === 1"
        :title="'我的收藏'"
        :items="likedPosts"
        :loading="likedPostsLoading"
        :auto-fetch="true"
        :has-more="likedPostsHasMore"
        :loading-more="likedPostsLoadingMore"
        @fetch="fetchLikedPosts"
        @fetch-more="fetchMoreLikedPosts"
        @select="handleSelectPost"
        @update:like-status="handleLikeStatusUpdate"
      />

      <!-- 关于页面 - 占位 -->
      <div v-else-if="activeNav === 2" class="content-placeholder">
        <el-empty description="关于功能即将上线" />
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { watch, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "../stores/userStore";
import { ElMessage, ElCard, ElEmpty, ElButton } from "element-plus";
import PostList, { type PostItem } from "../components/PostList.vue";
import axios from "axios";

// 路由和用户状态
const route = useRoute();
const userStore = useUserStore();

// 导航状态
const navItems = ["帖子", "收藏", "关于"];
const activeNav = ref(0);

// 帖子数据状态
const userPosts = ref<PostItem[]>([]);
const loading = ref(false);
const loadingMore = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const totalPosts = ref(0);

// 收藏列表数据状态
const likedPosts = ref<PostItem[]>([]);
const likedPostsLoading = ref(false);
const likedPostsLoadingMore = ref(false);
const likedPostsHasMore = ref(true);
const likedPostsCurrentPage = ref(1);
const likedPostsTotal = ref(0);

// 定义API响应数据结构
interface PostApiResponse {
  code: number;
  msg: string | null;
  data: {
    total: number;
    list: Array<{
      id: number;
      userId: number;
      userName: string;
      avatar: string | null;
      title: string;
      contentSummary: string;
      isAnonymous: number;
      status: number;
      viewCount: number;
      likeCount: number;
      commentCount: number;
      createdAt: string;
      tags: string[];
      isLike: number;
      images: string[]; // 帖子图片数组
    }>;
  };
}

// 计算属性：检查是否有帖子 - 暂时注释掉，因为未使用
// const hasPosts = computed(() => userPosts.value.length > 0);

// 切换导航
const switchNav = (index: number) => {
  activeNav.value = index;
};

// 获取用户帖子列表
const fetchUserPosts = async () => {
  if (!userStore.isLoggedIn || !userStore.user.id) {
    return;
  }

  loading.value = true;
  currentPage.value = 1;

  try {
    // 构建请求参数
    const params = {
      page: currentPage.value,
      size: 10,
      userId: userStore.user.id, // 筛选当前用户的帖子
    };

    // 向 /post/list API 发送请求并添加认证 token
    const response = await axios.get<PostApiResponse>("/post/list", {
      params,
      headers: {
        token: ` ${userStore.token}`,
      },
      timeout: 10000,
    });

    // 验证响应格式
    if (!response || !response.data) {
      throw new Error("无效的响应数据");
    }

    // 检查响应码
    if (response.data.code === 1) {
      // 验证数据结构
      if (!response.data.data || !Array.isArray(response.data.data.list)) {
        throw new Error("API返回的数据格式不正确");
      }

      // 更新总数据量
      totalPosts.value = response.data.data.total || 0;

      // 转换新获取的帖子数据
      const newPosts = response.data.data.list
        .filter((item) => item && typeof item === "object") // 过滤掉无效项
        .map((item) => ({
          id: item.id,
          title: item.title || "无标题",
          content: item.contentSummary || "",
          date: item.createdAt || new Date().toISOString(),
          userId: item.userId || 0,
          userName: item.userName || "未知用户",
          avatar: item.avatar || null,
          isAnonymous: item.isAnonymous || 0,
          viewCount: item.viewCount || 0,
          likeCount: item.likeCount || 0,
          commentCount: item.commentCount || 0,
          tags: item.tags || [],
          isLike: item.isLike || 0,
          images: item.images || [], // 传递图片数组给PostList组件
        }));

      userPosts.value = newPosts;
      hasMore.value = userPosts.value.length < totalPosts.value;

      // 如果是首次加载且帖子列表为空，显示提示信息
      if (userPosts.value.length === 0) {
        ElMessage.info("您还没有发布过帖子");
      }
    } else {
      // 处理业务错误
      const errorMessage = response.data.msg || "获取帖子失败，请稍后重试";
      ElMessage.error(errorMessage);
      console.error("API业务错误:", errorMessage);
      userPosts.value = [];
    }
  } catch (error) {
    // 处理网络错误、超时等
    console.error("获取帖子列表失败:", error);

    if (axios.isAxiosError(error)) {
      if (error.code === "ECONNABORTED") {
        ElMessage.error("请求超时，请检查网络连接");
      } else if (error.response) {
        // 服务器返回错误状态码
        const status = error.response.status;
        if (status === 401) {
          ElMessage.error("登录已过期，请重新登录");
          userStore.requireLogin();
        } else if (status === 403) {
          ElMessage.error("您没有权限查看帖子");
        } else if (status >= 500) {
          ElMessage.error("服务器错误，请稍后重试");
        } else {
          ElMessage.error(`请求失败: ${error.response.status}`);
        }
      } else if (error.request) {
        // 请求已发送但没有收到响应
        ElMessage.error("网络连接失败，请检查您的网络");
      } else {
        // 请求配置出错
        ElMessage.error("请求配置错误");
      }
    } else {
      // 其他类型的错误
      ElMessage.error("获取帖子失败，请稍后重试");
    }

    userPosts.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载更多帖子
const fetchMorePosts = async () => {
  if (
    loadingMore.value ||
    !hasMore.value ||
    !userStore.isLoggedIn ||
    !userStore.user.id
  )
    return;

  loadingMore.value = true;

  try {
    // 增加页码
    currentPage.value++;

    // 构建请求参数
    const params = {
      page: currentPage.value,
      size: 10,
      userId: userStore.user.id, // 筛选当前用户的帖子
    };

    // 向 /post/list API 发送请求并添加认证 token
    const response = await axios.get<PostApiResponse>("/post/list", {
      params,
      headers: {
        token: ` ${userStore.token}`,
      },
      timeout: 10000,
    });

    // 验证响应格式
    if (!response || !response.data) {
      throw new Error("无效的响应数据");
    }

    // 检查响应码
    if (response.data.code === 1) {
      // 验证数据结构
      if (!response.data.data || !Array.isArray(response.data.data.list)) {
        throw new Error("API返回的数据格式不正确");
      }

      // 转换新获取的帖子数据
      const newPosts = response.data.data.list
        .filter((item) => item && typeof item === "object") // 过滤掉无效项
        .map((item) => ({
          id: item.id,
          title: item.title || "无标题",
          content: item.contentSummary || "",
          date: item.createdAt || new Date().toISOString(),
          userId: item.userId || 0,
          userName: item.userName || "未知用户",
          avatar: item.avatar || null,
          isAnonymous: item.isAnonymous || 0,
          viewCount: item.viewCount || 0,
          likeCount: item.likeCount || 0,
          commentCount: item.commentCount || 0,
          tags: item.tags || [],
          isLike: item.isLike || 0,
        }));

      // 避免重复数据
      const existingIds = new Set(userPosts.value.map((post) => post.id));
      const uniqueNewPosts = newPosts.filter(
        (post) => !existingIds.has(post.id)
      );

      // 追加数据
      userPosts.value = [...userPosts.value, ...uniqueNewPosts];
      hasMore.value = userPosts.value.length < (response.data.data.total || 0);
    } else {
      // 处理业务错误
      const errorMessage = response.data.msg || "加载更多帖子失败，请稍后重试";
      ElMessage.error(errorMessage);
      console.error("API业务错误:", errorMessage);
    }
  } catch (error) {
    // 处理网络错误、超时等
    console.error("加载更多帖子失败:", error);

    if (axios.isAxiosError(error)) {
      if (error.code === "ECONNABORTED") {
        ElMessage.error("请求超时，请检查网络连接");
      } else if (error.response) {
        const status = error.response.status;
        if (status === 401) {
          ElMessage.error("登录已过期，请重新登录");
          userStore.requireLogin();
        } else if (status >= 500) {
          ElMessage.error("服务器错误，请稍后重试");
        } else {
          ElMessage.error(`请求失败: ${error.response.status}`);
        }
      } else if (error.request) {
        ElMessage.error("网络连接失败，请检查您的网络");
      } else {
        ElMessage.error("请求配置错误");
      }
    } else {
      ElMessage.error("加载更多帖子失败，请稍后重试");
    }
  } finally {
    loadingMore.value = false;
  }
};

// 获取用户已点赞帖子列表
const fetchLikedPosts = async () => {
  if (!userStore.isLoggedIn) {
    return;
  }

  likedPostsLoading.value = true;
  likedPostsCurrentPage.value = 1;

  try {
    // 构建请求参数
    const params = {
      page: likedPostsCurrentPage.value,
      size: 10,
    };

    // 向 /post/liked-list API 发送请求并添加认证 token
    const response = await axios.get<PostApiResponse>("/post/liked-list", {
      params,
      headers: {
        token: ` ${userStore.token}`,
      },
      timeout: 10000,
    });

    // 验证响应格式
    if (!response || !response.data) {
      throw new Error("无效的响应数据");
    }

    // 检查响应码
    if (response.data.code === 1) {
      // 验证数据结构
      if (!response.data.data || !Array.isArray(response.data.data.list)) {
        throw new Error("API返回的数据格式不正确");
      }

      // 更新总数据量
      likedPostsTotal.value = response.data.data.total || 0;

      // 转换新获取的帖子数据
      const newPosts = response.data.data.list
        .filter((item) => item && typeof item === "object") // 过滤掉无效项
        .map((item) => ({
          id: item.id,
          title: item.title || "无标题",
          content: item.contentSummary || "",
          date: item.createdAt || new Date().toISOString(),
          userId: item.userId || 0,
          userName: item.userName || "未知用户",
          avatar: item.avatar || null,
          isAnonymous: item.isAnonymous || 0,
          viewCount: item.viewCount || 0,
          likeCount: item.likeCount || 0,
          commentCount: item.commentCount || 0,
          tags: item.tags || [],
          isLike: item.isLike || 0,
          images: item.images || [], // 传递图片数组给PostList组件
        }));

      likedPosts.value = newPosts;
      likedPostsHasMore.value = likedPosts.value.length < likedPostsTotal.value;

      // 如果是首次加载且帖子列表为空，显示提示信息
      if (likedPosts.value.length === 0) {
        ElMessage.info("您还没有点赞过帖子");
      }
    } else {
      // 处理业务错误
      const errorMessage = response.data.msg || "获取点赞帖子失败，请稍后重试";
      ElMessage.error(errorMessage);
      console.error("API业务错误:", errorMessage);
      likedPosts.value = [];
    }
  } catch (error) {
    // 处理网络错误、超时等
    console.error("获取点赞帖子列表失败:", error);

    if (axios.isAxiosError(error)) {
      if (error.code === "ECONNABORTED") {
        ElMessage.error("请求超时，请检查网络连接");
      } else if (error.response) {
        // 服务器返回错误状态码
        const status = error.response.status;
        if (status === 401) {
          ElMessage.error("登录已过期，请重新登录");
          userStore.requireLogin();
        } else if (status === 403) {
          ElMessage.error("您没有权限查看点赞帖子");
        } else if (status >= 500) {
          ElMessage.error("服务器错误，请稍后重试");
        } else {
          ElMessage.error(`请求失败: ${error.response.status}`);
        }
      } else if (error.request) {
        // 请求已发送但没有收到响应
        ElMessage.error("网络连接失败，请检查您的网络");
      } else {
        // 请求配置出错
        ElMessage.error("请求配置错误");
      }
    } else {
      // 其他类型的错误
      ElMessage.error("获取点赞帖子失败，请稍后重试");
    }

    likedPosts.value = [];
  } finally {
    likedPostsLoading.value = false;
  }
};

// 加载更多已点赞帖子
const fetchMoreLikedPosts = async () => {
  if (
    likedPostsLoadingMore.value ||
    !likedPostsHasMore.value ||
    !userStore.isLoggedIn
  )
    return;

  likedPostsLoadingMore.value = true;

  try {
    // 增加页码
    likedPostsCurrentPage.value++;

    // 构建请求参数
    const params = {
      page: likedPostsCurrentPage.value,
      size: 10,
    };

    // 向 /post/liked-list API 发送请求并添加认证 token
    const response = await axios.get<PostApiResponse>("/post/liked-list", {
      params,
      headers: {
        token: ` ${userStore.token}`,
      },
      timeout: 10000,
    });

    // 验证响应格式
    if (!response || !response.data) {
      throw new Error("无效的响应数据");
    }

    // 检查响应码
    if (response.data.code === 1) {
      // 验证数据结构
      if (!response.data.data || !Array.isArray(response.data.data.list)) {
        throw new Error("API返回的数据格式不正确");
      }

      // 转换新获取的帖子数据
      const newPosts = response.data.data.list
        .filter((item) => item && typeof item === "object") // 过滤掉无效项
        .map((item) => ({
          id: item.id,
          title: item.title || "无标题",
          content: item.contentSummary || "",
          date: item.createdAt || new Date().toISOString(),
          userId: item.userId || 0,
          userName: item.userName || "未知用户",
          avatar: item.avatar || null,
          isAnonymous: item.isAnonymous || 0,
          viewCount: item.viewCount || 0,
          likeCount: item.likeCount || 0,
          commentCount: item.commentCount || 0,
          tags: item.tags || [],
          isLike: item.isLike || 0,
          images: item.images || [], // 传递图片数组给PostList组件
        }));

      // 避免重复数据
      const existingIds = new Set(likedPosts.value.map((post) => post.id));
      const uniqueNewPosts = newPosts.filter(
        (post) => !existingIds.has(post.id)
      );

      // 追加数据
      likedPosts.value = [...likedPosts.value, ...uniqueNewPosts];
      likedPostsHasMore.value =
        likedPosts.value.length < (response.data.data.total || 0);
    } else {
      // 处理业务错误
      const errorMessage =
        response.data.msg || "加载更多点赞帖子失败，请稍后重试";
      ElMessage.error(errorMessage);
      console.error("API业务错误:", errorMessage);
    }
  } catch (error) {
    // 处理网络错误、超时等
    console.error("加载更多点赞帖子失败:", error);

    if (axios.isAxiosError(error)) {
      if (error.code === "ECONNABORTED") {
        ElMessage.error("请求超时，请检查网络连接");
      } else if (error.response) {
        const status = error.response.status;
        if (status === 401) {
          ElMessage.error("登录已过期，请重新登录");
          userStore.requireLogin();
        } else if (status >= 500) {
          ElMessage.error("服务器错误，请稍后重试");
        } else {
          ElMessage.error(`请求失败: ${error.response.status}`);
        }
      } else if (error.request) {
        ElMessage.error("网络连接失败，请检查您的网络");
      } else {
        ElMessage.error("请求配置错误");
      }
    } else {
      ElMessage.error("加载更多点赞帖子失败，请稍后重试");
    }
  } finally {
    likedPostsLoadingMore.value = false;
  }
};

// 处理点赞状态更新
const handleLikeStatusUpdate = (id: number, isLike: number) => {
  // 更新用户帖子列表中的点赞状态
  const postIndex = userPosts.value.findIndex((post) => post.id === id);
  if (postIndex !== -1) {
    const post = userPosts.value[postIndex];
    if (post) {
      const oldLikeStatus = post.isLike || 0;

      // 更新点赞状态
      post.isLike = isLike;

      // 根据点赞状态变化更新点赞数
      if (isLike === 1 && oldLikeStatus === 0) {
        post.likeCount = (post.likeCount || 0) + 1;
      } else if (isLike === 0 && oldLikeStatus === 1) {
        post.likeCount = (post.likeCount || 0) - 1;
      }
      userPosts.value = [...userPosts.value];
    }
  }

  // 更新已点赞帖子列表中的点赞状态
  const likedPostIndex = likedPosts.value.findIndex((post) => post.id === id);
  if (likedPostIndex !== -1) {
    const post = likedPosts.value[likedPostIndex];
    if (post) {
      const oldLikeStatus = post.isLike || 0;

      // 更新点赞状态
      post.isLike = isLike;

      // 根据点赞状态变化更新点赞数
      if (isLike === 1 && oldLikeStatus === 0) {
        post.likeCount = (post.likeCount || 0) + 1;
      } else if (isLike === 0 && oldLikeStatus === 1) {
        post.likeCount = (post.likeCount || 0) - 1;
      }
      likedPosts.value = [...likedPosts.value];
    }
  }
};

// 处理帖子点击
const handleSelectPost = (post: PostItem) => {
  console.log("选中的帖子:", post);
  // 这里可以添加跳转到帖子详情页的逻辑
};

// 监听路由参数变化，当用户状态变化导致页面刷新时重新获取数据
watch(
  () => route.query._t,
  () => {
    console.log("检测到用户状态变化，刷新个人主页");
    fetchUserPosts();
  }
);

// 监听用户登录状态变化
watch(
  () => userStore.isLoggedIn,
  (newVal) => {
    if (newVal) {
      fetchUserPosts();
    } else {
      userPosts.value = [];
    }
  }
);

// 页面挂载时获取用户帖子
onMounted(() => {
  fetchUserPosts();
});
</script>

<style scoped>
.profile-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 16px 0 32px;
  width: 100%;
}

/* 头部样式 */
.profile-header {
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.profile-header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 头像样式 */
.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 150, 136, 0.2);
  transition: transform 0.2s ease;
}

.profile-avatar:hover {
  transform: scale(1.05);
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
}

.avatar-placeholder {
  font-size: 28px;
  font-weight: bold;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 用户信息样式 */
.profile-info {
  flex: 1;
  min-width: 0;
}

.profile-info .name {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 4px 0;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-info .desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

/* 导航样式 */
.profile-nav {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  background: #ffffff;
  border-radius: 12px;
  padding: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.nav-btn {
  flex: 1;
  padding: 10px 16px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  outline: none;
}

.nav-btn:hover {
  background-color: #f9faf6;
  color: #009688;
  transform: translateY(-1px);
}

.nav-btn.active {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 150, 136, 0.2);
}

/* 内容区域样式 */
.profile-content {
  transition: all 0.3s ease;
}

.content-placeholder {
  padding: 48px 24px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-page {
    padding: 12px 0 24px;
    max-width: 100%;
  }

  .profile-header-content {
    flex-direction: column;
    text-align: center;
    padding: 12px 0;
  }

  .profile-avatar {
    width: 60px;
    height: 60px;
  }

  .avatar-placeholder {
    font-size: 24px;
  }

  .profile-info .name {
    font-size: 18px;
  }

  .profile-nav {
    margin: 12px 0;
  }

  .nav-btn {
    padding: 8px 12px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .profile-page {
    padding: 8px 0 16px;
  }

  .profile-header {
    border-radius: 8px;
  }

  .profile-header-content {
    gap: 12px;
  }

  .profile-avatar {
    width: 50px;
    height: 50px;
  }

  .avatar-placeholder {
    font-size: 20px;
  }

  .profile-info .name {
    font-size: 16px;
  }

  .profile-info .desc {
    font-size: 12px;
  }
}
</style>