import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
  id: number | null
  userName: string
  token: string
  avatar?: string | null
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
    token.value = ''
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
  function setToken(t: string) { token.value = t }
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
    triggerRefresh
  }
}, {
  persist: true
})