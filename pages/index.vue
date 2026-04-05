<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-carousel">
        <div
          v-for="(slide, index) in heroSlides"
          :key="slide.slug"
          class="hero-slide"
          :class="{ 'is-active': currentHeroIndex === index }"
        >
          <div class="hero-bg" :style="{ backgroundImage: `url(${slide.image})` }" />
        </div>
        <div class="hero-indicators">
          <span
            v-for="(_, index) in heroSlides"
            :key="index"
            class="indicator-dot"
            :class="{ 'is-active': currentHeroIndex === index }"
            @click="currentHeroIndex = index"
          />
        </div>
        <div class="hero-overlay" />
      </div>

      <div class="container hero-content">
        <div class="text-content">
          <h1 class="title">
            {{ copy.heroLine1 }}<br />
            <span class="highlight">{{ copy.heroLine2 }}</span>
          </h1>
          <p class="subtitle">{{ t('homeHeroDesc') }}</p>
          <div class="action-group">
            <NuxtLink to="/collections/electric-scooters" class="btn-main">{{ t('shopNow') }}</NuxtLink>
            <NuxtLink to="/collections/electric-bike" class="btn-secondary">{{ t('exploreCatalog') }}</NuxtLink>
          </div>
        </div>
      </div>
    </section>

    <section class="explore-section container">
      <h2 class="section-title explore-title">Explore <i>isinwheel</i></h2>
      <div class="category-grid">
        <NuxtLink
          v-for="category in categories"
          :key="category.slug"
          :to="`/collections/${category.slug}`"
          class="category-card"
        >
          <div class="bg-image" :style="{ backgroundImage: `url(${category.menuImage || category.heroImage})` }" />
          <div class="content">
            <h3>{{ category.name }} <span class="count">{{ category.productCount }}</span></h3>
            <p>{{ category.description }}</p>
            <ArrowRightIcon class="icon" />
          </div>
        </NuxtLink>
      </div>
    </section>

    <section class="best-sellers-section container">
      <div class="section-header">
        <h2 class="section-title">Best Sellers</h2>
        <NuxtLink :to="activeCategoryLink" class="view-all-link">
          {{ copy.viewAll }} <ArrowRightIcon class="icon-right" />
        </NuxtLink>
      </div>

      <div class="tabs-wrapper">
        <div class="tabs">
          <button
            v-for="tab in tabs"
            :key="tab.slug"
            class="tab-btn"
            :class="{ active: currentTab === tab.slug }"
            @click="currentTab = tab.slug"
          >
            {{ tab.name }}
          </button>
        </div>
      </div>

      <div class="product-grid">
        <ProductCard v-for="product in filteredProducts" :key="product.id" :product="product" />
      </div>
    </section>

    <section class="video-section container">
      <div class="feature-panel">
        <div class="feature-copy">
          <p class="eyebrow">isinwheel</p>
          <h2>{{ copy.featureTitle }}</h2>
          <p>{{ copy.featureDesc }}</p>
        </div>
        <div class="feature-media" :style="{ backgroundImage: `url(${featureImage})` }" />
      </div>
    </section>

    <section class="blog-section container">
      <div class="section-header">
        <h2 class="section-title">isinwheel Blog</h2>
      </div>
      <div class="blog-grid">
        <article v-for="card in blogCards" :key="card.title" class="blog-card">
          <div class="blog-image" :style="{ backgroundImage: `url(${card.image})` }" />
          <div class="blog-content">
            <p class="blog-date">{{ card.date }}</p>
            <h3>{{ card.title }}</h3>
            <p>{{ card.desc }}</p>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ArrowRightIcon } from 'lucide-vue-next'

const { lang, t } = useShopLocale()

const categories = ref<any[]>([])
const products = ref<any[]>([])
const currentHeroIndex = ref(0)
const currentTab = ref('electric-scooters')
let heroTimer: ReturnType<typeof window.setInterval> | null = null

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        heroLine1: '探索无界',
        heroLine2: '智能骑行',
        viewAll: '查看当前分类',
        featureTitle: '真实商品数据已回接到旧版首页体验',
        featureDesc: '现在首页分类、商品卡片和列表跳转都来自后端接口，视觉回到原来的展示方式。',
      }
    : {
        heroLine1: 'Explore',
        heroLine2: 'Smart Riding',
        viewAll: 'View current collection',
        featureTitle: 'Real catalog data is back inside the original storefront feel',
        featureDesc: 'Collections, product cards, and routing on this page are now driven by backend APIs again.',
      },
)

const tabs = computed(() => categories.value.map((item) => ({ slug: item.slug, name: item.name })))
const activeCategoryLink = computed(() => `/collections/${currentTab.value}`)

const heroSlides = computed(() =>
  (categories.value.length ? categories.value : [{ slug: 'fallback', heroImage: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1920' }]).map(
    (category) => ({
      slug: category.slug,
      image: category.heroImage || category.menuImage,
    }),
  ),
)

const filteredProducts = computed(() => {
  const matched = products.value.filter((product) => product.categorySlug === currentTab.value)
  return matched.slice(0, 4)
})

const featureImage = computed(() => heroSlides.value[currentHeroIndex.value]?.image || heroSlides.value[0]?.image)

const blogCards = computed(() => [
  {
    title: lang.value === 'zh' ? '如何根据通勤距离选择电动滑板车' : 'How to pick a scooter for your commute distance',
    date: 'Apr 5, 2026',
    desc:
      lang.value === 'zh'
        ? '基于当前商城数据结构，我们已经支持按分类、SKU 和价格快速浏览。'
        : 'With the current storefront data model, browsing by collection, SKU, and price is now wired end to end.',
    image: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800',
  },
  {
    title: lang.value === 'zh' ? '电动自行车与滑板的使用场景差异' : 'When to choose an ebike over an eskateboard',
    date: 'Apr 2, 2026',
    desc:
      lang.value === 'zh'
        ? 'Electric Bike 与 Electric Skateboard 两个类目已经同步接入真实接口。'
        : 'Electric Bike and Electric Skateboard collections are now backed by real storefront APIs.',
    image: 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=800',
  },
  {
    title: lang.value === 'zh' ? '优惠券与订单预览是如何协同工作的' : 'How coupons and order preview now work together',
    date: 'Mar 28, 2026',
    desc:
      lang.value === 'zh'
        ? '优惠券应用发生在订单预览阶段，和当前结算流程保持一致。'
        : 'Coupons are validated during order preview, matching the new backend checkout flow.',
    image: 'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=800',
  },
])

const fetchHomeData = async () => {
  try {
    const [categoryRes, productRes] = await Promise.all([
      useHttp('/api/category/tree'),
      useHttp('/api/product/list?pageNum=1&pageSize=16'),
    ])
    categories.value = categoryRes?.code === 200 ? categoryRes.data || [] : []
    products.value = productRes?.code === 200 ? productRes.data?.records || [] : []
    if (categories.value.length && !currentTab.value) {
      currentTab.value = categories.value[0].slug
    }
    if (categories.value.length && !categories.value.some((item) => item.slug === currentTab.value)) {
      currentTab.value = categories.value[0].slug
    }
  } catch (error) {
    categories.value = []
    products.value = []
  }
}

const startHeroTimer = () => {
  if (heroTimer) clearInterval(heroTimer)
  heroTimer = window.setInterval(() => {
    if (!heroSlides.value.length) return
    currentHeroIndex.value = (currentHeroIndex.value + 1) % heroSlides.value.length
  }, 5000)
}

onMounted(async () => {
  await fetchHomeData()
  if (process.client) {
    startHeroTimer()
  }
})

onUnmounted(() => {
  if (heroTimer) clearInterval(heroTimer)
})
</script>

<style scoped lang="scss">
.home-page { padding-bottom: 70px; }
.hero-section { position: relative; min-height: 80vh; display: flex; align-items: center; overflow: hidden; margin-bottom: 60px; }
.hero-carousel { position: absolute; inset: 0; z-index: 1; }
.hero-slide { position: absolute; inset: 0; opacity: 0; transition: opacity .5s ease; }
.hero-slide.is-active { opacity: 1; }
.hero-bg { width: 100%; height: 100%; background-size: cover; background-position: center; }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(90deg, rgba(0,0,0,.55), rgba(0,0,0,.2)); }
.hero-indicators { position: absolute; left: 50%; bottom: 28px; transform: translateX(-50%); display: flex; gap: 10px; z-index: 3; }
.indicator-dot { width: 10px; height: 10px; border-radius: 50%; background: rgba(255,255,255,.4); cursor: pointer; }
.indicator-dot.is-active { background: #fff; }
.hero-content { position: relative; z-index: 2; }
.text-content { max-width: 560px; color: #fff; }
.title { font-size: clamp(48px, 6vw, 86px); line-height: 1; margin: 0 0 18px; font-weight: 800; }
.highlight { color: #58cc02; }
.subtitle { max-width: 48ch; line-height: 1.8; color: rgba(255,255,255,.86); }
.action-group { display: flex; gap: 16px; margin-top: 30px; flex-wrap: wrap; }
.btn-main,.btn-secondary { display: inline-flex; align-items: center; justify-content: center; min-height: 54px; padding: 0 24px; border-radius: 999px; text-decoration: none; font-weight: 700; }
.btn-main { background: #111; color: #fff; }
.btn-secondary { background: rgba(255,255,255,.14); border: 1px solid rgba(255,255,255,.25); color: #fff; }
.section-title { font-size: 40px; font-weight: 800; margin: 0 0 30px; }
.category-grid { display: grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap: 20px; }
.category-card { position: relative; border-radius: 18px; overflow: hidden; min-height: 280px; text-decoration: none; color: #fff; }
.bg-image { position: absolute; inset: 0; background-size: cover; background-position: center; transition: transform .4s ease; }
.category-card:hover .bg-image { transform: scale(1.06); }
.category-card .content { position: relative; z-index: 2; min-height: 280px; display: flex; flex-direction: column; justify-content: flex-end; padding: 24px; background: linear-gradient(180deg, transparent, rgba(0,0,0,.78)); }
.category-card h3 { font-size: 24px; margin: 0 0 8px; }
.category-card .count { color: #8ad5d0; }
.category-card p { margin: 0 0 12px; line-height: 1.6; color: rgba(255,255,255,.86); }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; gap: 16px; }
.view-all-link { display: inline-flex; align-items: center; gap: 8px; color: #111; text-decoration: none; font-weight: 600; }
.tabs-wrapper { margin-bottom: 30px; }
.tabs { display: inline-flex; gap: 20px; padding: 4px; }
.tab-btn { padding: 12px 28px; border-radius: 36px; font-weight: 600; font-size: 16px; background: #f5f5f5; color: #667085; border: none; cursor: pointer; }
.tab-btn.active,.tab-btn:hover { background: #111; color: #fff; }
.product-grid { display: grid; gap: 24px; grid-template-columns: repeat(4, 1fr); }
.feature-panel { display: grid; grid-template-columns: minmax(0,1fr) minmax(360px,.9fr); gap: 24px; background: #111; border-radius: 24px; overflow: hidden; color: #fff; }
.feature-copy { padding: 40px; }
.feature-copy .eyebrow { margin: 0 0 14px; text-transform: uppercase; letter-spacing: .08em; color: #8ad5d0; font-size: 12px; }
.feature-copy h2 { font-size: clamp(32px,4vw,54px); line-height: 1.08; margin: 0 0 16px; }
.feature-copy p { line-height: 1.8; color: rgba(255,255,255,.82); }
.feature-media { min-height: 320px; background-size: cover; background-position: center; }
.blog-section { margin-top: 60px; }
.blog-grid { display: grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap: 24px; }
.blog-card { border-radius: 20px; overflow: hidden; background: #fff; border: 1px solid #eceff3; box-shadow: 0 8px 18px rgba(0,0,0,.04); }
.blog-image { height: 220px; background-size: cover; background-position: center; }
.blog-content { padding: 22px; }
.blog-date { font-size: 12px; color: #667085; text-transform: uppercase; letter-spacing: .08em; }
.blog-content h3 { margin: 10px 0; font-size: 22px; line-height: 1.35; color: #111827; }
.blog-content p { margin: 0; color: #475467; line-height: 1.7; }
@media (max-width: 1100px) { .category-grid,.product-grid,.blog-grid { grid-template-columns: repeat(2, minmax(0,1fr)); } .feature-panel { grid-template-columns: 1fr; } }
@media (max-width: 720px) { .hero-section { min-height: 70vh; } .category-grid,.product-grid,.blog-grid { grid-template-columns: 1fr; } .section-header { flex-direction: column; align-items: flex-start; } }
</style>
