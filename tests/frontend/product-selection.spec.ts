import {
  findInitialSelection,
  findSelectedSku,
  isOptionSelectable,
  sortAttributeKeys,
} from '~/utils/productSelection'

describe('product selection helpers', () => {
  const skuList = [
    {
      id: 1,
      stock: 10,
      status: 'ACTIVE',
      attributes: {
        color: 'Black',
        bundle: 'Standard',
        style: 'Pro',
      },
    },
    {
      id: 2,
      stock: 0,
      status: 'ACTIVE',
      attributes: {
        color: 'White',
        bundle: 'Standard',
        style: 'Pro',
      },
    },
  ]

  it('sorts sku attribute keys in storefront order', () => {
    expect(sortAttributeKeys(['style', 'bundle', 'color'])).toEqual(['color', 'bundle', 'style'])
  })

  it('prefers the first in-stock sku for initial selection', () => {
    expect(findInitialSelection(skuList)).toEqual({
      color: 'Black',
      bundle: 'Standard',
      style: 'Pro',
    })
  })

  it('resolves the selected sku from current options', () => {
    const sku = findSelectedSku(skuList, {
      color: 'Black',
      bundle: 'Standard',
      style: 'Pro',
    })
    expect(sku?.id).toBe(1)
  })

  it('marks out-of-stock options as not selectable', () => {
    const selectable = isOptionSelectable(
      skuList,
      { bundle: 'Standard', style: 'Pro' },
      'color',
      'White',
    )
    expect(selectable).toBe(false)
  })
})
