<template>
  <div class="home-page">
    <!-- 首屏满铺视窗大图轮播 (Hero Banner) -->
    <section class="hero-section">
      <!-- 轮播图背景 -->
      <div class="hero-carousel">
        <div 
          class="hero-slide" 
          v-for="(slide, index) in heroSlides" 
          :key="index"
          :class="{ 'is-active': currentHeroIndex === index }"
        >
          <div class="hero-bg" :style="{ backgroundImage: `url(${slide.image})` }"></div>
        </div>
        <!-- 轮播指示器 -->
        <div class="hero-indicators">
          <span 
            v-for="(_, index) in heroSlides" 
            :key="index"
            class="indicator-dot"
            :class="{ 'is-active': currentHeroIndex === index }"
            @click="currentHeroIndex = index"
          ></span>
        </div>
        <!-- 半透明遮罩 -->
        <div class="hero-overlay"></div>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="container hero-content">
        <div class="text-content">
          <h1 class="title">探索无界 <br> <span class="highlight">智能骑行</span></h1>
          <p class="subtitle">采用领先科技，带来更加环保、便捷与酷炫的出行体验。</p>
          <div class="action-group">
            <Button variant="primary" size="lg" class="btn-main">即刻出发</Button>
            <Button variant="outline" size="lg" class="btn-secondary">了解更多</Button>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类导览 (Explore Categories) -->
    <section class="explore-section container">
      <h2 class="section-title explore-title">Explore <i>isinwheel</i></h2>
      <div class="category-grid">
        <div class="category-card" v-for="cat in categories" :key="cat.title">
          <div class="bg-placeholder"></div>
          <div class="content">
            <h3>{{ cat.title }} <span class="count">{{ cat.count }}</span></h3>
            <p>{{ cat.desc }}</p>
            <ArrowRightIcon class="icon" />
          </div>
        </div>
      </div>
    </section>
  <!-- 明星产品展示区 (Best Sellers) -->
    <section class="best-sellers-section container">
    <!-- Best Sellers 模块顶部标题区域 -->
    <div class="section-header flex-between align-center mb-xl">
      <h2 class="section-title mb-0">Best Sellers</h2>
      <NuxtLink to="/collections/all" class="view-all-link">
        All Commuter Scooter (7) <ArrowRightIcon class="icon-right" />
      </NuxtLink>
    </div>
      
      <!-- 选项卡 -->
      <div class="tabs-wrapper">
        <div class="tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab"
            :class="['tab-btn', { active: currentTab === tab }]"
            @click="currentTab = tab"
          >
            {{ tab }}
          </button>
        </div>
      </div>

      <!-- 产品网格 -->
      <div class="product-grid">
        <ProductCard 
          v-for="product in filteredProducts" 
          :key="product.id"
          :product="product"
        />
      </div>
    </section>
    <!-- 视频介绍模块 (Video Introduction) -->
    <section class="video-section container">
      <div class="video-container" @click="toggleVideo">
        <!-- 视频元素 -->
        <video 
          ref="videoRef"
          class="promo-video"
          src="https://www.w3schools.com/html/mov_bbb.mp4" 
          loop
          muted
          playsinline
        ></video>
        
        <!-- 视频封面图 -->
        <div class="video-cover" :class="{ 'is-hidden': isPlaying }">
          <img src="https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1920" alt="Video Cover" />
        </div>

        <!-- 播放控制覆盖层 -->
        <div class="video-controls" :class="{ 'is-playing': isPlaying }">
          <button class="play-pause-btn">
            <PlayIcon v-if="!isPlaying" class="icon play-icon" />
            <PauseIcon v-else class="icon pause-icon" />
          </button>
        </div>
      </div>
    </section>
  <!-- 达人视频展示区 (Influencer Videos) -->
    <section class="influencer-section container">
      <!-- <h2 class="section-title">Rider Stories</h2> -->
      <div class="influencer-grid">
        <div class="video-card" v-for="video in influencerVideos" :key="video.id">
          <!-- 背景图 -->
          <div class="bg-image" :style="{ backgroundImage: `url(${video.bgImage})` }"></div>
          
          <!-- 渐变遮罩 -->
          <div class="overlay"></div>

          <!-- 播放图标 -->
          <div class="play-btn">
            <PlayIcon class="icon" />
          </div>

          <!-- 底部内容区 -->
          <div class="card-content">
            <!-- 用户信息 -->
            <div class="user-info">
              <img :src="video.avatar" alt="Avatar" class="avatar" />
              <span class="username">{{ video.username }}</span>
            </div>
            <!-- 引言 -->
            <p class="quote">"{{ video.quote }}"</p>
            <!-- 标签 -->
            <div class="tags">
              <span v-for="tag in video.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  

  

    <!-- 媒体评价模块 (Media Review) -->
    <section class="media-review-section">
      <div class="media-bg">
        <div class="overlay"></div>
      </div>
      <div class="container media-content">
        <div class="quote-icon">“</div>
        <h2 class="review-title">isinwheel S10Max Review: A Powerful Yet Portable Last-Mile Scooter</h2>
        <div class="media-logo">
          <span class="logo-circle">CNET</span>
          <span class="logo-text">— CNET</span>
        </div>
        <div class="pagination-dots">
          <span class="dot active"></span>
          <span class="dot" v-for="i in 6" :key="i"></span>
        </div>
      </div>
    </section>

    <!-- 品牌介绍模块 (Why Choose isinwheel) -->
    <section class="why-choose-section container">
      <div class="content-wrapper">
        <div class="image-gallery">
          <div class="img-large"></div>
          <div class="img-small"></div>
        </div>
        <div class="text-content">
          <h2 class="section-title">Why Choose isinwheel</h2>
          <p class="description">
            we believe that technology makes communication easier and easier, but it becomes more difficult to connect the people, places, and experiences that are most important to us. isinwheel brings all the things you want closer to you, enriching your life in an easier, cheaper and more interesting way. Your office, the new ramen shop, the friends you are eager to bring to the new ramen shop.
          </p>
          <Button variant="primary" class="btn-brand-story">Brand Story <ArrowRightIcon class="icon-right" /></Button>
        </div>
      </div>
    </section>

    <!-- 用户评价模块 (Customer Reviews) -->
    <section class="customer-reviews-section container">
      <div class="section-header text-center">
        <h2 class="section-title">What The People Say About Isinwheel</h2>
        <p class="subtitle">from 5870 reviews</p>
      </div>
      <div class="reviews-carousel">
        <div class="nav-btn prev"><ChevronLeftIcon /></div>
        <div class="reviews-grid">
            <div class="review-card" v-for="review in customerReviews" :key="review.id">
              <div class="review-img" :style="{ backgroundImage: `url(${review.image})` }"></div>
              <div class="review-content">
                <div class="stars">
                <StarIcon v-for="i in 5" :key="i" class="star-icon filled" />
              </div>
              <h4 class="reviewer-name">
                {{ review.name }} 
                <span v-if="review.verified" class="verified-badge">Verified</span>
              </h4>
              <p class="review-text">{{ review.text }}</p>
            </div>
          </div>
        </div>
        <div class="nav-btn next"><ChevronRightIcon /></div>
      </div>
    </section>

    <!-- 博客文章模块 (Blog Section) -->
    <section class="blog-section container">
      <div class="section-header flex-between">
        <h2 class="section-title blog-title-main">
          isinwheel Blog
          <svg class="wave-underline" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20" preserveAspectRatio="none">
            <path d="M0,10 Q12.5,20 25,10 T50,10 T75,10 T100,10" fill="none" stroke="#58cc02" stroke-width="4" stroke-linecap="round"/>
          </svg>
        </h2>
        <Button variant="outline" class="btn-view-all">
          <FileTextIcon class="icon-left" /> View all
        </Button>
      </div>
      <div class="blog-grid">
        <div class="blog-main">
          <div class="blog-card large">
            <div class="bg-img" :style="{ backgroundImage: `url(${blogs[0].image})` }"></div>
            <div class="overlay"></div>
            <div class="blog-content">
              <div class="meta">
                <span class="date"><CalendarIcon class="meta-icon" /> {{ blogs[0].date }}</span>
                <span class="comments"><MessageCircleIcon class="meta-icon" /> {{ blogs[0].comments }} comments</span>
              </div>
              <h3 class="blog-title">{{ blogs[0].title }}</h3>
              <a href="#" class="read-more">Read more</a>
            </div>
          </div>
        </div>
        <div class="blog-side">
          <div class="blog-card small" v-for="blog in blogs.slice(1)" :key="blog.id">
            <div class="img-wrapper">
              <img :src="blog.image" :alt="blog.title" />
            </div>
            <div class="blog-content">
              <div class="meta">
                <span class="date"><CalendarIcon class="meta-icon" /> {{ blog.date }}</span>
                <span class="comments"><MessageCircleIcon class="meta-icon" /> {{ blog.comments }} comments</span>
              </div>
              <h3 class="blog-title">{{ blog.title }}</h3>
              <a href="#" class="read-more">Read more</a>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, markRaw, onMounted, onUnmounted } from 'vue'
import { 
  ArrowRightIcon, ZapIcon, NavigationIcon, BatteryIcon, ActivityIcon, 
  PlayIcon, PauseIcon, ChevronLeftIcon, ChevronRightIcon, StarIcon, 
  CalendarIcon, MessageCircleIcon, FileTextIcon 
} from 'lucide-vue-next'
import Button from '~/components/Button.vue'
import ProductCard from '~/components/ProductCard.vue'

// 视频播放状态控制
const isPlaying = ref(false)
const videoRef = ref(null)

// 英雄区轮播数据
const heroSlides = ref([
  {
    image: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1920',
    title: '探索无界',
    subtitle: '智能骑行'
  },
  {
    image: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1920',
    title: '极致性能',
    subtitle: '绿色出行'
  },
  {
    image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=1920',
    title: '城市通勤',
    subtitle: '最佳伴侣'
  }
])

const currentHeroIndex = ref(0)

// 英雄区自动轮播
let heroTimer = null
const startHeroTimer = () => {
  heroTimer = setInterval(() => {
    currentHeroIndex.value = (currentHeroIndex.value + 1) % heroSlides.value.length
  }, 5000)
}
const stopHeroTimer = () => {
  if (heroTimer) clearInterval(heroTimer)
}

// 页面加载时启动轮播
onMounted(() => {
  startHeroTimer()
})

onUnmounted(() => {
  stopHeroTimer()
})

const toggleVideo = () => {
  if (!videoRef.value) return
  if (isPlaying.value) {
    videoRef.value.pause()
    isPlaying.value = false
  } else {
    videoRef.value.play()
    isPlaying.value = true
  }
}

// 分类 Mock 数据
const categories = [
  { title: 'Electric Scooter', count: 15, desc: 'Foldable freedom for your daily commute' },
  { title: 'Electric Bike', count: 18, desc: 'Conquer hills and long distances with ease' },
  { title: 'Electric Skateboard', count: 5, desc: 'Electrify your ride with smooth carving and high-speed fun' },
  { title: 'Accessories', count: 333, desc: 'Everything you need to upgrade, protect, and personalize your ride' },
]

// Best Sellers 选项卡
const tabs = ['Electric Scooter', 'Electric Bike', 'E Skateboard']
const currentTab = ref(tabs[0])

// 产品 Mock 数据
const products = ref([
  {
    id: 's9-pro',
    category: 'Electric Scooter',
    title: 'isinwheel S9 Pro Pneumatic Tire Electric Scooter...',
    price: 269.99,
    compareAtPrice: 399.99,
    isFrom: true,
    tags: ['Spring Sale', 'Save $130.00'],
    images: [
      'https://via.placeholder.com/400x400?text=S9+Pro+1',
      'https://via.placeholder.com/400x400?text=S9+Pro+2'
    ],
    appImage: 'https://via.placeholder.com/60x120?text=APP',
    specs: [
      { label: 'Motor Capacity', value: '350W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '19 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '19 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '36V 7.5Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 's-nova-pro',
    category: 'Electric Scooter',
    title: 'S Nova Pro Commuting Electric Scooter...',
    price: 489.99,
    compareAtPrice: 599.99,
    tags: ['NEW', 'Spring Sale', 'Save $110.00'],
    images: [
      'https://via.placeholder.com/400x400?text=S+Nova+Pro+1',
      'https://via.placeholder.com/400x400?text=S+Nova+Pro+2'
    ],
    appImage: 'https://via.placeholder.com/60x120?text=APP',
    specs: [
      { label: 'Max Power', value: '1000W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '38 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '28 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '48V 13Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 'gt1-dual',
    category: 'Electric Scooter',
    title: 'GT1 Dual Motor Off-Road Electric Scooter...',
    price: 649.99,
    compareAtPrice: 799.99,
    tags: ['NEW', 'Spring Sale', 'Save $150.00'],
    images: ['https://via.placeholder.com/400x400?text=GT1'],
    specs: [
      { label: 'Motor Capacity', value: '800W*2', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '35 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '32 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '48V 13Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 'h7pro',
    category: 'Electric Scooter',
    title: 'isinwheel H7Pro 1200W High-End Commuting Electric Scooter...',
    price: 849.99,
    compareAtPrice: 1099.99,
    tags: ['HOT', 'Spring Sale', 'Save $250.00'],
    images: ['https://via.placeholder.com/400x400?text=H7Pro'],
    specs: [
      { label: 'Motor Capacity', value: '1200W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '43 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '38 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Fat Tires', value: '16*4"', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 'u1-bike',
    category: 'Electric Bike',
    title: 'U1 Electric Bike Commuter Ebike...',
    price: 899.99,
    compareAtPrice: 1199.99,
    tags: ['HOT', 'Save $300.00'],
    images: ['https://via.placeholder.com/400x400?text=U1+Bike'],
    specs: [
      { label: 'Motor Capacity', value: '500W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '45 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '20 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '48V 15Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 'v8-skateboard',
    category: 'E Skateboard',
    title: 'V8 Electric Skateboard with Remote...',
    price: 199.99,
    compareAtPrice: 299.99,
    tags: ['Flash Sale'],
    images: ['https://via.placeholder.com/400x400?text=V8+Skateboard'],
    specs: [
      { label: 'Motor Capacity', value: '400W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '12 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '15 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '36V 4Ah', icon: markRaw(BatteryIcon) }
    ]
  }
])

// 过滤后的产品列表
const filteredProducts = computed(() => {
  return products.value.filter(p => p.category === currentTab.value)
})

// 达人视频 Mock 数据
const influencerVideos = ref([
  {
    id: 1,
    bgImage: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=400&h=700',
    avatar: 'https://i.pravatar.cc/150?u=1',
    username: '@ride_master',
    quote: 'Best scooter I have ever ridden! Smooth and fast.',
    tags: ['#escooter', '#cityride']
  },
  {
    id: 2,
    bgImage: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400&h=700',
    avatar: 'https://i.pravatar.cc/150?u=2',
    username: '@urban_explorer',
    quote: 'Perfect for my daily commute. Highly recommend!',
    tags: ['#ebike', '#commute']
  },
  {
    id: 3,
    bgImage: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400&h=700',
    avatar: 'https://i.pravatar.cc/150?u=3',
    username: '@skate_pro',
    quote: 'Incredible speed and stability on this electric skateboard.',
    tags: ['#eskate', '#fun']
  },
  {
    id: 4,
  bgImage: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400&h=700',
    avatar: 'https://i.pravatar.cc/150?u=4',
    username: '@eco_traveler',
    quote: 'A green way to travel around the city.',
    tags: ['#ecofriendly', '#travel']
  }
])

// 用户评价 Mock 数据
const customerReviews = ref([
  {
    id: 1,
    image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400',
    name: 'Andrew',
    verified: false,
    text: 'I have this a 4 before because the shocks were tight. After playing around a bit I now understand why they are so tight. It is easier to loosen the tension...'
  },
  {
    id: 2,
    image: 'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=400',
    name: 'Joseph P.',
    verified: true,
    text: 'This board is amazing!! I could go on & on about how the specs really live up to the description. Just a monster of a board & the power is perfect &...'
  },
  {
    id: 3,
    image: 'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=400',
    name: 'Pamela',
    verified: false,
    text: 'I purchased a gt4 electric scooter. The assembly was extremely easy and it was completely put together in less than 30 minutes. I love the way...'
  },
  {
    id: 4,
    image: 'https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=400',
    name: 'Chad S.',
    verified: false,
    text: 'It\'s everything you need to piss off people in town. If you keep it slow you can get 40-45 miles on a single charge which is boring. Top speed is...'
  }
])

// 博客文章 Mock 数据
const blogs = ref([
  {
    id: 1,
    image: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800',
    title: 'Best Suspension for Value Off-Road Scooters: What Matters on Rough Paths',
    date: 'Mar 26, 2026',
    comments: 0
  },
  {
    id: 2,
    image: 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=400',
    title: '3 Best Value-for-Money Electric Scooters with the Best Off-Road Suspension',
    date: 'Mar 17, 2026',
    comments: 0
  },
  {
    id: 3,
    image: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=400',
    title: 'Neighborhood Errands Under $500: 4 Best Scooters for Families Who Want an Easy-to-Ride Vehicle',
    date: 'Mar 11, 2026',
    comments: 0
  }
])
</script>

<style lang="scss" scoped>
.home-page {
  padding-bottom: 60px;
}

// 模块通用标题
.section-title {
  font-size: 40px;
  font-weight: 800;
  margin-bottom: 30px;
}

// 首屏 Banner 样式 - 满铺视窗模块
.hero-section {
  position: relative;
  min-height: 85vh; // 全屏高度响应式，稍微减少一点底部空间
  display: flex;
  align-items: center;
  background-color: $bg-light;
  overflow: hidden;
  margin-bottom: $spacing-xl;
  padding: 40px 0; // 增加内边距

  // 轮播容器
  .hero-carousel {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;

    // 轮播幻灯片
    .hero-slide {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      opacity: 0;
      transition: opacity 1s ease;

      &.is-active {
        opacity: 1;
      }

      .hero-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-size: cover;
        background-position: center;
        border-radius: 24px; // 圆角效果
      }
    }

    // 轮播指示器
    .hero-indicators {
      position: absolute;
      bottom: 40px;
      right: 20%;
      display: flex;
      gap: 12px;
      z-index: 10;

      .indicator-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.4);
        cursor: pointer;
        transition: all 0.3s ease;

        &.is-active {
            width: 20px;
        height: 8px;
        border-radius: 50px;
          background: #fff;
          transform: scale(1.2);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
        }

        &:hover:not(.is-active) {
          background: rgba(255, 255, 255, 0.7);
        }
      }
    }

    // 半透明遮罩，增强文字对比度
    .hero-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(to right, rgba(0, 0, 0, 0.75) 0%, rgba(0, 0, 0, 0.3) 100%);
      border-radius: 24px; // 保持圆角
    }
  }

  // 主要内容容器
  .hero-content {
    position: relative;
    z-index: 2;
    width: 100%;
    color: $white; // 文字使用白色
    padding: 0 $spacing-md;
    

    .text-content {
      max-width: 600px;
      animation: fadeInUp 1s ease-out forwards; // 添加入场动画

      .title {
        font-size: 48px;
        font-weight: 800;
        line-height: 1.2;
        margin-bottom: $spacing-md;
        letter-spacing: 2px;
        
        // 高亮文字
        .highlight {
          color: $secondary-color;
        }
      }

      .subtitle {
        font-size: 18px;
        color: rgba($white, 0.9); // 半透明白色
        margin-bottom: $spacing-xl;
        line-height: 1.6;
      }

      // 按钮组排版
      .action-group {
        display: flex;
        gap: $spacing-md;
        
        // 响应式：在小屏幕上按钮垂直排列
        @media (max-width: 768px) {
          flex-direction: column;
        }
      }
    }
  }

  // 响应式断点适配 (使用在变量中定义的 768 / 1024 / 1440)
  @media (min-width: $bp-md) {
    .hero-content {
      .text-content {
        .title { font-size: 56px; }
        .subtitle { font-size: 20px; }
      }
    }
  }
  
  @media (min-width: $bp-lg) {
    .hero-content {
      .text-content {
        .title { font-size: 72px; }
        .subtitle { font-size: 24px; }
      }
    }
  }
  
  @media (min-width: $bp-xl) {
    .hero-content {
      .text-content {
        max-width: 800px;
        .title { font-size: 88px; }
        .subtitle { font-size: 28px; }
      }
    }
  }
}

// 定义入场动画关键帧
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 探索模块 (Explore Categories) 样式
.explore-section {
  margin-bottom: 80px;

  .explore-title {
    font-size: 48px;
    font-weight: 800;
    margin-bottom: 40px;
    i {
      color: $text-color;
      font-style: normal;
      position: relative;
      display: inline-block;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -8px;
        left: 0;
        width: 100%;
        height: 16px;
        background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20" preserveAspectRatio="none"><path d="M5,15 Q25,5 50,15 T95,10" fill="none" stroke="%2358cc02" stroke-width="4" stroke-linecap="round"/></svg>') no-repeat center/100% 100%;
      }
    }
  }

  // 网格布局容器
  .category-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px; // 增加间距

    // 单个图文卡片
    .category-card {
      position: relative;
      height: 320px; // 增加高度
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      color: $white;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); // 添加基础阴影
      transition: all 0.4s ease; // 整体过渡效果

      // 占位背景图
      .bg-placeholder {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94); // 平滑缩放动画
      }

      // 为不同的卡片配置不同的背景图，增加区分度
      &:nth-child(1) .bg-placeholder { background: url('https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=600') center/cover; }
      &:nth-child(2) .bg-placeholder { background: url('https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=600') center/cover; }
      &:nth-child(3) .bg-placeholder { background: url('https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=600') center/cover; }
      &:nth-child(4) .bg-placeholder { background: url('https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=600') center/cover; }

      // 内容层
      .content {
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        padding: 30px 24px 24px;
        background: linear-gradient(to top, rgba(0,0,0,0.85) 0%, rgba(0,0,0,0.4) 60%, transparent 100%);
        transition: all 0.4s ease;
        
        // 标题样式
        h3 {
          font-size: 28px;
          font-weight: 700;
          margin-bottom: 0; // 初始无下边距
          display: flex;
          align-items: flex-start;
          gap: 4px;
          transform: translateY(20px); // 初始下移
          transition: transform 0.4s ease;

          // 数量角标
          .count {
            font-size: 12px;
            font-weight: 600;
            color: rgba(255, 255, 255, 0.8);
            margin-top: 4px; // 模拟上标
          }
        }

        // 描述文本样式
        p {
          font-size: 15px;
          color: rgba(255, 255, 255, 0.8);
          line-height: 1.5;
          margin-bottom: 0;
          opacity: 0; // 初始隐藏
          transform: translateY(20px); // 初始下移
          transition: all 0.4s ease;
          max-height: 0; // 初始不占高度
        }

        // 箭头图标
        .icon {
          position: absolute;
          bottom: 24px;
          right: 24px;
          opacity: 0; // 初始隐藏
          transform: translateX(-10px); // 初始左移
          transition: all 0.4s ease;
          color: #58cc02; // 使用主题色高亮图标
        }
      }

      // 悬浮动效 (Hover Effects)
      &:hover {
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2); // 悬浮加深阴影
        transform: translateY(-5px); // 整体轻微上浮

        // 背景图放大
        .bg-placeholder {
          transform: scale(1.1);
        }

        // 内容区渐变加深
        .content {
          background: linear-gradient(to top, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.6) 70%, transparent 100%);
          
          // 标题复位
          h3 {
            transform: translateY(0);
          }

          // 描述文本显示并复位
          p {
            opacity: 1;
            transform: translateY(0);
            max-height: 60px; // 展开高度
            margin-top: 12px;
          }

          // 图标显示并向右平移
          .icon {
            opacity: 1;
            transform: translateX(0);
          }
        }
      }
    }
  }

  // 响应式适配
  @media (max-width: $bp-lg) {
    .category-grid {
      grid-template-columns: repeat(2, 1fr); // 平板显示2列
    }
  }

  @media (max-width: $bp-md) {
    .category-grid {
      grid-template-columns: 1fr; // 手机端显示1列
      
      .category-card {
        height: 280px; // 手机端稍微减小高度
        
        // 手机端默认显示部分悬浮效果，避免无法悬浮查看
        .content {
          h3 { transform: translateY(0); }
          p { 
            opacity: 1; 
            transform: translateY(0); 
            max-height: 60px;
            margin-top: 12px;
          }
          .icon { 
            opacity: 1; 
            transform: translateX(0); 
          }
        }
      }
    }
  }
}

// 视频介绍模块样式
.video-section {
  margin-bottom: 80px;
  max-width: $max-width;

  // 视频容器
  .video-container {
    position: relative;
    width: 100%;
    // 设置默认宽高比为 16:9
    aspect-ratio: 16 / 9;
    border-radius: 24px;
    overflow: hidden;
    cursor: pointer;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
    background-color: #000;

    // 自适应大屏，使用更宽的比例
    @media (min-width: $bp-xl) {
      aspect-ratio: 21 / 9;
    }

    // 视频元素
    .promo-video {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    // 视频封面图
    .video-cover {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      transition: opacity 0.5s ease;
      z-index: 1;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      // 播放时隐藏封面
      &.is-hidden {
        opacity: 0;
        pointer-events: none;
      }
    }

    // 播放控件覆盖层
    .video-controls {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 2;
      background: rgba(0, 0, 0, 0.3); // 半透明遮罩
      transition: all 0.3s ease;

      // 播放/暂停按钮
      .play-pause-btn {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(8px); // 磨砂玻璃效果
        border: 2px solid rgba(255, 255, 255, 0.6);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        cursor: pointer;
        transition: all 0.3s ease;
        padding: 0; // 移除默认内边距

        .icon {
          width: 32px;
          height: 32px;
          
          // 播放图标稍微偏右以居中视觉效果
          &.play-icon {
            margin-left: 4px;
          }
          
          &.pause-icon {
            margin-left: 0;
          }
        }

        // 按钮悬浮效果
        &:hover {
          background: rgba(255, 255, 255, 0.4);
          transform: scale(1.1);
        }
      }

      // 播放时样式变化
      &.is-playing {
        background: transparent;
        opacity: 0; // 默认隐藏控件

        // 悬浮时显示控件
        &:hover {
          opacity: 1;
          background: rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
}

// 明星产品区块样式
.best-sellers-section {
  margin-bottom: 80px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;

    .section-title {
      margin-bottom: 0;
    }

    .view-all-link {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: $text-color;
      text-decoration: none;
      transition: color 0.3s ease;

      &:hover {
        color: #58cc02;
        .icon-right {
          transform: translateX(4px);
        }
      }

      .icon-right {
        width: 16px;
        height: 16px;
        transition: transform 0.3s ease;
      }
    }
  }

  // 选项卡样式
  .tabs-wrapper {
    display: flex;
    justify-content: center;
    margin-bottom: 40px;

    .tabs {
      display: inline-flex;
      background: $bg-light;
      border-radius: 40px;
      padding: 4px;

      // 单个选项卡按钮
      .tab-btn {
        padding: 12px 32px;
        border-radius: 36px;
        font-weight: 600;
        font-size: 16px;
        background: transparent;
        color: $text-light;
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;

        // 激活与悬停状态
        &.active, &:hover {
          background: $primary-color;
          color: $white;
          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
      }
    }
  }

  // 产品网格样式
  .product-grid {
    display: grid;
    gap: 24px;
    
    // 默认手机端显示 1 列
    grid-template-columns: repeat(1, 1fr);

    // 平板及以上显示 2 列
    @media (min-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }

    // 适配 1024px 断点 (显示 3 列)
    @media (min-width: $bp-lg) {
      grid-template-columns: repeat(3, 1fr);
    }

    // 适配 1440px 断点 (显示 4 列)
    @media (min-width: $bp-xl) {
      grid-template-columns: repeat(4, 1fr);
      gap: 32px;
    }
  }
}

// 达人视频展示区样式
.influencer-section {
  margin-bottom: 80px;
  max-width: $max-width;

  // 网格布局，适配不同断点
  .influencer-grid {
    display: grid;
    gap: 20px;
    grid-template-columns: repeat(2, 1fr); // 手机端默认 2 列

    @media (min-width: $bp-md) {
      grid-template-columns: repeat(3, 1fr); // 平板 3 列
    }

    @media (min-width: $bp-lg) {
      grid-template-columns: repeat(4, 1fr); // 桌面 4 列
    }

    // @media (min-width: $bp-xl) {
    //   grid-template-columns: repeat(5, 1fr); // 大屏 5 列
    // }

    // 单个视频卡片
    .video-card {
      position: relative;
      width: 100%;
      aspect-ratio: 9 / 16; // 垂直比例，类似 TikTok
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease, box-shadow 0.3s ease; // 卡片整体过渡效果

      // 悬浮动效
      &:hover {
        transform: translateY(-8px); // 悬浮上移
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2); // 悬浮阴影加深

        .bg-image {
          transform: scale(1.05); // 背景图轻微放大
        }

        .play-btn {
          transform: translate(-50%, -50%) scale(1.1); // 播放按钮放大，注意保留 translate 居中
          background: rgba(255, 255, 255, 0.3); // 按钮背景变亮
        }
      }

      // 背景图片
      .bg-image {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-size: cover;
        background-position: center;
        transition: transform 0.5s ease; // 背景放大过渡效果
      }

      // 渐变遮罩，为了让底部文字清晰可见
      .overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.2) 50%, transparent 100%);
      }

      // 中心播放按钮
      .play-btn {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%); // 居中
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(4px);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease; // 播放按钮过渡效果

        .icon {
          color: $white;
          width: 24px;
          height: 24px;
          margin-left: 3px; // 调整三角形居中
        }
      }

      // 底部内容区
      .card-content {
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        padding: 16px;
        color: $white;
        display: flex;
        flex-direction: column;
        gap: 8px;

        // 用户信息区
        .user-info {
          display: flex;
          align-items: center;
          gap: 8px;

          .avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            border: 2px solid rgba(255, 255, 255, 0.8);
            object-fit: cover;
          }

          .username {
            font-size: 14px;
            font-weight: 600;
            text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
          }
        }

        // 用户引言
        .quote {
          font-size: 13px;
          line-height: 1.4;
          margin: 0;
          opacity: 0.9;
          display: -webkit-box;
          -webkit-line-clamp: 2; // 最多显示两行
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        // 标签区
        .tags {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;

          .tag {
            font-size: 12px;
            color: $secondary-color; // 使用辅助色高亮标签
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 媒体评价模块样式
.media-review-section {
  position: relative;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 80px;
  color: $white;

  .media-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: url('https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1920') center/cover fixed; // 视差效果
    z-index: 1;

    .overlay {
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.7); // 较深的遮罩
    }
  }

  .media-content {
    position: relative;
    z-index: 2;
    max-width: 800px;
    padding: 60px 20px;

    .quote-icon {
      font-size: 80px;
      line-height: 1;
      font-family: Georgia, serif;
      color: $secondary-color;
      margin-bottom: 20px;
    }

    .review-title {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.4;
      margin-bottom: 40px;

      @media (min-width: $bp-md) {
        font-size: 36px;
      }
    }

    .media-logo {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      margin-bottom: 40px;

      .logo-circle {
        width: 60px;
        height: 60px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 16px;
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: $danger-color; // CNET red
      }

      .logo-text {
        font-size: 16px;
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .pagination-dots {
      display: flex;
      justify-content: center;
      gap: 8px;

      .dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.3);
        cursor: pointer;
        transition: all 0.3s ease;

        &.active, &:hover {
          background: $white;
          transform: scale(1.2);
        }
      }
    }
  }
}

// 品牌介绍模块样式
.why-choose-section {
  margin-bottom: 80px;
  max-width: $max-width;
  .content-wrapper {
    display: flex;
    flex-direction: column;
    gap: 20px;
    align-items: center;

    @media (min-width: $bp-lg) {
      flex-direction: row;
      gap: 80px;
    }
  }

  .image-gallery {
    flex: 1;
    position: relative;
    width: 100%;
    min-height: 500px; // 增加高度以容纳倾斜的图片

    .img-large {
      position: absolute;
      top: 5%;
      right: 15%;
      width: 65%;
      height: 80%;
      border-radius: 16px;
      background: url('https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800') center/cover;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
      transform: rotate(6deg);
      z-index: 1;
      transition: transform 0.5s ease;
      
      &:hover {
        transform: rotate(2deg) scale(1.02);
      }
    }

    .img-small {
      position: absolute;
      bottom: 15%;
      left: 10%;
      width: 50%;
      height: 55%;
      border-radius: 12px;
      background: url('https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=600') center/cover;
      border: 6px solid $white;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
      z-index: 2;
      transform: rotate(-4deg);
      transition: transform 0.5s ease;
      
      &:hover {
        transform: rotate(-1deg) scale(1.05);
      }
    }
  }

  .text-content {
    flex: 1;

    .section-title {
      font-size: 36px;
      font-weight: 800;
      margin-bottom: 24px;
      line-height: 1.2;
      color: #58cc02; // 标题整体变绿

      @media (min-width: $bp-lg) {
        font-size: 48px;
      }
    }

    .description {
      font-size: 16px;
      line-height: 1.8;
      color: $text-light;
      margin-bottom: 32px;
    }

    .btn-brand-story {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 12px 32px;
      border-radius: 30px;
      font-weight: 600;
      background-color: #111; // 黑色背景
      color: $white;
      border: none;
      
      &:hover {
        background-color: #333;
        .icon-right {
          transform: translateX(4px);
        }
      }

      .icon-right {
        transition: transform 0.3s ease;
        width: 16px;
        height: 16px;
      }
    }
  }
}

// 用户评价模块样式
.customer-reviews-section {
  margin-bottom: 80px;
  max-width: $max-width;
  .section-header {
    margin-bottom: 40px;
    
    .section-title {
      font-size: 32px;
      margin-bottom: 8px;
      
      @media (min-width: $bp-md) {
        font-size: 40px;
      }
    }
    
    .subtitle {
      color: $text-light;
      font-size: 16px;
    }
  }

  .reviews-carousel {
    display: flex;
    align-items: center;
    gap: 20px;
    position: relative;

    .nav-btn {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      background: $white;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: $secondary-color;
      transition: all 0.3s ease;
      z-index: 2;

      &:hover {
        background: $secondary-color;
        color: $white;
      }
      
      // 移动端隐藏按钮，改用滑动
      @media (max-width: $bp-lg) {
        display: none;
      }
    }

    .reviews-grid {
      display: flex;
      gap: 24px;
      overflow-x: auto;
      padding: 20px 0;
      scroll-snap-type: x mandatory;
      scrollbar-width: none; // Firefox
      &::-webkit-scrollbar { display: none; } // Chrome/Safari
      flex: 1;

      .review-card {
        flex: 0 0 100%;
        scroll-snap-align: start;
        background: $white;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        border: 1px solid $border-color;

        @media (min-width: $bp-md) {
          flex: 0 0 calc(50% - 12px);
        }

        @media (min-width: $bp-lg) {
          flex: 0 0 calc(25% - 18px);
        }

        .review-img {
          width: 100%;
          height: 200px;
          background-size: cover;
          background-position: center;
        }

        .review-content {
          padding: 24px;
          display: flex;
          flex-direction: column;
          gap: 12px;

          .stars {
            display: flex;
            gap: 4px;
            color: #ffc107; // 星星黄色
            
            .star-icon {
              width: 16px;
              height: 16px;
              fill: currentColor;
            }
          }

          .reviewer-name {
            font-size: 16px;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 8px;

            .verified-badge {
              font-size: 10px;
              background: #000;
              color: #fff;
              padding: 2px 6px;
              border-radius: 4px;
              font-weight: 500;
            }
          }

          .review-text {
            font-size: 14px;
            color: $text-light;
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 4;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
        }
      }
    }
  }
}

// 博客文章模块样式
.blog-section {
  margin-bottom: 80px;
  max-width: $max-width;
  .flex-between {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 40px;
    flex-wrap: wrap;
    gap: 16px;
  }

  .blog-title-main {
    margin-bottom: 0;
    position: relative;
    display: inline-block;

    .wave-underline {
      position: absolute;
      bottom: -12px;
      left: 0;
      width: 100%;
      height: 12px;
    }
  }

  .btn-view-all {
    display: flex;
    align-items: center;
    gap: 8px;
    border-radius: 30px;
  }

  .blog-grid {
    display: grid;
    gap: 24px;
    
    @media (min-width: $bp-lg) {
      grid-template-columns: 1.5fr 1fr; // 左侧大图，右侧列表
    }

    .blog-card {
      border-radius: 20px;
      overflow: hidden;
      position: relative;
      cursor: pointer;

      .meta {
        display: flex;
        gap: 16px;
        font-size: 12px;
        margin-bottom: 12px;
        align-items: center;
        
        .meta-icon {
          width: 14px;
          height: 14px;
          margin-right: 4px;
          vertical-align: middle;
        }
      }

      .read-more {
        font-size: 14px;
        font-weight: 600;
        text-decoration: underline;
        text-underline-offset: 4px;
        display: inline-block;
        margin-top: 16px;
        transition: color 0.3s ease;
      }

      // 大卡片样式
      &.large {
        height: 400px;
        @media (min-width: $bp-lg) {
          height: 100%;
          min-height: 500px;
        }

        .bg-img {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-size: cover;
          background-position: center;
          transition: transform 0.5s ease;
        }

        .overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 100%);
        }

        .blog-content {
          position: absolute;
          bottom: 0;
          left: 0;
          width: 100%;
          padding: 40px;
          color: $white;
        }

        .blog-title {
          font-size: 24px;
          font-weight: 700;
          line-height: 1.3;
          @media (min-width: $bp-md) {
            font-size: 32px;
          }
        }

        .read-more {
          color: $white;
          &:hover { color: $secondary-color; }
        }

        &:hover .bg-img {
          transform: scale(1.05);
        }
      }

      // 小卡片样式
      &.small {
        display: flex;
        flex-direction: column;
        background: $white;
        border: 1px solid $border-color;
        transition: box-shadow 0.3s ease, transform 0.3s ease;
        height: 100%;

        @media (min-width: $bp-md) {
          flex-direction: row; // 平板以上横向排列
          align-items: center;
        }

        @media (min-width: $bp-lg) {
          flex-direction: column; // 桌面端恢复纵向或根据空间调整
        }

        @media (min-width: $bp-xl) {
          flex-direction: row; // 大屏横向
        }

        &:hover {
          box-shadow: 0 10px 20px rgba(0,0,0,0.05);
          transform: translateY(-4px);
        }

        .img-wrapper {
          flex: 0 0 200px;
          height: 200px;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s ease;
          }
        }

        .blog-content {
          padding: 24px;
          flex: 1;

          .meta {
            color: $text-light;
          }

          .blog-title {
            font-size: 18px;
            font-weight: 700;
            color: $text-color;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .read-more {
            color: $text-color;
            &:hover { color: $secondary-color; }
          }
        }

        &:hover .img-wrapper img {
          transform: scale(1.05);
        }
      }
    }
    
    .blog-side {
      display: flex;
      flex-direction: column;
      gap: 24px;
    }
  }
}
</style>
