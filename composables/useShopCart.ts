import { computed } from 'vue'

export function useShopCart() {
  const items = useState<any[]>('shop-cart-items', () => [])
  const isOpen = useState('shop-cart-open', () => false)
  const loading = useState('shop-cart-loading', () => false)
  const session = useShopSession()

  const normalizeQuantity = (value: unknown) =>
    Math.max(1, Number.parseInt(String(value ?? 1), 10) || 1)

  const refreshCart = async () => {
    if (!session.token.value) {
      items.value = []
      return items.value
    }

    if (loading.value) {
      return items.value
    }

    loading.value = true
    try {
      const res = await useHttp('/api/cart/list')
      items.value = res?.code === 200 ? res.data || [] : []
      return items.value
    } catch (error) {
      items.value = []
      return []
    } finally {
      loading.value = false
    }
  }

  const addToCart = async (payload: Record<string, any>) => {
    const body = {
      ...payload,
      quantity: normalizeQuantity(payload?.quantity),
    }
    const res = await useHttp('/api/cart/add', {
      method: 'POST',
      body,
    })
    await refreshCart()
    return res
  }

  const updateQuantity = async (cartItemId: number, quantity: number, addonCodes?: string[]) => {
    const res = await useHttp(`/api/cart/${cartItemId}`, {
      method: 'PUT',
      body: {
        quantity,
        addonCodes,
      },
    })
    await refreshCart()
    return res
  }

  const removeItem = async (cartItemId: number) => {
    const res = await useHttp(`/api/cart/${cartItemId}`, {
      method: 'DELETE',
    })
    await refreshCart()
    return res
  }

  return {
    items,
    isOpen,
    loading: computed(() => loading.value),
    count: computed(() => items.value.reduce((sum, item) => sum + Number(item.quantity || 0), 0)),
    subtotal: computed(() =>
      items.value.reduce((sum, item) => sum + Number(item.lineAmount || 0), 0),
    ),
    openCart: () => {
      isOpen.value = true
    },
    closeCart: () => {
      isOpen.value = false
    },
    refreshCart,
    addToCart,
    updateQuantity,
    removeItem,
  }
}
