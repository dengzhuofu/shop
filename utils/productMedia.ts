const extractImageCandidates = (input: unknown): string[] => {
  if (!input) {
    return []
  }

  if (Array.isArray(input)) {
    return input.flatMap((item) => extractImageCandidates(item))
  }

  if (typeof input === 'string') {
    const value = input.trim()
    return value ? [value] : []
  }

  if (typeof input === 'object') {
    const source = input as Record<string, unknown>
    for (const key of ['url', 'src', 'image', 'pic', 'originalSrc']) {
      const value = source[key]
      if (typeof value === 'string' && value.trim()) {
        return [value.trim()]
      }
    }
    return Object.values(source).flatMap((value) => extractImageCandidates(value))
  }

  return []
}

export const mergeProductImages = (...sources: unknown[]) => {
  const seen = new Set<string>()
  return sources
    .flatMap((source) => extractImageCandidates(source))
    .filter((value) => {
      if (seen.has(value)) {
        return false
      }
      seen.add(value)
      return true
    })
}
