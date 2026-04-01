<template>
  <div class="orders-page">
    <h1 class="page-title">Orders</h1>
    
    <div class="orders-content">
      <div class="empty-state">
        <h3 class="empty-title">No orders yet</h3>
        <p class="empty-desc">Go to store to place an order.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
definePageMeta({
  layout: 'account'
})
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
