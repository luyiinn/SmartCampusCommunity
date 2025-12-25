import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export interface UserInfo {
  id: number | null
  userName: string
  token: string
  avatar?: string | null
  email?: string
  phone?: string
  studentId?: number
  createdAt?: string
  updatedAt?: string
}

export const useUserStore = defineStore('user', () => {
  const user = ref<UserInfo>({ id: null, userName: '',avatar:'', token: '' })
  const token = computed(() => user.value.token || '')
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])
  const loggedIn = ref(false)
  const rememberedUser = ref<null | { username: string; id: number }>(null)
  const authModalVisible = ref(false)
  const pendingRoute = ref<string | null>(null)
  // 用于触发页面刷新的状态标志
  const refreshFlag = ref(0)

  const isLoggedIn = computed(() => loggedIn.value || !!user.value.id)
  const displayName = computed(() => user.value.userName || '')

  function loginSuccess(payload: { id: number; userName: string; token?: string; remember?: boolean; avatar?: string | null }) {
    // 优化头像URL处理：检查是否为空、是否为完整URL、是否为uploads路径或是否已包含/api前缀
    let avatarUrl: string | null = null;
    if (payload.avatar) {
      // 检查是否已经是完整URL
      if (payload.avatar.startsWith('http://') || payload.avatar.startsWith('https://')) {
        avatarUrl = payload.avatar;
      } 
      // 优先处理/uploads开头的路径，确保直接使用而不添加/api前缀
      else if (payload.avatar.startsWith('/uploads')) {
        avatarUrl = payload.avatar;
      }
      // 检查是否是uploads开头（没有前导斜杠）
      else if (payload.avatar.startsWith('uploads')) {
        avatarUrl = `/${payload.avatar}`;
      }
      // 处理/api开头的路径
      else if (payload.avatar.startsWith('/api')) {
        avatarUrl = payload.avatar;
      }
      else {
        // 对于其他情况，添加/api前缀并处理斜杠
        avatarUrl = `/api${payload.avatar.startsWith('/') ? '' : '/'}${payload.avatar}`;
      }
    }
    
  user.value = { 
    id: payload.id, 
    userName: payload.userName, 
    token: payload.token || '', 
    avatar: avatarUrl 
  }
  loggedIn.value = true
  rememberedUser.value = payload.remember ? { username: payload.userName, id: payload.id } : null
    
    // 登录成功后触发页面刷新
    triggerRefresh()
  }

  function logout() {
    user.value = { id: null, userName: '', token: '' ,avatar:'',}
    loggedIn.value = false
    permissions.value = []
    roles.value = []
    pendingRoute.value = null
    authModalVisible.value = false
    
    // 退出登录后触发页面刷新
    triggerRefresh()
  }
  
  // 触发页面刷新的方法
  function triggerRefresh() {
    refreshFlag.value++
  }

  function setPermissions(perms: string[]) { permissions.value = perms }
  function setRoles(rs: string[]) { roles.value = rs }
  function setToken(t: string) { user.value.token = t }
  function requireLogin(redirectTo?: string) {
    authModalVisible.value = true
    if (redirectTo) pendingRoute.value = redirectTo
  }
  function setAuthModalVisible(v: boolean) { authModalVisible.value = v }
  function consumePendingRoute(): string | null {
    const r = pendingRoute.value
    pendingRoute.value = null
    return r
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!user.value.id) return
    
    try {
      const response = await axios.get(`/user/${user.value.id}`, {
        headers: {
          token: ` ${token.value}`,
        },
        timeout: 10000,
      })

      if (response.data && response.data.code === 1 && response.data.data) {
        const userData = response.data.data
        return userData
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 上传图片
  async function uploadImage(file: File | undefined) {
    if (!file) {
      throw new Error("文件不存在")
    }
    try {
      const formData = new FormData()
      formData.append("file", file)

      // 调用上传接口
      const response = await axios.post("/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
        timeout: 10000,
      })

      // 从响应中提取URL并返回
      if (response.data && response.data.url) {
        let url = response.data.url

        // 处理URL格式
        if (url.startsWith("http://") || url.startsWith("https://")) {
          // 完整URL保持不变
          return url
        } else if (url.startsWith("/uploads")) {
          // 已经是正确的/uploads开头路径
          return url
        } else if (url.startsWith("uploads")) {
          // 添加前导斜杠
          return `/${url}`
        } else {
          // 对于其他情况，确保以/uploads开头
          return url.includes("uploads")
            ? url.startsWith("/")
              ? url
              : `/${url}`
            : `/uploads/${url}`
        }
      } else {
        throw new Error("上传成功但未返回图片URL")
      }
      
    } catch (error) {
      console.error("图片上传失败:", error)
      throw new Error((error as any)?.response?.data?.message || "图片上传失败，请重试")
    }
  }

  // 更新用户信息
  async function updateUserInfo(userData: Partial<Omit<UserInfo, 'id' | 'token'>>) {
    if (!user.value.id) return false
    
    try {
      const response = await axios.put('/user', 
        { id: user.value.id, username: userData.userName, ...userData },
        {
          headers: {
            token: ` ${token.value}`,
          },
          timeout: 10000,
        }
      )

      if (response.data && response.data.code === 1) {
        user.value = { ...user.value, ...userData }
        return true
      }
      return false
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  return { 
    user, 
    token, 
    roles, 
    permissions, 
    loggedIn, 
    rememberedUser, 
    authModalVisible, 
    pendingRoute, 
    isLoggedIn, 
    displayName, 
    loginSuccess, 
    logout, 
    setPermissions, 
    setRoles, 
    setToken, 
    requireLogin, 
    setAuthModalVisible, 
    consumePendingRoute,
    refreshFlag,
    triggerRefresh,
    fetchUserInfo,
    updateUserInfo,
    uploadImage
  }
}, {
  persist: true
})