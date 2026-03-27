<template>
  <div class="product-card">
    <!-- 商品图片区域 -->
    <div class="image-wrapper">
      <!-- 打折/新品标签 (Spring Sale, Save $xxx 等) -->
      <div class="tags" v-if="product.tags && product.tags.length">
        <span class="tag" v-for="tag in product.tags" :key="tag">{{ tag }}</span>
      </div>
      <!-- 商品主图 -->
      <img :src="product.image" :alt="product.title" class="main-img" />
      <!-- Hover 出现的按钮 -->
      <div class="hover-actions">
        <button class="btn-choose">Choose options</button>
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
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
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
    
    .image-wrapper .hover-actions {
      opacity: 1;
      transform: translateY(0);
    }
  }

  // 图片包装容器
  .image-wrapper {
    position: relative;
    padding-top: 100%; // 保持 1:1 的宽高比
    background: #f8f8f8;
    overflow: hidden;

    // 左上角标签容器
    .tags {
      position: absolute;
      top: 12px;
      left: 12px;
      z-index: 2;
      display: flex;
      flex-direction: column;
      gap: 6px;

      // 单个标签样式
      .tag {
        background: $danger-color;
        color: $white;
        font-size: 12px;
        padding: 4px 8px;
        border-radius: 4px;
        font-weight: bold;
        text-transform: uppercase; // 标签字母大写
      }
    }

    // 主图样式
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

      // 选择选项按钮
      .btn-choose {
        background: $primary-color;
        color: $white;
        padding: 10px 24px;
        border-radius: 20px;
        font-weight: 600;
        font-size: 14px;
        border: none;
        cursor: pointer;
        
        &:hover {
          background: lighten($primary-color, 20%);
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
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 12px;
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
      gap: 8px;
      margin-bottom: 20px;

      // 当前售价 (折扣价)
      .current-price {
        color: $danger-color;
        font-size: 20px;
        font-weight: bold;
      }

      // 原价
      .old-price {
        color: $text-light;
        text-decoration: line-through;
        font-size: 14px;
      }

      // 节省金额标签
      .save-badge {
        background: $danger-color;
        color: $white;
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 4px;
        font-weight: bold;
      }
    }

    // 底部关键参数网格
    .specs-grid {
      display: grid;
      grid-template-columns: 1fr 1fr; // 默认两列
      gap: 12px;
      margin-top: auto; // 将参数推至底部

      // 单个参数项
      .spec-item {
        display: flex;
        align-items: center;
        gap: 8px;
        background: $bg-light;
        padding: 8px;
        border-radius: 8px;

        // 参数图标
        .spec-icon {
          width: 20px;
          height: 20px;
          color: $secondary-color; // 使用主题绿色
        }

        // 参数文本容器
        .spec-text {
          display: flex;
          flex-direction: column;
          
          // 参数值 (如 350W)
          .value {
            font-size: 13px;
            font-weight: bold;
            color: $text-color;
          }
          
          // 参数标签 (如 Motor Capacity)
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
