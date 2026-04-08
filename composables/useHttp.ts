import { useCookie, useRouter } from '#app'
import { useMessage } from '~/composables/useMessage'
import { useShopLocale } from '~/composables/useShopLocale'

const AUTH_ERROR_COOLDOWN_MS = 1500

let lastAuthErrorAt = 0

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

  const getSessionExpiredMessage = () =>
    langCookie.value === 'zh' ? '登录已失效，请重新登录' : 'Login expired, please sign in again'

  const notifySessionExpired = () => {
    const now = Date.now()
    if (now - lastAuthErrorAt < AUTH_ERROR_COOLDOWN_MS) {
      return
    }

    lastAuthErrorAt = now
    error(getSessionExpiredMessage())
  }

  const handleUnauthorized = (showError: boolean) => {
    token.value = null

    if (!process.client) {
      return
    }

    if (showError) {
      notifySessionExpired()
    }

    if (router.currentRoute.value.path !== '/login') {
      router.replace('/login')
    }
  }

  const requestUrl =
    process.server && typeof url === 'string' && url.startsWith('/api')
      ? `${config.internalApiBase}${url.replace(/^\/api/, '')}`
      : url

  const defaultOptions = {
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

      if (token.value) {
        options.headers.Authorization = `${token.value}`
      }
    },
    onResponse({ response, options }: any) {
      const res = response._data

      if (res && res.code && res.code !== 200) {
        if (res.code === 401 && options.handleAuthError !== false) {
          handleUnauthorized(options.showError !== false)
          return
        }

        if (process.client && options.showError !== false) {
          error(res.message || options.errorMsg || t('requestFailed'))
        }
        return
      }

      if (process.client && options.successMsg) {
        success(options.successMsg)
      }
    },
    onResponseError({ response, options }: any) {
      if ((response.status === 401 || response._data?.code === 401) && options.handleAuthError !== false) {
        handleUnauthorized(options.showError !== false)
        return
      }

      if (process.client && options.showError !== false) {
        const errorMsg = response._data?.message || options.errorMsg || t('networkError')
        error(errorMsg)
      }
    },
  }

  return await $fetch(requestUrl, { ...defaultOptions, ...options })
}
