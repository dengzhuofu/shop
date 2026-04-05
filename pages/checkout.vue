<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <main class="checkout-main">
        <div class="checkout-header">
          <NuxtLink to="/" class="logo">
            <h2><i>isinwheel</i></h2>
          </NuxtLink>
        </div>

        <div class="checkout-section user-section">
          <div class="user-info">
            <span class="step-number">1</span>
            <span class="user-email">{{ session.user.value?.email }}</span>
            <button type="button" class="more-btn" @click="toggleLang">
              <MoreVerticalIcon class="icon" />
            </button>
          </div>
        </div>

        <div class="checkout-section address-section">
          <div v-if="addresses.length" class="saved-addresses">
            <label v-for="item in addresses" :key="item.id" class="saved-address">
              <input v-model="selectedAddressId" :value="item.id" type="radio" />
              <span>
                <strong>{{ item.firstName }} {{ item.lastName }}</strong>
                <small>{{ item.addressLine1 }}, {{ item.city }}, {{ item.state }} {{ item.zipCode }}</small>
              </span>
            </label>
            <label class="saved-address">
              <input v-model="selectedAddressId" value="manual" type="radio" />
              <span>
                <strong>{{ t('useManualAddress') }}</strong>
                <small>{{ t('noAddress') }}</small>
              </span>
            </label>
          </div>

          <form class="address-form" @submit.prevent>
            <div class="form-group">
              <label>{{ t('country') }}</label>
              <div class="select-wrapper">
                <input v-model="addressForm.country" type="text" />
                <ChevronDownIcon class="select-icon" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <input v-model="addressForm.firstName" type="text" :placeholder="t('firstName')" />
              </div>
              <div class="form-group">
                <input v-model="addressForm.lastName" type="text" :placeholder="t('lastName')" />
              </div>
            </div>

            <div class="form-group">
              <input v-model="addressForm.addressLine1" type="text" :placeholder="t('addressLine1')" />
            </div>

            <div class="form-group">
              <input v-model="addressForm.addressLine2" type="text" :placeholder="t('addressLine2')" />
            </div>

            <div class="form-row three-cols">
              <div class="form-group">
                <input v-model="addressForm.city" type="text" :placeholder="t('city')" />
              </div>
              <div class="form-group">
                <input v-model="addressForm.state" type="text" :placeholder="t('state')" />
              </div>
              <div class="form-group">
                <input v-model="addressForm.zipCode" type="text" :placeholder="t('zipCode')" />
              </div>
            </div>

            <div class="form-group">
              <div class="input-with-icon">
                <input v-model="addressForm.phone" type="tel" :placeholder="t('phone')" />
                <HelpCircleIcon class="help-icon" />
              </div>
            </div>

            <button type="button" class="save-address-btn" @click="saveAddressFromForm">
              {{ t('saveAddress') }}
            </button>
          </form>
        </div>

        <div class="checkout-section shipping-method">
          <h2 class="section-title">{{ t('shipping') }}</h2>
          <div class="info-box">UPS Ground/FedEx Home Delivery(2-5 Business Days)</div>
        </div>

        <div class="checkout-section payment-section">
          <h2 class="section-title">{{ t('paymentMethod') }}</h2>
          <p class="section-desc">{{ copy.secure }}</p>

          <div class="payment-methods">
            <div class="payment-option selected">
              <div class="option-header">
                <div class="radio-wrap">
                  <div class="radio-inner"></div>
                </div>
                <span class="option-name">Credit card</span>
                <div class="card-icons">
                  <span class="card-icon visa">VISA</span>
                  <span class="card-icon master">MC</span>
                  <span class="card-icon amex">AMEX</span>
                  <span class="card-icon more">+5</span>
                </div>
              </div>
              <div class="option-body">
                <div class="card-form">
                  <div class="form-group">
                    <div class="input-with-icon">
                      <input type="text" :placeholder="copy.cardNumber" />
                      <LockIcon class="lock-icon" />
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-group">
                      <input type="text" :placeholder="copy.expiry" />
                    </div>
                    <div class="form-group">
                      <div class="input-with-icon">
                        <input type="text" :placeholder="copy.cvc" />
                        <HelpCircleIcon class="help-icon" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <input type="text" :placeholder="copy.nameOnCard" />
                  </div>
                </div>
              </div>
            </div>

            <div class="payment-option">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">Shop Pay</span>
                <span class="brand-text shop">shop</span>
              </div>
            </div>
            <div class="payment-option">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">PayPal</span>
                <span class="brand-text paypal">PayPal</span>
              </div>
            </div>
          </div>

          <div class="action-stack">
            <button type="button" class="pay-now-btn secondary" @click="previewOrder">{{ t('previewOrder') }}</button>
            <button type="button" class="pay-now-btn" :disabled="!preview.previewToken" @click="createOrder">
              {{ t('createOrder') }}
            </button>
            <button type="button" class="pay-now-btn ghost" :disabled="!paymentIntent?.id" @click="completePayment">
              {{ t('completeMockPayment') }}
            </button>
          </div>
          <p v-if="message" class="status-message">{{ message }}</p>
        </div>
      </main>

      <aside class="checkout-sidebar">
        <div class="sidebar-inner">
          <div class="cart-items">
            <div v-for="item in cart.items.value" :key="item.cartItemId" class="cart-item">
              <div class="item-img-wrapper">
                <img :src="item.productPic" :alt="item.title" class="item-img" />
                <span class="item-qty">{{ item.quantity }}</span>
              </div>
              <div class="item-info">
                <h4 class="item-title">{{ item.title }}</h4>
                <p class="item-variant">{{ attributeText(item.attributes) }}</p>
                <p v-if="item.addons?.length" class="item-variant">
                  + {{ item.addons.map((addon: any) => addon.name).join(', ') }}
                </p>
              </div>
              <div class="item-price">{{ money(item.lineAmount) }}</div>
            </div>
          </div>

          <div class="points-banner">
            <div class="points-icon"><GiftIcon class="icon" /></div>
            <div class="points-text">
              <strong>{{ copy.pointsTitle }}</strong>
              <p>{{ copy.pointsSubtitle }}</p>
            </div>
          </div>

          <div class="discount-section">
            <div class="discount-input">
              <select v-model="selectedCouponUserId">
                <option :value="null">{{ copy.noCoupon }}</option>
                <option v-for="coupon in availableCoupons" :key="coupon.couponUserId" :value="coupon.couponUserId">
                  {{ coupon.code }} - {{ coupon.title }}
                </option>
              </select>
              <button type="button" class="apply-btn" @click="previewOrder">{{ t('applyCoupon') }}</button>
            </div>
            <div v-if="claimableCoupons.length" class="claimable-list">
              <button
                v-for="coupon in claimableCoupons"
                :key="coupon.couponId"
                type="button"
                class="claim-btn"
                @click="claimCoupon(coupon.couponId)"
              >
                {{ coupon.code }} · {{ t('claim') }}
              </button>
            </div>
          </div>

          <div class="summary-lines">
            <div class="line">
              <span class="label">{{ t('subtotal') }}</span>
              <span class="value">{{ money(preview.subtotal || cart.subtotal.value) }}</span>
            </div>
            <div class="line">
              <span class="label">{{ t('shipping') }}</span>
              <span class="value">{{ money(preview.shippingAmount || 0) }}</span>
            </div>
            <div class="line">
              <span class="label">{{ t('tax') }}</span>
              <span class="value">{{ money(preview.taxAmount || 0) }}</span>
            </div>
            <div v-if="preview.discountAmount" class="line">
              <span class="label">{{ t('coupon') }}</span>
              <span class="value">-{{ money(preview.discountAmount || 0) }}</span>
            </div>
          </div>

          <div class="total-line">
            <span class="label">{{ t('total') }}</span>
            <div class="value">
              <span class="currency">USD</span>
              <span class="amount">{{ money(preview.totalAmount || cart.subtotal.value) }}</span>
            </div>
          </div>

          <div v-if="paymentIntent" class="payment-tip">
            <strong>{{ t('paymentStep') }}</strong>
            <p>{{ paymentIntent.methodCode }} · {{ t('paymentReady') }}</p>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  ChevronDownIcon,
  GiftIcon,
  HelpCircleIcon,
  LockIcon,
  MoreVerticalIcon,
} from 'lucide-vue-next'

definePageMeta({
  layout: 'blank',
})

const { lang, setLang, t } = useShopLocale()
const { money, attributeText } = useShopFormat()
const cart = useShopCart()
const session = useShopSession()

const addresses = ref<any[]>([])
const availableCoupons = ref<any[]>([])
const claimableCoupons = ref<any[]>([])
const selectedAddressId = ref<number | 'manual' | null>(null)
const selectedCouponUserId = ref<number | null>(null)
const preview = ref<Record<string, any>>({})
const paymentIntent = ref<Record<string, any> | null>(null)
const message = ref('')

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        secure: '所有交易均为安全加密。',
        cardNumber: '银行卡号',
        expiry: '有效期 (MM / YY)',
        cvc: '安全码',
        nameOnCard: '持卡人姓名',
        pointsTitle: '完成订单后可获得积分',
        pointsSubtitle: '后续可用于兑换下次订单折扣。',
        noCoupon: '暂不使用优惠券',
      }
    : {
        secure: 'All transactions are secure and encrypted.',
        cardNumber: 'Card number',
        expiry: 'Expiration date (MM / YY)',
        cvc: 'Security code',
        nameOnCard: 'Name on card',
        pointsTitle: 'Complete this purchase to earn reward points',
        pointsSubtitle: 'Use your points to redeem a discount on your next order.',
        noCoupon: 'No coupon selected',
      },
)

const addressForm = reactive({
  country: 'United States',
  firstName: '',
  lastName: '',
  phone: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  zipCode: '',
})

const applyAddress = (address: any) => {
  if (!address) return
  addressForm.country = address.country || 'United States'
  addressForm.firstName = address.firstName || ''
  addressForm.lastName = address.lastName || ''
  addressForm.phone = address.phone || ''
  addressForm.addressLine1 = address.addressLine1 || ''
  addressForm.addressLine2 = address.addressLine2 || ''
  addressForm.city = address.city || ''
  addressForm.state = address.state || ''
  addressForm.zipCode = address.zipCode || ''
}

watch(selectedAddressId, (value) => {
  if (value === 'manual') return
  const matched = addresses.value.find((item) => item.id === value)
  if (matched) applyAddress(matched)
})

const fetchAddresses = async () => {
  const res = await useHttp('/api/address/list')
  addresses.value = res?.code === 200 ? res.data || [] : []
  if (addresses.value.length && !selectedAddressId.value) {
    selectedAddressId.value = addresses.value[0].id
    applyAddress(addresses.value[0])
  }
}

const refreshCoupons = async () => {
  const [myRes, availableRes] = await Promise.all([useHttp('/api/coupon/my'), useHttp('/api/coupon/available')])
  availableCoupons.value = myRes?.code === 200 ? myRes.data || [] : []
  claimableCoupons.value = availableRes?.code === 200 ? (availableRes.data || []).filter((coupon: any) => !coupon.claimed) : []
}

const claimCoupon = async (couponId: number) => {
  await useHttp(`/api/coupon/${couponId}/claim`, { method: 'POST' })
  await refreshCoupons()
}

const saveAddressFromForm = async () => {
  const res = await useHttp('/api/address', {
    method: 'POST',
    body: {
      ...addressForm,
      isDefault: !addresses.value.length,
    },
  })
  if (res?.code === 200) {
    await fetchAddresses()
    selectedAddressId.value = res.data.id
    message.value = t('saveAddress')
  }
}

const buildPreviewPayload = () => ({
  source: 'cart',
  cartItemIds: cart.items.value.map((item) => item.cartItemId),
  addressId: typeof selectedAddressId.value === 'number' ? selectedAddressId.value : undefined,
  shippingMethod: 'UPS Ground/FedEx Home Delivery(2-5 Business Days)',
  couponUserId: selectedCouponUserId.value || undefined,
})

const previewOrder = async () => {
  const res = await useHttp('/api/order/preview', {
    method: 'POST',
    body: buildPreviewPayload(),
  })
  if (res?.code === 200) {
    preview.value = res.data
    message.value = t('paymentPending')
  }
}

const createOrder = async () => {
  if (!preview.value.previewToken) {
    message.value = t('previewRequired')
    return
  }

  const res = await useHttp('/api/order/create', {
    method: 'POST',
    body: {
      ...buildPreviewPayload(),
      previewToken: preview.value.previewToken,
      addressSnapshot: typeof selectedAddressId.value === 'number' ? undefined : addressForm,
      remark: 'Restored checkout UI flow',
    },
  })

  if (res?.code === 200) {
    const intentRes = await useHttp('/api/payment/intent', {
      method: 'POST',
      body: {
        orderId: res.data.id,
        paymentMethod: 'credit_card',
      },
    })
    if (intentRes?.code === 200) {
      paymentIntent.value = intentRes.data
      message.value = t('paymentReady')
    }
    await cart.refreshCart()
  }
}

const completePayment = async () => {
  if (!paymentIntent.value?.id) return
  const res = await useHttp('/api/payment/mock/complete', {
    method: 'POST',
    body: {
      paymentIntentId: paymentIntent.value.id,
      mockResult: 'success',
    },
  })
  if (res?.code === 200) {
    paymentIntent.value = res.data
    message.value = t('orderSuccess')
  }
}

const toggleLang = () => {
  setLang(lang.value === 'en' ? 'zh' : 'en')
  if (process.client) {
    window.location.reload()
  }
}

onMounted(async () => {
  await session.fetchMe()
  if (!session.isLoggedIn.value) {
    await navigateTo('/login')
    return
  }
  await Promise.all([cart.refreshCart(), fetchAddresses(), refreshCoupons()])
})
</script>

<style scoped lang="scss">
.checkout-page { min-height: 100vh; background-color: #fff; font-family: 'Montserrat', sans-serif; color: #333; }
.checkout-container { display: flex; max-width: 1200px; margin: 0 auto; min-height: 100vh; }
.checkout-main { flex: 1; padding: 40px 5%; border-right: 1px solid #e6e6e6; padding-right: 6%; }
.checkout-header { margin-bottom: 40px; }
.checkout-header .logo { text-decoration: none; color: #111; }
.checkout-header .logo h2 { font-size: 28px; font-weight: 800; margin: 0; }
.checkout-section { margin-bottom: 40px; }
.section-title { font-size: 20px; font-weight: 600; margin-bottom: 8px; color: #111; }
.section-desc,.status-message { font-size: 14px; color: #666; margin-top: 12px; }
.user-section { display: flex; align-items: center; justify-content: center; position: relative; }
.user-section::before { content: 'OR'; position: absolute; top: -20px; left: 50%; transform: translateX(-50%); background: #fff; padding: 0 10px; font-size: 12px; color: #999; }
.user-section::after { content: ''; position: absolute; top: -10px; left: 0; right: 0; height: 1px; background: #e6e6e6; z-index: -1; }
.user-info { width: 100%; display: flex; align-items: center; border: 1px solid #e6e6e6; border-radius: 8px; padding: 12px 16px; background: #fff; }
.step-number { width: 24px; height: 24px; background: #f0f0f0; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; margin-right: 12px; }
.user-email { font-size: 14px; font-weight: 500; flex: 1; }
.more-btn { background: none; border: none; cursor: pointer; color: #666; padding: 4px; }
.more-btn .icon { width: 20px; height: 20px; }
.saved-addresses { display: grid; gap: 10px; margin-bottom: 18px; }
.saved-address { display: grid; grid-template-columns: auto minmax(0,1fr); gap: 10px; padding: 12px 14px; border: 1px solid #e6e6e6; border-radius: 8px; font-size: 13px; }
.saved-address small { display: block; color: #666; line-height: 1.4; margin-top: 4px; }
.address-form,.card-form { display: flex; flex-direction: column; gap: 16px; }
.form-group { position: relative; }
.form-group label { position: absolute; top: 6px; left: 12px; font-size: 11px; color: #666; z-index: 1; }
.form-group input,.form-group select { width: 100%; padding: 12px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; background: #fff; outline: none; }
.form-group input:focus,.form-group select:focus { border-color: #58cc02; box-shadow: 0 0 0 1px #58cc02; }
.select-wrapper,.input-with-icon { position: relative; }
.select-icon,.help-icon,.lock-icon { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); width: 16px; height: 16px; color: #999; pointer-events: none; }
.form-row { display: flex; gap: 16px; }
.form-row .form-group { flex: 1; }
.save-address-btn { padding: 12px 18px; border-radius: 6px; border: 1px solid #111; background: #fff; font-weight: 600; width: fit-content; }
.shipping-method .info-box { background: #f5f5f5; padding: 16px; border-radius: 6px; font-size: 14px; color: #666; text-align: center; }
.payment-methods { border: 1px solid #d9d9d9; border-radius: 8px; overflow: hidden; margin-bottom: 24px; }
.payment-option { border-bottom: 1px solid #d9d9d9; }
.payment-option:last-child { border-bottom: none; }
.payment-option.selected { background: #fafafa; }
.option-header { display: flex; align-items: center; padding: 16px; cursor: pointer; }
.radio-wrap { width: 18px; height: 18px; border: 1px solid #d9d9d9; border-radius: 50%; margin-right: 12px; display: flex; align-items: center; justify-content: center; background: #fff; }
.option-name { font-size: 14px; font-weight: 500; flex: 1; }
.card-icons { display: flex; gap: 4px; }
.card-icon { font-size: 10px; font-weight: 700; padding: 2px 6px; border-radius: 2px; border: 1px solid #eee; background: #fff; }
.card-icon.visa { color: #1a1f71; }
.card-icon.master { color: #ff5f00; }
.card-icon.amex { color: #002663; }
.card-icon.more { color: #666; }
.brand-text { font-weight: 800; font-size: 16px; }
.brand-text.shop { color: #5a31f4; }
.brand-text.paypal { color: #003087; }
.payment-option.selected .radio-wrap { border-color: #58cc02; }
.payment-option.selected .radio-inner { width: 10px; height: 10px; background: #58cc02; border-radius: 50%; }
.option-body { padding: 0 16px 16px; background: #fafafa; }
.action-stack { display: grid; gap: 12px; }
.pay-now-btn { width: 100%; padding: 16px; background: #58cc02; color: #fff; border: none; border-radius: 6px; font-size: 16px; font-weight: 600; cursor: pointer; transition: background .2s; }
.pay-now-btn.secondary { background: #111; }
.pay-now-btn.ghost { background: #f0f0f0; color: #111; }
.pay-now-btn:disabled { opacity: .45; cursor: not-allowed; }
.checkout-sidebar { flex: 0 0 45%; background: #fafafa; border-left: 1px solid #e6e6e6; padding: 40px 5%; }
.sidebar-inner { position: sticky; top: 40px; }
.cart-items { margin-bottom: 24px; }
.cart-item { display: flex; align-items: center; gap: 16px; margin-bottom: 14px; }
.item-img-wrapper { position: relative; width: 64px; height: 64px; background: #fff; border: 1px solid #e6e6e6; border-radius: 8px; padding: 4px; }
.item-img { width: 100%; height: 100%; object-fit: cover; }
.item-qty { position: absolute; top: -8px; right: -8px; width: 20px; height: 20px; background: rgba(114,114,114,.9); color: #fff; font-size: 12px; font-weight: 600; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
.item-info { flex: 1; }
.item-title { font-size: 14px; font-weight: 600; margin: 0 0 4px; color: #333; }
.item-variant { font-size: 12px; color: #666; margin: 0; }
.item-price { font-size: 14px; font-weight: 500; color: #333; }
.points-banner { display: flex; gap: 12px; background: #f0f0f0; padding: 16px; border-radius: 8px; margin-bottom: 24px; }
.points-text { font-size: 13px; color: #333; }
.points-text strong { display: block; margin-bottom: 4px; }
.points-text p { margin: 0; color: #666; }
.discount-section { margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px solid #e6e6e6; }
.discount-input { display: flex; gap: 12px; }
.discount-input select { flex: 1; padding: 12px 16px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; outline: none; }
.apply-btn { padding: 0 24px; background: #f0f0f0; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; font-weight: 600; }
.claimable-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
.claim-btn { border: 1px solid #111; border-radius: 999px; background: #fff; padding: 8px 12px; font-size: 12px; }
.summary-lines { margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px solid #e6e6e6; }
.line { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14px; }
.line .label { color: #333; }
.line .value { font-weight: 500; }
.total-line { display: flex; justify-content: space-between; align-items: center; }
.total-line .label { font-size: 16px; font-weight: 600; color: #333; }
.total-line .value { display: flex; align-items: baseline; gap: 8px; }
.currency { font-size: 12px; color: #666; }
.amount { font-size: 24px; font-weight: 700; color: #111; }
.payment-tip { margin-top: 20px; padding: 14px 16px; background: #111; color: #fff; border-radius: 10px; }
.payment-tip p { margin: 6px 0 0; color: rgba(255,255,255,.8); }
@media (max-width: 992px) { .checkout-container { flex-direction: column-reverse; } .checkout-main { border-right: none; padding: 40px 5%; } .checkout-sidebar { border-left: none; border-bottom: 1px solid #e6e6e6; padding: 40px 5%; } }
@media (max-width: 640px) { .form-row,.form-row.three-cols { flex-direction: column; gap: 16px; } .discount-input { flex-direction: column; } }
</style>
