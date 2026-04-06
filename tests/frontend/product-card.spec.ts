import { mountSuspended } from '@nuxt/test-utils/runtime'
import ProductCard from '~/components/ProductCard.vue'

describe('ProductCard', () => {
  it('shows add to cart when the product has a single purchasable sku', async () => {
    const wrapper = await mountSuspended(ProductCard, {
      props: {
        product: {
          id: 101,
          slug: 'single-sku-scooter',
          title: 'Single SKU Scooter',
          price: 399.99,
          stock: 8,
          pic: 'https://example.com/scooter.jpg',
          skuList: [{ id: 1, price: 399.99, stock: 8, status: 'ACTIVE' }],
        },
      },
    })

    expect(wrapper.text()).toContain('Add to cart')
  })

  it('shows choose options when the product exposes multiple sku choices', async () => {
    const wrapper = await mountSuspended(ProductCard, {
      props: {
        product: {
          id: 102,
          slug: 'multi-sku-scooter',
          title: 'Multi SKU Scooter',
          price: 499.99,
          stock: 16,
          pic: 'https://example.com/scooter-2.jpg',
          hasOptions: true,
          skuList: [
            {
              id: 1,
              price: 499.99,
              stock: 8,
              status: 'ACTIVE',
              attributes: { color: 'Black' },
            },
            {
              id: 2,
              price: 549.99,
              stock: 8,
              status: 'ACTIVE',
              attributes: { color: 'White' },
            },
          ],
        },
      },
    })

    expect(wrapper.text()).toContain('Choose options')
  })

  it('keeps add to cart when multiple skus do not expose selectable attributes', async () => {
    const wrapper = await mountSuspended(ProductCard, {
      props: {
        product: {
          id: 103,
          slug: 'same-option-scooter',
          title: 'Same Option Scooter',
          price: 459.99,
          stock: 10,
          pic: 'https://example.com/scooter-3.jpg',
          skuList: [
            {
              id: 11,
              price: 459.99,
              stock: 4,
              status: 'ACTIVE',
              attributes: { color: 'Black' },
            },
            {
              id: 12,
              price: 459.99,
              stock: 6,
              status: 'ACTIVE',
              attributes: { color: 'Black' },
            },
          ],
        },
      },
    })

    expect(wrapper.text()).toContain('Add to cart')
    expect(wrapper.text()).not.toContain('Choose options')
  })
})
