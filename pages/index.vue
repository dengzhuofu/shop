<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-carousel">
        <div
          v-for="(slide, index) in heroSlides"
          :key="slide.slug || slide.linkUrl || index"
          class="hero-slide"
          :class="{ 'is-active': currentHeroIndex === index }"
        >
          <div
            class="hero-bg"
            :style="{ backgroundImage: `url(${slide.image})` }"
          />
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
            {{ currentHeroTitle.line1 }}<br /><span class="highlight">{{
              currentHeroTitle.line2
            }}</span>
          </h1>
          <p class="subtitle">{{ currentHeroSubtitle }}</p>
          <div class="action-group">
            <NuxtLink :to="currentHeroPrimaryLink" class="btn-main">{{
              currentHeroPrimaryLabel
            }}</NuxtLink>
            <NuxtLink :to="currentHeroSecondaryLink" class="btn-secondary">{{
              t('exploreCatalog')
            }}</NuxtLink>
          </div>
        </div>
      </div>
    </section>

    <section class="explore-section container">
      <h2 class="section-title explore-title">Explore <i>isinwheel</i></h2>
      <div class="category-grid">
        <NuxtLink
          v-for="category in categoryCards"
          :key="category.slug"
          :to="`/collections/${category.slug}`"
          class="category-card"
        >
          <div
            class="bg-placeholder"
            :style="{ backgroundImage: `url(${category.image})` }"
          />
          <div class="content">
            <h3>
              <span class="title-text">{{ category.name }}</span>
              <span class="count">{{ category.productCount }}</span>
            </h3>
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
          <svg
            class="wave-underline"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 100 20"
            preserveAspectRatio="none"
          >
            <path
              d="M0,10 Q12.5,20 25,10 T50,10 T75,10 T100,10"
              fill="none"
              stroke="#58cc02"
              stroke-width="4"
              stroke-linecap="round"
            />
          </svg>
        </h2>
        <NuxtLink :to="activeCategoryLink" class="view-all-link"
          >{{ copy.viewAllCurrent }} <ArrowRightIcon class="icon-right"
        /></NuxtLink>
      </div>
      <div class="tabs-wrapper">
        <div class="tabs">
          <button
            v-for="tab in tabs"
            :key="tab.slug"
            type="button"
            :class="['tab-btn', { active: currentTab === tab.slug }]"
            @click="currentTab = tab.slug"
          >
            {{ tab.name }}
          </button>
        </div>
      </div>
      <div v-if="featuredProducts.length" class="product-grid">
        <ProductCard
          v-for="product in featuredProducts"
          :key="product.id"
          :product="product"
        />
      </div>
      <div v-else class="products-empty-state">
        <p>{{ t('emptyProducts') }}</p>
        <NuxtLink :to="activeCategoryLink" class="empty-link">
          {{ copy.viewAllCurrent }} <ArrowRightIcon class="icon-right" />
        </NuxtLink>
      </div>
    </section>

    <section class="video-section">
      <div class="video-container" @click="toggleVideo">
        <iframe
          v-if="featureVideoEmbedUrl && isPlaying"
          class="promo-embed"
          :src="playingEmbedUrl"
          :title="copy.videoAlt"
          allow="
            accelerometer;
            autoplay;
            clipboard-write;
            encrypted-media;
            gyroscope;
            picture-in-picture;
          "
          allowfullscreen
        />
        <video
          v-else
          ref="videoRef"
          class="promo-video"
          src="https://www.w3schools.com/html/mov_bbb.mp4"
          loop
          muted
          playsinline
        />
        <div class="video-cover" :class="{ 'is-hidden': isPlaying }">
          <img :src="featureImage" :alt="copy.videoAlt" />
        </div>
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
        <a
          v-for="card in socialCards"
          :key="card.platform"
          class="video-card social-card"
          :href="card.url"
          target="_blank"
          rel="noreferrer"
        >
          <div
            class="bg-image"
            :style="{ backgroundImage: `url(${card.image})` }"
          />
          <div class="overlay" />
          <div class="play-btn"><ArrowRightIcon class="icon" /></div>
          <div class="card-content">
            <div class="user-info">
              <span class="avatar">{{ card.shortLabel }}</span>
              <span class="username">{{ card.platform }}</span>
            </div>
            <p class="quote">{{ card.title }}</p>
            <div class="tags">
              <span v-for="tag in card.tags" :key="tag" class="tag">{{
                tag
              }}</span>
            </div>
          </div>
        </a>
      </div>
    </section>

    <section class="media-review-section">
      <div class="media-bg"><div class="overlay" /></div>
      <div class="container media-content">
        <div class="quote-icon">{{ reviewSummary.rating || '4.8' }}</div>
        <h2 class="review-title">{{ reviewSummary.title || copy.mediaReviewTitle }}</h2>
        <div class="media-logo">
          <span class="logo-circle">REV</span
          ><span class="logo-text">{{ reviewSummary.countText || copy.customerReviewsSubtitle }}</span>
        </div>
        <div class="pagination-dots">
          <span class="dot active" /><span
            v-for="dot in 6"
            :key="dot"
            class="dot"
          />
        </div>
      </div>
    </section>

    <section class="why-choose-section container">
      <div class="content-wrapper">
        <div class="image-gallery">
          <div class="img-large" />
          <div class="img-small" />
        </div>
        <div class="text-content">
          <h2 class="section-title">{{ copy.whyChooseTitle }}</h2>
          <p class="description">{{ whyChooseDescription }}</p>
          <div class="why-choose-meta">
            <span v-if="supportContact.hours">{{ supportContact.hours }}</span>
            <span v-if="supportContact.phone">{{ supportContact.phone }}</span>
            <span v-if="supportContact.email">{{ supportContact.email }}</span>
          </div>
          <a
            v-if="primarySocialLink"
            :href="primarySocialLink.url"
            target="_blank"
            rel="noreferrer"
            class="btn-brand-story"
            >{{ copy.brandStory }} <ArrowRightIcon class="icon-right"
          /></a>
          <NuxtLink v-else to="/collections/electric-scooters" class="btn-brand-story"
            >{{ copy.brandStory }} <ArrowRightIcon class="icon-right"
          /></NuxtLink>
        </div>
      </div>
    </section>

    <section class="customer-reviews-section container">
      <div class="section-header text-center">
        <h2 class="section-title">{{ copy.customerReviewsTitle }}</h2>
        <p class="subtitle">{{ customerReviewsSubtitle }}</p>
      </div>
      <div class="reviews-carousel">
        <div class="nav-btn prev"><ChevronLeftIcon /></div>
        <div class="reviews-grid">
          <div
            v-for="review in customerReviews"
            :key="review.id"
            class="review-card"
          >
            <div
              class="review-img"
              :style="{
                backgroundImage: `url(${review.image || featureImage})`,
              }"
            />
            <div class="review-content">
              <div class="stars">
                <StarIcon
                  v-for="idx in 5"
                  :key="idx"
                  class="star-icon filled"
                />
              </div>
              <h4 class="reviewer-name">
                {{ review.name }}
                <span v-if="review.verified" class="verified-badge">{{
                  copy.verified
                }}</span>
              </h4>
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
          <svg
            class="wave-underline"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 100 20"
            preserveAspectRatio="none"
          >
            <path
              d="M0,10 Q12.5,20 25,10 T50,10 T75,10 T100,10"
              fill="none"
              stroke="#58cc02"
              stroke-width="4"
              stroke-linecap="round"
            />
          </svg>
        </h2>
        <NuxtLink to="/collections/electric-scooters" class="btn-view-all"
          ><FileTextIcon class="icon-left" /> {{ copy.viewAll }}</NuxtLink
        >
      </div>
      <div v-if="blogCards.length" class="blog-grid">
        <div class="blog-main">
          <div class="blog-card large">
            <div
              class="bg-img"
              :style="{ backgroundImage: `url(${blogCards[0].image})` }"
            />
            <div class="overlay" />
            <div class="blog-content">
              <div class="meta">
                <span class="date"
                  ><CalendarIcon class="meta-icon" />
                  {{ blogCards[0].date }}</span
                ><span class="comments"
                  ><MessageCircleIcon class="meta-icon" />
                  {{ blogCards[0].comments }}</span
                >
              </div>
              <h3 class="blog-title">{{ blogCards[0].title }}</h3>
              <a :href="blogCards[0].url || '#'" class="read-more">{{
                copy.readMore
              }}</a>
            </div>
          </div>
        </div>
        <div class="blog-side">
          <div
            v-for="blog in blogCards.slice(1)"
            :key="blog.id"
            class="blog-card small"
          >
            <div class="img-wrapper">
              <img :src="blog.image" :alt="blog.title" />
            </div>
            <div class="blog-content">
              <div class="meta">
                <span class="date"
                  ><CalendarIcon class="meta-icon" /> {{ blog.date }}</span
                ><span class="comments"
                  ><MessageCircleIcon class="meta-icon" />
                  {{ blog.comments }}</span
                >
              </div>
              <h3 class="blog-title">{{ blog.title }}</h3>
              <a :href="blog.url || '#'" class="read-more">{{
                copy.readMore
              }}</a>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="products-empty-state">
        <p>{{ t('emptyProducts') }}</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watchEffect } from 'vue'
import {
  ArrowRightIcon,
  CalendarIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  FileTextIcon,
  MessageCircleIcon,
  PauseIcon,
  PlayIcon,
  StarIcon,
} from 'lucide-vue-next'

const { lang, t } = useShopLocale()
const categories = ref<any[]>([])
const products = ref<any[]>([])
const homeContent = ref<Record<string, any>>({})
const currentHeroIndex = ref(0)
const currentTab = ref('electric-scooters')
const isPlaying = ref(false)
const videoRef = ref<HTMLVideoElement | null>(null)
let heroTimer: ReturnType<typeof window.setInterval> | null = null

const categoryVisuals = {
  'electric-scooters': {
    image:
      'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1400',
    description: {
      zh: '\u8f7b\u677e\u901a\u52e4\uff0c\u57ce\u5e02\u4e0e\u5468\u672b\u9a91\u884c\u90fd\u80fd\u517c\u987e\u3002',
      en: 'Foldable freedom for your daily commute and weekend rides.',
    },
  },
  'electric-bike': {
    image:
      'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1400',
    description: {
      zh: '\u957f\u8ddd\u79bb\u4e0e\u590d\u6742\u8def\u51b5\u4e5f\u80fd\u66f4\u4ece\u5bb9\u62ff\u4e0b\u3002',
      en: 'Conquer hills and long-distance routes with confidence.',
    },
  },
  'electric-skateboard': {
    image:
      'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=1400',
    description: {
      zh: '\u517c\u987e\u987a\u6ed1\u64cd\u63a7\u4e0e\u901f\u5ea6\u611f\u7684\u8f7b\u5feb\u51fa\u884c\u3002',
      en: 'Electrify every carve with smooth control and speed.',
    },
  },
  accessories: {
    image:
      'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1400',
    description: {
      zh: '\u5347\u7ea7\u3001\u4fdd\u62a4\u5e76\u4e2a\u6027\u5316\u4f60\u7684\u6bcf\u4e00\u6bb5\u9a91\u884c\u3002',
      en: 'Upgrade, protect, and personalize every ride.',
    },
  },
} as const

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        heroLine1: '\u63a2\u7d22\u66f4\u8fdc',
        heroLine2: '\u667a\u80fd\u9a91\u884c',
        bestSellers: '\u70ed\u9500\u4ea7\u54c1',
        viewAllCurrent: '\u67e5\u770b\u5f53\u524d\u5206\u7c7b',
        mediaReviewTitle:
          'isinwheel S10Max \u8bc4\u6d4b\uff1a\u517c\u987e\u4fbf\u643a\u4e0e\u6027\u80fd\u7684\u6700\u540e\u4e00\u516c\u91cc\u6ed1\u677f\u8f66',
        whyChooseTitle: '\u4e3a\u4ec0\u4e48\u9009\u62e9 isinwheel',
        whyChooseDesc:
          '\u6211\u4eec\u5e0c\u671b\u628a\u503c\u5f97\u4fe1\u8d56\u7684\u7535\u52a8\u51fa\u884c\u4ea7\u54c1\u5e26\u5230\u66f4\u591a\u771f\u5b9e\u573a\u666f\u91cc\uff0c\u8ba9\u901a\u52e4\u3001\u77ed\u9014\u51fa\u6e38\u548c\u65e5\u5e38\u4ee3\u6b65\u90fd\u66f4\u8f7b\u677e\u3001\u66f4\u6709\u8da3\u3002',
        brandStory: '\u54c1\u724c\u6545\u4e8b',
        customerReviewsTitle: '\u5927\u5bb6\u5982\u4f55\u8bc4\u4ef7 isinwheel',
        customerReviewsSubtitle: '\u6765\u81ea 5870 \u6761\u771f\u5b9e\u8bc4\u4ef7',
        verified: '\u5df2\u9a8c\u8bc1',
        blogTitle: 'isinwheel \u535a\u5ba2',
        viewAll: '\u67e5\u770b\u5168\u90e8',
        readMore: '\u9605\u8bfb\u66f4\u591a',
        videoAlt: 'isinwheel \u9996\u9875\u89c6\u9891\u5c01\u9762',
      }
    : {
        heroLine1: 'Explore Beyond',
        heroLine2: 'Smart Riding',
        bestSellers: 'Best Sellers',
        viewAllCurrent: 'View current collection',
        mediaReviewTitle:
          'isinwheel S10Max Review: A Powerful Yet Portable Last-Mile Scooter',
        whyChooseTitle: 'Why Choose isinwheel',
        whyChooseDesc:
          'We build electric rides that make commuting, short trips, and everyday errands feel easier, more affordable, and a lot more fun.',
        brandStory: 'Brand Story',
        customerReviewsTitle: 'What The People Say About Isinwheel',
        customerReviewsSubtitle: 'from 5870 reviews',
        verified: 'Verified',
        blogTitle: 'isinwheel Blog',
        viewAll: 'View all',
        readMore: 'Read more',
        videoAlt: 'isinwheel homepage video cover',
      },
)

const fallbackCategories = computed(() => [
  { slug: 'electric-scooters', name: t('electricScooters'), productCount: 0 },
  { slug: 'electric-bike', name: t('electricBike'), productCount: 0 },
  {
    slug: 'electric-skateboard',
    name: t('electricSkateboard'),
    productCount: 0,
  },
  { slug: 'accessories', name: t('accessories'), productCount: 0 },
])

const displayCategories = computed(() =>
  categories.value.length ? categories.value : fallbackCategories.value,
)
const categoryCards = computed(() =>
  displayCategories.value.map((category: any) => {
    const visual =
      categoryVisuals[category.slug as keyof typeof categoryVisuals] ||
      categoryVisuals['electric-scooters']
    return {
      ...category,
      image: category.menuImage || category.heroImage || visual.image,
      description:
        category.description ||
        visual.description[lang.value === 'zh' ? 'zh' : 'en'],
    }
  }),
)
const tabs = computed(() =>
  displayCategories.value.map((item: any) => ({
    slug: item.slug,
    name: item.name,
  })),
)
const fallbackHeroSlides = computed(() =>
  categoryCards.value.map((category) => ({
    slug: category.slug,
    title: '',
    subtitle: '',
    image: category.heroImage || category.menuImage || category.image,
    linkUrl: `/collections/${category.slug}`,
    ctaLabel: t('shopNow'),
  })),
)
const heroSlides = computed(() => {
  const slides = Array.isArray(homeContent.value?.heroSlides)
    ? homeContent.value.heroSlides.filter((item: any) => item?.image)
    : []
  return slides.length ? slides : fallbackHeroSlides.value
})
const activeCategoryLink = computed(() => `/collections/${currentTab.value}`)

const resolveCategorySlug = (product: any) => {
  const direct =
    product.categorySlug ||
    product.category?.slug ||
    product.category?.categorySlug
  if (direct) return direct
  const source =
    `${product.categoryName || ''} ${product.title || ''}`.toLowerCase()
  if (source.includes('skateboard')) return 'electric-skateboard'
  if (source.includes('bike')) return 'electric-bike'
  if (source.includes('accessor')) return 'accessories'
  return 'electric-scooters'
}

const featuredProducts = computed(() => {
  return products.value
    .filter((product) => resolveCategorySlug(product) === currentTab.value)
    .slice(0, 4)
})

const currentHeroSlide = computed(
  () => heroSlides.value[currentHeroIndex.value] || heroSlides.value[0] || null,
)
const currentHeroTitle = computed(() => {
  const title = String(currentHeroSlide.value?.title || '').trim()
  if (!title) {
    return {
      line1: copy.value.heroLine1,
      line2: copy.value.heroLine2,
    }
  }
  const parts = title.split(/\s+/)
  if (parts.length === 1) {
    return {
      line1: title,
      line2: copy.value.heroLine2,
    }
  }
  const splitIndex = Math.max(1, Math.ceil(parts.length / 2))
  return {
    line1: parts.slice(0, splitIndex).join(' '),
    line2: parts.slice(splitIndex).join(' '),
  }
})
const currentHeroSubtitle = computed(
  () => currentHeroSlide.value?.subtitle || t('homeHeroDesc'),
)
const currentHeroPrimaryLink = computed(
  () => currentHeroSlide.value?.linkUrl || '/collections/electric-scooters',
)
const currentHeroPrimaryLabel = computed(
  () => currentHeroSlide.value?.ctaLabel || t('shopNow'),
)
const currentHeroSecondaryLink = computed(() => activeCategoryLink.value)
const featureVideo = computed(() => homeContent.value?.featureVideo || {})
const featureVideoEmbedUrl = computed(() =>
  String(featureVideo.value?.embedUrl || '').trim(),
)
const playingEmbedUrl = computed(() => {
  if (!featureVideoEmbedUrl.value) {
    return ''
  }
  return `${featureVideoEmbedUrl.value}${featureVideoEmbedUrl.value.includes('?') ? '&' : '?'}autoplay=1`
})
const featureImage = computed(
  () =>
    featureVideo.value?.poster ||
    heroSlides.value[currentHeroIndex.value]?.image ||
    heroSlides.value[0]?.image ||
    categoryVisuals['electric-scooters'].image,
)
const siteMeta = computed(() => homeContent.value?.siteMeta || {})
const reviewSummary = computed(() => homeContent.value?.reviewSummary || {})
const supportContact = computed(() => homeContent.value?.supportContact || {})
const customerReviewsSubtitle = computed(
  () => reviewSummary.value?.countText || copy.value.customerReviewsSubtitle,
)
const whyChooseDescription = computed(
  () => siteMeta.value?.description || copy.value.whyChooseDesc,
)
const socialCards = computed(() => {
  const fallbackImages = [
    'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=800',
  ]
  const links = Array.isArray(homeContent.value?.socialLinks)
    ? homeContent.value.socialLinks
    : []
  if (!links.length) {
    return []
  }
  return links.slice(0, 4).map((item: any, index: number) => ({
    platform: item.platform || `Social ${index + 1}`,
    title: item.title || item.platform || `Social ${index + 1}`,
    url: item.url || '#',
    image: fallbackImages[index % fallbackImages.length],
    shortLabel: String(item.platform || `S${index + 1}`)
      .slice(0, 2)
      .toUpperCase(),
    tags: [
      `#${String(item.platform || 'social').toLowerCase().replace(/\s+/g, '')}`,
      reviewSummary.value?.reviewCount
        ? lang.value === 'zh'
          ? `${reviewSummary.value.reviewCount}\u6761\u8bc4\u4ef7`
          : `${reviewSummary.value.reviewCount} reviews`
        : lang.value === 'zh'
          ? '\u5b98\u65b9\u8d26\u53f7'
          : 'official',
    ],
  }))
})
const primarySocialLink = computed(() => socialCards.value[0] || null)

const fallbackCustomerReviews = computed(() =>
  lang.value === 'zh'
    ? [
        {
          id: 1,
          image:
            'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400',
          name: 'Andrew',
          verified: false,
          text: '\u51cf\u9707\u521d\u4e0a\u624b\u65f6\u504f\u786c\uff0c\u7a0d\u5fae\u8c03\u6821\u4e4b\u540e\u901a\u52e4\u4f53\u9a8c\u987a\u4e86\u5f88\u591a\uff0c\u73b0\u5728\u6bcf\u5929\u90fd\u5728\u9a91\u3002',
        },
        {
          id: 2,
          image:
            'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=400',
          name: 'Joseph P.',
          verified: true,
          text: '\u53c2\u6570\u548c\u63cf\u8ff0\u57fa\u672c\u4e00\u81f4\uff0c\u52a8\u529b\u8f93\u51fa\u5f88\u76f4\u63a5\uff0c\u6574\u8f66\u505a\u5de5\u4e5f\u6bd4\u9884\u671f\u66f4\u624e\u5b9e\u3002',
        },
        {
          id: 3,
          image:
            'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=400',
          name: 'Pamela',
          verified: false,
          text: '\u5b89\u88c5\u5f88\u5feb\uff0c\u7b2c\u4e00\u6b21\u4e0a\u8def\u5c31\u80fd\u611f\u53d7\u5230\u5b83\u7684\u7a33\u5b9a\u548c\u6613\u4e0a\u624b\u3002',
        },
        {
          id: 4,
          image:
            'https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=400',
          name: 'Chad S.',
          verified: false,
          text: '\u5982\u679c\u4f60\u60f3\u627e\u4e00\u53f0\u901f\u5ea6\u548c\u7eed\u822a\u6bd4\u8f83\u5747\u8861\u7684\u8f66\uff0c\u8fd9\u53f0\u786e\u5b9e\u5f88\u6709\u7ade\u4e89\u529b\u3002',
        },
      ]
    : [
        {
          id: 1,
          image:
            'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400',
          name: 'Andrew',
          verified: false,
          text: 'The suspension felt stiff at first, but once adjusted it became a very smooth daily commuter.',
        },
        {
          id: 2,
          image:
            'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=400',
          name: 'Joseph P.',
          verified: true,
          text: 'Specs line up well with the description. The power delivery is direct and the build feels solid.',
        },
        {
          id: 3,
          image:
            'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=400',
          name: 'Pamela',
          verified: false,
          text: 'Assembly was quick and the first ride already felt stable and easy to get comfortable with.',
        },
        {
          id: 4,
          image:
            'https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=400',
          name: 'Chad S.',
          verified: false,
          text: 'If you want a ride with a good balance of speed and range, this one is genuinely competitive.',
        },
      ],
)

const fallbackBlogCards = computed(() =>
  lang.value === 'zh'
    ? [
        {
          id: 1,
          image:
            'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800',
          title: '\u9ad8\u6027\u4ef7\u6bd4\u8d8a\u91ce\u7535\u52a8\u6ed1\u677f\u8f66\u7684\u60ac\u6302\u5230\u5e95\u8be5\u600e\u4e48\u9009',
          date: '2026-03-26',
          comments: '0 \u6761\u8bc4\u8bba',
        },
        {
          id: 2,
          image:
            'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400',
          title: '\u9884\u7b97\u6709\u9650\u65f6\uff0c\u5982\u4f55\u6311\u4e00\u53f0\u9002\u5408\u65e5\u5e38\u901a\u52e4\u7684\u7535\u52a8\u6ed1\u677f\u8f66',
          date: '2026-03-17',
          comments: '0 \u6761\u8bc4\u8bba',
        },
        {
          id: 3,
          image:
            'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400',
          title: '\u5bb6\u5ead\u77ed\u9014\u4ee3\u6b65\uff0c500 \u7f8e\u5143\u4ee5\u5185\u6709\u54ea\u4e9b\u66f4\u5408\u9002\u7684\u9009\u62e9',
          date: '2026-03-11',
          comments: '0 \u6761\u8bc4\u8bba',
        },
      ]
    : [
        {
          id: 1,
          image:
            'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800',
          title:
            'Best Suspension for Value Off-Road Scooters: What Matters on Rough Paths',
          date: 'Mar 26, 2026',
          comments: '0 comments',
        },
        {
          id: 2,
          image:
            'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400',
          title:
            'How to Pick a Value-Packed Electric Scooter for Daily Commuting',
          date: 'Mar 17, 2026',
          comments: '0 comments',
        },
        {
          id: 3,
          image:
            'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400',
          title: 'Best Under-$500 Options for Easy Family Neighborhood Errands',
          date: 'Mar 11, 2026',
          comments: '0 comments',
        },
      ],
)

const customerReviews = computed(() => {
  const reviews = Array.isArray(homeContent.value?.customerReviews)
    ? homeContent.value.customerReviews
    : []
  if (!reviews.length) {
    return fallbackCustomerReviews.value
  }
  return reviews.slice(0, 4).map((review: any, index: number) => ({
    id: review.id || index + 1,
    image: review.image || '',
    name: review.name || `Review ${index + 1}`,
    verified: Boolean(review.verified),
    text: review.text || review.title || '',
  }))
})

const blogCards = computed(() => {
  const cards = Array.isArray(homeContent.value?.blogCards)
    ? homeContent.value.blogCards
    : []
  if (!cards.length) {
    return fallbackBlogCards.value
  }
  return cards.slice(0, 3).map((card: any, index: number) => ({
    id: card.id || index + 1,
    image: card.image || categoryVisuals['electric-scooters'].image,
    title: card.title || '',
    date: card.date || '',
    comments: card.comments || '',
    url: card.url || '#',
  }))
})

const { data: homeData } = await useAsyncData(
  'home-page-data',
  async () => {
    try {
      const [categoryRes, productRes, contentRes] = await Promise.all([
        useHttp('/api/category/tree'),
        useHttp(`/api/product/list?pageNum=1&pageSize=24&lang=${lang.value}`),
        $fetch('/home-content').catch(() => null),
      ])
      return {
        categories: categoryRes?.code === 200 ? categoryRes.data || [] : [],
        products:
          productRes?.code === 200 ? productRes.data?.records || [] : [],
        content: contentRes || {},
      }
    } catch (error) {
      return {
        categories: [],
        products: [],
        content: {},
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
  homeContent.value = homeData.value?.content || {}
  if (
    displayCategories.value.length &&
    !displayCategories.value.some((item: any) => item.slug === currentTab.value)
  ) {
    currentTab.value = displayCategories.value[0].slug
  }
  if (currentHeroIndex.value >= heroSlides.value.length) {
    currentHeroIndex.value = 0
  }
})

const startHeroTimer = () => {
  if (heroTimer) window.clearInterval(heroTimer)
  heroTimer = window.setInterval(() => {
    if (!heroSlides.value.length) return
    currentHeroIndex.value =
      (currentHeroIndex.value + 1) % heroSlides.value.length
  }, 5000)
}

const stopHeroTimer = () => {
  if (!heroTimer) return
  window.clearInterval(heroTimer)
  heroTimer = null
}

const toggleVideo = async () => {
  if (featureVideoEmbedUrl.value) {
    isPlaying.value = !isPlaying.value
    return
  }
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
.home-page {
  padding-bottom: 60px;
}
.section-title {
  font-size: 40px;
  font-weight: 800;
  margin-bottom: 30px;
}
.hero-section {
  position: relative;
  min-height: 92vh;
  display: flex;
  align-items: center;
  overflow: hidden;
  margin-bottom: $spacing-xl;
  padding: 40px 0;
  background-color: $bg-light;
}
.hero-carousel,
.hero-slide,
.hero-bg,
.hero-overlay,
.media-bg,
.media-bg .overlay {
  position: absolute;
  inset: 0;
}
.hero-carousel {
  z-index: 1;
}
.hero-slide {
  opacity: 0;
  transition: opacity 0.6s ease;
}
.hero-slide.is-active {
  opacity: 1;
}
.hero-bg {
  background-size: cover;
  background-position: center;
}
.hero-overlay {
  background: linear-gradient(
    to right,
    rgba(0, 0, 0, 0.78) 0%,

    rgba(0, 0, 0, 0.22) 100%
  );
}
.hero-indicators {
  position: absolute;
  left: 80%;
  bottom: 32px;
  z-index: 3;
  display: flex;
  gap: 10px;
  transform: translateX(-50%);
}
.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.35);
  cursor: pointer;
  transition: all 0.25s ease;
}
.indicator-dot.is-active,
.indicator-dot:hover,
.pagination-dots .dot.active,
.pagination-dots .dot:hover {
  background: #fff;
  transform: scale(1.1);
  width: 20px;
  height: 10px;
  border-radius: 5px;
}
.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
  color: $white;
}
.text-content {
  max-width: 620px;
}
.title {
  margin: 0 0 18px;
  font-size: clamp(48px, 6vw, 84px);
  font-weight: 800;
  line-height: 1;
  letter-spacing: 1px;
}
.highlight {
  color: #58cc02;
}
.subtitle {
  max-width: 48ch;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  line-height: 1.7;
}
.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 32px;
}
.btn-main,
.btn-secondary,
.btn-brand-story,
.btn-view-all {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 999px;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.25s ease;
}
.btn-main,
.btn-secondary {
  min-height: 54px;
  padding: 0 28px;
}
.btn-main {
  background: #111;
  color: #fff;
}
.btn-main:hover,
.btn-brand-story:hover {
  background: #222;
}
.btn-secondary {
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}
.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.2);
}
.explore-section,
.best-sellers-section,
.video-section,
.influencer-section,
.why-choose-section,
.customer-reviews-section,
.blog-section {
  margin-top: 80px;
}
.explore-title {
  margin-bottom: 28px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}
.category-card {
  position: relative;
  min-height: 320px;
  overflow: hidden;
  border-radius: 18px;
  color: #fff;
  text-decoration: none;
}
.bg-placeholder {
  position: absolute;
  inset: 0;
  background-position: center;
  background-size: cover;
  transition: transform 0.45s ease;
}
.category-card:hover .bg-placeholder {
  transform: scale(1.05);
}
.category-card .content {
  position: relative;
  z-index: 2;
  display: flex;
  min-height: 320px;
  flex-direction: column;
  justify-content: flex-end;
  padding: 24px;
  background: linear-gradient(
    180deg,
    transparent 15%,
    rgba(0, 0, 0, 0.82) 100%
  );
}
.category-card h3 {
  display: inline-flex;
  align-items: flex-end;
  gap: 8px;
  margin: 0 0 12px;
  font-size: 26px;
}
.category-card .title-text {
  position: relative;
  display: inline-block;
  padding-bottom: 14px;
}
.category-card .title-text::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 1px;
  background: #fff;
  transform: scaleX(0);
  transform-origin: left center;
  transition: transform 0.2s ease-out;
}
.category-card:hover .title-text::after {
  transform: scaleX(1);
}
.category-card .count {
  color: #8ad5d0;
}
.category-card p {
  margin: 0 0 14px;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.65;
}
.category-card .icon {
  width: 22px;
  height: 22px;
  transform: rotate(0deg);
  transform-origin: center;
  transition: transform 0.25s ease-out;
}
.category-card:hover .icon {
  transform: rotate(90deg);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 40px;
}
.best-sellers-title,
.blog-title-main {
  position: relative;
  display: inline-block;
  margin-bottom: 0;
}
.wave-underline {
  position: absolute;
  left: 0;
  bottom: -12px;
  width: 100%;
  height: 12px;
}
.view-all-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: $text-color;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.25s ease;
}
.view-all-link:hover,
.btn-view-all:hover {
  color: #58cc02;
}
.icon-right {
  width: 16px;
  height: 16px;
  transition: transform 0.25s ease;
}
.view-all-link:hover .icon-right,
.btn-brand-story:hover .icon-right {
  transform: translateX(4px);
}
.tabs-wrapper {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 40px;
}
.tabs {
  display: inline-flex;
  gap: 20px;
  padding: 4px;
}
.tab-btn {
  padding: 12px 32px;
  border: none;
  border-radius: 36px;
  background: $bg-light;
  color: $text-light;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}
.tab-btn.active,
.tab-btn:hover {
  background: $primary-color;
  color: $white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(260px, 1fr));
  gap: 24px;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 12px;
  scroll-snap-type: x proximity;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.product-grid > * {
  min-width: 0;
  scroll-snap-align: start;
}
.product-grid::-webkit-scrollbar {
  display: none;
}
.products-empty-state {
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 16px;
  border: 1px dashed $border-color;
  border-radius: 24px;
  background: linear-gradient(180deg, #fcfcfc 0%, #f7f7f7 100%);
  color: $text-light;
  text-align: center;
}
.empty-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: $text-color;
  font-weight: 700;
  text-decoration: none;
}
.empty-link:hover {
  color: $primary-color;
}
.video-container {
  max-width: 1280px;
  margin: 0 auto;
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  border-radius: 24px;
  background: #000;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  cursor: pointer;
}
.promo-video,
.promo-embed,
.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.promo-embed {
  border: 0;
}
.video-cover {
  position: absolute;
  inset: 0;
  z-index: 1;
  transition: opacity 0.45s ease;
}
.video-cover.is-hidden {
  opacity: 0;
  pointer-events: none;
}
.video-controls {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}
.video-controls.is-playing {
  background: transparent;
  opacity: 0;
}
.video-controls.is-playing:hover {
  background: rgba(0, 0, 0, 0.1);
  opacity: 1;
}
.play-pause-btn {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  backdrop-filter: blur(8px);
  cursor: pointer;
  transition: all 0.3s ease;
}
.play-pause-btn:hover {
  transform: scale(1.08);
  background: rgba(255, 255, 255, 0.35);
}
.play-pause-btn .icon {
  width: 32px;
  height: 32px;
}
.play-pause-btn .play-icon {
  margin-left: 4px;
}
.influencer-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}
.video-card {
  position: relative;
  width: 100%;
  aspect-ratio: 9/16;
  overflow: hidden;
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}
.video-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
}
.video-card:hover .bg-image,
.blog-card.large:hover .bg-img,
.blog-card.small:hover .img-wrapper img {
  transform: scale(1.05);
}
.video-card:hover .play-btn {
  transform: translate(-50%, -50%) scale(1.1);
  background: rgba(255, 255, 255, 0.3);
}
.social-card {
  text-decoration: none;
}
.video-card .bg-image,
.review-img,
.blog-main .bg-img {
  position: absolute;
  inset: 0;
  background-position: center;
  background-size: cover;
}
.video-card .bg-image,
.blog-main .bg-img,
.img-wrapper img {
  transition: transform 0.5s ease;
}
.video-card .overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.8) 0%,
    rgba(0, 0, 0, 0.2) 50%,
    transparent 100%
  );
}
.play-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: $white;
  backdrop-filter: blur(4px);
  transform: translate(-50%, -50%);
  transition: all 0.3s ease;
}
.play-btn .icon {
  width: 24px;
  height: 24px;
  margin-left: 3px;
}
.card-content {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  color: $white;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.avatar {
  width: 32px;
  height: 32px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  object-fit: cover;
}
.social-card .avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.16);
  color: #fff;
  font-size: 12px;
  font-weight: 800;
}
.username {
  font-size: 14px;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}
.quote {
  margin: 0;
  font-size: 13px;
  line-height: 1.4;
  opacity: 0.9;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tag {
  color: $secondary-color;
  font-size: 12px;
  font-weight: 500;
}
.media-review-section {
  position: relative;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 80px 0;
  color: $white;
  text-align: center;
}
.media-bg {
  z-index: 1;
  background: url('https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1920')
    center/cover fixed;
}
.media-bg .overlay {
  background: rgba(0, 0, 0, 0.7);
}
.media-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  padding: 60px 20px;
}
.quote-icon {
  margin-bottom: 20px;
  color: $secondary-color;
  font-size: 80px;
  line-height: 1;
  font-family: Georgia, serif;
}
.review-title {
  margin-bottom: 40px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.4;
}
.media-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 40px;
}
.logo-circle {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: $danger-color;
  font-size: 16px;
  font-weight: 700;
}
.logo-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}
.pagination-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
}
.pagination-dots .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.25s ease;
}
.why-choose-section,
.customer-reviews-section,
.blog-section {
  max-width: $max-width;
}
.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}
.image-gallery {
  position: relative;
  width: 100%;
  min-height: 500px;
  flex: 1;
}
.img-large,
.img-small {
  position: absolute;
  background-position: center;
  background-size: cover;
  transition: transform 0.5s ease;
}
.img-large {
  top: 5%;
  right: 15%;
  width: 65%;
  height: 80%;
  z-index: 1;
  border-radius: 16px;
  background-image: url('https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800');
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  transform: rotate(6deg);
}
.img-small {
  left: 10%;
  bottom: 15%;
  width: 50%;
  height: 55%;
  z-index: 2;
  border: 6px solid $white;
  border-radius: 12px;
  background-image: url('https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=600');
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  transform: rotate(-4deg);
}
.img-large:hover {
  transform: rotate(2deg) scale(1.02);
}
.img-small:hover {
  transform: rotate(-1deg) scale(1.05);
}
.why-choose-section .text-content {
  flex: 1;
}
.why-choose-section .section-title {
  color: #58cc02;
  font-size: 36px;
  line-height: 1.2;
}
.description {
  margin-bottom: 32px;
  color: $text-light;
  font-size: 16px;
  line-height: 1.8;
}
.why-choose-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;

  span {
    padding: 9px 14px;
    border-radius: 999px;
    background: #f3f4f6;
    color: #344054;
    font-size: 13px;
    font-weight: 600;
  }
}
.btn-brand-story {
  padding: 12px 32px;
  background: #111;
  color: $white;
}
.btn-brand-story .icon-right,
.btn-view-all .icon-left {
  width: 16px;
  height: 16px;
}
.customer-reviews-section .section-header {
  margin-bottom: 40px;
}
.customer-reviews-section .section-title {
  margin-bottom: 8px;
  font-size: 32px;
}
.customer-reviews-section .subtitle {
  margin: 0;
  color: $text-light;
  font-size: 16px;
}
.reviews-carousel {
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
}
.nav-btn {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: $white;
  color: $secondary-color;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.25s ease;
  z-index: 2;
}
.nav-btn:hover {
  background: $secondary-color;
  color: $white;
}
.reviews-grid {
  display: flex;
  flex: 1;
  gap: 24px;
  overflow-x: auto;
  padding: 20px 0;
  scroll-snap-type: x mandatory;
  scrollbar-width: none;
}
.reviews-grid::-webkit-scrollbar {
  display: none;
}
.review-card {
  flex: 0 0 100%;
  overflow: hidden;
  border: 1px solid $border-color;
  border-radius: 16px;
  background: $white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  scroll-snap-align: start;
}
.review-img {
  position: relative;
  height: 200px;
}
.review-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 24px;
}
.stars {
  display: flex;
  gap: 4px;
  color: #ffc107;
}
.star-icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
}
.reviewer-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 16px;
  font-weight: 700;
}
.verified-badge {
  padding: 2px 6px;
  border-radius: 4px;
  background: #000;
  color: #fff;
  font-size: 10px;
  font-weight: 500;
}
.review-text {
  margin: 0;
  color: $text-light;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}
.blog-section {
  margin-bottom: 80px;
}
.btn-view-all {
  padding: 12px 24px;
  border: 1px solid $border-color;
  background: #fff;
  color: $text-color;
}
.blog-grid {
  display: grid;
  gap: 24px;
}
.blog-card {
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  cursor: pointer;
}
.blog-card .meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 12px;
}
.meta-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  vertical-align: middle;
}
.read-more {
  display: inline-block;
  margin-top: 16px;
  color: inherit;
  font-size: 14px;
  font-weight: 600;
  text-decoration: underline;
  text-underline-offset: 4px;
}
.blog-card.large {
  height: 400px;
}
.blog-card.large .overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, transparent 100%);
}
.blog-card.large .blog-content {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  padding: 40px;
  color: $white;
}
.blog-card.large .blog-title {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
}
.blog-side {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.blog-card.small {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid $border-color;
  background: $white;
  transition:
    box-shadow 0.3s ease,
    transform 0.3s ease;
}
.blog-card.small:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}
.img-wrapper {
  flex: 0 0 200px;
  height: 200px;
  overflow: hidden;
}
.img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.blog-card.small .blog-content {
  flex: 1;
  padding: 24px;
}
.blog-card.small .meta {
  color: $text-light;
}
.blog-card.small .blog-title {
  color: $text-color;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
.best-sellers-section {
  margin-bottom: 80px;
}
.influencer-section {
  margin-bottom: 80px;
  max-width: $max-width;
}
.blog-section,
.customer-reviews-section,
.why-choose-section {
  max-width: $max-width;
}
.blog-section .flex-between {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 16px;
}
@media (min-width: 768px) {
  .review-card {
    flex: 0 0 calc(50% - 12px);
  }
}
@media (min-width: $bp-md) {
  .influencer-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
  .media-content .review-title {
    font-size: 36px;
  }
  .customer-reviews-section .section-title {
    font-size: 40px;
  }
  .blog-card.large .blog-title {
    font-size: 32px;
  }
  .blog-card.small {
    flex-direction: row;
    align-items: center;
  }
}
@media (min-width: $bp-lg) {
  .influencer-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
  .content-wrapper {
    flex-direction: row;
    gap: 80px;
  }
  .blog-grid {
    grid-template-columns: 1.5fr 1fr;
  }
  .blog-card.large {
    min-height: 500px;
    height: 100%;
  }
  .blog-card.small {
    flex-direction: column;
  }
  .review-card {
    flex: 0 0 calc(25% - 18px);
  }
  .why-choose-section .section-title {
    font-size: 48px;
  }
}
@media (min-width: $bp-xl) {
  .video-container {
    aspect-ratio: 21/9;
  }
  .blog-card.small {
    flex-direction: row;
  }
}
@media (max-width: 1100px) {
  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: $bp-lg) {
  .nav-btn {
    display: none;
  }
}
@media (max-width: 720px) {
  .hero-section {
    min-height: 75vh;
    padding: 28px 0;
  }
  .title {
    font-size: clamp(40px, 13vw, 64px);
  }
  .subtitle {
    font-size: 16px;
  }
  .category-grid,
  .influencer-grid {
    grid-template-columns: 1fr;
  }
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }
  .tabs {
    flex-wrap: wrap;
  }
}
</style>
