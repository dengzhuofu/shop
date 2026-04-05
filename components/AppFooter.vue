<template>
  <footer class="app-footer">
    <div class="container footer-grid">
      <div class="brand-block">
        <div class="brand">isinwheel</div>
        <p>{{ t('footerNote') }}</p>
      </div>

      <div class="link-block">
        <h3>{{ t('categoryCollections') }}</h3>
        <NuxtLink to="/collections/electric-scooters">{{ t('electricScooters') }}</NuxtLink>
        <NuxtLink to="/collections/electric-bike">{{ t('electricBike') }}</NuxtLink>
        <NuxtLink to="/collections/electric-skateboard">{{ t('electricSkateboard') }}</NuxtLink>
        <NuxtLink to="/collections/accessories">{{ t('accessories') }}</NuxtLink>
      </div>

      <div class="link-block">
        <h3>{{ t('account') }}</h3>
        <NuxtLink to="/account/profile">{{ t('profile') }}</NuxtLink>
        <NuxtLink to="/account/orders">{{ t('orders') }}</NuxtLink>
        <NuxtLink to="/checkout">{{ t('checkout') }}</NuxtLink>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const { t } = useShopLocale()
</script>

<style scoped lang="scss">
.app-footer {
  background: #0f172a;
  color: #e2e8f0;
  padding: 56px 0;
  margin-top: 72px;
}

.footer-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) repeat(2, minmax(0, 1fr));
  gap: 32px;
}

.brand {
  font-family: 'Poppins', sans-serif;
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 14px;
}

.brand-block p {
  max-width: 420px;
  color: rgba(226, 232, 240, 0.78);
  line-height: 1.7;
}

.link-block {
  display: flex;
  flex-direction: column;
  gap: 10px;

  h3 {
    margin: 0 0 8px;
    color: white;
    font-size: 14px;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  a {
    color: rgba(226, 232, 240, 0.82);
    text-decoration: none;

    &:hover {
      color: white;
    }
  }
}

@media (max-width: 720px) {
  .footer-grid {
    grid-template-columns: 1fr;
  }
}
</style>
