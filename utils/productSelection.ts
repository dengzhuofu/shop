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
  return skuList.some((sku) => isSkuAvailable(sku) && sku.attributes?.[attributeKey] === optionValue)
}

export function selectSkuOption(
  skuList: ProductSku[], selection: ProductSelection, attributeKey: string, optionValue: string,
) {
  const candidates = skuList.filter((sku) => isSkuAvailable(sku) && sku.attributes?.[attributeKey] === optionValue)
  const keys = sortAttributeKeys(Object.keys(selection))
  // Keep earlier controls (color before kit before version) when combinations differ.
  const score = (sku: ProductSku) => keys.reduce((total, key, index) =>
    total + (key !== attributeKey && sku.attributes?.[key] === selection[key] ? 2 ** (keys.length - index) : 0), 0)
  candidates.sort((left, right) => score(right) - score(left))
  return candidates.length ? { ...candidates[0].attributes } as ProductSelection : { ...selection }
}

export function isSkuAvailable(sku: ProductSku) {
  return (sku.status || 'ACTIVE') === 'ACTIVE' && Number(sku.stock || 0) > 0
}
