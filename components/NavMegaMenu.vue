<template>
  <div class="mega-menu">
    <div class="mega-menu-inner container">
      <div class="sidebar">
        <div v-if="menuData.banner?.tag" class="discount-tag">
          {{ menuData.banner.tag }}
        </div>

        <div class="categories">
          <div class="section-title">{{ copy.collections }}</div>
          <ul>
            <li
              v-for="group in menuGroups"
              :key="group.id"
              :class="{ active: activeGroupId === group.id }"
              @mouseenter="activeGroupId = group.id"
            >
              {{ group.name }}
            </li>
          </ul>
        </div>

        <div v-if="menuData.banner" class="sidebar-bottom">
          <div v-if="menuData.banner.trustpilot" class="trustpilot">
            <span class="star-icon">★</span>
            <span>Trustpilot</span>
            <span class="rating-stars">
              <span class="star">★</span><span class="star">★</span><span class="star">★</span><span class="star">★</span><span class="star half">★</span>
            </span>
            <span class="score">{{ menuData.banner.trustpilot }}</span>
          </div>

          <NuxtLink :to="menuData.banner.linkUrl" class="combo-link">
            {{ menuData.banner.linkText }}
            <span class="arrow">→</span>
          </NuxtLink>
        </div>
      </div>

      <div class="content-area">
        <div class="content-header">
          <span class="title">{{ copy.popular }}</span>
          <NuxtLink :to="activeGroup?.allLinkUrl || `/collections/${menuData.slug}`" class="view-all">
            {{ activeGroup?.allLinkText || fallbackViewAll }}
            <span class="arrow">→</span>
          </NuxtLink>
        </div>

        <div class="products-grid">
          <article
            v-for="product in activeProducts"
            :key="product.id"
            class="product-card"
            @mouseenter="handleMouseEnter(product.id)"
            @mouseleave="handleMouseLeave(product.id)"
            @click="goToDetail(product)"
          >
            <div class="image-wrapper">
              <div v-if="leftTags(product).length" class="tags-left">
                <span
                  v-for="tag in leftTags(product)"
                  :key="tag"
                  class="tag-label"
                  :class="tag.toLowerCase()"
                >
                  {{ tag }}
                </span>
              </div>

              <div v-if="hasSpringSale(product)" class="tags-right">
                <div class="spring-sale-badge">
                  <span class="text">Spring<br />Sale</span>
                </div>
              </div>

              <div class="image-carousel">
                <img
                  v-for="(img, index) in displayImages(product)"
                  :key="`${product.id}-${index}`"
                  :src="img"
                  :alt="`${product.title} - ${index + 1}`"
                  class="main-img"
                  :class="{ 'is-active': activeImageIndex(product.id) === index }"
                />
              </div>

              <div
                v-if="displayImages(product).length > 1 && hoveredProductId === product.id"
                class="carousel-indicators"
              >
                <span
                  v-for="(_, index) in displayImages(product)"
                  :key="`${product.id}-dot-${index}`"
                  class="indicator-dot"
                  :class="{ 'is-active': activeImageIndex(product.id) === index }"
                  @mouseenter.stop="setActiveImageIndex(product.id, index)"
                />
              </div>

              <img
                v-if="product.appImage"
                :src="product.appImage"
                :alt="`${product.title}-app`"
                class="app-preview-img"
              />

              <div class="hover-actions" :class="{ 'is-visible': hoveredProductId === product.id || isSoldOut(product) }">
                <button
                  v-if="!isSoldOut(product)"
                  type="button"
                  class="btn-action"
                  @click.stop="goToDetail(product)"
                >
                  {{ hasOptions(product) ? t('chooseOptions') : t('addToCart') }}
                </button>
                <button v-else type="button" class="btn-action btn-sold-out" disabled>
                  {{ t('soldOut') }}
                </button>
              </div>
            </div>

            <div class="info">
              <h3 class="title">{{ product.title }}</h3>
              <div class="price-area">
                <span class="current-price">
                  <template v-if="isFromPrice(product)">From </template>
                  {{ money(product.price) }}
                </span>
                <span v-if="product.compareAtPrice" class="old-price">{{ money(product.compareAtPrice) }}</span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onUnmounted, ref, watch } from 'vue'

const props = defineProps<{
  menuData: Record<string, any>
}>()

const router = useRouter()
const { lang, t } = useShopLocale()
const { money } = useShopFormat()

const activeGroupId = ref<number | null>(null)
const hoveredProductId = ref<number | null>(null)
const activeImageIndices = ref<Record<number, number>>({})
const hoverTimers = ref<Record<number, ReturnType<typeof window.setInterval>>>({})

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        collections: '分类',
        popular: '热门推荐',
      }
    : {
        collections: 'Collections',
        popular: 'MOST POPULAR',
      },
)

const menuGroups = computed(() => props.menuData?.children || [])
const activeGroup = computed(
  () => menuGroups.value.find((group: any) => group.id === activeGroupId.value) || menuGroups.value[0] || null,
)
const activeProducts = computed(() => activeGroup.value?.products || [])
const fallbackViewAll = computed(() => `All ${props.menuData?.name || ''}`.trim())

watch(
  () => props.menuData,
  (menuData) => {
    activeGroupId.value = menuData?.children?.[0]?.id ?? null
    hoveredProductId.value = null
    activeImageIndices.value = {}
  },
  { immediate: true },
)

const goToDetail = async (product: Record<string, any>) => {
  await router.push(`/products/${product.slug || product.id}`)
}

const leftTags = (product: Record<string, any>) =>
  (Array.isArray(product.tags) ? product.tags : []).filter((tag) => ['NEW', 'HOT'].includes(String(tag).toUpperCase()))

const hasSpringSale = (product: Record<string, any>) =>
  (Array.isArray(product.tags) ? product.tags : []).some((tag) => String(tag).toLowerCase().includes('spring sale'))

const displayImages = (product: Record<string, any>) => {
  if (Array.isArray(product.images) && product.images.length) {
    return product.images
  }
  if (product.pic) {
    return [product.pic]
  }
  return ['https://via.placeholder.com/640x640?text=isinwheel']
}

const activeImageIndex = (productId: number) => activeImageIndices.value[productId] || 0

const setActiveImageIndex = (productId: number, index: number) => {
  activeImageIndices.value = {
    ...activeImageIndices.value,
    [productId]: index,
  }
}

const clearHoverTimer = (productId: number) => {
  if (hoverTimers.value[productId]) {
    clearInterval(hoverTimers.value[productId])
    delete hoverTimers.value[productId]
  }
}

const handleMouseEnter = (productId: number) => {
  hoveredProductId.value = productId
  clearHoverTimer(productId)

  const product = activeProducts.value.find((item: any) => item.id === productId)
  const images = product ? displayImages(product) : []
  if (images.length > 1) {
    hoverTimers.value[productId] = window.setInterval(() => {
      const nextIndex = (activeImageIndex(productId) + 1) % images.length
      setActiveImageIndex(productId, nextIndex)
    }, 1400)
  }
}

const handleMouseLeave = (productId: number) => {
  if (hoveredProductId.value === productId) {
    hoveredProductId.value = null
  }
  clearHoverTimer(productId)
  setActiveImageIndex(productId, 0)
}

const isSoldOut = (product: Record<string, any>) => {
  const skuList = Array.isArray(product.skuList) ? product.skuList : []
  if (!skuList.length) {
    return Number(product.stock || 0) <= 0
  }
  return !skuList.some((sku) => Number(sku.stock || 0) > 0 && String(sku.status || 'ACTIVE') !== 'INACTIVE')
}

const hasOptions = (product: Record<string, any>) => Array.isArray(product.skuList) && product.skuList.length > 1

const isFromPrice = (product: Record<string, any>) => {
  const skuList = Array.isArray(product.skuList) ? product.skuList : []
  const uniquePrices = [...new Set(skuList.map((sku) => Number(sku.price || 0)).filter(Boolean))]
  return uniquePrices.length > 1
}

onUnmounted(() => {
  Object.keys(hoverTimers.value).forEach((key) => clearHoverTimer(Number(key)))
})
</script>

<style scoped lang="scss">
.mega-menu {
  position: absolute;
  top: calc(100% + 18px);
  left: 50%;
  transform: translateX(-50%);
  width: min(1760px, calc(100vw - 32px));
  background: #fff;
  border: 1px solid #efefef;
  border-radius: 34px;
  box-shadow: 0 18px 50px rgba(17, 17, 17, 0.12);
  padding: 28px 0 26px;
  cursor: default;
  z-index: 110;
}

.mega-menu-inner {
  display: flex;
  gap: 58px;
  padding: 0 28px;
}

.sidebar {
  position: relative;
  width: 350px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.discount-tag {
  position: absolute;
  left: -28px;
  top: 50%;
  transform: translateY(-50%) rotate(-90deg);
  background: #73f33e;
  color: #fff;
  font-weight: 800;
  font-size: 14px;
  padding: 8px 18px;
  border-radius: 12px 12px 0 0;
  white-space: nowrap;
}

.section-title {
  margin-bottom: 16px;
  color: #333;
  font-size: 16px;
}

.categories ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.categories li {
  padding: 14px 20px;
  color: #d1d1d1;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover,
  &.active {
    background: linear-gradient(90deg, #c0ff49 0%, #f6fbd7 100%);
    color: #333;
    border-radius: 0 16px 16px 0;
  }
}

.sidebar-bottom {
  margin-top: 26px;
  padding-right: 12px;
}

.trustpilot {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 16px;
  color: #222;
  font-size: 14px;
}

.star-icon {
  color: #00b67a;
}

.rating-stars {
  display: inline-flex;
  gap: 1px;
  padding: 2px 4px;
  border-radius: 6px;
  background: #00b67a;
  color: #fff;
  font-size: 11px;
}

.combo-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 18px;
  border-top: 1px solid #202020;
  color: #202020;
  text-decoration: none;
  font-size: 16px;
  font-weight: 700;
}

.content-area {
  flex: 1;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.content-header .title {
  color: #c9c9c9;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.view-all {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #333;
  font-size: 16px;
  font-weight: 700;
  text-decoration: none;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 24px;
}

.product-card {
  cursor: pointer;
}

.image-wrapper {
  position: relative;
  min-height: 310px;
  overflow: hidden;
}

.tags-left,
.tags-right {
  position: absolute;
  top: 10px;
  z-index: 3;
}

.tags-left {
  left: 0;
}

.tags-right {
  right: 8px;
}

.tag-label {
  display: inline-flex;
  padding: 5px 10px;
  color: #fff;
  font-size: 10px;
  font-weight: 700;

  &.new {
    background: #f54337;
  }

  &.hot {
    background: #ff7a00;
  }
}

.spring-sale-badge {
  padding: 5px 10px;
  border-radius: 14px;
  border: 2px solid #fff;
  background: linear-gradient(135deg, #d7ff8f 0%, #9cf482 100%);
  transform: rotate(6deg);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.08);

  .text {
    color: #2b8c2d;
    font-size: 10px;
    font-weight: 900;
    line-height: 1.05;
    text-transform: uppercase;
    text-align: center;
  }
}

.image-carousel {
  position: relative;
  height: 100%;
  min-height: 310px;
}

.main-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  opacity: 0;
  transform: scale(1);
  transition: opacity 0.35s ease, transform 0.35s ease;

  &.is-active {
    opacity: 1;
    z-index: 1;
  }
}

.product-card:hover .main-img.is-active {
  transform: scale(1.04);
}

.carousel-indicators {
  position: absolute;
  left: 50%;
  bottom: 68px;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  z-index: 5;
}

.indicator-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.28);
  transition: all 0.25s ease;

  &.is-active {
    width: 18px;
    background: #111;
  }
}

.app-preview-img {
  position: absolute;
  right: 14px;
  bottom: 74px;
  width: 38px;
  height: 58px;
  object-fit: cover;
  border-radius: 10px;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.hover-actions {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 14px;
  display: flex;
  justify-content: center;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.25s ease;
  z-index: 6;

  &.is-visible {
    opacity: 1;
    transform: translateY(0);
  }
}

.btn-action {
  min-height: 42px;
  padding: 0 20px;
  border: none;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;

  &.btn-sold-out {
    background: rgba(17, 17, 17, 0.55);
    cursor: not-allowed;
  }
}

.info .title {
  margin: 8px 0 10px;
  color: #333;
  font-size: 16px;
  font-weight: 500;
  line-height: 1.4;
}

.price-area {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.current-price {
  color: #ff3f84;
  font-size: 19px;
  font-weight: 700;
}

.old-price {
  color: #a9a9a9;
  font-size: 14px;
  text-decoration: line-through;
}

@media (max-width: 1440px) {
  .products-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
