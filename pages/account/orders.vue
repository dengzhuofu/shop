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
            <div>
              <strong>{{ order.orderSn }}</strong>
              <p>{{ order.createTime }}</p>
            </div>
            <span class="status-chip">{{ statusText(order.status) }}</span>
          </div>

          <div class="line-items">
            <div v-for="item in order.items || []" :key="item.id" class="line-item">
              <NuxtLink :to="`/products/${item.slug || item.productId}`">{{ item.productName }}</NuxtLink>
              <span>{{ money(item.lineAmount) }}</span>
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
import { onMounted, ref } from 'vue'

definePageMeta({
  layout: 'account',
})

const { t } = useShopLocale()
const { money, statusText } = useShopFormat()

const orders = ref<any[]>([])

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
.order-total,
.line-item {
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
  gap: 14px;
}

.order-card {
  border-radius: 22px;
  border: 1px solid #ececec;
  background: #fafafa;
  padding: 18px 20px;
}

.order-top p {
  margin: 6px 0 0;
  color: #767676;
}

.status-chip {
  padding: 8px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 700;
}

.line-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 18px 0;
}

.line-item {
  padding-bottom: 10px;
  border-bottom: 1px solid #e7e7e7;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }

  a {
    color: #111;
    text-decoration: none;
  }
}

.order-total {
  padding-top: 14px;
  border-top: 1px solid #e7e7e7;
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
}
</style>
