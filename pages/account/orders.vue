<template>
  <div class="orders-page">
    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">{{ t('orderHistory') }}</p>
          <h2>{{ t('orders') }}</h2>
        </div>
        <button type="button" class="minor-btn" @click="fetchOrders">{{ t('refreshData') }}</button>
      </div>

      <div v-if="orders.length" class="order-list">
        <article v-for="order in orders" :key="order.id" class="order-card">
          <div class="top-row">
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

          <div class="totals">
            <span>{{ t('total') }}</span>
            <strong>{{ money(order.totalAmount) }}</strong>
          </div>
        </article>
      </div>

      <p v-else class="helper-text">{{ t('noOrders') }}</p>
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
.card {
  border-radius: 28px;
  background: white;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 24px;
}

.section-head,
.top-row,
.totals,
.line-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.section-head {
  margin-bottom: 18px;
}

.eyebrow {
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #0f766e;
}

.minor-btn {
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 999px;
  background: #f8fafc;
  padding: 10px 14px;
  font-weight: 700;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.order-card {
  border-radius: 22px;
  background: #f8fafc;
  padding: 18px;
}

.top-row p,
.helper-text,
.line-item a {
  color: #64748b;
}

.top-row p {
  margin: 6px 0 0;
}

.status-chip {
  border-radius: 999px;
  background: #e0f2fe;
  color: #0f172a;
  padding: 8px 12px;
  font-weight: 700;
}

.line-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 16px 0;
}

.line-item {
  padding: 10px 0;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);

  &:last-child {
    border-bottom: none;
  }

  a {
    text-decoration: none;
  }
}

.totals {
  padding-top: 12px;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
}
</style>
