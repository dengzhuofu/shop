<template>
  <div class="mega-menu">
    <div class="mega-menu-inner container">
      <!-- 左侧分类与横幅 -->
      <div class="sidebar">
        <div class="categories">
          <div class="section-title">Collections</div>
          <ul>
            <li 
              v-for="cat in menuData.categories" 
              :key="cat.id"
              :class="{ active: activeCategoryId === cat.id }"
              @mouseenter="activeCategoryId = cat.id"
            >
              {{ cat.name }}
            </li>
          </ul>
        </div>
        
       <!-- 底部横幅 -->
       <div class="sidebar-bottom" v-if="menuData.banner">
         <div class="trustpilot" v-if="menuData.banner.trustpilot">
           <span class="star-icon">★</span> Trustpilot
           <span class="rating-stars">
             <span class="star">★</span><span class="star">★</span><span class="star">★</span><span class="star">★</span><span class="star half">★</span>
           </span>
           <span class="score">{{ menuData.banner.trustpilot }}</span>
         </div>
         <NuxtLink :to="menuData.banner.linkUrl" class="combo-link">
           {{ menuData.banner.linkText }} <span class="arrow">→</span>
         </NuxtLink>
       </div>
      </div>

      <!-- 右侧商品区 -->
      <div class="content-area">
        <div class="content-header">
          <span class="title">MOST POPULAR</span>
          <NuxtLink :to="activeCategory.allLinkUrl" class="view-all">
            {{ activeCategory.allLinkText }} <span class="arrow">→</span>
          </NuxtLink>
        </div>
        
        <div class="products-grid">
          <div 
            class="product-card" 
            v-for="product in activeCategory.products" 
            :key="product.id"
            @mouseenter="handleMouseEnter(product)"
            @mouseleave="handleMouseLeave(product)"
          >
            <div class="image-wrapper">
              <!-- 左上角动态标签 (NEW/HOT) -->
              <div class="tags-left" v-if="getLeftTags(product).length">
                <span class="tag-label" v-for="tag in getLeftTags(product)" :key="tag" :class="tag.toLowerCase()">{{ tag }}</span>
              </div>
              <!-- 右上角动态标签 (Spring Sale) -->
              <div class="tags-right" v-if="hasSpringSale(product)">
                <div class="spring-sale-badge">
                  <span class="text">Spring<br>Sale</span>
                </div>
              </div>

              <!-- 图片轮播区域 -->
              <div class="image-carousel">
                <img 
                  v-for="(img, index) in getDisplayImages(product)" 
                  :key="index"
                  :src="img" 
                  :alt="`${product.title} - ${index + 1}`" 
                  class="main-img" 
                  :class="{ 'is-active': getActiveImageIndex(product) === index }"
                />
              </div>

              <!-- 轮播指示器 -->
              <div class="carousel-indicators" v-if="getDisplayImages(product).length > 1 && product._isHovered">
                <span 
                  v-for="(_, index) in getDisplayImages(product)" 
                  :key="index"
                  class="indicator-dot"
                  :class="{ 'is-active': getActiveImageIndex(product) === index }"
                  @mouseenter="setActiveImageIndex(product, index)"
                ></span>
              </div>
              
              <!-- 手机APP预览图 -->
              <img v-if="product.appImage" :src="product.appImage" class="app-preview-img" alt="App Preview" />

              <!-- 悬停操作按钮 -->
              <div class="hover-actions" :class="{ 'is-visible': product._isHovered || product.soldOut }">
                <button 
                  v-if="!product.soldOut" 
                  class="btn-action" 
                  @click.stop="handleActionClick(product)"
                >
                  {{ product.hasOptions !== false ? 'Choose options' : 'Add to cart' }}
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
            <div class="info">
              <h3 class="title">{{ product.title }}</h3>
              <div class="price-area">
                <span class="current-price">
                  <template v-if="product.isFrom">From </template>
                  ${{ product.price }}
                </span>
                <span class="old-price" v-if="product.compareAtPrice">${{ product.compareAtPrice }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, inject } from 'vue'

const props = defineProps({
  menuData: {
    type: Object,
    required: true
  }
})

// 默认选中第一个分类
const activeCategoryId = ref('')

// 监听 menuData 变化，重置默认选中的分类
watch(() => props.menuData, (newData) => {
  if (newData && newData.categories && newData.categories.length > 0) {
    activeCategoryId.value = newData.categories[0].id
  }
}, { immediate: true })

const activeCategory = computed(() => {
  return props.menuData.categories.find(c => c.id === activeCategoryId.value) || props.menuData.categories[0]
})

// 分离标签
const getLeftTags = (product) => {
  if (!product.tags) return []
  return product.tags.filter(t => t.toUpperCase() === 'NEW' || t.toUpperCase() === 'HOT')
}

const hasSpringSale = (product) => {
  if (!product.tags) return false
  return product.tags.some(t => t.toLowerCase().includes('spring sale'))
}

// 图片轮播相关逻辑
const hoverTimers = ref({})

const getDisplayImages = (product) => {
  if (product.images && product.images.length > 0) {
    return product.images
  }
  if (product.image) {
    return [product.image]
  }
  return ['https://via.placeholder.com/400x400?text=No+Image']
}

const getActiveImageIndex = (product) => {
  return product._activeImageIndex || 0
}

const setActiveImageIndex = (product, index) => {
  product._activeImageIndex = index
}

const handleMouseEnter = (product) => {
  product._isHovered = true
  // 自动轮播逻辑 (可选)
  // 如果有多张图片，可以设置定时器自动切换
}

const handleMouseLeave = (product) => {
  product._isHovered = false
  product._activeImageIndex = 0 // 鼠标移出时重置为第一张
}

// 注入全局弹窗和购物车侧边栏方法
const openQuickView = inject('openQuickView', () => {
  console.warn('openQuickView not provided')
})

const openCartSidebar = inject('openCartSidebar', () => {
  console.warn('openCartSidebar not provided')
})

const handleActionClick = (product) => {
  if (product.soldOut) return
  if (product.hasOptions !== false) {
    openQuickView(product)
  } else {
    // 模拟加入购物车并打开侧边栏
    openCartSidebar()
  }
}
</script>

<style lang="scss" scoped>
.mega-menu {
  position: absolute;
  top: 100%; // 紧贴 header 底部
  left: 0;
  width: 100vw;
  background: $white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border-top: 1px solid $border-color;
  z-index: 100;
  padding: 30px 0;
  cursor: default;

  // 如果父组件用 v-show 隐藏，其实不需要这里的 opacity，但可以配合 transition 
  &.show {
    // vue 的 v-show 会控制 display
    // 若要保留动画，可以在外部用 v-if 结合 transition，这里简单处理
  }

  .mega-menu-inner {
    max-width: 1600px; // 增加最大宽度以减小左右边距
    margin: 0 auto;
    display: flex;
    gap: 60px; // 增加左右区块的间距
    padding: 0 40px;
  }

  .sidebar {
    width: 250px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .categories {
      margin-bottom: 20px;
      .section-title {
        font-size: 14px;
        color: $text-color;
        margin-bottom: 16px;
        padding: 0 16px;
      }
      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          padding: 12px 16px;
          font-size: 16px;
          font-weight: 600;
          color: $text-light;
          cursor: pointer;
          border-radius: 4px;
          transition: all 0.3s ease;
          margin-bottom: 8px;

          &:hover {
            color: $text-color;
          }

          &.active {
            background: linear-gradient(90deg, rgba(88, 204, 2, 0.15) 0%, rgba(255, 255, 255, 0) 100%);
            color: $text-color;
            border-left: 4px solid #58cc02;
          }
        }
      }
    }

    .banner {
      margin-top: 30px;
      position: relative;
      
      .discount-tag {
        position: absolute;
        left: -32px;
        top: 50%;
        transform: translateY(-50%) rotate(-90deg);
        background-color: #58cc02;
        color: $white;
        font-weight: 800;
        font-size: 14px;
        padding: 4px 16px;
        border-radius: 4px 4px 0 0;
        letter-spacing: 1px;
      }

      .banner-content {
        padding-left: 20px;
        
        .trustpilot {
          border: 1px solid $border-color;
          padding: 12px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 12px;
          margin-bottom: 16px;
          
          .star-icon {
            color: #00b67a;
            font-size: 16px;
          }

          .rating-stars {
            display: flex;
            background: #00b67a;
            padding: 2px 4px;
            border-radius: 2px;
            
            .star {
              color: $white;
              font-size: 10px;
            }
          }
          
          .score {
            font-weight: bold;
          }
        }

        .combo-link {
          font-weight: 700;
          font-size: 14px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          color: $text-color;
          text-decoration: none;

          &:hover {
            color: #58cc02;
          }
        }
      }
    }
  }

  .content-area {
    flex: 1;
    display: flex;
    flex-direction: column;

    .content-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 1px solid $border-color;

      .title {
        font-size: 12px;
        color: $text-light;
        font-weight: 600;
        letter-spacing: 1px;
      }

      .view-all {
        font-size: 14px;
        font-weight: 600;
        color: $text-color;
        text-decoration: none;
        display: flex;
        align-items: center;
        gap: 4px;

        &:hover {
          color: #58cc02;
        }
      }
    }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;

      .product-card {
        display: flex;
        flex-direction: column;
        cursor: pointer;
        transition: transform 0.3s ease;
        
        &:hover {
          transform: translateY(-4px); // 整体悬浮轻微上移
          .image-wrapper img.main-img {
            transform: scale(1.05);
          }
          .title {
            color: #58cc02;
          }
        }

        .image-wrapper {
          position: relative;
          background: #fff;
          border-radius: 12px;
          border: 1px solid #eee;
          padding-top: 100%; // 1:1 比例
          margin-bottom: 16px;
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
              font-size: 10px;
              padding: 4px 8px;
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
                font-size: 10px;
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
              padding: 24px;
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
            top: 30px;
            right: 15px;
            width: 45px;
            height: auto;
            object-fit: contain;
            z-index: 1;
          }

          .hover-actions {
              position: absolute;
              bottom: 15px;
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

              .btn-action {
                background: #111;
                color: $white;
                padding: 10px 20px;
                border-radius: 30px;
                font-weight: 600;
                font-size: 13px;
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

        .info {
            display: flex;
            flex-direction: column;

            .title {
              font-size: 13px;
              font-weight: 500;
              line-height: 1.4;
              margin-bottom: 8px;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              transition: color 0.3s ease;
            }

            .price-area {
              display: flex;
              align-items: center;
              gap: 8px;

              .current-price {
                color: #e62332;
                font-weight: 700;
                font-size: 15px;
              }

              .old-price {
                color: #999;
                font-size: 12px;
                position: relative;
                
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
            }
          }
      }
    }
  }
}
</style>
