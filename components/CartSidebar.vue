<template>
  <div>
    <!-- 侧边栏遮罩 -->
    <transition name="fade">
      <div class="cart-overlay" v-if="isOpen" @click="close"></div>
    </transition>

    <!-- 侧边栏内容 -->
    <transition name="slide-right">
      <div class="cart-sidebar" v-if="isOpen">
        <!-- 头部 -->
        <div class="cart-header">
          <h2>Cart <span class="count">{{ cartItems.length }}</span></h2>
          <button class="btn-recently-viewed">Recently viewed</button>
          <button class="btn-close" @click="close">
            <span class="icon">✕</span>
          </button>
        </div>

        <!-- 购物车商品列表 -->
        <div class="cart-body">
          <div class="cart-items" v-if="cartItems.length > 0">
            <div class="cart-item" v-for="item in cartItems" :key="item.id">
              <div class="item-image">
                <img :src="item.productPic || 'https://via.placeholder.com/150'" alt="Product Image" />
              </div>
              <div class="item-details">
                <h3 class="title">{{ item.title }}</h3>
                <div class="price-row">
                  <span class="current-price">${{ Number(item.unitPrice || 0).toFixed(2) }}</span>
                </div>
              </div>
              <div class="item-actions">
                <div class="quantity-selector">
                  <span class="qty">{{ item.quantity }}</span>
                  <div class="controls">
                    <button class="btn-up" @click="changeQty(item, item.quantity + 1)">^</button>
                    <button class="btn-down" @click="changeQty(item, Math.max(1, item.quantity - 1))">v</button>
                  </div>
                </div>
                <button class="btn-remove" @click="removeItem(item)">Remove</button>
              </div>
            </div>
          </div>
          <div v-else class="empty-cart">
            <p>Your cart is currently empty.</p>
          </div>
        </div>

        <!-- 底部结算区 -->
        <div class="cart-footer">
          <div class="cart-options">
            <button class="btn-option">
              <span class="icon">📝</span> Order note
            </button>
            <button class="btn-option">
              <span class="icon">🏷️</span> Discount
            </button>
          </div>

          <div class="subtotal-row">
            <div class="taxes-note">Taxes and shipping calculated<br>at checkout</div>
            <div class="subtotal">
              <span class="label">Subtotal</span>
              <span class="amount">${{ subtotal.toFixed(2) }} USD</span>
            </div>
          </div>

          <!-- Shipping protection -->
          <div class="shipping-protection">
            <div class="icon-box">📦</div>
            <div class="content">
              <div class="header">
                <strong>Shipping protection ($68.67)</strong>
                <label class="switch">
                  <input type="checkbox" checked>
                  <span class="slider"></span>
                </label>
              </div>
              <p>We recommend adding shipping protection for peace of mind in case your package is lost, damaged, or stolen during delivery.</p>
            </div>
          </div>

          <!-- 按钮 -->
          <div class="checkout-actions">
            <button class="btn-checkout" @click="goCheckout">
              <span class="icon">🔒</span> Check out
            </button>
            <button class="btn-view-cart" @click="goCheckout">View cart</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useHttp } from '~/composables/useHttp'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

const close = () => {
  emit('close')
}

const cartItems = ref([])
const subtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + Number(item.lineAmount || 0), 0)
})

const fetchCartItems = async () => {
  try {
    const res = await useHttp('/api/cart/list')
    if (res && res.code === 200) {
      cartItems.value = res.data || []
    }
  } catch (error) {
    console.error('Failed to fetch cart items:', error)
  }
}

const changeQty = async (item, quantity) => {
  await useHttp(`/api/cart/${item.cartItemId}`, {
    method: 'PUT',
    body: { quantity }
  })
  fetchCartItems()
}

const removeItem = async (item) => {
  await useHttp(`/api/cart/${item.cartItemId}`, {
    method: 'DELETE'
  })
  fetchCartItems()
}

const goCheckout = () => {
  navigateTo('/checkout')
}

// 阻止背景滚动并获取数据
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
    fetchCartItems()
  } else {
    document.body.style.overflow = ''
  }
})
</script>

<style lang="scss" scoped>
.cart-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1000;
  backdrop-filter: blur(2px);
}

.cart-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  width: 100%;
  max-width: 480px;
  height: 100vh;
  background: #fff;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
}

.cart-header {
  padding: 24px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;

  h2 {
    font-size: 24px;
    font-weight: 800;
    margin: 0;
    display: flex;
    align-items: flex-start;
    
    .count {
      font-size: 12px;
      margin-left: 4px;
      margin-top: 2px;
    }
  }

  .btn-recently-viewed {
    margin-left: 16px;
    color: #999;
    background: none;
    border: none;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    
    &:hover {
      color: #111;
    }
  }

  .btn-close {
    margin-left: auto;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #f5f5f5;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #e0e0e0;
    }
  }
}

.cart-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;

  .cart-item {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
      margin-bottom: 0;
      padding-bottom: 0;
    }

    .item-image {
      width: 80px;
      height: 80px;
      flex-shrink: 0;
      background: #f8f8f8;
      border-radius: 8px;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }
    }

    .item-details {
      flex: 1;

      .title {
        font-size: 13px;
        font-weight: 500;
        line-height: 1.4;
        margin-bottom: 8px;
        color: #111;
      }

      .price-row {
        margin-bottom: 8px;

        .old-price {
          color: #999;
          text-decoration: line-through;
          font-size: 12px;
          margin-right: 8px;
        }

        .current-price {
          color: #e62332;
          font-weight: 700;
          font-size: 14px;
        }
      }

      .bundle-sale {
        display: flex;
        align-items: center;
        gap: 8px;

        .tag {
          font-size: 10px;
          color: #e62332;
          border: 1px solid #ffd6da;
          padding: 2px 6px;
          border-radius: 4px;
          background: #fff0f2;
        }

        .discount-amount {
          background: #e62332;
          color: #fff;
          font-size: 10px;
          padding: 2px 6px;
          border-radius: 4px;
          font-weight: bold;
        }
      }
    }

    .item-actions {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      justify-content: space-between;

      .quantity-selector {
        display: flex;
        align-items: center;
        border: 1px solid #ddd;
        border-radius: 4px;
        padding: 4px 8px;

        .qty {
          font-size: 14px;
          min-width: 20px;
          text-align: center;
        }

        .controls {
          display: flex;
          flex-direction: column;
          margin-left: 8px;

          button {
            background: none;
            border: none;
            font-size: 10px;
            cursor: pointer;
            padding: 0 4px;
            line-height: 1;
            color: #666;

            &:hover {
              color: #111;
            }
          }
        }
      }

      .btn-remove {
        background: none;
        border: none;
        color: #999;
        font-size: 12px;
        text-decoration: underline;
        cursor: pointer;

        &:hover {
          color: #e62332;
        }
      }
    }
  }
}

.cart-footer {
  padding: 24px;
  background: #fdfdfd;
  border-top: 1px solid #eee;

  .cart-options {
    display: flex;
    justify-content: space-between;
    margin-bottom: 24px;

    .btn-option {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      background: none;
      border: none;
      font-size: 14px;
      font-weight: 500;
      color: #333;
      cursor: pointer;

      &:first-child {
        border-right: 1px solid #ddd;
      }

      &:hover {
        color: #58cc02;
      }
    }
  }

  .subtotal-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 24px;

    .taxes-note {
      font-size: 12px;
      color: #666;
      line-height: 1.4;
    }

    .subtotal {
      text-align: right;
      display: flex;
      flex-direction: column;

      .label {
        font-size: 14px;
        color: #666;
        margin-bottom: 4px;
      }

      .amount {
        font-size: 24px;
        font-weight: 800;
        color: #111;
      }
    }
  }

  .shipping-protection {
    display: flex;
    gap: 16px;
    background: #fff;
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 24px;

    .icon-box {
      font-size: 24px;
    }

    .content {
      flex: 1;

      .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 4px;

        strong {
          font-size: 14px;
          color: #111;
        }

        // Switch toggle style
        .switch {
          position: relative;
          display: inline-block;
          width: 40px;
          height: 24px;

          input {
            opacity: 0;
            width: 0;
            height: 0;
          }

          .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: .4s;
            border-radius: 34px;

            &:before {
              position: absolute;
              content: "";
              height: 16px;
              width: 16px;
              left: 4px;
              bottom: 4px;
              background-color: white;
              transition: .4s;
              border-radius: 50%;
            }
          }

          input:checked + .slider {
            background-color: #58cc02;
          }

          input:checked + .slider:before {
            transform: translateX(16px);
          }
        }
      }

      p {
        font-size: 11px;
        color: #666;
        line-height: 1.4;
        margin: 0;
      }
    }
  }

  .checkout-actions {
    display: flex;
    gap: 12px;

    .btn-checkout {
      flex: 2;
      background: #111;
      color: #fff;
      border: none;
      border-radius: 30px;
      padding: 16px;
      font-size: 16px;
      font-weight: bold;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      cursor: pointer;
      transition: background 0.2s;

      &:hover {
        background: #333;
      }
    }

    .btn-view-cart {
      flex: 1;
      background: #fff;
      color: #111;
      border: 1px solid #ddd;
      border-radius: 30px;
      padding: 16px;
      font-size: 16px;
      font-weight: bold;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        border-color: #111;
        background: #f5f5f5;
      }
    }
  }
}

// 动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(100%);
}
</style>
