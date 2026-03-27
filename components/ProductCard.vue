<template>
  <div class="product-card">
    <div class="image-wrapper">
      <div class="tags" v-if="product.tags && product.tags.length">
        <span class="tag" v-for="tag in product.tags" :key="tag">{{ tag }}</span>
      </div>
      <img :src="product.image" :alt="product.title" class="main-img" />
      <!-- Hover 出现的按钮 -->
      <div class="hover-actions">
        <button class="btn-choose">Choose options</button>
      </div>
    </div>
    
    <div class="info">
      <h3 class="title">{{ product.title }}</h3>
      
      <div class="price-area">
        <span class="current-price" v-if="product.price">
          <template v-if="product.isFrom">From </template>
          ${{ product.price }}
        </span>
        <span class="old-price" v-if="product.compareAtPrice">${{ product.compareAtPrice }}</span>
        <span class="save-badge" v-if="product.compareAtPrice && product.price">
          Save ${{ (product.compareAtPrice - product.price).toFixed(2) }}
        </span>
      </div>

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
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.product-card {
  border-radius: 12px;
  overflow: hidden;
  background: $white;
  transition: box-shadow 0.3s ease;
  border: 1px solid $border-color;
  display: flex;
  flex-direction: column;

  &:hover {
    box-shadow: 0 10px 20px rgba(0,0,0,0.08);
    
    .image-wrapper .hover-actions {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .image-wrapper {
    position: relative;
    padding-top: 100%; // 1:1 aspect ratio
    background: #f8f8f8;
    overflow: hidden;

    .tags {
      position: absolute;
      top: 12px;
      left: 12px;
      z-index: 2;
      display: flex;
      flex-direction: column;
      gap: 6px;

      .tag {
        background: $danger-color;
        color: $white;
        font-size: 12px;
        padding: 4px 8px;
        border-radius: 4px;
        font-weight: bold;
      }
    }

    .main-img {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: contain;
      padding: 20px;
      transition: transform 0.3s ease;
    }

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

      .btn-choose {
        background: #111;
        color: #fff;
        padding: 10px 24px;
        border-radius: 20px;
        font-weight: 600;
        font-size: 14px;
        
        &:hover {
          background: #333;
        }
      }
    }
  }

  .info {
    padding: 20px;
    display: flex;
    flex-direction: column;
    flex: 1;

    .title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.4;
    }

    .price-area {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
      margin-bottom: 20px;

      .current-price {
        color: $danger-color;
        font-size: 20px;
        font-weight: bold;
      }

      .old-price {
        color: #999;
        text-decoration: line-through;
        font-size: 14px;
      }

      .save-badge {
        background: $danger-color;
        color: $white;
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 4px;
        font-weight: bold;
      }
    }

    .specs-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 12px;
      margin-top: auto;

      .spec-item {
        display: flex;
        align-items: center;
        gap: 8px;
        background: #f9f9f9;
        padding: 8px;
        border-radius: 8px;

        .spec-icon {
          width: 20px;
          height: 20px;
          color: #58cc02;
        }

        .spec-text {
          display: flex;
          flex-direction: column;
          
          .value {
            font-size: 13px;
            font-weight: bold;
            color: #111;
          }
          
          .label {
            font-size: 11px;
            color: #666;
          }
        }
      }
    }
  }
}
</style>
