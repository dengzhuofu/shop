<template>
  <div class="electric-scooters-page">
    <section class="collection-header container">
      <h1 class="title">{{ category?.name || route.params.slug }}</h1>
      <p class="subtitle">{{ products.length }} {{ t('productsCount') }}</p>
    </section>

    <section class="toolbar container">
      <button type="button" class="filter-btn" @click="isFilterOpen = !isFilterOpen">
        <SlidersHorizontalIcon class="icon" />
        {{ copy.filters }}
      </button>

      <div class="sort-wrap">
        <label for="sort-by">{{ copy.sortBy }}</label>
        <div class="select-wrap">
          <select id="sort-by" v-model="sortBy">
            <option value="">{{ t('sortDefault') }}</option>
            <option value="price-ascending">{{ t('sortPriceAsc') }}</option>
            <option value="price-descending">{{ t('sortPriceDesc') }}</option>
          </select>
          <ChevronDownIcon class="icon" />
        </div>
      </div>
    </section>

    <section class="mobile-filters" :class="{ 'is-open': isFilterOpen }">
      <div class="container mobile-filters-inner">
        <div class="filter-group">
          <h3>{{ copy.availability }}</h3>
          <label><input v-model="stockFilter" value="in" type="radio" /> {{ t('inStock') }}</label>
          <label><input v-model="stockFilter" value="all" type="radio" /> {{ copy.allProducts }}</label>
        </div>
        <div class="filter-group">
          <h3>{{ copy.price }}</h3>
          <label><input v-model="priceFilter" value="all" type="radio" /> {{ copy.allProducts }}</label>
          <label><input v-model="priceFilter" value="low" type="radio" /> < $500</label>
          <label><input v-model="priceFilter" value="high" type="radio" /> >= $500</label>
        </div>
      </div>
    </section>

    <section class="product-list container">
      <div class="grid">
        <CollectionProductCard v-for="product in filteredProducts" :key="product.id" :product="product" />
      </div>

      <div v-if="canLoadMore" class="show-more-wrap">
        <button type="button" class="show-more-btn" @click="loadMore">{{ t('loadMore') }}</button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ChevronDownIcon, SlidersHorizontalIcon } from 'lucide-vue-next'
import CollectionProductCard from '~/components/CollectionProductCard.vue'

const route = useRoute()
const { lang, t } = useShopLocale()

const category = ref<any | null>(null)
const products = ref<any[]>([])
const total = ref(0)
const pageSize = ref(12)
const sortBy = ref('')
const isFilterOpen = ref(false)
const stockFilter = ref<'all' | 'in'>('all')
const priceFilter = ref<'all' | 'low' | 'high'>('all')

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        filters: '显示筛选',
        sortBy: '排序方式:',
        availability: '库存',
        price: '价格',
        allProducts: '全部商品',
      }
    : {
        filters: 'Show filters',
        sortBy: 'Sort by:',
        availability: 'Availability',
        price: 'Price',
        allProducts: 'All products',
      },
)

const filteredProducts = computed(() =>
  products.value.filter((product) => {
    if (stockFilter.value === 'in' && Number(product.stock || 0) <= 0) {
      return false
    }
    if (priceFilter.value === 'low' && Number(product.price || 0) >= 500) {
      return false
    }
    if (priceFilter.value === 'high' && Number(product.price || 0) < 500) {
      return false
    }
    return true
  }),
)

const canLoadMore = computed(() => products.value.length < total.value)

const fetchCollection = async () => {
  try {
    const slug = String(route.params.slug)
    const [categoryRes, productRes] = await Promise.all([
      useHttp(`/api/category/${slug}`),
      useHttp(`/api/category/${slug}/products?pageNum=1&pageSize=${pageSize.value}`, {
        query: sortBy.value ? { sort: sortBy.value } : undefined,
      }),
    ])

    category.value = categoryRes?.code === 200 ? categoryRes.data : null
    products.value = productRes?.code === 200 ? productRes.data?.records || [] : []
    total.value = Number(productRes?.data?.total || 0)
  } catch (error) {
    category.value = null
    products.value = []
    total.value = 0
  }
}

const loadMore = async () => {
  pageSize.value += 12
  await fetchCollection()
}

watch(() => route.params.slug, fetchCollection)
watch(sortBy, fetchCollection)

onMounted(fetchCollection)
</script>

<style scoped lang="scss">
.electric-scooters-page {
  padding: 20px 0 70px;
}

.collection-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;

  .title {
    font-size: 34px;
    line-height: 1.2;
    font-weight: 700;
    color: #101828;
    margin: 0;
  }

  .subtitle {
    font-size: 14px;
    color: #667085;
    margin: 0;
  }
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #eaecf0;
  border-bottom: 1px solid #eaecf0;
  padding-top: 14px;
  padding-bottom: 14px;
  margin-bottom: 24px;
  gap: 12px;

  .filter-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #101828;
    font-size: 14px;
    font-weight: 500;
    background: none;
    border: none;

    .icon {
      width: 17px;
      height: 17px;
    }
  }

  .sort-wrap {
    display: inline-flex;
    align-items: center;
    gap: 10px;

    label {
      font-size: 14px;
      color: #344054;
      white-space: nowrap;
    }

    .select-wrap {
      position: relative;

      select {
        appearance: none;
        border: 1px solid #d0d5dd;
        border-radius: 10px;
        padding: 8px 34px 8px 12px;
        font-size: 14px;
        color: #101828;
        background: #fff;
        min-width: 210px;
      }

      .icon {
        width: 16px;
        height: 16px;
        color: #667085;
        position: absolute;
        right: 10px;
        top: 50%;
        transform: translateY(-50%);
        pointer-events: none;
      }
    }
  }
}

.mobile-filters {
  display: none;
  border-bottom: 1px solid #eaecf0;
  margin-bottom: 24px;

  &.is-open {
    display: block;
  }

  .mobile-filters-inner {
    display: flex;
    gap: 28px;
    padding-top: 12px;
    padding-bottom: 16px;
  }

  .filter-group {
    display: flex;
    flex-direction: column;
    gap: 8px;

    h3 {
      font-size: 13px;
      font-weight: 700;
      color: #101828;
      margin-bottom: 2px;
    }

    label {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #344054;
    }
  }
}

.product-list {
  .grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 18px;
  }

  .show-more-wrap {
    display: flex;
    justify-content: center;
    margin-top: 28px;
  }

  .show-more-btn {
    border: 1px solid #1d2939;
    border-radius: 999px;
    color: #1d2939;
    font-size: 14px;
    font-weight: 600;
    padding: 10px 26px;
    transition: all 0.2s ease;
    background: #fff;

    &:hover {
      background: #1d2939;
      color: #fff;
    }
  }
}

@media (max-width: 1440px) {
  .product-list {
    .grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
  }
}

@media (max-width: 1024px) {
  .collection-header {
    .title {
      font-size: 28px;
    }
  }

  .product-list {
    .grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
}

@media (max-width: 768px) {
  .electric-scooters-page {
    padding-top: 14px;
  }

  .collection-header {
    flex-direction: column;
    gap: 4px;
    margin-bottom: 12px;

    .title {
      font-size: 24px;
    }
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;

    .sort-wrap {
      justify-content: space-between;

      .select-wrap {
        flex: 1;

        select {
          width: 100%;
          min-width: 0;
        }
      }
    }
  }

  .mobile-filters {
    .mobile-filters-inner {
      flex-direction: column;
      gap: 14px;
    }
  }

  .product-list {
    .grid {
      grid-template-columns: 1fr;
      gap: 14px;
    }
  }
}
</style>
