<template>
  <div class="home-page">
    <section class="hero container">
      <div class="hero-copy">
        <p class="eyebrow">{{ t('brandTagline') }}</p>
        <h1>{{ t('homeHeroTitle') }}</h1>
        <p class="desc">{{ t('homeHeroDesc') }}</p>
        <div class="hero-actions">
          <NuxtLink to="/collections/electric-scooters" class="primary-action">
            {{ t('shopNow') }}
          </NuxtLink>
          <NuxtLink to="/collections/electric-bike" class="secondary-action">
            {{ t('exploreCatalog') }}
          </NuxtLink>
        </div>
      </div>

      <div class="hero-card">
        <p class="card-kicker">isinwheel</p>
        <h2>{{ t('featuredProducts') }}</h2>
        <div class="stat-grid">
          <div class="stat">
            <span>{{ categories.length }}</span>
            <small>{{ t('categoryCollections') }}</small>
          </div>
          <div class="stat">
            <span>{{ featuredProducts.length }}</span>
            <small>{{ t('productsCount') }}</small>
          </div>
        </div>
      </div>
    </section>

    <section class="section container">
      <div class="section-head">
        <div>
          <p class="eyebrow">Collections</p>
          <h2>{{ t('categoryCollections') }}</h2>
        </div>
      </div>

      <div class="category-grid">
        <NuxtLink
          v-for="category in categories"
          :key="category.id || category.slug"
          :to="`/collections/${category.slug}`"
          class="category-card"
        >
          <img :src="category.heroImage || category.menuImage" :alt="category.name" />
          <div class="overlay">
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
            <span>{{ category.productCount }} {{ t('productsCount') }}</span>
          </div>
        </NuxtLink>
      </div>
    </section>

    <section class="section container">
      <div class="section-head">
        <div>
          <p class="eyebrow">Catalog</p>
          <h2>{{ t('featuredProducts') }}</h2>
        </div>
        <button type="button" class="refresh-btn" @click="fetchHomeData">
          {{ t('refreshData') }}
        </button>
      </div>

      <div class="product-grid">
        <ProductCard v-for="product in featuredProducts" :key="product.id" :product="product" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

const { t } = useShopLocale()

const categories = ref<any[]>([])
const featuredProducts = ref<any[]>([])

const fetchHomeData = async () => {
  try {
    const [categoryRes, productRes] = await Promise.all([
      useHttp('/api/category/tree'),
      useHttp('/api/product/list?pageNum=1&pageSize=8'),
    ])

    categories.value = categoryRes?.code === 200 ? categoryRes.data || [] : []
    featuredProducts.value = productRes?.code === 200 ? productRes.data?.records || [] : []
  } catch (error) {
    categories.value = []
    featuredProducts.value = []
  }
}

onMounted(fetchHomeData)
</script>

<style scoped lang="scss">
.home-page {
  padding: 28px 0 24px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.8fr);
  gap: 24px;
  padding: 36px 0 20px;
}

.hero-copy,
.hero-card {
  border-radius: 32px;
  padding: 36px;
}

.hero-copy {
  background:
    linear-gradient(135deg, rgba(15, 118, 110, 0.88), rgba(15, 23, 42, 0.94)),
    url('https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1600');
  background-size: cover;
  color: white;

  h1 {
    margin: 0 0 16px;
    font-size: clamp(38px, 6vw, 64px);
    line-height: 1;
    max-width: 10ch;
  }
}

.hero-card {
  background: white;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.08);
}

.card-kicker,
.eyebrow {
  margin: 0 0 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.74);
}

.hero-card .card-kicker,
.section .eyebrow {
  color: #0f766e;
}

.desc {
  max-width: 60ch;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.88);
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 24px;
}

.primary-action,
.secondary-action,
.refresh-btn {
  min-height: 50px;
  padding: 0 18px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  font-weight: 700;
}

.primary-action {
  background: white;
  color: #0f172a;
}

.secondary-action,
.refresh-btn {
  background: rgba(255, 255, 255, 0.12);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.hero-card h2,
.section h2 {
  margin: 0;
  font-size: clamp(28px, 3vw, 40px);
  color: #0f172a;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 28px;
}

.stat {
  padding: 20px;
  border-radius: 20px;
  background: #f8fafc;

  span {
    display: block;
    font-size: 32px;
    font-weight: 800;
    color: #0f172a;
  }

  small {
    color: #64748b;
  }
}

.section {
  margin-top: 34px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 22px;
}

.refresh-btn {
  border: none;
  background: #0f172a;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.category-card {
  position: relative;
  min-height: 320px;
  border-radius: 28px;
  overflow: hidden;
  text-decoration: none;
  color: white;

  img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 24px;
  background: linear-gradient(180deg, transparent, rgba(15, 23, 42, 0.85));

  h3 {
    margin: 0 0 8px;
    font-size: 24px;
  }

  p {
    margin: 0 0 10px;
    line-height: 1.6;
    color: rgba(255, 255, 255, 0.86);
  }

  span {
    font-size: 13px;
    font-weight: 700;
    color: #99f6e4;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

@media (max-width: 1100px) {
  .category-grid,
  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 860px) {
  .hero {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .category-grid,
  .product-grid {
    grid-template-columns: 1fr;
  }

  .hero-copy,
  .hero-card {
    padding: 24px;
  }
}
</style>
