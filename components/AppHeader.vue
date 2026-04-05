<template>
  <header class="app-header">
    <div class="promo-bar">
      <div class="container promo-inner">
        <span>{{ t('brandTagline') }}</span>
        <button type="button" class="lang-switch" @click="toggleLang">
          {{ lang === 'en' ? '中文' : 'EN' }}
        </button>
      </div>
    </div>

    <div class="main-bar">
      <div class="container main-inner">
        <NuxtLink to="/" class="logo">isinwheel</NuxtLink>

        <nav class="desktop-nav">
          <NuxtLink
            v-for="item in navItems"
            :key="item.slug"
            :to="`/collections/${item.slug}`"
            class="nav-link"
          >
            {{ item.name }}
          </NuxtLink>
        </nav>

        <div class="actions">
          <NuxtLink v-if="session.isLoggedIn.value" to="/account/profile" class="action-link">
            {{ t('account') }}
          </NuxtLink>
          <NuxtLink v-else to="/login" class="action-link">
            {{ t('login') }}
          </NuxtLink>
          <button v-if="session.isLoggedIn.value" type="button" class="action-link ghost" @click="handleLogout">
            {{ t('logout') }}
          </button>
          <button type="button" class="cart-button" @click="cart.openCart()">
            {{ t('cart') }}
            <span class="cart-count">{{ cart.count.value }}</span>
          </button>
        </div>
      </div>
    </div>

    <div class="mobile-strip">
      <div class="container mobile-links">
        <NuxtLink
          v-for="item in navItems"
          :key="`mobile-${item.slug}`"
          :to="`/collections/${item.slug}`"
          class="mobile-link"
        >
          {{ item.name }}
        </NuxtLink>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

const { lang, setLang, t } = useShopLocale()
const session = useShopSession()
const cart = useShopCart()

const categories = ref<any[]>([])

const fallbackCategories = computed(() => [
  { slug: 'electric-scooters', name: t('electricScooters') },
  { slug: 'electric-bike', name: t('electricBike') },
  { slug: 'electric-skateboard', name: t('electricSkateboard') },
  { slug: 'accessories', name: t('accessories') },
])

const navItems = computed(() => (categories.value.length ? categories.value : fallbackCategories.value))

const fetchCategories = async () => {
  try {
    const res = await useHttp('/api/category/tree')
    if (res?.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    categories.value = fallbackCategories.value
  }
}

const toggleLang = () => {
  setLang(lang.value === 'en' ? 'zh' : 'en')
  if (process.client) {
    window.location.reload()
  }
}

const handleLogout = async () => {
  await session.logout()
  await cart.refreshCart()
  await navigateTo('/login')
}

onMounted(async () => {
  await Promise.allSettled([fetchCategories(), session.fetchMe(), cart.refreshCart()])
})
</script>

<style scoped lang="scss">
.app-header {
  position: sticky;
  top: 0;
  z-index: 40;
  background: rgba(248, 247, 241, 0.95);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(17, 24, 39, 0.08);
}

.promo-bar {
  background: linear-gradient(90deg, #0f172a, #1f2937);
  color: #f8fafc;

  .promo-inner {
    min-height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 13px;
    letter-spacing: 0.02em;
  }
}

.lang-switch {
  border: 1px solid rgba(248, 250, 252, 0.18);
  border-radius: 999px;
  background: transparent;
  color: inherit;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 700;
}

.main-bar {
  .main-inner {
    min-height: 76px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
  }
}

.logo {
  color: #0f172a;
  text-decoration: none;
  font-family: 'Poppins', sans-serif;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.desktop-nav {
  display: flex;
  align-items: center;
  gap: 22px;
  flex: 1;
  justify-content: center;
}

.nav-link,
.action-link,
.mobile-link {
  color: #1f2937;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  transition: color 0.2s ease;

  &:hover {
    color: #0f766e;
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ghost {
  border: none;
  background: none;
  padding: 0;
}

.cart-button {
  border: none;
  border-radius: 999px;
  background: #0f766e;
  color: white;
  padding: 10px 16px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
}

.cart-count {
  min-width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 12px;
}

.mobile-strip {
  display: none;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
  background: white;
}

.mobile-links {
  display: flex;
  gap: 14px;
  overflow-x: auto;
  padding: 12px 0;
}

@media (max-width: 960px) {
  .desktop-nav {
    display: none;
  }

  .mobile-strip {
    display: block;
  }

  .main-bar .main-inner {
    min-height: 68px;
  }

  .actions {
    gap: 10px;
  }
}

@media (max-width: 640px) {
  .promo-inner {
    gap: 12px;
  }

  .logo {
    font-size: 24px;
  }

  .actions .action-link {
    display: none;
  }
}
</style>
