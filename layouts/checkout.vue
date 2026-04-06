<template>
  <div class="layout-checkout">
    <header class="checkout-header">
      <div class="container checkout-header__inner">
        <NuxtLink to="/" class="checkout-brand">
          <span class="checkout-brand__mark">O</span>
          <span class="checkout-brand__word">isinwheel</span>
        </NuxtLink>

        <div class="checkout-header__meta">
          <div class="checkout-header__copy">
            <span class="checkout-header__eyebrow">{{ copy.secure }}</span>
            <strong>{{ copy.title }}</strong>
          </div>

          <div class="checkout-header__actions">
            <NuxtLink to="/" class="header-link">{{ copy.continueShopping }}</NuxtLink>
            <NuxtLink
              v-if="session.isLoggedIn.value"
              to="/account/orders"
              class="header-link primary"
            >
              {{ copy.orders }}
            </NuxtLink>
          </div>
        </div>
      </div>
    </header>

    <main class="checkout-main">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'

const { lang } = useShopLocale()
const session = useShopSession()

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        secure: '安全结算',
        title: '结算',
        continueShopping: '继续购物',
        orders: '订单',
      }
    : {
        secure: 'Secure checkout',
        title: 'Checkout',
        continueShopping: 'Continue shopping',
        orders: 'Orders',
      },
)

onMounted(async () => {
  if (!session.user.value) {
    await session.fetchMe()
  }
})
</script>

<style scoped lang="scss">
.layout-checkout {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(88, 204, 2, 0.08), transparent 26%),
    linear-gradient(180deg, #f7f8f4 0%, #ffffff 22%);
}

.checkout-header {
  position: sticky;
  top: 0;
  z-index: 80;
  backdrop-filter: blur(16px);
  background: rgba(255, 255, 255, 0.88);
  border-bottom: 1px solid rgba(17, 24, 39, 0.08);
}

.checkout-header__inner {
  min-height: 88px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.checkout-brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #111827;
  text-decoration: none;
}

.checkout-brand__mark {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #111827;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
}

.checkout-brand__word {
  font-size: 28px;
  font-style: italic;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.checkout-header__meta {
  display: flex;
  align-items: center;
  gap: 24px;
}

.checkout-header__copy {
  display: flex;
  flex-direction: column;
  gap: 4px;

  strong {
    font-size: 18px;
    color: #111827;
  }
}

.checkout-header__eyebrow {
  color: #667085;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.checkout-header__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-link {
  min-height: 42px;
  padding: 0 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  border: 1px solid #d0d5dd;
  color: #111827;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  background: #fff;

  &.primary {
    border-color: #111827;
    background: #111827;
    color: #fff;
  }
}

.checkout-main {
  min-height: calc(100vh - 88px);
}

@media (max-width: 860px) {
  .checkout-header__inner,
  .checkout-header__meta {
    align-items: flex-start;
    flex-direction: column;
  }

  .checkout-header__inner {
    min-height: auto;
    padding: 16px 0;
  }

  .checkout-header__actions {
    width: 100%;
    flex-wrap: wrap;
  }
}

@media (max-width: 640px) {
  .checkout-brand__word {
    font-size: 24px;
  }

  .header-link {
    flex: 1 1 auto;
  }
}
</style>
