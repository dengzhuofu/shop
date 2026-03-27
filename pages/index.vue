<template>
  <div class="home-page">
    <!-- 首屏大图轮播 (Hero Banner) -->
    <section class="hero-section">
      <div class="container hero-content">
        <div class="text-content">
          <h1 class="title">Ride Into Spring</h1>
          <p class="subtitle">Thoughtfully designed e-rides for brighter days ahead</p>
          <Button variant="primary" size="lg">Find Your Ride</Button>
        </div>
      </div>
      <!-- Hero 背景图，此处使用 CSS 占位 -->
      <div class="hero-bg"></div>
    </section>

    <!-- 分类导览 (Explore Categories) -->
    <section class="explore-section container">
      <h2 class="section-title">Explore <i>isinwheel</i></h2>
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
      <h2 class="section-title">Best Sellers</h2>
      
      <!-- 选项卡 -->
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

      <!-- 产品网格 -->
      <div class="product-grid">
        <ProductCard 
          v-for="product in products" 
          :key="product.id"
          :product="product"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, markRaw } from 'vue'
import { ArrowRightIcon, ZapIcon, NavigationIcon, BatteryIcon, ActivityIcon } from 'lucide-vue-next'
import Button from '~/components/Button.vue'
import ProductCard from '~/components/ProductCard.vue'

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
    title: 'isinwheel S9 Pro Pneumatic Tire Electric Scooter...',
    price: 269.99,
    compareAtPrice: 399.99,
    isFrom: true,
    tags: ['Spring Sale', 'Save $130.00'],
    image: 'https://via.placeholder.com/400x400?text=S9+Pro',
    specs: [
      { label: 'Motor Capacity', value: '350W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '19 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '19 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '36V 7.5Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 's-nova-pro',
    title: 'S Nova Pro Commuting Electric Scooter...',
    price: 489.99,
    compareAtPrice: 599.99,
    tags: ['NEW', 'Spring Sale', 'Save $110.00'],
    image: 'https://via.placeholder.com/400x400?text=S+Nova+Pro',
    specs: [
      { label: 'Max Power', value: '1000W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '38 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '28 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '48V 13Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 'gt1-dual',
    title: 'GT1 Dual Motor Off-Road Electric Scooter...',
    price: 649.99,
    compareAtPrice: 799.99,
    tags: ['NEW', 'Spring Sale', 'Save $150.00'],
    image: 'https://via.placeholder.com/400x400?text=GT1',
    specs: [
      { label: 'Motor Capacity', value: '800W*2', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '35 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '32 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Battery Capacity', value: '48V 13Ah', icon: markRaw(BatteryIcon) }
    ]
  },
  {
    id: 'h7pro',
    title: 'isinwheel H7Pro 1200W High-End Commuting Electric Scooter...',
    price: 849.99,
    compareAtPrice: 1099.99,
    tags: ['HOT', 'Spring Sale', 'Save $250.00'],
    image: 'https://via.placeholder.com/400x400?text=H7Pro',
    specs: [
      { label: 'Motor Capacity', value: '1200W', icon: markRaw(ZapIcon) },
      { label: 'Max Range', value: '43 Miles', icon: markRaw(NavigationIcon) },
      { label: 'Top Speed', value: '38 MPH', icon: markRaw(ActivityIcon) },
      { label: 'Fat Tires', value: '16*4"', icon: markRaw(BatteryIcon) }
    ]
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
  i {
    color: #58cc02;
    font-style: italic;
  }
}

// 首屏 Banner 样式
.hero-section {
  position: relative;
  height: 600px;
  display: flex;
  align-items: center;
  background-color: #f0f0f0;
  overflow: hidden;
  border-radius: 0 0 40px 40px;
  margin-bottom: 60px;

  .hero-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    // 模拟左侧透明到右侧实色的渐变背景
    background: linear-gradient(to right, rgba(255,255,255,0.9) 0%, rgba(255,255,255,0) 50%), url('https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1920') center/cover;
    z-index: 1;
  }

  .hero-content {
    position: relative;
    z-index: 2;
    width: 100%;

    .text-content {
      max-width: 500px;

      .title {
        font-size: 56px;
        font-weight: 800;
        line-height: 1.1;
        margin-bottom: 16px;
      }

      .subtitle {
        font-size: 18px;
        color: #555;
        margin-bottom: 32px;
      }
    }
  }
}

// 分类导览样式
.explore-section {
  margin-bottom: 80px;

  .category-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .category-card {
      position: relative;
      height: 300px;
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      color: $white;

      .bg-placeholder {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: #333;
        transition: transform 0.3s ease;
      }

      // 根据索引给占位图不同的颜色，增加区分度
      &:nth-child(1) .bg-placeholder { background: url('https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=600') center/cover; }
      &:nth-child(2) .bg-placeholder { background: url('https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=600') center/cover; }
      &:nth-child(3) .bg-placeholder { background: url('https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=600') center/cover; }
      &:nth-child(4) .bg-placeholder { background: url('https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=600') center/cover; }

      &:hover {
        .bg-placeholder {
          transform: scale(1.05);
        }
      }

      .content {
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        padding: 24px;
        background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
        
        h3 {
          font-size: 24px;
          font-weight: bold;
          margin-bottom: 8px;
          display: flex;
          align-items: center;
          gap: 8px;

          .count {
            font-size: 12px;
            vertical-align: super;
            opacity: 0.8;
          }
        }

        p {
          font-size: 14px;
          opacity: 0.9;
          margin-bottom: 16px;
        }

        .icon {
          position: absolute;
          bottom: 24px;
          right: 24px;
        }
      }
    }
  }
}

// 明星产品区块样式
.best-sellers-section {
  
  .tabs {
    display: flex;
    gap: 12px;
    margin-bottom: 30px;

    .tab-btn {
      padding: 10px 24px;
      border-radius: 30px;
      font-weight: 600;
      font-size: 15px;
      background: #f5f5f5;
      color: #333;
      transition: all 0.3s ease;

      &.active, &:hover {
        background: #111;
        color: #fff;
      }
    }
  }

  .product-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
  }
}
</style>
