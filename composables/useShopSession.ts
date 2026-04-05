import { computed } from 'vue'

type AuthUser = {
  id: number
  email: string
  firstName: string
  lastName: string
  nickname?: string
  fullName?: string
}

export function useShopSession() {
  const token = useCookie<string | null>('token', {
    default: () => null,
    maxAge: 60 * 60 * 24 * 7,
  })
  const user = useState<AuthUser | null>('shop-user', () => null)
  const isLoading = useState('shop-session-loading', () => false)

  const fetchMe = async () => {
    if (!token.value) {
      user.value = null
      return null
    }

    if (isLoading.value) {
      return user.value
    }

    isLoading.value = true
    try {
      const res = await useHttp('/api/auth/me')
      if (res?.code === 200) {
        user.value = res.data
      }
      return user.value
    } catch (error) {
      token.value = null
      user.value = null
      return null
    } finally {
      isLoading.value = false
    }
  }

  const applyAuthPayload = (payload: any) => {
    token.value = payload?.tokenValue || null
    user.value = payload?.user || null
  }

  const logout = async () => {
    try {
      if (token.value) {
        await useHttp('/api/auth/logout', { method: 'POST' })
      }
    } catch (error) {
      // Ignore logout transport failures and clear local state anyway.
    } finally {
      token.value = null
      user.value = null
    }
  }

  return {
    token,
    user,
    isLoading: computed(() => isLoading.value),
    isLoggedIn: computed(() => Boolean(token.value)),
    fetchMe,
    applyAuthPayload,
    logout,
  }
}
