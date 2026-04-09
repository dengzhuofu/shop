<template>
  <div>
    <transition name="fade">
      <div v-if="cart.isOpen.value" class="cart-overlay" @click="handleClose" />
    </transition>

    <transition name="slide-right">
      <aside v-if="cart.isOpen.value" class="cart-sidebar" aria-label="Shopping cart">
        <header class="cart-header">
          <div class="cart-tabs" role="tablist" :aria-label="t('cart')">
            <button type="button" class="tab-button is-active" aria-selected="true">
              <span class="tab-label">{{ t('cart') }}</span>
              <span class="tab-count">{{ cart.count.value }}</span>
            </button>
            <button type="button" class="tab-button is-muted" aria-selected="false">
              <span class="tab-label">{{ copy.recentlyViewed }}</span>
            </button>
          </div>

          <button type="button" class="btn-close" :aria-label="copy.close" @click="handleClose">
            <X :size="20" :stroke-width="1.9" />
          </button>
        </header>

        <div class="cart-body">
          <div v-if="cart.loading.value && !cart.items.value.length" class="empty-cart is-loading">
            <div class="empty-visual" />
            <p>{{ copy.loading }}</p>
          </div>

          <div v-else-if="cart.items.value.length" class="cart-items">
            <article v-for="item in cart.items.value" :key="item.cartItemId" class="cart-item">
              <NuxtLink :to="itemLink(item)" class="item-image" @click="handleClose">
                <img :src="item.productPic" :alt="item.title" loading="lazy" />
              </NuxtLink>

              <div class="item-main">
                <NuxtLink :to="itemLink(item)" class="item-title" @click="handleClose">
                  {{ item.title }}
                </NuxtLink>

                <p v-if="itemMeta(item)" class="item-meta">{{ itemMeta(item) }}</p>

                <div class="price-row">
                  <span class="current-price">{{ linePrice(item) }}</span>
                  <span v-if="showUnitPrice(item)" class="unit-price">
                    {{ copy.qtyLabel }} {{ item.quantity }} &middot; {{ money(item.unitPrice || 0) }} {{ copy.each }}
                  </span>
                </div>

                <div v-if="addonNames(item.addons).length" class="addon-pills">
                  <span class="addon-badge">{{ copy.addons }}</span>
                  <span
                    v-for="name in addonNames(item.addons)"
                    :key="`${item.cartItemId}-${name}`"
                    class="addon-pill"
                  >
                    {{ name }}
                  </span>
                </div>
              </div>

              <div class="item-actions">
                <div class="quantity-box" :aria-label="`${copy.quantity}: ${item.quantity}`">
                  <span class="quantity-value">{{ item.quantity }}</span>
                  <div class="quantity-buttons">
                    <button
                      type="button"
                      class="qty-btn"
                      :aria-label="copy.increase"
                      :disabled="isUpdating.value || !canIncrease(item)"
                      @click="changeQty(item, item.quantity + 1)"
                    >
                      <ChevronUp :size="15" :stroke-width="2" />
                    </button>
                    <button
                      type="button"
                      class="qty-btn"
                      :aria-label="copy.decrease"
                      :disabled="isUpdating.value || item.quantity <= 1"
                      @click="changeQty(item, item.quantity - 1)"
                    >
                      <ChevronDown :size="15" :stroke-width="2" />
                    </button>
                  </div>
                </div>

                <button
                  type="button"
                  class="btn-remove"
                  :disabled="isUpdating.value"
                  @click="remove(item.cartItemId)"
                >
                  {{ copy.remove }}
                </button>
              </div>
            </article>
          </div>

          <div v-else class="empty-cart">
            <div class="empty-visual">
              <ShoppingBag :size="24" :stroke-width="1.8" />
            </div>
            <p>{{ t('emptyCart') }}</p>
          </div>
        </div>

        <footer class="cart-footer">
          <div class="cart-tools">
            <button type="button" class="tool-button">
              <NotebookText :size="18" :stroke-width="1.9" />
              <span>{{ copy.orderNote }}</span>
            </button>
            <button type="button" class="tool-button">
              <BadgePercent :size="18" :stroke-width="1.9" />
              <span>{{ t('coupon') }}</span>
            </button>
          </div>

          <div class="summary-row">
            <p class="tax-note">{{ copy.taxesNote }}</p>
            <div class="summary-total">
              <span class="summary-label">{{ t('subtotal') }}</span>
              <strong class="summary-amount">{{ money(cart.subtotal.value) }}</strong>
            </div>
          </div>

          <div class="protection-card">
            <div class="protection-icon" aria-hidden="true">
              <Package :size="30" :stroke-width="1.9" />
              <ShieldCheck class="protection-shield" :size="18" :stroke-width="2" />
            </div>

            <div class="protection-copy">
              <div class="protection-topline">
                <strong>{{ copy.protection }}</strong>
                <label class="switch">
                  <input v-model="shippingProtectionEnabled" type="checkbox" />
                  <span class="slider" />
                </label>
              </div>
              <p>{{ copy.protectionNote }}</p>
            </div>
          </div>

          <div class="checkout-actions">
            <NuxtLink to="/checkout" class="btn-checkout" @click="handleClose">
              <LockKeyhole :size="18" :stroke-width="2" />
              <span>{{ t('checkout') }}</span>
            </NuxtLink>
            <NuxtLink to="/" class="btn-secondary" @click="handleClose">
              {{ t('continueShopping') }}
            </NuxtLink>
          </div>
        </footer>
      </aside>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import {
  BadgePercent,
  ChevronDown,
  ChevronUp,
  LockKeyhole,
  NotebookText,
  Package,
  ShieldCheck,
  ShoppingBag,
  X,
} from 'lucide-vue-next'
import { clearPageScrollLock, setPageScrollLocked } from '~/utils/scrollLock'

const cart = useShopCart()
const { lang, t } = useShopLocale()
const { money, attributeText } = useShopFormat()

const shippingProtectionEnabled = ref(true)
const isUpdating = ref(false)

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        recentlyViewed: '\u6700\u8fd1\u6d4f\u89c8',
        close: '\u5173\u95ed\u8d2d\u7269\u8f66',
        loading: '\u6b63\u5728\u52a0\u8f7d\u8d2d\u7269\u8f66...',
        orderNote: '\u8ba2\u5355\u5907\u6ce8',
        taxesNote: '\u7a0e\u8d39\u4e0e\u8fd0\u8d39\u5c06\u5728\u7ed3\u8d26\u65f6\u8ba1\u7b97',
        protection: '\u914d\u9001\u4fdd\u969c',
        protectionNote:
          '\u5efa\u8bae\u5f00\u542f\u914d\u9001\u4fdd\u969c\uff0c\u4ee5\u4fbf\u5728\u5305\u88f9\u4e22\u5931\u3001\u7834\u635f\u6216\u88ab\u76d7\u65f6\u83b7\u5f97\u66f4\u5b89\u5fc3\u7684\u5904\u7406\u3002',
        addons: '\u9644\u52a0\u9879',
        remove: '\u79fb\u9664',
        quantity: '\u6570\u91cf',
        qtyLabel: '\u6570\u91cf',
        each: '\u6bcf\u4ef6',
        increase: '\u589e\u52a0\u6570\u91cf',
        decrease: '\u51cf\u5c11\u6570\u91cf',
      }
    : {
        recentlyViewed: 'Recently viewed',
        close: 'Close cart',
        loading: 'Loading cart...',
        orderNote: 'Order note',
        taxesNote: 'Taxes and shipping calculated at checkout',
        protection: 'Shipping protection',
        protectionNote:
          'We recommend adding shipping protection for peace of mind in case your package is lost, damaged, or stolen during delivery.',
        addons: 'Add-ons',
        remove: 'Remove',
        quantity: 'Quantity',
        qtyLabel: 'Qty',
        each: 'each',
        increase: 'Increase quantity',
        decrease: 'Decrease quantity',
      },
)

const itemLink = (item: any) => `/products/${item.slug || item.productId}`

const itemMeta = (item: any) => {
  const text = attributeText(item.attributes)
  return text?.trim?.() || ''
}

const addonNames = (addons: any) => {
  if (!Array.isArray(addons)) {
    return []
  }
  return addons
    .map((addon: any) => addon?.name || addon?.title || addon?.code)
    .filter((value: string | undefined) => Boolean(value))
}

const linePrice = (item: any) => {
  const fallback = Number(item.unitPrice || 0) * Number(item.quantity || 1)
  return money(item.lineAmount || fallback)
}

const showUnitPrice = (item: any) =>
  Number(item.quantity || 1) > 1 || Number(item.addonAmount || 0) > 0

const canIncrease = (item: any) => {
  if (typeof item?.stock !== "number") {
    return true
  }
  return Number(item.quantity || 0) < item.stock
}

const changeQty = async (item: any, quantity: number) => {
  if (quantity < 1 || isUpdating.value || quantity === Number(item.quantity || 1)) {
    return
  }

  isUpdating.value = true
  try {
    await cart.updateQuantity(item.cartItemId, quantity, item.addons?.map((addon: any) => addon.code))
  } finally {
    isUpdating.value = false
  }
}

const remove = async (cartItemId: number) => {
  if (isUpdating.value) {
    return
  }

  isUpdating.value = true
  try {
    await cart.removeItem(cartItemId)
  } finally {
    isUpdating.value = false
  }
}

const handleClose = () => {
  cart.closeCart()
  clearPageScrollLock()
}

onMounted(() => {
  setPageScrollLocked(cart.isOpen.value)
})

onUnmounted(() => {
  clearPageScrollLock()
})

watch(
  () => cart.isOpen.value,
  async (newVal) => {
    if (newVal) {
      setPageScrollLocked(true)
      await cart.refreshCart()
    } else {
      clearPageScrollLock()
    }
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
.cart-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(20, 20, 20, 0.34);
  backdrop-filter: blur(5px);
}

.cart-sidebar {
  --cart-border: rgba(17, 17, 17, 0.08);
  --cart-border-strong: rgba(17, 17, 17, 0.16);
  --cart-muted: #6e6e6e;
  --cart-muted-soft: #bcbcbc;
  --cart-soft: #f8f5f0;
  --cart-soft-strong: #f1ede7;
  --cart-accent: #111111;
  --cart-danger: #d92d4c;
  --cart-ring: rgba(17, 17, 17, 0.18);

  position: fixed;
  top: 0;
  right: 0;
  width: min(100%, 548px);
  height: 100dvh;
  display: grid;
  grid-template-rows: auto 1fr auto;
  overflow: hidden;
  border-top-left-radius: 30px;
  border-bottom-left-radius: 30px;
  background:
    #ffffff;
  color: #111111;
  font-family: "Inter", "Noto Sans SC", "Segoe UI", sans-serif;
  // box-shadow:
  //   -28px 0 72px rgba(17, 17, 17, 0.16),
  //   -2px 0 0 rgba(255, 255, 255, 0.7);
  z-index: 1001;
}

.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 30px 28px 24px;
  border-bottom: 1px solid var(--cart-border);
}

.cart-tabs {
  display: flex;
  align-items: flex-end;
  gap: 26px;
  min-width: 0;
}

.tab-button {
  min-height: 44px;
  display: inline-flex;
  align-items: flex-start;
  gap: 4px;
  padding: 0;
  border: 0;
  background: transparent;
  color: inherit;
  font: inherit;
  cursor: pointer;
}

.tab-button.is-active {
  color: #111111;
}

.tab-button.is-muted {
  color: var(--cart-muted-soft);
}

.tab-button.is-muted .tab-label {
  font-weight: 760;
}

.tab-label {
  max-width: 100%;
  font-size: clamp(2.05rem, 4vw, 2.32rem);
  line-height: 0.96;
  font-weight: 800;
  letter-spacing: -0.058em;
  white-space: nowrap;
}

.tab-count {
  position: relative;
  top: 0.06em;
  font-size: 0.76rem;
  line-height: 1;
  font-weight: 700;
}

.btn-close {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--cart-border);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.98);
  color: #111111;
  cursor: pointer;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease;
}

.btn-close:hover {
  background: var(--cart-soft);
  border-color: var(--cart-border-strong);
}

.cart-body {
  padding: 28px 28px 10px;
  overflow-y: auto;
  overscroll-behavior: contain;
  scrollbar-gutter: stable;
}

.cart-body::-webkit-scrollbar {
  width: 10px;
}

.cart-body::-webkit-scrollbar-track {
  background: transparent;
}

.cart-body::-webkit-scrollbar-thumb {
  border: 3px solid transparent;
  border-radius: 999px;
  background: rgba(17, 17, 17, 0.18);
  background-clip: padding-box;
}

.cart-body::-webkit-scrollbar-thumb:hover {
  background: rgba(17, 17, 17, 0.28);
  background-clip: padding-box;
}

.cart-items {
  display: grid;
  gap: 24px;
}

.cart-item {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr) auto;
  gap: 18px;
  align-items: start;
  padding-bottom: 22px;
  border-bottom: 1px solid var(--cart-border);
}

.item-image {
  display: block;
  width: 96px;
  height: 96px;
  overflow: hidden;
  border-radius: 18px;
  background: linear-gradient(145deg, rgba(248, 245, 240, 0.96), rgba(241, 237, 231, 0.8));
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.78);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-main {
  min-width: 0;
  padding-top: 2px;
}

.item-title {
  display: -webkit-box;
  margin: 0 0 6px;
  overflow: hidden;
  color: #111111;
  text-decoration: none;
  font-size: 1rem;
  line-height: 1.28;
  font-weight: 600;
  letter-spacing: -0.02em;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-title:hover {
  color: #000000;
}

.item-meta {
  margin: 0 0 8px;
  color: var(--cart-muted);
  font-size: 0.86rem;
  line-height: 1.48;
}

.price-row {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 8px 10px;
}

.current-price {
  color: var(--cart-danger);
  font-size: 1.1rem;
  line-height: 1.1;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.unit-price {
  color: var(--cart-muted);
  font-size: 0.79rem;
  line-height: 1.4;
}

.addon-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.addon-badge,
.addon-pill {
  min-height: 30px;
  display: inline-flex;
  align-items: center;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 0.76rem;
  line-height: 1;
}

.addon-badge {
  border: 1px solid rgba(217, 45, 76, 0.1);
  background: #fff2f5;
  color: var(--cart-danger);
  font-weight: 700;
}

.addon-pill {
  border: 1px solid rgba(17, 17, 17, 0.05);
  background: #f8f6f2;
  color: #444444;
}

.item-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  gap: 14px;
}

.quantity-box {
  display: grid;
  grid-template-columns: minmax(28px, auto) 24px;
  gap: 10px;
  align-items: center;
  min-width: 78px;
  padding: 12px 12px 12px 14px;
  border: 1px solid rgba(17, 17, 17, 0.05);
  border-radius: 14px;
  background: linear-gradient(180deg, #fbfaf8, #f3efea);
}

.quantity-value {
  text-align: center;
  font-size: 0.98rem;
  line-height: 1;
  font-weight: 700;
}

.quantity-buttons {
  display: grid;
  gap: 4px;
}

.qty-btn {
  width: 24px;
  height: 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: #111111;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease,
    opacity 0.2s ease;
}

.qty-btn:hover:not(:disabled) {
  background: rgba(17, 17, 17, 0.08);
}

.qty-btn:disabled {
  opacity: 0.34;
  cursor: not-allowed;
}

.btn-remove {
  padding: 0;
  border: 0;
  background: transparent;
  color: #4e4e4e;
  font-size: 0.84rem;
  text-decoration: underline;
  text-underline-offset: 2px;
  cursor: pointer;
  transition:
    color 0.2s ease,
    opacity 0.2s ease;
}

.btn-remove:hover:not(:disabled) {
  color: var(--cart-danger);
}

.btn-remove:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.empty-cart {
  min-height: 100%;
  display: grid;
  place-content: center;
  gap: 14px;
  padding: 72px 0 48px;
  text-align: center;
}

.empty-cart p {
  margin: 0;
  color: #4e4e4e;
  font-size: 1rem;
  line-height: 1.45;
  font-weight: 500;
}

.empty-visual {
  width: 68px;
  height: 68px;
  display: grid;
  place-items: center;
  margin: 0 auto;
  border-radius: 22px;
  background: linear-gradient(180deg, #faf8f5, #f1ede7);
  color: #111111;
}

.empty-cart.is-loading .empty-visual {
  background: linear-gradient(90deg, #f3efea 18%, #fbfaf8 50%, #f3efea 82%);
  background-size: 200% 100%;
  animation: shimmer 1.3s linear infinite;
}

.cart-footer {
  padding: 12px 28px 28px;
  border-top: 1px solid var(--cart-border);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.97), rgba(249, 247, 244, 0.99)),
    #ffffff;
}

.cart-tools {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  border-bottom: 1px solid var(--cart-border);
}

.tool-button {
  min-height: 56px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 12px 14px 16px;
  border: 0;
  background: transparent;
  color: #111111;
  font: inherit;
  font-size: 0.96rem;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s ease;
}

.tool-button + .tool-button {
  border-left: 1px solid var(--cart-border);
}

.tool-button:hover {
  color: var(--cart-danger);
}

.summary-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 0 20px;
}

.tax-note {
  max-width: 214px;
  margin: 0;
  color: #474747;
  font-size: 1rem;
  line-height: 1.36;
}

.summary-total {
  text-align: right;
}

.summary-label {
  display: block;
  margin-bottom: 6px;
  color: #4b4b4b;
  font-size: 0.96rem;
}

.summary-amount {
  display: block;
  color: #111111;
  font-size: clamp(2rem, 4vw, 2.35rem);
  line-height: 0.98;
  font-weight: 900;
  letter-spacing: -0.05em;
}

.protection-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 14px;
  align-items: center;
  padding: 16px 16px 16px 15px;
  border: 1px solid rgba(17, 17, 17, 0.07);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(251, 249, 246, 0.98), rgba(247, 243, 238, 0.98));
}

.protection-icon {
  position: relative;
  width: 80px;
  height: 80px;
  display: grid;
  place-items: center;
  border-radius: 20px;
  background: linear-gradient(180deg, #faf8f5, #f2eee8);
  color: #111111;
}

.protection-shield {
  position: absolute;
  right: 11px;
  bottom: 11px;
  padding: 4px;
  border-radius: 999px;
  background: #ffffff;
  color: #9f9f9f;
  box-shadow: 0 6px 18px rgba(17, 17, 17, 0.08);
}

.protection-copy {
  min-width: 0;
}

.protection-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 6px;
}

.protection-topline strong {
  font-size: 1.02rem;
  line-height: 1.2;
}

.protection-copy p {
  margin: 0;
  color: #4e4e4e;
  font-size: 0.92rem;
  line-height: 1.5;
}

.switch {
  position: relative;
  width: 42px;
  height: 24px;
  flex-shrink: 0;
  display: inline-flex;
}

.switch input {
  position: absolute;
  inset: 0;
  opacity: 0;
}

.slider {
  position: absolute;
  inset: 0;
  border-radius: 999px;
  background: #a9a9a9;
  cursor: pointer;
  transition: background 0.25s ease;
}

.slider::before {
  content: "";
  position: absolute;
  top: 4px;
  left: 4px;
  width: 16px;
  height: 16px;
  border-radius: 999px;
  background: #ffffff;
  transition: transform 0.25s ease;
}

.switch input:checked + .slider {
  background: #111111;
}

.switch input:checked + .slider::before {
  transform: translateX(18px);
}

.checkout-actions {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(0, 1fr);
  gap: 12px;
  margin-top: 20px;
}

.btn-checkout,
.btn-secondary {
  min-height: 62px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 0 24px;
  border-radius: 999px;
  text-decoration: none;
  font-size: 1.03rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease,
    box-shadow 0.2s ease;
}

.btn-checkout {
  border: 1px solid #111111;
  background: #111111;
  color: #ffffff;
}

.btn-checkout:hover {
  background: #1f1f1f;
  box-shadow: 0 12px 24px rgba(17, 17, 17, 0.12);
}

.btn-secondary {
  border: 1.5px solid rgba(17, 17, 17, 0.8);
  background: transparent;
  color: #111111;
}

.btn-secondary:hover {
  background: #f8f5f0;
}

.tab-button:focus-visible,
.btn-close:focus-visible,
.qty-btn:focus-visible,
.btn-remove:focus-visible,
.tool-button:focus-visible,
.switch input:focus-visible + .slider,
.btn-checkout:focus-visible,
.btn-secondary:focus-visible,
.item-image:focus-visible,
.item-title:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px var(--cart-ring);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.35s cubic-bezier(0.22, 0.8, 0.3, 1);
}

.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(100%);
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }

  100% {
    background-position: -200% 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .empty-cart.is-loading .empty-visual {
    animation: none;
  }

  .fade-enter-active,
  .fade-leave-active,
  .slide-right-enter-active,
  .slide-right-leave-active,
  .btn-close,
  .qty-btn,
  .btn-remove,
  .tool-button,
  .btn-checkout,
  .btn-secondary,
  .slider,
  .slider::before {
    transition: none;
  }
}

@media (max-width: 640px) {
  .cart-sidebar {
    width: 100%;
    border-radius: 0;
  }

  .cart-header,
  .cart-body,
  .cart-footer {
    padding-left: 20px;
    padding-right: 20px;
  }

  .cart-tabs {
    gap: 14px;
  }

  .cart-item {
    grid-template-columns: 82px minmax(0, 1fr);
  }

  .item-actions {
    grid-column: 2;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }

  .summary-row,
  .checkout-actions {
    display: grid;
    grid-template-columns: 1fr;
  }

  .summary-total {
    text-align: left;
  }

  .tax-note {
    max-width: none;
  }

  .protection-card {
    grid-template-columns: 1fr;
  }

  .protection-icon {
    width: 72px;
    height: 72px;
  }
}
</style>
