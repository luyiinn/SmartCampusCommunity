<template>
  <el-card class="diary-list-card" shadow="hover">
    <header class="diary-list-header">
      <div class="header-left">
        <h2 class="title">{{ title }}</h2>
      </div>
      <div class="search-container">
        <el-input v-model="searchText" placeholder="搜索日记..." :prefix-icon="Search" class="search-input" clearable
          @clear="handleClearSearch" @keyup.enter="handleSearch" @input="handleInput">
        </el-input>
        <el-button type="primary" :icon="Search" @click="handleSearch" circle class="search-button"></el-button>
      </div>
    </header>

    <section class="diary-list-body" ref="listBodyRef" @scroll="handleScroll">
      <slot name="default" :items="items">
        <el-empty v-if="!loading && items.length === 0" description="暂无日记"></el-empty>

        <div v-else class="items-container">
          <DiaryCard v-for="item in items" :key="item.id" :diary="item" @click="emitSelect(item)"
            @update:like-status="handleLikeStatusUpdate" />
        </div>

        <!-- 初始加载中 -->
        <el-skeleton v-if="loading && items.length === 0" :rows="4" animated class="mt-4" />
      </slot>

      <!-- 加载更多状态 -->
      <div v-if="loadingMore" class="loading-more">
        <el-skeleton :rows="1" animated style="margin: 16px 0" />
        <div class="loading-text">加载中...</div>
      </div>

      <!-- 无更多数据提示 -->
      <div v-else-if="!hasMore && items.length > 0" class="no-more">
        <el-divider>没有更多内容了</el-divider>
      </div>
    </section>
  </el-card>
</template>

<script lang="ts" setup>
import { computed, ref } from "vue";
import { Search } from "@element-plus/icons-vue";
import DiaryCard, { type DiaryData } from "./DiaryCard.vue";
import debounce from "lodash/debounce"; // 假设项目有lodash，如果没有则手写debounce

const props = defineProps<{
  title?: string;
  items?: DiaryData[];
  loading?: boolean;
  hasMore?: boolean;
  loadingMore?: boolean;
}>();

const emit = defineEmits<{
  search: [string];
  loadMore: [];
  select: [DiaryData];
  "update:like-status": [id: number, isLike: number];
}>();

const searchText = ref("");
const listBodyRef = ref<HTMLElement>();
const title = computed(() => props.title ?? "日记列表");
const items = computed(() => props.items ?? []);

// 搜索处理
const handleSearch = () => {
  emit("search", searchText.value);
};

const handleClearSearch = () => {
  searchText.value = "";
  emit("search", "");
};

// 防抖处理输入，避免频繁请求 (如果父组件是实时搜索)
// 这里主要是为了用户体验，但实际请求由handleSearch触发（回车或按钮）
// 如果用户希望输入即搜索，可以解开下面的注释
const handleInput = () => {
  // 可以在这里做防抖搜索，或者不做，仅依赖回车/按钮
};

// 滚动加载
const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement;
  if (props.loadingMore || !props.hasMore) return;

  const { scrollTop, scrollHeight, clientHeight } = target;
  // 距离底部50px触发加载
  if (scrollTop + clientHeight >= scrollHeight - 50) {
    emit("loadMore");
  }
};

const emitSelect = (item: DiaryData) => {
  emit("select", item);
};

const handleLikeStatusUpdate = (id: number, isLike: number) => {
  emit("update:like-status", id, isLike);
};
</script>

<style scoped>
.diary-list-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 600px;
  /* 固定高度或者由父容器决定，这里给个默认高度以便滚动 */
}

:deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.diary-list-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: 300px;
  flex: 1;
  justify-content: flex-end;
}

.search-input {
  width: 100%;
}

/* 搜索按钮样式，与项目风格一致的绿色 */
.search-button {
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.25);
  transition: all 0.2s ease;
}

.search-button:hover {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%);
  transform: translateY(-1px);
}

.diary-list-body {
  padding: 16px 20px;
  flex: 1;
  overflow-y: auto;
  scroll-behavior: smooth;
}

/* 滚动条样式 */
.diary-list-body::-webkit-scrollbar {
  width: 6px;
}

.diary-list-body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.diary-list-body::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.diary-list-body::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.items-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.loading-more,
.no-more {
  text-align: center;
  padding: 10px 0;
}

.loading-text {
  color: #909399;
  font-size: 14px;
}

.mt-4 {
  margin-top: 16px;
}
</style>
