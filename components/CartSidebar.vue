<template>
  <div>
    <transition name="fade">
      <div v-if="cart.isOpen.value" class="cart-overlay" @click="handleClose" />
    </transition>

    <transition name="slide-right">
      <div v-if="cart.isOpen.value" class="cart-sidebar">
        <div class="cart-header">
          <h2>
            {{ t('cart') }}
            <span class="count">{{ cart.count.value }}</span>
          </h2>
          <button type="button" class="btn-recently-viewed">{{ copy.recentlyViewed }}</button>
          <button type="button" class="btn-close" @click="handleClose">
            <span class="icon">✕</span>
          </button>
        </div>

        <div class="cart-body">
          <div v-if="cart.items.value.length" class="cart-items">
            <div v-for="item in cart.items.value" :key="item.cartItemId" class="cart-item">
              <NuxtLink :to="`/products/${item.slug || item.productId}`" class="item-image" @click="handleClose">
                <img :src="item.productPic" :alt="item.title" />
              </NuxtLink>

              <div class="item-details">
                <NuxtLink
                  :to="`/products/${item.slug || item.productId}`"
                  class="title"
                  @click="handleClose"
                >
                  {{ item.title }}
                </NuxtLink>
                <div class="price-row">
                  <span class="current-price">{{ money(item.unitPrice) }}</span>
                </div>
                <p class="meta">{{ attributeText(item.attributes) }}</p>
                <p v-if="item.addons?.length" class="bundle-sale">
                  <span class="tag">{{ copy.addons }}</span>
                  <span class="discount-amount">
                    + {{ item.addons.map((addon: any) => addon.name).join(', ') }}
                  </span>
                </p>
              </div>

              <div class="item-actions">
                <div class="quantity-selector">
                  <span class="qty">{{ item.quantity }}</span>
                  <div class="controls">
                    <button type="button" class="btn-up" @click="changeQty(item, item.quantity + 1)">^</button>
                    <button
                      type="button"
                      class="btn-down"
                      @click="changeQty(item, Math.max(1, item.quantity - 1))"
                    >
                      v
                    </button>
                  </div>
                </div>
                <button type="button" class="btn-remove" @click="remove(item.cartItemId)">Remove</button>
              </div>
            </div>
          </div>

          <div v-else class="empty-cart">
            <p>{{ t('emptyCart') }}</p>
          </div>
        </div>

        <div class="cart-footer">
          <div class="cart-options">
            <button type="button" class="btn-option">
              <span class="icon">📝</span> {{ copy.orderNote }}
            </button>
            <button type="button" class="btn-option">
              <span class="icon">🏷️</span> {{ t('coupon') }}
            </button>
          </div>

          <div class="subtotal-row">
            <div class="taxes-note">{{ copy.taxesNote }}</div>
            <div class="subtotal">
              <span class="label">{{ t('subtotal') }}</span>
              <span class="amount">{{ money(cart.subtotal.value) }}</span>
            </div>
          </div>

          <div class="shipping-protection">
            <div class="icon-box">📦</div>
            <div class="content">
              <div class="header">
                <strong>{{ copy.protection }}</strong>
                <label class="switch">
                  <input checked type="checkbox" />
                  <span class="slider" />
                </label>
              </div>
              <p>{{ copy.protectionNote }}</p>
            </div>
          </div>

          <div class="checkout-actions">
            <NuxtLink to="/checkout" class="btn-checkout" @click="handleClose">
              <span class="icon">🔒</span> {{ t('checkout') }}
            </NuxtLink>
            <NuxtLink to="/" class="btn-view-cart" @click="handleClose">
              {{ t('continueShopping') }}
            </NuxtLink>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { clearPageScrollLock, setPageScrollLocked } from '~/utils/scrollLock'

const cart = useShopCart()
const { lang, t } = useShopLocale()
const { money, attributeText } = useShopFormat()

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        recentlyViewed: '最近浏览',
        orderNote: '订单备注',
        taxesNote: '税费和运费将在结账时计算',
        protection: '配送保障',
        protectionNote: '建议保留配送保障，用于覆盖运输丢失、损坏或被盗的情况。',
        addons: '附加项',
      }
    : {
        recentlyViewed: 'Recently viewed',
        orderNote: 'Order note',
        taxesNote: 'Taxes and shipping calculated at checkout',
        protection: 'Shipping protection',
        protectionNote:
          'We recommend adding shipping protection for peace of mind in case your package is lost, damaged, or stolen during delivery.',
        addons: 'Add-ons',
      },
)

const changeQty = async (item: any, quantity: number) => {
  await cart.updateQuantity(item.cartItemId, quantity, item.addons?.map((addon: any) => addon.code))
}

const remove = async (cartItemId: number) => {
  await cart.removeItem(cartItemId)
}

const handleClose = () => {
  cart.closeCart()
  clearPageScrollLock()
}

onMounted(() => {
  setPageScrollLocked(cart.isOpen.value)
})

onUnmounted(() => {
  clearPageScrollLock()
})

watch(
  () => cart.isOpen.value,
  async (newVal) => {
    if (newVal) {
      setPageScrollLocked(true)
      await cart.refreshCart()
    } else {
      clearPageScrollLock()
    }
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
.cart-overlay {
  position: fixed;
  inset: 0;
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
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .item-details {
      flex: 1;

      .title {
        display: block;
        font-size: 13px;
        font-weight: 500;
        line-height: 1.4;
        margin-bottom: 8px;
        color: #111;
        text-decoration: none;
      }

      .price-row {
        margin-bottom: 8px;

        .current-price {
          color: #e62332;
          font-weight: 700;
          font-size: 14px;
        }
      }

      .meta {
        margin: 0 0 8px;
        font-size: 12px;
        color: #667085;
        line-height: 1.5;
      }

      .bundle-sale {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0;

        .tag {
          font-size: 10px;
          color: #e62332;
          border: 1px solid #ffd6da;
          padding: 2px 6px;
          border-radius: 4px;
          background: #fff0f2;
        }

        .discount-amount {
          color: #555;
          font-size: 11px;
          line-height: 1.4;
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

.empty-cart {
  p {
    color: #475467;
    margin: 0;
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
      max-width: 180px;
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
            inset: 0;
            background-color: #ccc;
            transition: 0.4s;
            border-radius: 34px;

            &:before {
              position: absolute;
              content: '';
              height: 16px;
              width: 16px;
              left: 4px;
              bottom: 4px;
              background-color: white;
              transition: 0.4s;
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

    .btn-checkout,
    .btn-view-cart {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      text-decoration: none;
      padding: 16px;
      font-size: 16px;
      font-weight: 700;
      border-radius: 30px;
    }

    .btn-checkout {
      flex: 2;
      background: #111;
      color: #fff;
      gap: 8px;

      &:hover {
        background: #333;
      }
    }

    .btn-view-cart {
      flex: 1;
      background: #fff;
      color: #111;
      border: 1px solid #ddd;

      &:hover {
        border-color: #111;
        background: #f5f5f5;
      }
    }
  }
}

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
