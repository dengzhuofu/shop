import { clearPageScrollLock, setPageScrollLocked } from '~/utils/scrollLock'

describe('scroll lock helpers', () => {
  afterEach(() => {
    clearPageScrollLock()
  })

  it('locks and unlocks the page scroll state', () => {
    setPageScrollLocked(true)

    expect(document.body.style.overflow).toBe('hidden')
    expect(document.documentElement.style.overflow).toBe('hidden')

    clearPageScrollLock()

    expect(document.body.style.overflow).toBe('')
    expect(document.documentElement.style.overflow).toBe('')
  })
})
