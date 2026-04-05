import { mountSuspended } from '@nuxt/test-utils/runtime'
import { defineComponent } from 'vue'

describe('Nuxt unit test harness', () => {
  it('mounts a component inside the Nuxt runtime', async () => {
    const TestComponent = defineComponent({
      template: '<div data-test="ready">ready</div>',
    })

    const wrapper = await mountSuspended(TestComponent)

    expect(wrapper.get('[data-test="ready"]').text()).toBe('ready')
  })
})
