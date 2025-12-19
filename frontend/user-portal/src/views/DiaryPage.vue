<template>
  <div class="square-page">
    <div class="square-header">
      <div class="header-content">
        <h1>个人日志</h1>
        <p class="subtitle">记录每日写作情况与日志列表</p>
      </div>
      <el-button type="primary" class="publish-btn" @click="showPublishDialog"
        >发布日志</el-button
      >
    </div>

    <!-- 日志热力图日历 -->
    <HeatmapCalendar :hasDataDates="hasDataDates" @update:year="updateYear" />

    <div class="diary-list-container">
      <DiaryList
        :items="diaryList"
        :loading="loading"
        :has-more="hasMore"
        :loading-more="loadingMore"
        @search="handleSearch"
        @load-more="handleLoadMore"
        @select="handleSelectDiary"
        @update:like-status="handleLikeStatusUpdate"
      />
    </div>

    <!-- 发布日志对话框 -->
    <DiaryPublishDialog v-model:visible="publishDialogVisible" />
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import HeatmapCalendar from "../components/HeatmapCalendar.vue";
import DiaryList from "../components/DiaryList.vue";
import DiaryPublishDialog from "../components/DiaryPublishDialog.vue";
import type { DiaryData } from "../components/DiaryCard.vue";
import { useUserStore } from "../stores/userStore";
import axios from "axios";
import { ElMessage } from "element-plus";

const userStore = useUserStore();
const route = useRoute();

// 发布日志对话框控制
const publishDialogVisible = ref(false);

// 显示发布日志对话框
const showPublishDialog = () => {
  publishDialogVisible.value = true;
};

// 日志列表数据
const diaryList = ref<DiaryData[]>([]);
const loading = ref(false);
const loadingMore = ref(false);
const hasMore = ref(true);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref("");

// 监听路由参数变化，当用户状态变化导致页面刷新时重新获取数据
watch(
  () => route.query._t,
  () => {
    console.log("检测到用户状态变化，刷新日志页面");
    // 重新获取数据
    loadHasDataDates();
    resetAndFetchDiaries();
  }
);

// 从API获取已写日志日期数据
const generateHasDataDates = async (year?: number) => {
  try {
    const targetYear = year || new Date().getFullYear();

    const response = await axios.get("/diary/dates", {
      params: {
        year: targetYear,
      },
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    // 根据API响应格式：code为1表示成功，0表示失败
    if (response.data.code === 1) {
      // 成功时，返回data.dates中的日期列表
      return response.data.data?.dates || [];
    } else {
      // 失败时，显示错误信息
      console.error("获取日志日期数据失败:", response.data.message);
      ElMessage.error(response.data.message || "获取日志数据失败，请稍后重试");
      return [];
    }
  } catch (error) {
    console.error("获取日志日期数据失败:", error);
    ElMessage.error("获取日志数据失败，请稍后重试");
    return [];
  }
};

// 已写日志日期列表（格式：YYYY-MM-DD）
const hasDataDates = ref<string[]>([]);

// 加载日志日期数据的函数
const loadHasDataDates = async (year?: number) => {
  hasDataDates.value = await generateHasDataDates(year);
};

// 获取日志列表
const fetchDiaries = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true;
  } else {
    loading.value = true;
  }

  try {
    const requestData = {
      page: page.value,
      size: pageSize.value,
      userId: userStore.user.id,
      keyword: keyword.value,
    };

    // 发送请求到/diary/list接口 (假设接口名为/diary/list，根据上下文推断)
    // 根据用户描述的请求结构，这里使用GET请求
    const response = await axios.get("/diary/list", {
      params: requestData,
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    if (response.data.code === 1) {
      const data = response.data.data;
      total.value = data.total;

      const newItems = data.list || [];

      if (isLoadMore) {
        diaryList.value = [...diaryList.value, ...newItems];
      } else {
        diaryList.value = newItems;
      }

      // 判断是否还有更多数据
      hasMore.value = diaryList.value.length < total.value;
    } else {
      ElMessage.error(response.data.msg || "获取日志列表失败");
    }
  } catch (error) {
    console.error("获取日志列表失败:", error);
    ElMessage.error("获取日志列表失败，请稍后重试");
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

// 重置并重新获取日志
const resetAndFetchDiaries = () => {
  page.value = 1;
  hasMore.value = true;
  diaryList.value = []; // 清空列表
  fetchDiaries(false);
};

// 处理搜索
const handleSearch = (val: string) => {
  keyword.value = val;
  resetAndFetchDiaries();
};

// 处理加载更多
const handleLoadMore = () => {
  if (!loadingMore.value && hasMore.value) {
    page.value++;
    fetchDiaries(true);
  }
};

// 处理点击日志
const handleSelectDiary = (item: DiaryData) => {
  console.log("点击日志:", item);
  // 这里可以跳转到日志详情页，目前暂无需求
};

// 处理点赞状态更新
const handleLikeStatusUpdate = (id: number, isLike: number) => {
  const item = diaryList.value.find((d) => d.id === id);
  if (item) {
    item.isLike = isLike;
    item.likeCount += isLike === 1 ? 1 : -1;
  }
};

// 初始化加载数据
onMounted(() => {
  loadHasDataDates();
  resetAndFetchDiaries();
});

// 更新年份的处理函数
const updateYear = async (year: number) => {
  console.log(`切换到年份: ${year}`);
  // 加载对应年份的数据
  await loadHasDataDates(year);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.header-content {
  flex: 1;
}

.header-content > h1 {
  margin: 0;
  font-size: 22px;
  color: #0f766e;
}

.header-content > .subtitle {
  margin: 6px 0 0 0;
  font-size: 13px;
  color: #6b7280;
}

.publish-btn {
  font-weight: 600;
  background: linear-gradient(135deg, #00b09b 0%, #009688 100%) !important;
  color: #ffffff !important;
  border: none !important;
  border-radius: 10px !important;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.25) !important;
  transition: all 0.3s ease !important;
  flex: 0 0 auto;
  padding: 8px 20px !important;
  height: auto !important;
  line-height: 1.5 !important;
}

.publish-btn:hover {
  background: linear-gradient(135deg, #00a495 0%, #008c7e 100%) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 16px rgba(0, 150, 136, 0.35) !important;
}

.publish-btn:active {
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(0, 150, 136, 0.2) !important;
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

.diary-list-container {
  margin-top: 16px;
}
</style>