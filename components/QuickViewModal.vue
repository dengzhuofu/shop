<template>
  <div class="quick-view-modal" v-if="isOpen" @click="close">
    <div class="modal-content" @click.stop>
      <!-- 关闭按钮 -->
      <button class="btn-close" @click="close">
        <span class="icon">✕</span>
      </button>

      <div class="modal-body">
        <!-- 左侧图片画廊 -->
        <div class="image-gallery">
          <div class="main-image">
            <img :src="product.image" :alt="product.title" />
            <!-- <div class="app-preview" v-if="product.appImage">
              <img :src="product.appImage" alt="App Preview" />
            </div> -->
          </div>
          <!-- 缩略图指示器 (仅作展示) -->
          <div class="thumbnails-indicator">
            <span class="dot active"></span>
            <span class="dot" v-for="i in 10" :key="i"></span>
          </div>
        </div>

        <!-- 右侧商品信息 -->
        <div class="product-info">
          <div class="brand">isinwheel Official Store</div>
          <h2 class="title">{{ product.title }}</h2>
          
          <div class="price-review-row">
            <div class="reviews">
              <span class="stars">★★★★★</span>
              <span class="count">711 reviews</span>
            </div>
            <div class="price-box">
              <span class="current-price">${{ product.price }}</span>
              <span class="old-price" v-if="product.compareAtPrice">${{ product.compareAtPrice }}</span>
            </div>
          </div>

          <div class="installment">
            4 interest-free installments, or from <strong>$24.37/mo</strong> with <strong>shop Pay</strong>
            <a href="#" class="link">Check your purchasing power</a>
          </div>

          <!-- 倒计时 Banner -->
          <div class="countdown-banner">
            <div class="left">
              <span class="icon">⏱</span>
              <div class="text">
                <strong>Hurry up!</strong>
                <small>SALE END IN:</small>
              </div>
            </div>
            <div class="right">
              <div class="time-block">
                <span class="num">3</span>
                <span class="label">DAYS</span>
              </div>
              <span class="colon">:</span>
              <div class="time-block">
                <span class="num">0</span>
                <span class="label">HOURS</span>
              </div>
              <span class="colon">:</span>
              <div class="time-block">
                <span class="num">17</span>
                <span class="label">MINS</span>
              </div>
              <span class="colon">:</span>
              <div class="time-block">
                <span class="num">58</span>
                <span class="label">SECS</span>
              </div>
            </div>
          </div>

          <!-- 选项选择 -->
          <div class="options">
            <div class="option-group">
              <label>Style: <strong>2026 Upgraded Edition</strong></label>
              <div class="buttons">
                <button class="btn-option active">2026 Upgraded Edition</button>
              </div>
            </div>
            
            <div class="option-group">
              <label>Buy More Save More: <strong>S9 Pro*1</strong></label>
              <div class="buttons">
                <button class="btn-option active">S9 Pro*1</button>
                <button class="btn-option">S9 Pro*2</button>
              </div>
            </div>
          </div>

          <!-- 库存状态 -->
          <div class="stock-status">
            <span class="dot"></span> In stock, ready to ship
          </div>

          <!-- 操作区 -->
          <div class="actions">
            <div class="quantity-selector">
              <button class="btn-qty">-</button>
              <span class="qty">1</span>
              <button class="btn-qty">+</button>
            </div>
            <button class="btn-add-cart">Add to cart</button>
          </div>
          
          <button class="btn-buy-shop">Buy with <span>shop</span></button>
          <a href="#" class="more-payment">More payment options</a>

          <div class="footer-links">
            <div class="share">
              Share: 
              <span class="icon-share">p</span>
              <span class="icon-share">✉</span>
            </div>
            <a href="#" class="need-help">Need help?</a>
          </div>

          <a href="#" class="view-details">
            View full details <span>→</span>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  product: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close'])

const close = () => {
  emit('close')
}

// 阻止背景滚动
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})
</script>

<style lang="scss" scoped>
.quick-view-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);

  .modal-content {
    background: #fff;
    width: 90%;
    max-width: 1000px;
    height: 90vh;
    max-height: 700px;
    border-radius: 16px;
    position: relative;
    overflow-y: auto;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);

    .btn-close {
      position: absolute;
      top: 20px;
      right: 20px;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: #fff;
      border: 1px solid #eee;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      z-index: 10;
      transition: all 0.3s ease;
      
      &:hover {
        background: #f5f5f5;
        transform: scale(1.1);
      }
    }

    .modal-body {
      display: flex;
      flex-direction: column;
      height: 100%;

      @media (min-width: 768px) {
        flex-direction: row;
      }
    }

    // 左侧图片画廊
    .image-gallery {
      flex: 1;
      padding: 40px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border-right: 1px solid #eee;
      background: #fafafa;

      .main-image {
        position: relative;
        width: 100%;
        max-width: 400px;
        aspect-ratio: 1;

        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
        }

        .app-preview {
          position: absolute;
          top: 0;
          right: -40px;
          width: 120px;
          
          img {
            width: 100%;
            height: auto;
          }
        }
      }

      .thumbnails-indicator {
        display: flex;
        gap: 8px;
        margin-top: 30px;

        .dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: #ccc;
          
          &.active {
            background: #111;
            transform: scale(1.2);
          }
        }
      }
    }

    // 右侧商品信息
    .product-info {
      flex: 1;
      padding: 40px;
      overflow-y: auto;

      .brand {
        font-size: 13px;
        color: #666;
        margin-bottom: 8px;
      }

      .title {
        font-size: 32px;
        font-weight: 800;
        line-height: 1.2;
        margin-bottom: 16px;
        color: #111;
      }

      .price-review-row {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 24px;

        .reviews {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .stars {
            color: #ffc107;
            letter-spacing: 2px;
          }
          
          .count {
            font-size: 13px;
            color: #666;
          }
        }

        .price-box {
          text-align: right;
          
          .current-price {
            display: block;
            font-size: 24px;
            font-weight: 700;
            color: #e62332;
          }
          
          .old-price {
            font-size: 14px;
            color: #999;
            text-decoration: line-through;
          }
        }
      }

      .installment {
        font-size: 14px;
        color: #333;
        margin-bottom: 24px;
        line-height: 1.5;

        .link {
          display: block;
          color: #666;
          text-decoration: underline;
          margin-top: 4px;
        }
      }

      .countdown-banner {
        background: #fff0f2;
        border: 1px solid #ffd6da;
        border-radius: 8px;
        padding: 12px 16px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        .left {
          display: flex;
          align-items: center;
          gap: 12px;
          color: #e62332;

          .icon {
            font-size: 24px;
          }

          .text {
            display: flex;
            flex-direction: column;
            
            strong { font-size: 16px; }
            small { font-size: 10px; font-weight: bold; }
          }
        }

        .right {
          display: flex;
          align-items: center;
          gap: 4px;

          .time-block {
            display: flex;
            flex-direction: column;
            align-items: center;
            
            .num {
              background: #fff;
              color: #e62332;
              font-weight: bold;
              padding: 4px 8px;
              border-radius: 4px;
              border: 1px solid #ffd6da;
              min-width: 28px;
              text-align: center;
            }
            
            .label {
              font-size: 9px;
              color: #e62332;
              margin-top: 4px;
            }
          }
          
          .colon {
            color: #e62332;
            font-weight: bold;
            margin: 0 2px;
            align-self: flex-start;
            margin-top: 4px;
          }
        }
      }

      .options {
        margin-bottom: 24px;

        .option-group {
          margin-bottom: 16px;

          label {
            display: block;
            font-size: 14px;
            color: #333;
            margin-bottom: 8px;
          }

          .buttons {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;

            .btn-option {
              padding: 10px 20px;
              border: 1px solid #ddd;
              background: #fff;
              border-radius: 8px;
              font-size: 14px;
              cursor: pointer;
              transition: all 0.2s;

              &:hover {
                border-color: #999;
              }

              &.active {
                border-color: #111;
                border-width: 2px;
                padding: 9px 19px; // 补偿边框宽度
              }
            }
          }
        }
      }

      .stock-status {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        color: #333;
        margin-bottom: 24px;
        background: #f6fdf6;
        padding: 8px 16px;
        border-radius: 20px;
        display: inline-flex;

        .dot {
          width: 8px;
          height: 8px;
          background: #58cc02;
          border-radius: 50%;
        }
      }

      .actions {
        display: flex;
        gap: 16px;
        margin-bottom: 16px;

        .quantity-selector {
          display: flex;
          align-items: center;
          border: 1px solid #ddd;
          border-radius: 30px;
          padding: 4px;
          
          button {
            width: 36px;
            height: 36px;
            background: none;
            border: none;
            font-size: 18px;
            cursor: pointer;
          }
          
          .qty {
            width: 40px;
            text-align: center;
            font-weight: bold;
          }
        }

        .btn-add-cart {
          flex: 1;
          background: #111;
          color: #fff;
          border: none;
          border-radius: 30px;
          font-size: 16px;
          font-weight: bold;
          cursor: pointer;
          transition: background 0.2s;

          &:hover {
            background: #333;
          }
        }
      }

      .btn-buy-shop {
        width: 100%;
        background: #5a31f4; // shop pay purple
        color: #fff;
        border: none;
        border-radius: 30px;
        padding: 16px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        margin-bottom: 12px;
        
        span {
          font-weight: 900;
        }
      }

      .more-payment {
        display: block;
        text-align: center;
        font-size: 13px;
        color: #666;
        text-decoration: underline;
        margin-bottom: 32px;
      }

      .footer-links {
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-top: 1px solid #eee;
        padding-top: 24px;
        margin-bottom: 24px;

        .share {
          font-size: 14px;
          color: #666;
          display: flex;
          align-items: center;
          gap: 12px;

          .icon-share {
            cursor: pointer;
            &:hover { color: #111; }
          }
        }

        .need-help {
          font-size: 14px;
          color: #666;
          text-decoration: underline;
        }
      }

      .view-details {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        padding: 16px 0;
        border-top: 1px solid #eee;
        color: #111;
        text-decoration: none;
        font-weight: 600;
        font-size: 15px;

        &:hover {
          color: #58cc02;
        }
      }
    }
  }
}
</style>