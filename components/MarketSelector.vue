<template>
  <label class="market-selector"><span class="sr-only">{{ isZh ? '销售地区与币种' : 'Sales market and currency' }}</span><select v-model="selectedMarket"><option value="US">{{ isZh ? '美国 · USD' : 'United States · USD' }}</option><option value="CA">{{ isZh ? '加拿大 · USD' : 'Canada · USD' }}</option><option value="CN">{{ isZh ? '中国大陆 · CNY' : 'Mainland China · CNY' }}</option></select></label>
</template>
<script setup lang="ts">
const { isZh } = useShopLocale()
const { market, setMarket } = useShopMarket()
const selectedMarket = computed({ get: () => market.value, set: (value: string) => setMarket(value) })
</script>
<style scoped>.market-selector select{max-width:190px;min-height:38px;padding:7px 24px 7px 10px;border:1px solid #b8c0b0;border-radius:6px;font:inherit;font-size:12px;background:#fff;color:#222;cursor:pointer}.market-selector select:focus-visible{outline:3px solid #39850c;outline-offset:2px}.sr-only{position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;border:0}</style>
