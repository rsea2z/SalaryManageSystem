import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 导入 path 模块

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src') // 设置 @ 别名指向 src 目录
    }
  },
  server: {
    host: '127.0.0.1', // 添加此行来指定监听 IPv4 localhost
    port: 3000, // 如果5173端口持续有问题，可以取消注释并尝试一个不同的端口
  }
})
