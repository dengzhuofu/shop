<template>
  <div class="orders-page">
    <section class="panel">
      <div class="panel-head">
        <div>
          <p class="eyebrow">{{ t('orderHistory') }}</p>
          <h2>{{ t('orders') }}</h2>
        </div>
        <button type="button" class="refresh-btn" :disabled="loading" @click="fetchOrders">
          {{ loading ? ui.refreshing : t('refreshData') }}
        </button>
      </div>

      <div v-if="orders.length" class="order-list">
        <article v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-top">
            <div class="order-top__main">
              <p class="order-label">{{ ui.orderNumber }}</p>
              <strong>#{{ order.orderSn }}</strong>
              <div class="order-top__subline">
                <p>{{ ui.purchasedAt }}: {{ dateTime(order.createTime) }}</p>
                <span>{{ itemCountText(order.items || []) }}</span>
              </div>
            </div>
            <div class="order-top__aside">
              <span class="status-chip" :class="statusTone(order.status)">{{ statusText(order.status) }}</span>
              <div class="order-top__total">
                <span>{{ t('total') }}</span>
                <strong>{{ money(order.totalAmount) }}</strong>
              </div>
            </div>
          </div>

          <div class="order-meta">
            <div class="meta-item">
              <span>{{ ui.paymentMethod }}</span>
              <strong>{{ order.paymentMethod || '--' }}</strong>
            </div>
            <div class="meta-item">
              <span>{{ ui.paymentStatus }}</span>
              <strong>{{ paymentStatusText(order.paymentStatus) }}</strong>
            </div>
            <div class="meta-item">
              <span>{{ ui.shippingMethod }}</span>
              <strong>{{ order.shippingMethod || '--' }}</strong>
            </div>
            <div v-if="order.paymentExpireTime && canContinuePayment(order)" class="meta-item">
              <span>{{ ui.payBefore }}</span>
              <strong>{{ dateTime(order.paymentExpireTime) }}</strong>
            </div>
            <div
              v-if="order.paymentExpireTime && canContinuePayment(order)"
              class="meta-item"
              :class="{ danger: isLocallyExpired(order) }"
            >
              <span>{{ ui.remaining }}</span>
              <strong>{{ countdownForOrder(order) }}</strong>
            </div>
            <div v-if="order.payTxnNo" class="meta-item">
              <span>{{ ui.transactionNo }}</span>
              <strong>{{ order.payTxnNo }}</strong>
            </div>
          </div>

          <div class="line-items">
            <div v-for="item in order.items || []" :key="item.id" class="line-item">
              <NuxtLink :to="`/products/${item.slug || item.productId}`" class="line-item__media">
                <img
                  v-if="item.productPic"
                  :src="item.productPic"
                  :alt="itemImageAlt(item)"
                  class="line-item__image"
                />
                <div v-else class="line-item__fallback">{{ productInitial(item.productName) }}</div>
              </NuxtLink>

              <div class="line-item__body">
                <NuxtLink :to="`/products/${item.slug || item.productId}`">{{ item.productName }}</NuxtLink>
                <p class="line-item__spec">{{ ui.specification }}: {{ attributeText(item.skuAttributesSnapshot) || '--' }}</p>
                <p v-if="item.addons?.length" class="line-item__spec">{{ ui.addons }}: {{ addonText(item.addons) }}</p>
                <div class="line-item__facts">
                  <span v-if="item.skuCode">{{ ui.sku }}: {{ item.skuCode }}</span>
                  <span>{{ ui.quantity }}: {{ item.quantity }}</span>
                </div>
              </div>
              <div class="line-item__meta">
                <span>{{ money(item.lineAmount) }}</span>
                <small>{{ ui.unitPrice }} {{ money(item.unitPrice) }}</small>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="action-row">
              <button
                v-if="canContinuePayment(order)"
                type="button"
                class="action-btn primary"
                :disabled="payingOrderId === order.id"
                @click="continuePayment(order)"
              >
                {{ payingOrderId === order.id ? ui.processingPayment : ui.continuePayment }}
              </button>
              <button
                v-if="canCancelOrder(order)"
                type="button"
                class="action-btn"
                :disabled="cancellingOrderId === order.id"
                @click="cancelOrder(order)"
              >
                {{ cancellingOrderId === order.id ? ui.cancellingOrder : ui.cancelOrder }}
              </button>
            </div>
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
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'

definePageMeta({
  layout: 'account',
})

const { lang, t } = useShopLocale()
const { money, statusText, attributeText, dateTime } = useShopFormat()
const { start: startDeadlineTicker, stop: stopDeadlineTicker, countdownText, isExpired } = usePaymentDeadline()
const cart = useShopCart()

const orders = ref<any[]>([])
const loading = ref(false)
const payingOrderId = ref<number | null>(null)
const cancellingOrderId = ref<number | null>(null)
let ordersPollTimer: ReturnType<typeof setInterval> | null = null

const ui = computed(() =>
  lang.value === 'zh'
      ? {
        specification: '\u89c4\u683c',
        addons: '\u9644\u52a0\u9879',
        quantity: '\u6570\u91cf',
        items: '\u4ef6\u5546\u54c1',
        unitPrice: '\u5355\u4ef7',
        sku: 'SKU',
        orderNumber: '\u8ba2\u5355\u53f7',
        purchasedAt: '\u8d2d\u4e70\u65f6\u95f4',
        paymentMethod: '\u652f\u4ed8\u65b9\u5f0f',
        paymentStatus: '\u652f\u4ed8\u72b6\u6001',
        payBefore: '\u6700\u665a\u652f\u4ed8\u65f6\u95f4',
        remaining: '\u5269\u4f59\u652f\u4ed8\u65f6\u95f4',
        shippingMethod: '\u914d\u9001\u65b9\u5f0f',
        transactionNo: '\u4ea4\u6613\u53f7',
        continuePayment: '\u7ee7\u7eed\u652f\u4ed8',
        cancelOrder: '\u53d6\u6d88\u8ba2\u5355',
        processingPayment: '\u6b63\u5728\u62c9\u8d77\u652f\u4ed8...',
        cancellingOrder: '\u6b63\u5728\u53d6\u6d88...',
        refreshing: '\u5237\u65b0\u4e2d...',
      }
    : {
        specification: 'Specification',
        addons: 'Add-ons',
        quantity: 'Quantity',
        items: 'items',
        unitPrice: 'Unit price',
        sku: 'SKU',
        orderNumber: 'Order no.',
        purchasedAt: 'Purchased',
        paymentMethod: 'Payment',
        paymentStatus: 'Payment status',
        payBefore: 'Pay before',
        remaining: 'Time remaining',
        shippingMethod: 'Shipping',
        transactionNo: 'Transaction no.',
        continuePayment: 'Continue payment',
        cancelOrder: 'Cancel order',
        processingPayment: 'Starting payment...',
        cancellingOrder: 'Cancelling...',
        refreshing: 'Refreshing...',
      },
)

const isLocallyExpired = (order: any) =>
  Boolean(order?.paymentExpireTime) && isExpired(order.paymentExpireTime)

const hasActivePendingOrders = computed(() =>
  orders.value.some((order) => canContinuePayment(order)),
)

const hasLocallyExpiredPendingOrders = computed(() =>
  orders.value.some((order) => canContinuePayment(order) && isLocallyExpired(order)),
)

const paymentStatusText = (status: string | null | undefined) => {
  if (status === 'PROCESSING') {
    return lang.value === 'zh' ? '\u652f\u4ed8\u5904\u7406\u4e2d' : 'Processing'
  }
  if (status === 'FAILED') {
    return lang.value === 'zh' ? '\u652f\u4ed8\u5931\u8d25' : 'Failed'
  }
  if (status === 'PENDING') {
    return lang.value === 'zh' ? '\u5f85\u652f\u4ed8' : 'Pending'
  }
  if (status === 'PAID') {
    return lang.value === 'zh' ? '\u5df2\u652f\u4ed8' : 'Paid'
  }
  if (status === 'CANCELLED') {
    return lang.value === 'zh' ? '\u5df2\u53d6\u6d88' : 'Cancelled'
  }
  if (status === 'EXPIRED') {
    return lang.value === 'zh' ? '\u5df2\u8d85\u65f6' : 'Expired'
  }
  return status || '--'
}

const statusTone = (status: string | null | undefined) => {
  if (status === 'PAID') {
    return 'is-paid'
  }
  if (status === 'PENDING_PAYMENT' || status === 'PAYMENT_PROCESSING') {
    return 'is-pending'
  }
  if (status === 'CANCELLED' || status === 'EXPIRED') {
    return 'is-muted'
  }
  return ''
}

const addonText = (addons: any) => {
  if (!Array.isArray(addons) || !addons.length) {
    return '--'
  }

  return addons
    .map((addon) => addon?.name || addon?.title || addon?.code)
    .filter(Boolean)
    .join(', ')
}

const productInitial = (name: string | null | undefined) =>
  (name || 'P').trim().charAt(0).toUpperCase() || 'P'

const itemImageAlt = (item: any) => {
  const spec = attributeText(item?.skuAttributesSnapshot)
  return spec ? `${item.productName} - ${spec}` : item.productName || 'Product image'
}

const itemCountText = (items: any[]) => {
  const count = Array.isArray(items) ? items.length : 0
  if (lang.value === 'zh') {
    return `${count}${ui.value.items}`
  }
  return `${count} ${ui.value.items}`
}

const canContinuePayment = (order: any) =>
  order?.status === 'PENDING_PAYMENT'
    && order?.paymentStatus !== 'PAID'
    && order?.paymentStatus !== 'CANCELLED'
    && order?.paymentStatus !== 'EXPIRED'
    && !isLocallyExpired(order)

const canCancelOrder = (order: any) =>
  order?.status === 'PENDING_PAYMENT'
    && order?.paymentStatus !== 'PAID'
    && order?.paymentStatus !== 'CANCELLED'
    && order?.paymentStatus !== 'EXPIRED'
    && !isLocallyExpired(order)

const countdownForOrder = (order: any) =>
  order?.paymentExpireTime ? countdownText(order.paymentExpireTime, lang.value) : '--'

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await useHttp('/api/order/list?pageNum=1&pageSize=20')
    orders.value = res?.code === 200 ? res.data?.records || [] : []
  } finally {
    loading.value = false
  }
}

const stopOrdersPolling = () => {
  if (!ordersPollTimer) {
    return
  }

  clearInterval(ordersPollTimer)
  ordersPollTimer = null
}

const startOrdersPolling = () => {
  if (!process.client || ordersPollTimer || !hasActivePendingOrders.value) {
    return
  }

  ordersPollTimer = window.setInterval(() => {
    fetchOrders()
  }, 15000)
}

const refreshOnVisibility = () => {
  if (!process.client || !hasActivePendingOrders.value) {
    return
  }

  if (document.visibilityState === 'hidden') {
    return
  }

  fetchOrders()
}

const continuePayment = async (order: any) => {
  payingOrderId.value = order.id
  try {
    const intentRes = await useHttp('/api/payment/intent', {
      method: 'POST',
      body: {
        orderId: order.id,
        paymentMethod: order.paymentMethod || 'alipay',
      },
      showError: false,
    }).catch(() => null)

    if (intentRes?.code !== 200) {
      await fetchOrders()
      return
    }

    if (intentRes.data?.nextAction === 'REDIRECT' && intentRes.data?.redirectUrl && process.client) {
      window.location.href = intentRes.data.redirectUrl
      return
    }

    if (intentRes.data?.providerKey === 'mock' && intentRes.data?.id) {
      const completeRes = await useHttp('/api/payment/mock/complete', {
        method: 'POST',
        body: {
          paymentIntentId: intentRes.data.id,
          mockResult: 'success',
        },
      }).catch(() => null)

      if (completeRes?.code === 200) {
        await cart.refreshCart()
      }
    }

    await fetchOrders()
  } finally {
    payingOrderId.value = null
  }
}

const cancelOrder = async (order: any) => {
  cancellingOrderId.value = order.id
  try {
    const res = await useHttp(`/api/order/${order.id}/cancel`, {
      method: 'PUT',
      showError: false,
    }).catch(() => null)

    if (res?.code === 200) {
      await fetchOrders()
    }
  } finally {
    cancellingOrderId.value = null
  }
}

onMounted(fetchOrders)

watch(hasActivePendingOrders, (active) => {
  if (active) {
    startDeadlineTicker()
    startOrdersPolling()
    return
  }

  stopOrdersPolling()
  stopDeadlineTicker()
})

watch(hasLocallyExpiredPendingOrders, async (expired) => {
  if (!expired || loading.value) {
    return
  }

  await fetchOrders()
})

onMounted(() => {
  if (process.client) {
    window.addEventListener('focus', refreshOnVisibility)
    document.addEventListener('visibilitychange', refreshOnVisibility)
  }
})

onBeforeUnmount(() => {
  stopOrdersPolling()
  stopDeadlineTicker()
  if (process.client) {
    window.removeEventListener('focus', refreshOnVisibility)
    document.removeEventListener('visibilitychange', refreshOnVisibility)
  }
})
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
.order-footer {
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

.refresh-btn,
.action-btn {
  min-height: 42px;
  padding: 0 16px;
  border-radius: 999px;
  font-weight: 700;
  cursor: pointer;
}

.refresh-btn,
.action-btn {
  border: 1px solid #dcdcdc;
  background: #fff;
}

.action-btn.primary {
  border-color: #111827;
  background: #111827;
  color: #fff;
}

.refresh-btn:disabled,
.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border-radius: 22px;
  border: 1px solid #e8e6e1;
  background:
    linear-gradient(180deg, #fff 0%, #fcfbf8 100%);
  padding: 22px 22px 18px;
}

.order-label {
  margin: 0 0 8px;
  color: #8a8578;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.order-top__main strong {
  display: block;
  color: #111;
  font-size: 18px;
}

.order-top__main p {
  margin: 6px 0 0;
  color: #667085;
}

.order-top__subline {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  margin-top: 6px;

  p {
    margin: 0;
  }

  span {
    color: #8a8578;
    font-size: 13px;
  }
}

.order-top__aside {
  display: flex;
  align-items: flex-end;
  flex-direction: column;
  gap: 12px;
}

.order-top__total {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  text-align: right;

  span {
    color: #667085;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  strong {
    color: #111;
    font-size: 22px;
    line-height: 1;
  }
}

.status-chip {
  padding: 8px 12px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #111827;
  font-size: 13px;
  font-weight: 700;
}

.status-chip.is-paid {
  background: #ecfdf3;
  color: #027a48;
}

.status-chip.is-pending {
  background: #fff7ed;
  color: #b54708;
}

.status-chip.is-muted {
  background: #f4f4f5;
  color: #52525b;
}

.order-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin: 18px 0 0;
}

.meta-item {
  min-height: 72px;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid #ece8df;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;

  span {
    color: #8a8578;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  strong {
    color: #1f2937;
    font-size: 14px;
    line-height: 1.45;
    word-break: break-word;
  }
}

.meta-item.danger {
  background: #fff6f4;
  border-color: #f5d2cc;
}

.meta-item.danger strong {
  color: #b42318;
}

.line-items {
  display: flex;
  flex-direction: column;
  gap: 0;
  margin: 20px 0 0;
}

.line-item {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr) auto;
  align-items: center;
  gap: 18px;
  padding: 18px 0;
  border-top: 1px solid #ebe7dd;
}

.line-item__media {
  width: 96px;
  height: 96px;
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #ece8df;
  text-decoration: none;
  display: block;
}

.line-item__image,
.line-item__fallback {
  width: 100%;
  height: 100%;
}

.line-item__image {
  object-fit: contain;
  padding: 8px;
  background: #fff;
}

.line-item__fallback {
  display: grid;
  place-items: center;
  color: #6b7280;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.line-item__body a {
  color: #111;
  font-weight: 700;
  text-decoration: none;
  font-size: 16px;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-item__spec {
  margin: 8px 0 0;
  color: #667085;
  font-size: 13px;
  line-height: 1.5;
}

.line-item__facts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  margin-top: 12px;

  span {
    color: #525866;
    font-size: 12px;
    letter-spacing: 0.01em;
  }
}

.line-item__meta {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  text-align: right;
}

.line-item__meta span,
.order-top__total strong {
  font-weight: 700;
  color: #111;
}

.line-item__meta small {
  color: #667085;
}

.order-footer {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid #ebe7dd;
  justify-content: flex-end;
}

.action-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 980px) {
  .order-meta {
    grid-template-columns: repeat(2, minmax(0, 1fr));
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
    padding: 18px;
    border-radius: 20px;
  }

  .panel-head,
  .order-top,
  .order-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .order-top__aside,
  .order-top__total {
    align-items: flex-start;
    text-align: left;
  }

  .order-card {
    padding: 18px 16px 16px;
    border-radius: 18px;
  }

  .order-meta {
    grid-template-columns: 1fr;
  }

  .line-item {
    grid-template-columns: 72px minmax(0, 1fr);
    align-items: flex-start;
  }

  .line-item__media {
    width: 72px;
    height: 72px;
    border-radius: 16px;
  }

  .line-item__meta {
    grid-column: 2;
    min-width: 0;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    gap: 12px;
    align-items: flex-start;
    text-align: left;
    margin-top: 4px;
  }

  .action-row {
    width: 100%;
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }
}

@media (max-width: 560px) {
  .panel-head {
    margin-bottom: 18px;
  }

  .refresh-btn {
    width: 100%;
  }

  .meta-item {
    min-height: auto;
  }

  .line-item {
    grid-template-columns: 64px minmax(0, 1fr);
    gap: 14px;
    padding: 16px 0;
  }

  .line-item__media {
    width: 64px;
    height: 64px;
    border-radius: 14px;
  }

  .line-item__body a {
    font-size: 15px;
  }

  .line-item__spec {
    font-size: 12px;
  }

  .line-item__facts {
    gap: 8px 12px;
    margin-top: 10px;
  }
}
</style>
