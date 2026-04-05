import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    name: 'frontend-integration',
    environment: 'node',
    globals: true,
    include: ['tests/integration/**/*.spec.ts'],
    exclude: ['node_modules/**', '.nuxt/**'],
  },
})
