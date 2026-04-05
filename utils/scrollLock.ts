export const setPageScrollLocked = (locked: boolean) => {
  if (typeof document === 'undefined') {
    return
  }

  const value = locked ? 'hidden' : ''
  document.body.style.overflow = value
  document.documentElement.style.overflow = value
}

export const clearPageScrollLock = () => {
  setPageScrollLocked(false)
}
