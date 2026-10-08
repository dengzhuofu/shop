import { mountSuspended } from '@nuxt/test-utils/runtime'
import { defineComponent } from 'vue'

describe('market pricing display', () => {
  it('keeps language independent and formats server amounts without converting them', async () => {
    let market:any, locale:any, format:any
    const wrapper=await mountSuspended(defineComponent({ setup(){
      market=useShopMarket(); locale=useShopLocale(); format=useShopFormat(); return () => null
    }}))
    locale.setLang('en'); market.setMarket('CN')
    expect(locale.lang.value).toBe('en')
    expect(market.currency.value).toBe('CNY')
    expect(format.money(1889.93)).toMatch(/CNY.*1,889\.93/)
    expect(format.money(269.99,'USD')).toMatch(/USD.*269\.99/)
    locale.setLang('zh')
    expect(market.market.value).toBe('CN')
    market.setMarket('CA')
    expect(market.currency.value).toBe('USD')
    expect(locale.lang.value).toBe('zh')
    market.setMarket('US');locale.setLang('en');wrapper.unmount()
  })
})
