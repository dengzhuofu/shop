<template>
  <div class="product-card" @mouseenter="isHovered = true" @mouseleave="isHovered = false">
    <!-- 商品图片区域 -->
    <div class="image-wrapper">
      <!-- 动态标签：左上角 (NEW/HOT) 和 右上角 (Spring Sale) -->
      <div class="tags-left" v-if="leftTags.length">
        <span class="tag-label" v-for="tag in leftTags" :key="tag" :class="tag.toLowerCase()">{{ tag }}</span>
      </div>
      <div class="tags-right" v-if="hasSpringSale">
        <div class="spring-sale-badge">
          <span class="text">Spring<br>Sale</span>
        </div>
      </div>

      <!-- 图片轮播区域 -->
      <div class="image-carousel">
        <img 
          v-for="(img, index) in displayImages" 
          :key="index"
          :src="img" 
          :alt="`${product.title} - ${index + 1}`" 
          class="main-img" 
          :class="{ 'is-active': currentImageIndex === index }"
        />
      </div>

      <!-- 轮播指示器 -->
      <div class="carousel-indicators" v-if="displayImages.length > 1 && isHovered">
        <span 
          v-for="(_, index) in displayImages" 
          :key="index"
          class="indicator-dot"
          :class="{ 'is-active': currentImageIndex === index }"
          @mouseenter="currentImageIndex = index"
        ></span>
      </div>
      
      <!-- 手机APP预览图 -->
      <img v-if="product.appImage" :src="product.appImage" class="app-preview-img" alt="App Preview" />

      <!-- Hover 出现的按钮 -->
      <div class="hover-actions" :class="{ 'is-visible': isHovered || isSoldOut }">
        <button 
          v-if="!isSoldOut" 
          class="btn-action" 
          @click.stop="handleActionClick"
        >
          {{ actionText }}
        </button>
        <button 
          v-else 
          class="btn-action btn-sold-out" 
          disabled
        >
          Sold Out
        </button>
      </div>
    </div>
    
    <!-- 商品信息区域 -->
    <div class="info">
      <!-- 商品名称 -->
      <h3 class="title">{{ product.title }}</h3>
      
      <!-- 价格与打折标签区域 -->
      <div class="price-area">
        <span class="current-price" v-if="product.price">
          <template v-if="product.isFrom">From </template>
          ${{ product.price }}
        </span>
        <!-- 原价 -->
        <span class="old-price" v-if="product.compareAtPrice">${{ product.compareAtPrice }}</span>
        <!-- 节省金额标签 -->
        <span class="save-badge" v-if="product.compareAtPrice && product.price">
          Save ${{ (product.compareAtPrice - product.price).toFixed(2) }}
        </span>
      </div>

      <!-- 底部关键参数网格 (Motor, Range, Speed, Battery) -->
      <div class="specs-grid">
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
</template>

<script setup>
import { computed, ref, inject } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

// 模拟状态
const isHovered = ref(false)
const isSoldOut = computed(() => !!props.product.soldOut)
const hasOptions = computed(() => props.product.hasOptions !== false) // 默认有 options 以测试弹窗

const actionText = computed(() => {
  if (isSoldOut.value) return 'Sold Out'
  if (hasOptions.value) return 'Choose options'
  return 'Add to cart'
})

// 注入全局方法
const openQuickView = inject('openQuickView', () => {
  console.warn('openQuickView not provided')
})

const openCartSidebar = inject('openCartSidebar', () => {
  console.warn('openCartSidebar not provided')
})

const handleActionClick = () => {
  if (isSoldOut.value) return
  if (hasOptions.value) {
    // 触发打开弹窗事件
    openQuickView(props.product)
    emit('open-quick-view', props.product)
  } else {
    // 模拟加入购物车并打开侧边栏
    openCartSidebar()
  }
}

const emit = defineEmits(['open-quick-view'])

// 分离标签
const leftTags = computed(() => {
  if (!props.product.tags) return []
  return props.product.tags.filter(t => t.toUpperCase() === 'NEW' || t.toUpperCase() === 'HOT')
})

const hasSpringSale = computed(() => {
  if (!props.product.tags) return false
  return props.product.tags.some(t => t.toLowerCase().includes('spring sale'))
})

// 图片轮播逻辑
const currentImageIndex = ref(0)
const displayImages = computed(() => {
  if (props.product.images && props.product.images.length > 0) {
    return props.product.images
  }
  if (props.product.image) {
    return [props.product.image]
  }
  return ['https://via.placeholder.com/400x400?text=No+Image']
})
</script>

<style lang="scss" scoped>
// 商品卡片整体样式
.product-card {
  border-radius: 12px;
  overflow: hidden;
  background: $white;
  transition: box-shadow 0.3s ease;
  border: 1px solid $border-color;
  display: flex;
  flex-direction: column;
  height: 100%; // 确保网格中的卡片高度一致

  // 悬停时的阴影与动效
  &:hover {
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  }

  // 图片包装容器
    .image-wrapper {
      position: relative;
      padding-top: 100%; // 保持 1:1 的宽高比
      background: $white;
      overflow: hidden;

      // 左上角标签容器
      .tags-left {
        position: absolute;
        top: 0;
        left: 0;
        z-index: 2;

        .tag-label {
          display: inline-block;
          color: $white;
          font-size: 12px;
          padding: 4px 10px;
          font-weight: bold;
          text-transform: uppercase;
          border-radius: 0 0 8px 0; // 仅右下角圆角
          
          &.new {
            background: #e62332; // 红色
          }
          &.hot {
            background: #ff5722; // 橙色
          }
        }
      }

      // 右上角标签容器
      .tags-right {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 2;

        .spring-sale-badge {
          background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
          border: 2px solid #fff;
          box-shadow: 0 4px 10px rgba(0,0,0,0.1);
          border-radius: 12px;
          padding: 4px 10px;
          transform: rotate(5deg);
          
          .text {
            color: #2e7d32;
            font-weight: 900;
            font-size: 11px;
            line-height: 1.1;
            display: block;
            text-align: center;
            text-transform: uppercase;
            text-shadow: 1px 1px 0px rgba(255,255,255,0.5);
          }
        }
      }

      // 图片轮播区域
      .image-carousel {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;

        .main-img {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          object-fit: contain;
          padding: 30px;
          opacity: 0;
          transition: opacity 0.4s ease, transform 0.4s ease;

          &.is-active {
            opacity: 1;
          }
        }
      }

      // 轮播指示器
      .carousel-indicators {
        position: absolute;
        bottom: 70px; // 在按钮上方
        left: 0;
        width: 100%;
        display: flex;
        justify-content: center;
        gap: 6px;
        z-index: 5;
        padding: 10px 0;

        .indicator-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: rgba(0, 0, 0, 0.2);
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover, &.is-active {
            background: #111;
            transform: scale(1.2);
          }
        }
      }

      // 手机APP预览图
      .app-preview-img {
      position: absolute;
      top: 40px;
      right: 20px;
      width: 60px;
      height: auto;
      object-fit: contain;
      z-index: 1;
    }

    // 悬停时出现的操作按钮区域
    .hover-actions {
      position: absolute;
      bottom: 20px;
      left: 0;
      width: 100%;
      display: flex;
      justify-content: center;
      opacity: 0;
      transform: translateY(10px);
      transition: all 0.3s ease;
      z-index: 10;

      &.is-visible {
        opacity: 1;
        transform: translateY(0);
      }

      // 操作按钮
      .btn-action {
        background: #111; // 黑色背景
        color: $white;
        padding: 12px 32px;
        border-radius: 30px; // 更圆的胶囊形状
        font-weight: 600;
        font-size: 15px;
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        
        &:hover:not(:disabled) {
          background: #333;
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
        }

        &.btn-sold-out {
          background: rgba(0, 0, 0, 0.6);
          color: $white;
          cursor: not-allowed;
          box-shadow: none;
        }
      }
    }
  }

  // 信息区域容器
  .info {
    padding: 20px;
    display: flex;
    flex-direction: column;
    flex: 1;

    // 商品标题
    .title {
      font-size: 18px;
      font-weight: 500;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2; // 最多显示两行
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.4;
      color: $text-color;
    }

    // 价格区域
    .price-area {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
      margin-bottom: 20px;

      // 当前售价 (折扣价)
      .current-price {
        color: #e62332; // 红色
        font-size: 22px;
        font-weight: 600;
      }

      // 原价
      .old-price {
        color: $text-light;
        font-size: 14px;
        position: relative;
        
        // 红色删除线
        &::after {
          content: '';
          position: absolute;
          left: -2px;
          right: -2px;
          top: 50%;
          height: 1px;
          background-color: #e62332;
          transform: rotate(-10deg);
        }
      }

      // 节省金额标签
      .save-badge {
        background: #e62332;
        color: $white;
        font-size: 13px;
        padding: 4px 12px;
        border-radius: 16px; // 药丸形状
        font-weight: 600;
      }
    }

    // 底部关键参数网格
    .specs-grid {
      display: grid;
      grid-template-columns: 1fr 1fr; // 两列
      grid-template-rows: 1fr 1fr; // 两行
      margin-top: auto; // 将参数推至底部
      border-top: 1px solid $border-color;
      padding-top: 16px;

      // 单个参数项
      .spec-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        
        // 添加内边框
        &:nth-child(odd) {
          border-right: 1px solid $border-color;
          padding-right: 12px;
        }
        &:nth-child(even) {
          padding-left: 12px;
        }
        &:nth-child(1), &:nth-child(2) {
          border-bottom: 1px solid $border-color;
        }

        // 参数图标
        .spec-icon {
          width: 24px;
          height: 24px;
          color: #58cc02; // 主题绿色
          stroke-width: 1.5;
        }

        // 参数文本容器
        .spec-text {
          display: flex;
          flex-direction: column;
          
          // 参数值 (如 1000W)
          .value {
            font-size: 14px;
            font-weight: 600;
            color: $text-color;
            margin-bottom: 2px;
          }
          
          // 参数标签 (如 Max Power)
          .label {
            font-size: 11px;
            color: $text-light;
          }
        }
      }
    }
  }
}
</style>
