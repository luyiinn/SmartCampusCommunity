<template>
  <el-card class="post-list-card" shadow="hover">
    <header class="post-list-header">
      <div class="title-group">
        <slot name="title">
          <h2 class="title">{{ title }}</h2>
        </slot>
        <div class="search-container">
          <el-input
            v-model="searchText"
            :placeholder="searchPlaceholder"
            :prefix-icon="Search"
            class="search-input"
            clearable
            @clear="handleClearSearch"
            @keyup="handleKeyPress"
            @input="handleSearch"
          >
          </el-input>
        </div>
      </div>
      <div class="actions">
        <slot name="actions">
          <el-button
            size="small"
            :loading="loadingProxy"
            @click="emitFetch"
            class="refresh-button"
            >刷新</el-button
          >
        </slot>
      </div>
    </header>

    <section
      class="post-list-body"
      ref="listBodyRef"
      @scroll="handleScroll"
      @touchstart="handleTouchStart"
      @touchend="handleTouchEnd"
    >
      <slot name="default" :items="itemsProxy">
        <el-empty
          v-if="!loadingProxy && itemsProxy.length === 0"
          description="暂无内容"
        ></el-empty>
        <el-skeleton v-else-if="loadingProxy" :rows="4" animated />
        <div v-else class="items">
          <PostCard
            v-for="it in itemsProxy"
            :key="it.id"
            :post="convertToPostCardData(it)"
            @click="emitSelect(it)"
            @update:like-status="handleLikeStatusUpdate"
          />
        </div>
      </slot>

      <!-- 加载更多状态 -->
      <div v-if="loadingMore" class="loading-more">
        <el-skeleton :rows="1" animated style="margin: 16px 0" />
        <div class="loading-text">加载中...</div>
      </div>

      <!-- 无更多数据提示 -->
      <div v-else-if="!hasMore && itemsProxy.length > 0" class="no-more">
        <el-divider>{{ noMoreText }}</el-divider>
      </div>
    </section>

    <footer class="post-list-footer">
      <slot name="footer"></slot>
    </footer>
  </el-card>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from "vue";
import PostCard from "./PostCard.vue";
import { Search } from "@element-plus/icons-vue";

// 组件 Props 类型定义
export interface PostItem {
  id: number | string;
  title: string;
  content?: string;
  date?: string;
  userId?: number;
  userName?: string;
  avatar?: string | null;
  isAnonymous?: number;
  viewCount?: number;
  likeCount?: number;
  commentCount?: number;
  tags?: string[];
  isLike?: number; // 0表示未点赞，1表示已点赞
  images?: string[]; // 帖子图片数组
}

const props = defineProps<{
  title?: string;
  description?: string;
  items?: PostItem[];
  loading?: boolean;
  autoFetch?: boolean;
  hasMore?: boolean;
  loadingMore?: boolean;
  noMoreText?: string;
  searchPlaceholder?: string;
}>();

const emit = defineEmits<{
  fetch: [];
  search: [string];
  fetchMore: [];
  select: [PostItem];
  "update:items": [PostItem[]];
  "update:loading": [boolean];
  "update:like-status": [id: number, isLike: number];
}>();

// 处理点赞状态更新
const handleLikeStatusUpdate = (id: number, isLike: number) => {
  // 更新本地缓存
  likeStatusCache.value.set(id, isLike);

  // // 添加console.log来调试点赞状态更新
  // console.log("PostList handleLikeStatusUpdate:", {
  //   id: id,
  //   isLike: isLike,
  //   cacheSize: likeStatusCache.value.size,
  // });

  // 通知父组件（如果需要）
  emit("update:like-status", id, isLike);
};

// 列表容器引用
const listBodyRef = ref<HTMLElement>();

// 下拉刷新相关状态
const showScrollTopHint = ref(false);
const startY = ref(0);
const isPulling = ref(false);

// 搜索相关状态
const searchText = ref("");
const searchPlaceholder = computed(
  () => props.searchPlaceholder ?? "搜索帖子关键词..."
);

// 用于保存点赞状态的本地缓存
const likeStatusCache = ref<Map<number | string, number>>(new Map());

// 标题默认值
const title = computed(() => props.title ?? "动态列表");

// 分页相关默认值
const hasMore = computed(() => props.hasMore ?? true);
const loadingMore = computed(() => props.loadingMore ?? false);
const noMoreText = computed(() => props.noMoreText ?? "没有更多内容了");

// 数据绑定代理 - 添加likeStatusCache作为依赖，确保点赞状态更新时重新渲染
const itemsProxy = computed<PostItem[]>(() => {
  // 访问likeStatusCache以建立依赖关系
  likeStatusCache.value.forEach(() => {});
  return props.items ?? [];
});
const loadingProxy = computed<boolean>(() => props.loading ?? false);

// 处理滚动事件，实现下拉加载更多和顶部提示
const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement;

  // 显示/隐藏顶部刷新提示
  showScrollTopHint.value = target.scrollTop < 50;

  // 避免重复触发加载
  if (loadingMore.value || !hasMore.value) return;

  // 计算是否滚动到底部附近（距离底部50px时触发）
  const { scrollTop, scrollHeight, clientHeight } = target;
  if (scrollTop + clientHeight >= scrollHeight - 50) {
    emit("fetchMore");
  }
};

// 处理触摸开始事件（移动设备支持）
const handleTouchStart = (event: TouchEvent) => {
  const target = event.currentTarget as HTMLElement;
  if (target.scrollTop === 0 && event.touches?.[0]) {
    startY.value = event.touches[0].clientY;
    isPulling.value = true;
  }
};

// 处理触摸结束事件（移动设备支持）
const handleTouchEnd = (event: TouchEvent) => {
  if (isPulling.value && event.changedTouches?.[0]) {
    const target = event.currentTarget as HTMLElement;
    const endY = event.changedTouches[0].clientY;
    const pullDistance = endY - startY.value;

    // 下拉距离足够时触发刷新
    if (pullDistance > 80 && target.scrollTop === 0) {
      emitFetch();
    }

    isPulling.value = false;
  }
};

// 生命周期：自动触发数据加载
onMounted(() => {
  if (props.autoFetch) emit("fetch");
});

// 数据转换函数：将PostItem转换为PostCard需要的格式
const convertToPostCardData = (item: PostItem) => {
  // 获取缓存的点赞状态，如果没有则使用item中的值
  const cachedLikeStatus = likeStatusCache.value.get(item.id);
  const isLikeValue =
    cachedLikeStatus !== undefined ? cachedLikeStatus : item.isLike || 0;

  return {
    id: typeof item.id === "string" ? parseInt(item.id) : item.id,
    user: {
      id: item.userId || 0,
      name: item.isAnonymous === 1 ? "匿名用户" : item.userName || "未知用户",
      avatarUrl: item.isAnonymous === 1 ? null : item.avatar || undefined,
    },
    title: item.title,
    content: item.content || "",
    createTime: item.date || new Date(),
    likeCount: item.likeCount || 0,
    viewCount: item.viewCount || 0,
    commentCount: item.commentCount || 0,
    tags: item.tags || [],
    images: item.images || [], // 传递图片数组给PostCard组件
    isLike: isLikeValue, // 传递点赞状态给PostCard组件，优先使用缓存的状态
  };
};

/** 事件触发封装 */
const emitFetch = () => emit("fetch");
const emitSelect = (item: PostItem) => emit("select", item);

// 搜索事件处理
const handleSearch = () => {
  emit("search", searchText.value);
};

// 清空搜索并重新加载全部数据
const handleClearSearch = () => {
  searchText.value = "";
  emit("search", "");
};

// 回车键触发搜索
const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === "Enter") handleSearch();
};
</script>

<style scoped>
.post-list-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  min-height: 400px !important;
}

.post-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px;
  border-bottom: 1px solid #f3f4f6;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
  min-width: 0;
}

.title {
  font-size: 18px;
  color: #1f2937;
  margin: 0;
  font-weight: 600;
  white-space: nowrap;
}

.search-container {
  display: flex;
  align-items: center;
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 60%;
}

.search-input .el-input__wrapper {
  border-radius: 8px;
  transition: all 0.3s ease;
  border-color: #009688;
  background-color: #f9faf6;
}

.search-input .el-input__wrapper:hover {
  box-shadow: 0 0 0 2px rgba(0, 150, 136, 0.2);
  border-color: #009688;
}

.search-input .el-input__wrapper:focus-within {
  box-shadow: 0 0 0 2px rgba(0, 150, 136, 0.3);
  border-color: #009688;
  background-color: #ffffff;
}

:deep(.el-input__icon) {
  color: #009688;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border-color: #009688;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 150, 136, 0.2);
}

:deep(.el-button--success:hover) {
  background: linear-gradient(135deg, #009688 0%, #00796b 100%);
  border-color: #00796b;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
}

.actions {
  display: flex;
  gap: 8px;
}

.refresh-button {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  color: #ffffff;
  border: none;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 150, 136, 0.2);
  transition: all 0.3s ease;
  font-weight: 500;
}

.refresh-button:hover {
  background: linear-gradient(135deg, #009688 0%, #00796b 100%);
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
  transform: translateY(-1px);
}

.refresh-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(0, 150, 136, 0.25);
}

.refresh-button.is-loading {
  background: #9e9e9e;
  cursor: not-allowed;
  box-shadow: 0 2px 8px rgba(158, 158, 158, 0.2);
}

.post-list-body {
  padding: 16px 20px;
  height: calc(100vh - 200px);
  overflow-y: auto;
  position: relative;
  scroll-behavior: smooth;
}

/* 自定义滚动条样式 */
.post-list-body::-webkit-scrollbar {
  width: 6px;
}

.post-list-body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.post-list-body::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
  transition: background 0.2s ease;
}

.post-list-body::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.items {
  display: grid;
  gap: 12px;
}

.post-list-footer {
  padding: 12px 20px;
  border-top: 1px solid #f3f4f6;
}

/* 滚动顶部提示样式 */
.scroll-top-hint {
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  text-align: center;
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.scroll-top-hint:hover {
  opacity: 1;
}

.refresh-icon {
  font-size: 20px;
  color: #409eff;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.refresh-icon:hover {
  transform: rotate(180deg);
}

/* 加载更多样式 */
.loading-more {
  padding: 16px 0;
  text-align: center;
  transition: all 0.3s ease;
}

.loading-text {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
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

/* 无更多数据样式 */
.no-more {
  padding: 16px 0;
  transition: all 0.3s ease;
}

:deep(.el-divider__text) {
  color: #9ca3af;
  font-size: 13px;
}
</style>