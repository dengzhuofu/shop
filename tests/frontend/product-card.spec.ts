import { mountSuspended } from '@nuxt/test-utils/runtime'
import ProductCard from '~/components/ProductCard.vue'

describe('ProductCard', () => {
  it('renders the restored card layout with real product fields', async () => {
    const wrapper = await mountSuspended(ProductCard, {
      props: {
        product: {
          id: 1,
          slug: 's9max-commuter-scooter',
          title: 'S9Max Commuter Scooter',
          price: 699.99,
          compareAtPrice: 899.99,
          pic: 'https://example.com/cover.jpg',
          images: [
            'https://example.com/cover.jpg',
            'https://example.com/detail.jpg',
          ],
          appImage: 'https://example.com/app-preview.png',
          tags: ['NEW', 'Spring Sale'],
          specs: [
            { label: 'Motor', value: '1000W', icon: 'ZapIcon' },
            { label: 'Range', value: '38 Miles', icon: 'NavigationIcon' },
            { label: 'Top Speed', value: '28 MPH', icon: 'ActivityIcon' },
            { label: 'Battery', value: '48V 13Ah', icon: 'BatteryIcon' },
          ],
          skuList: [
            { price: 699.99, stock: 12, status: 'ACTIVE' },
            { price: 749.99, stock: 8, status: 'ACTIVE' },
          ],
        },
      },
    })

    expect(wrapper.get('.title').text()).toBe('S9Max Commuter Scooter')
    expect(wrapper.get('.current-price').text()).toContain('From')
    expect(wrapper.findAll('.main-img')).toHaveLength(2)
    expect(wrapper.find('.app-preview-img').exists()).toBe(true)
    expect(wrapper.findAll('.spec-item')).toHaveLength(4)
    expect(wrapper.find('.tag-label.new').text()).toBe('NEW')
    expect(wrapper.find('.tags-right').exists()).toBe(true)
  })

  it('shows sold-out state when all sku inventory is unavailable', async () => {
    const wrapper = await mountSuspended(ProductCard, {
      props: {
        product: {
          id: 2,
          slug: 'cable-lock',
          title: 'Cable Lock',
          price: 29.99,
          stock: 0,
          pic: 'https://example.com/lock.jpg',
          skuList: [
            { price: 29.99, stock: 0, status: 'ACTIVE' },
          ],
          specs: [],
        },
      },
    })

    expect(wrapper.text()).toContain('Sold out')
  })
})
