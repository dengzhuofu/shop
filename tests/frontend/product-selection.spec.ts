import {
  findInitialSelection,
  findSelectedSku,
  isOptionSelectable,
  selectSkuOption,
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

  it('allows switching to a stocked color whose kit differs from the current selection', () => {
    const variants = [
      { id: 1, stock: 5, attributes: { color: 'Black', bundle: 'Standard' } },
      { id: 2, stock: 5, attributes: { color: 'Orange', bundle: 'Twin Pack' } },
      { id: 3, stock: 0, attributes: { color: 'White', bundle: 'Twin Pack' } },
    ]
    const current = { color: 'Black', bundle: 'Standard' }
    expect(isOptionSelectable(variants, current, 'color', 'Orange')).toBe(true)
    expect(selectSkuOption(variants, current, 'color', 'Orange')).toEqual({ color: 'Orange', bundle: 'Twin Pack' })
    expect(selectSkuOption(variants, current, 'color', 'White')).toEqual(current)
  })

  it('keeps the chosen color when switching a kit also requires a different version', () => {
    const variants = [
      { id: 1, stock: 5, attributes: { color: 'Black', bundle: 'Standard', style: 'Pro' } },
      { id: 2, stock: 5, attributes: { color: 'Grey', bundle: 'Commuter', style: 'Pro' } },
      { id: 3, stock: 5, attributes: { color: 'Black', bundle: 'Commuter', style: 'Lite' } },
      { id: 4, stock: 5, attributes: { color: 'Grey', bundle: 'Standard', style: 'Lite' } },
    ]
    let current = { color: 'Black', bundle: 'Commuter', style: 'Lite' }
    current = selectSkuOption(variants, current, 'color', 'Grey') as typeof current
    current = selectSkuOption(variants, current, 'bundle', 'Standard') as typeof current
    current = selectSkuOption(variants, current, 'style', 'Lite') as typeof current
    expect(findSelectedSku(variants, current)?.id).toBe(4)
  })
})
