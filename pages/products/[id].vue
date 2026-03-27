<template>
  <div class="product-detail-page">
    <div class="container main-content">
      <!-- 面包屑导航 -->
      <!-- <nav class="breadcrumb">
        <NuxtLink to="/">Home</NuxtLink>
        <span class="separator">/</span>
        <NuxtLink to="/collections/electric-scooter">Electric Scooter</NuxtLink>
        <span class="separator">/</span>
        <span class="current">{{ product.title }}</span>
      </nav> -->

      <div class="product-layout">
        <!-- 左侧图片区域 (Sticky 粘性定位) -->
        <div class="product-media">
          <div class="media-sticky-wrapper">
            <div class="main-image-container">
              <!-- 左上角和右上角的徽章 -->
              <div class="tags-left" v-if="product.tags && product.tags.includes('NEW')">
                <span class="tag-label new">NEW</span>
              </div>
              <div class="tags-right" v-if="product.tags && product.tags.includes('Spring Sale')">
                <div class="spring-sale-badge">
                  <span class="text">Spring<br>Sale</span>
                </div>
              </div>

              <!-- 主图 -->
              <img :src="product.images[activeImageIndex]" :alt="product.title" class="main-image" />

              <!-- App 预览图标 -->
              <!-- <img v-if="product.appImage" :src="product.appImage" class="app-preview" alt="App Support" /> -->
            </div>

            <!-- 缩略图列表 -->
            <div class="thumbnails">
              <button class="nav-btn prev">
                <ChevronLeftIcon />
              </button>
              <div class="thumbs-list">
                <button v-for="(img, index) in product.images" :key="index" class="thumbnail-btn"
                  :class="{ 'is-active': activeImageIndex === index }" @click="activeImageIndex = index">
                  <img :src="img" :alt="`${product.title} thumbnail ${index + 1}`" />
                </button>
              </div>
              <button class="nav-btn next">
                <ChevronRightIcon />
              </button>
            </div>

            <!-- 媒体切换按钮 -->
            <div class="media-toggles">
              <button class="toggle-btn active">
                <CameraIcon class="icon" /> Photo
              </button>
              <button class="toggle-btn">
                <VideoIcon class="icon" /> Video
              </button>
            </div>

            <!-- 核心参数区 -->
            <div class="key-specs">
              <div class="spec-item" v-for="spec in product.specs" :key="spec.label">
                <component :is="spec.icon" class="spec-icon" />
                <div class="spec-text">
                  <span class="value">{{ spec.value }}</span>
                  <span class="label">{{ spec.label }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧信息区域 (Scrollable 可滚动) -->
        <div class="product-info">
          <!-- 标签 -->
          <div class="product-badges">
            <span class="badge success">Ride Deals</span>
            <span class="badge danger">Fresh Rides, Fresh Start</span>
          </div>

          <!-- 标题与评价 -->
          <h1 class="product-title">{{ product.title }}</h1>

          <div class="price-review-row">
            <div class="price-area">
              <span class="current-price">${{ product.price }}</span>
              <span class="old-price" v-if="product.compareAtPrice">${{ product.compareAtPrice }}</span>
              <span class="save-badge" v-if="product.compareAtPrice">
                Save ${{ (product.compareAtPrice - product.price).toFixed(2) }}
              </span>
            </div>
          </div>

          <div class="review-stars">
            <div class="stars">
              <StarIcon v-for="i in 5" :key="i" class="star-icon filled" />
            </div>
            <span class="review-count">61 reviews</span>
          </div>

          <!-- 分期付款提示 -->
          <div class="installment-info">
            <p>4 interest-free installments, or from <strong>$44.23</strong>/mo with <span class="shop-pay">shop
                pay</span> <a href="#" class="check-link">Check your purchasing power</a></p>
          </div>

          <!-- 规格选择 (Variants) -->
          <div class="variant-selectors">
            <!-- 款式 (Style) 选择 -->
            <div class="selector-group" v-if="product.styles && product.styles.length">
              <p class="selector-label">Style: <strong>{{ selectedStyle }}</strong></p>
              <div class="options-list">
                <button 
                  v-for="style in product.styles" 
                  :key="style"
                  class="text-btn"
                  :class="{ 'is-active': selectedStyle === style }"
                  @click="selectedStyle = style"
                >
                  {{ style }}
                </button>
              </div>
            </div>

            <!-- 套餐/数量 (Buy More Save More) 选择 -->
            <div class="selector-group" v-if="product.bundles && product.bundles.length">
              <p class="selector-label">Buy More Save More: <strong>{{ selectedBundle }}</strong></p>
              <div class="options-list">
                <button 
                  v-for="bundle in product.bundles" 
                  :key="bundle"
                  class="text-btn"
                  :class="{ 'is-active': selectedBundle === bundle }"
                  @click="selectedBundle = bundle"
                >
                  {{ bundle }}
                </button>
              </div>
            </div>

            <!-- 颜色选择 (保留原有逻辑，如果有颜色配置的话) -->
            <div class="selector-group" v-if="product.colors && product.colors.length">
              <p class="selector-label">Color: <strong>{{ selectedColor }}</strong></p>
              <div class="color-options">
                <button v-for="color in product.colors" :key="color.name" class="color-btn"
                  :class="{ 'is-active': selectedColor === color.name }" @click="selectedColor = color.name">
                  <img :src="color.thumbnail" :alt="color.name" />
                </button>
              </div>
            </div>
          </div>

          <!-- 数量与加入购物车 -->
          <div class="add-to-cart-section">
            <div class="quantity-selector">
              <button class="qty-btn" @click="quantity > 1 && quantity--">
                <MinusIcon class="icon" />
              </button>
              <input type="number" v-model="quantity" min="1" class="qty-input" />
              <button class="qty-btn" @click="quantity++">
                <PlusIcon class="icon" />
              </button>
            </div>
            <button class="btn-add-to-cart">Add to cart</button>
          </div>

          <!-- 促销配件 (Upsell) -->
          <div class="upsell-section">
            <h3 class="upsell-title">
              <ZapIcon class="icon-zap" /> Spring Sale
            </h3>

            <div class="upsell-item" v-for="item in product.upsells" :key="item.id">
              <div class="item-info">
                <img :src="item.image" :alt="item.name" class="item-img" />
                <span class="item-name">{{ item.name }}</span>
              </div>
              <div class="item-price">
                <span class="free-price">$0.00</span>
                <span class="old-price">${{ item.value }}</span>
              </div>
            </div>
          </div>

          <!-- 延长保修 -->
          <div class="protection-plan">
            <div class="plan-header">
              <span>Add Protect+ Plan including Accident Protection</span>
              <InfoIcon class="icon-info" />
            </div>
            <div class="plan-options">
              <button class="plan-btn">
                <span class="plan-name">1 Year Extended Warranty</span>
                <span class="plan-price">$73.99</span>
              </button>
              <button class="plan-btn active">
                <span class="plan-name">2 Year Extended Warranty</span>
                <span class="plan-price">$99.99</span>
              </button>
            </div>
          </div>

          <!-- 折叠信息面板 (Accordions) -->
          <div class="accordion-group">
            <!-- Quick Know 面板 -->
            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'quick-know' }">
              <button class="accordion-header" @click="toggleAccordion('quick-know')">
                <span>Quick Know</span>
                <ChevronUpIcon v-if="activeAccordion === 'quick-know'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div class="accordion-content" v-show="activeAccordion === 'quick-know'">
                <ul class="feature-list">
                  <li v-for="(feature, index) in product.features" :key="index">
                    <CheckSquareIcon class="icon-check" />
                    <span v-html="feature"></span>
                  </li>
                </ul>
              </div>
            </div>

            <!-- Specification 面板 -->
            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'specification' }">
              <button class="accordion-header" @click="toggleAccordion('specification')">
                <span>Specification</span>
                <ChevronUpIcon v-if="activeAccordion === 'specification'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div class="accordion-content" v-show="activeAccordion === 'specification'">
                <div class="specs-table">
                  <div class="spec-row" v-for="spec in product.specs" :key="spec.label">
                    <div class="spec-label">{{ spec.label }}</div>
                    <div class="spec-value">{{ spec.value }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- What's in the Box 面板 -->
            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'in-the-box' }">
              <button class="accordion-header" @click="toggleAccordion('in-the-box')">
                <span>What's in the Box</span>
                <ChevronUpIcon v-if="activeAccordion === 'in-the-box'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div class="accordion-content" v-show="activeAccordion === 'in-the-box'">
                <ul class="box-list">
                  <li>1 x S Nova Pro Electric Scooter</li>
                  <li>1 x Charger</li>
                  <li>1 x Tool Kit</li>
                  <li>1 x User Manual</li>
                </ul>
              </div>
            </div>

            <!-- User Manual 面板 -->
            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'manual' }">
              <button class="accordion-header" @click="toggleAccordion('manual')">
                <span>User Manual</span>
                <ChevronUpIcon v-if="activeAccordion === 'manual'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div class="accordion-content" v-show="activeAccordion === 'manual'">
                <a href="#" class="download-link">Download PDF Manual</a>
              </div>
            </div>

            <!-- Shipping 面板 -->
            <div class="accordion-item" :class="{ 'is-open': activeAccordion === 'shipping' }">
              <button class="accordion-header" @click="toggleAccordion('shipping')">
                <span>Fast, Trusted Shipping</span>
                <ChevronUpIcon v-if="activeAccordion === 'shipping'" class="icon" />
                <ChevronDownIcon v-else class="icon" />
              </button>
              <div class="accordion-content" v-show="activeAccordion === 'shipping'">
                <p class="shipping-text">We offer free shipping on all electric scooters. Orders are processed within 24
                  hours and typically delivered within 3-5 business days via UPS or FedEx.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部悬浮购物车栏 (Sticky Add to Cart) -->
    <div class="sticky-cart-bar is-visible">
      <div class="container sticky-content">
        <div class="product-mini-info">
          <img :src="product.images[0]" :alt="product.title" class="mini-img" />
          <div class="mini-text">
            <h4 class="mini-title">{{ product.title }}</h4>
            <span class="mini-variant">{{ selectedColor }}</span>
          </div>
        </div>
        <div class="sticky-actions">
          <div class="price-area">
            <span class="current-price">${{ product.price }}</span>
            <span class="old-price" v-if="product.compareAtPrice">${{ product.compareAtPrice }}</span>
          </div>
          <button class="btn-add-to-cart mini">Add to cart</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, markRaw } from 'vue'
import {
  CameraIcon, VideoIcon, StarIcon, MinusIcon, PlusIcon,
  ZapIcon, InfoIcon, ChevronDownIcon, ChevronUpIcon, CheckSquareIcon,
  ActivityIcon, BatteryIcon, NavigationIcon, ChevronLeftIcon, ChevronRightIcon
} from 'lucide-vue-next'

// Mock 商品数据
const product = ref({
  id: 's-nova-pro',
  title: 'S Nova Pro Commuting Electric Scooter',
  price: '489.99',
  compareAtPrice: '599.99',
  tags: ['Spring Sale'],
  images: [
    'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800',
    'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800'
  ],
  appImage: 'https://via.placeholder.com/60x120?text=APP',
  specs: [
    { label: 'Max Power', value: '1000W', icon: markRaw(ActivityIcon) },
    { label: 'Max Range', value: '38 Miles', icon: markRaw(NavigationIcon) },
    { label: 'Top Speed', value: '28 MPH', icon: markRaw(ActivityIcon) },
    { label: 'Battery Capacity', value: '48V 13Ah', icon: markRaw(BatteryIcon) },
    { label: 'Charging Time', value: '6-7 Hours', icon: markRaw(BatteryIcon) },
    { label: 'Max Load', value: '264 Lbs', icon: markRaw(ActivityIcon) }
  ],
  styles: ['2026 Upgraded Edition'],
  bundles: ['S9 Pro*1', 'S9 Pro*2'],
  colors: [], // 原图里没有颜色选择，暂时清空
  upsells: [
    { id: 1, name: 'Cable Lock for Escooter', image: 'https://via.placeholder.com/80x80?text=Lock', value: '35.99' },
    { id: 2, name: '14-Day Free Trial', image: 'https://via.placeholder.com/80x80?text=Trial', value: '75.99' },
    { id: 3, name: 'One Year Warranty', image: 'https://via.placeholder.com/80x80?text=Warranty', value: '73.99' }
  ],
  features: [
    '<strong>1000W</strong> Max Power, Max Speed <strong>28 MPH</strong>. <strong>30%</strong> Hill Climbing',
    '<strong>48V 13Ah</strong> Battery, <strong>38 Miles</strong> Max Range, <strong>6-7H</strong> Charging Time',
    '<strong>10 inch</strong> Pneumatic Tire, Front and Rear <strong>Dual Suspension</strong>, <strong>Disc Brake</strong>, Easy to <strong>Fold</strong>',
    '<strong>ALUMINUM</strong> Frame, <strong>264Lbs</strong> Max Load',
    'Bright <strong>Headlight, Multi-Color Ambient Light</strong> and Smart Turn Signals',
    'Safety Certified: Brand New <strong>App Supported, 365-day</strong> Quality Assurance'
  ]
})

const activeImageIndex = ref(0)
const selectedStyle = ref('2026 Upgraded Edition')
const selectedBundle = ref('S9 Pro*1')
const selectedColor = ref('')
const quantity = ref(1)
const activeAccordion = ref('quick-know') // 默认展开第一个面板

const toggleAccordion = (panelName) => {
  if (activeAccordion.value === panelName) {
    activeAccordion.value = null // 如果点击已展开的，则收起
  } else {
    activeAccordion.value = panelName // 展开新的
  }
}
</script>

<style lang="scss" scoped>
.product-detail-page {
  padding: 20px 0 60px;
  max-width: 1440px;
  background-color: #fff;
  margin: 0 auto;
}

// 面包屑
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
  margin-bottom: 24px;

  a {
    transition: color 0.3s ease;

    &:hover {
      color: #58cc02;
    }
  }

  .separator {
    color: #ccc;
  }

  .current {
    color: #333;
    font-weight: 500;
  }
}

// 核心布局：左侧粘性，右侧滚动
.product-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: start; // 必须是 start 才能使 sticky 生效

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
    gap: 40px;
  }
}

// 左侧图片区域 (关键 Sticky 样式)
.product-media {
  // position: sticky 是实现“到达导航栏下方时固定，右侧继续滚动”的核心
  position: sticky;
  // 这里的 100px 是预留给顶部 Header 的高度。如果你的 header 更高，可以调大这个值
  top: 100px;
  // 防止粘性区域超出父容器导致布局崩溃
  max-height: calc(100vh - 100px);
  overflow-y: auto; // 如果左侧内容较多，允许其内部小范围滚动
  scrollbar-width: none; // 隐藏滚动条

  &::-webkit-scrollbar {
    display: none;
  }

  .media-sticky-wrapper {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .main-image-container {
    position: relative;
    border-radius: 20px;
    background: #fff;
    border: 1px solid #f0f0f0;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    aspect-ratio: 1 / 1;

    .main-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.05);
      }
    }

    .tags-left {
      position: absolute;
      top: 0;
      left: 0;

      .tag-label {
        display: inline-block;
        color: #fff;
        font-size: 12px;
        padding: 6px 12px;
        font-weight: bold;
        border-radius: 0 0 12px 0;

        &.new {
          background: #e62332;
        }
      }
    }

    .tags-right {
      position: absolute;
      top: 20px;
      right: 20px;

      .spring-sale-badge {
        background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
        border: 2px solid #fff;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        border-radius: 12px;
        padding: 6px 12px;
        transform: rotate(5deg);

        .text {
          color: #2e7d32;
          font-weight: 900;
          font-size: 12px;
          line-height: 1.1;
          display: block;
          text-align: center;
          text-transform: uppercase;
        }
      }
    }

    .app-preview {
      position: absolute;
      top: 100px;
      right: 20px;
      width: 60px;
      height: auto;
      background: #fff;
      padding: 4px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  // 缩略图
  .thumbnails {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12px;

    .nav-btn {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #f5f5f5;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;

      &:hover {
        background: #e0e0e0;
      }
    }

    .thumbs-list {
      display: flex;
      gap: 12px;
      overflow-x: auto;
      scrollbar-width: none;

      &::-webkit-scrollbar {
        display: none;
      }

      .thumbnail-btn {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        border: 2px solid transparent;
        background: #f9f9f9;
        padding: 4px;
        flex-shrink: 0;
        transition: all 0.2s;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        &.is-active {
          border-color: #333;
          background: #fff;
        }
      }
    }
  }

  // 切换按钮
  .media-toggles {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-top: 10px;

    .toggle-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 24px;
      border-radius: 30px;
      font-weight: 600;
      font-size: 14px;
      background: #fff;
      border: 1px solid #ddd;
      color: #333;
      transition: all 0.2s;

      .icon {
        width: 16px;
        height: 16px;
      }

      &.active {
        background: #111;
        color: #fff;
        border-color: #111;
      }

      &:hover:not(.active) {
        background: #f5f5f5;
      }
    }
  }

  // 参数网格
  .key-specs {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-top: 20px;
    padding: 24px;
    background: #fafafa;
    border-radius: 16px;

    .spec-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 8px;

      .spec-icon {
        width: 24px;
        height: 24px;
        color: #58cc02;
      }

      .value {
        font-weight: 700;
        font-size: 14px;
        color: #111;
      }

      .label {
        font-size: 12px;
        color: #666;
      }
    }
  }
}

// 右侧信息区域
.product-info {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .product-badges {
    display: flex;
    gap: 8px;

    .badge {
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 700;
      text-transform: uppercase;

      &.success {
        background: #e8f5e9;
        color: #2e7d32;
      }

      &.danger {
        background: #ffebee;
        color: #c62828;
      }

      // 模拟原图深红色背景
    }
  }

  .product-title {
    font-size: 32px;
    font-weight: 800;
    line-height: 1.2;
    margin: 0;
    color: #111;
  }

  .price-review-row {
    .price-area {
      display: flex;
      align-items: center;
      gap: 12px;

      .current-price {
        font-size: 28px;
        font-weight: 800;
        color: #e62332;
      }

      .old-price {
        font-size: 18px;
        color: #999;
        text-decoration: line-through;
      }

      .save-badge {
        background: #e62332;
        color: #fff;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: bold;
      }
    }
  }

  .review-stars {
    display: flex;
    align-items: center;
    gap: 8px;

    .stars {
      display: flex;
      color: #ffc107;

      .star-icon {
        width: 16px;
        height: 16px;
        fill: currentColor;
      }
    }

    .review-count {
      font-size: 13px;
      color: #666;
      text-decoration: underline;
      cursor: pointer;
    }
  }

  .installment-info {
    font-size: 13px; color: #555; background: #f9f9f9; padding: 12px 16px; border-radius: 8px;
    .shop-pay { color: #5a31f4; font-weight: bold; }
    .check-link { color: #666; text-decoration: underline; }
  }

  // 规格选择
  .variant-selectors {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-top: 10px;

    .selector-group {
      .selector-label { 
        font-size: 14px; 
        margin-bottom: 12px; 
        color: #333; 
        strong { font-weight: 600; color: #111; }
      }

      .options-list {
        display: flex; 
        flex-wrap: wrap; 
        gap: 12px;

        .text-btn {
          padding: 10px 20px;
          border: 1px solid #ccc;
          border-radius: 4px;
          background: #fff;
          font-size: 14px;
          color: #333;
          cursor: pointer;
          transition: all 0.2s ease;
          font-weight: 500;

          &:hover {
            border-color: #999;
          }

          &.is-active {
            border-color: #111;
            border-width: 2px;
            padding: 9px 19px; // 补偿边框宽度防止跳动
          }
        }
      }

      .color-options {
        display: flex; gap: 12px;
        .color-btn {
          width: 60px; height: 60px; border-radius: 8px; border: 2px solid #eee; padding: 2px;
          background: #fff; cursor: pointer; transition: all 0.2s;
          img { width: 100%; height: 100%; object-fit: contain; border-radius: 4px; }
          &.is-active { border-color: #111; }
        }
      }
    }
  }

  .add-to-cart-section {
    display: flex;
    gap: 16px;
    margin-top: 10px;

    .quantity-selector {
      display: flex;
      align-items: center;
      border: 1px solid #ddd;
      border-radius: 30px;
      padding: 4px 8px;
      width: 120px;

      .qty-btn {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #333;
      }

      .qty-input {
        flex: 1;
        width: 100%;
        text-align: center;
        border: none;
        font-size: 16px;
        font-weight: 600;
        outline: none;
        -moz-appearance: textfield;
      }

      .qty-input::-webkit-outer-spin-button,
      .qty-input::-webkit-inner-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }
    }

    .btn-add-to-cart {
      flex: 1;
      background: #111;
      color: #fff;
      font-size: 16px;
      font-weight: 700;
      border-radius: 30px;
      transition: all 0.3s;

      &:hover {
        background: #333;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
  }

  // 促销列表
  .upsell-section {
    margin-top: 20px;
    border: 1px solid #ffe0b2;
    border-radius: 12px;
    padding: 16px;
    background: #fffcf8;

    .upsell-title {
      font-size: 14px;
      font-weight: 700;
      color: #e65100;
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 16px;

      .icon-zap {
        width: 16px;
        height: 16px;
      }
    }

    .upsell-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px;
      background: #fff;
      border: 1px solid #eee;
      border-radius: 8px;
      margin-bottom: 8px;

      .item-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .item-img {
          width: 40px;
          height: 40px;
          object-fit: contain;
        }

        .item-name {
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }
      }

      .item-price {
        display: flex;
        flex-direction: column;
        align-items: flex-end;

        .free-price {
          color: #e62332;
          font-weight: 700;
          font-size: 14px;
        }

        .old-price {
          color: #999;
          font-size: 12px;
          text-decoration: line-through;
        }
      }
    }
  }

  // 延长保修
  .protection-plan {
    margin-top: 10px;
    background: #f9f9f9;
    padding: 16px;
    border-radius: 12px;
    border: 1px solid #eee;

    .plan-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      font-weight: 500;
      color: #555;
      margin-bottom: 12px;

      .icon-info {
        width: 16px;
        height: 16px;
        color: #58cc02;
      }
    }

    .plan-options {
      display: flex;
      gap: 12px;

      .plan-btn {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 8px;
        background: #fff;
        transition: all 0.2s;

        .plan-name {
          font-size: 12px;
          color: #666;
          margin-bottom: 4px;
        }

        .plan-price {
          font-size: 14px;
          font-weight: 700;
          color: #333;
        }

        &.active {
          border-color: #58cc02;
          background: #f1f8e9;

          .plan-name {
            color: #2e7d32;
          }
        }
      }
    }
  }

  // 折叠面板
  .accordion-group {
    margin-top: 20px;
    border-top: 1px solid #eee;

    .accordion-item {
      border-bottom: 1px solid #eee;

      .accordion-header {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 0;
        font-size: 16px;
        font-weight: 700;
        color: #333;
        background: transparent;
        border: none;
        cursor: pointer;
        text-align: left;
        transition: color 0.2s;

        &:hover {
          color: #111;
        }

        .icon {
          width: 20px;
          height: 20px;
          color: #666;
          transition: transform 0.3s ease;
        }
      }

      .accordion-content {
        padding-bottom: 20px;

        // 特性列表样式
        .feature-list {
          li {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            margin-bottom: 12px;
            font-size: 14px;
            color: #555;
            line-height: 1.5;

            .icon-check {
              width: 18px;
              height: 18px;
              color: #58cc02;
              flex-shrink: 0;
              margin-top: 2px;
            }

            :deep(strong) {
              color: #111;
            }
          }
        }

        // 参数表格样式
        .specs-table {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .spec-row {
            display: flex;
            padding: 10px 16px;
            background: #f9f9f9;
            border-radius: 8px;
            font-size: 14px;

            .spec-label {
              width: 40%;
              color: #666;
              font-weight: 500;
            }

            .spec-value {
              width: 60%;
              color: #111;
              font-weight: 600;
            }
          }
        }

        // 包装清单样式
        .box-list {
          list-style: disc;
          padding-left: 20px;
          font-size: 14px;
          color: #555;

          li {
            margin-bottom: 8px;
          }
        }

        // 下载链接样式
        .download-link {
          display: inline-flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: #111;
          font-weight: 600;
          text-decoration: underline;
          text-underline-offset: 4px;

          &:hover {
            color: #58cc02;
          }
        }

        // 运输说明样式
        .shipping-text {
          font-size: 14px;
          color: #555;
          line-height: 1.6;
          margin: 0;
        }
      }
    }
  }
}

// 底部悬浮购物车栏
.sticky-cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
  z-index: 100;
  transform: translateY(100%);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 0;
  border-top: 1px solid #eee;

  &.is-visible {
    transform: translateY(0);
  }

  .sticky-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .product-mini-info {
      display: flex;
      align-items: center;
      gap: 16px;

      .mini-img {
        width: 48px;
        height: 48px;
        object-fit: contain;
        background: #f9f9f9;
        border-radius: 4px;
      }

      .mini-text {
        display: flex;
        flex-direction: column;

        .mini-title {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin: 0;
        }

        .mini-variant {
          font-size: 12px;
          color: #666;
        }
      }
    }

    .sticky-actions {
      display: flex;
      align-items: center;
      gap: 24px;

      .price-area {
        display: flex;
        align-items: center;
        gap: 8px;

        .current-price {
          font-size: 20px;
          font-weight: 800;
          color: #e62332;
        }

        .old-price {
          font-size: 14px;
          color: #999;
          text-decoration: line-through;
        }
      }

      .btn-add-to-cart.mini {
        padding: 10px 40px;
      }
    }
  }
}
</style>