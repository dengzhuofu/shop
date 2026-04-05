<template>
  <div class="product-card" @mouseenter="isHovered = true" @mouseleave="handleMouseLeave">
    <NuxtLink :to="productLink" class="link-mask" :aria-label="product.title" />

    <div class="image-wrapper">
      <div v-if="leftTags.length" class="tags-left">
        <span v-for="tag in leftTags" :key="tag" class="tag-label" :class="tag.toLowerCase()">
          {{ tag }}
        </span>
      </div>
      <div v-if="hasSpringSale" class="tags-right">
        <div class="spring-sale-badge">
          <span class="text">Spring<br />Sale</span>
        </div>
      </div>

      <div class="image-carousel">
        <img
          v-for="(img, index) in displayImages"
          :key="img"
          :src="img"
          :alt="`${product.title} - ${index + 1}`"
          class="main-img"
          :class="{ 'is-active': currentImageIndex === index }"
        />
      </div>

      <div v-if="displayImages.length > 1 && isHovered" class="carousel-indicators">
        <span
          v-for="(_, index) in displayImages"
          :key="index"
          class="indicator-dot"
          :class="{ 'is-active': currentImageIndex === index }"
          @mouseenter="currentImageIndex = index"
        />
      </div>

      <div class="hover-actions" :class="{ 'is-visible': isHovered || isSoldOut }">
        <button
          v-if="!isSoldOut"
          type="button"
          class="btn-action"
          @click.stop="navigateToDetail"
        >
          {{ t('chooseOptions') }}
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
          <template v-if="isFrom">From </template>
          {{ money(product.price) }}
        </span>
        <span v-if="product.compareAtPrice" class="old-price">{{ money(product.compareAtPrice) }}</span>
        <span v-if="product.compareAtPrice" class="save-badge">
          Save {{ money(Number(product.compareAtPrice) - Number(product.price)) }}
        </span>
      </div>

      <div v-if="resolvedSpecs.length" class="specs-grid">
        <div v-for="spec in resolvedSpecs" :key="spec.label" class="spec-item">
          <component :is="spec.icon" class="spec-icon" />
          <div class="spec-text">
            <span class="value">{{ spec.value }}</span>
            <span class="label">{{ spec.label }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ActivityIcon, BatteryIcon, NavigationIcon, ZapIcon } from 'lucide-vue-next'

const props = defineProps<{
  product: Record<string, any>
}>()

const { t } = useShopLocale()
const { money } = useShopFormat()

const isHovered = ref(false)
const currentImageIndex = ref(0)

const iconMap = {
  ZapIcon,
  NavigationIcon,
  ActivityIcon,
  BatteryIcon,
} as const

const displayImages = computed(() => {
  if (Array.isArray(props.product.images) && props.product.images.length) {
    return props.product.images
  }
  if (props.product.pic) {
    return [props.product.pic]
  }
  return ['https://via.placeholder.com/600x600?text=isinwheel']
})

const leftTags = computed(() => {
  const tags = Array.isArray(props.product.tags) ? props.product.tags : []
  return tags.filter((tag) => ['NEW', 'HOT'].includes(String(tag).toUpperCase()))
})

const hasSpringSale = computed(() => {
  const tags = Array.isArray(props.product.tags) ? props.product.tags : []
  return tags.some((tag) => String(tag).toLowerCase().includes('spring sale'))
})

const isSoldOut = computed(() => {
  const skuList = Array.isArray(props.product.skuList) ? props.product.skuList : []
  if (skuList.length) {
    return !skuList.some(
      (sku) => (sku.status || 'ACTIVE') === 'ACTIVE' && Number(sku.stock || 0) > 0,
    )
  }
  return Number(props.product.stock || 0) <= 0
})

const isFrom = computed(() => {
  const skuList = Array.isArray(props.product.skuList) ? props.product.skuList : []
  const uniquePrices = new Set(skuList.map((sku) => Number(sku.price || 0)).filter(Boolean))
  return uniquePrices.size > 1
})

const resolvedSpecs = computed(() =>
  (Array.isArray(props.product.specs) ? props.product.specs : [])
    .slice(0, 4)
    .map((spec: any) => ({
      ...spec,
      icon: iconMap[(spec.icon || 'ActivityIcon') as keyof typeof iconMap] || ActivityIcon,
    })),
)

const productLink = computed(() => `/products/${props.product.slug || props.product.id}`)

const navigateToDetail = async () => {
  await navigateTo(productLink.value)
}

const handleMouseLeave = () => {
  isHovered.value = false
  currentImageIndex.value = 0
}
</script>

<style scoped lang="scss">
.product-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  transition: box-shadow 0.3s ease;
  border: 1px solid #eceff3;
  display: flex;
  flex-direction: column;
  height: 100%;

  &:hover {
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  }
}

.link-mask {
  position: absolute;
  inset: 0;
  z-index: 1;
}

.image-wrapper {
  position: relative;
  padding-top: 100%;
  background: #fff;
  overflow: hidden;

  .tags-left {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 2;

    .tag-label {
      display: inline-block;
      color: #fff;
      font-size: 12px;
      padding: 4px 10px;
      font-weight: 700;
      text-transform: uppercase;
      border-radius: 0 0 8px 0;

      &.new {
        background: #e62332;
      }

      &.hot {
        background: #ff5722;
      }
    }
  }

  .tags-right {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 2;

    .spring-sale-badge {
      background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
      border: 2px solid #fff;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      border-radius: 12px;
      padding: 4px 10px;
      transform: rotate(5deg);

      .text {
        color: #2e7d32;
        font-weight: 900;
        font-size: 11px;
        line-height: 1.1;
        display: block;
        text-align: center;
        text-transform: uppercase;
        text-shadow: 1px 1px 0 rgba(255, 255, 255, 0.5);
      }
    }
  }

  .image-carousel {
    position: absolute;
    inset: 0;

    .main-img {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 0;
      transition:
        opacity 0.4s ease,
        transform 0.4s ease;

      &.is-active {
        opacity: 1;
        z-index: 1;
      }
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
  }

  .hover-actions {
    position: absolute;
    bottom: 20px;
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

    .btn-action {
      position: relative;
      z-index: 2;
      background: #111;
      color: #fff;
      padding: 12px 32px;
      border-radius: 30px;
      font-weight: 600;
      font-size: 15px;
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
  }
}

.info {
  position: relative;
  z-index: 2;
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;

  .title {
    font-size: 18px;
    font-weight: 500;
    margin: 0 0 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.4;
    color: #111827;
  }

  .price-area {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 20px;

    .current-price {
      color: #e62332;
      font-size: 22px;
      font-weight: 600;
    }

    .old-price {
      color: #98a2b3;
      font-size: 14px;
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

    .save-badge {
      background: #e62332;
      color: #fff;
      font-size: 13px;
      padding: 4px 12px;
      border-radius: 16px;
      font-weight: 600;
    }
  }

  .specs-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    margin-top: auto;
    border-top: 1px solid #eceff3;
    padding-top: 16px;

    .spec-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;

      &:nth-child(odd) {
        border-right: 1px solid #eceff3;
        padding-right: 12px;
      }

      &:nth-child(even) {
        padding-left: 12px;
      }

      &:nth-child(1),
      &:nth-child(2) {
        border-bottom: 1px solid #eceff3;
      }

      .spec-icon {
        width: 24px;
        height: 24px;
        color: #58cc02;
        stroke-width: 1.5;
      }

      .spec-text {
        display: flex;
        flex-direction: column;

        .value {
          font-size: 14px;
          font-weight: 600;
          color: #101828;
          margin-bottom: 2px;
        }

        .label {
          font-size: 11px;
          color: #667085;
        }
      }
    }
  }
}
</style>
