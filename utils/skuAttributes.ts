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

export function formatSkuAttributeKey(value: string | null | undefined) {
  const normalized = `${value || ''}`.trim()
  if (!normalized) {
    return ''
  }

  const lowerCased = normalized.toLowerCase()
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
