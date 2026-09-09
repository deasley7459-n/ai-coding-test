import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      // 开发环境下将 /api 请求代理到 Spring Boot 后端
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
