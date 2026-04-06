<template>
  <div v-if="product" class="product-detail-page">
    <div class="container main-content">
      <div class="product-layout">
        <section class="product-media">
          <div class="main-image-container">
            <div v-if="leftTags.length" class="tags-left">
              <span v-for="tag in leftTags" :key="tag" class="tag-label" :class="tag.toLowerCase()">{{ tag }}</span>
            </div>
            <div v-if="hasSpringSale" class="tags-right">
              <div class="spring-sale-badge">
                <span class="text">Spring<br />Sale</span>
              </div>
            </div>
            <img :src="activeImage" :alt="product.title" class="main-image" />

            <div v-if="galleryImages.length > 1" class="gallery-controls">
              <button type="button" class="gallery-nav" @click="cycleImage(-1)">
                <ChevronLeftIcon class="gallery-nav-icon" />
              </button>
              <span class="gallery-count">{{ activeImageIndex + 1 }} / {{ galleryImages.length }}</span>
              <button type="button" class="gallery-nav" @click="cycleImage(1)">
                <ChevronRightIcon class="gallery-nav-icon" />
              </button>
            </div>
          </div>

          <div v-if="galleryImages.length > 1" class="thumbnails">
            <button type="button" class="nav-btn" @click="cycleImage(-1)"><ChevronLeftIcon /></button>
            <div class="thumbs-list">
              <button
                v-for="(img, index) in galleryImages"
                :key="`${img}-${index}`"
                type="button"
                class="thumbnail-btn"
                :class="{ 'is-active': activeImage === img }"
                @click="activeImage = img"
              >
                <img :src="img" :alt="`${product.title}-${index}`" />
              </button>
            </div>
            <button type="button" class="nav-btn" @click="cycleImage(1)"><ChevronRightIcon /></button>
          </div>

          <div class="key-specs">
            <div v-for="spec in resolvedSpecs" :key="spec.label" class="spec-item">
              <component :is="spec.icon" class="spec-icon" />
              <div class="spec-text">
                <span class="value">{{ spec.value }}</span>
                <span class="label">{{ spec.label }}</span>
              </div>
            </div>
          </div>
        </section>

        <section class="product-info">
          <div class="product-badges">
            <span class="badge success">{{ copy.badgePrimary }}</span>
            <span class="badge danger">{{ copy.badgeSecondary }}</span>
          </div>

          <h1 class="product-title">{{ product.title }}</h1>

          <div class="price-area">
            <span class="current-price">{{ money(selectedPrice) }}</span>
            <span v-if="selectedCompareAtPrice" class="old-price">{{ money(selectedCompareAtPrice) }}</span>
            <span v-if="selectedCompareAtPrice" class="save-badge">
              Save {{ money(Number(selectedCompareAtPrice) - Number(selectedPrice)) }}
            </span>
          </div>

          <div class="review-stars">
            <div class="stars">
              <StarIcon v-for="i in 5" :key="i" class="star-icon filled" />
            </div>
            <span class="review-count">{{ reviewCount }} reviews</span>
          </div>

          <div class="installment-info">
            <p>{{ copy.installmentPrefix }} <strong>{{ money(Math.max(Number(selectedPrice || 0) / 4, 1)) }}</strong> {{ copy.installmentSuffix }}</p>
          </div>

          <div class="variant-selectors">
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

          <div class="add-to-cart-section">
            <div class="quantity-selector">
              <button type="button" class="qty-btn" @click="quantity = Math.max(1, quantity - 1)"><MinusIcon class="icon" /></button>
              <input v-model="quantity" type="number" min="1" class="qty-input" />
              <button type="button" class="qty-btn" @click="quantity += 1"><PlusIcon class="icon" /></button>
            </div>
            <button type="button" class="btn-add-to-cart" :disabled="!selectedSkuAvailable || adding" @click="handleAddToCart">
              {{ adding ? t('adding') : t('addToCart') }}
            </button>
          </div>

          <div v-if="product.upsells?.length" class="upsell-section">
            <h3 class="upsell-title"><ZapIcon class="icon-zap" /> {{ copy.addonTitle }}</h3>
            <label v-for="item in product.upsells" :key="item.code" class="upsell-item">
              <input v-model="selectedAddonCodes" :value="item.code" type="checkbox" />
              <div class="item-info">
                <img :src="item.image" :alt="item.name" class="item-img" />
                <span class="item-name">{{ item.name }}</span>
              </div>
              <div class="item-price">
                <span class="free-price">{{ money(item.price) }}</span>
                <span v-if="item.compareAtPrice" class="old-price">{{ money(item.compareAtPrice) }}</span>
              </div>
            </label>
          </div>

          <div class="accordion-group">
            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'quick-know' }">
              <button type="button" class="accordion-header" @click="toggleAccordion('quick-know')">
                <span>{{ copy.quickKnow }}</span>
                <ChevronUpIcon v-if="activeAccordion === 'quick-know'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div v-show="activeAccordion === 'quick-know'" class="accordion-content">
                <ul class="feature-list">
                  <li v-for="feature in product.quickKnow || []" :key="feature">
                    <CheckSquareIcon class="icon-check" />
                    <span>{{ feature }}</span>
                  </li>
                </ul>
              </div>
            </div>

            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'specification' }">
              <button type="button" class="accordion-header" @click="toggleAccordion('specification')">
                <span>{{ t('specs') }}</span>
                <ChevronUpIcon v-if="activeAccordion === 'specification'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div v-show="activeAccordion === 'specification'" class="accordion-content">
                <div class="specs-table">
                  <div v-for="spec in product.specTable || []" :key="`${spec.label}-${spec.value}`" class="spec-row">
                    <div class="spec-label">{{ spec.label }}</div>
                    <div class="spec-value">{{ spec.value }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'in-the-box' }">
              <button type="button" class="accordion-header" @click="toggleAccordion('in-the-box')">
                <span>{{ t('whatsInTheBox') }}</span>
                <ChevronUpIcon v-if="activeAccordion === 'in-the-box'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div v-show="activeAccordion === 'in-the-box'" class="accordion-content">
                <ul class="box-list">
                  <li v-for="item in product.boxItems || []" :key="item">{{ item }}</li>
                </ul>
              </div>
            </div>

            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'faq' }">
              <button type="button" class="accordion-header" @click="toggleAccordion('faq')">
                <span>{{ t('faq') }}</span>
                <ChevronUpIcon v-if="activeAccordion === 'faq'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div v-show="activeAccordion === 'faq'" class="accordion-content">
                <div class="faq-list">
                  <div v-for="item in product.faqs || []" :key="item.question" class="faq-entry">
                    <strong>{{ item.question }}</strong>
                    <p>{{ item.answer }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <div class="sticky-cart-bar is-visible">
      <div class="container sticky-content">
        <div class="product-mini-info">
          <img :src="galleryImages[0]" :alt="product.title" class="mini-img" />
          <div class="mini-text">
            <h4 class="mini-title">{{ product.title }}</h4>
            <span class="mini-variant">{{ selectedVariantText }}</span>
          </div>
        </div>
        <div class="sticky-actions">
          <div class="price-area mini">
            <span class="current-price">{{ money(selectedPrice) }}</span>
            <span v-if="selectedCompareAtPrice" class="old-price">{{ money(selectedCompareAtPrice) }}</span>
          </div>
          <button type="button" class="btn-add-to-cart mini" :disabled="!selectedSkuAvailable || adding" @click="handleAddToCart">
            {{ adding ? t('adding') : t('addToCart') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  ActivityIcon,
  BatteryIcon,
  CheckSquareIcon,
  ChevronDownIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  ChevronUpIcon,
  MinusIcon,
  NavigationIcon,
  PlusIcon,
  StarIcon,
  ZapIcon,
} from 'lucide-vue-next'
import { findInitialSelection, findSelectedSku, isOptionSelectable, isSkuAvailable, sortAttributeKeys } from '~/utils/productSelection'
import { mergeProductImages } from '~/utils/productMedia'

const route = useRoute()
const { lang, t } = useShopLocale()
const cart = useShopCart()
const session = useShopSession()
const { money, attributeText } = useShopFormat()

const product = ref<any | null>(null)
const activeImage = ref('')
const quantity = ref(1)
const adding = ref(false)
const activeAccordion = ref<'quick-know' | 'specification' | 'in-the-box' | 'faq' | null>('quick-know')
const selection = reactive<Record<string, string>>({})
const selectedAddonCodes = ref<string[]>([])

const iconMap = { ZapIcon, NavigationIcon, ActivityIcon, BatteryIcon } as const

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        badgePrimary: '\u9a91\u884c\u597d\u4ef7',
        badgeSecondary: '\u65b0\u54c1\u4e0a\u67b6',
        installmentPrefix: '\u652f\u6301\u5206\u671f\uff0c\u5355\u671f\u7ea6',
        installmentSuffix: '/ 4 \u671f',
        quickKnow: '\u5feb\u901f\u4e86\u89e3',
        addonTitle: '\u9644\u52a0\u670d\u52a1',
      }
    : {
        badgePrimary: 'Ride Deals',
        badgeSecondary: 'Fresh Rides, Fresh Start',
        installmentPrefix: '4 interest-free installments from',
        installmentSuffix: 'with flexible payment options',
        quickKnow: 'Quick Know',
        addonTitle: 'Spring Sale Add-ons',
      },
)

const galleryImages = computed(() => {
  return mergeProductImages(
    selectedSku.value?.images,
    selectedSku.value?.pic,
    product.value?.images,
    product.value?.pic,
  )
})
const activeImageIndex = computed(() => {
  const index = galleryImages.value.indexOf(activeImage.value)
  return index >= 0 ? index : 0
})

const resolvedSpecs = computed(() =>
  (Array.isArray(product.value?.specs) ? product.value.specs : []).slice(0, 6).map((spec: any) => ({
    ...spec,
    icon: iconMap[(spec.icon || 'ActivityIcon') as keyof typeof iconMap] || ActivityIcon,
  })),
)

const attributeOptions = computed(() => product.value?.skuAttributeOptions || {})
const attributeKeys = computed(() => sortAttributeKeys(Object.keys(attributeOptions.value || {})))
const selectedSku = computed(() => findSelectedSku(product.value?.skuList || [], selection))
const selectedSkuAvailable = computed(() => Boolean(selectedSku.value && isSkuAvailable(selectedSku.value)))
const selectedPrice = computed(() => selectedSku.value?.price || product.value?.price || 0)
const selectedCompareAtPrice = computed(() => selectedSku.value?.compareAtPrice || product.value?.compareAtPrice || 0)
const leftTags = computed(() => (Array.isArray(product.value?.tags) ? product.value.tags : []).filter((tag: string) => ['NEW', 'HOT'].includes(String(tag).toUpperCase())))
const hasSpringSale = computed(() => (Array.isArray(product.value?.tags) ? product.value.tags : []).some((tag: string) => String(tag).toLowerCase().includes('spring sale')))
const reviewCount = computed(() => Math.max(Number(product.value?.reviewCount || 0), 61))
const selectedVariantText = computed(() => attributeText(selection))
const normalizedQuantity = computed(() =>
  Math.max(1, Number.parseInt(String(quantity.value || 1), 10) || 1),
)

const syncActiveImage = () => {
  activeImage.value = galleryImages.value[0] || ''
}

const resetSelection = () => {
  const nextSelection = findInitialSelection(product.value?.skuList || [])
  Object.keys(selection).forEach((key) => delete selection[key])
  Object.assign(selection, nextSelection)
}

const fetchProduct = async () => {
  try {
    const identifier = String(route.params.id)
    const endpoint = /^\d+$/.test(identifier) ? `/api/product/${identifier}` : `/api/product/slug/${identifier}`
    const res = await useHttp(endpoint)
    product.value = res?.code === 200 ? res.data : null
    if (product.value) {
      resetSelection()
      selectedAddonCodes.value = []
      quantity.value = 1
      syncActiveImage()
    }
  } catch (error) {
    product.value = null
  }
}

const toggleAccordion = (panelName: 'quick-know' | 'specification' | 'in-the-box' | 'faq') => {
  activeAccordion.value = activeAccordion.value === panelName ? null : panelName
}

const isSelectable = (attributeKey: string, option: string) => isOptionSelectable(product.value?.skuList || [], selection, attributeKey, option)

const formatAttributeKey = (value: string) => ({ color: 'Color', bundle: 'Bundle', style: 'Style' }[value] || value.charAt(0).toUpperCase() + value.slice(1))

const optionThumbnail = (attributeKey: string, option: string) => {
  const skuList = Array.isArray(product.value?.skuList) ? product.value.skuList : []
  const matched = skuList.find((sku: any) => sku.attributes?.[attributeKey] === option)
  return (
    mergeProductImages(matched?.images, matched?.pic, product.value?.images, product.value?.pic)[0] ||
    product.value?.pic
  )
}

const cycleImage = (step: number) => {
  if (!galleryImages.value.length) return
  const currentIndex = activeImageIndex.value
  const nextIndex = (currentIndex + step + galleryImages.value.length) % galleryImages.value.length
  activeImage.value = galleryImages.value[nextIndex]
}

const handleAddToCart = async () => {
  await session.fetchMe()
  if (!session.isLoggedIn.value) {
    await navigateTo('/login')
    return
  }
  if (!selectedSku.value) return
  adding.value = true
  try {
    const res = await cart.addToCart({ productId: product.value.id, skuId: selectedSku.value.id, quantity: normalizedQuantity.value, addonCodes: selectedAddonCodes.value })
    if (res?.code === 200) {
      await cart.refreshCart()
      cart.openCart()
    }
  } finally {
    adding.value = false
  }
}

watch(() => route.params.id, fetchProduct)
watch(galleryImages, syncActiveImage)

onMounted(async () => {
  await session.fetchMe()
  await fetchProduct()
})
</script>

<style scoped lang="scss">
.product-detail-page { padding: 20px 0 120px; max-width: 1440px; background: #fff; margin: 0 auto; }
.product-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: start; }
.product-media { position: sticky; top: 100px; display: flex; flex-direction: column; gap: 20px; }
.main-image-container { position: relative; border-radius: 20px; background: #fff; border: 1px solid #f0f0f0; overflow: hidden; aspect-ratio: 1/1; }
.main-image { width: 100%; height: 100%; object-fit: cover; transition: transform .3s ease; }
.main-image:hover { transform: scale(1.05); }
.gallery-controls { position: absolute; right: 16px; bottom: 16px; z-index: 3; display: inline-flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: 999px; background: rgba(17, 24, 39, .72); color: #fff; backdrop-filter: blur(10px); }
.gallery-nav { width: 36px; height: 36px; border-radius: 50%; border: 1px solid rgba(255, 255, 255, .18); background: rgba(255, 255, 255, .08); display: flex; align-items: center; justify-content: center; color: inherit; }
.gallery-nav-icon { width: 18px; height: 18px; }
.gallery-count { min-width: 56px; text-align: center; font-size: 12px; font-weight: 700; }
.tags-left { position: absolute; top: 0; left: 0; z-index: 2; }
.tag-label { display: inline-block; color: #fff; font-size: 12px; padding: 6px 12px; font-weight: 700; border-radius: 0 0 12px 0; }
.tag-label.new { background: #e62332; }
.tag-label.hot { background: #ff5722; }
.tags-right { position: absolute; top: 20px; right: 20px; }
.spring-sale-badge { background: linear-gradient(135deg,#d4fc79 0%,#96e6a1 100%); border: 2px solid #fff; box-shadow: 0 4px 10px rgba(0,0,0,.1); border-radius: 12px; padding: 6px 12px; transform: rotate(5deg); }
.spring-sale-badge .text { color: #2e7d32; font-weight: 900; font-size: 12px; line-height: 1.1; display: block; text-align: center; text-transform: uppercase; }
.thumbnails { display: flex; justify-content: center; align-items: center; gap: 12px; }
.nav-btn { width: 32px; height: 32px; border-radius: 50%; background: #f5f5f5; display: flex; align-items: center; justify-content: center; border: none; }
.thumbs-list { display: flex; gap: 12px; overflow-x: auto; scrollbar-width: none; }
.thumbs-list::-webkit-scrollbar { display: none; }
.thumbnail-btn { width: 60px; height: 60px; border-radius: 8px; border: 2px solid transparent; background: #f9f9f9; padding: 4px; flex-shrink: 0; }
.thumbnail-btn img { width: 100%; height: 100%; object-fit: cover; border-radius: 4px; }
.thumbnail-btn.is-active { border-color: #333; background: #fff; }
.key-specs { display: grid; grid-template-columns: repeat(3,1fr); gap: 16px; padding: 24px; background: #fafafa; border-radius: 16px; }
.spec-item { display: flex; flex-direction: column; align-items: center; text-align: center; gap: 8px; }
.spec-icon { width: 24px; height: 24px; color: #58cc02; }
.spec-text .value { font-weight: 700; font-size: 14px; color: #111; display: block; }
.spec-text .label { font-size: 12px; color: #666; }
.product-info { display: flex; flex-direction: column; gap: 20px; }
.product-badges { display: flex; gap: 8px; }
.badge { padding: 4px 10px; border-radius: 4px; font-size: 12px; font-weight: 700; text-transform: uppercase; }
.badge.success { background: #e8f5e9; color: #2e7d32; }
.badge.danger { background: #ffebee; color: #c62828; }
.product-title { font-size: 32px; font-weight: 800; line-height: 1.2; margin: 0; color: #111; }
.price-area { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.price-area .current-price { font-size: 28px; font-weight: 800; color: #e62332; }
.price-area .old-price { font-size: 18px; color: #999; text-decoration: line-through; }
.save-badge { background: #e62332; color: #fff; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; }
.review-stars { display: flex; align-items: center; gap: 8px; }
.stars { display: flex; color: #ffc107; }
.star-icon { width: 16px; height: 16px; fill: currentColor; }
.review-count { font-size: 13px; color: #666; text-decoration: underline; }
.installment-info { font-size: 13px; color: #555; background: #f9f9f9; padding: 12px 16px; border-radius: 8px; }
.variant-selectors { display: flex; flex-direction: column; gap: 16px; }
.selector-label { font-size: 14px; margin-bottom: 12px; color: #333; }
.options-list { display: flex; flex-wrap: wrap; gap: 12px; }
.text-btn { padding: 10px 20px; border: 1px solid #ccc; border-radius: 4px; background: #fff; font-size: 14px; color: #333; cursor: pointer; font-weight: 500; }
.text-btn:disabled,.color-btn:disabled { opacity: .35; cursor: not-allowed; }
.text-btn.is-active { border-color: #111; border-width: 2px; padding: 9px 19px; }
.color-options { display: flex; gap: 12px; }
.color-btn { width: 60px; height: 60px; border-radius: 8px; border: 2px solid #eee; padding: 2px; background: #fff; cursor: pointer; }
.color-btn img { width: 100%; height: 100%; object-fit: cover; border-radius: 4px; }
.color-btn.is-active { border-color: #111; }
.stock-notice { display: inline-flex; width: fit-content; padding: 6px 12px; border-radius: 999px; background: #ecfdf3; color: #027a48; font-size: 13px; font-weight: 700; }
.stock-notice.soldout { background: #fef3f2; color: #b42318; }
.add-to-cart-section { display: flex; gap: 16px; }
.quantity-selector { display: flex; align-items: center; border: 1px solid #ddd; border-radius: 30px; padding: 4px 8px; width: 120px; }
.qty-btn { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; color: #333; border: none; background: none; }
.qty-input { flex: 1; width: 100%; text-align: center; border: none; font-size: 16px; font-weight: 600; outline: none; -moz-appearance: textfield; }
.qty-input::-webkit-outer-spin-button,.qty-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.btn-add-to-cart { flex: 1; background: #111; color: #fff; font-size: 16px; font-weight: 700; border-radius: 30px; transition: all .3s; border: none; padding: 0 24px; }
.btn-add-to-cart:disabled,.btn-add-to-cart.mini:disabled { opacity: .45; }
.upsell-section { border: 1px solid #ffe0b2; border-radius: 12px; padding: 16px; background: #fffcf8; }
.upsell-title { font-size: 14px; font-weight: 700; color: #e65100; display: flex; align-items: center; gap: 6px; margin: 0 0 16px; }
.upsell-item { display: grid; grid-template-columns: auto minmax(0,1fr) auto; align-items: center; gap: 12px; padding: 12px; background: #fff; border: 1px solid #eee; border-radius: 8px; margin-bottom: 8px; }
.item-info { display: flex; align-items: center; gap: 12px; }
.item-img { width: 40px; height: 40px; object-fit: cover; border-radius: 8px; }
.item-name { font-size: 14px; font-weight: 500; color: #333; }
.item-price { display: flex; flex-direction: column; align-items: flex-end; }
.free-price { color: #e62332; font-weight: 700; font-size: 14px; }
.item-price .old-price { color: #999; font-size: 12px; text-decoration: line-through; }
.accordion-group { border-top: 1px solid #eee; }
.accordion-item { border-bottom: 1px solid #eee; }
.accordion-header { width: 100%; display: flex; justify-content: space-between; align-items: center; padding: 20px 0; font-size: 16px; font-weight: 700; color: #333; background: transparent; border: none; text-align: left; }
.accordion-content { padding-bottom: 20px; }
.feature-list li { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 12px; font-size: 14px; color: #555; line-height: 1.5; }
.icon-check { width: 18px; height: 18px; color: #58cc02; flex-shrink: 0; margin-top: 2px; }
.specs-table { display: flex; flex-direction: column; gap: 8px; }
.spec-row { display: flex; padding: 10px 16px; background: #f9f9f9; border-radius: 8px; font-size: 14px; }
.spec-label { width: 40%; color: #666; font-weight: 500; }
.spec-value { width: 60%; color: #111; font-weight: 600; }
.box-list { list-style: disc; padding-left: 20px; font-size: 14px; color: #555; }
.box-list li { margin-bottom: 8px; }
.faq-entry { margin-bottom: 14px; }
.faq-entry p { margin: 6px 0 0; color: #555; line-height: 1.6; }
.sticky-cart-bar { position: fixed; bottom: 0; left: 0; width: 100%; background: #fff; box-shadow: 0 -4px 20px rgba(0,0,0,.08); z-index: 100; transform: translateY(100%); transition: transform .3s cubic-bezier(.4,0,.2,1); padding: 12px 0; border-top: 1px solid #eee; }
.sticky-cart-bar.is-visible { transform: translateY(0); }
.sticky-content { display: flex; justify-content: space-between; align-items: center; gap: 20px; }
.product-mini-info { display: flex; align-items: center; gap: 16px; }
.mini-img { width: 48px; height: 48px; object-fit: cover; background: #f9f9f9; border-radius: 4px; }
.mini-text { display: flex; flex-direction: column; }
.mini-title { font-size: 14px; font-weight: 600; color: #333; margin: 0; }
.mini-variant { font-size: 12px; color: #666; }
.sticky-actions { display: flex; align-items: center; gap: 24px; }
.price-area.mini .current-price { font-size: 20px; }
.btn-add-to-cart.mini { padding: 10px 40px; border: none; border-radius: 30px; background: #111; color: #fff; font-weight: 700; }
@media (max-width: 1024px) { .product-layout { grid-template-columns: 1fr; gap: 40px; } .product-media { position: static; } }
@media (max-width: 960px) { .sticky-content { flex-direction: column; align-items: stretch; } .key-specs { grid-template-columns: repeat(2,1fr); } }
</style>
