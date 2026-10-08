<template>
  <main class="search-page container">
    <h1>{{ isZh ? '搜索 CBJJ 商品' : 'Search CBJJ products' }}</h1>
    <form @submit.prevent="search"><label for="product-search">{{ isZh ? '型号或商品名称' : 'Model or product name' }}</label><div class="search-input"><input id="product-search" v-model="term" maxlength="100" type="search" required /><button type="submit" :disabled="loading">{{ isZh ? '搜索' : 'Search' }}</button></div></form>
    <p role="status">{{ loading ? (isZh ? '正在搜索…' : 'Searching…') : searched ? (isZh ? `找到 ${products.length} 款商品` : `${products.length} products found`) : (isZh ? '输入型号或名称，例如 S9、头盔。' : 'Enter a model or name, such as S9 or helmet.') }}</p>
    <div class="search-results"><ProductCard v-for="product in products" :key="product.id" :product="product" /></div>
  </main>
</template>
<script setup lang="ts">
const { isZh } = useShopLocale()
const term = ref('')
const products = ref<any[]>([])
const loading = ref(false)
const searched = ref(false)
useSeoMeta({ title: () => isZh.value ? '商品搜索' : 'Product search' })
const search = async () => {
  if (!term.value.trim()) return
  loading.value = true
  try {
    const res = await useHttp('/api/product/list', { query: { keyword: term.value.trim(), pageSize: 30 } })
    products.value = res?.code === 200 ? res.data?.records || [] : []
    searched.value = true
  } finally { loading.value = false }
}
</script>
<style scoped>.search-page{padding-top:48px;padding-bottom:64px;min-height:60vh}.search-page form{max-width:600px}.search-input{display:flex;gap:12px;margin-top:8px}.search-input input{min-width:0;flex:1;padding:12px;border:1px solid #aaa;border-radius:6px;font:inherit}.search-input button{padding:12px 22px;background:#20251b;color:#fff;border:0;border-radius:6px;cursor:pointer}.search-results{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:24px;margin-top:32px}@media(max-width:700px){.search-results{grid-template-columns:repeat(2,minmax(0,1fr));gap:14px}}</style>
