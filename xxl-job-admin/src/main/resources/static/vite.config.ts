// vite.config.ts 配置参考 https://cn.vitejs.dev/config/

import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import autoprefixer from 'autoprefixer'
import vuetify from 'vite-plugin-vuetify'
// 预加载插件
import Components from 'unplugin-vue-components/vite';
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  const rumPlugin = {
    name: 'inject-rum',
    transformIndexHtml(html: string) {
      if (env.VITE_RUM_ENABLED !== 'true') return html
      const rumScript = `!(function(w,d,s,r){w['rum_queue']=w['rum_queue']||[];w[r]||(w[r]={});w[r].config={appId:${env.VITE_RUM_APP_ID},agentId:'${env.VITE_RUM_AGENT_ID}',userMark:'${env.VITE_RUM_USER_MARK}',reportUrl:'${env.VITE_RUM_REPORT_URL}',disableHook:false,disableResource:false,disableFmp:false,clickScope:'part',disableConsole:true,globalDisabled:false,rate:1,enableFullLink:false,enableApiCors:false,enableCatonFps:false,catonFpsThreshold:20};var dom=d.createElement("script");dom.setAttribute("crossorigin","anonymous");dom.setAttribute("src",s);var nd=d.getElementsByTagName("script")[0];nd.parentNode.insertBefore(dom,nd);})(window,document,'${env.VITE_RUM_JS_URL}',"__rum");`
      return [
        { tag: 'script', children: rumScript, injectTo: 'head-prepend' as const }
      ]
    }
  }

  return {
  base: './',
  plugins: [
    // 预加载组件
    Components({
      resolvers: [ElementPlusResolver()]
    }),
    vue(),
    vueDevTools(),
    vuetify({autoImport: true}),
    rumPlugin,
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    postcss: {
      plugins: [autoprefixer()] // 自动加浏览器前缀
    }
  },
  // 预构建依赖
  optimizeDeps: {
    include: ['lodash-es', 'element-plus/es/']
  },
  build: {
    outDir: 'xxl-job-ui',
    emptyOutDir: true,
    // 根据项目实际需求调整（单位：kb）
    chunkSizeWarningLimit: 1024,
    rollupOptions: {
      output: {
        // 统一字体文件名格式
        assetFileNames: 'assets/[name]-[hash][extname]',
        // 分模块打包，减少单个文件的体积
        manualChunks: {
          // 核心框架合并
          libs: ['vue', 'vue-router', 'pinia'],
          element: ['element-plus'],
          // 工具库合并
          utils: ['lodash-es', 'axios', 'echarts']
        }
      }
    }
  },
  server: {
    // 设置开发服务器端口
    port: 3000,
    // 启动时自动打开浏览器
    open: true,
    proxy: {
      '/dev': {
        // 代理目标地址
        target: 'http://localhost:30635',
        // 是否改变源地址
        changeOrigin: true,
        // 如果是https接口，需要配置这个参数
        secure: false,
        // 重写路径
        rewrite: (path) => path.replace(/^\/dev/, '')
      }
    }
  }
  }
})
