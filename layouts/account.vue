<template>
  <div class="layout-account">
    <header class="account-header">
      <div class="container header-inner">
        <NuxtLink to="/" class="brand-mark">
          <span class="brand-logo">◎</span>
          <span class="brand-word">isinwheel</span>
        </NuxtLink>

        <nav class="account-nav">
          <NuxtLink to="/account/orders" active-class="active">{{ t('orders') }}</NuxtLink>
          <NuxtLink to="/account/profile" active-class="active">{{ t('profile') }}</NuxtLink>
          <NuxtLink to="/checkout" active-class="active">{{ t('checkout') }}</NuxtLink>
        </nav>

        <div ref="accountMenuRef" class="account-user">
          <button
            type="button"
            class="account-user-trigger"
            :aria-expanded="userMenuOpen"
            @click="toggleUserMenu"
          >
            <span class="avatar-pill">{{ userInitial }}</span>
            <ChevronDownIcon class="arrow-icon" :class="{ open: userMenuOpen }" />
          </button>

          <div v-if="userMenuOpen" class="account-dropdown">
            <div class="dropdown-profile">
              <span class="dropdown-avatar">{{ userInitial }}</span>
              <div class="dropdown-usercopy">
                <strong>{{ session.user.value?.fullName || session.user.value?.email || t('account') }}</strong>
                <p>{{ session.user.value?.email || '' }}</p>
              </div>
            </div>

            <div class="dropdown-divider" />

            <button type="button" class="dropdown-item" @click="toggleLang">
              {{ lang.value === 'zh' ? 'English' : '中文' }}
            </button>
            <button type="button" class="dropdown-item" @click="goTo('/account/profile')">{{ t('profile') }}</button>
            <button type="button" class="dropdown-item" @click="goTo('/account/orders')">{{ t('orders') }}</button>
            <button type="button" class="dropdown-item" @click="goTo('/checkout')">{{ t('checkout') }}</button>
            <div class="dropdown-divider" />
            <button type="button" class="dropdown-item logout" @click="handleLogout">
              {{ t('logout') }}
            </button>
          </div>
        </div>
      </div>
    </header>

    <main class="account-main" :class="{ 'is-checkout': isCheckout }">
      <div v-if="!isCheckout" class="container account-shell">
        <div class="page-title-wrap">
          <h1>{{ pageTitle }}</h1>
        </div>

        <nav class="page-tabs">
          <NuxtLink to="/account/profile" active-class="active">{{ t('profile') }}</NuxtLink>
          <NuxtLink to="/account/orders" active-class="active">{{ t('orders') }}</NuxtLink>
        </nav>

        <slot />
      </div>

      <slot v-else />
    </main>

    <footer class="account-footer">
      <div class="container footer-inner">
        <div class="footer-region">
          <span class="region-flag">🌎</span>
          <span>{{ footerCopy.region }}</span>
          <ChevronDownIcon class="footer-arrow" />
        </div>

        <nav class="footer-links">
          <NuxtLink to="/account/profile">{{ footerCopy.refund }}</NuxtLink>
          <NuxtLink to="/checkout">{{ t('shipping') }}</NuxtLink>
          <NuxtLink to="/account/profile">{{ footerCopy.privacy }}</NuxtLink>
          <NuxtLink to="/account/profile">{{ footerCopy.terms }}</NuxtLink>
          <NuxtLink to="/account/profile">{{ footerCopy.contact }}</NuxtLink>
        </nav>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ChevronDownIcon } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const { lang, setLang, t } = useShopLocale()
const session = useShopSession()

const userMenuOpen = ref(false)
const accountMenuRef = ref<HTMLElement | null>(null)

const isCheckout = computed(() => route.path.startsWith('/checkout'))
const pageTitle = computed(() => {
  if (route.path.startsWith('/account/orders')) {
    return t('orders')
  }
  return t('profile')
})

const footerCopy = computed(() =>
  lang.value === 'zh'
    ? {
        region: '美国',
        refund: '退款政策',
        privacy: '隐私政策',
        terms: '服务条款',
        contact: '联系信息',
      }
    : {
        region: 'United States',
        refund: 'Refund policy',
        privacy: 'Privacy policy',
        terms: 'Terms of service',
        contact: 'Contact information',
      },
)

const userInitial = computed(() => {
  const first = session.user.value?.firstName?.trim()
  const email = session.user.value?.email?.trim()
  return (first?.[0] || email?.[0] || 'U').toUpperCase()
})

const handleUserClick = async () => {
  if (!session.isLoggedIn.value) {
    await router.push('/login')
  }
}

const goTo = async (path: string) => {
  userMenuOpen.value = false
  await router.push(path)
}

const handleLogout = async () => {
  await session.logout()
  userMenuOpen.value = false
  await router.push('/login')
}

const toggleUserMenu = async () => {
  if (!session.isLoggedIn.value) {
    await handleUserClick()
    return
  }
  userMenuOpen.value = !userMenuOpen.value
}

const toggleLang = () => {
  setLang(lang.value === 'en' ? 'zh' : 'en')
  userMenuOpen.value = false
  if (process.client) {
    window.location.reload()
  }
}

const handleDocumentClick = (event: MouseEvent) => {
  if (!accountMenuRef.value) {
    return
  }
  const target = event.target as Node | null
  if (target && !accountMenuRef.value.contains(target)) {
    userMenuOpen.value = false
  }
}

onMounted(async () => {
  await session.fetchMe()
  if (!session.isLoggedIn.value) {
    await router.push('/login')
  }
  if (process.client) {
    document.addEventListener('click', handleDocumentClick)
  }
})

onBeforeUnmount(() => {
  if (process.client) {
    document.removeEventListener('click', handleDocumentClick)
  }
})
</script>

<style scoped lang="scss">
.layout-account {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f7f7f5;
}

.account-header {
  position: sticky;
  top: 0;
  z-index: 60;
  background: #fff;
  border-bottom: 1px solid #ececec;
}

.header-inner {
  min-height: 92px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 32px;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #111;
  text-decoration: none;
}

.brand-logo {
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #111;
  border-radius: 999px;
  font-size: 13px;
  line-height: 1;
}

.brand-word {
  font-size: 26px;
  line-height: 1;
  font-weight: 800;
  letter-spacing: -0.04em;
  font-style: italic;
}

.account-nav {
  display: inline-flex;
  align-items: center;
  gap: 14px;

  a {
    padding: 8px 14px;
    border-radius: 999px;
    color: #111;
    text-decoration: none;
    font-size: 15px;
    font-weight: 600;

    &.active,
    &:hover {
      background: #111;
      color: #fff;
    }
  }
}

.account-user {
  position: relative;
}

.account-user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
}

.avatar-pill {
  width: 44px;
  height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #8f8f8f;
  border-radius: 999px;
  color: #555;
  font-weight: 700;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  color: #333;
  transition: transform 0.2s ease;
}

.arrow-icon.open {
  transform: rotate(180deg);
}

.account-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  min-width: 320px;
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 24px;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.12);
  padding: 20px 0 12px;
  z-index: 80;
}

.dropdown-profile {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 20px 12px;
}

.dropdown-avatar {
  width: 54px;
  height: 54px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #8f8f8f;
  border-radius: 999px;
  color: #555;
  font-size: 20px;
  font-weight: 700;
}

.dropdown-usercopy {
  min-width: 0;

  strong {
    display: block;
    color: #111;
    font-size: 16px;
    line-height: 1.3;
    word-break: break-word;
  }

  p {
    margin: 4px 0 0;
    color: #6b7280;
    font-size: 14px;
    word-break: break-word;
  }
}

.dropdown-item {
  width: 100%;
  display: block;
  padding: 14px 20px;
  background: transparent;
  border: none;
  color: #111;
  text-align: left;
  text-decoration: none;
  font-size: 16px;
  cursor: pointer;

  &:hover {
    background: #f7f7f5;
  }

  &.logout {
    color: #c5221f;
  }
}

.dropdown-divider {
  height: 1px;
  margin: 6px 20px;
  background: #ececec;
}

.account-main {
  flex: 1;
  padding: 34px 0 64px;
}

.account-main.is-checkout {
  padding: 0;
}

.account-shell {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.page-title-wrap h1 {
  margin: 0;
  font-size: 26px;
  line-height: 1.1;
  color: #111;
}

.page-tabs {
  display: flex;
  gap: 14px;

  a {
    padding: 10px 18px;
    border-radius: 999px;
    background: #fff;
    border: 1px solid #ececec;
    color: #111;
    text-decoration: none;
    font-weight: 600;

    &.active,
    &:hover {
      background: #111;
      border-color: #111;
      color: #fff;
    }
  }
}

.account-footer {
  background: #f7f7f5;
  border-top: 1px solid #e7e7e4;
}

.footer-inner {
  min-height: 84px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.footer-region {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #111;
  font-size: 15px;
  font-weight: 600;
}

.footer-arrow {
  width: 14px;
  height: 14px;
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;

  a {
    color: #111;
    font-size: 15px;
    font-weight: 500;
    text-decoration: underline;
    text-underline-offset: 3px;
  }
}

@media (max-width: 960px) {
  .header-inner {
    grid-template-columns: 1fr auto;
    gap: 18px;
  }

  .account-nav {
    grid-column: 1 / -1;
    order: 3;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .account-dropdown {
    right: -8px;
    min-width: 280px;
  }

  .footer-inner {
    padding: 20px 0;
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .brand-word {
    font-size: 22px;
  }

  .page-tabs {
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .account-dropdown {
    right: 0;
    min-width: min(320px, calc(100vw - 24px));
  }
}
</style>
