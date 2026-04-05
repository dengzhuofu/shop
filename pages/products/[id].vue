<template>
  <div class="product-page container" v-if="product">
    <div class="product-layout">
      <section class="gallery">
        <img :src="activeImage" :alt="product.title" class="hero-image" />
        <div class="thumbs" v-if="galleryImages.length > 1">
          <button
            v-for="image in galleryImages"
            :key="image"
            type="button"
            class="thumb"
            :class="{ active: image === activeImage }"
            @click="activeImage = image"
          >
            <img :src="image" :alt="product.title" />
          </button>
        </div>
      </section>

      <section class="summary">
        <p class="category">{{ product.categorySlug }}</p>
        <h1>{{ product.title }}</h1>
        <p v-if="product.subtitle" class="subtitle">{{ product.subtitle }}</p>

        <div class="price-row">
          <strong>{{ money(selectedSku?.price || product.price) }}</strong>
          <span v-if="selectedSku?.compareAtPrice || product.compareAtPrice" class="compare">
            {{ money(selectedSku?.compareAtPrice || product.compareAtPrice) }}
          </span>
        </div>

        <div class="stock-chip" :class="{ out: !selectedSkuAvailable }">
          {{ selectedSkuAvailable ? t('inStock') : t('soldOut') }}
        </div>

        <div class="selector-block" v-for="attributeKey in attributeKeys" :key="attributeKey">
          <p>{{ attributeKey }}</p>
          <div class="selector-options">
            <button
              v-for="option in attributeOptions[attributeKey] || []"
              :key="option"
              type="button"
              class="selector-option"
              :class="{ active: selection[attributeKey] === option }"
              :disabled="!isSelectable(attributeKey, option)"
              @click="selection[attributeKey] = option"
            >
              {{ option }}
            </button>
          </div>
        </div>

        <div class="selector-block" v-if="product.upsells?.length">
          <p>{{ t('coupon') }} / Add-ons</p>
          <label v-for="addon in product.upsells" :key="addon.code" class="addon-item">
            <input v-model="selectedAddonCodes" :value="addon.code" type="checkbox" />
            <span class="addon-copy">
              <strong>{{ addon.name }}</strong>
              <small>{{ addon.description }}</small>
            </span>
            <span>{{ money(addon.price) }}</span>
          </label>
        </div>

        <div class="purchase-row">
          <div class="qty-box">
            <button type="button" @click="quantity = Math.max(1, quantity - 1)">-</button>
            <span>{{ quantity }}</span>
            <button type="button" @click="quantity += 1">+</button>
          </div>
          <button type="button" class="add-btn" :disabled="!selectedSkuAvailable || adding" @click="handleAddToCart">
            {{ adding ? t('adding') : t('addToCart') }}
          </button>
        </div>

        <div class="rich-text" v-html="product.description" />
      </section>
    </div>

    <section class="detail-grid">
      <article class="detail-card">
        <h2>{{ t('specs') }}</h2>
        <div v-if="product.specTable?.length" class="spec-table">
          <div v-for="row in product.specTable" :key="`${row.label}-${row.value}`" class="spec-row">
            <span>{{ row.label }}</span>
            <strong>{{ row.value }}</strong>
          </div>
        </div>
      </article>

      <article class="detail-card">
        <h2>{{ t('whatsInTheBox') }}</h2>
        <ul>
          <li v-for="item in product.boxItems || []" :key="item">{{ item }}</li>
        </ul>
      </article>

      <article class="detail-card">
        <h2>{{ t('faq') }}</h2>
        <div v-for="item in product.faqs || []" :key="item.question" class="faq-item">
          <strong>{{ item.question }}</strong>
          <p>{{ item.answer }}</p>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  findInitialSelection,
  findSelectedSku,
  isOptionSelectable,
  isSkuAvailable,
  sortAttributeKeys,
} from '~/utils/productSelection'

const route = useRoute()
const router = useRouter()
const { t } = useShopLocale()
const cart = useShopCart()
const session = useShopSession()
const { money } = useShopFormat()

const product = ref<any | null>(null)
const activeImage = ref('')
const quantity = ref(1)
const adding = ref(false)
const selection = reactive<Record<string, string>>({})
const selectedAddonCodes = ref<string[]>([])

const galleryImages = computed(() => {
  const baseImages = Array.isArray(product.value?.images) ? product.value.images : []
  if (selectedSku.value?.images?.length) {
    return selectedSku.value.images
  }
  return baseImages.length ? baseImages : [product.value?.pic].filter(Boolean)
})

const attributeOptions = computed(() => product.value?.skuAttributeOptions || {})
const attributeKeys = computed(() => sortAttributeKeys(Object.keys(attributeOptions.value || {})))
const selectedSku = computed(() => findSelectedSku(product.value?.skuList || [], selection))
const selectedSkuAvailable = computed(() => Boolean(selectedSku.value && isSkuAvailable(selectedSku.value)))

const isSelectable = (attributeKey: string, option: string) =>
  isOptionSelectable(product.value?.skuList || [], selection, attributeKey, option)

const applyInitialSelection = () => {
  const nextSelection = findInitialSelection(product.value?.skuList || [])
  Object.keys(selection).forEach((key) => {
    delete selection[key]
  })
  Object.assign(selection, nextSelection)
}

const syncActiveImage = () => {
  activeImage.value = galleryImages.value[0] || ''
}

const fetchProduct = async () => {
  try {
    const identifier = String(route.params.id)
    const endpoint = /^\d+$/.test(identifier)
      ? `/api/product/${identifier}`
      : `/api/product/slug/${identifier}`
    const res = await useHttp(endpoint)
    if (res?.code === 200) {
      product.value = res.data
      applyInitialSelection()
      syncActiveImage()
    } else {
      product.value = null
    }
  } catch (error) {
    product.value = null
  }
}

const handleAddToCart = async () => {
  if (!session.isLoggedIn.value) {
    await navigateTo('/login')
    return
  }
  if (!selectedSku.value) {
    return
  }

  adding.value = true
  try {
    await cart.addToCart({
      productId: product.value.id,
      skuId: selectedSku.value.id,
      quantity: quantity.value,
      addonCodes: selectedAddonCodes.value,
    })
    cart.openCart()
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
.product-page {
  padding-top: 30px;
}

.product-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(340px, 0.92fr);
  gap: 28px;
}

.gallery,
.summary,
.detail-card {
  background: white;
  border-radius: 28px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 24px;
}

.hero-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-radius: 22px;
  background: #f8fafc;
}

.thumbs {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  overflow-x: auto;
}

.thumb {
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 16px;
  padding: 0;
  background: transparent;

  img {
    width: 76px;
    height: 76px;
    object-fit: cover;
    border-radius: 15px;
  }

  &.active {
    border-color: #0f766e;
  }
}

.category {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #0f766e;
}

h1 {
  margin: 0 0 12px;
  font-size: clamp(32px, 4vw, 48px);
  line-height: 1.04;
  color: #0f172a;
}

.subtitle {
  margin: 0 0 16px;
  line-height: 1.7;
  color: #475569;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;

  strong {
    font-size: 34px;
    color: #0f172a;
  }
}

.compare {
  color: #94a3b8;
  text-decoration: line-through;
}

.stock-chip {
  display: inline-flex;
  border-radius: 999px;
  padding: 8px 12px;
  background: #dcfce7;
  color: #166534;
  font-weight: 700;
  margin-bottom: 22px;

  &.out {
    background: #fee2e2;
    color: #991b1b;
  }
}

.selector-block {
  margin-bottom: 20px;

  p {
    margin: 0 0 10px;
    font-weight: 700;
    color: #0f172a;
    text-transform: capitalize;
  }
}

.selector-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.selector-option {
  border-radius: 999px;
  padding: 10px 14px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  background: #fff;
  font-weight: 700;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.4;
  }

  &.active {
    background: #0f172a;
    color: white;
    border-color: #0f172a;
  }
}

.addon-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 12px;
  align-items: flex-start;
  padding: 14px 16px;
  border-radius: 18px;
  background: #f8fafc;
  margin-bottom: 10px;
}

.addon-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;

  small {
    color: #64748b;
    line-height: 1.5;
  }
}

.purchase-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 14px;
  margin: 26px 0;
}

.qty-box {
  display: inline-flex;
  align-items: center;
  gap: 18px;
  min-height: 54px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  padding: 0 16px;

  button {
    border: none;
    background: none;
    font-size: 20px;
  }
}

.add-btn {
  min-height: 54px;
  border: none;
  border-radius: 999px;
  background: #0f766e;
  color: white;
  font-weight: 800;
}

.rich-text {
  color: #334155;
  line-height: 1.8;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.detail-card h2 {
  margin: 0 0 16px;
  color: #0f172a;
}

.spec-table {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.spec-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.faq-item {
  margin-bottom: 14px;

  p {
    margin: 6px 0 0;
    color: #475569;
    line-height: 1.7;
  }
}

@media (max-width: 960px) {
  .product-layout,
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
