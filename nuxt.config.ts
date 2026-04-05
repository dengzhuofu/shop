// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  // 部署到 GitHub Pages 的基础路径配置
  // 如果你的仓库名叫 my-project，且通过 username.github.io/my-project 访问
  // 那么你应该在部署时设置 NUXT_APP_BASE_URL=/my-project/
  app: {
    baseURL: process.env.NUXT_APP_BASE_URL || '/',
    buildAssetsDir: '/_nuxt/',
    head: {
      link: [
        { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
        { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,400&family=Poppins:wght@300;400;500;600;700;800&display=swap' }
      ]
    }
  },

  // 避免静态生成 (generate) 时由于死链或 API 报错导致部署直接失败
  nitro: {
    prerender: {
      failOnError: false,
    }
  },

  runtimeConfig: {
    internalApiBase: process.env.NUXT_INTERNAL_API_BASE || 'http://localhost:8081',
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/api',
    },
  },

  devtools: { enabled: true },
  modules: [
    '@pinia/nuxt'
  ],
  css: [
    '@/assets/styles/main.scss'
  ],
  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@use "@/assets/styles/_variables.scss" as *;'
        }
      }
    },
    server: {
      proxy: {
        '/api': {
          target: process.env.NUXT_DEV_API_TARGET || 'http://localhost:8081',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      }
    }
  },
  compatibilityDate: '2024-11-01',
})
