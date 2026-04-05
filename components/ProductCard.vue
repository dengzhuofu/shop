<template>
  <article class="product-card">
    <NuxtLink :to="productLink" class="card-link">
      <div class="image-wrap">
        <img :src="coverImage" :alt="product.title" class="cover" />
        <span v-if="tagText" class="tag">{{ tagText }}</span>
      </div>

      <div class="content">
        <p class="category">{{ product.categoryName || product.categorySlug }}</p>
        <h3>{{ product.title }}</h3>
        <p v-if="product.subtitle" class="subtitle">{{ product.subtitle }}</p>
        <div class="price-row">
          <strong>{{ money(product.price) }}</strong>
          <span v-if="product.compareAtPrice" class="compare">{{ money(product.compareAtPrice) }}</span>
        </div>
        <p class="stock" :class="{ out: Number(product.stock || 0) <= 0 }">
          {{ Number(product.stock || 0) > 0 ? t('inStock') : t('outOfStock') }}
        </p>
      </div>
    </NuxtLink>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  product: Record<string, any>
}>()

const { t } = useShopLocale()
const { money } = useShopFormat()

const coverImage = computed(() => {
  if (Array.isArray(props.product.images) && props.product.images.length) {
    return props.product.images[0]
  }
  return props.product.pic || 'https://via.placeholder.com/600x600?text=isinwheel'
})

const tagText = computed(() => {
  if (Array.isArray(props.product.tags) && props.product.tags.length) {
    return props.product.tags[0]
  }
  return ''
})

const productLink = computed(() => `/products/${props.product.slug || props.product.id}`)
</script>

<style scoped lang="scss">
.product-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.08);
  transition:
    transform 0.24s ease,
    box-shadow 0.24s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 18px 36px rgba(15, 23, 42, 0.09);
  }
}

.card-link {
  display: block;
  color: inherit;
  text-decoration: none;
}

.image-wrap {
  position: relative;
  aspect-ratio: 1 / 1;
  background: linear-gradient(180deg, #f8fafc, #eef2f7);
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tag {
  position: absolute;
  top: 16px;
  left: 16px;
  border-radius: 999px;
  background: rgba(15, 118, 110, 0.95);
  color: white;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 700;
}

.content {
  padding: 20px;
}

.category {
  margin: 0 0 10px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

h3 {
  margin: 0 0 8px;
  font-size: 18px;
  line-height: 1.35;
  color: #0f172a;
}

.subtitle {
  margin: 0 0 14px;
  color: #475569;
  line-height: 1.6;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;

  strong {
    font-size: 20px;
    color: #0f172a;
  }
}

.compare {
  color: #94a3b8;
  text-decoration: line-through;
}

.stock {
  margin: 0;
  color: #0f766e;
  font-weight: 600;

  &.out {
    color: #dc2626;
  }
}
</style>
