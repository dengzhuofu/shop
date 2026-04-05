import { defineVitestConfig } from '@nuxt/test-utils/config'

export default defineVitestConfig({
  test: {
    name: 'frontend-unit',
    environment: 'nuxt',
    globals: true,
    include: ['tests/frontend/**/*.spec.ts'],
    exclude: ['tests/e2e/**', 'node_modules/**', '.nuxt/**'],
    setupFiles: ['tests/frontend/setup.ts'],
  },
})
