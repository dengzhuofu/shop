<template>
  <div class="payment-return-page">
    <div class="payment-return-card">
      <p class="eyebrow">Alipay Return</p>
      <h1>{{ ui.title }}</h1>
      <p class="message">{{ message }}</p>

      <div class="actions">
        <NuxtLink class="action primary" to="/account/orders">{{ ui.orders }}</NuxtLink>
        <NuxtLink class="action" to="/checkout">{{ ui.checkout }}</NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

const route = useRoute()
const cart = useShopCart()
const { lang } = useShopLocale()

const message = ref('')

const ui = computed(() =>
  lang.value === 'zh'
    ? {
        title: '正在确认支付结果',
        orders: '查看订单',
        checkout: '返回结算',
        success: '支付宝支付已确认，正在同步订单状态。',
        pending: '已收到回跳参数，正在校验签名与交易结果。',
        failed: '支付回跳已收到，但暂时未能确认，请稍后在订单页查看。',
        invalid: '缺少必要的回跳参数，暂时无法确认支付。',
      }
    : {
        title: 'Confirming your payment',
        orders: 'View orders',
        checkout: 'Back to checkout',
        success: 'Alipay payment confirmed. Syncing your order now.',
        pending: 'Return parameters received. Verifying signature and trade result.',
        failed: 'The return reached us, but payment could not be confirmed yet.',
        invalid: 'Missing required return parameters, so payment cannot be confirmed yet.',
      },
)

const normalizeQuery = () => {
  const params: Record<string, string> = {}
  Object.entries(route.query).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      if (value[0]) {
        params[key] = String(value[0])
      }
      return
    }
    if (value != null) {
      params[key] = String(value)
    }
  })
  return params
}

onMounted(async () => {
  const params = normalizeQuery()
  if (!params.out_trade_no || !params.sign) {
    message.value = ui.value.invalid
    return
  }

  message.value = ui.value.pending

  const res = await useHttp('/api/payment/alipay/return/confirm', {
    method: 'POST',
    body: params,
    showError: false,
    handleAuthError: false,
  }).catch(() => null)

  if (res?.code === 200) {
    message.value = res.data?.displayMessage || ui.value.success
    await cart.refreshCart()
    setTimeout(() => navigateTo('/account/orders'), 1200)
    return
  }

  message.value = res?.message || ui.value.failed
})
</script>

<style scoped lang="scss">
.payment-return-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at top, rgba(22, 119, 255, 0.12), transparent 32%),
    linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
}

.payment-return-card {
  width: min(520px, 100%);
  padding: 32px;
  border: 1px solid #dbe7f5;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 20px 60px rgba(17, 24, 39, 0.08);
}

.eyebrow {
  margin: 0 0 10px;
  color: #1677ff;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

h1 {
  margin: 0;
  color: #0f172a;
  font-size: 32px;
  line-height: 1.1;
}

.message {
  margin: 16px 0 0;
  color: #475467;
  font-size: 15px;
  line-height: 1.7;
}

.actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  padding: 0 18px;
  border: 1px solid #d0d5dd;
  border-radius: 999px;
  color: #0f172a;
  text-decoration: none;
  font-weight: 700;
}

.action.primary {
  border-color: #1677ff;
  background: #1677ff;
  color: #fff;
}
</style>
