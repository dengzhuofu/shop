const ATTRIBUTE_PRIORITY = ['color', 'bundle', 'style']

type ProductSku = {
  id: number
  stock: number
  status?: string
  attributes?: Record<string, any>
}

type ProductSelection = Record<string, string>

export function sortAttributeKeys(keys: string[]) {
  return [...keys].sort((left, right) => {
    const leftIndex = ATTRIBUTE_PRIORITY.indexOf(left)
    const rightIndex = ATTRIBUTE_PRIORITY.indexOf(right)
    const leftWeight = leftIndex === -1 ? ATTRIBUTE_PRIORITY.length + 1 : leftIndex
    const rightWeight = rightIndex === -1 ? ATTRIBUTE_PRIORITY.length + 1 : rightIndex
    if (leftWeight !== rightWeight) {
      return leftWeight - rightWeight
    }
    return left.localeCompare(right)
  })
}

export function findInitialSelection(skuList: ProductSku[]) {
  const initialSku = skuList.find((sku) => isSkuAvailable(sku)) || skuList[0]
  return { ...(initialSku?.attributes || {}) }
}

export function findSelectedSku(skuList: ProductSku[], selection: ProductSelection) {
  return (
    skuList.find((sku) => {
      const attributes = sku.attributes || {}
      return Object.entries(selection).every(([key, value]) => attributes[key] === value)
    }) || null
  )
}

export function isOptionSelectable(
  skuList: ProductSku[],
  selection: ProductSelection,
  attributeKey: string,
  optionValue: string,
) {
  return skuList.some((sku) => {
    if (!isSkuAvailable(sku)) {
      return false
    }
    const attributes = sku.attributes || {}
    if (attributes[attributeKey] !== optionValue) {
      return false
    }
    return Object.entries(selection).every(([key, value]) => {
      if (key === attributeKey || !value) {
        return true
      }
      return attributes[key] === value
    })
  })
}

export function isSkuAvailable(sku: ProductSku) {
  return (sku.status || 'ACTIVE') === 'ACTIVE' && Number(sku.stock || 0) > 0
}
