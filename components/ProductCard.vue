<template>
  <article
    class="product-card"
    role="link"
    tabindex="0"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
    @click="navigateToDetail"
    @keyup.enter="navigateToDetail"
  >
    <div class="image-wrapper" @mousemove="handleImageHoverMove">
      <div v-if="leftTags.length" class="tags-left">
        <span
          v-for="tag in leftTags"
          :key="tag"
          class="tag-label"
          :class="resolveTagClass(tag)"
        >
          {{ resolveTagLabel(tag) }}
        </span>
      </div>

      <div v-if="hasSpringSale" class="tags-right">
        <div class="spring-sale-badge">
          <span class="text">Spring<br />Sale</span>
        </div>
      </div>

      <div class="image-carousel">
        <div
          class="image-track"
          :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }"
        >
          <img
            v-for="(img, index) in displayImages"
            :key="`${img}-${index}`"
            :src="img"
            :alt="`${product.title} - ${index + 1}`"
            class="main-img"
          />
        </div>
      </div>

      <div v-if="displayImages.length > 1" class="carousel-indicators">
        <span
          v-for="(_, index) in displayImages"
          :key="index"
          class="indicator-dot"
          :class="{ 'is-active': currentImageIndex === index }"
          @mouseenter="updateImageIndex(index)"
        />
      </div>

      <img
        v-if="product.appImage"
        :src="product.appImage"
        :alt="`${product.title} app preview`"
        class="app-preview-img"
      />

      <div class="hover-actions" :class="{ 'is-visible': isHovered }">
        <button
          v-if="!isSoldOut"
          type="button"
          class="btn-action"
          :disabled="actionBusy"
          @click.stop="handleActionClick"
        >
          <span class="btn-text">{{ actionButtonText }}</span>
        </button>
        <button v-else type="button" class="btn-action btn-sold-out" disabled>
          <span class="btn-text">{{ t("soldOut") }}</span>
        </button>
      </div>
    </div>

    <div class="info">
      <h3 class="title">{{ product.title }}</h3>

      <div class="price-area">
        <span v-if="product.price" class="current-price">
          <template v-if="isFrom">{{ fromText }} </template>
          {{ money(product.price) }}
        </span>
        <span v-if="product.compareAtPrice" class="old-price">{{
          money(product.compareAtPrice)
        }}</span>
        <span v-if="savedAmount > 0" class="save-badge">
          {{ saveText }} {{ money(savedAmount) }}
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
  </article>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from "vue";
import {
  ActivityIcon,
  BatteryIcon,
  NavigationIcon,
  ZapIcon,
} from "lucide-vue-next";
import { mergeProductImages } from "~/utils/productMedia";

const props = defineProps<{
  product: Record<string, any>;
}>();

const { lang, t } = useShopLocale();
const { money } = useShopFormat();
const quickView = useQuickView();
const { addSingleSkuToCart, productHasOptions } = useProductQuickActions();

const isHovered = ref(false);
const currentImageIndex = ref(0);
const actionBusy = ref(false);

const iconMap = {
  ZapIcon,
  NavigationIcon,
  ActivityIcon,
  BatteryIcon,
} as const;

const displayImages = computed(() => {
  const images = mergeProductImages(
    props.product.images,
    props.product.image,
    props.product.pic,
  ).slice(0, 3);
  return images.length
    ? images
    : ["https://via.placeholder.com/600x600?text=isinwheel"];
});

const leftTags = computed(() => {
  const rawTags = Array.isArray(props.product.tags) ? props.product.tags : [];
  const tags = [...rawTags];
  if (
    props.product.isNew &&
    !rawTags.some((tag) => String(tag).toUpperCase() === "NEW")
  ) {
    tags.unshift("NEW");
  }
  return tags.filter((tag) =>
    ["NEW", "HOT"].includes(String(tag).toUpperCase()),
  );
});

const hasSpringSale = computed(() => {
  const tags = Array.isArray(props.product.tags) ? props.product.tags : [];
  return tags.some((tag) => String(tag).toLowerCase().includes("spring sale"));
});

const isSoldOut = computed(() => {
  const skuList = Array.isArray(props.product.skuList)
    ? props.product.skuList
    : [];
  if (skuList.length) {
    return !skuList.some(
      (sku) =>
        (sku.status || "ACTIVE") === "ACTIVE" && Number(sku.stock || 0) > 0,
    );
  }
  return Number(props.product.stock || 0) <= 0;
});

const hasOptions = computed(() => productHasOptions(props.product));

const actionText = computed(() =>
  hasOptions.value ? t("chooseOptions") : t("addToCart"),
);
const actionButtonText = computed(() =>
  actionBusy.value && !hasOptions.value ? t("adding") : actionText.value,
);

const isFrom = computed(() => {
  const skuList = Array.isArray(props.product.skuList)
    ? props.product.skuList
    : [];
  const uniquePrices = new Set(
    skuList.map((sku) => Number(sku.price || 0)).filter(Boolean),
  );
  return uniquePrices.size > 1;
});

const savedAmount = computed(() => {
  const compareAtPrice = Number(props.product.compareAtPrice || 0);
  const price = Number(props.product.price || 0);
  return compareAtPrice > price ? compareAtPrice - price : 0;
});

const resolvedSpecs = computed(() =>
  (Array.isArray(props.product.specs) ? props.product.specs : [])
    .slice(0, 4)
    .map((spec: any) => ({
      ...spec,
      icon:
        iconMap[(spec.icon || "ActivityIcon") as keyof typeof iconMap] ||
        ActivityIcon,
    })),
);

const productLink = computed(
  () => `/products/${props.product.slug || props.product.id}`,
);
const fromText = computed(() => (lang.value === "zh" ? "起" : "From"));
const saveText = computed(() => (lang.value === "zh" ? "立省" : "Save"));

const resolveTagClass = (tag: string) =>
  String(tag).toLowerCase().replace(/\s+/g, "-");

const resolveTagLabel = (tag: string) => {
  const normalized = String(tag).toUpperCase();
  if (normalized === "NEW") {
    return lang.value === "zh" ? "新的" : "NEW";
  }
  if (normalized === "HOT") {
    return lang.value === "zh" ? "热卖" : "HOT";
  }
  return tag;
};

const navigateToDetail = async () => {
  await navigateTo(productLink.value);
};

const handleActionClick = async () => {
  if (isSoldOut.value || actionBusy.value) {
    return;
  }

  if (hasOptions.value) {
    quickView.openQuickView(props.product);
    return;
  }

  actionBusy.value = true;
  try {
    const result = await addSingleSkuToCart(props.product);
    if (result.requiresOptions && result.product) {
      quickView.openQuickView(result.product);
    }
  } finally {
    actionBusy.value = false;
  }
};

let carouselTimer: ReturnType<typeof setInterval> | null = null;

const updateImageIndex = (nextIndex: number) => {
  const total = displayImages.value.length;
  if (total <= 1) return;

  const safeIndex = Math.min(total - 1, Math.max(0, nextIndex));
  if (safeIndex === currentImageIndex.value) return;

  currentImageIndex.value = safeIndex;
};

const startCarousel = () => {
  if (displayImages.value.length <= 1) return;
  carouselTimer = setInterval(() => {
    updateImageIndex(
      (currentImageIndex.value + 1) % displayImages.value.length,
    );
  }, 3000);
};

const stopCarousel = () => {
  if (carouselTimer) {
    clearInterval(carouselTimer);
    carouselTimer = null;
  }
};

onMounted(() => {
  startCarousel();
});

onUnmounted(() => {
  stopCarousel();
});

const handleMouseEnter = () => {
  isHovered.value = true;
  stopCarousel();
};

const handleImageHoverMove = (event: MouseEvent) => {
  const total = displayImages.value.length;
  if (total <= 1) return;

  const currentTarget = event.currentTarget as HTMLElement | null;
  if (!currentTarget) return;

  const rect = currentTarget.getBoundingClientRect();
  const relativeX = event.clientX - rect.left;
  const segmentWidth = rect.width / total;
  const nextIndex = Math.min(
    total - 1,
    Math.max(0, Math.floor(relativeX / segmentWidth)),
  );

  updateImageIndex(nextIndex);
};

const handleMouseLeave = () => {
  isHovered.value = false;
  updateImageIndex(0);
  startCarousel();
};
</script>

<style scoped lang="scss">
.product-card {
  border-radius: 12px;
  background: #fff;
  transition: box-shadow 0.3s ease;
  border: 1px solid $border-color;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  cursor: pointer;

  &:hover {
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  }
}

.image-wrapper {
  position: relative;
  padding-top: 100%;
  background: linear-gradient(180deg, #fff 0%, #f7f7f7 100%);
  overflow: hidden;

  .tags-left {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 2;

    .tag-label {
      display: inline-block;
      color: $white;
      font-size: 12px;
      padding: 4px 10px;
      font-weight: bold;
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
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;

    .image-track {
      display: flex;
      width: 100%;
      height: 100%;
      transition: transform 0.38s ease;
      will-change: transform;
    }

    .main-img {
      flex: 0 0 100%;
      width: 100%;
      height: 100%;
      object-fit: cover;
      padding: 0;
      display: block;
    }
  }

  .carousel-indicators {
    position: absolute;
    bottom: 12px;
    left: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    gap: 12px;
    z-index: 5;
    padding: 10px 0;

    .indicator-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #2d2d2d;
      border: none;
      cursor: pointer;
      transition:
        width 0.22s ease,
        height 0.22s ease,
        background 0.22s ease,
        border-color 0.22s ease,
        transform 0.22s ease;

      &.is-active {
        width: 18px;
        height: 10px;
        border-radius: 999px;
        background: #fff;
        border: 2px solid #2d2d2d;
        transform: translateY(-1px);
      }

      &:hover:not(.is-active) {
        background: #111;
      }
    }
  }

  .app-preview-img {
    position: absolute;
    top: 16px;
    right: 16px;
    width: 44px;
    height: auto;
    object-fit: contain;
    z-index: 10;
    background: #fff;
    padding: 2px;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  }

  .hover-actions {
    position: absolute;
    bottom: 52px;
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
      overflow: hidden;
      background: #111;
      color: $white;
      padding: 12px 32px;
      border-radius: 30px;
      font-weight: 600;
      font-size: 15px;
      border: none;
      cursor: pointer;
      transition:
        color 0.28s ease,
        transform 0.3s ease,
        box-shadow 0.3s ease;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);

      &::before {
        content: "";
        position: absolute;
        left: -14%;
        bottom: -250%;
        width: 128%;
        height: 240%;
        background: #fff;
        border-radius: 44% 56% 0 0 / 18% 18% 0 0;
        transform: translateY(0);
        transition: bottom 0.42s ease;
      }

      &::after {
        content: "";
        position: absolute;
        left: -8%;
        bottom: -250%;
        width: 116%;
        height: 225%;
        background: rgba(255, 255, 255, 0.92);
        border-radius: 52% 48% 0 0 / 22% 18% 0 0;
        transition: bottom 0.5s ease;
      }

      .btn-text {
        position: relative;
        z-index: 1;
      }

      &:hover:not(:disabled) {
        color: #111;
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);

        &::before {
          bottom: -84%;
        }

        &::after {
          bottom: -94%;
        }
      }

      &.btn-sold-out {
        background: rgba(0, 0, 0, 0.6);
        color: $white;
        cursor: not-allowed;
        box-shadow: none;

        &::before {
          display: none;
        }
      }
    }
  }
}

.info {
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
    color: $text-color;
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
      color: $text-light;
      font-size: 14px;
      position: relative;

      &::after {
        content: "";
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
      color: $white;
      font-size: 13px;
      padding: 4px 12px;
      border-radius: 16px;
      font-weight: 600;
    }
  }

  .specs-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: 1fr 1fr;
    margin-top: auto;
    border-top: 1px solid $border-color;
    padding-top: 16px;

    .spec-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;

      &:nth-child(odd) {
        border-right: 1px solid $border-color;
        padding-right: 12px;
      }

      &:nth-child(even) {
        padding-left: 12px;
      }

      &:nth-child(1),
      &:nth-child(2) {
        border-bottom: 1px solid $border-color;
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
          color: $text-color;
          margin-bottom: 2px;
        }

        .label {
          font-size: 11px;
          color: $text-light;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .info {
    padding: 16px;
  }
}
</style>
