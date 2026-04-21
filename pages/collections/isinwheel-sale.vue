<template>
  <div class="spring-sale-page">
    <div ref="saleNavShellRef" class="sale-subnav-shell">
      <nav class="sale-subnav" :class="{ 'is-sticky': showStickySaleNav }">
        <div class="container sale-subnav-inner">
          <p class="sale-subnav-label">isinwheel Sale</p>

          <div class="sale-subnav-links">
            <button
              v-for="item in saleSubnavItems"
              :key="item.id"
              type="button"
              class="sale-subnav-link"
              :class="{ 'is-active': activeSectionId === item.id }"
              @click="scrollToSection(item.id)"
            >
              {{ item.label }}
            </button>
          </div>
        </div>
      </nav>
    </div>

    <section ref="heroSectionRef" class="hero-band full-bleed">
      <div class="hero-overlay"></div>
      <div class="container hero-content">
        <p class="hero-kicker">Spring Adventures</p>
        <h1>Ride Into Spring</h1>
        <p class="hero-text">
          Thoughtfully designed e-rides for brighter days ahead.
        </p>
      </div>
    </section>

    <section id="sale-overview" class="featured-sale">
      <div class="container">
        <div class="section-head">
          <h2>Spring Sale</h2>
          <p>Meet the bestselling e-rides ready for spring adventures</p>
        </div>

        <div class="featured-rides" @mouseleave="handleRideLeave">
          <article
            v-for="ride in featuredRides"
            :key="ride.id"
            class="feature-ride"
            :class="{
              'is-active': expandedRideId === ride.id,
              'is-inactive': expandedRideId && expandedRideId !== ride.id,
            }"
            @mouseenter="handleRideEnter(ride.id)"
            @focusin="handleRideEnter(ride.id)"
            @click="handleRideClick(ride.id)"
          >
            <div class="feature-ride-media">
              <img
                class="feature-ride-background"
                :src="ride.backgroundImage"
                :alt="ride.name"
                loading="lazy"
              />
              <div class="feature-ride-scrim"></div>
              <div class="feature-ride-sale-tag">
                <span>SALE</span>
                <strong>{{ ride.saleTag }}</strong>
              </div>
            </div>

            <div class="feature-ride-heading">
              <p class="feature-ride-name">{{ ride.name }}</p>
              <p class="feature-ride-tagline">{{ ride.tagline }}</p>
            </div>

            <div class="feature-ride-thumb">
              <img :src="ride.thumbImage" :alt="ride.title" loading="lazy" />
              <p>{{ ride.title }}</p>
            </div>

            <div class="feature-ride-expanded">
              <div class="feature-ride-expanded-thumb">
                <img :src="ride.thumbImage" :alt="ride.title" loading="lazy" />
              </div>

              <div class="feature-ride-footer">
                <div class="feature-ride-copy">
                  <p class="feature-ride-title">{{ ride.title }}</p>
                  <div class="feature-ride-price-row">
                    <strong>{{ ride.salePrice }}</strong>
                    <span>{{ ride.comparePrice }}</span>
                  </div>
                </div>

                <div class="feature-ride-actions">
                  <NuxtLink :to="ride.productUrl" class="feature-btn is-light">
                    View
                  </NuxtLink>

                  <button
                    type="button"
                    class="feature-btn is-dark"
                    :disabled="pendingRideId === ride.id"
                    @click.stop="addRideToCart(ride)"
                  >
                    {{ pendingRideId === ride.id ? 'Adding...' : 'Add to cart' }}
                  </button>
                </div>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section id="ride-match" class="match-section">
      <div class="container">
        <div class="section-head section-head-left">
          <h2>Find Your Perfect Match</h2>
          <p>
            From entry-level to premium, find a ride that fits your spring
            journey.
          </p>
        </div>

        <div class="tab-row">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            type="button"
            class="tab-btn"
            :class="{ 'is-active': activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>

        <div class="product-grid">
          <article
            v-for="product in activeProducts"
            :key="product.slug"
            class="sale-card"
          >
            <NuxtLink :to="`/products/${product.slug}`" class="sale-image">
              <img :src="product.image" :alt="product.title" loading="lazy" />
            </NuxtLink>

            <div class="sale-copy">
              <div class="sale-topline">
                <span class="sale-pill">Spring Sale</span>
                <span class="sale-tag">{{ product.label }}</span>
              </div>

              <NuxtLink :to="`/products/${product.slug}`" class="sale-title">
                {{ product.title }}
              </NuxtLink>
              <p class="sale-specs">{{ product.specs }}</p>

              <div class="sale-price">
                <strong>{{ money(product.price) }}</strong>
                <span>{{ money(product.compareAtPrice) }}</span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section id="membership-program" class="membership-section">
      <div class="container membership-card">
        <div>
          <p class="section-chip">isinwheel Membership Program</p>
          <h2>Earn more perks on every spring ride purchase.</h2>
        </div>

        <div class="membership-stats">
          <div>
            <strong>3X</strong>
            <span>Points on featured rides</span>
          </div>
          <div>
            <strong>24h</strong>
            <span>Priority support response</span>
          </div>
          <div>
            <strong>VIP</strong>
            <span>Early access to new drops</span>
          </div>
        </div>
      </div>
    </section>

    <section id="bundle-save" class="bundle-section">
      <div class="container">
        <div class="section-head section-head-left">
          <h2>Bundle &amp; Save</h2>
          <p>Stack extra value with spring-ready add-ons and protection plans.</p>
        </div>

        <div class="bundle-grid">
          <article class="bundle-card">
            <p class="bundle-card-title">Bundle &amp; Save 10%</p>
            <p>
              Pair a ride with accessories or protection to unlock stronger
              spring-sale value.
            </p>
          </article>

          <article class="bundle-card">
            <p class="bundle-card-title">Spring Sale Guarantee</p>
            <p>
              Ride coverage, quick support, and commuter essentials in one
              cleaner checkout path.
            </p>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'

// Keep the page implementation intact so it can be restored by flipping this flag later.
const springSalePageEnabled = false

if (!springSalePageEnabled) {
  throw createError({
    statusCode: 404,
    statusMessage: 'Page Not Found',
  })
}

type SaleTab = 'scooter' | 'bike' | 'skateboard'
type SectionId =
  | 'sale-overview'
  | 'ride-match'
  | 'membership-program'
  | 'bundle-save'

type FeaturedRide = {
  id: string
  name: string
  tagline: string
  title: string
  salePrice: string
  comparePrice: string
  saleTag: string
  thumbImage: string
  backgroundImage: string
  productUrl: string
  slug: string
}

useHead({
  title: 'isinwheel Sale',
})

const { money } = useShopFormat()
const cart = useShopCart()
const quickActions = useProductQuickActions()

const heroSectionRef = ref<HTMLElement | null>(null)
const saleNavShellRef = ref<HTMLElement | null>(null)
const showStickySaleNav = ref(false)
const activeSectionId = ref<SectionId>('sale-overview')
const expandedRideId = ref<string | null>(null)
const pendingRideId = ref<string | null>(null)
const activeTab = ref<SaleTab>('scooter')
const isMobileViewport = ref(false)
const saleNavOffsetTop = ref(0)

const toCdnUrl = (path: string) =>
  path.startsWith('http') ? path : `https:${path}`

const saleSubnavItems: Array<{ id: SectionId; label: string }> = [
  { id: 'sale-overview', label: 'Spring Sale' },
  { id: 'ride-match', label: 'Find Your Perfect Match' },
  { id: 'membership-program', label: 'Membership Program' },
  { id: 'bundle-save', label: 'Bundle & Save' },
]

const featuredRides: FeaturedRide[] = [
  {
    id: 's-nova',
    name: 'S Nova',
    tagline: 'Smart, Stylish, City-Ready',
    title: 'S Nova Commuting Electric Scooter',
    salePrice: '349.99',
    comparePrice: '$499.99',
    saleTag: '$150',
    thumbImage: toCdnUrl(
      '//www.isinwheel.com/cdn/shop/files/s_nova_pro_40fa2f3f-2a4b-4230-88aa-069054926b94.png?v=1760954693',
    ),
    backgroundImage: toCdnUrl(
      '//www.isinwheel.com/cdn/shop/files/15_2e3f9840-bdd8-4951-9f18-27f3001875b3.png?v=1765594822',
    ),
    productUrl: '/products/s-nova-commuting-electric-scooter',
    slug: 's-nova-commuting-electric-scooter',
  },
  {
    id: 'h7-pro',
    name: 'H7 Pro',
    tagline: 'Premium Comfort, Peak Performance',
    title: 'isinwheel H7Pro 1200W High-End Commuting Electric Scooter with Seat',
    salePrice: '799.99',
    comparePrice: '$1,099.99',
    saleTag: '$310',
    thumbImage: toCdnUrl(
      '//www.isinwheel.com/cdn/shop/files/h7pro-111_258efde7-c6a7-411a-9152-63ed3adf307b.png?v=1760954775',
    ),
    backgroundImage: toCdnUrl(
      '//www.isinwheel.com/cdn/shop/files/11_8b4b0c77-b2ed-45af-adc0-95e5412f7c97.png?v=1765594149',
    ),
    productUrl:
      '/products/isinwheel-h7-pro-1200w-high-end-commuting-electric-scooter-with-seat',
    slug: 'isinwheel-h7-pro-1200w-high-end-commuting-electric-scooter-with-seat',
  },
  {
    id: 'u8-plus',
    name: 'U8 Plus',
    tagline: 'Comfort Meets Capability.',
    title: 'isinwheel U8 Plus Electric Bike for Adults',
    salePrice: '$789.99',
    comparePrice: '$899.99',
    saleTag: '$110',
    thumbImage: toCdnUrl(
      '//www.isinwheel.com/cdn/shop/files/18.png?v=1770618143',
    ),
    backgroundImage: toCdnUrl(
      '//www.isinwheel.com/cdn/shop/files/192.168.8.1_-_F__25_isinwheel.png?v=1770618168',
    ),
    productUrl: '/products/isinwheel-u8-plus-electric-bike-for-adults',
    slug: 'isinwheel-u8-plus-electric-bike-for-adults',
  },
]

const tabs = [
  { id: 'scooter' as const, label: 'E-Scooter' },
  { id: 'bike' as const, label: 'E-Bike' },
  { id: 'skateboard' as const, label: 'E-Skateboard' },
]

const products = [
  {
    slug: 's-nova-pro-commuting-electric-scooter',
    title: 'S Nova Pro',
    label: '1000W',
    specs: '1000W | 28MPH | 38 Miles | 10" Pneumatic Tires',
    price: 489.99,
    compareAtPrice: 599.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/Snova_pro_33db2906-b0d8-4c3a-92c1-c56b5336aca5.png?v=1772177599',
    category: 'scooter',
  },
  {
    slug: 'isinwheel-gt1-800w-off-road-electric-scooter',
    title: 'GT1 Electric Scooter',
    label: '800W',
    specs: '800W | 28MPH | 28 Miles | 10" Off Road Tires',
    price: 499.99,
    compareAtPrice: 799.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/GT1_3b22c303-cc7f-459f-b1d5-081a57bb7f76.png?v=1772177620',
    category: 'scooter',
  },
  {
    slug: 'isinwheel-s10max-1000w-high-end-commuting-electric-scooter',
    title: 'S10Max Electric Scooter',
    label: '1000W',
    specs: '1000W | 28MPH | 37 Miles | 10" Pneumatic Tires',
    price: 609.99,
    compareAtPrice: 899.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/S10max_aee4d2a1-d7c6-4a7b-a34a-2c9f3bee7027.png?v=1772177646',
    category: 'scooter',
  },
  {
    slug: 'isinwheel-h7-pro-1200w-high-end-commuting-electric-scooter-with-seat',
    title: 'H7 Pro Electric Scooter',
    label: '1200W',
    specs: '1200W | 38MPH | 43 Miles | 11" Tires',
    price: 799.99,
    compareAtPrice: 1099.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/h7pro-111_258efde7-c6a7-411a-9152-63ed3adf307b.png?v=1760954775',
    category: 'scooter',
  },
  {
    slug: 'isinwheel-u8-electric-bike-for-adults',
    title: 'U8 E-Bike',
    label: '1000W',
    specs: '1000W | 28MPH | 75 Miles | 20*3 Tires',
    price: 609.99,
    compareAtPrice: 799.99,
    image: 'https://www.isinwheel.com/cdn/shop/files/DSC5385.jpg?v=1764072089',
    category: 'bike',
  },
  {
    slug: 'isinwheel-u8-plus-electric-bike-for-adults',
    title: 'U8 Plus E-Bike',
    label: '1500W',
    specs: '1500W | 28MPH | 75 Miles | 26*4.0 Fat Tires',
    price: 789.99,
    compareAtPrice: 899.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/U8_plus_bc49eba5-1219-4328-b5cb-bccec0de3c05.png?v=1772177787',
    category: 'bike',
  },
  {
    slug: 'isinwheel-r6-1000w-retro-electric-bike',
    title: 'R6 Retro E-Bike',
    label: '1000W',
    specs: '1000W | 28MPH | 75 Miles | 20*4.0 Off Road Tires',
    price: 799.99,
    compareAtPrice: 999.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/R6_bf13e26c-d3ff-4333-827f-49171606a230.png?v=1772177806',
    category: 'bike',
  },
  {
    slug: 'isinwheel-r6-pro-1500w-retro-electric-bike',
    title: 'R6 Pro E-Bike',
    label: '1500W',
    specs: '1500W | 35MPH | 105 Miles | 20*4.0 Tires',
    price: 849.99,
    compareAtPrice: 1099.99,
    image: 'https://www.isinwheel.com/cdn/shop/files/DSC0803.jpg?v=1775036302',
    category: 'bike',
  },
  {
    slug: 'isinwheel-v10-off-road-electric-skateboard-with-ambient-light-remote-control',
    title: 'V10 E-Skateboard',
    label: '1500W*2',
    specs: '1500W*2 | 32MPH | 28 Miles | 6 Inch Wheel',
    price: 619.99,
    compareAtPrice: 1099.99,
    image: 'https://www.isinwheel.com/cdn/shop/files/14.png?v=1749455001',
    category: 'skateboard',
  },
  {
    slug: 'isinwheel-v8-electric-skateboard-with-remote',
    title: 'V8 E-Skateboard',
    label: '600W*2',
    specs: '600W*2 | 28MPH | 12 Miles | 105mm Off Road Wheels',
    price: 399.99,
    compareAtPrice: 599.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/v8_8f1b4e73-f1b7-4e8c-aa24-66d81de1d42e.png?v=1772177505',
    category: 'skateboard',
  },
  {
    slug: 'isinwheel-v6-electric-skateboard',
    title: 'V6 E-Skateboard',
    label: '500W',
    specs: '500W | 18MPH | 10 Miles | LED Deck',
    price: 279.99,
    compareAtPrice: 399.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/GT2.jpg?v=1757652052',
    category: 'skateboard',
  },
  {
    slug: 'isinwheel-v4-electric-skateboard',
    title: 'V4 E-Skateboard',
    label: '450W',
    specs: '450W | 15MPH | 8 Miles | Compact Deck',
    price: 239.99,
    compareAtPrice: 329.99,
    image:
      'https://www.isinwheel.com/cdn/shop/files/v8_1.jpg?v=1767102048',
    category: 'skateboard',
  },
] as const

const activeProducts = computed(() =>
  products.filter((product) => product.category === activeTab.value),
)

const updateViewportMode = () => {
  isMobileViewport.value = window.innerWidth <= 960
  if (isMobileViewport.value && !expandedRideId.value) {
    expandedRideId.value = featuredRides[0]?.id || null
  }
  if (!isMobileViewport.value && expandedRideId.value && !showStickySaleNav.value) {
    expandedRideId.value = null
  }
}

const measureOffsets = () => {
  saleNavOffsetTop.value =
    (saleNavShellRef.value?.getBoundingClientRect().top || 0) + window.scrollY
}

const updateSectionState = () => {
  const probe = window.scrollY + 180
  let nextSection: SectionId = 'sale-overview'

  for (const item of saleSubnavItems) {
    const el = document.getElementById(item.id)
    if (el && probe >= el.offsetTop) {
      nextSection = item.id
    }
  }

  activeSectionId.value = nextSection
}

const syncStickyState = () => {
  showStickySaleNav.value = window.scrollY >= Math.max(saleNavOffsetTop.value - 6, 0)
  document.body.classList.toggle(
    'spring-sale-header-hidden',
    showStickySaleNav.value,
  )
  updateSectionState()
}

const handlePageScroll = () => {
  syncStickyState()
}

const scrollToSection = (sectionId: SectionId) => {
  const el = document.getElementById(sectionId)
  if (!el) {
    return
  }
  const offset = showStickySaleNav.value ? 94 : 134
  window.scrollTo({
    top: Math.max(el.offsetTop - offset, 0),
    behavior: 'smooth',
  })
}

const handleRideEnter = (rideId: string) => {
  expandedRideId.value = rideId
}

const handleRideClick = (rideId: string) => {
  if (!isMobileViewport.value) {
    return
  }
  expandedRideId.value = rideId
}

const handleRideLeave = () => {
  if (isMobileViewport.value) {
    return
  }
  expandedRideId.value = null
}

const addRideToCart = async (ride: FeaturedRide) => {
  if (pendingRideId.value) {
    return
  }

  pendingRideId.value = ride.id
  try {
    await quickActions.addSingleSkuToCart({ slug: ride.slug }, 1)
  } finally {
    pendingRideId.value = null
  }
}

onMounted(async () => {
  document.body.classList.add('spring-sale-route')
  await cart.refreshCart()
  updateViewportMode()
  measureOffsets()
  syncStickyState()
  window.addEventListener('resize', updateViewportMode)
  window.addEventListener('resize', measureOffsets)
  window.addEventListener('scroll', handlePageScroll, { passive: true })
})

onUnmounted(() => {
  document.body.classList.remove('spring-sale-route')
  document.body.classList.remove('spring-sale-header-hidden')
  window.removeEventListener('resize', updateViewportMode)
  window.removeEventListener('resize', measureOffsets)
  window.removeEventListener('scroll', handlePageScroll)
})
</script>

<style scoped lang="scss">
.spring-sale-page {
  background:
    radial-gradient(circle at top, rgba(144, 223, 58, 0.14), transparent 28%),
    linear-gradient(180deg, #fbfff1 0%, #ffffff 34%, #fbfdf7 100%);
  color: #111;
  padding-bottom: 88px;
}

.full-bleed {
  width: 100vw;
  margin-left: calc(50% - 50vw);
  margin-right: calc(50% - 50vw);
}

.sale-subnav-shell {
  min-height: 74px;
}

.sale-subnav {
  position: relative;
  z-index: 70;
  width: 100%;
  border-top: 1px solid rgba(17, 17, 17, 0.05);
  border-bottom: 1px solid rgba(17, 17, 17, 0.08);
  background: rgba(255, 255, 255, 0.84);
  backdrop-filter: blur(18px);
  transition:
    box-shadow 0.25s ease,
    background 0.25s ease;
}

.sale-subnav.is-sticky {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  box-shadow: 0 18px 40px -32px rgba(15, 23, 42, 0.35);
}

.sale-subnav-inner {
  min-height: 74px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.sale-subnav-label {
  margin: 0;
  color: #4a8f11;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.sale-subnav-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.sale-subnav-link {
  min-height: 42px;
  padding: 0 18px;
  border: 1px solid rgba(17, 17, 17, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  color: #111;
  font: inherit;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease,
    color 0.2s ease;
}

.sale-subnav-link:hover,
.sale-subnav-link.is-active {
  border-color: #75d431;
  background: #75d431;
  color: #fff;
}

.hero-band {
  position: relative;
  min-height: clamp(360px, 46vw, 620px);
  display: flex;
  align-items: flex-end;
  background:
    linear-gradient(180deg, rgba(0, 0, 0, 0.02) 0%, rgba(0, 0, 0, 0.5) 100%),
    url('https://www.isinwheel.com/cdn/shop/files/1_f86cc1d0-064f-4df4-aee6-c52be970fc18.png?v=1774521032')
      center center / cover no-repeat;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 20%, rgba(117, 212, 49, 0.18), transparent 32%),
    linear-gradient(180deg, rgba(7, 17, 28, 0.04) 0%, rgba(7, 17, 28, 0.55) 100%);
}

.hero-content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 14px;
  padding-top: 120px;
  padding-bottom: 52px;
  color: #fff;
}

.hero-kicker {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  font-weight: 800;
}

.hero-band h1 {
  margin: 0;
  max-width: 620px;
  font-size: clamp(48px, 7vw, 92px);
  line-height: 0.95;
  letter-spacing: -0.05em;
}

.hero-text {
  margin: 0;
  max-width: 540px;
  font-size: clamp(16px, 1.8vw, 22px);
  line-height: 1.55;
  color: rgba(255, 255, 255, 0.92);
}

.featured-sale,
.match-section,
.membership-section,
.bundle-section {
  scroll-margin-top: 108px;
}

.featured-sale {
  padding: 56px 0 34px;
}

.section-head {
  display: grid;
  justify-items: center;
  gap: 10px;
  margin-bottom: 30px;
  text-align: center;
}

.section-head-left {
  justify-items: start;
  text-align: left;
}

.section-head h2 {
  margin: 0;
  color: #75d431;
  font-size: clamp(38px, 5vw, 60px);
  line-height: 0.96;
  letter-spacing: -0.05em;
}

.section-head p {
  margin: 0;
  max-width: 720px;
  font-size: clamp(18px, 2.2vw, 26px);
  line-height: 1.28;
  font-weight: 700;
}

.featured-rides {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.feature-ride {
  position: relative;
  flex: 1 1 0;
  min-height: 552px;
  padding: 26px 28px 28px;
  border-radius: 18px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(17, 17, 17, 0.05);
  box-shadow: 0 22px 56px -48px rgba(17, 17, 17, 0.24);
  transition:
    flex 0.34s ease,
    transform 0.34s ease,
    background 0.34s ease,
    box-shadow 0.34s ease;
  cursor: pointer;
}

.feature-ride.is-active {
  flex: 1 1 68%;
  background: #f3f8e9;
  box-shadow: 0 28px 62px -42px rgba(17, 17, 17, 0.32);
}

.feature-ride.is-inactive {
  flex: 1 1 16%;
}

.feature-ride-media {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-ride.is-active .feature-ride-media {
  opacity: 1;
}

.feature-ride-background,
.feature-ride-scrim {
  position: absolute;
  inset: 0;
}

.feature-ride-background {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.feature-ride-scrim {
  background:
    linear-gradient(180deg, rgba(0, 0, 0, 0.12) 0%, rgba(0, 0, 0, 0.02) 46%, rgba(0, 0, 0, 0.08) 100%);
}

.feature-ride-sale-tag {
  position: absolute;
  top: 0;
  right: 0;
  width: 126px;
  height: 126px;
  display: grid;
  place-items: start end;
  padding: 10px 14px;
  background:
    radial-gradient(circle at 100% 0, #75d431 0, #75d431 71%, transparent 72%);
  color: #111;
  text-align: right;
  transform: scale(0.88);
  opacity: 0;
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.feature-ride.is-active .feature-ride-sale-tag {
  opacity: 1;
  transform: scale(1);
}

.feature-ride-sale-tag span,
.feature-ride-sale-tag strong {
  display: block;
  transform: rotate(38deg);
  transform-origin: right top;
  font-weight: 900;
}

.feature-ride-sale-tag span {
  font-size: 15px;
  margin-top: 2px;
}

.feature-ride-sale-tag strong {
  margin-top: -4px;
  font-size: 22px;
  line-height: 1;
}

.feature-ride-heading,
.feature-ride-thumb,
.feature-ride-expanded {
  position: relative;
  z-index: 1;
}

.feature-ride-heading {
  display: grid;
  gap: 4px;
  justify-items: center;
  text-align: center;
  transition:
    color 0.3s ease,
    align-items 0.3s ease,
    justify-items 0.3s ease;
}

.feature-ride.is-active .feature-ride-heading {
  justify-items: start;
  text-align: left;
  color: #fff;
}

.feature-ride-name {
  margin: 0;
  font-size: clamp(22px, 2vw, 40px);
  line-height: 1;
  font-weight: 800;
}

.feature-ride-tagline {
  margin: 0;
  font-size: 14px;
  line-height: 1.3;
}

.feature-ride-thumb {
  display: grid;
  justify-items: center;
  align-content: end;
  min-height: 448px;
  padding-top: 28px;
  transition:
    opacity 0.28s ease,
    transform 0.28s ease;
}

.feature-ride-thumb img {
  width: min(100%, 220px);
  max-height: 285px;
  object-fit: contain;
}

.feature-ride-thumb p {
  margin: 18px 0 0;
  max-width: 270px;
  font-size: 15px;
  line-height: 1.2;
  font-weight: 700;
  text-align: center;
}

.feature-ride.is-active .feature-ride-thumb {
  opacity: 0;
  transform: translateY(18px);
  pointer-events: none;
}

.feature-ride-expanded {
  position: absolute;
  inset: auto 0 0;
  display: grid;
  gap: 10px;
  opacity: 0;
  transform: translateY(16px);
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.feature-ride.is-active .feature-ride-expanded {
  opacity: 1;
  transform: translateY(0);
}

.feature-ride-expanded-thumb {
  position: absolute;
  left: 28px;
  bottom: 74px;
}

.feature-ride-expanded-thumb img {
  width: clamp(170px, 18vw, 248px);
  max-height: 240px;
  object-fit: contain;
  filter: drop-shadow(0 18px 30px rgba(17, 17, 17, 0.16));
}

.feature-ride-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  min-height: 88px;
  padding: 18px 28px;
  background: #75d431;
}

.feature-ride-copy {
  padding-left: clamp(182px, 18vw, 268px);
}

.feature-ride-title {
  margin: 0 0 6px;
  color: #fff;
  font-size: clamp(18px, 1.65vw, 30px);
  line-height: 1.12;
  font-weight: 800;
}

.feature-ride-price-row {
  display: flex;
  align-items: baseline;
  gap: 14px;
  flex-wrap: wrap;
}

.feature-ride-price-row strong {
  color: #fff;
  font-size: clamp(26px, 2.2vw, 42px);
  line-height: 1;
}

.feature-ride-price-row span {
  color: rgba(255, 255, 255, 0.88);
  font-size: clamp(18px, 1.4vw, 26px);
  font-weight: 700;
  text-decoration: line-through;
}

.feature-ride-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 0 0 auto;
}

.feature-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 116px;
  min-height: 54px;
  padding: 0 24px;
  border: none;
  border-radius: 999px;
  text-decoration: none;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
}

.feature-btn.is-light {
  background: rgba(255, 255, 255, 0.92);
  color: #75d431;
}

.feature-btn.is-dark {
  background: #111;
  color: #fff;
}

.feature-btn:disabled {
  opacity: 0.6;
  cursor: wait;
}

.match-section {
  padding: 28px 0 34px;
}

.tab-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 28px;
}

.tab-btn {
  min-height: 48px;
  padding: 0 20px;
  border: 1px solid rgba(17, 17, 17, 0.1);
  border-radius: 999px;
  background: #fff;
  color: #111;
  font: inherit;
  font-weight: 700;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease,
    border-color 0.2s ease;
}

.tab-btn.is-active {
  border-color: #75d431;
  background: #75d431;
  color: #fff;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 22px;
}

.sale-card {
  display: grid;
  border-radius: 24px;
  overflow: hidden;
  background: #fff;
  border: 1px solid rgba(17, 17, 17, 0.05);
  box-shadow: 0 20px 44px -38px rgba(17, 17, 17, 0.26);
}

.sale-image {
  display: block;
  background: linear-gradient(180deg, #f7f8f2 0%, #edf5e3 100%);
}

.sale-image img {
  display: block;
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
}

.sale-copy {
  display: grid;
  gap: 14px;
  padding: 18px 18px 20px;
}

.sale-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.sale-pill,
.sale-tag,
.section-chip {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.sale-pill {
  background: #111;
  color: #fff;
}

.sale-tag {
  background: #eef9df;
  color: #5b9f17;
}

.sale-title {
  color: #111;
  text-decoration: none;
  font-size: 22px;
  line-height: 1.1;
  font-weight: 700;
}

.sale-specs {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.65;
}

.sale-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}

.sale-price strong {
  font-size: 24px;
}

.sale-price span {
  color: #969696;
  text-decoration: line-through;
}

.membership-section {
  padding: 30px 0 34px;
}

.membership-card {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
  gap: 24px;
  padding: 34px;
  border-radius: 28px;
  background:
    radial-gradient(circle at right top, rgba(255, 255, 255, 0.16), transparent 30%),
    linear-gradient(120deg, #75d431 0%, #5aa810 100%);
  color: #fff;
  box-shadow: 0 26px 60px -44px rgba(85, 145, 18, 0.65);
}

.section-chip {
  width: fit-content;
  margin-bottom: 14px;
  background: rgba(255, 255, 255, 0.16);
  color: #fff;
}

.membership-card h2 {
  margin: 0;
  max-width: 620px;
  font-size: clamp(32px, 4vw, 48px);
  line-height: 1;
  letter-spacing: -0.05em;
}

.membership-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.membership-stats div {
  display: grid;
  gap: 8px;
  align-content: end;
  min-height: 168px;
  padding: 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.14);
}

.membership-stats strong {
  font-size: 34px;
  line-height: 1;
}

.membership-stats span {
  font-size: 14px;
  line-height: 1.5;
}

.bundle-section {
  padding: 26px 0 0;
}

.bundle-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.bundle-card {
  padding: 28px;
  border-radius: 24px;
  background: #fff;
  border: 1px solid rgba(17, 17, 17, 0.06);
  box-shadow: 0 20px 44px -40px rgba(17, 17, 17, 0.22);
}

.bundle-card-title {
  margin: 0 0 10px;
  color: #75d431;
  font-size: 24px;
  line-height: 1.05;
  font-weight: 800;
}

.bundle-card p:last-child {
  margin: 0;
  color: #606060;
  line-height: 1.7;
}

:global(body.spring-sale-header-hidden .app-header) {
  opacity: 0;
  transform: translateY(calc(-100% - 16px));
  pointer-events: none;
  transition:
    opacity 0.24s ease,
    transform 0.24s ease;
}

@media (max-width: 1200px) {
  .feature-ride {
    min-height: 520px;
  }

  .feature-ride-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .feature-ride-actions {
    justify-content: flex-start;
  }

  .product-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .membership-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 960px) {
  .sale-subnav-shell {
    min-height: 98px;
  }

  .sale-subnav-inner {
    min-height: 98px;
    align-items: flex-start;
    justify-content: center;
    padding-top: 16px;
    padding-bottom: 16px;
  }

  .sale-subnav-links {
    justify-content: flex-start;
  }

  .featured-rides {
    flex-direction: column;
  }

  .feature-ride,
  .feature-ride.is-active,
  .feature-ride.is-inactive {
    flex: none;
    min-height: 0;
  }

  .feature-ride {
    padding: 22px 20px 20px;
  }

  .feature-ride-thumb {
    min-height: 276px;
  }

  .feature-ride.is-active .feature-ride-thumb {
    opacity: 1;
    transform: none;
    pointer-events: auto;
  }

  .feature-ride-expanded {
    position: relative;
    inset: auto;
    margin-top: 18px;
    opacity: 1;
    transform: none;
  }

  .feature-ride-expanded-thumb {
    position: relative;
    left: auto;
    bottom: auto;
  }

  .feature-ride-expanded-thumb img {
    width: min(100%, 220px);
  }

  .feature-ride-footer {
    min-height: 0;
    padding: 18px;
  }

  .feature-ride-copy {
    padding-left: 0;
  }

  .product-grid,
  .membership-stats,
  .bundle-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 680px) {
  .sale-subnav-shell {
    min-height: 122px;
  }

  .hero-content {
    padding-top: 88px;
    padding-bottom: 34px;
  }

  .section-head p {
    font-size: 18px;
  }

  .feature-ride {
    padding: 18px 16px 16px;
  }

  .feature-ride-thumb img {
    max-height: 210px;
  }

  .feature-ride-actions {
    flex-direction: column;
  }

  .feature-btn {
    width: 100%;
  }

  .product-grid,
  .membership-stats,
  .bundle-grid {
    grid-template-columns: 1fr;
  }

  .membership-card {
    padding: 24px 20px;
  }
}
</style>
