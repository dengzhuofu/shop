import { mockNuxtImport, mountSuspended } from '@nuxt/test-utils/runtime'
import { defineComponent } from 'vue'

const { useHttpMock, useShopSessionMock } = vi.hoisted(() => ({
  useHttpMock: vi.fn(),
  useShopSessionMock: vi.fn(),
}))

mockNuxtImport('useHttp', () => useHttpMock)
mockNuxtImport('useShopSession', () => useShopSessionMock)

describe('useShopCart', () => {
  beforeEach(() => {
    useHttpMock.mockReset()
    useShopSessionMock.mockReset()
  })

  it('re-runs cart refresh after add-to-cart when an earlier refresh is still in flight', async () => {
    let cartApi: any = null
    let resolveFirstList: ((value: any) => void) | null = null
    let listCalls = 0

    useShopSessionMock.mockReturnValue({
      token: { value: 'test-token' },
    })

    useHttpMock.mockImplementation((url: string) => {
      if (url === '/api/cart/add') {
        return Promise.resolve({ code: 200 })
      }

      if (url === '/api/cart/list') {
        listCalls += 1

        if (listCalls === 1) {
          return new Promise((resolve) => {
            resolveFirstList = resolve
          })
        }

        return Promise.resolve({
          code: 200,
          data: [
            {
              cartItemId: 1,
              quantity: 1,
              lineAmount: 269.99,
            },
          ],
        })
      }

      return Promise.resolve({ code: 200, data: null })
    })

    const TestComponent = defineComponent({
      setup() {
        cartApi = useShopCart()
        return () => null
      },
    })

    await mountSuspended(TestComponent)

    const initialRefresh = cartApi.refreshCart()
    await Promise.resolve()

    const addPromise = cartApi.addToCart({
      productId: 1,
      skuId: 1,
      quantity: 1,
    })

    await Promise.resolve()
    resolveFirstList?.({ code: 200, data: [] })

    await Promise.all([initialRefresh, addPromise])

    expect(listCalls).toBe(2)
    expect(cartApi.items.value).toEqual([
      {
        cartItemId: 1,
        quantity: 1,
        lineAmount: 269.99,
      },
    ])
    expect(cartApi.count.value).toBe(1)
  })
})
