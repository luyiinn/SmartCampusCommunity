<template>
  <div class="square-page">
    <div class="square-header">
      <h1>广场</h1>
      <p class="subtitle">校园社区的公共动态与话题聚合区</p>
    </div>

    <div class="square-content">
      <TagNav @change="handleTagChange" />

      <main class="feed">
        <div class="publish-bar">
          <div class="publish-text">
            记录灵感与日常，分享你的校园故事，让更多同学看到你
          </div>
          <el-button type="primary" class="publish-btn" @click="openPublish"
            >立即发布一条新的校园动态</el-button
          >
        </div>

        <PostList
          :title="'动态列表'"
          :items="posts"
          :loading="postsLoading"
          :has-more="hasMorePosts"
          :loading-more="postsLoadingMore"
          :auto-fetch="true"
          @fetch="handleRefresh"
          @fetch-more="fetchMorePosts"
          @select="handleSelect"
          @search="handleSearch"
          @update:like-status="handleLikeStatusUpdate"
        />
      </main>
    </div>

    <PostPublishDialog v-model:visible="publishVisible" />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import PostPublishDialog from "../components/PostPublishDialog.vue";
import TagNav from "../components/TagNav.vue";
import PostList from "../components/PostList.vue";
import type { PostItem } from "../components/PostList.vue";
import axios from "axios";
import { useTagStore } from "../stores/tagStore";
import { useUserStore } from "../stores/userStore";
import { ElMessage } from "element-plus";
import type { Tag } from "../stores/tagStore";

const route = useRoute();

const publishVisible = ref(false);
const openPublish = () => {
  publishVisible.value = true;
};
const tagStore = useTagStore();
const userStore = useUserStore();

// 分页相关状态
let currentPage = ref(1);
const hasMorePosts = ref(true);
const postsLoadingMore = ref(false);
const totalPosts = ref(0);

// 刷新方法
const handleRefresh = () => {
  // 确保currentPage是响应式引用
  if (!currentPage || typeof currentPage.value === "undefined") {
    currentPage = ref(1);
  }
  currentPage.value = 1;
  hasMorePosts.value = true;
  fetchPosts(false);
};

// 初始化时确保标签数据已加载
onMounted(async () => {
  try {
    await tagStore.fetchTags();
    // 初始化时获取帖子数据
    handleRefresh();
  } catch (error) {
    console.error("初始化标签数据失败:", error);
    // 错误处理已在tagStore中完成
  }
});

// 监听路由参数变化，当用户状态变化导致页面刷新时重新获取数据
watch(
  () => route.query._t,
  () => {
    console.log("路由参数变化，重新获取帖子数据");
    handleRefresh();
  }
);

// 处理标签切换事件
const handleTagChange = (tagId: number, tag: Tag) => {
  console.log("标签已切换:", tagId, tag.name);
  // 标签切换时重置分页
  currentPage.value = 1;
  hasMorePosts.value = true;
  posts.value = [];
  fetchPosts();
};

// 搜索关键词状态
const searchKeyword = ref("");

const postsLoading = ref(false);
const posts = ref<PostItem[]>([]);
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
      isLike: number; // 添加isLike字段，0表示未点赞，1表示已点赞
      images: string[]; // 帖子图片数组
    }>;
  };
}

// 处理搜索事件
const handleSearch = (keyword: string) => {
  searchKeyword.value = keyword;
  // 重置页码并重新获取数据
  currentPage.value = 1;
  fetchPosts();
};

const fetchPosts = async (isLoadMore = false) => {
  // 确保currentPage是响应式引用
  if (!currentPage || typeof currentPage.value === "undefined") {
    currentPage = ref(1);
  }

  // 根据是否加载更多设置不同的加载状态
  if (isLoadMore) {
    postsLoadingMore.value = true;
  } else {
    postsLoading.value = true;
  }

  try {
    // 获取选中的标签ID
    const selectedTagId = tagStore.selectedTagId;
    // console.log("Square getPosts request:", {
    //   page: currentPage.value,
    //   tag: selectedTagId,
    //   keyword: searchKeyword.value,
    // });

    // 检查用户是否已登录（有token）
    if (!userStore.token) {
      ElMessage.warning("请先登录以查看帖子列表");
      if (!isLoadMore) {
        posts.value = [];
      }
      return;
    }

    // 构建请求参数，包含搜索关键词
    const params = {
      tagId: selectedTagId || undefined,
      page: currentPage.value,
      size: 10,
      keyword: searchKeyword.value || undefined,
    };

    // 向 /post/list API 发送请求并添加认证 token
    const response = await axios.get<PostApiResponse>("/post/list", {
      params,
      headers: {
        token: ` ${userStore.token}`,
      },
      timeout: 10000, // 添加10秒超时设置
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
        .map((item) => {
          // // 添加console.log来调试数据转换
          // console.log("Square convertToPostListData:", {
          //   postId: item.id,
          //   postImages: item.images,
          //   postIsLike: item.isLike,
          // });
          return {
            id: item.id,
            title: item.title || "无标题",
            content: item.contentSummary || "",
            date: item.createdAt || new Date().toISOString(),
            // 添加额外字段以便PostList组件可以传递给PostCard
            userId: item.userId || 0,
            userName: item.userName || "未知用户",
            avatar: item.avatar || null,
            isAnonymous: item.isAnonymous || 0,
            viewCount: item.viewCount || 0,
            likeCount: item.likeCount || 0,
            commentCount: item.commentCount || 0,
            tags: item.tags || [],
            // 从API响应数据中获取isLike字段的值
            isLike: item.isLike || 0,
            // 传递图片数组给PostList组件
            images: item.images || [],
          };
        });

      // 根据是否加载更多决定是替换还是追加数据
      if (isLoadMore) {
        // 追加数据，避免重复
        const existingIds = new Set(
          posts.value.map((post: PostItem) => post.id)
        );
        const uniqueNewPosts = newPosts.filter(
          (post) => !existingIds.has(post.id)
        );
        posts.value = [...posts.value, ...uniqueNewPosts];
      } else {
        posts.value = newPosts;
      }

      // 判断是否还有更多数据
      hasMorePosts.value = posts.value.length < totalPosts.value;

      // 如果是首次加载且帖子列表为空，显示提示信息
      if (!isLoadMore && posts.value.length === 0) {
        ElMessage.info("当前没有帖子");
      }
    } else {
      // 处理业务错误
      const errorMessage = response.data.msg || "获取帖子失败，请稍后重试";
      ElMessage.error(errorMessage);
      console.error("API业务错误:", errorMessage);
      if (!isLoadMore) {
        posts.value = [];
      }
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
          // 这里可以添加跳转到登录页的逻辑
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

    if (!isLoadMore) {
      posts.value = [];
    }
  } finally {
    // 重置加载状态
    postsLoading.value = false;
    postsLoadingMore.value = false;
  }
};

// 加载更多帖子的方法
const fetchMorePosts = async () => {
  // 检查是否有更多数据且不在加载中
  if (!hasMorePosts.value || postsLoadingMore.value) {
    return;
  }

  // 确保currentPage是响应式引用
  if (!currentPage || typeof currentPage.value === "undefined") {
    currentPage = ref(1);
  }

  // 增加页码
  currentPage.value++;

  // 加载下一页数据
  await fetchPosts(true);
};
const handleSelect = (item: PostItem) => {
  console.log("选中的帖子:", item);
};

// 处理点赞状态更新
const handleLikeStatusUpdate = (id: number, isLike: number) => {
  // 找到对应的帖子并更新点赞状态
  const postIndex = posts.value.findIndex((post) => post.id === id);
  if (postIndex !== -1 && posts.value[postIndex]) {
    const post = posts.value[postIndex];
    // 记录旧的点赞状态，用于计算点赞数变化
    const oldIsLike = post.isLike || 0;

    post.isLike = isLike;

    // 根据点赞状态变化更新点赞数（+1/-1切换逻辑）
    if (isLike === 1 && oldIsLike === 0) {
      // 从不点赞变为点赞，点赞数+1
      post.likeCount = (post.likeCount || 0) + 1;
    } else if (isLike === 0 && oldIsLike === 1) {
      // 从点赞变为不点赞，点赞数-1
      post.likeCount = (post.likeCount || 0) - 1;
    }
    // 如果状态没有变化（如点击已点赞的帖子再次点赞），则不更新点赞数
  }
};
</script>

<style scoped>
.square-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 16px 0 32px;
}

.square-header {
  margin-bottom: 16px;
  padding: 16px 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.square-header h1 {
  font-size: 22px;
  color: #0f766e;
  margin: 0 0 6px;
}

.square-header .subtitle {
  font-size: 13px;
  color: #6b7280;
}

.square-content {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.square-content > .tag-nav {
  width: 260px;
  flex: 0 0 260px;
}

.feed {
  flex: 1 1 auto;
  display: grid;
  gap: 16px;
}

.feed {
  display: grid;
  gap: 16px;
}

.publish-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  max-height: 64px;
}

.publish-text {
  color: #4b5563;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: calc(100% - 280px);
}

.publish-btn {
  font-weight: 600;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.25);
  transition: all 0.3s ease;
  flex: 0 0 auto;
}

.publish-btn:hover {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-1px);
}

.publish-btn:active {
  transform: translateY(0);
}

.post-list h2 {
  font-size: 16px;
  color: #1f2937;
  margin-bottom: 8px;
}

.post-list p {
  font-size: 14px;
  color: #6b7280;
}

@media (max-width: 768px) {
  .square-content {
    flex-direction: column;
  }

  .square-content > .tag-nav {
    width: 100%;
    flex: 0 0 auto;
  }
}
</style>