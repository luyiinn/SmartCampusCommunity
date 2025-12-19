import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // find: '@',
      // replacement: resolve(__dirname, './src'),
      "@": resolve(__dirname, "src"),
      "@stores": resolve(__dirname, "src/stores"),
      "@components": resolve(__dirname, "src/components"),
      "@assets": resolve(__dirname, "src/assets"),
    },
  },
  server: {
    port: 3001,
    hmr: true,
    host: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080/",
        changeOrigin: true,
      },
      // 优化上传文件目录的代理配置，确保头像文件能够被正确访问
      "/uploads": {
        target: "http://localhost:8080/",
        changeOrigin: true,
        // 直接代理到后端的uploads目录，保持路径不变
        // 例如：/uploads/xxx.png -> http://localhost:8080/uploads/xxx.png
      },
    },
    allowedHosts: ['frp-act.com'],
  },
});
