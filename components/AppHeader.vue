<template>
  <header class="app-header" :class="{ 'is-scrolled': isScrolled }">
    <NuxtLink
      v-if="resolvedActivity"
      :to="resolvedActivity.linkUrl || '/'"
      class="top-bar"
      :style="activityStyle"
    >
      <div class="container top-bar-content">
        <div class="offer-copy">
          <p class="offer-kicker">{{ resolvedActivity.title }}</p>
          <p class="offer-title">{{ resolvedActivity.subtitle }}</p>
        </div>

        <div class="countdown-panel">
          <div class="countdown">
            <template v-for="(item, index) in countdownItems" :key="item.label">
              <span class="time-block">{{ item.value }}</span>
              <span
                v-if="index < countdownItems.length - 1"
                class="time-separator"
                >:</span
              >
            </template>
          </div>
          <div class="countdown-labels">
            <span v-for="item in countdownItems" :key="`${item.label}-label`">{{
              item.label
            }}</span>
          </div>
        </div>
      </div>
    </NuxtLink>

    <div class="main-nav">
      <div class="container nav-content">
        <NuxtLink to="/" class="logo">
          <h2><i>isinwheel</i></h2>
        </NuxtLink>

        <div class="desktop-nav" @mouseleave="scheduleMenuClose">
          <ul class="nav-list">
            <li
              v-for="item in navItems"
              :key="item.slug"
              class="nav-item"
              :class="{ 'is-active': activeMenu?.slug === item.slug }"
              @mouseenter="handleNavEnter(item)"
            >
              <NuxtLink :to="`/collections/${item.slug}`">{{
                item.name
              }}</NuxtLink>
            </li>

            <li
              class="nav-item support-item"
              @mouseenter="closeMenuImmediately"
            >
              <button type="button" class="support-trigger">
                {{ copy.support }}
              </button>
              <NavDropdown :links="supportLinks" />
            </li>
          </ul>
        </div>

        <div class="actions">
          <button type="button" class="country-pill" @click="toggleLang">
            <span>{{ lang.value === 'en' ? '🇺🇸 EN' : '🇨🇳 中文' }}</span>
          </button>

          <button type="button" class="icon-button" :aria-label="copy.search">
            <SearchIcon class="icon muted" />
          </button>

          <div class="user-menu-wrapper">
            <button
              type="button"
              class="icon-button"
              :aria-label="t('account')"
              @click="handleUserClick"
            >
              <UserIcon class="icon" />
            </button>
          </div>

          <button
            type="button"
            class="cart-icon"
            :aria-label="t('cart')"
            @click="handleCartClick"
          >
            <ShoppingCartIcon class="icon" />
            <span class="cart-count">{{ cart.count.value }}</span>
          </button>
        </div>
      </div>
    </div>

    <div
      class="mega-menus-container"
      @mouseenter="handleMegaMenuEnter"
      @mouseleave="handleMegaMenuLeave"
    >
      <Transition name="fade" mode="out-in">
        <NavMegaMenu
          v-if="activeMenu"
          :key="activeMenu.slug"
          :menu-data="activeMenu"
        />
      </Transition>
    </div>

    <div class="mobile-strip">
      <div class="container mobile-links">
        <NuxtLink
          v-for="item in navItems"
          :key="`mobile-${item.slug}`"
          :to="`/collections/${item.slug}`"
        >
          {{ item.name }}
        </NuxtLink>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { SearchIcon, ShoppingCartIcon, UserIcon } from 'lucide-vue-next'

const { lang, t, setLang } = useShopLocale()
const session = useShopSession()
const cart = useShopCart()
const router = useRouter()

const categoryMenu = ref<any[]>([])
const activity = ref<Record<string, any> | null>(null)
const isScrolled = ref(false)
const hoverMenuSlug = ref<string | null>(null)
const activeMenuCacheSlug = ref<string | null>(null)
const isHoveringMegaMenu = ref(false)
const countdownTarget = ref(0)
const countdownMs = ref(0)
let countdownTimer: ReturnType<typeof window.setInterval> | null = null
let closeMenuTimer: ReturnType<typeof window.setTimeout> | null = null

const fallbackCategories = computed(() => [
  { slug: 'electric-scooters', name: t('electricScooters') },
  { slug: 'electric-bike', name: t('electricBike') },
  { slug: 'electric-skateboard', name: t('electricSkateboard') },
  { slug: 'accessories', name: t('accessories') },
])

const normalizeMenuProducts = (products: unknown) =>
  Array.isArray(products) ? products.slice(0, 4) : []

const normalizeMenuGroups = (groups: unknown, rootSlug: string) =>
  (Array.isArray(groups) ? groups : [])
    .map((group: any) => ({
      ...group,
      allLinkUrl: group?.allLinkUrl || `/collections/${group?.slug || rootSlug}`,
      allLinkText:
        group?.allLinkText ||
        `All ${group?.name || ''}${group?.productCount ? ` (${group.productCount})` : ''}`.trim(),
      products: normalizeMenuProducts(group?.products),
    }))
    .filter((group: any) => group?.name)

const normalizeMenuItem = (item: any) => ({
  ...item,
  banner: item?.banner
    ? {
        ...item.banner,
        linkUrl: item.banner.linkUrl || `/collections/${item.slug}`,
      }
    : null,
  children: normalizeMenuGroups(item?.children, item?.slug),
})

const fallbackActivity = computed(() => ({
  title: lang.value === 'zh' ? '复活节促销' : 'Easter Sale',
  subtitle:
    lang.value === 'zh'
      ? '本周热卖车型限时优惠'
      : "Save on this week's hottest rides",
  countdownEndAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
  desktopBg:
    'https://www.isinwheel.com/cdn/shop/files/4_fa32ee9a-10f9-4743-8a0a-f0c74bc54f07.png?v=1775033994',
  mobileBg:
    'https://www.isinwheel.com/cdn/shop/files/4_fa32ee9a-10f9-4743-8a0a-f0c74bc54f07.png?v=1775033994',
  linkUrl: '/collections/electric-bike',
}))

const resolvedActivity = computed(
  () => activity.value || fallbackActivity.value,
)
const hasUsableMenuData = computed(() => categoryMenu.value.length > 0)
const navItems = computed(() =>
  hasUsableMenuData.value ? categoryMenu.value : fallbackCategories.value,
)
const megaMenuItems = computed(() =>
  categoryMenu.value.filter(
    (item) => Array.isArray(item?.children) && item.children.length > 0,
  ),
)
const visibleMenuSlug = computed(
  () =>
    hoverMenuSlug.value ||
    (isHoveringMegaMenu.value ? activeMenuCacheSlug.value : null),
)
const activeMenu = computed(
  () =>
    megaMenuItems.value.find((item: any) => item.slug === visibleMenuSlug.value) ||
    null,
)

const activityStyle = computed(() => ({
  '--activity-desktop-bg': `url("${resolvedActivity.value.desktopBg}")`,
  '--activity-mobile-bg': `url("${resolvedActivity.value.mobileBg || resolvedActivity.value.desktopBg}")`,
}))

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        support: '支持',
        search: '搜索',
        about: '关于我们',
        contact: '联系我们',
        faq: '常见问题',
        photos: '照片',
        blog: '博客',
        videos: 'Video Labs',
        orders: '订单追踪',
        dealers: '成为经销商',
      }
    : {
        support: 'Support',
        search: 'Search',
        about: 'About Us',
        contact: 'Contact Us',
        faq: 'FAQ',
        photos: 'Photos',
        blog: 'Blog',
        videos: 'Video Labs',
        orders: 'Track Your Order',
        dealers: 'Become A Dealer',
      },
)

const supportLinks = computed(() => [
  { title: copy.value.about, url: '/pages/about-us-1' },
  { title: copy.value.contact, url: '/pages/contact-us' },
  { title: copy.value.faq, url: '/pages/support-faq' },
  { title: copy.value.photos, url: '/pages/photos' },
  { title: copy.value.blog, url: '/blogs/news' },
  { title: copy.value.videos, url: '/pages/isinwheel-videos' },
  { title: copy.value.orders, url: '/account/orders' },
  { title: copy.value.dealers, url: '/account/profile' },
])

const formatCountdownValue = (value: number) => String(value).padStart(2, '0')

const countdownParts = computed(() => {
  const totalSeconds = Math.floor(Math.max(countdownMs.value, 0) / 1000)
  const days = Math.floor(totalSeconds / (24 * 60 * 60))
  const hours = Math.floor((totalSeconds % (24 * 60 * 60)) / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  return { days, hours, minutes, seconds }
})

const countdownItems = computed(() => [
  {
    label: lang.value === 'zh' ? '天' : 'Days',
    value: formatCountdownValue(countdownParts.value.days),
  },
  {
    label: lang.value === 'zh' ? '时' : 'Hours',
    value: formatCountdownValue(countdownParts.value.hours),
  },
  {
    label: lang.value === 'zh' ? '分' : 'Mins',
    value: formatCountdownValue(countdownParts.value.minutes),
  },
  {
    label: lang.value === 'zh' ? '秒' : 'Secs',
    value: formatCountdownValue(countdownParts.value.seconds),
  },
])

const updateCountdown = () => {
  countdownMs.value = Math.max(countdownTarget.value - Date.now(), 0)
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 0
}

const fetchCategoryMenu = async () => {
  try {
    const res = await useHttp('/api/category/menu', {
      query: {
        productLimit: 4,
      },
    })
    categoryMenu.value =
      res?.code === 200
        ? (Array.isArray(res.data) ? res.data : []).map(normalizeMenuItem)
        : []
  } catch {
    categoryMenu.value = []
  }
}

const fetchActivity = async () => {
  try {
    const res = await useHttp('/api/marketing/activities/current')
    activity.value = res?.code === 200 && res.data ? res.data : null
  } catch {
    activity.value = null
  }
  countdownTarget.value = new Date(
    resolvedActivity.value.countdownEndAt,
  ).getTime()
  updateCountdown()
}

const clearMenuCloseTimer = () => {
  if (closeMenuTimer) {
    window.clearTimeout(closeMenuTimer)
    closeMenuTimer = null
  }
}

const resetMenuState = () => {
  hoverMenuSlug.value = null
  isHoveringMegaMenu.value = false
}

const hasMegaMenu = (item: Record<string, any>) =>
  Array.isArray(item?.children) && item.children.length > 0

const handleNavEnter = (item: Record<string, any>) => {
  clearMenuCloseTimer()
  isHoveringMegaMenu.value = false
  if (!hasMegaMenu(item)) {
    resetMenuState()
    return
  }
  hoverMenuSlug.value = item.slug
  activeMenuCacheSlug.value = item.slug
}

const scheduleMenuClose = () => {
  clearMenuCloseTimer()
  closeMenuTimer = window.setTimeout(() => {
    resetMenuState()
  }, 140)
}

const closeMenuImmediately = () => {
  clearMenuCloseTimer()
  activeMenuCacheSlug.value = null
  resetMenuState()
}

const handleMegaMenuEnter = () => {
  if (!activeMenuCacheSlug.value) {
    return
  }
  clearMenuCloseTimer()
  hoverMenuSlug.value = null
  isHoveringMegaMenu.value = true
}

const handleMegaMenuLeave = () => {
  isHoveringMegaMenu.value = false
  scheduleMenuClose()
}

const handleUserClick = async () => {
  if (session.isLoggedIn.value) {
    await router.push('/account/profile')
  } else {
    await router.push('/login')
  }
}

const handleLogout = async () => {
  await session.logout()
  await cart.refreshCart()
  await router.push('/login')
}

const handleCartClick = async () => {
  cart.openCart()
  await cart.refreshCart()
}

const toggleLang = () => {
  setLang(lang.value === 'en' ? 'zh' : 'en')
  if (process.client) {
    window.location.reload()
  }
}

onMounted(async () => {
  handleScroll()
  window.addEventListener('scroll', handleScroll)
  await Promise.allSettled([
    fetchCategoryMenu(),
    fetchActivity(),
    session.fetchMe(),
    cart.refreshCart(),
  ])
  countdownTimer = window.setInterval(updateCountdown, 1000)
})

onUnmounted(() => {
  if (countdownTimer) {
    window.clearInterval(countdownTimer)
  }
  clearMenuCloseTimer()
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped lang="scss">
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
  background: #fff;
 
  will-change: transform;
  box-shadow: none;
  filter: none;
}

.top-bar {
  display: block;
  max-height: 116px;
  overflow: hidden;
  position: relative;
  z-index: 0;
  padding: 4px 0;
  color: #111;
  font-size: 12px;
  text-decoration: none;
  background-image: var(--activity-desktop-bg);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  transition:
    max-height 0.3s ease,
    padding 0.3s ease,
    opacity 0.3s ease;
}

.top-bar-content {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) auto;
  align-items: center;
  gap: 24px;
  min-height: 86px;
}

.offer-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.offer-kicker {
  margin: 0;
  font-size: 12px;
  line-height: 1.2;
  font-weight: 700;
  letter-spacing: 0.01em;
}

.offer-title {
  margin: 2px 0 0;
  color: #090f1f;
  font-size: 30px;
  line-height: 1.02;
  letter-spacing: 0.01em;
  font-weight: 700;
}

.countdown-panel {
  justify-self: end;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-block {
  min-width: 50px;
  height: 50px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  background: #8ad5d0;
  color: #39750a;
  font-size: 26px;
  line-height: 1;
  font-weight: 700;
}

.time-separator {
  color: #37760a;
  font-size: 22px;
  font-weight: 700;
}

.countdown-labels {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.countdown-labels span {
  color: #4f7e0b;
  font-size: 11px;
  text-align: center;
}

.app-header.is-scrolled .top-bar {
  max-height: 0;
  padding: 0;
  opacity: 0;
  overflow: hidden;
  box-shadow: none;
  pointer-events: none;
}

.app-header.is-scrolled {
  box-shadow: none;
  filter: none;
}

:global(body.product-tabs-mode .app-header) {
  background: transparent;
  box-shadow: none;
  opacity: 0;
  transform: translateY(calc(-100% - 12px));
  pointer-events: none;
}

.main-nav {
  padding: 16px 0;
  position: relative;
  z-index: 1;
  background: #fff;
  box-shadow: 0 16px 24px -24px rgba(15, 23, 42, 0.45);
  transition:
    opacity 0.2s ease,
    padding 0.2s ease,
    max-height 0.2s ease;
}

.nav-content {
  position: static;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
}

.logo {
  display: inline-flex;
  align-items: center;
  flex: 0 0 auto;
  text-decoration: none;
}

.logo h2 {
  margin: 0;
  color: #111;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -1px;
}

.desktop-nav {
  position: static;
  display: flex;
  flex: 1 1 auto;
  justify-content: center;
  min-width: 0;
}

.nav-list {
  position: static;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  width: 100%;
  margin: 0;
  padding: 0;
  list-style: none;
}

.nav-item {
  position: relative;
  flex: 0 0 auto;
  font-size: 15px;
  font-weight: 600;
  padding: 20px 0;
  margin: -20px 0;
}

.nav-item a,
.support-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 50px;
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background: transparent;
  color: #111;
  text-decoration: none;
  font: inherit;
  cursor: pointer;
  transition: all 0.25s ease;
}

.nav-item:hover > a,
.nav-item:hover > .support-trigger,
.nav-item.is-active > a,
.nav-item.is-active > .support-trigger,
.nav-item > a.router-link-active {
  background: #111;
  color: #fff;
  border-radius: 200px;
}

.mega-menus-container {
  position: relative;
  z-index: 110;
}

.support-item:hover :deep(.nav-dropdown) {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
  pointer-events: auto;
}

.actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 0 0 auto;
}

.country-pill {
  border: 1px solid #d6dae2;
  border-radius: 20px;
  padding: 4px 12px;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.icon-button,
.cart-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
}

.icon {
  width: 20px;
  height: 20px;
  color: #111;
  transition: color 0.2s ease;
}

.icon:hover,
.icon.muted:hover {
  color: #58cc02;
}

.icon.muted {
  color: #444;
}

.user-menu-wrapper {
  display: flex;
  align-items: center;
}

.cart-icon {
  position: relative;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #e62332;
  color: #fff;
  font-size: 10px;
}

.mobile-strip {
  display: none;
  border-top: 1px solid #eef0f4;
}

.mobile-links {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding: 12px 0;
}

.mobile-links a {
  white-space: nowrap;
  color: #111;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
}

.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.25s ease,
    transform 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 1100px) {
  .top-bar {
    max-height: 176px;
    padding: 8px 0;
    background-image: var(--activity-mobile-bg);
  }

  .top-bar-content {
    grid-template-columns: 1fr;
    justify-items: center;
    gap: 18px;
    min-height: auto;
  }

  .offer-copy {
    width: 100%;
    text-align: center;
  }

  .countdown-panel {
    justify-self: stretch;
    width: 100%;
    min-width: 0;
    max-width: 540px;
  }

  .desktop-nav {
    display: none;
  }

  .mobile-strip {
    display: block;
  }
}

@media (max-width: 640px) {
  .top-bar {
    max-height: 220px;
  }

  .offer-title {
    margin-top: 6px;
    font-size: 20px;
  }

  .time-block {
    min-width: 34px;
    height: 34px;
    font-size: 16px;
  }

  .time-separator {
    font-size: 14px;
  }

  .countdown-labels {
    gap: 10px;
  }

  .countdown-labels span {
    font-size: 10px;
  }

  .actions {
    gap: 12px;
  }

  .country-pill {
    display: none;
  }
}
</style>
