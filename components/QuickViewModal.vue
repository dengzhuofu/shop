<template>
  <Teleport to="body">
    <div v-if="isOpen" class="quick-view-modal" @click="close">
      <div class="modal-shell" @click.stop>
        <button type="button" class="btn-close" @click="close">
          <XIcon class="icon-close" />
        </button>

        <div v-if="loading" class="modal-state">
          <p>{{ t('loadingProduct') }}</p>
        </div>

        <div v-else-if="!productDetail" class="modal-state">
          <p>{{ t('productUnavailable') }}</p>
        </div>

        <div v-else class="modal-layout">
          <section class="gallery-panel">
            <div class="main-image-wrap">
              <div v-if="leftTags.length" class="tags-left">
                <span
                  v-for="tag in leftTags"
                  :key="tag"
                  class="tag-label"
                  :class="String(tag).toLowerCase()"
                >
                  {{ tag }}
                </span>
              </div>

              <div v-if="hasSpringSale" class="tags-right">
                <div class="spring-sale-badge">
                  <span class="text">Spring<br />Sale</span>
                </div>
              </div>

              <img :src="activeImage" :alt="productDetail.title" class="main-image" />

              <div v-if="galleryImages.length > 1" class="gallery-controls">
                <button type="button" class="gallery-nav" @click="cycleImage(-1)">
                  <ChevronLeftIcon class="gallery-nav__icon" />
                </button>
                <span class="gallery-count">{{ activeImageIndex + 1 }} / {{ galleryImages.length }}</span>
                <button type="button" class="gallery-nav" @click="cycleImage(1)">
                  <ChevronRightIcon class="gallery-nav__icon" />
                </button>
              </div>
            </div>

            <div v-if="galleryImages.length > 1" class="thumbs-shell">
              <button
                type="button"
                class="thumb-stepper"
                :disabled="!canSlideThumbsBackward"
                @click="stepThumbs(-1)"
              >
                <ChevronLeftIcon class="thumb-stepper__icon" />
              </button>
              <div class="thumbs-viewport">
                <div class="thumbs-list">
                  <button
                    v-for="(img, index) in visibleThumbImages"
                    :key="`${img}-${thumbStartIndex + index}`"
                    type="button"
                    class="thumb-btn"
                    :class="{ 'is-active': activeImage === img }"
                    @click="activeImage = img"
                  >
                    <img :src="img" :alt="`${productDetail.title}-${thumbStartIndex + index + 1}`" />
                  </button>
                </div>
              </div>
              <button
                type="button"
                class="thumb-stepper"
                :disabled="!canSlideThumbsForward"
                @click="stepThumbs(1)"
              >
                <ChevronRightIcon class="thumb-stepper__icon" />
              </button>
            </div>
          </section>

          <section class="info-panel">
            <p class="brand">isinwheel Official Store</p>
            <h2 class="title">{{ productDetail.title }}</h2>

            <div class="price-row">
              <div class="reviews">
                <StarIcon v-for="index in 5" :key="index" class="star-icon" />
                <span>{{ reviewCount }} reviews</span>
              </div>
              <div class="price-box">
                <span class="current-price">{{ money(selectedPrice) }}</span>
                <span v-if="selectedCompareAtPrice" class="old-price">
                  {{ money(selectedCompareAtPrice) }}
                </span>
              </div>
            </div>

            <div v-if="attributeKeys.length" class="variant-selectors">
              <div v-for="attributeKey in attributeKeys" :key="attributeKey" class="selector-group">
                <p class="selector-label">
                  {{ formatAttributeKey(attributeKey) }}:
                  <strong>{{ selection[attributeKey] }}</strong>
                </p>

                <div v-if="attributeKey === 'color'" class="color-options">
                  <button
                    v-for="option in attributeOptions[attributeKey] || []"
                    :key="option"
                    type="button"
                    class="color-btn"
                    :class="{ 'is-active': selection[attributeKey] === option }"
                    :disabled="!isSelectable(attributeKey, option)"
                    @click="selection[attributeKey] = option"
                  >
                    <img :src="optionThumbnail(attributeKey, option)" :alt="String(option)" />
                  </button>
                </div>

                <div v-else class="options-list">
                  <button
                    v-for="option in attributeOptions[attributeKey] || []"
                    :key="option"
                    type="button"
                    class="text-btn"
                    :class="{ 'is-active': selection[attributeKey] === option }"
                    :disabled="!isSelectable(attributeKey, option)"
                    @click="selection[attributeKey] = option"
                  >
                    {{ option }}
                  </button>
                </div>
              </div>
            </div>

            <div class="stock-notice" :class="{ soldout: !selectedSkuAvailable }">
              {{ selectedSkuAvailable ? t('inStock') : t('soldOut') }}
            </div>

            <div class="actions">
              <div class="quantity-selector">
                <button type="button" class="qty-btn" @click="quantity = Math.max(1, quantity - 1)">
                  <MinusIcon class="qty-icon" />
                </button>
                <input v-model="quantity" type="number" min="1" class="qty-input" />
                <button type="button" class="qty-btn" @click="quantity += 1">
                  <PlusIcon class="qty-icon" />
                </button>
              </div>

              <button
                type="button"
                class="btn-add-to-cart"
                :disabled="!selectedSkuAvailable || adding"
                @click="handleAddToCart"
              >
                {{ adding ? t('adding') : t('addToCart') }}
              </button>
            </div>

            <NuxtLink :to="productLink" class="view-details" @click="close">
              {{ t('viewFullDetails') }}
              <ArrowRightIcon class="detail-icon" />
            </NuxtLink>
          </section>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { ArrowRightIcon, ChevronLeftIcon, ChevronRightIcon, MinusIcon, PlusIcon, StarIcon, XIcon } from 'lucide-vue-next'
import { findInitialSelection, findSelectedSku, isOptionSelectable, isSkuAvailable, sortAttributeKeys } from '~/utils/productSelection'
import { mergeProductImages } from '~/utils/productMedia'

const props = defineProps<{
  isOpen: boolean
  product: Record<string, any> | null
}>()

const emit = defineEmits<{
  close: []
}>()

const { t } = useShopLocale()
const { money } = useShopFormat()
const cart = useShopCart()
const session = useShopSession()
const { fetchProductDetail } = useProductQuickActions()

const loading = ref(false)
const adding = ref(false)
const productDetail = ref<Record<string, any> | null>(null)
const activeImage = ref('')
const thumbStartIndex = ref(0)
const quantity = ref(1)
const selection = reactive<Record<string, string>>({})
let requestToken = 0

const attributeOptions = computed(() => productDetail.value?.skuAttributeOptions || {})
const attributeKeys = computed(() => sortAttributeKeys(Object.keys(attributeOptions.value || {})))
const selectedSku = computed(() => findSelectedSku(productDetail.value?.skuList || [], selection))
const selectedSkuAvailable = computed(() => Boolean(selectedSku.value && isSkuAvailable(selectedSku.value)))
const selectedPrice = computed(() => selectedSku.value?.price || productDetail.value?.price || 0)
const selectedCompareAtPrice = computed(() => selectedSku.value?.compareAtPrice || productDetail.value?.compareAtPrice || 0)
const reviewCount = computed(() => Math.max(Number(productDetail.value?.reviewCount || 0), 61))
const galleryImages = computed(() => {
  return mergeProductImages(
    selectedSku.value?.images,
    selectedSku.value?.pic,
    productDetail.value?.images,
    productDetail.value?.pic,
  )
})
const activeImageIndex = computed(() => {
  const index = galleryImages.value.indexOf(activeImage.value)
  return index >= 0 ? index : 0
})
const THUMB_WINDOW = 4
const visibleThumbImages = computed(() =>
  galleryImages.value.slice(thumbStartIndex.value, thumbStartIndex.value + THUMB_WINDOW),
)
const canSlideThumbsBackward = computed(() => thumbStartIndex.value > 0)
const canSlideThumbsForward = computed(
  () => thumbStartIndex.value + THUMB_WINDOW < galleryImages.value.length,
)
const leftTags = computed(() =>
  (Array.isArray(productDetail.value?.tags) ? productDetail.value.tags : []).filter((tag: string) =>
    ['NEW', 'HOT'].includes(String(tag).toUpperCase()),
  ),
)
const hasSpringSale = computed(() =>
  (Array.isArray(productDetail.value?.tags) ? productDetail.value.tags : []).some((tag: string) =>
    String(tag).toLowerCase().includes('spring sale'),
  ),
)
const productLink = computed(() => `/products/${productDetail.value?.slug || productDetail.value?.id || ''}`)

const syncActiveImage = () => {
  activeImage.value = galleryImages.value[0] || ''
  thumbStartIndex.value = 0
}

const resetSelection = () => {
  const nextSelection = findInitialSelection(productDetail.value?.skuList || [])
  Object.keys(selection).forEach((key) => delete selection[key])
  Object.assign(selection, nextSelection)
}

const formatAttributeKey = (value: string) =>
  ({ color: 'Color', bundle: 'Bundle', style: 'Style' }[value] || value.charAt(0).toUpperCase() + value.slice(1))

const optionThumbnail = (attributeKey: string, option: string) => {
  const skuList = Array.isArray(productDetail.value?.skuList) ? productDetail.value.skuList : []
  const matched = skuList.find((sku: any) => sku.attributes?.[attributeKey] === option)
  return (
    mergeProductImages(matched?.images, matched?.pic, productDetail.value?.images, productDetail.value?.pic)[0] ||
    productDetail.value?.pic
  )
}

const isSelectable = (attributeKey: string, option: string) =>
  isOptionSelectable(productDetail.value?.skuList || [], selection, attributeKey, option)

const cycleImage = (step: number) => {
  if (!galleryImages.value.length) {
    return
  }
  const nextIndex =
    (activeImageIndex.value + step + galleryImages.value.length) % galleryImages.value.length
  activeImage.value = galleryImages.value[nextIndex]
}

const ensureActiveThumbVisible = () => {
  if (galleryImages.value.length <= THUMB_WINDOW) {
    thumbStartIndex.value = 0
    return
  }

  if (activeImageIndex.value < thumbStartIndex.value) {
    thumbStartIndex.value = activeImageIndex.value
    return
  }

  if (activeImageIndex.value >= thumbStartIndex.value + THUMB_WINDOW) {
    thumbStartIndex.value = activeImageIndex.value - THUMB_WINDOW + 1
  }
}

const stepThumbs = (direction: number) => {
  const maxStart = Math.max(galleryImages.value.length - THUMB_WINDOW, 0)
  thumbStartIndex.value = Math.min(
    Math.max(thumbStartIndex.value + direction, 0),
    maxStart,
  )
}

const close = () => {
  emit('close')
}

const syncBodyScroll = (locked: boolean) => {
  if (!process.client) {
    return
  }
  document.body.style.overflow = locked ? 'hidden' : ''
}

const normalizedQuantity = () =>
  Math.max(1, Number.parseInt(String(quantity.value || 1), 10) || 1)

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.isOpen) {
    close()
  }
}

const loadProduct = async () => {
  if (!props.isOpen || !props.product) {
    productDetail.value = null
    return
  }

  const token = ++requestToken
  loading.value = true
  try {
    const detailedProduct = await fetchProductDetail(props.product)
    if (token !== requestToken) {
      return
    }
    productDetail.value = detailedProduct
    quantity.value = 1
    resetSelection()
    syncActiveImage()
  } finally {
    if (token === requestToken) {
      loading.value = false
    }
  }
}

const handleAddToCart = async () => {
  if (!productDetail.value || !selectedSku.value) {
    return
  }

  await session.fetchMe()
  if (!session.isLoggedIn.value) {
    close()
    await navigateTo('/login')
    return
  }

  adding.value = true
  try {
    const res = await cart.addToCart({
      productId: productDetail.value.id,
      skuId: selectedSku.value.id,
      quantity: normalizedQuantity(),
      addonCodes: [],
    })
    if (res?.code === 200) {
      await cart.refreshCart()
      cart.openCart()
      close()
    }
  } finally {
    adding.value = false
  }
}

watch(
  () => [props.isOpen, props.product?.id, props.product?.slug],
  async ([isOpen]) => {
    syncBodyScroll(Boolean(isOpen))
    if (!isOpen) {
      productDetail.value = null
      loading.value = false
      quantity.value = 1
      return
    }
    await loadProduct()
  },
  { immediate: true },
)

watch(galleryImages, syncActiveImage)
watch(activeImage, ensureActiveThumbVisible)

onMounted(() => {
  if (process.client) {
    window.addEventListener('keydown', handleKeydown)
  }
})

onBeforeUnmount(() => {
  syncBodyScroll(false)
  if (process.client) {
    window.removeEventListener('keydown', handleKeydown)
  }
})
</script>

<style scoped lang="scss">
.quick-view-modal {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(17, 17, 17, 0.55);
  backdrop-filter: blur(4px);
}

.modal-shell {
  position: relative;
  width: min(1180px, 100%);
  max-height: min(860px, calc(100vh - 48px));
  overflow: auto;
  border-radius: 28px;
  background: #fff;
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.24);
}

.btn-close {
  position: absolute;
  top: 22px;
  right: 22px;
  z-index: 4;
  width: 48px;
  height: 48px;
  border: 1px solid #ececec;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.96);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.icon-close {
  width: 18px;
  height: 18px;
}

.modal-state {
  min-height: 360px;
  display: grid;
  place-items: center;
  padding: 40px;
  color: #6b7280;
  font-size: 16px;
}

.modal-layout {
  display: grid;
  grid-template-columns: minmax(0, 0.84fr) minmax(420px, 1.16fr);
  min-height: 680px;
}

.gallery-panel {
  padding: 28px;
  border-right: 1px solid #ececec;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.main-image-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 24px;
  overflow: hidden;
  background: linear-gradient(180deg, #fcfcfc 0%, #f3f4f6 100%);
  max-width: 520px;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 24px;
}

.gallery-controls {
  position: absolute;
  right: 18px;
  bottom: 18px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 999px;
  background: rgba(17, 24, 39, 0.72);
  color: #fff;
  backdrop-filter: blur(10px);
}

.gallery-nav {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: inherit;
}

.gallery-nav__icon {
  width: 16px;
  height: 16px;
}

.gallery-count {
  min-width: 52px;
  text-align: center;
  font-size: 12px;
  font-weight: 700;
}

.thumbs-shell {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
  max-width: 404px;
  width: 100%;
}

.thumbs-viewport {
  width: 100%;
  overflow: hidden;
}

.thumb-stepper {
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #d0d5dd;
  border-radius: 999px;
  background: #fff;
  color: #111827;
  flex-shrink: 0;
}

.thumb-stepper:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.thumb-stepper__icon {
  width: 16px;
  height: 16px;
}

.tags-left {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 2;
}

.tag-label {
  display: inline-block;
  color: #fff;
  font-size: 11px;
  padding: 5px 10px;
  font-weight: 700;
  text-transform: uppercase;
  border-radius: 0 0 10px 0;
}

.tag-label.new {
  background: #e62332;
}

.tag-label.hot {
  background: #ff5722;
}

.tags-right {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 2;
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
}

.thumbs-list {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.thumb-btn {
  aspect-ratio: 1 / 1;
  border: 2px solid transparent;
  border-radius: 14px;
  padding: 4px;
  background: #fff;
  cursor: pointer;
}

.thumb-btn.is-active {
  border-color: #111;
}

.thumb-btn img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 10px;
}

.info-panel {
  padding: 48px 42px 36px;
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.brand {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.title {
  margin: 0;
  color: #111;
  font-size: 34px;
  line-height: 1.05;
  font-weight: 800;
}

.price-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
}

.reviews {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #4b5563;
  font-size: 14px;
  flex-wrap: wrap;
}

.star-icon {
  width: 16px;
  height: 16px;
  color: #ffb400;
  fill: currentColor;
}

.price-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.current-price {
  color: #e62332;
  font-size: 28px;
  font-weight: 800;
}

.old-price {
  color: #9ca3af;
  font-size: 18px;
  text-decoration: line-through;
}

.variant-selectors {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.selector-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.selector-label {
  margin: 0;
  color: #374151;
  font-size: 15px;
}

.options-list,
.color-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.text-btn {
  min-height: 46px;
  padding: 0 18px;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  background: #fff;
  color: #111;
  cursor: pointer;
  font-weight: 600;
}

.text-btn.is-active {
  border-color: #111;
  box-shadow: inset 0 0 0 1px #111;
}

.text-btn:disabled,
.color-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.color-btn {
  width: 68px;
  height: 68px;
  border: 2px solid #e5e7eb;
  border-radius: 14px;
  padding: 4px;
  background: #fff;
  cursor: pointer;
}

.color-btn.is-active {
  border-color: #111;
}

.color-btn img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.stock-notice {
  width: fit-content;
  display: inline-flex;
  align-items: center;
  min-height: 42px;
  padding: 0 16px;
  border-radius: 999px;
  background: #ecfdf3;
  color: #027a48;
  font-size: 14px;
  font-weight: 700;
}

.stock-notice.soldout {
  background: #fef3f2;
  color: #b42318;
}

.actions {
  display: flex;
  gap: 14px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  width: 148px;
  border: 1px solid #d9d9d9;
  border-radius: 999px;
  padding: 4px 8px;
}

.qty-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.qty-icon {
  width: 16px;
  height: 16px;
}

.qty-input {
  flex: 1;
  border: none;
  background: transparent;
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  outline: none;
  -moz-appearance: textfield;
}

.qty-input::-webkit-outer-spin-button,
.qty-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.btn-add-to-cart {
  flex: 1;
  min-height: 56px;
  border: none;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
}

.btn-add-to-cart:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.view-details {
  margin-top: auto;
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding-top: 18px;
  border-top: 1px solid #ececec;
  color: #111;
  text-decoration: none;
  font-weight: 700;
}

.detail-icon {
  width: 18px;
  height: 18px;
}

@media (max-width: 1080px) {
  .modal-layout {
    grid-template-columns: 1fr;
  }

  .gallery-panel {
    border-right: none;
    border-bottom: 1px solid #ececec;
  }

  .main-image-wrap,
  .thumbs-shell {
    max-width: 100%;
  }
}

@media (max-width: 720px) {
  .quick-view-modal {
    padding: 12px;
  }

  .modal-shell {
    border-radius: 20px;
    max-height: calc(100vh - 24px);
  }

  .gallery-panel,
  .info-panel {
    padding: 22px;
  }

  .title {
    font-size: 28px;
  }

  .price-row,
  .actions {
    flex-direction: column;
    align-items: stretch;
  }

  .quantity-selector {
    width: 100%;
  }
}
</style>
