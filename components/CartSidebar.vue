<template>
  <div>
    <transition name="fade">
      <div v-if="cart.isOpen.value" class="overlay" @click="cart.closeCart()" />
    </transition>

    <transition name="slide">
      <aside v-if="cart.isOpen.value" class="sidebar">
        <div class="header">
          <div>
            <h2>{{ t('cart') }}</h2>
            <p>{{ cart.count.value }} item(s)</p>
          </div>
          <button type="button" class="close-btn" @click="cart.closeCart()">×</button>
        </div>

        <div class="body" v-if="cart.items.value.length">
          <article v-for="item in cart.items.value" :key="item.cartItemId" class="cart-item">
            <NuxtLink :to="`/products/${item.slug || item.productId}`" class="thumb-link" @click="cart.closeCart()">
              <img :src="item.productPic" :alt="item.title" />
            </NuxtLink>
            <div class="item-main">
              <NuxtLink
                :to="`/products/${item.slug || item.productId}`"
                class="item-title"
                @click="cart.closeCart()"
              >
                {{ item.title }}
              </NuxtLink>
              <p class="meta">{{ attributeText(item.attributes) }}</p>
              <p v-if="item.addons?.length" class="addons">
                + {{ item.addons.map((addon: any) => addon.name).join(', ') }}
              </p>
              <div class="item-actions">
                <div class="qty-control">
                  <button type="button" @click="changeQty(item, Math.max(1, item.quantity - 1))">-</button>
                  <span>{{ item.quantity }}</span>
                  <button type="button" @click="changeQty(item, item.quantity + 1)">+</button>
                </div>
                <button type="button" class="remove" @click="remove(item.cartItemId)">Remove</button>
              </div>
            </div>
            <div class="amount">{{ money(item.lineAmount) }}</div>
          </article>
        </div>

        <div v-else class="empty-state">
          <p>{{ t('emptyCart') }}</p>
          <NuxtLink to="/" class="continue-link" @click="cart.closeCart()">
            {{ t('continueShopping') }}
          </NuxtLink>
        </div>

        <div class="footer">
          <div class="summary-line">
            <span>{{ t('subtotal') }}</span>
            <strong>{{ money(cart.subtotal.value) }}</strong>
          </div>
          <NuxtLink to="/checkout" class="checkout-link" @click="cart.closeCart()">
            {{ t('checkout') }}
          </NuxtLink>
        </div>
      </aside>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue'

const cart = useShopCart()
const { t } = useShopLocale()
const { money, attributeText } = useShopFormat()

const changeQty = async (item: any, quantity: number) => {
  await cart.updateQuantity(item.cartItemId, quantity)
}

const remove = async (cartItemId: number) => {
  await cart.removeItem(cartItemId)
}

watch(
  () => cart.isOpen.value,
  async (value) => {
    if (value) {
      document.body.style.overflow = 'hidden'
      await cart.refreshCart()
    } else {
      document.body.style.overflow = ''
    }
  },
)
</script>

<style scoped lang="scss">
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  z-index: 60;
}

.sidebar {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 61;
  width: min(460px, 100vw);
  height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: -18px 0 40px rgba(15, 23, 42, 0.15);
}

.header,
.footer {
  padding: 24px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.footer {
  border-bottom: none;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
  margin-top: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  gap: 16px;

  h2 {
    margin: 0 0 4px;
    font-size: 24px;
  }

  p {
    margin: 0;
    color: #64748b;
  }
}

.close-btn {
  border: none;
  background: #f1f5f9;
  width: 40px;
  height: 40px;
  border-radius: 999px;
  font-size: 22px;
}

.body {
  padding: 20px 24px;
  overflow-y: auto;
}

.cart-item {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr) auto;
  gap: 16px;
  padding: 18px 0;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.thumb-link img {
  width: 88px;
  height: 88px;
  object-fit: cover;
  border-radius: 16px;
  background: #f8fafc;
}

.item-title {
  display: inline-block;
  color: #0f172a;
  text-decoration: none;
  font-weight: 700;
  line-height: 1.4;
  margin-bottom: 6px;
}

.meta,
.addons {
  margin: 0 0 8px;
  color: #64748b;
  font-size: 13px;
}

.item-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.qty-control {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  border-radius: 999px;
  padding: 6px 10px;

  button {
    border: none;
    background: none;
    font-size: 18px;
    line-height: 1;
  }
}

.remove {
  border: none;
  background: none;
  color: #dc2626;
  font-weight: 600;
}

.amount {
  font-weight: 800;
  color: #0f172a;
}

.empty-state {
  padding: 40px 24px;
  color: #475569;
}

.continue-link,
.checkout-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 50px;
  border-radius: 999px;
  background: #0f766e;
  color: #fff;
  text-decoration: none;
  font-weight: 700;
}

.summary-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.fade-enter-active,
.fade-leave-active,
.slide-enter-active,
.slide-leave-active {
  transition: all 0.22s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
}
</style>
