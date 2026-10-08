const ATTRIBUTE_LABELS: Record<string, string> = {
  color: 'Color',
  bundle: 'Bundle',
  style: 'Style',
  size: 'Size',
  model: 'Model',
  gift: 'Gift',
  'with gift box': 'With Gift Box',
  'free gift included': 'Free Gift Included',
  'buy more save more': 'Buy More Save More',
  title: 'Title',
}

const ZH_ATTRIBUTE_LABELS: Record<string, string> = { color: '颜色', bundle: '套装', style: '版本', size: '尺寸', model: '型号', gift: '赠品', 'with gift box': '礼盒包装', 'free gift included': '随附赠品', 'buy more save more': '组合数量', title: '款式' }

export function formatSkuAttributeKey(value: string | null | undefined, lang = 'en') {
  const normalized = `${value || ''}`.trim()
  if (!normalized) {
    return ''
  }

  const lowerCased = normalized.toLowerCase()
  if (lang === 'zh' && ZH_ATTRIBUTE_LABELS[lowerCased]) return ZH_ATTRIBUTE_LABELS[lowerCased]
  if (ATTRIBUTE_LABELS[lowerCased]) {
    return ATTRIBUTE_LABELS[lowerCased]
  }

  return lowerCased
    .replace(/[_-]+/g, ' ')
    .split(/\s+/)
    .filter(Boolean)
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}
