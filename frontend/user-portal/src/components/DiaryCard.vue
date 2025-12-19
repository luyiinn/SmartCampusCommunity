<template>
    <div class="diary-card" @click="handleCardClick">
        <!-- 日志内容区域 -->
        <div class="diary-content">
            <h2 class="diary-title">{{ diary.title }}</h2>
            <p class="diary-text">{{ diary.contentSummary }}</p>

            <!-- 图片展示区域 -->
            <div v-if="diary.images && diary.images.length > 0" class="diary-images">
                <div v-for="(image, index) in diary.images" :key="index" class="diary-image-container">
                    <img :src="getAvatarUrl(image)" :alt="`图片 ${index + 1}`" class="diary-image"
                        @error="handleImageError"
                        @click.stop="image && getAvatarUrl(image) && viewImage(getAvatarUrl(image)!)" />
                </div>
            </div>
        </div>

        <!-- 底部信息区域：时间和统计 -->
        <div class="diary-footer">
            <div class="diary-time">{{ diary.createdAt }}</div>

            <div class="diary-stats">
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
                    <span class="stat-text">{{ diary.viewCount || 0 }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch, h } from "vue";
import { ElIcon, ElImage, ElMessageBox, ElMessage } from "element-plus";
import { Star, View } from "@element-plus/icons-vue";
import { useUserStore } from "../stores/userStore";
import axios from "axios";

// 日志数据接口
export interface DiaryData {
    id: number;
    title: string;
    contentSummary: string;
    status?: any;
    viewCount: number;
    likeCount: number;
    createdAt: string;
    isLike: number; // 0: 未点赞, 1: 已点赞
    images: string[];
}

const props = defineProps<{
    diary: DiaryData;
}>();

const emit = defineEmits<{
    click: [DiaryData];
    "update:like-status": [id: number, isLike: number];
}>();

const userStore = useUserStore();

// 本地状态用于乐观更新
const localLikeStatus = ref({
    isLike: props.diary.isLike || 0,
    likeCount: props.diary.likeCount || 0,
});

// 监听props变化
watch(
    () => props.diary,
    (newVal) => {
        localLikeStatus.value = {
            isLike: newVal.isLike || 0,
            likeCount: newVal.likeCount || 0,
        };
    },
    { deep: true }
);

// 处理图片URL
const getAvatarUrl = (url?: string): string | undefined => {
    if (!url) return undefined;
    if (url.startsWith("http://") || url.startsWith("https://")) return url;
    if (url.startsWith("/uploads")) return url;
    if (url.startsWith("uploads")) return `/${url}`;
    return `/uploads/${url}`;
};

// 图片加载错误
const handleImageError = (event: Event) => {
    if (event.target instanceof HTMLImageElement) {
        event.target.src = "/src/assets/placeholders/error.png";
        event.target.alt = "图片加载失败";
    }
};

// 查看大图
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
    }).catch(() => { });
};

// 点击卡片
const handleCardClick = () => {
    emit("click", props.diary);
};

// 点赞/取消点赞
const toggleLike = async () => {
    if (!userStore.isLoggedIn) {
        ElMessage.warning("请先登录后再点赞");
        return;
    }

    const oldIsLike = localLikeStatus.value.isLike;
    const oldLikeCount = localLikeStatus.value.likeCount;

    // 乐观更新
    const newIsLike = oldIsLike === 1 ? 0 : 1;
    localLikeStatus.value.isLike = newIsLike;
    localLikeStatus.value.likeCount += newIsLike === 1 ? 1 : -1;

    try {
            // 调用日记点赞接口
            const response = await axios.post(`/diary/like/${props.diary.id}`, null, {
                headers: { token: ` ${userStore.token}` }
            });

            if (response.data.code !== 1) {
                throw new Error(response.data.message);
            }

            emit("update:like-status", props.diary.id, newIsLike);

        } catch (error) {
        // 回滚
        localLikeStatus.value.isLike = oldIsLike;
        localLikeStatus.value.likeCount = oldLikeCount;
        ElMessage.error("操作失败");
    }
};
</script>

<style scoped>
.diary-card {
    background: #ffffff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #ebeef5;
}

.diary-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.diary-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px;
    line-height: 1.4;
}

.diary-text {
    font-size: 14px;
    color: #606266;
    margin: 0 0 12px;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.diary-images {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    margin-bottom: 12px;
}

.diary-image-container {
    aspect-ratio: 1;
    border-radius: 4px;
    overflow: hidden;
    background-color: #f5f7fa;
}

.diary-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
}

.diary-image:hover {
    transform: scale(1.05);
}

.diary-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
}

.diary-time {
    font-size: 12px;
    color: #909399;
}

.diary-stats {
    display: flex;
    gap: 16px;
}

.stat-item {
    display: flex;
    align-items: center;
    color: #909399;
    font-size: 13px;
    cursor: pointer;
    transition: color 0.3s;
}

.stat-item:hover {
    color: #409eff;
}

.stat-icon {
    margin-right: 4px;
    font-size: 16px;
}

.liked {
    color: #e6a23c;
}

.liked-text {
    color: #e6a23c;
}
</style>
