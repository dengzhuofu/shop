<template>
  <ProductCard :product="product" class="collection-product-card" />
</template>

<script setup lang="ts">
import ProductCard from '~/components/ProductCard.vue'

defineProps<{
  product: Record<string, any>
}>()
</script>

<style scoped lang="scss">
.collection-product-card {
  height: 100%;
}
</style>
