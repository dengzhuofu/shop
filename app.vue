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

const layoutRenderKey = computed(
  () => `${route.meta.layout || 'default'}:${lang.value}`,
)
</script>
