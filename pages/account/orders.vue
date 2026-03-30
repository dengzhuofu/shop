<template>
  <div class="account-page">
    <div class="container account-container">
      <!-- 侧边栏导航 -->
      <aside class="account-sidebar">
        <h2 class="sidebar-title">My Account</h2>
        <nav class="sidebar-nav">
          <a href="#" class="nav-item">Profile</a>
          <a href="#" class="nav-item active">Orders</a>
          <a href="#" class="nav-item">Addresses</a>
          <a href="#" class="nav-item">Settings</a>
          <a href="#" class="nav-item logout">Logout</a>
        </nav>
      </aside>

      <!-- 主要内容区 -->
      <main class="account-content">
        <div class="content-header">
          <h1 class="page-title">Order History</h1>
          <p class="page-desc">View and manage your past and current orders.</p>
        </div>

        <!-- 订单状态标签页 -->
        <div class="order-tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.value"
            class="tab-btn"
            :class="{ active: currentStatus === tab.value }"
            @click="filterOrders(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 订单列表 -->
        <div class="orders-list" v-if="orders.length > 0">
          <div class="order-card" v-for="order in orders" :key="order.id">
            <div class="order-header">
              <div class="header-info">
                <div class="info-item">
                  <span class="label">Order ID</span>
                  <span class="value">{{ order.orderSn }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Date</span>
                  <span class="value">{{ formatDate(order.createTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Total Amount</span>
                  <span class="value price">${{ order.totalAmount.toFixed(2) }}</span>
                </div>
              </div>
              <div class="order-status">
                <span class="status-badge" :class="getStatusClass(order.status)">
                  {{ getStatusText(order.status) }}
                </span>
              </div>
            </div>

            <div class="order-body">
              <div class="order-item" v-for="item in order.items" :key="item.id">
                <img :src="item.productPic || 'https://via.placeholder.com/80'" :alt="item.productName" class="item-img" />
                <div class="item-details">
                  <h4 class="item-name">{{ item.productName }}</h4>
                  <p class="item-sku">SKU: {{ item.skuCode }}</p>
                  <div class="item-price-qty">
                    <span class="item-price">${{ item.price.toFixed(2) }}</span>
                    <span class="item-qty">x {{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="order-footer">
              <div class="shipping-info" v-if="order.receiverName">
                <MapPinIcon class="icon" />
                <span>Ship to: {{ order.receiverName }} - {{ order.receiverAddress }}</span>
              </div>
              <div class="order-actions">
                <button v-if="order.status === 0" class="btn btn-outline" @click="cancelOrder(order.id)">Cancel</button>
                <button v-if="order.status === 0" class="btn btn-primary">Pay Now</button>
                <button v-if="order.status === 2" class="btn btn-primary">Track Order</button>
                <button v-if="order.status === 3" class="btn btn-outline">Write a Review</button>
                <button class="btn btn-text">View Details</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-else>
          <PackageIcon class="empty-icon" />
          <h3>No orders found</h3>
          <p>You haven't placed any orders yet, or no orders match the selected filter.</p>
          <NuxtLink to="/" class="btn btn-primary">Start Shopping</NuxtLink>
        </div>
        
        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
            <ChevronLeftIcon />
          </button>
          <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">
            <ChevronRightIcon />
          </button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MapPinIcon, PackageIcon, ChevronLeftIcon, ChevronRightIcon } from 'lucide-vue-next'

const tabs = [
  { label: 'All Orders', value: null },
  { label: 'Pending Payment', value: 0 },
  { label: 'Processing', value: 1 },
  { label: 'Shipped', value: 2 },
  { label: 'Completed', value: 3 },
  { label: 'Cancelled', value: 4 }
]

const currentStatus = ref(null)
const orders = ref([])
const currentPage = ref(1)
const totalPages = ref(1)

const fetchOrders = async () => {
  try {
    let url = `/api/order/list?pageNum=${currentPage.value}&pageSize=5`
    if (currentStatus.value !== null) {
      url += `&status=${currentStatus.value}`
    }
    const res = await useHttp(url)
    if (res && res.code === 200) {
      orders.value = res.data.records
      totalPages.value = Math.ceil(res.data.total / 5) || 1
    }
  } catch (error) {
    console.error('Failed to fetch orders', error)
  }
}

const filterOrders = (status) => {
  currentStatus.value = status
  currentPage.value = 1
  fetchOrders()
}

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchOrders()
}

const cancelOrder = async (orderId) => {
  if (!confirm('Are you sure you want to cancel this order?')) return

  try {
    const res = await useHttp(`/api/order/${orderId}/cancel`, {
      method: 'PUT',
    })
    if (res && res.code === 200) {
      alert('Order cancelled successfully')
      fetchOrders()
    } else {
      alert(res.message || 'Failed to cancel order')
    }
  } catch (error) {
    console.error('Failed to cancel order', error)
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

const getStatusText = (status) => {
  const map = {
    0: 'Pending Payment',
    1: 'Processing',
    2: 'Shipped',
    3: 'Completed',
    4: 'Cancelled'
  }
  return map[status] || 'Unknown'
}

const getStatusClass = (status) => {
  const map = {
    0: 'status-warning',
    1: 'status-info',
    2: 'status-primary',
    3: 'status-success',
    4: 'status-danger'
  }
  return map[status] || 'status-default'
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.account-page {
  padding: 40px 0 80px;
  background-color: #f9fafb;
  min-height: calc(100vh - 80px);
}

.account-container {
  display: flex;
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

/* Sidebar Styles */
.account-sidebar {
  width: 260px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 30px 20px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  align-self: flex-start;
  position: sticky;
  top: 100px;

  .sidebar-title {
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 24px;
    color: #111;
    padding-left: 12px;
  }

  .sidebar-nav {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .nav-item {
      padding: 12px 16px;
      border-radius: 8px;
      color: #4b5563;
      font-weight: 500;
      text-decoration: none;
      transition: all 0.2s ease;

      &:hover {
        background-color: #f3f4f6;
        color: #111;
      }

      &.active {
        background-color: #111;
        color: #fff;
      }

      &.logout {
        margin-top: 20px;
        color: #ef4444;
        
        &:hover {
          background-color: #fef2f2;
        }
      }
    }
  }
}

/* Main Content Styles */
.account-content {
  flex: 1;
  min-width: 0;

  .content-header {
    margin-bottom: 30px;

    .page-title {
      font-size: 32px;
      font-weight: 800;
      color: #111;
      margin-bottom: 8px;
    }

    .page-desc {
      color: #6b7280;
      font-size: 16px;
    }
  }
}

/* Tabs */
.order-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: none;
  
  &::-webkit-scrollbar {
    display: none;
  }

  .tab-btn {
    padding: 10px 20px;
    border-radius: 20px;
    background-color: #fff;
    border: 1px solid #e5e7eb;
    color: #4b5563;
    font-weight: 600;
    font-size: 14px;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.2s;

    &:hover {
      border-color: #d1d5db;
      background-color: #f9fafb;
    }

    &.active {
      background-color: #111;
      border-color: #111;
      color: #fff;
    }
  }
}

/* Order Card */
.order-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  overflow: hidden;
  border: 1px solid #f3f4f6;

  .order-header {
    background-color: #f9fafb;
    padding: 20px 24px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    border-bottom: 1px solid #f3f4f6;

    .header-info {
      display: flex;
      gap: 32px;
      flex-wrap: wrap;

      .info-item {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .label {
          font-size: 12px;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          color: #6b7280;
          font-weight: 600;
        }

        .value {
          font-size: 15px;
          color: #111;
          font-weight: 500;

          &.price {
            font-weight: 700;
          }
        }
      }
    }
  }

  .order-body {
    padding: 24px;

    .order-item {
      display: flex;
      gap: 20px;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #f3f4f6;

      &:last-child {
        margin-bottom: 0;
        padding-bottom: 0;
        border-bottom: none;
      }

      .item-img {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 8px;
        background-color: #f9fafb;
      }

      .item-details {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;

        .item-name {
          font-size: 16px;
          font-weight: 600;
          color: #111;
          margin-bottom: 4px;
        }

        .item-sku {
          font-size: 14px;
          color: #6b7280;
          margin-bottom: 12px;
        }

        .item-price-qty {
          display: flex;
          align-items: center;
          gap: 12px;

          .item-price {
            font-size: 16px;
            font-weight: 700;
            color: #111;
          }

          .item-qty {
            font-size: 14px;
            color: #6b7280;
          }
        }
      }
    }
  }

  .order-footer {
    padding: 20px 24px;
    border-top: 1px solid #f3f4f6;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #fafafa;
    flex-wrap: wrap;
    gap: 16px;

    .shipping-info {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #4b5563;
      
      .icon {
        width: 16px;
        height: 16px;
        color: #9ca3af;
      }
    }

    .order-actions {
      display: flex;
      gap: 12px;
      margin-left: auto;
    }
  }
}

/* Status Badges */
.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;

  &.status-warning {
    background-color: #fffbeb;
    color: #d97706;
    border: 1px solid #fde68a;
  }
  &.status-info {
    background-color: #eff6ff;
    color: #2563eb;
    border: 1px solid #bfdbfe;
  }
  &.status-primary {
    background-color: #f5f3ff;
    color: #4f46e5;
    border: 1px solid #ddd6fe;
  }
  &.status-success {
    background-color: #ecfdf5;
    color: #059669;
    border: 1px solid #a7f3d0;
  }
  &.status-danger {
    background-color: #fef2f2;
    color: #dc2626;
    border: 1px solid #fecaca;
  }
  &.status-default {
    background-color: #f3f4f6;
    color: #4b5563;
    border: 1px solid #e5e7eb;
  }
}

/* Buttons */
.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: none;

  &.btn-primary {
    background-color: #58cc02; /* matching the green theme from before */
    color: #fff;
    
    &:hover {
      background-color: #46a302;
    }
  }

  &.btn-outline {
    background-color: transparent;
    border: 1px solid #d1d5db;
    color: #4b5563;

    &:hover {
      border-color: #111;
      color: #111;
    }
  }

  &.btn-text {
    background-color: transparent;
    color: #4f46e5;
    padding: 10px 12px;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* Empty State */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 80px 20px;
  text-align: center;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);

  .empty-icon {
    width: 64px;
    height: 64px;
    color: #d1d5db;
    margin-bottom: 24px;
  }

  h3 {
    font-size: 24px;
    font-weight: 700;
    color: #111;
    margin-bottom: 12px;
  }

  p {
    color: #6b7280;
    font-size: 16px;
    margin-bottom: 32px;
  }
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;

  .page-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    background-color: #fff;
    border: 1px solid #e5e7eb;
    color: #111;
    cursor: pointer;
    transition: all 0.2s;

    &:hover:not(:disabled) {
      border-color: #111;
      background-color: #f9fafb;
    }

    &:disabled {
      color: #d1d5db;
      cursor: not-allowed;
      background-color: #f9fafb;
    }
  }

  .page-info {
    font-size: 14px;
    font-weight: 600;
    color: #4b5563;
  }
}

/* Responsive */
@media (max-width: 992px) {
  .account-container {
    flex-direction: column;
  }

  .account-sidebar {
    width: 100%;
    position: static;
    
    .sidebar-nav {
      flex-direction: row;
      overflow-x: auto;
      padding-bottom: 8px;

      .nav-item {
        white-space: nowrap;
        &.logout { margin-top: 0; }
      }
    }
  }
}

@media (max-width: 640px) {
  .order-card {
    .order-header {
      flex-direction: column;
      gap: 16px;

      .header-info {
        flex-direction: column;
        gap: 12px;
      }
    }

    .order-footer {
      flex-direction: column;
      align-items: stretch;

      .order-actions {
        margin-left: 0;
        justify-content: stretch;
        
        .btn {
          flex: 1;
          text-align: center;
        }
      }
    }
  }
}
</style>