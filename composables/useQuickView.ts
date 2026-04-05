export function useQuickView() {
  const isOpen = useState<boolean>('quick-view-open', () => false)
  const product = useState<Record<string, any> | null>('quick-view-product', () => null)

  const openQuickView = (nextProduct: Record<string, any> | null) => {
    product.value = nextProduct
    isOpen.value = Boolean(nextProduct)
  }

  const closeQuickView = () => {
    isOpen.value = false
    product.value = null
  }

  return {
    isOpen,
    product,
    openQuickView,
    closeQuickView,
  }
}
