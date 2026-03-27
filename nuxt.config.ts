// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  // 部署到 GitHub Pages 的基础路径配置
  // 如果你的仓库名叫 my-project，且通过 username.github.io/my-project 访问
  // 那么你应该在部署时设置 NUXT_APP_BASE_URL=/my-project/
  app: {
    baseURL: process.env.NUXT_APP_BASE_URL || '/',
    buildAssetsDir: '/_nuxt/',
  },

  // 避免静态生成 (generate) 时由于死链或 API 报错导致部署直接失败
  nitro: {
    prerender: {
      failOnError: false,
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
    }
  },
  compatibilityDate: '2024-11-01',
})
