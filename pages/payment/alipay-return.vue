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
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const route = useRoute()
const cart = useShopCart()
const { lang } = useShopLocale()

const message = ref('')
const checking = ref(false)
const confirmAttempts = ref(0)
let retryTimer: ReturnType<typeof setTimeout> | null = null
let redirectTimer: ReturnType<typeof setTimeout> | null = null
const maxConfirmAttempts = 5

const ui = computed(() =>
  lang.value === 'zh'
    ? {
        title: '\u6b63\u5728\u786e\u8ba4\u652f\u4ed8\u7ed3\u679c',
        orders: '\u67e5\u770b\u8ba2\u5355',
        checkout: '\u8fd4\u56de\u7ed3\u7b97\u9875',
        success: '\u652f\u4ed8\u5b9d\u652f\u4ed8\u5df2\u786e\u8ba4\uff0c\u6b63\u5728\u540c\u6b65\u8ba2\u5355\u72b6\u6001\u3002',
        pending: '\u5df2\u6536\u5230\u56de\u8df3\u53c2\u6570\uff0c\u6b63\u5728\u6821\u9a8c\u7b7e\u540d\u5e76\u6838\u5b9e\u4ea4\u6613\u72b6\u6001\u3002',
        retrying: '\u652f\u4ed8\u7ed3\u679c\u4ecd\u5728\u540c\u6b65\u4e2d\uff0c\u7cfb\u7edf\u6b63\u5728\u518d\u6b21\u786e\u8ba4...',
        failed: '\u5df2\u6536\u5230\u652f\u4ed8\u56de\u8df3\uff0c\u4f46\u6682\u65f6\u8fd8\u65e0\u6cd5\u786e\u8ba4\u652f\u4ed8\u7ed3\u679c\uff0c\u8bf7\u524d\u5f80\u8ba2\u5355\u9875\u7ee7\u7eed\u67e5\u770b\u3002',
        expired: '\u8be5\u8ba2\u5355\u5df2\u8d85\u65f6\uff0c\u7cfb\u7edf\u5df2\u81ea\u52a8\u5173\u5355\u3002',
        invalid: '\u7f3a\u5c11\u5fc5\u8981\u7684\u56de\u8df3\u53c2\u6570\uff0c\u6682\u65f6\u65e0\u6cd5\u786e\u8ba4\u652f\u4ed8\u3002',
      }
    : {
        title: 'Confirming your payment',
        orders: 'View orders',
        checkout: 'Back to checkout',
        success: 'Alipay payment confirmed. Syncing your order now.',
        pending: 'Return parameters received. Verifying signature and trade result.',
        retrying: 'Payment confirmation is still syncing. Retrying now.',
        failed: 'The payment return reached us, but the result could not be confirmed yet.',
        expired: 'This order has expired and was closed automatically.',
        invalid: 'Missing required return parameters, so payment cannot be confirmed yet.',
      },
)

const getRawQueryParam = (targetKey: string) => {
  if (!process.client) {
    return null
  }

  const rawQuery = window.location.search.startsWith('?')
    ? window.location.search.slice(1)
    : window.location.search

  if (!rawQuery) {
    return null
  }

  for (const pair of rawQuery.split('&')) {
    if (!pair) {
      continue
    }

    const [rawKey, ...rawValueParts] = pair.split('=')
    const key = decodeURIComponent(rawKey || '')
    if (key !== targetKey) {
      continue
    }

    return decodeURIComponent(rawValueParts.join('=') || '')
  }

  return null
}

const normalizeQuery = () => {
  const params: Record<string, string> = {}
  Object.entries(route.query).forEach(([key, value]) => {
    if (key === 'lang') {
      return
    }

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

  const rawSign = getRawQueryParam('sign')
  if (rawSign) {
    params.sign = rawSign
  }

  return params
}

const clearTimers = () => {
  if (retryTimer) {
    clearTimeout(retryTimer)
    retryTimer = null
  }
  if (redirectTimer) {
    clearTimeout(redirectTimer)
    redirectTimer = null
  }
}

const scheduleRedirect = (delay = 1200) => {
  if (!process.client) {
    return
  }

  if (redirectTimer) {
    clearTimeout(redirectTimer)
  }

  redirectTimer = window.setTimeout(() => navigateTo('/account/orders'), delay)
}

const isPendingIntent = (status: string | null | undefined) => status === 'CREATED'

const isExpiredMessage = (value: string | null | undefined) =>
  (value || '').toLowerCase().includes('expired')

const confirmReturn = async () => {
  const params = normalizeQuery()
  if (!params.out_trade_no || !params.sign) {
    message.value = ui.value.invalid
    return
  }

  checking.value = true
  confirmAttempts.value += 1
  message.value = confirmAttempts.value > 1 ? ui.value.retrying : ui.value.pending

  const res = await useHttp('/api/payment/alipay/return/confirm', {
    method: 'POST',
    body: params,
    showError: false,
    handleAuthError: false,
  }).catch(() => null)

  if (res?.code === 200) {
    const intentStatus = res.data?.status
    const displayMessage = res.data?.displayMessage || ''

    if (intentStatus === 'SUCCEEDED') {
      message.value = displayMessage || ui.value.success
      await cart.refreshCart()
      scheduleRedirect()
      checking.value = false
      return
    }

    if (isExpiredMessage(displayMessage)) {
      message.value = ui.value.expired
      scheduleRedirect(1800)
      checking.value = false
      return
    }

    if (isPendingIntent(intentStatus) && confirmAttempts.value < maxConfirmAttempts && process.client) {
      message.value = displayMessage || ui.value.retrying
      retryTimer = window.setTimeout(confirmReturn, 3000)
      checking.value = false
      return
    }

    message.value = displayMessage || ui.value.failed
    scheduleRedirect(1800)
    checking.value = false
    return
  }

  message.value = res?.message || ui.value.failed
  scheduleRedirect(1800)
  checking.value = false
}

const refreshOnVisibility = () => {
  if (!process.client || document.visibilityState !== 'visible' || checking.value) {
    return
  }

  if (confirmAttempts.value > 0 && confirmAttempts.value < maxConfirmAttempts) {
    clearTimers()
    confirmReturn()
  }
}

onMounted(async () => {
  if (process.client) {
    window.addEventListener('focus', refreshOnVisibility)
    document.addEventListener('visibilitychange', refreshOnVisibility)
  }

  await confirmReturn()
})

onBeforeUnmount(() => {
  clearTimers()
  if (process.client) {
    window.removeEventListener('focus', refreshOnVisibility)
    document.removeEventListener('visibilitychange', refreshOnVisibility)
  }
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
