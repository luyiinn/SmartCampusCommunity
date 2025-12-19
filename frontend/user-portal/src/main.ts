import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

//使用路由
app.use(router)

// 使用Element Plus
app.use(ElementPlus)
// 使用 Pinia
app.use(pinia)

axios.defaults.baseURL = '/api'
axios.defaults.timeout = 15000

app.mount('#app')
