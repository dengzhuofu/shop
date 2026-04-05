<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-carousel">
        <div v-for="(slide, index) in heroSlides" :key="slide.slug" class="hero-slide" :class="{ 'is-active': currentHeroIndex === index }">
          <div class="hero-bg" :style="{ backgroundImage: `url(${slide.image})` }" />
        </div>
        <div class="hero-indicators">
          <span v-for="(_, index) in heroSlides" :key="index" class="indicator-dot" :class="{ 'is-active': currentHeroIndex === index }" @click="currentHeroIndex = index" />
        </div>
        <div class="hero-overlay" />
      </div>

      <div class="container hero-content">
        <div class="text-content">
          <h1 class="title">{{ copy.heroLine1 }}<br /><span class="highlight">{{ copy.heroLine2 }}</span></h1>
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
        <NuxtLink v-for="category in categoryCards" :key="category.slug" :to="`/collections/${category.slug}`" class="category-card">
          <div class="bg-placeholder" :style="{ backgroundImage: `url(${category.image})` }" />
          <div class="content">
            <h3>{{ category.name }} <span class="count">{{ category.productCount }}</span></h3>
            <p>{{ category.description }}</p>
            <ArrowRightIcon class="icon" />
          </div>
        </NuxtLink>
      </div>
    </section>

    <section class="best-sellers-section container">
      <div class="section-header flex-between align-center mb-xl">
        <h2 class="section-title mb-0 best-sellers-title">
          {{ copy.bestSellers }}
          <svg class="wave-underline" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20" preserveAspectRatio="none">
            <path d="M0,10 Q12.5,20 25,10 T50,10 T75,10 T100,10" fill="none" stroke="#58cc02" stroke-width="4" stroke-linecap="round" />
          </svg>
        </h2>
        <NuxtLink :to="activeCategoryLink" class="view-all-link">{{ copy.viewAllCurrent }} <ArrowRightIcon class="icon-right" /></NuxtLink>
      </div>
      <div class="tabs-wrapper">
        <div class="tabs">
          <button v-for="tab in tabs" :key="tab.slug" type="button" :class="['tab-btn', { active: currentTab === tab.slug }]" @click="currentTab = tab.slug">{{ tab.name }}</button>
        </div>
      </div>
      <div class="product-grid">
        <ProductCard v-for="product in featuredProducts" :key="product.id" :product="product" />
      </div>
    </section>

    <section class="video-section container">
      <div class="video-container" @click="toggleVideo">
        <video ref="videoRef" class="promo-video" src="https://www.w3schools.com/html/mov_bbb.mp4" loop muted playsinline />
        <div class="video-cover" :class="{ 'is-hidden': isPlaying }"><img :src="featureImage" :alt="copy.videoAlt" /></div>
        <div class="video-controls" :class="{ 'is-playing': isPlaying }">
          <button type="button" class="play-pause-btn">
            <PlayIcon v-if="!isPlaying" class="icon play-icon" />
            <PauseIcon v-else class="icon pause-icon" />
          </button>
        </div>
      </div>
    </section>

    <section class="influencer-section container">
      <div class="influencer-grid">
        <div v-for="video in influencerVideos" :key="video.id" class="video-card">
          <div class="bg-image" :style="{ backgroundImage: `url(${video.bgImage})` }" />
          <div class="overlay" />
          <div class="play-btn"><PlayIcon class="icon" /></div>
          <div class="card-content">
            <div class="user-info"><img :src="video.avatar" alt="Avatar" class="avatar" /><span class="username">{{ video.username }}</span></div>
            <p class="quote">"{{ video.quote }}"</p>
            <div class="tags"><span v-for="tag in video.tags" :key="tag" class="tag">{{ tag }}</span></div>
          </div>
        </div>
      </div>
    </section>

    <section class="media-review-section">
      <div class="media-bg"><div class="overlay" /></div>
      <div class="container media-content">
        <div class="quote-icon">“</div>
        <h2 class="review-title">{{ copy.mediaReviewTitle }}</h2>
        <div class="media-logo"><span class="logo-circle">CNET</span><span class="logo-text">— CNET</span></div>
        <div class="pagination-dots"><span class="dot active" /><span v-for="dot in 6" :key="dot" class="dot" /></div>
      </div>
    </section>

    <section class="why-choose-section container">
      <div class="content-wrapper">
        <div class="image-gallery"><div class="img-large" /><div class="img-small" /></div>
        <div class="text-content">
          <h2 class="section-title">{{ copy.whyChooseTitle }}</h2>
          <p class="description">{{ copy.whyChooseDesc }}</p>
          <NuxtLink to="/collections/electric-scooters" class="btn-brand-story">{{ copy.brandStory }} <ArrowRightIcon class="icon-right" /></NuxtLink>
        </div>
      </div>
    </section>

    <section class="customer-reviews-section container">
      <div class="section-header text-center">
        <h2 class="section-title">{{ copy.customerReviewsTitle }}</h2>
        <p class="subtitle">{{ copy.customerReviewsSubtitle }}</p>
      </div>
      <div class="reviews-carousel">
        <div class="nav-btn prev"><ChevronLeftIcon /></div>
        <div class="reviews-grid">
          <div v-for="review in customerReviews" :key="review.id" class="review-card">
            <div class="review-img" :style="{ backgroundImage: `url(${review.image})` }" />
            <div class="review-content">
              <div class="stars"><StarIcon v-for="idx in 5" :key="idx" class="star-icon filled" /></div>
              <h4 class="reviewer-name">{{ review.name }} <span v-if="review.verified" class="verified-badge">{{ copy.verified }}</span></h4>
              <p class="review-text">{{ review.text }}</p>
            </div>
          </div>
        </div>
        <div class="nav-btn next"><ChevronRightIcon /></div>
      </div>
    </section>

    <section class="blog-section container">
      <div class="section-header flex-between">
        <h2 class="section-title blog-title-main">
          {{ copy.blogTitle }}
          <svg class="wave-underline" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20" preserveAspectRatio="none">
            <path d="M0,10 Q12.5,20 25,10 T50,10 T75,10 T100,10" fill="none" stroke="#58cc02" stroke-width="4" stroke-linecap="round" />
          </svg>
        </h2>
        <NuxtLink to="/collections/electric-scooters" class="btn-view-all"><FileTextIcon class="icon-left" /> {{ copy.viewAll }}</NuxtLink>
      </div>
      <div class="blog-grid">
        <div class="blog-main">
          <div class="blog-card large">
            <div class="bg-img" :style="{ backgroundImage: `url(${blogCards[0].image})` }" />
            <div class="overlay" />
            <div class="blog-content">
              <div class="meta"><span class="date"><CalendarIcon class="meta-icon" /> {{ blogCards[0].date }}</span><span class="comments"><MessageCircleIcon class="meta-icon" /> {{ blogCards[0].comments }}</span></div>
              <h3 class="blog-title">{{ blogCards[0].title }}</h3>
              <a href="#" class="read-more">{{ copy.readMore }}</a>
            </div>
          </div>
        </div>
        <div class="blog-side">
          <div v-for="blog in blogCards.slice(1)" :key="blog.id" class="blog-card small">
            <div class="img-wrapper"><img :src="blog.image" :alt="blog.title" /></div>
            <div class="blog-content">
              <div class="meta"><span class="date"><CalendarIcon class="meta-icon" /> {{ blog.date }}</span><span class="comments"><MessageCircleIcon class="meta-icon" /> {{ blog.comments }}</span></div>
              <h3 class="blog-title">{{ blog.title }}</h3>
              <a href="#" class="read-more">{{ copy.readMore }}</a>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watchEffect } from 'vue'
import { ArrowRightIcon, CalendarIcon, ChevronLeftIcon, ChevronRightIcon, FileTextIcon, MessageCircleIcon, PauseIcon, PlayIcon, StarIcon } from 'lucide-vue-next'

const { lang, t } = useShopLocale()
const categories = ref<any[]>([])
const products = ref<any[]>([])
const currentHeroIndex = ref(0)
const currentTab = ref('electric-scooters')
const isPlaying = ref(false)
const videoRef = ref<HTMLVideoElement | null>(null)
let heroTimer: ReturnType<typeof window.setInterval> | null = null

const categoryVisuals = {
  'electric-scooters': { image: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1400', description: { zh: '轻松通勤，城市与周末骑行都能兼顾。', en: 'Foldable freedom for your daily commute and weekend rides.' } },
  'electric-bike': { image: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1400', description: { zh: '长距离与复杂路况都能更轻松拿下。', en: 'Conquer hills and long-distance routes with confidence.' } },
  'electric-skateboard': { image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=1400', description: { zh: '顺滑 carving 和速度感兼备的轻快出行。', en: 'Electrify every carve with smooth control and speed.' } },
  accessories: { image: 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1400', description: { zh: '升级、保护和个性化你的每一段骑行。', en: 'Upgrade, protect, and personalize every ride.' } },
} as const

const copy = computed(() => lang.value === 'zh'
  ? { heroLine1: '探索无界', heroLine2: '智能骑行', bestSellers: '热卖产品', viewAllCurrent: '查看当前分类', mediaReviewTitle: 'isinwheel S10Max 评测：兼顾便携与性能的最后一公里电动滑板车', whyChooseTitle: '为什么选择 isinwheel', whyChooseDesc: '我们希望把值得信赖的电动出行产品带到更多真实场景里，让通勤、短途出游和日常代步都更轻松、更有趣，也更接近你真正想去的地方。', brandStory: '品牌故事', customerReviewsTitle: '大家如何评价 isinwheel', customerReviewsSubtitle: '来自 5870 条真实评价', verified: '已验证', blogTitle: 'isinwheel 博客', viewAll: '查看全部', readMore: '阅读更多', videoAlt: 'isinwheel 首页视频封面' }
  : { heroLine1: 'Explore Beyond', heroLine2: 'Smart Riding', bestSellers: 'Best Sellers', viewAllCurrent: 'View current collection', mediaReviewTitle: 'isinwheel S10Max Review: A Powerful Yet Portable Last-Mile Scooter', whyChooseTitle: 'Why Choose isinwheel', whyChooseDesc: 'We build electric rides that make commuting, short trips, and everyday errands feel easier, more affordable, and a lot more fun.', brandStory: 'Brand Story', customerReviewsTitle: 'What The People Say About Isinwheel', customerReviewsSubtitle: 'from 5870 reviews', verified: 'Verified', blogTitle: 'isinwheel Blog', viewAll: 'View all', readMore: 'Read more', videoAlt: 'isinwheel homepage video cover' })

const fallbackCategories = computed(() => [
  { slug: 'electric-scooters', name: t('electricScooters'), productCount: 0 },
  { slug: 'electric-bike', name: t('electricBike'), productCount: 0 },
  { slug: 'electric-skateboard', name: t('electricSkateboard'), productCount: 0 },
  { slug: 'accessories', name: t('accessories'), productCount: 0 },
])

const displayCategories = computed(() => categories.value.length ? categories.value : fallbackCategories.value)
const categoryCards = computed(() => displayCategories.value.map((category: any) => {
  const visual = categoryVisuals[category.slug as keyof typeof categoryVisuals] || categoryVisuals['electric-scooters']
  return { ...category, image: category.menuImage || category.heroImage || visual.image, description: category.description || visual.description[lang.value === 'zh' ? 'zh' : 'en'] }
}))
const tabs = computed(() => displayCategories.value.map((item: any) => ({ slug: item.slug, name: item.name })))
const heroSlides = computed(() => categoryCards.value.map((category) => ({ slug: category.slug, image: category.heroImage || category.menuImage || category.image })))
const activeCategoryLink = computed(() => `/collections/${currentTab.value}`)

const resolveCategorySlug = (product: any) => {
  const direct = product.categorySlug || product.category?.slug || product.category?.categorySlug
  if (direct) return direct
  const source = `${product.categoryName || ''} ${product.title || ''}`.toLowerCase()
  if (source.includes('skateboard')) return 'electric-skateboard'
  if (source.includes('bike')) return 'electric-bike'
  if (source.includes('accessor')) return 'accessories'
  return 'electric-scooters'
}

const featuredProducts = computed(() => {
  const matched = products.value.filter((product) => resolveCategorySlug(product) === currentTab.value)
  return (matched.length ? matched : products.value).slice(0, 4)
})

const featureImage = computed(() => heroSlides.value[currentHeroIndex.value]?.image || heroSlides.value[0]?.image || categoryVisuals['electric-scooters'].image)

const influencerVideos = computed(() => lang.value === 'zh'
  ? [
      { id: 1, bgImage: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=11', username: '@ride_master', quote: '目前最喜欢的一台通勤滑板车，起步和刹车都很顺。', tags: ['#电动滑板车', '#城市通勤'] },
      { id: 2, bgImage: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=12', username: '@urban_explorer', quote: '每天上下班都在骑，续航和稳定性都很放心。', tags: ['#电动自行车', '#长续航'] },
      { id: 3, bgImage: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=13', username: '@skate_pro', quote: '板子的响应很干脆，速度上来之后也依然稳。', tags: ['#电动滑板', '#骑行乐趣'] },
      { id: 4, bgImage: 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=14', username: '@eco_traveler', quote: '短途出行几乎都被它替代了，轻松又环保。', tags: ['#绿色出行', '#周末骑行'] },
    ]
  : [
      { id: 1, bgImage: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=11', username: '@ride_master', quote: 'My favorite commuter scooter right now. Smooth pickup and braking.', tags: ['#escooter', '#commute'] },
      { id: 2, bgImage: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=12', username: '@urban_explorer', quote: 'I ride it to work every day. Range and stability both feel reliable.', tags: ['#ebike', '#range'] },
      { id: 3, bgImage: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=13', username: '@skate_pro', quote: 'Responsive underfoot and still stable once you really pick up speed.', tags: ['#eskate', '#ride'] },
      { id: 4, bgImage: 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=400&h=700', avatar: 'https://i.pravatar.cc/150?u=14', username: '@eco_traveler', quote: 'It has replaced most of my short trips. Easy, fun, and greener.', tags: ['#eco', '#weekend'] },
    ])

const customerReviews = computed(() => lang.value === 'zh'
  ? [
      { id: 1, image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400', name: 'Andrew', verified: false, text: '一开始我觉得减震偏硬，调了一下之后明显更顺了。现在通勤每天都在骑，整体很满意。' },
      { id: 2, image: 'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=400', name: 'Joseph P.', verified: true, text: '参数和描述基本一致，动力输出很直接，整车做工也比我预期更扎实。' },
      { id: 3, image: 'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=400', name: 'Pamela', verified: false, text: '安装很快，半小时内就能搞定。第一次上路就能感受到它的稳定和易上手。' },
      { id: 4, image: 'https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=400', name: 'Chad S.', verified: false, text: '如果你想找一台速度和续航比较均衡的车，这台确实很有竞争力。' },
    ]
  : [
      { id: 1, image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400', name: 'Andrew', verified: false, text: 'The suspension felt stiff at first, but once adjusted it became a very smooth daily commuter.' },
      { id: 2, image: 'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=400', name: 'Joseph P.', verified: true, text: 'Specs line up well with the description. The power delivery is direct and the build feels solid.' },
      { id: 3, image: 'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=400', name: 'Pamela', verified: false, text: 'Assembly was quick and the first ride already felt stable and easy to get comfortable with.' },
      { id: 4, image: 'https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=400', name: 'Chad S.', verified: false, text: 'If you want a ride with a good balance of speed and range, this one is genuinely competitive.' },
    ])

const blogCards = computed(() => lang.value === 'zh'
  ? [
      { id: 1, image: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800', title: '越野电动滑板车的悬挂到底该怎么选', date: '2026-03-26', comments: '0 条评论' },
      { id: 2, image: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400', title: '预算有限时，如何挑一台适合日常通勤的电动滑板车', date: '2026-03-17', comments: '0 条评论' },
      { id: 3, image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400', title: '家庭短途代步，500 美元以内有哪些更合适的选择', date: '2026-03-11', comments: '0 条评论' },
    ]
  : [
      { id: 1, image: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800', title: 'Best Suspension for Value Off-Road Scooters: What Matters on Rough Paths', date: 'Mar 26, 2026', comments: '0 comments' },
      { id: 2, image: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400', title: 'How to Pick a Value-Packed Electric Scooter for Daily Commuting', date: 'Mar 17, 2026', comments: '0 comments' },
      { id: 3, image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400', title: 'Best Under-$500 Options for Easy Family Neighborhood Errands', date: 'Mar 11, 2026', comments: '0 comments' },
    ])

const { data: homeData } = await useAsyncData(
  'home-page-data',
  async () => {
    try {
      const [categoryRes, productRes] = await Promise.all([
        useHttp('/api/category/tree'),
        useHttp(`/api/product/list?pageNum=1&pageSize=24&lang=${lang.value}`),
      ])
      return {
        categories: categoryRes?.code === 200 ? categoryRes.data || [] : [],
        products: productRes?.code === 200 ? productRes.data?.records || [] : [],
      }
    } catch (error) {
      return {
        categories: [],
        products: [],
      }
    }
  },
  {
    watch: [lang],
  },
)

watchEffect(() => {
  categories.value = homeData.value?.categories || []
  products.value = homeData.value?.products || []
  if (displayCategories.value.length && !displayCategories.value.some((item: any) => item.slug === currentTab.value)) {
    currentTab.value = displayCategories.value[0].slug
  }
})

const startHeroTimer = () => {
  if (heroTimer) window.clearInterval(heroTimer)
  heroTimer = window.setInterval(() => {
    if (!heroSlides.value.length) return
    currentHeroIndex.value = (currentHeroIndex.value + 1) % heroSlides.value.length
  }, 5000)
}

const stopHeroTimer = () => {
  if (!heroTimer) return
  window.clearInterval(heroTimer)
  heroTimer = null
}

const toggleVideo = async () => {
  if (!videoRef.value) return
  if (isPlaying.value) {
    videoRef.value.pause()
    isPlaying.value = false
    return
  }
  try {
    await videoRef.value.play()
    isPlaying.value = true
  } catch (error) {
    isPlaying.value = false
  }
}

onMounted(() => {
  startHeroTimer()
})

onUnmounted(() => {
  stopHeroTimer()
})
</script>

<style scoped lang="scss">
.home-page{padding-bottom:60px}
.section-title{font-size:40px;font-weight:800;margin-bottom:30px}
.hero-section{position:relative;min-height:85vh;display:flex;align-items:center;overflow:hidden;margin-bottom:$spacing-xl;padding:40px 0;background-color:$bg-light}
.hero-carousel,.hero-slide,.hero-bg,.hero-overlay,.media-bg,.media-bg .overlay{position:absolute;inset:0}
.hero-carousel{z-index:1}.hero-slide{opacity:0;transition:opacity .6s ease}.hero-slide.is-active{opacity:1}.hero-bg{background-size:cover;background-position:center}.hero-overlay{background:linear-gradient(to right,rgba(0,0,0,.78) 0%,rgba(0,0,0,.22) 100%)}
.hero-indicators{position:absolute;left:50%;bottom:32px;z-index:3;display:flex;gap:10px;transform:translateX(-50%)}
.indicator-dot{width:10px;height:10px;border-radius:50%;background:rgba(255,255,255,.35);cursor:pointer;transition:all .25s ease}
.indicator-dot.is-active,.indicator-dot:hover,.pagination-dots .dot.active,.pagination-dots .dot:hover{background:#fff;transform:scale(1.1)}
.hero-content{position:relative;z-index:2;width:100%;color:$white}.text-content{max-width:620px}
.title{margin:0 0 18px;font-size:clamp(48px,6vw,84px);font-weight:800;line-height:1;letter-spacing:1px}.highlight{color:#58cc02}
.subtitle{max-width:48ch;margin:0;color:rgba(255,255,255,.9);font-size:18px;line-height:1.7}
.action-group{display:flex;flex-wrap:wrap;gap:16px;margin-top:32px}
.btn-main,.btn-secondary,.btn-brand-story,.btn-view-all{display:inline-flex;align-items:center;justify-content:center;gap:8px;border-radius:999px;font-weight:700;text-decoration:none;transition:all .25s ease}
.btn-main,.btn-secondary{min-height:54px;padding:0 28px}.btn-main{background:#111;color:#fff}.btn-main:hover,.btn-brand-story:hover{background:#222}.btn-secondary{border:1px solid rgba(255,255,255,.25);background:rgba(255,255,255,.14);color:#fff}.btn-secondary:hover{background:rgba(255,255,255,.2)}
.explore-section,.best-sellers-section,.video-section,.influencer-section,.why-choose-section,.customer-reviews-section,.blog-section{margin-top:80px}
.explore-title{margin-bottom:28px}
.category-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:20px}
.category-card{position:relative;min-height:320px;overflow:hidden;border-radius:18px;color:#fff;text-decoration:none}
.bg-placeholder{position:absolute;inset:0;background-position:center;background-size:cover;transition:transform .45s ease}.category-card:hover .bg-placeholder{transform:scale(1.05)}
.category-card .content{position:relative;z-index:2;display:flex;min-height:320px;flex-direction:column;justify-content:flex-end;padding:24px;background:linear-gradient(180deg,transparent 15%,rgba(0,0,0,.82) 100%)}
.category-card h3{margin:0 0 8px;font-size:26px}.category-card .count{color:#8ad5d0}.category-card p{margin:0 0 14px;color:rgba(255,255,255,.88);line-height:1.65}.category-card .icon{width:22px;height:22px}
.section-header{display:flex;justify-content:space-between;align-items:center;gap:16px;margin-bottom:40px}
.best-sellers-title,.blog-title-main{position:relative;display:inline-block;margin-bottom:0}.wave-underline{position:absolute;left:0;bottom:-12px;width:100%;height:12px}
.view-all-link{display:inline-flex;align-items:center;gap:8px;color:$text-color;font-size:16px;font-weight:600;text-decoration:none;transition:color .25s ease}
.view-all-link:hover,.btn-view-all:hover{color:#58cc02}.icon-right{width:16px;height:16px;transition:transform .25s ease}.view-all-link:hover .icon-right,.btn-brand-story:hover .icon-right{transform:translateX(4px)}
.tabs-wrapper{display:flex;justify-content:flex-start;margin-bottom:40px}.tabs{display:inline-flex;gap:20px;padding:4px}
.tab-btn{padding:12px 32px;border:none;border-radius:36px;background:$bg-light;color:$text-light;font-size:16px;font-weight:600;cursor:pointer;transition:all .25s ease}.tab-btn.active,.tab-btn:hover{background:$primary-color;color:$white;box-shadow:0 4px 12px rgba(0,0,0,.1)}
.product-grid{display:grid;grid-template-columns:repeat(1,minmax(0,1fr));gap:24px}
.video-container{position:relative;width:100%;aspect-ratio:16/9;overflow:hidden;border-radius:24px;background:#000;box-shadow:0 10px 30px rgba(0,0,0,.15);cursor:pointer}
.promo-video,.video-cover img{width:100%;height:100%;object-fit:cover}.video-cover{position:absolute;inset:0;z-index:1;transition:opacity .45s ease}.video-cover.is-hidden{opacity:0;pointer-events:none}
.video-controls{position:absolute;inset:0;z-index:2;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.3);transition:all .3s ease}.video-controls.is-playing{background:transparent;opacity:0}.video-controls.is-playing:hover{background:rgba(0,0,0,.1);opacity:1}
.play-pause-btn{width:80px;height:80px;display:flex;align-items:center;justify-content:center;border:2px solid rgba(255,255,255,.6);border-radius:50%;background:rgba(255,255,255,.2);color:#fff;backdrop-filter:blur(8px);cursor:pointer;transition:all .3s ease}.play-pause-btn:hover{transform:scale(1.08);background:rgba(255,255,255,.35)}.play-pause-btn .icon{width:32px;height:32px}.play-pause-btn .play-icon{margin-left:4px}
.influencer-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:20px}
.video-card{position:relative;width:100%;aspect-ratio:9/16;overflow:hidden;border-radius:16px;box-shadow:0 4px 15px rgba(0,0,0,.1);cursor:pointer;transition:transform .3s ease,box-shadow .3s ease}
.video-card:hover{transform:translateY(-8px);box-shadow:0 12px 24px rgba(0,0,0,.2)}.video-card:hover .bg-image,.blog-card.large:hover .bg-img,.blog-card.small:hover .img-wrapper img{transform:scale(1.05)}.video-card:hover .play-btn{transform:translate(-50%,-50%) scale(1.1);background:rgba(255,255,255,.3)}
.video-card .bg-image,.review-img,.blog-main .bg-img{position:absolute;inset:0;background-position:center;background-size:cover}.video-card .bg-image,.blog-main .bg-img,.img-wrapper img{transition:transform .5s ease}
.video-card .overlay{position:absolute;inset:0;background:linear-gradient(to top,rgba(0,0,0,.8) 0%,rgba(0,0,0,.2) 50%,transparent 100%)}
.play-btn{position:absolute;top:50%;left:50%;width:50px;height:50px;display:flex;align-items:center;justify-content:center;border-radius:50%;background:rgba(255,255,255,.2);color:$white;backdrop-filter:blur(4px);transform:translate(-50%,-50%);transition:all .3s ease}.play-btn .icon{width:24px;height:24px;margin-left:3px}
.card-content{position:absolute;left:0;bottom:0;width:100%;display:flex;flex-direction:column;gap:8px;padding:16px;color:$white}
.user-info{display:flex;align-items:center;gap:8px}.avatar{width:32px;height:32px;border:2px solid rgba(255,255,255,.8);border-radius:50%;object-fit:cover}.username{font-size:14px;font-weight:600;text-shadow:0 1px 2px rgba(0,0,0,.5)}
.quote{margin:0;font-size:13px;line-height:1.4;opacity:.9;display:-webkit-box;overflow:hidden;-webkit-line-clamp:2;-webkit-box-orient:vertical}.tags{display:flex;flex-wrap:wrap;gap:6px}.tag{color:$secondary-color;font-size:12px;font-weight:500}
.media-review-section{position:relative;min-height:400px;display:flex;align-items:center;justify-content:center;margin:80px 0;color:$white;text-align:center}.media-bg{z-index:1;background:url('https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1920') center/cover fixed}.media-bg .overlay{background:rgba(0,0,0,.7)}
.media-content{position:relative;z-index:2;max-width:800px;padding:60px 20px}.quote-icon{margin-bottom:20px;color:$secondary-color;font-size:80px;line-height:1;font-family:Georgia,serif}.review-title{margin-bottom:40px;font-size:28px;font-weight:700;line-height:1.4}
.media-logo{display:flex;flex-direction:column;align-items:center;gap:12px;margin-bottom:40px}.logo-circle{width:60px;height:60px;display:flex;align-items:center;justify-content:center;border:1px solid rgba(255,255,255,.3);border-radius:50%;background:rgba(255,255,255,.1);color:$danger-color;font-size:16px;font-weight:700}.logo-text{color:rgba(255,255,255,.8);font-size:16px}
.pagination-dots{display:flex;justify-content:center;gap:8px}.pagination-dots .dot{width:8px;height:8px;border-radius:50%;background:rgba(255,255,255,.3);transition:all .25s ease}
.why-choose-section,.customer-reviews-section,.blog-section{max-width:$max-width}.content-wrapper{display:flex;flex-direction:column;align-items:center;gap:20px}
.image-gallery{position:relative;width:100%;min-height:500px;flex:1}.img-large,.img-small{position:absolute;background-position:center;background-size:cover;transition:transform .5s ease}
.img-large{top:5%;right:15%;width:65%;height:80%;z-index:1;border-radius:16px;background-image:url('https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800');box-shadow:0 20px 40px rgba(0,0,0,.15);transform:rotate(6deg)}.img-small{left:10%;bottom:15%;width:50%;height:55%;z-index:2;border:6px solid $white;border-radius:12px;background-image:url('https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=600');box-shadow:0 10px 30px rgba(0,0,0,.15);transform:rotate(-4deg)}
.img-large:hover{transform:rotate(2deg) scale(1.02)}.img-small:hover{transform:rotate(-1deg) scale(1.05)}
.why-choose-section .text-content{flex:1}.why-choose-section .section-title{color:#58cc02;font-size:36px;line-height:1.2}.description{margin-bottom:32px;color:$text-light;font-size:16px;line-height:1.8}
.btn-brand-story{padding:12px 32px;background:#111;color:$white}.btn-brand-story .icon-right,.btn-view-all .icon-left{width:16px;height:16px}.customer-reviews-section .section-header{margin-bottom:40px}.customer-reviews-section .section-title{margin-bottom:8px;font-size:32px}.customer-reviews-section .subtitle{margin:0;color:$text-light;font-size:16px}
.reviews-carousel{position:relative;display:flex;align-items:center;gap:20px}.nav-btn{width:48px;height:48px;display:flex;align-items:center;justify-content:center;border-radius:50%;background:$white;color:$secondary-color;box-shadow:0 4px 12px rgba(0,0,0,.1);cursor:pointer;transition:all .25s ease;z-index:2}.nav-btn:hover{background:$secondary-color;color:$white}
.reviews-grid{display:flex;flex:1;gap:24px;overflow-x:auto;padding:20px 0;scroll-snap-type:x mandatory;scrollbar-width:none}.reviews-grid::-webkit-scrollbar{display:none}.review-card{flex:0 0 100%;overflow:hidden;border:1px solid $border-color;border-radius:16px;background:$white;box-shadow:0 4px 20px rgba(0,0,0,.08);scroll-snap-align:start}.review-img{position:relative;height:200px}.review-content{display:flex;flex-direction:column;gap:12px;padding:24px}
.stars{display:flex;gap:4px;color:#ffc107}.star-icon{width:16px;height:16px;fill:currentColor}.reviewer-name{display:flex;align-items:center;gap:8px;margin:0;font-size:16px;font-weight:700}.verified-badge{padding:2px 6px;border-radius:4px;background:#000;color:#fff;font-size:10px;font-weight:500}.review-text{margin:0;color:$text-light;font-size:14px;line-height:1.6;display:-webkit-box;overflow:hidden;-webkit-line-clamp:4;-webkit-box-orient:vertical}
.blog-section{margin-bottom:80px}.btn-view-all{padding:12px 24px;border:1px solid $border-color;background:#fff;color:$text-color}
.blog-grid{display:grid;gap:24px}.blog-card{position:relative;overflow:hidden;border-radius:20px;cursor:pointer}.blog-card .meta{display:flex;align-items:center;gap:16px;margin-bottom:12px;font-size:12px}.meta-icon{width:14px;height:14px;margin-right:4px;vertical-align:middle}.read-more{display:inline-block;margin-top:16px;color:inherit;font-size:14px;font-weight:600;text-decoration:underline;text-underline-offset:4px}
.blog-card.large{height:400px}.blog-card.large .overlay{position:absolute;inset:0;background:linear-gradient(to top,rgba(0,0,0,.8) 0%,transparent 100%)}.blog-card.large .blog-content{position:absolute;left:0;bottom:0;width:100%;padding:40px;color:$white}.blog-card.large .blog-title{font-size:24px;font-weight:700;line-height:1.3}
.blog-side{display:flex;flex-direction:column;gap:24px}.blog-card.small{display:flex;flex-direction:column;height:100%;border:1px solid $border-color;background:$white;transition:box-shadow .3s ease,transform .3s ease}.blog-card.small:hover{transform:translateY(-4px);box-shadow:0 10px 20px rgba(0,0,0,.05)}
.img-wrapper{flex:0 0 200px;height:200px;overflow:hidden}.img-wrapper img{width:100%;height:100%;object-fit:cover}.blog-card.small .blog-content{flex:1;padding:24px}.blog-card.small .meta{color:$text-light}.blog-card.small .blog-title{color:$text-color;font-size:18px;font-weight:700;line-height:1.4;display:-webkit-box;overflow:hidden;-webkit-line-clamp:3;-webkit-box-orient:vertical}
@media (min-width:768px){.product-grid{grid-template-columns:repeat(2,minmax(0,1fr))}.review-card{flex:0 0 calc(50% - 12px)}}
@media (min-width:$bp-lg){.product-grid{grid-template-columns:repeat(3,minmax(0,1fr))}.influencer-grid{grid-template-columns:repeat(4,minmax(0,1fr))}.content-wrapper{flex-direction:row;gap:80px}.blog-grid{grid-template-columns:1.5fr 1fr}.blog-card.large{min-height:500px;height:100%}.blog-card.small{flex-direction:column}.review-card{flex:0 0 calc(25% - 18px)}}
@media (min-width:$bp-xl){.product-grid{grid-template-columns:repeat(4,minmax(0,1fr));gap:32px}.video-container{aspect-ratio:21/9}.blog-card.small{flex-direction:row}}
@media (max-width:1100px){.category-grid{grid-template-columns:repeat(2,minmax(0,1fr))}}
@media (max-width:$bp-lg){.nav-btn{display:none}}
@media (max-width:720px){.hero-section{min-height:75vh;padding:28px 0}.title{font-size:clamp(40px,13vw,64px)}.subtitle{font-size:16px}.category-grid,.product-grid,.influencer-grid{grid-template-columns:1fr}.section-header{align-items:flex-start;flex-direction:column}.tabs{flex-wrap:wrap}}
</style>
