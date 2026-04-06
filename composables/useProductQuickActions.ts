import { findInitialSelection, findSelectedSku, isSkuAvailable } from '~/utils/productSelection'

export function useProductQuickActions() {
  const cart = useShopCart()
  const session = useShopSession()

  const getProductIdentifier = (product: Record<string, any> | null | undefined) =>
    product?.slug || product?.id || null

  const normalizeVariantAttributes = (product: Record<string, any> | null | undefined) => {
    const attributeOptions = product?.skuAttributeOptions || {}
    return Object.values(attributeOptions).filter((options) => Array.isArray(options) && options.length > 1)
  }

  const productHasOptions = (product: Record<string, any> | null | undefined) => {
    if (normalizeVariantAttributes(product).length > 0) {
      return true
    }

    const skuList = Array.isArray(product?.skuList) ? product.skuList : []
    if (skuList.length) {
      const optionMap = new Map<string, Set<string>>()
      for (const sku of skuList) {
        const attributes = sku?.attributes || {}
        for (const [key, value] of Object.entries(attributes)) {
          const normalized = String(value || '').trim()
          if (!normalized) {
            continue
          }
          if (!optionMap.has(key)) {
            optionMap.set(key, new Set())
          }
          optionMap.get(key)?.add(normalized)
        }
      }
      return [...optionMap.values()].some((values) => values.size > 1)
    }

    return product?.hasOptions === true
  }

  const fetchProductDetail = async (product: Record<string, any> | null | undefined) => {
    if (!product) {
      return null
    }

    const identifier = getProductIdentifier(product)
    if (!identifier) {
      return product
    }

    const endpoint =
      /^\d+$/.test(String(identifier)) ? `/api/product/${identifier}` : `/api/product/slug/${identifier}`
    const res = await useHttp(endpoint)
    return res?.code === 200 ? res.data : product
  }

  const addSingleSkuToCart = async (product: Record<string, any>, quantity = 1) => {
    await session.fetchMe()
    if (!session.isLoggedIn.value) {
      await navigateTo('/login')
      return { success: false, redirected: true, product: null }
    }

    const detailedProduct = await fetchProductDetail(product)
    if (!detailedProduct) {
      return { success: false, product: null }
    }

    if (productHasOptions(detailedProduct)) {
      return { success: false, requiresOptions: true, product: detailedProduct }
    }

    const skuList = Array.isArray(detailedProduct.skuList) ? detailedProduct.skuList : []
    const selection = findInitialSelection(skuList)
    const selectedSku = findSelectedSku(skuList, selection) || skuList.find((sku: any) => isSkuAvailable(sku))

    if (!selectedSku) {
      return { success: false, soldOut: true, product: detailedProduct }
    }

    const res = await cart.addToCart({
      productId: detailedProduct.id,
      skuId: selectedSku.id,
      quantity,
    })
    if (res?.code === 200) {
      cart.openCart()
      await cart.refreshCart()
    }

    return {
      success: res?.code === 200,
      product: detailedProduct,
      sku: selectedSku,
    }
  }

  return {
    getProductIdentifier,
    productHasOptions,
    fetchProductDetail,
    addSingleSkuToCart,
  }
}
