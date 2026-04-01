<template>
  <div class="orders-page">
    <h1 class="page-title">Orders</h1>
    
    <div class="orders-content">
      <div class="order-list" v-if="orders.length">
        <div class="order-row" v-for="order in orders" :key="order.id">
          <div class="meta">
            <h3>{{ order.orderSn }}</h3>
            <p>{{ order.createTime }}</p>
          </div>
          <div class="status">{{ statusText(order.status) }}</div>
          <div class="amount">${{ Number(order.totalAmount || 0).toFixed(2) }}</div>
        </div>
      </div>
      <div class="empty-state" v-else>
        <h3 class="empty-title">No orders yet</h3>
        <p class="empty-desc">Go to store to place an order.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

definePageMeta({
  layout: 'account'
})

const orders = ref([])

const fetchOrders = async () => {
  try {
    const res = await useHttp('/api/order/list?pageNum=1&pageSize=20')
    if (res?.code === 200) {
      orders.value = res.data.records || []
    }
  } catch (error) {
    console.error('Failed to fetch orders', error)
  }
}

const statusText = (status) => {
  const map = {
    0: 'Pending',
    1: 'Paid',
    2: 'Shipped',
    3: 'Completed',
    4: 'Cancelled'
  }
  return map[status] || 'Unknown'
}

onMounted(fetchOrders)
</script>

<style lang="scss" scoped>
.orders-page {
  .page-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 24px;
    color: #111;
  }

  .orders-content {
    background: #fff;
    border-radius: 8px;
    padding: 60px 20px;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    border: 1px solid #eaeaea;

    .order-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
      text-align: left;

      .order-row {
        display: grid;
        grid-template-columns: 1fr 120px 120px;
        align-items: center;
        gap: 16px;
        padding: 16px;
        border: 1px solid #efefef;
        border-radius: 8px;

        .meta h3 {
          margin: 0 0 6px;
          font-size: 14px;
        }

        .meta p {
          margin: 0;
          color: #666;
          font-size: 12px;
        }

        .status {
          font-size: 13px;
          color: #333;
        }

        .amount {
          text-align: right;
          font-weight: 700;
        }
      }
    }

    .empty-state {
      .empty-title {
        font-size: 16px;
        font-weight: 600;
        color: #111;
        margin-bottom: 8px;
      }

      .empty-desc {
        font-size: 14px;
        color: #666;
        a {
          color: #111;
          text-decoration: underline;
        }
      }
    }
  }
}
</style>
