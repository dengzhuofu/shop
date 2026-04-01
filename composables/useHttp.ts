import { useCookie, useRouter } from '#app'

export const useHttp = async (url: string, options: any = {}) => {
  const token = useCookie('token')
  const langCookie = useCookie('lang')
  const router = useRouter()

  const defaultOptions = {
    // 请求拦截器
    onRequest({ request, options }: any) {
      const currentLang =
        langCookie.value ||
        (process.client && navigator.language?.toLowerCase().startsWith('en')
          ? 'en'
          : 'zh')
      options.headers = options.headers || {}
      options.headers['Accept-Language'] = currentLang
      if (
        typeof request === 'string' &&
        !request.includes('lang=') &&
        (!options.query || !options.query.lang)
      ) {
        options.query = { ...(options.query || {}), lang: currentLang }
      }
      // 自动携带 token，Sa-Token 默认获取请求头 Authorization 字段
      if (token.value) {
        options.headers.Authorization = `${token.value}`
      }
    },
    // 响应拦截器
    onResponse({ request, response, options }: any) {
      // 可以在这里统一处理响应格式，如果业务报错可以抛出异常等
    },
    // 错误拦截器
    onResponseError({ request, response, options }: any) {
      // 处理 401 未登录或 token 过期
      if (response.status === 401) {
        // 清除无效 token 并跳转到登录页
        token.value = null
        if (process.client) {
          router.push('/login')
        }
      }
    }
  }

  // 合并默认配置和传入配置
  return await $fetch(url, { ...defaultOptions, ...options })
}
