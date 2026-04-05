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
            <span class="star-icon">&#9733;</span>
            <span>Trustpilot</span>
            <span class="rating-stars">
              <span class="star">&#9733;</span><span class="star">&#9733;</span><span class="star">&#9733;</span><span class="star">&#9733;</span><span class="star half">&#9733;</span>
            </span>
            <span class="score">{{ menuData.banner.trustpilot }}</span>
          </div>

          <NuxtLink :to="menuData.banner.linkUrl" class="combo-link">
            {{ menuData.banner.linkText }}
            <span class="arrow">&rarr;</span>
          </NuxtLink>
        </div>
      </div>

      <div class="content-area">
        <div class="content-header">
          <span class="title">{{ copy.popular }}</span>
          <NuxtLink :to="activeGroup?.allLinkUrl || `/collections/${menuData.slug}`" class="view-all">
            {{ activeGroup?.allLinkText || fallbackViewAll }}
            <span class="arrow">&rarr;</span>
          </NuxtLink>
        </div>

        <div v-if="activeProducts.length" class="products-grid">
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
                  :disabled="actioningProductId === product.id"
                  @click.stop="handleProductAction(product)"
                >
                  {{ actionLabel(product) }}
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

        <div v-else class="products-empty-state">
          <p>{{ t('emptyProducts') }}</p>
          <NuxtLink :to="activeGroup?.allLinkUrl || `/collections/${menuData.slug}`" class="empty-link">
            {{ activeGroup?.allLinkText || fallbackViewAll }}
            <span class="arrow">&rarr;</span>
          </NuxtLink>
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
const quickView = useQuickView()
const { addSingleSkuToCart, productHasOptions } = useProductQuickActions()

const activeGroupId = ref<number | null>(null)
const hoveredProductId = ref<number | null>(null)
const activeImageIndices = ref<Record<number, number>>({})
const hoverTimers = ref<Record<number, ReturnType<typeof window.setInterval>>>({})
const actioningProductId = ref<number | null>(null)

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
    actioningProductId.value = null
    activeImageIndices.value = {}
  },
  { immediate: true },
)

const goToDetail = async (product: Record<string, any>) => {
  await router.push(`/products/${product.slug || product.id}`)
}

const handleProductAction = async (product: Record<string, any>) => {
  if (actioningProductId.value === product.id || isSoldOut(product)) {
    return
  }

  if (hasOptions(product)) {
    quickView.openQuickView(product)
    return
  }

  actioningProductId.value = product.id
  try {
    const result = await addSingleSkuToCart(product)
    if (result.requiresOptions && result.product) {
      quickView.openQuickView(result.product)
    }
  } finally {
    actioningProductId.value = null
  }
}

const actionLabel = (product: Record<string, any>) => {
  if (actioningProductId.value === product.id && !hasOptions(product)) {
    return t('adding')
  }
  return hasOptions(product) ? t('chooseOptions') : t('addToCart')
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

const hasOptions = (product: Record<string, any>) => productHasOptions(product)

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
  top: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border-top: 1px solid #ececec;
  padding: 30px 0 34px;
  cursor: default;
  z-index: 100;
}

.mega-menu-inner {
  max-width: 1760px;
  margin: 0 auto;
  display: flex;
  gap: 60px;
  padding: 0 40px;
}

.sidebar {
  position: relative;
  width: 250px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.discount-tag {
  position: absolute;
  left: -20px;
  top: 50%;
  transform: translateY(-50%) translateX(-50%) rotate(-90deg);
  background-color: #58cc02;
  color: #fff;
  font-weight: 800;
  font-size: 14px;
  padding: 6px 16px;
  border-radius: 4px 4px 0 0;
  letter-spacing: 1px;
  white-space: nowrap;
}

.categories {
  margin-bottom: 20px;
}

.section-title {
  padding: 0 16px;
  margin-bottom: 16px;
  color: #111;
  font-size: 14px;
}

.categories ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.categories li {
  padding: 12px 16px;
  margin-bottom: 4px;
  color: #8d8d8d;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    color: #111;
  }

  &.active {
    background: linear-gradient(90deg, #bcf093 0%, #eefbe2 100%);
    color: #111;
    font-weight: 800;
    border-left: 6px solid #58cc02;
  }
}

.sidebar-bottom {
  margin-top: 30px;
  padding-right: 20px;
}

.trustpilot {
  border: 1px solid #e6e6e6;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  margin-bottom: 16px;
  color: #111;
}

.star-icon {
  color: #00b67a;
  font-size: 16px;
}

.rating-stars {
  display: flex;
  background: #00b67a;
  padding: 2px 4px;
  border-radius: 2px;
}

.rating-stars .star {
  color: #fff;
  font-size: 10px;
}

.score {
  font-weight: 700;
}

.combo-link {
  font-weight: 700;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #111;
  text-decoration: none;
  padding-bottom: 8px;
  border-bottom: 1px solid #e6e6e6;

  &:hover {
    color: #58cc02;
  }
}

.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
}

.content-header .title {
  font-size: 12px;
  color: #9d9d9d;
  font-weight: 600;
  letter-spacing: 1px;
}

.view-all {
  font-size: 14px;
  font-weight: 600;
  color: #111;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover {
    color: #58cc02;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.products-empty-state {
  min-height: 320px;
  border: 1px dashed #d9d9d9;
  border-radius: 18px;
  background: linear-gradient(180deg, #fcfcfc 0%, #f7f7f7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 16px;
  color: #6b7280;
  text-align: center;
}

.empty-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #111;
  font-weight: 700;
  text-decoration: none;
}

.empty-link:hover {
  color: #58cc02;
}

.product-card {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-4px);
  }

  &:hover .image-wrapper {
    border-color: #eee;
  }

  &:hover .image-wrapper .main-img.is-active {
    transform: scale(1.05);
  }

  &:hover .info .title {
    color: #58cc02;
  }
}

.image-wrapper {
  position: relative;
  background: #fff;
  border-radius: 12px;
  border: 1px solid transparent;
  padding-top: 100%;
  margin-bottom: 16px;
  overflow: hidden;
  transition: border-color 0.3s ease;
}

.tags-left {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 2;
}

.tags-right {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
}

.tag-label {
  display: inline-block;
  color: #fff;
  font-size: 10px;
  padding: 4px 8px;
  font-weight: 700;
  text-transform: uppercase;
  border-radius: 0 0 8px 0;
}

.tag-label.new {
  background: #e62332;
}

.tag-label.hot {
  background: #ff5722;
}

.spring-sale-badge {
  background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
  border: 2px solid #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  padding: 4px 10px;
  transform: rotate(5deg);
}

.spring-sale-badge .text {
  color: #2e7d32;
  font-weight: 900;
  font-size: 10px;
  line-height: 1.1;
  display: block;
  text-align: center;
  text-transform: uppercase;
  text-shadow: 1px 1px 0 rgba(255, 255, 255, 0.5);
}

.image-carousel {
  position: absolute;
  inset: 0;
}

.main-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.4s ease, transform 0.4s ease;

  &.is-active {
    opacity: 1;
    z-index: 1;
  }
}

.carousel-indicators {
  position: absolute;
  bottom: 70px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 6px;
  z-index: 15;
  padding: 10px 0;
}

.indicator-dot {
  width: 6px;
  height: 6px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);

  &.is-active {
    width: 16px;
    background: #111;
  }

  &:hover:not(.is-active) {
    background: rgba(0, 0, 0, 0.6);
  }
}

.app-preview-img {
  position: absolute;
  top: 60px;
  right: 10px;
  width: 40px;
  height: auto;
  object-fit: contain;
  z-index: 10;
  background: #fff;
  padding: 2px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.hover-actions {
  position: absolute;
  bottom: 15px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
  z-index: 10;

  &.is-visible {
    opacity: 1;
    transform: translateY(0);
  }
}

.btn-action {
  background: #111;
  color: #fff;
  padding: 10px 20px;
  border-radius: 30px;
  font-weight: 600;
  font-size: 13px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);

  &:hover:not(:disabled) {
    background: #333;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  }

  &.btn-sold-out {
    background: rgba(0, 0, 0, 0.6);
    color: #fff;
    cursor: not-allowed;
    box-shadow: none;
  }
}

.info {
  display: flex;
  flex-direction: column;
}

.info .title {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 8px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.3s ease;
}

.price-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-price {
  color: #e62332;
  font-weight: 700;
  font-size: 16px;
}

.old-price {
  color: #999;
  font-size: 12px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    left: -2px;
    right: -2px;
    top: 50%;
    height: 1px;
    background-color: #e62332;
    transform: rotate(-10deg);
  }
}

@media (max-width: 1440px) {
  .products-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1240px) {
  .mega-menu-inner {
    gap: 32px;
    padding: 0 24px;
  }

  .sidebar {
    width: 220px;
  }

  .products-grid {
    gap: 16px;
  }
}
</style>
