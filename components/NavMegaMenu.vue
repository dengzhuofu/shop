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
        <div class="banner" v-if="menuData.banner">
          <div class="discount-tag">{{ menuData.banner.tag }}</div>
          <div class="banner-content">
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
          <div class="product-card" v-for="product in activeCategory.products" :key="product.id">
            <div class="image-wrapper">
              <div class="tags" v-if="product.tags && product.tags.length">
                <span class="tag" v-for="tag in product.tags" :key="tag" :class="tag.toLowerCase().replace(' ', '-')">{{ tag }}</span>
              </div>
              <img :src="product.image" :alt="product.title" class="main-img" />
              <div class="sold-out-overlay" v-if="product.soldOut">
                <span class="sold-out-text">Sold Out</span>
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
import { ref, computed, watch } from 'vue'

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
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    gap: 40px;
    padding: 0 20px;
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
        
        &:hover {
          .image-wrapper img {
            transform: scale(1.05);
          }
          .title {
            color: #58cc02;
          }
        }

        .image-wrapper {
          position: relative;
          background: #f8f8f8;
          border-radius: 12px;
          padding-top: 100%; // 1:1 比例
          margin-bottom: 16px;
          overflow: hidden;

          .main-img {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: contain;
            padding: 16px;
            transition: transform 0.3s ease;
          }

          .tags {
            position: absolute;
            top: 10px;
            left: 10px;
            z-index: 2;
            display: flex;
            gap: 4px;

            .tag {
              font-size: 10px;
              padding: 2px 6px;
              border-radius: 4px;
              font-weight: bold;
              color: $white;
              text-transform: uppercase;
              
              &.new { background: #ff3b30; }
              &.hot { background: #ff9500; }
              &.spring-sale { 
                background: $white;
                color: #58cc02;
                border: 1px solid #58cc02;
                border-radius: 20px;
                padding: 2px 8px;
              }
            }
          }

          .sold-out-overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255,255,255,0.6);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 3;

            .sold-out-text {
              background: rgba(0,0,0,0.6);
              color: $white;
              padding: 6px 16px;
              border-radius: 20px;
              font-size: 14px;
              font-weight: bold;
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
          }

          .price-area {
            display: flex;
            align-items: center;
            gap: 8px;

            .current-price {
              color: $danger-color;
              font-weight: 700;
              font-size: 16px;
            }

            .old-price {
              color: $text-light;
              text-decoration: line-through;
              font-size: 12px;
            }
          }
        }
      }
    }
  }
}
</style>
