<template>
  <MessageContainer />
  <NuxtLayout :key="layoutRenderKey">
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const route = useRoute()
const { lang } = useShopLocale()
const { market } = useShopMarket()
useHead(() => ({ titleTemplate: (title: string | undefined) => title?.includes('CBJJ') ? title : `${title || 'Electric mobility'} | CBJJ`, htmlAttrs: { lang: lang.value === 'zh' ? 'zh-CN' : 'en' } }))

const layoutRenderKey = computed(
  () => `${route.meta.layout || 'default'}:${lang.value}:${market.value}`,
)
</script>
