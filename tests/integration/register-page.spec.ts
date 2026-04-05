import { fetch, setup } from '@nuxt/test-utils/e2e'

await setup({
  rootDir: process.cwd(),
  server: true,
  browser: false,
})

describe('register page integration', () => {
  it('renders the registration form in SSR output', async () => {
    const response = await fetch('/register')
    const html = await response.text()

    expect(response.status).toBe(200)
    expect(html).toContain('Create your rider account')
    expect(html).toContain('First name')
    expect(html).toContain('Last name')
    expect(html).toContain('Email')
  })
})
