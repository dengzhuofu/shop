<template>
  <div class="layout-account">
    <AppHeader />
    <main class="account-main container">
      <div class="account-hero">
        <div>
          <p class="eyebrow">{{ t('account') }}</p>
          <h1>{{ session.user.value?.fullName || session.user.value?.email || t('account') }}</h1>
        </div>
      </div>

      <nav class="account-tabs">
        <NuxtLink to="/account/profile" active-class="active">{{ t('profile') }}</NuxtLink>
        <NuxtLink to="/account/orders" active-class="active">{{ t('orders') }}</NuxtLink>
      </nav>

      <section class="account-content">
        <slot />
      </section>
    </main>
    <AppFooter />
    <CartSidebar />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

const { t } = useShopLocale()
const session = useShopSession()

onMounted(() => {
  session.fetchMe()
})
</script>

<style scoped lang="scss">
.layout-account {
  min-height: 100vh;
  background: #f8f7f1;
}

.account-main {
  padding-top: 40px;
}

.account-hero {
  border-radius: 28px;
  padding: 28px 32px;
  background: linear-gradient(135deg, #0f172a, #1f2937);
  color: white;
  margin-bottom: 22px;

  .eyebrow {
    margin: 0 0 8px;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.68);
  }

  h1 {
    margin: 0;
    font-size: 34px;
  }
}

.account-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;

  a {
    border-radius: 999px;
    padding: 12px 18px;
    text-decoration: none;
    color: #0f172a;
    background: rgba(255, 255, 255, 0.7);
    border: 1px solid rgba(15, 23, 42, 0.08);
    font-weight: 700;

    &.active {
      background: #0f766e;
      color: white;
    }
  }
}

.account-content {
  margin-bottom: 32px;
}
</style>
