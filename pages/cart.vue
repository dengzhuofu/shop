<template>
  <div class="cart-page">
    <section class="cart-shell container">
      <div class="cart-heading">
        <h1>{{ copy.title }}</h1>
        <NuxtLink to="/" class="continue-link">
          <span class="continue-arrow">&#8630;</span>
          <span>{{ t('continueShopping') }}</span>
        </NuxtLink>
      </div>

      <div v-if="loadingPage" class="cart-loading">
        <div class="loading-orb" />
        <p>{{ copy.loading }}</p>
      </div>

      <div v-else-if="cart.items.value.length" class="cart-layout">
        <div class="cart-lines">
          <article v-for="item in cart.items.value" :key="item.cartItemId" class="cart-line">
            <NuxtLink :to="itemLink(item)" class="line-media">
              <img :src="item.productPic" :alt="item.title" loading="lazy" />
            </NuxtLink>

            <div class="line-main">
              <NuxtLink :to="itemLink(item)" class="line-title">
                {{ item.title }}
              </NuxtLink>
              <p v-if="itemMeta(item)" class="line-meta">{{ itemMeta(item) }}</p>

              <div class="line-price">
                <span v-if="comparePrice(item)" class="line-old-price">{{ comparePrice(item) }}</span>
                <strong>{{ money(item.unitPrice || 0) }}</strong>
              </div>

              <div v-if="addonNames(item.addons).length" class="addon-row">
                <span
                  v-for="name in addonNames(item.addons)"
                  :key="`${item.cartItemId}-${name}`"
                  class="addon-chip"
                >
                  {{ name }}
                </span>
              </div>
            </div>

            <div class="line-quantity">
              <select
                :value="lineQuantities[item.cartItemId] || item.quantity"
                :disabled="updatingIds.has(item.cartItemId)"
                @change="handleQuantityChange(item, $event)"
              >
                <option
                  v-for="option in quantityOptions(item.quantity)"
                  :key="`${item.cartItemId}-${option}`"
                  :value="option"
                >
                  {{ option }}
                </option>
              </select>

              <button
                type="button"
                class="remove-link"
                :disabled="updatingIds.has(item.cartItemId)"
                @click="removeLine(item.cartItemId)"
              >
                {{ copy.remove }}
              </button>
            </div>

            <div class="line-total">{{ linePrice(item) }}</div>
          </article>
        </div>

        <aside class="summary-card">
          <div class="summary-top">
            <span>{{ t('subtotal') }}</span>
            <strong>{{ money(cart.subtotal.value) }}</strong>
          </div>
          <p class="summary-note">{{ copy.taxHint }}</p>

          <div class="note-box">
            <label for="cart-note">{{ copy.noteTitle }}</label>
            <textarea
              id="cart-note"
              v-model="orderNote"
              :placeholder="copy.notePlaceholder"
              rows="5"
            />
          </div>

          <div class="protection-card">
            <div class="protection-icon" aria-hidden="true">
              <span class="protection-box" />
              <span class="protection-shield" />
            </div>

            <div class="protection-copy">
              <div class="protection-topline">
                <strong>{{ copy.protection }} ({{ money(protectionPrice) }})</strong>
                <label class="switch">
                  <input v-model="shippingProtectionEnabled" type="checkbox" />
                  <span class="slider" />
                </label>
              </div>
              <p>{{ copy.protectionHint }}</p>
            </div>
          </div>

          <div class="summary-actions">
            <NuxtLink to="/checkout" class="checkout-btn">
              <span class="lock-dot" />
              <span>{{ t('checkout') }}</span>
            </NuxtLink>
            <button type="button" class="wallet-btn shop-btn">shop</button>
            <button type="button" class="wallet-btn paypal-btn">PayPal</button>
            <button type="button" class="wallet-btn gpay-btn">G Pay</button>
          </div>

          <div class="payment-strip">
            <p>{{ copy.accept }}</p>
            <div class="payment-grid">
              <span v-for="item in paymentMethods" :key="item">{{ item }}</span>
            </div>
          </div>
        </aside>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">
          <span class="bag-handle" />
          <span class="bag-body" />
        </div>
        <p>{{ copy.empty }}</p>
        <NuxtLink to="/" class="empty-cta">{{ t('continueShopping') }}</NuxtLink>
      </div>
    </section>

    <section v-if="recentlyViewed.items.value.length" class="recently-section container">
      <div class="section-head">
        <div>
          <p class="section-kicker">{{ copy.recentKicker }}</p>
          <h2>{{ copy.recentTitle }}</h2>
        </div>
      </div>

      <div class="recent-grid">
        <article v-for="item in recentlyViewed.items.value" :key="item.id" class="recent-card">
          <NuxtLink :to="recentlyViewedLink(item)" class="recent-image">
            <img :src="item.pic" :alt="item.title" loading="lazy" />
          </NuxtLink>
          <div class="recent-copy">
            <NuxtLink :to="recentlyViewedLink(item)" class="recent-title">
              {{ item.title }}
            </NuxtLink>
            <p v-if="item.subtitle" class="recent-subtitle">{{ item.subtitle }}</p>
            <div class="recent-price">
              <strong>{{ money(item.price || 0) }}</strong>
              <span v-if="item.compareAtPrice">{{ money(item.compareAtPrice) }}</span>
            </div>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'

useHead({
  title: 'Your Shopping Cart',
})

const cart = useShopCart()
const recentlyViewed = useRecentlyViewed()
const { lang, t } = useShopLocale()
const { money, attributeText } = useShopFormat()

const loadingPage = ref(true)
const orderNote = ref('')
const shippingProtectionEnabled = ref(false)
const protectionPrice = 18.17
const lineQuantities = reactive<Record<number, number>>({})
const updatingIds = reactive(new Set<number>())
const paymentMethods = ['AMEX', 'Apple Pay', 'Discover', 'G Pay', 'Mastercard', 'PayPal', 'Shop', 'Venmo', 'Visa']

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        title: '\u60a8\u7684\u8d2d\u7269\u8f66',
        loading: '\u6b63\u5728\u52a0\u8f7d\u8d2d\u7269\u8f66...',
        empty: '\u60a8\u7684\u8d2d\u7269\u8f66\u6682\u65f6\u8fd8\u662f\u7a7a\u7684\u3002',
        remove: '\u79fb\u9664',
        taxHint: '\u7a0e\u8d39\u4e0e\u8fd0\u8d39\u5c06\u5728\u7ed3\u8d26\u65f6\u8ba1\u7b97',
        noteTitle: '\u6dfb\u52a0\u8ba2\u5355\u5907\u6ce8',
        notePlaceholder: '\u8f93\u5165\u7ed9\u5546\u5bb6\u7684\u5907\u6ce8',
        protection: '\u914d\u9001\u4fdd\u969c',
        protectionHint:
          '\u5efa\u8bae\u5f00\u542f\u914d\u9001\u4fdd\u969c\uff0c\u4ee5\u4fbf\u5728\u5305\u88f9\u4e22\u5931\u3001\u7834\u635f\u6216\u88ab\u76d7\u65f6\u83b7\u5f97\u66f4\u5b89\u5fc3\u7684\u5904\u7406\u3002',
        accept: '\u652f\u6301\u4ed8\u6b3e',
        recentKicker: '\u521a\u521a\u770b\u8fc7',
        recentTitle: '\u6700\u8fd1\u6d4f\u89c8',
      }
    : {
        title: 'Your cart',
        loading: 'Loading cart...',
        empty: 'Your cart is currently empty.',
        remove: 'Remove',
        taxHint: 'Taxes and shipping calculated at checkout',
        noteTitle: 'Add a note to your order',
        notePlaceholder: 'Order note',
        protection: 'Shipping protection',
        protectionHint:
          'We recommend adding shipping protection for peace of mind in case your package is lost, damaged, or stolen during delivery.',
        accept: 'We accept',
        recentKicker: 'From your browsing',
        recentTitle: 'Recently viewed',
      },
)

const itemLink = (item: any) => `/products/${item.slug || item.productId}`
const recentlyViewedLink = (item: any) => `/products/${item.slug || item.id}`

const addonNames = (addons: any) => {
  if (!Array.isArray(addons)) {
    return []
  }
  return addons
    .map((addon: any) => addon?.name || addon?.title || addon?.code)
    .filter((value: string | undefined) => Boolean(value))
}

const itemMeta = (item: any) => {
  const meta = attributeText(item.attributes)
  return meta?.trim?.() || ''
}

const comparePrice = (item: any) => {
  const value = Number(
    item?.compareAtPrice ||
      item?.marketPrice ||
      item?.originalUnitPrice ||
      item?.lineCompareAtAmount,
  )
  if (!Number.isFinite(value) || value <= Number(item.unitPrice || 0)) {
    return ''
  }
  return money(value)
}

const linePrice = (item: any) => {
  const fallback = Number(item.unitPrice || 0) * Number(item.quantity || 1)
  return money(item.lineAmount || fallback)
}

const quantityOptions = (currentQuantity: number) => {
  const max = Math.max(6, Number(currentQuantity || 1) + 2)
  return Array.from({ length: max }, (_, index) => index + 1)
}

const syncQuantities = () => {
  for (const item of cart.items.value) {
    lineQuantities[item.cartItemId] = Number(item.quantity || 1)
  }
}

const handleQuantityChange = async (item: any, event: Event) => {
  const target = event.target as HTMLSelectElement
  const quantity = Number(target.value || item.quantity || 1)
  lineQuantities[item.cartItemId] = quantity
  updatingIds.add(item.cartItemId)
  try {
    await cart.updateQuantity(
      item.cartItemId,
      quantity,
      item.addons?.map((addon: any) => addon.code),
    )
  } finally {
    updatingIds.delete(item.cartItemId)
  }
}

const removeLine = async (cartItemId: number) => {
  updatingIds.add(cartItemId)
  try {
    await cart.removeItem(cartItemId)
  } finally {
    updatingIds.delete(cartItemId)
  }
}

watch(
  () => cart.items.value,
  () => {
    syncQuantities()
  },
  { deep: true, immediate: true },
)

onMounted(async () => {
  loadingPage.value = true
  await Promise.allSettled([cart.refreshCart(), recentlyViewed.refreshRecentlyViewed()])
  loadingPage.value = false
})
</script>

<style scoped lang="scss">
.cart-page {
  background: #fff;
  color: #111;
}

.cart-shell {
  padding: 48px 0 72px;
}

.cart-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 36px;
}

.cart-heading h1 {
  margin: 0;
  font-size: clamp(42px, 5vw, 64px);
  line-height: 0.96;
  letter-spacing: -0.04em;
}

.continue-link {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  border: 1.5px solid #111;
  border-radius: 999px;
  color: #111;
  text-decoration: none;
  font-weight: 600;
}

.continue-arrow {
  font-size: 20px;
  line-height: 1;
}

.cart-loading,
.empty-state {
  min-height: 360px;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 20px;
  text-align: center;
  border-radius: 32px;
  background: linear-gradient(180deg, #f8f8f8 0%, #ffffff 100%);
  border: 1px solid rgba(17, 17, 17, 0.06);
}

.loading-orb {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: 4px solid rgba(17, 17, 17, 0.08);
  border-top-color: #111;
  animation: spin 0.9s linear infinite;
}

.empty-icon {
  position: relative;
  width: 66px;
  height: 62px;
}

.bag-handle {
  position: absolute;
  inset: 0 14px auto;
  height: 22px;
  border: 4px solid #111;
  border-bottom: 0;
  border-radius: 18px 18px 0 0;
}

.bag-body {
  position: absolute;
  inset: 16px 4px 0;
  border: 4px solid #111;
  border-radius: 18px;
}

.empty-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 190px;
  padding: 14px 28px;
  border-radius: 999px;
  background: #111;
  color: #fff;
  text-decoration: none;
  font-weight: 700;
}

.cart-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 44px;
  align-items: start;
}

.cart-lines {
  border-top: 1px solid rgba(17, 17, 17, 0.08);
}

.cart-line {
  display: grid;
  grid-template-columns: 112px minmax(0, 1fr) 140px 120px;
  gap: 24px;
  align-items: start;
  padding: 28px 0;
  border-bottom: 1px solid rgba(17, 17, 17, 0.08);
}

.line-media {
  display: block;
  border-radius: 18px;
  background: #f4f4f4;
  overflow: hidden;
}

.line-media img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  display: block;
}

.line-main {
  display: grid;
  gap: 10px;
}

.line-title {
  color: #111;
  text-decoration: none;
  font-size: 24px;
  line-height: 1.15;
  font-weight: 600;
}

.line-meta {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.line-price {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  font-size: 20px;
}

.line-old-price,
.recent-price span {
  color: #9a9a9a;
  text-decoration: line-through;
}

.addon-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.addon-chip {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  background: #ffe4ea;
  color: #dd2345;
  font-size: 12px;
  font-weight: 700;
}

.line-quantity {
  display: grid;
  justify-items: start;
  gap: 10px;
  padding-top: 4px;
}

.line-quantity select {
  min-width: 60px;
  padding: 10px 12px;
  border: 1px solid rgba(17, 17, 17, 0.08);
  border-radius: 10px;
  background: #f7f7f7;
}

.remove-link {
  border: 0;
  padding: 0;
  background: transparent;
  color: #111;
  text-decoration: underline;
  cursor: pointer;
}

.line-total {
  padding-top: 8px;
  text-align: right;
  font-size: 24px;
  font-weight: 600;
}

.summary-card {
  display: grid;
  gap: 22px;
  padding: 30px 34px;
  border-radius: 24px;
  background: #f5f5f5;
}

.summary-top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  font-size: 18px;
}

.summary-top strong {
  font-size: 22px;
}

.summary-note {
  margin: -10px 0 0;
  color: #717171;
  font-size: 13px;
}

.note-box {
  display: grid;
  gap: 12px;
}

.note-box label {
  font-size: 15px;
  font-weight: 600;
}

.note-box textarea {
  width: 100%;
  resize: vertical;
  min-height: 110px;
  padding: 16px 18px;
  border: 1px solid rgba(17, 17, 17, 0.1);
  border-radius: 14px;
  background: #fff;
  font: inherit;
}

.protection-card {
  display: grid;
  grid-template-columns: 54px minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.protection-icon {
  position: relative;
  width: 54px;
  height: 54px;
}

.protection-box,
.protection-shield {
  position: absolute;
  display: block;
}

.protection-box {
  inset: 6px;
  border: 3px solid #111;
  border-radius: 10px;
}

.protection-shield {
  right: 0;
  bottom: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #9aa0a8;
}

.protection-copy {
  display: grid;
  gap: 8px;
}

.protection-copy p {
  margin: 0;
  color: #666;
  font-size: 13px;
  line-height: 1.55;
}

.protection-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.switch {
  position: relative;
  width: 42px;
  height: 24px;
}

.switch input {
  position: absolute;
  opacity: 0;
  inset: 0;
}

.slider {
  position: absolute;
  inset: 0;
  border-radius: 999px;
  background: #a7a7a7;
  transition: background 0.2s ease;
}

.slider::after {
  content: '';
  position: absolute;
  top: 3px;
  left: 3px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  transition: transform 0.2s ease;
}

.switch input:checked + .slider {
  background: #111;
}

.switch input:checked + .slider::after {
  transform: translateX(18px);
}

.summary-actions {
  display: grid;
  gap: 10px;
}

.checkout-btn,
.wallet-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  border-radius: 999px;
  border: 0;
  text-decoration: none;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
}

.checkout-btn {
  gap: 10px;
  background: #111;
  color: #fff;
}

.lock-dot {
  width: 12px;
  height: 12px;
  border: 2px solid currentColor;
  border-radius: 3px;
  display: inline-block;
}

.wallet-btn {
  background: #fff;
  color: #111;
}

.shop-btn {
  background: linear-gradient(90deg, #5b37f3 0%, #5f2de0 100%);
  color: #fff;
  text-transform: lowercase;
}

.paypal-btn {
  background: #ffc439;
  color: #003087;
  font-style: italic;
}

.gpay-btn {
  background: #000;
  color: #fff;
}

.payment-strip {
  display: grid;
  gap: 12px;
  justify-items: center;
  text-align: center;
}

.payment-strip p {
  margin: 0;
  color: #777;
  font-size: 13px;
}

.payment-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.payment-grid span {
  padding: 5px 8px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid rgba(17, 17, 17, 0.08);
  font-size: 11px;
  font-weight: 700;
}

.recently-section {
  padding-bottom: 84px;
}

.section-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}

.section-head h2 {
  margin: 6px 0 0;
  font-size: 34px;
  line-height: 1;
}

.section-kicker {
  margin: 0;
  color: #7e7e7e;
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.recent-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.recent-card {
  border-radius: 24px;
  overflow: hidden;
  background: #fafafa;
  border: 1px solid rgba(17, 17, 17, 0.06);
}

.recent-image {
  display: block;
  background: #f2f2f2;
}

.recent-image img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  display: block;
}

.recent-copy {
  display: grid;
  gap: 10px;
  padding: 18px;
}

.recent-title {
  color: #111;
  text-decoration: none;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.2;
}

.recent-subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.recent-price {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1100px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }

  .summary-card {
    order: -1;
  }
}

@media (max-width: 760px) {
  .cart-shell {
    padding-top: 32px;
  }

  .cart-heading {
    flex-direction: column;
    align-items: stretch;
  }

  .cart-line {
    grid-template-columns: 92px minmax(0, 1fr);
  }

  .line-quantity,
  .line-total {
    grid-column: 2;
  }

  .line-total {
    text-align: left;
    padding-top: 0;
  }

  .recent-grid {
    grid-template-columns: 1fr;
  }
}
</style>
