import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

// 标签接口定义
export interface Tag {
  id: number
  name: string
  value?: string // 用于PostPublishDialog中的标签值
  label?: string // 用于PostPublishDialog中的标签显示文本
}

// 标签Store
export const useTagStore = defineStore('tag', () => {
  // 状态定义
  const tags = ref<Tag[]>([])
  const selectedTagId = ref<number>(0) // 默认选中全部标签
  const loading = ref<boolean>(false)
  const error = ref<string | null>(null)
  const lastFetchTime = ref<number | null>(null)
  const cacheDuration = 5 * 60 * 1000 // 缓存时间：5分钟

  // 计算属性
  const allTags = computed(() => {
    // 确保总是包含"全部"标签作为第一个选项
    if (tags.value && tags.value.length > 0) {
      const firstTag = tags.value[0];
      if (firstTag && firstTag.id !== 0) {
        return [{ id: 0, name: '全部' }, ...tags.value]
      }
    }
    return tags.value || []
  })

  const selectedTag = computed(() => {
    return allTags.value.find(tag => tag.id === selectedTagId.value) || allTags.value[0]
  })

  const publishTagOptions = computed(() => {
    // 使用从API获取的标签数据生成发布帖子对话框的标签选项
    // 过滤掉"全部"标签(id=0)，并将标签格式转换为label-value格式
    const apiTags = tags.value
      .filter(tag => tag.id !== 0) // 排除"全部"标签
      .map(tag => ({
        label: tag.name,
        value: tag.name.toLowerCase().replace(/\s+/g, '-') // 将标签名转换为小写并替换空格为连字符
      }))
    
    // 如果从API获取的标签数据为空，提供默认标签作为备用
    // if (apiTags.length === 0) {
    //   return [
    //     { label: "校园动态", value: "campus" },
    //     { label: "学习交流", value: "study" },
    //     { label: "生活分享", value: "life" },
    //     { label: "求助问答", value: "help" },
    //     { label: "活动通知", value: "activity" },
    //     { label: "二手交易", value: "secondhand" },
    //     { label: "吐槽讨论", value: "discussion" },
    //     { label: "其他", value: "other" },
    //   ]
    // }
    
    return apiTags
  })

  // Actions
  async function fetchTags(forceRefresh = false) {
    // 检查是否需要刷新数据（缓存过期或强制刷新）
    const shouldRefresh = forceRefresh || 
                         !lastFetchTime.value || 
                         (Date.now() - lastFetchTime.value > cacheDuration)

    if (!shouldRefresh && tags.value.length > 0) {
      return // 使用缓存数据
    }

    loading.value = true
    error.value = null

    try {
      const res = await axios.get('/tag/list')
      const list = Array.isArray(res?.data?.data?.tags) ? res.data.data.tags : []
      const items = list.map((t: { id: number; name: string }) => ({ id: t.id, name: t.name }))
      
      // 确保包含"全部"标签
      if (items.length > 0 && items[0].id !== 0) {
        tags.value = [{ id: 0, name: '全部' }, ...items]
      } else {
        tags.value = items
      }
      
      lastFetchTime.value = Date.now()
    } catch (err) {
      console.error('获取标签列表失败:', err)
      error.value = '获取标签列表失败，请稍后重试'
      
      // 如果之前有缓存数据，继续使用缓存
      if (tags.value.length === 0) {
        // 如果没有缓存，使用默认数据
        tags.value = [{ id: 0, name: '全部' }]
      }
    } finally {
      loading.value = false
    }
  }

  function setSelectedTag(tagId: number) {
    selectedTagId.value = tagId
  }

  function resetSelectedTag() {
    selectedTagId.value = 0 // 重置为选中全部标签
  }

  function clearCache() {
    lastFetchTime.value = null
  }

  function clearError() {
    error.value = null
  }

  // 初始化函数，可在应用启动时调用
  async function initialize() {
    await fetchTags()
  }

  return {
    // 状态
    tags,
    selectedTagId,
    loading,
    error,
    
    // 计算属性
    allTags,
    selectedTag,
    publishTagOptions,
    
    // Actions
    fetchTags,
    setSelectedTag,
    resetSelectedTag,
    clearCache,
    clearError,
    initialize
  }
})