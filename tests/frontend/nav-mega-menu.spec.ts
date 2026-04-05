import { mountSuspended } from '@nuxt/test-utils/runtime'
import NavMegaMenu from '~/components/NavMegaMenu.vue'

describe('NavMegaMenu', () => {
  it('renders real menu groups and products from backend-shaped data', async () => {
    const wrapper = await mountSuspended(NavMegaMenu, {
      props: {
        menuData: {
          id: 2,
          slug: 'electric-bike',
          name: 'Electric Bike',
          banner: {
            tag: 'Save $20',
            trustpilot: '4.4',
            linkUrl: '/collections/electric-bike',
            linkText: 'All Electric Bike (4)',
          },
          children: [
            {
              id: 13,
              name: 'Commuter & City Road',
              allLinkUrl: '/collections/electric-bike',
              allLinkText: 'All Commuter & City Road',
              products: [
                {
                  id: 1,
                  slug: 'u8-electric-bike',
                  title: 'isinwheel U8 Electric Bike',
                  price: 609.99,
                  compareAtPrice: 799.99,
                  pic: 'https://example.com/u8.jpg',
                  images: ['https://example.com/u8.jpg', 'https://example.com/u8-2.jpg'],
                  tags: ['NEW', 'Spring Sale'],
                  skuList: [
                    { price: 609.99, stock: 8, status: 'ACTIVE' },
                    { price: 649.99, stock: 3, status: 'ACTIVE' },
                  ],
                },
              ],
            },
            {
              id: 14,
              name: 'Off Road & All Terrain',
              allLinkUrl: '/collections/electric-bike',
              allLinkText: 'All Off Road & All Terrain',
              products: [],
            },
          ],
        },
      },
    })

    expect(wrapper.text()).toContain('Collections')
    expect(wrapper.text()).toContain('Commuter & City Road')
    expect(wrapper.text()).toContain('All Commuter & City Road')
    expect(wrapper.text()).toContain('isinwheel U8 Electric Bike')
    expect(wrapper.text()).toContain('$609.99')
    expect(wrapper.find('.spring-sale-badge').exists()).toBe(true)
  })
})
