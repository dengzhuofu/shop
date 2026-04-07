import { useCookie, useRouter } from '#app'
import { useMessage } from '~/composables/useMessage'
import { useShopLocale } from '~/composables/useShopLocale'

export const useHttp = async (url: string, options: any = {}) => {
  const token = useCookie('token')
  const langCookie = useCookie('lang')
  const router = useRouter()
  const config = useRuntimeConfig()
  const { success, error } = useMessage()
  
  // NOTE: useShopLocale() uses useState and must be called during setup/component init.
  // When useHttp is called inside a component setup, this will work.
  // For safety in async contexts, we fallback to English/Chinese if it's not available.
  let t: (key: string) => string
  try {
    const locale = useShopLocale()
    t = locale.t as (key: string) => string
  } catch (e) {
    t = (key: string) => {
      const isZh = langCookie.value === 'zh'
      if (key === 'requestFailed') return isZh ? '请求失败' : 'Request failed'
      if (key === 'networkError') return isZh ? '网络错误，请稍后重试' : 'Network error, please try again later'
      return key
    }
  }

  const requestUrl =
    process.server && typeof url === 'string' && url.startsWith('/api')
      ? `${config.internalApiBase}${url.replace(/^\/api/, '')}`
      : url

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
      const res = response._data
      
      // 处理业务逻辑错误 (例如 code 不等于 200)
      if (res && res.code && res.code !== 200) {
        // 排除某些特定情况如果需要的话，或者统一报出
        if (process.client && options.showError !== false) {
          error(res.message || options.errorMsg || t('requestFailed'))
        }
      } else {
        // 成功时如果需要提示
        if (process.client && options.successMsg) {
          success(options.successMsg)
        }
      }
    },
    // 错误拦截器
    onResponseError({ request, response, options }: any) {
      if (process.client && options.showError !== false) {
        const errorMsg = response._data?.message || options.errorMsg || t('networkError')
        error(errorMsg)
      }
      
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
  return await $fetch(requestUrl, { ...defaultOptions, ...options })
}
