<template>
  <div class="mega-menu">
    <div class="mega-menu-inner container">
      <!-- 左侧分类与横幅 -->
      <div class="sidebar">
        <div class="discount-tag" v-if="menuData.banner && menuData.banner.tag">
          {{ menuData.banner.tag }}
        </div>
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
            <div class="product-card" 
            v-for="product in activeCategory.products" 
            :key="product.id"
            @mouseenter="handleMouseEnter(product.id)"
            @mouseleave="handleMouseLeave(product.id)"
          >
            <div class="image-wrapper">
              <!-- 左上角动态标签 (NEW/HOT) -->
              <div class="tags-left" v-if="getLeftTags(product).length">
                <span class="tag-label" v-for="tag in getLeftTags(product)" :key="tag" :class="tag.toLowerCase()">{{ tag }}</span>
              </div>
              
              <!-- 右上角动态标签 (Spring Sale) -->
              <div class="tags-right" v-if="hasSpringSale(product)">
                <img src="https://cdn.shopify.com/s/files/1/0553/7624/8930/files/spring_sale_icon.png" class="spring-sale-img" alt="Spring Sale" v-if="false" />
                <!-- 用样式模拟原图中的弹簧打折标签 -->
                <div class="spring-sale-badge">
                  <span class="text">Spring<br>Sale</span>
                </div>
              </div>
              
              <!-- 悬停时右上角的眼睛图标 (Quick View) -->
              <!-- <div class="quick-view-icon" v-if="hoveredProductId === product.id" @click.stop="handleQuickView(product)">
                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="eye-icon"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              </div> -->

              <!-- 图片轮播区域 -->
              <div class="image-carousel">
                <img 
                  v-for="(img, index) in getDisplayImages(product)" 
                  :key="index"
                  :src="img" 
                  :alt="`${product.title} - ${index + 1}`" 
                  class="main-img" 
                  :class="{ 'is-active': getActiveImageIndex(product.id) === index }"
                />
              </div>

              <!-- 轮播指示器 -->
              <div class="carousel-indicators" v-if="getDisplayImages(product).length > 1 && hoveredProductId === product.id">
                <span 
                  v-for="(_, index) in getDisplayImages(product)" 
                  :key="index"
                  class="indicator-dot"
                  :class="{ 'is-active': getActiveImageIndex(product.id) === index }"
                  @mouseenter="setActiveImageIndex(product.id, index)"
                ></span>
              </div>
              
              <!-- 手机APP预览图 -->
              <!-- <img v-if="product.appImage" :src="product.appImage" class="app-preview-img" alt="App Preview" /> -->

              <!-- 悬停操作按钮 -->
              <div class="hover-actions" :class="{ 'is-visible': hoveredProductId === product.id || product.soldOut }">
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
const hoveredProductId = ref(null)
const activeImageIndices = ref({})

const getDisplayImages = (product) => {
  if (product.images && product.images.length > 0) {
    return product.images
  }
  if (product.image) {
    return [product.image]
  }
  return ['https://via.placeholder.com/400x400?text=No+Image']
}

const getActiveImageIndex = (productId) => {
  return activeImageIndices.value[productId] || 0
}

const setActiveImageIndex = (productId, index) => {
  activeImageIndices.value = {
    ...activeImageIndices.value,
    [productId]: index
  }
}

const handleMouseEnter = (productId) => {
  hoveredProductId.value = productId
  
  // 清除可能存在的旧定时器
  if (hoverTimers.value[productId]) {
    clearInterval(hoverTimers.value[productId])
  }
  
  // 获取当前产品
  const product = activeCategory.value.products.find(p => p.id === productId)
  if (!product) return
  const images = getDisplayImages(product)
  
  // 只有多张图才开启自动轮播
  if (images.length > 1) {
    hoverTimers.value[productId] = setInterval(() => {
      const currentIndex = getActiveImageIndex(productId)
      const nextIndex = (currentIndex + 1) % images.length
      setActiveImageIndex(productId, nextIndex)
    }, 1500) // 每 1.5 秒切换一次
  }
}

const handleMouseLeave = (productId) => {
  if (hoveredProductId.value === productId) {
    hoveredProductId.value = null
  }
  
  // 清除定时器
  if (hoverTimers.value[productId]) {
    clearInterval(hoverTimers.value[productId])
    delete hoverTimers.value[productId]
  }
  
  // 鼠标移出时重置为第一张
  activeImageIndices.value = {
    ...activeImageIndices.value,
    [productId]: 0
  }
}

// 注入全局弹窗和购物车侧边栏方法
const openQuickView = inject('openQuickView', () => {
  console.warn('openQuickView not provided')
})

const openCartSidebar = inject('openCartSidebar', () => {
  console.warn('openCartSidebar not provided')
})

const handleQuickView = (product) => {
  openQuickView(product)
}

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
    max-width: 1880px; // 增加最大宽度以减小左右边距
    margin: 0 auto;
    display: flex;
    gap: 60px; // 增加左右区块的间距
    padding: 0 40px;
  }

  .sidebar {
    position: relative;
    width: 250px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .discount-tag {
      position: absolute;
      left: -20px;
      top: 50%;
      transform: translateY(-50%) translateX(-50%) rotate(-90deg);
      background-color: #58cc02;
      color: $white;
      font-weight: 800;
      font-size: 14px;
      padding: 6px 16px;
      border-radius: 4px 4px 0 0;
      letter-spacing: 1px;
      white-space: nowrap;
    }

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
          transition: all 0.3s ease;
          margin-bottom: 4px;

          &:hover {
            color: $text-color;
          }

          &.active {
            background: linear-gradient(90deg, #bcf093 0%, #eefbe2 100%); // 模拟原图左侧绿色高亮
            color: #111;
            font-weight: 800;
            border-left: 6px solid #58cc02;
          }
        }
      }
    }

    .sidebar-bottom {
      margin-top: 30px;
      padding-right: 20px;
      
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
        padding-bottom: 8px;
        border-bottom: 1px solid $border-color;

        &:hover {
          color: #58cc02;
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
          border: 1px solid transparent; // 取消边框，让它看起来更干净
          padding-top: 100%; // 1:1 比例
          margin-bottom: 16px;
          overflow: hidden;

          // 悬停时稍微加深一点背景，如果是透明的话。原图背景是白色，可以加点灰度或者阴影
          &:hover {
             border-color: #eee;
          }

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

          // 右上角眼睛图标 (Quick View)
          .quick-view-icon {
            position: absolute;
            top: 10px;
            right: 10px;
            width: 32px;
            height: 32px;
            background: #fff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
            cursor: pointer;
            z-index: 10;
            color: #333;
            transition: all 0.2s ease;

            &:hover {
              background: #f5f5f5;
              transform: scale(1.1);
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
              object-fit: cover; // 占满整个盒子
              padding: 0; // 去除内边距
              opacity: 0;
              transition: opacity 0.4s ease, transform 0.4s ease;

              &.is-active {
                opacity: 1;
                z-index: 1;
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
            z-index: 15; // 提高层级确保可见
            padding: 10px 0;

            .indicator-dot {
              width: 6px;
              height: 6px;
              border-radius: 4px;
              background: rgba(0, 0, 0, 0.3);
              cursor: pointer;
              transition: all 0.3s ease;
              box-shadow: 0 1px 2px rgba(255,255,255,0.5); // 增加对比度

              &.is-active {
                width: 16px;
                background: #111;
              }
              
              &:hover:not(.is-active) {
                background: rgba(0, 0, 0, 0.6);
              }
            }
          }

          // 手机APP预览图
          .app-preview-img {
            position: absolute;
            top: 60px; // 下移，避免与标签重叠
            right: 10px;
            width: 40px;
            height: auto;
            object-fit: contain;
            z-index: 10;
            background: #fff;
            padding: 2px;
            border-radius: 4px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
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
              font-size: 14px;
              font-weight: 500;
              line-height: 1.4;
              margin-bottom: 8px;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              transition: color 0.3s ease;
              color: #333;
            }

            .price-area {
              display: flex;
              align-items: center;
              gap: 8px;

              .current-price {
                color: #e62332;
                font-weight: 700;
                font-size: 16px;
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
