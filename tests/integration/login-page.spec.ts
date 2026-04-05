import { fetch, setup } from '@nuxt/test-utils/e2e'

await setup({
  rootDir: process.cwd(),
  server: true,
  browser: false,
})

describe('login page integration', () => {
  it('renders the core login form in SSR output', async () => {
    const response = await fetch('/login')
    const html = await response.text()

    expect(response.status).toBe(200)
    expect(html).toContain('Sign in to your rider account')
    expect(html).toContain('Email')
    expect(html).toContain('Password')
    expect(html).toContain('Register')
  })
})
