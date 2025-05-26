import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite' // 导入
import Components from 'unplugin-vue-components/vite' // 导入
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers' // 导入
import viteCompression from 'vite-plugin-compression'; // 导入插件

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    viteCompression({ // 添加插件配置
      verbose: true, // 是否在控制台输出压缩结果
      disable: false, // 是否禁用
      threshold: 10240, // 文件大小大于 10kb 才进行压缩 (单位: bytes)
      algorithm: 'gzip', // 压缩算法, 可选 [ 'gzip' , 'brotliCompress' ,'deflate' , 'deflateRaw']
      ext: '.gz', // 生成的压缩包后缀
    }),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
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
