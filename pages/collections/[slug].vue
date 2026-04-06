<template>
  <div class="collection-page">
    <section class="collection-header container">
      <div class="heading-copy">
        <p class="eyebrow">{{ activeRootMenu?.name || t('collections') }}</p>
        <h1 class="title">{{ category?.name || route.params.slug }}</h1>
        <p v-if="category?.description" class="description">
          {{ category.description }}
        </p>
      </div>
      <p class="result-count">
        {{ total }} {{ t('resultsCount') }}
      </p>
    </section>

    <section class="toolbar container">
      <button type="button" class="filter-trigger" @click="isFilterOpen = true">
        <SlidersHorizontalIcon class="icon" />
        {{ t('filters') }}
      </button>

      <div class="toolbar-right">
        <div v-if="activeFilterLabels.length" class="active-filter-list">
          <button
            v-for="item in activeFilterLabels"
            :key="item.key"
            type="button"
            class="active-filter-chip"
            @click="item.clear"
          >
            {{ item.label }}
            <XIcon class="chip-icon" />
          </button>
        </div>

        <div class="sort-control">
          <span class="sort-label">{{ t('sortByLabel') }}:</span>
          <button type="button" class="sort-trigger" @click="isSortOpen = true">
            <span class="sort-value">{{ selectedSortLabel }}</span>
            <span class="sort-dot" />
          </button>
        </div>
      </div>
    </section>

    <section class="product-list container">
      <div v-if="products.length" class="grid">
        <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
        />
      </div>

      <div v-else-if="!loading" class="empty-state">
        <p>{{ t('emptyProducts') }}</p>
        <button type="button" class="reset-btn" @click="resetFilters">
          {{ t('resetFilters') }}
        </button>
      </div>

      <div v-if="canLoadMore" class="show-more-wrap">
        <button type="button" class="show-more-btn" @click="loadMore">
          {{ loadingMore ? t('loadingProduct') : t('loadMore') }}
        </button>
      </div>
    </section>

    <Transition name="overlay-fade">
      <div
        v-if="isFilterOpen"
        class="panel-overlay"
        @click.self="isFilterOpen = false"
      >
        <aside class="filter-panel">
          <div class="panel-head">
            <div>
              <p class="panel-kicker">{{ t('filters') }}</p>
              <h2>{{ category?.name || activeRootMenu?.name || route.params.slug }}</h2>
            </div>
            <button
              type="button"
              class="panel-close"
              :aria-label="t('close')"
              @click="isFilterOpen = false"
            >
              <XIcon class="icon" />
            </button>
          </div>

          <div class="panel-body">
            <section
              v-if="isRootCollection && collectionOptions.length"
              class="filter-group"
            >
              <div class="group-head">
                <h3>{{ t('collections') }}</h3>
                <button
                  v-if="selectedChildSlug"
                  type="button"
                  class="clear-link"
                  @click="selectedChildSlug = ''"
                >
                  {{ t('clearAll') }}
                </button>
              </div>
              <div class="option-list">
                <button
                  type="button"
                  class="filter-option"
                  :class="{ 'is-selected': selectedChildSlug === '' }"
                  @click="selectedChildSlug = ''"
                >
                  {{ t('allProducts') }}
                </button>
                <button
                  v-for="item in collectionOptions"
                  :key="item.slug"
                  type="button"
                  class="filter-option"
                  :class="{ 'is-selected': selectedChildSlug === item.slug }"
                  @click="selectedChildSlug = item.slug"
                >
                  {{ item.name }}
                  <span class="option-meta">{{ item.productCount || 0 }}</span>
                </button>
              </div>
            </section>

            <section class="filter-group">
              <h3>{{ t('availability') }}</h3>
              <div class="option-list">
                <button
                  v-for="item in stockOptions"
                  :key="item.value"
                  type="button"
                  class="filter-option"
                  :class="{ 'is-selected': stockFilter === item.value }"
                  @click="stockFilter = item.value"
                >
                  {{ item.label }}
                </button>
              </div>
            </section>

            <section class="filter-group">
              <h3>{{ t('price') }}</h3>
              <div class="option-list">
                <button
                  v-for="item in priceOptions"
                  :key="item.value"
                  type="button"
                  class="filter-option"
                  :class="{ 'is-selected': priceFilter === item.value }"
                  @click="priceFilter = item.value"
                >
                  {{ item.label }}
                </button>
              </div>
            </section>
          </div>

          <div class="panel-foot">
            <button type="button" class="secondary-btn" @click="resetFilters">
              {{ t('resetFilters') }}
            </button>
            <button type="button" class="primary-btn" @click="isFilterOpen = false">
              {{ total }} {{ t('resultsCount') }}
            </button>
          </div>
        </aside>
      </div>
    </Transition>

    <Transition name="overlay-fade">
      <div
        v-if="isSortOpen"
        class="sort-overlay"
        @click.self="isSortOpen = false"
      >
        <div class="sort-panel">
          <div class="sort-head">
            <div>
              <p class="panel-kicker">{{ t('sortByLabel') }}</p>
            </div>
            <button
              type="button"
              class="panel-close light"
              :aria-label="t('close')"
              @click="isSortOpen = false"
            >
              <XIcon class="icon" />
            </button>
          </div>

          <div class="sort-options">
            <button
              v-for="item in sortOptions"
              :key="item.value"
              type="button"
              class="sort-option"
              :class="{ 'is-selected': sortBy === item.value }"
              @click="selectSort(item.value)"
            >
              <span>{{ item.label }}</span>
              <span class="sort-check" />
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import {
  SlidersHorizontalIcon,
  XIcon,
} from 'lucide-vue-next'
import ProductCard from '~/components/ProductCard.vue'

type SortValue =
  | 'featured'
  | 'best-selling'
  | 'title-ascending'
  | 'title-descending'
  | 'price-ascending'
  | 'price-descending'
  | 'created-ascending'
  | 'created-descending'

type StockValue = 'all' | 'in-stock' | 'out-of-stock'
type PriceValue = 'all' | 'under-500' | '500-1000' | '1000-plus'

const PAGE_SIZE = 12

const route = useRoute()
const router = useRouter()
const { t } = useShopLocale()

const category = ref<any | null>(null)
const menuItems = ref<any[]>([])
const products = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(false)
const loadingMore = ref(false)
const isFilterOpen = ref(false)
const isSortOpen = ref(false)
const syncingFromRoute = ref(false)

const sortBy = ref<SortValue>('featured')
const stockFilter = ref<StockValue>('all')
const priceFilter = ref<PriceValue>('all')
const selectedChildSlug = ref('')

const sortOptions = computed(() => [
  { value: 'featured' as const, label: t('sortDefault') },
  { value: 'best-selling' as const, label: t('sortBestSelling') },
  { value: 'title-ascending' as const, label: t('sortTitleAsc') },
  { value: 'title-descending' as const, label: t('sortTitleDesc') },
  { value: 'price-ascending' as const, label: t('sortPriceAsc') },
  { value: 'price-descending' as const, label: t('sortPriceDesc') },
  { value: 'created-ascending' as const, label: t('sortDateAsc') },
  { value: 'created-descending' as const, label: t('sortDateDesc') },
])

const stockOptions = computed(() => [
  { value: 'all' as const, label: t('allProducts') },
  { value: 'in-stock' as const, label: t('inStock') },
  { value: 'out-of-stock' as const, label: t('outOfStock') },
])

const priceOptions = computed(() => [
  { value: 'all' as const, label: t('allProducts') },
  { value: 'under-500' as const, label: t('under500') },
  { value: '500-1000' as const, label: t('between500And1000') },
  { value: '1000-plus' as const, label: t('over1000') },
])

const currentSlug = computed(() => String(route.params.slug || ''))
const activeRootMenu = computed(() =>
  menuItems.value.find(
    (item) =>
      item.slug === currentSlug.value ||
      (Array.isArray(item.children) &&
        item.children.some((child: any) => child.slug === currentSlug.value)),
  ) || null,
)
const isRootCollection = computed(
  () => activeRootMenu.value?.slug === currentSlug.value,
)
const collectionOptions = computed(() =>
  isRootCollection.value && Array.isArray(activeRootMenu.value?.children)
    ? activeRootMenu.value.children
    : [],
)
const selectedSortLabel = computed(
  () =>
    sortOptions.value.find((item) => item.value === sortBy.value)?.label ||
    t('sortDefault'),
)
const canLoadMore = computed(
  () => !loading.value && !loadingMore.value && products.value.length < total.value,
)

const activeFilterLabels = computed(() => {
  const labels: Array<{ key: string; label: string; clear: () => void }> = []

  if (selectedChildSlug.value && isRootCollection.value) {
    const collection = collectionOptions.value.find(
      (item: any) => item.slug === selectedChildSlug.value,
    )
    if (collection) {
      labels.push({
        key: 'collection',
        label: collection.name,
        clear: () => {
          selectedChildSlug.value = ''
        },
      })
    }
  }

  if (stockFilter.value !== 'all') {
    labels.push({
      key: 'stock',
      label:
        stockFilter.value === 'in-stock' ? t('inStock') : t('outOfStock'),
      clear: () => {
        stockFilter.value = 'all'
      },
    })
  }

  if (priceFilter.value !== 'all') {
    const priceLabel =
      priceOptions.value.find((item) => item.value === priceFilter.value)?.label ||
      priceFilter.value
    labels.push({
      key: 'price',
      label: priceLabel,
      clear: () => {
        priceFilter.value = 'all'
      },
    })
  }

  return labels
})

const normalizeQueryValue = (value: unknown) =>
  Array.isArray(value) ? String(value[0] || '') : String(value || '')

const serializeQuery = (query: Record<string, string>) =>
  JSON.stringify(
    Object.keys(query)
      .sort()
      .reduce<Record<string, string>>((result, key) => {
        result[key] = query[key]
        return result
      }, {}),
  )

const applyRouteState = () => {
  syncingFromRoute.value = true

  const nextSort = normalizeQueryValue(route.query.sort) as SortValue
  sortBy.value = sortOptions.value.some((item) => item.value === nextSort)
    ? nextSort
    : 'featured'

  const nextStock = normalizeQueryValue(route.query.stock) as StockValue
  stockFilter.value = stockOptions.value.some((item) => item.value === nextStock)
    ? nextStock
    : 'all'

  const nextPrice = normalizeQueryValue(route.query.price) as PriceValue
  priceFilter.value = priceOptions.value.some((item) => item.value === nextPrice)
    ? nextPrice
    : 'all'

  const nextChild = normalizeQueryValue(route.query.collection)
  selectedChildSlug.value = nextChild

  syncingFromRoute.value = false
}

const buildProductQuery = (pageNum: number) => {
  const query: Record<string, string | number> = {
    pageNum,
    pageSize: PAGE_SIZE,
  }

  if (sortBy.value !== 'featured') {
    query.sort = sortBy.value
  }
  if (stockFilter.value !== 'all') {
    query.stock = stockFilter.value
  }
  if (priceFilter.value === 'under-500') {
    query.maxPrice = 500
  } else if (priceFilter.value === '500-1000') {
    query.minPrice = 500
    query.maxPrice = 1000
  } else if (priceFilter.value === '1000-plus') {
    query.minPrice = 1000
  }
  if (isRootCollection.value && selectedChildSlug.value) {
    query.childSlug = selectedChildSlug.value
  }

  return query
}

const syncRouteQuery = async () => {
  const nextQuery: Record<string, string> = {}
  if (sortBy.value !== 'featured') {
    nextQuery.sort = sortBy.value
  }
  if (stockFilter.value !== 'all') {
    nextQuery.stock = stockFilter.value
  }
  if (priceFilter.value !== 'all') {
    nextQuery.price = priceFilter.value
  }
  if (isRootCollection.value && selectedChildSlug.value) {
    nextQuery.collection = selectedChildSlug.value
  }

  const currentQuery = Object.fromEntries(
    Object.entries(route.query).map(([key, value]) => [key, normalizeQueryValue(value)]),
  )
  if (serializeQuery(currentQuery) === serializeQuery(nextQuery)) {
    return
  }

  await router.replace({
    query: nextQuery,
  })
}

const fetchMenu = async () => {
  const res = await useHttp('/api/category/menu', {
    query: { productLimit: 4 },
  })
  menuItems.value = res?.code === 200 ? res.data || [] : []
}

const fetchCategory = async () => {
  const res = await useHttp(`/api/category/${currentSlug.value}`)
  category.value = res?.code === 200 ? res.data : null
}

const fetchProducts = async (pageNum = 1) => {
  const res = await useHttp(`/api/category/${currentSlug.value}/products`, {
    query: buildProductQuery(pageNum),
  })

  if (res?.code !== 200) {
    if (pageNum === 1) {
      products.value = []
      total.value = 0
    }
    return
  }

  const records = Array.isArray(res.data?.records) ? res.data.records : []
  total.value = Number(res.data?.total || 0)
  if (pageNum === 1) {
    products.value = records
  } else {
    const seen = new Set(products.value.map((item) => item.id))
    products.value = [...products.value, ...records.filter((item) => !seen.has(item.id))]
  }
}

const refreshCollection = async () => {
  loading.value = true
  currentPage.value = 1
  isFilterOpen.value = false
  isSortOpen.value = false

  try {
    await Promise.all([fetchMenu(), fetchCategory()])
    if (!isRootCollection.value && selectedChildSlug.value) {
      selectedChildSlug.value = ''
      await syncRouteQuery()
      return
    }
    if (
      selectedChildSlug.value &&
      !collectionOptions.value.some(
        (item: any) => item.slug === selectedChildSlug.value,
      )
    ) {
      selectedChildSlug.value = ''
      await syncRouteQuery()
      return
    }
    await fetchProducts(1)
  } catch (error) {
    category.value = null
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (!canLoadMore.value) {
    return
  }

  loadingMore.value = true
  currentPage.value += 1
  try {
    await fetchProducts(currentPage.value)
  } finally {
    loadingMore.value = false
  }
}

const selectSort = (value: SortValue) => {
  sortBy.value = value
  isSortOpen.value = false
}

const resetFilters = () => {
  selectedChildSlug.value = ''
  stockFilter.value = 'all'
  priceFilter.value = 'all'
  sortBy.value = 'featured'
}

watch(
  () => route.fullPath,
  async () => {
    applyRouteState()
    await refreshCollection()
  },
  { immediate: true },
)

watch([sortBy, stockFilter, priceFilter, selectedChildSlug], async () => {
  if (syncingFromRoute.value) {
    return
  }
  await syncRouteQuery()
})
</script>

<style scoped lang="scss">
.collection-page {
  padding: 20px 0 70px;
  background: #fff;
}

.collection-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding-bottom: 24px;

  .heading-copy {
    max-width: 820px;
  }

  .eyebrow {
    margin: 0 0 10px;
    font-size: 12px;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: #98a2b3;
  }

  .title {
    margin: 0;
    font-size: 48px;
    line-height: 0.96;
    font-weight: 800;
    color: #111;
  }

  .description {
    margin: 14px 0 0;
    font-size: 16px;
    line-height: 1.6;
    color: #475467;
  }

  .result-count {
    margin: 0 0 8px;
    white-space: nowrap;
    font-size: 15px;
    color: #344054;
  }
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 0 26px;
  border-top: 1px solid #f0f1f3;

  .filter-trigger {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    height: 52px;
    padding: 0 20px;
    border-radius: 999px;
    border: 1px solid #101828;
    background: #fff;
    color: #101828;
    font-size: 15px;
    font-weight: 600;

    .icon {
      width: 18px;
      height: 18px;
    }
  }

  .toolbar-right {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 14px;
    flex: 1;
  }
}

.active-filter-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.active-filter-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 14px;
  border: 1px solid #d0d5dd;
  border-radius: 999px;
  background: #fff;
  color: #344054;
  font-size: 13px;
  font-weight: 600;

  .chip-icon {
    width: 14px;
    height: 14px;
  }
}

.sort-control {
  display: inline-flex;
  align-items: center;
  gap: 20px;
}

.sort-label {
  font-size: 15px;
  color: #111;
}

.sort-trigger {
  display: inline-flex;
  align-items: center;
  gap: 18px;
  min-width: 194px;
  justify-content: center;
  height: 74px;
  padding: 0 34px;
  border-radius: 999px;
  border: 2px solid #101010;
  background: #fff;
  color: #101010;

  .sort-value {
    font-size: 18px;
    font-weight: 700;
  }

  .sort-dot {
    width: 6px;
    height: 6px;
    border-radius: 999px;
    background: #101010;
  }
}

.product-list {
  .grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 22px;
  }
}

.show-more-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.show-more-btn,
.reset-btn {
  min-width: 180px;
  height: 52px;
  border-radius: 999px;
  border: 1px solid #101828;
  background: #fff;
  color: #101828;
  font-size: 15px;
  font-weight: 600;
}

.empty-state {
  padding: 60px 20px;
  border-radius: 28px;
  background: linear-gradient(180deg, #fafafa 0%, #f4f5f6 100%);
  text-align: center;

  p {
    margin: 0 0 18px;
    font-size: 16px;
    color: #475467;
  }
}

.panel-overlay,
.sort-overlay {
  position: fixed;
  inset: 0;
  z-index: 160;
  display: flex;
  backdrop-filter: blur(10px);
}

.panel-overlay {
  background: rgba(15, 23, 42, 0.36);
  justify-content: flex-start;
  align-items: flex-start;
  padding: 104px 24px 24px;
}

.sort-overlay {
  background: rgba(15, 23, 42, 0.3);
  justify-content: flex-end;
  align-items: flex-start;
  padding: 104px 24px 24px;
}

.filter-panel,
.sort-panel {
  box-shadow: 0 28px 80px rgba(15, 23, 42, 0.18);
}

.filter-panel {
  width: min(420px, calc(100vw - 48px));
  max-height: calc(100vh - 128px);
  background: #fff;
  padding: 26px 24px 28px;
  display: flex;
  flex-direction: column;
  border-radius: 32px;
}

.sort-panel {
  width: min(390px, calc(100vw - 24px));
  max-height: calc(100vh - 128px);
  border-radius: 30px;
  background: #171717;
  color: #fff;
  padding: 22px 22px 26px;
  overflow-y: auto;
}

.panel-head,
.sort-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;

  h2 {
    margin: 0;
    font-size: 28px;
    line-height: 1.05;
    color: #111827;
  }
}

.sort-head {
  margin-bottom: 14px;
}

.panel-kicker {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #667085;
}

.panel-close {
  width: 48px;
  height: 48px;
  border: 1px solid #e4e7ec;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: #111827;

  .icon {
    width: 22px;
    height: 22px;
  }

  &.light {
    border-color: transparent;
  }
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 26px 0;
}

.filter-group + .filter-group {
  margin-top: 28px;
}

.group-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.filter-group h3 {
  margin: 0 0 14px;
  font-size: 18px;
  color: #111827;
}

.clear-link {
  border: none;
  background: none;
  color: #667085;
  font-size: 13px;
  font-weight: 700;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-option {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-height: 44px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid #d0d5dd;
  background: #fff;
  color: #344054;
  font-size: 14px;
  font-weight: 600;

  &.is-selected {
    border-color: #111827;
    background: #111827;
    color: #fff;
  }

  .option-meta {
    font-size: 12px;
    opacity: 0.75;
  }
}

.panel-foot {
  display: flex;
  align-items: center;
  gap: 12px;

  .secondary-btn,
  .primary-btn {
    flex: 1;
    height: 54px;
    border-radius: 999px;
    font-size: 15px;
    font-weight: 700;
  }

  .secondary-btn {
    border: 1px solid #d0d5dd;
    background: #fff;
    color: #344054;
  }

  .primary-btn {
    border: 1px solid #111827;
    background: #111827;
    color: #fff;
  }
}

.sort-options {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sort-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.72);
  font-size: 18px;
  font-weight: 700;
  text-align: left;

  &.is-selected {
    color: #fff;

    .sort-check {
      background: rgba(255, 255, 255, 0.92);
    }
  }

  .sort-check {
    width: 8px;
    height: 8px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.32);
    flex-shrink: 0;
  }
}

.overlay-fade-enter-active,
.overlay-fade-leave-active {
  transition: opacity 0.25s ease;
}

.overlay-fade-enter-from,
.overlay-fade-leave-to {
  opacity: 0;
}

@media (max-width: 1400px) {
  .product-list .grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .collection-header {
    flex-direction: column;
    align-items: flex-start;

    .title {
      font-size: 38px;
    }
  }

  .product-list .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;

    .toolbar-right {
      justify-content: space-between;
    }
  }

  .sort-control {
    justify-content: space-between;
  }
}

@media (max-width: 768px) {
  .collection-page {
    padding-top: 12px;
  }

  .collection-header {
    padding-bottom: 18px;

    .title {
      font-size: 30px;
    }

    .description {
      font-size: 14px;
    }
  }

  .toolbar {
    padding: 14px 0 20px;

    .toolbar-right {
      flex-direction: column;
      align-items: stretch;
    }
  }

  .active-filter-list {
    justify-content: flex-start;
  }

  .sort-control {
    width: 100%;
    gap: 12px;
    justify-content: space-between;
  }

  .sort-trigger,
  .filter-trigger {
    width: 100%;
  }

  .product-list .grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .sort-overlay {
    padding: 88px 12px 12px;
    align-items: flex-end;
  }

  .sort-panel {
    width: 100%;
    border-radius: 28px;
    max-height: calc(100vh - 104px);
  }

  .filter-panel {
    width: 100%;
    max-height: calc(100vh - 104px);
  }

  .panel-overlay {
    padding: 88px 12px 12px;
  }
}
</style>
