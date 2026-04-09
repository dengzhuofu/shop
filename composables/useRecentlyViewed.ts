import { computed } from 'vue'

const STORAGE_KEY = 'shop-recently-viewed-v1'
const MAX_RECENTLY_VIEWED = 6

type RecentlyViewedItem = {
  id: number
  slug?: string | null
  title: string
  subtitle?: string | null
  pic?: string | null
  price?: number | null
  compareAtPrice?: number | null
  tags?: any
  isNew?: boolean
  categorySlug?: string | null
  viewedAt: string
}

const normalizeNumber = (value: unknown) => {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : null
}

const normalizeImage = (product: Record<string, any>) => {
  if (typeof product?.pic === 'string' && product.pic.trim()) {
    return product.pic
  }

  if (Array.isArray(product?.images) && typeof product.images[0] === 'string') {
    return product.images[0]
  }

  return null
}

const normalizeProduct = (product: Record<string, any> | null | undefined): RecentlyViewedItem | null => {
  const productId = Number(product?.id)
  const title = String(product?.title || '').trim()
  if (!Number.isFinite(productId) || !title) {
    return null
  }

  return {
    id: productId,
    slug: product?.slug || null,
    title,
    subtitle: product?.subtitle || null,
    pic: normalizeImage(product),
    price: normalizeNumber(product?.price),
    compareAtPrice: normalizeNumber(product?.compareAtPrice),
    tags: product?.tags || [],
    isNew: Boolean(product?.isNew),
    categorySlug: product?.categorySlug || null,
    viewedAt: new Date().toISOString(),
  }
}

const normalizeStoredItem = (item: Record<string, any> | null | undefined): RecentlyViewedItem | null => {
  const normalized = normalizeProduct(item)
  if (!normalized) {
    return null
  }

  return {
    ...normalized,
    viewedAt:
      typeof item?.viewedAt === 'string' && item.viewedAt.trim()
        ? item.viewedAt
        : normalized.viewedAt,
  }
}

const readStorage = (): RecentlyViewedItem[] => {
  if (!process.client) {
    return []
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    if (!raw) {
      return []
    }

    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) {
      return []
    }

    return parsed
      .map((item) => normalizeStoredItem(item))
      .filter((item): item is RecentlyViewedItem => Boolean(item))
      .slice(0, MAX_RECENTLY_VIEWED)
  } catch {
    return []
  }
}

const writeStorage = (items: RecentlyViewedItem[]) => {
  if (!process.client) {
    return
  }
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(items.slice(0, MAX_RECENTLY_VIEWED)))
}

export function useRecentlyViewed() {
  const items = useState<RecentlyViewedItem[]>('shop-recently-viewed-items', () => [])
  const loading = useState('shop-recently-viewed-loading', () => false)
  const syncedToken = useState<string | null>('shop-recently-viewed-synced-token', () => null)
  const session = useShopSession()

  const applyLocalItems = (nextItems: RecentlyViewedItem[]) => {
    items.value = nextItems.slice(0, MAX_RECENTLY_VIEWED)
    writeStorage(items.value)
  }

  const recordLocally = (product: Record<string, any> | null | undefined) => {
    const normalized = normalizeProduct(product)
    if (!normalized) {
      return null
    }

    const nextItems = [normalized, ...readStorage().filter((item) => item.id !== normalized.id)]
      .slice(0, MAX_RECENTLY_VIEWED)
    applyLocalItems(nextItems)
    return normalized
  }

  const syncLocalToServer = async () => {
    const localItems = readStorage()
    if (!localItems.length || !session.token.value) {
      return null
    }

    const res = await useHttp('/api/product/recently-viewed/sync', {
      method: 'POST',
      body: {
        productIds: localItems.map((item) => item.id),
      },
      handleAuthError: false,
      showError: false,
    })
    syncedToken.value = session.token.value
    return Array.isArray(res?.data) ? res.data : null
  }

  const refreshRecentlyViewed = async () => {
    if (!process.client) {
      return items.value
    }

    loading.value = true
    try {
      const localItems = readStorage()

      if (!session.token.value) {
        syncedToken.value = null
        items.value = localItems
        return items.value
      }

      let remoteItems: any[] | null = null
      if (syncedToken.value !== session.token.value) {
        remoteItems = await syncLocalToServer()
      }

      if (!remoteItems) {
        const res = await useHttp('/api/product/recently-viewed?limit=6', {
          handleAuthError: false,
          showError: false,
        })
        remoteItems = Array.isArray(res?.data) ? res.data : null
      }

      items.value = Array.isArray(remoteItems) && remoteItems.length ? remoteItems : localItems
      return items.value
    } catch {
      items.value = readStorage()
      return items.value
    } finally {
      loading.value = false
    }
  }

  const recordProduct = async (product: Record<string, any> | null | undefined) => {
    const normalized = recordLocally(product)
    if (!normalized || !session.token.value) {
      return normalized
    }

    try {
      await useHttp('/api/product/recently-viewed/sync', {
        method: 'POST',
        body: {
          productIds: readStorage().map((item) => item.id),
        },
        handleAuthError: false,
        showError: false,
      })
      syncedToken.value = session.token.value
    } catch {
      // Keep the local record even if the background sync fails.
    }

    return normalized
  }

  return {
    items,
    loading: computed(() => loading.value),
    count: computed(() => items.value.length),
    refreshRecentlyViewed,
    recordProduct,
  }
}
