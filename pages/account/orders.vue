<template>
  <div class="orders-page">
    <section class="panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">{{ t('orderHistory') }}</p>
          <h2>{{ t('orders') }}</h2>
        </div>
        <button type="button" class="refresh-btn" @click="fetchOrders">{{ t('refreshData') }}</button>
      </div>

      <div v-if="orders.length" class="order-list">
        <article v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-top">
            <div class="order-top__main">
              <strong>#{{ order.orderSn }}</strong>
              <p>{{ orderTimeLabel }}: {{ dateTime(order.createTime) }}</p>
            </div>
            <span class="status-chip">{{ statusText(order.status) }}</span>
          </div>

          <div class="order-meta">
            <span>{{ checkoutLabel }}: {{ order.paymentMethod || 'credit_card' }}</span>
            <span>{{ shippingLabel }}: {{ order.shippingMethod || '--' }}</span>
          </div>

          <div class="line-items">
            <div v-for="item in order.items || []" :key="item.id" class="line-item">
              <div class="line-item__body">
                <NuxtLink :to="`/products/${item.slug || item.productId}`">{{ item.productName }}</NuxtLink>
                <p>{{ specLabel }}: {{ attributeText(item.skuAttributesSnapshot) || '--' }}</p>
                <p>{{ quantityLabel }}: {{ item.quantity }}</p>
              </div>
              <div class="line-item__meta">
                <span>{{ money(item.lineAmount) }}</span>
                <small>{{ statusText(order.status) }}</small>
              </div>
            </div>
          </div>

          <div class="order-total">
            <span>{{ t('total') }}</span>
            <strong>{{ money(order.totalAmount) }}</strong>
          </div>
        </article>
      </div>

      <div v-else class="empty-state">
        <p>{{ t('noOrders') }}</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

definePageMeta({
  layout: 'account',
})

const { lang, t } = useShopLocale()
const { money, statusText, attributeText, dateTime } = useShopFormat()

const orders = ref<any[]>([])

const specLabel = computed(() => (lang.value === 'zh' ? '规格' : 'Specification'))
const quantityLabel = computed(() => (lang.value === 'zh' ? '数量' : 'Quantity'))
const orderTimeLabel = computed(() => (lang.value === 'zh' ? '购买时间' : 'Purchased'))
const checkoutLabel = computed(() => (lang.value === 'zh' ? '支付方式' : 'Payment'))
const shippingLabel = computed(() => (lang.value === 'zh' ? '配送方式' : 'Shipping'))

const fetchOrders = async () => {
  const res = await useHttp('/api/order/list?pageNum=1&pageSize=20')
  orders.value = res?.code === 200 ? res.data?.records || [] : []
}

onMounted(fetchOrders)
</script>

<style scoped lang="scss">
.panel {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 24px;
  padding: 30px 32px;
}

.panel-head,
.order-top,
.order-total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.panel-head {
  margin-bottom: 22px;

  h2 {
    margin: 6px 0 0;
    font-size: 22px;
    color: #111;
  }
}

.eyebrow {
  margin: 0;
  color: #777;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.refresh-btn {
  min-height: 42px;
  padding: 0 16px;
  border: 1px solid #dcdcdc;
  border-radius: 999px;
  background: #fff;
  font-weight: 700;
  cursor: pointer;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border-radius: 22px;
  border: 1px solid #ececec;
  background: #fafafa;
  padding: 20px;
}

.order-top__main {
  strong {
    display: block;
    color: #111;
    font-size: 18px;
  }

  p {
    margin: 6px 0 0;
    color: #667085;
  }
}

.status-chip {
  padding: 8px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 700;
}

.order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 16px 0;

  span {
    padding: 8px 12px;
    border-radius: 999px;
    background: #fff;
    border: 1px solid #e5e7eb;
    color: #475467;
    font-size: 13px;
    font-weight: 600;
  }
}

.line-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 18px 0;
}

.line-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding: 14px 0;
  border-top: 1px solid #e5e7eb;

  &:last-child {
    border-bottom: 1px solid #e5e7eb;
  }
}

.line-item__body {
  min-width: 0;

  a {
    color: #111;
    font-weight: 700;
    text-decoration: none;
  }

  p {
    margin: 6px 0 0;
    color: #667085;
    font-size: 13px;
    line-height: 1.5;
  }
}

.line-item__meta {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  text-align: right;

  span {
    font-weight: 700;
    color: #111;
  }

  small {
    color: #667085;
  }
}

.order-total {
  padding-top: 14px;

  strong {
    color: #111;
    font-size: 18px;
  }
}

.empty-state {
  min-height: 140px;
  display: grid;
  place-items: center;
  border-radius: 18px;
  background: #fafafa;
  color: #767676;
}

@media (max-width: 720px) {
  .panel {
    padding: 22px;
  }

  .panel-head,
  .order-top,
  .order-total,
  .line-item {
    align-items: flex-start;
    flex-direction: column;
  }

  .line-item__meta {
    align-items: flex-start;
    text-align: left;
  }
}
</style>
