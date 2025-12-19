<template>
  <div class="tag-nav">
    <div class="card">
      <h2>
        <slot name="title">标签导航</slot>
      </h2>
      <div class="tags">
        <slot>
          <template v-if="!tagStore.loading">
            <el-tag v-for="t in tagStore.allTags" :key="t.id" :type="tagStore.selectedTagId === t.id ? 'success' : ''"
              class="tag-item" @click="onSelect(t)">{{ t.name }}</el-tag>
          </template>
          <template v-else>
            <el-tag type="info" disabled>加载中...</el-tag>
          </template>
          <template v-if="tagStore.error">
            <el-tag type="danger" class="error-tag" @click="retryFetchTags">
              加载失败，点击重试
            </el-tag>
          </template>
        </slot>
      </div>
      <div class="extra">
        <slot name="extra"></slot>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from "vue";
import { useTagStore } from "../stores/tagStore";
import type { Tag } from "../stores/tagStore";

const tagStore = useTagStore();
const emit = defineEmits<{
  change: [number, Tag];
}>();

// 组件挂载时获取标签数据
onMounted(() => {
  if (tagStore.allTags.length === 0) {
    tagStore.fetchTags();
  }
});

// 重试获取标签数据
const retryFetchTags = () => {
  tagStore.clearError();
  tagStore.fetchTags(true); // 强制刷新
};

const onSelect = (t: Tag) => {
  // 更新store中的选中状态
  tagStore.setSelectedTag(t.id);
  // 同时发出事件通知父组件
  emit("change", t.id, t);
};
</script>

<style scoped>
.tag-nav {
  position: sticky;
  top: 16px;
  align-self: start;
}

.card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  max-height: calc(100vh - 80px);
  overflow-y: auto;
  box-sizing: border-box;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag-item:hover {
  transform: translateY(-1px);
}

.extra {
  margin-top: 12px;
}

.error-tag {
  cursor: pointer;
}

.error-tag:hover {
  background-color: var(--el-color-danger-light-9);
}
</style>