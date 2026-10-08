import { computed } from 'vue'

export type ShopMarket = 'US' | 'CA' | 'CN'
export type ShopCurrency = 'USD' | 'CNY'
export function useShopMarket() {
  const cookie = useCookie<ShopMarket>('shop-market', { default: () => 'US' })
  const market = useState<ShopMarket>('shop-market', () => ['US', 'CA', 'CN'].includes(cookie.value) ? cookie.value : 'US')
  const currency = computed<ShopCurrency>(() => market.value === 'CN' ? 'CNY' : 'USD')
  const setMarket = (value: string) => {
    if (!['US', 'CA', 'CN'].includes(value)) return
    market.value = value as ShopMarket
    cookie.value = value as ShopMarket
  }
  return { market, currency, setMarket }
}
