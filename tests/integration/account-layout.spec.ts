import { fetch, setup } from '@nuxt/test-utils/e2e'

await setup({
  rootDir: process.cwd(),
  server: true,
  browser: false,
})

describe('account shell integration', () => {
  it('renders account profile with independent account layout instead of the storefront header', async () => {
    const response = await fetch('/account/profile')
    const html = await response.text()

    expect(response.status).toBe(200)
    expect(html).toContain('isinwheel')
    expect(html).toContain('Profile')
    expect(html).toContain('Refund policy')
    expect(html).not.toContain('Limited Time Offer!')
  })

  it('renders checkout with the account shell and pay action copy', async () => {
    const response = await fetch('/checkout')
    const html = await response.text()

    expect(response.status).toBe(200)
    expect(html).toContain('Shipping address')
    expect(html).toContain('Pay now')
    expect(html).not.toContain('Limited Time Offer!')
  })
})
