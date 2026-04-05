<template>
  <div class="collection-page container">
    <section class="hero" v-if="category">
      <img :src="category.heroImage || category.menuImage" :alt="category.name" />
      <div class="overlay">
        <p class="eyebrow">Collection</p>
        <h1>{{ category.name }}</h1>
        <p>{{ category.description }}</p>
      </div>
    </section>

    <section class="toolbar">
      <div>
        <h2>{{ products.length }} {{ t('productsCount') }}</h2>
      </div>

      <label class="sort-box">
        <span>{{ t('sortDefault') }}</span>
        <select v-model="sortBy" @change="fetchCollection">
          <option value="">{{ t('sortDefault') }}</option>
          <option value="price-ascending">{{ t('sortPriceAsc') }}</option>
          <option value="price-descending">{{ t('sortPriceDesc') }}</option>
        </select>
      </label>
    </section>

    <div class="product-grid">
      <ProductCard v-for="product in products" :key="product.id" :product="product" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'

const route = useRoute()
const { t } = useShopLocale()

const category = ref<any | null>(null)
const products = ref<any[]>([])
const sortBy = ref('')

const fetchCollection = async () => {
  try {
    const slug = String(route.params.slug)
    const [categoryRes, productRes] = await Promise.all([
      useHttp(`/api/category/${slug}`),
      useHttp(`/api/category/${slug}/products?pageNum=1&pageSize=24`, {
        query: sortBy.value ? { sort: sortBy.value } : undefined,
      }),
    ])

    category.value = categoryRes?.code === 200 ? categoryRes.data : null
    products.value = productRes?.code === 200 ? productRes.data?.records || [] : []
  } catch (error) {
    category.value = null
    products.value = []
  }
}

watch(() => route.params.slug, fetchCollection)
onMounted(fetchCollection)
</script>

<style scoped lang="scss">
.collection-page {
  padding-top: 28px;
}

.hero {
  position: relative;
  min-height: 320px;
  border-radius: 32px;
  overflow: hidden;
  margin-bottom: 24px;

  img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.overlay {
  position: relative;
  z-index: 1;
  min-height: 320px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 30px;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.08), rgba(15, 23, 42, 0.78));
  color: white;

  h1 {
    margin: 0 0 8px;
    font-size: clamp(32px, 5vw, 52px);
  }

  p {
    margin: 0;
    max-width: 58ch;
    line-height: 1.7;
  }
}

.eyebrow {
  margin-bottom: 10px !important;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.72);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;

  h2 {
    margin: 0;
    color: #0f172a;
  }
}

.sort-box {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 999px;
  background: white;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 10px 14px;

  span {
    color: #64748b;
    font-size: 13px;
  }

  select {
    border: none;
    background: transparent;
    font-weight: 700;
    color: #0f172a;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

@media (max-width: 1100px) {
  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
