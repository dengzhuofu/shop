<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <main class="checkout-main">
        <section class="checkout-section user-section">
          <div class="section-head">
            <div>
              <p class="step-label">{{ checkoutCopy.contact }}</p>
              <h2>{{ session.user.value?.email || '--' }}</h2>
            </div>
            <button type="button" class="ghost-btn" @click="toggleLang">
              {{ lang.value === 'zh' ? 'EN' : '中文' }}
            </button>
          </div>
        </section>

        <section class="checkout-section">
          <div class="section-head">
            <div>
              <p class="step-label">{{ t('addressTitle') }}</p>
              <h2>{{ checkoutCopy.shippingAddress }}</h2>
            </div>
            <button type="button" class="ghost-btn" @click="saveAddressFromForm">{{ t('saveAddress') }}</button>
          </div>

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
              <input v-model="addressForm.country" type="text" />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>{{ t('firstName') }}</label>
                <input v-model="addressForm.firstName" type="text" />
              </div>
              <div class="form-group">
                <label>{{ t('lastName') }}</label>
                <input v-model="addressForm.lastName" type="text" />
              </div>
            </div>

            <div class="form-group">
              <label>{{ t('addressLine1') }}</label>
              <input v-model="addressForm.addressLine1" type="text" />
            </div>

            <div class="form-group">
              <label>{{ t('addressLine2') }}</label>
              <input v-model="addressForm.addressLine2" type="text" />
            </div>

            <div class="form-row three-cols">
              <div class="form-group">
                <label>{{ t('city') }}</label>
                <input v-model="addressForm.city" type="text" />
              </div>
              <div class="form-group">
                <label>{{ t('state') }}</label>
                <input v-model="addressForm.state" type="text" />
              </div>
              <div class="form-group">
                <label>{{ t('zipCode') }}</label>
                <input v-model="addressForm.zipCode" type="text" />
              </div>
            </div>

            <div class="form-group">
              <label>{{ t('phone') }}</label>
              <input v-model="addressForm.phone" type="tel" />
            </div>
          </form>
        </section>

        <section class="checkout-section">
          <div class="section-head compact">
            <div>
              <p class="step-label">{{ t('shipping') }}</p>
              <h2>{{ checkoutCopy.shippingMethod }}</h2>
            </div>
          </div>
          <div class="info-box">UPS Ground/FedEx Home Delivery(2-5 Business Days)</div>
        </section>

        <section class="checkout-section">
          <div class="section-head compact">
            <div>
              <p class="step-label">{{ t('paymentMethod') }}</p>
              <h2>{{ checkoutCopy.paymentTitle }}</h2>
            </div>
          </div>

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
                </div>
              </div>

              <div class="option-body">
                <p class="payment-note">{{ checkoutCopy.secure }}</p>
                <div class="card-form">
                  <input type="text" :placeholder="checkoutCopy.cardNumber" />
                  <div class="form-row">
                    <input type="text" :placeholder="checkoutCopy.expiry" />
                    <input type="text" :placeholder="checkoutCopy.cvc" />
                  </div>
                  <input type="text" :placeholder="checkoutCopy.nameOnCard" />
                </div>
              </div>
            </div>

            <div class="payment-option disabled">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">Shop Pay</span>
                <span class="brand-text shop">shop</span>
              </div>
            </div>

            <div class="payment-option disabled">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">PayPal</span>
                <span class="brand-text paypal">PayPal</span>
              </div>
            </div>
          </div>

          <div class="action-stack">
            <button
              type="button"
              class="pay-now-btn"
              :disabled="isPayDisabled || isPaying"
              @click="startPayment"
            >
              {{ isPaying ? checkoutCopy.processing : checkoutCopy.payNow }}
            </button>
            <button
              type="button"
              class="pay-now-btn ghost"
              :disabled="!paymentIntent?.id || completingPayment"
              @click="completePayment"
            >
              {{ completingPayment ? checkoutCopy.completing : t('completeMockPayment') }}
            </button>
          </div>

          <p v-if="message" class="status-message">{{ message }}</p>
        </section>
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
            <strong>{{ checkoutCopy.pointsTitle }}</strong>
            <p>{{ checkoutCopy.pointsSubtitle }}</p>
          </div>

          <div class="discount-section">
            <div class="discount-input">
              <select v-model="selectedCouponUserId">
                <option :value="null">{{ checkoutCopy.noCoupon }}</option>
                <option v-for="coupon in availableCoupons" :key="coupon.couponUserId" :value="coupon.couponUserId">
                  {{ coupon.code }} - {{ coupon.title }}
                </option>
              </select>
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
              <span>{{ t('subtotal') }}</span>
              <span>{{ money(preview.subtotal || cart.subtotal.value) }}</span>
            </div>
            <div class="line">
              <span>{{ t('shipping') }}</span>
              <span>{{ money(preview.shippingAmount || 0) }}</span>
            </div>
            <div class="line">
              <span>{{ t('tax') }}</span>
              <span>{{ money(preview.taxAmount || 0) }}</span>
            </div>
            <div v-if="preview.discountAmount" class="line discount">
              <span>{{ t('coupon') }}</span>
              <span>-{{ money(preview.discountAmount || 0) }}</span>
            </div>
          </div>

          <div class="total-line">
            <span>{{ t('total') }}</span>
            <strong>{{ money(preview.totalAmount || cart.subtotal.value) }}</strong>
          </div>

          <div v-if="preview.previewToken" class="preview-state">
            <strong>{{ checkoutCopy.previewReady }}</strong>
            <p>{{ preview.previewToken }}</p>
          </div>

          <div v-if="paymentIntent" class="payment-state">
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

definePageMeta({
  layout: 'account',
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
const bootstrapped = ref(false)
const previewing = ref(false)
const isPaying = ref(false)
const completingPayment = ref(false)

const checkoutCopy = computed(() =>
  lang.value === 'zh'
    ? {
        contact: '联系邮箱',
        shippingAddress: '配送地址',
        shippingMethod: '配送方式',
        paymentTitle: '支付',
        secure: '支付层已保留，当前为模拟支付流程。',
        cardNumber: '银行卡号',
        expiry: '有效期',
        cvc: '安全码',
        nameOnCard: '持卡人姓名',
        pointsTitle: '完成支付后将获得积分',
        pointsSubtitle: '积分能力后续可接入会员体系。',
        noCoupon: '暂不使用优惠券',
        previewReady: '订单预览已生成',
        payNow: '支付并创建订单',
        processing: '处理中...',
        completing: '完成中...',
      }
    : {
        contact: 'Contact',
        shippingAddress: 'Shipping address',
        shippingMethod: 'Shipping method',
        paymentTitle: 'Payment',
        secure: 'The payment layer is preserved and currently runs as a mock flow.',
        cardNumber: 'Card number',
        expiry: 'Expiration date',
        cvc: 'Security code',
        nameOnCard: 'Name on card',
        pointsTitle: 'Complete this purchase to earn reward points',
        pointsSubtitle: 'Reward logic will connect to the member system later.',
        noCoupon: 'No coupon selected',
        previewReady: 'Order preview ready',
        payNow: 'Pay now',
        processing: 'Processing...',
        completing: 'Completing...',
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

const cartSignature = computed(() =>
  cart.items.value.map((item) => `${item.cartItemId}:${item.quantity}:${(item.addons || []).join('|')}`).join(';'),
)

const isPayDisabled = computed(() => !preview.value.previewToken || !cart.items.value.length)

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
  if (value === 'manual') {
    return
  }
  const matched = addresses.value.find((item) => item.id === value)
  if (matched) {
    applyAddress(matched)
  }
})

watch([selectedAddressId, selectedCouponUserId, cartSignature], async () => {
  if (!bootstrapped.value) {
    return
  }
  paymentIntent.value = null
  await previewOrder()
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
  claimableCoupons.value =
    availableRes?.code === 200 ? (availableRes.data || []).filter((coupon: any) => !coupon.claimed) : []
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
  if (previewing.value || !cart.items.value.length || typeof selectedAddressId.value !== 'number') {
    return
  }

  previewing.value = true
  try {
    const res = await useHttp('/api/order/preview', {
      method: 'POST',
      body: buildPreviewPayload(),
    })
    if (res?.code === 200) {
      preview.value = res.data
      message.value = t('paymentPending')
    }
  } finally {
    previewing.value = false
  }
}

const startPayment = async () => {
  if (!preview.value.previewToken) {
    await previewOrder()
  }
  if (!preview.value.previewToken) {
    message.value = t('previewRequired')
    return
  }

  isPaying.value = true
  try {
    const res = await useHttp('/api/order/create', {
      method: 'POST',
      body: {
        ...buildPreviewPayload(),
        previewToken: preview.value.previewToken,
        addressSnapshot: typeof selectedAddressId.value === 'number' ? undefined : addressForm,
        remark: 'Checkout creates order on pay action',
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
  } finally {
    isPaying.value = false
  }
}

const completePayment = async () => {
  if (!paymentIntent.value?.id) return
  completingPayment.value = true
  try {
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
  } finally {
    completingPayment.value = false
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
  bootstrapped.value = true
  await previewOrder()
})
</script>

<style scoped lang="scss">
.checkout-page {
  min-height: calc(100vh - 176px);
}

.checkout-container {
  max-width: 1420px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 1.12fr) minmax(360px, 0.88fr);
  gap: 0;
}

.checkout-main {
  padding: 42px 48px 56px;
  background: #fff;
}

.checkout-sidebar {
  padding: 42px 40px 56px;
  background: #faf9f5;
  border-left: 1px solid #ececec;
}

.sidebar-inner {
  position: sticky;
  top: 120px;
}

.checkout-section {
  margin-bottom: 34px;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

  h2 {
    margin: 6px 0 0;
    font-size: 24px;
    line-height: 1.15;
    color: #111;
  }

  &.compact h2 {
    font-size: 20px;
  }
}

.step-label {
  margin: 0;
  color: #7b7b7b;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.ghost-btn {
  min-height: 40px;
  padding: 0 14px;
  border: 1px solid #d9d9d9;
  border-radius: 999px;
  background: #fff;
  font-weight: 700;
  cursor: pointer;
}

.saved-addresses {
  display: grid;
  gap: 10px;
  margin-bottom: 18px;
}

.saved-address {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 10px;
  padding: 14px 16px;
  border: 1px solid #e3e3e3;
  border-radius: 16px;
  background: #fafafa;

  small {
    display: block;
    margin-top: 5px;
    color: #717171;
    line-height: 1.45;
  }
}

.address-form,
.card-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.form-row.three-cols {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    color: #4c4c4c;
    font-size: 13px;
    font-weight: 600;
  }

  input,
  select {
    min-height: 48px;
    padding: 0 14px;
    border: 1px solid #d9d9d9;
    border-radius: 14px;
    background: #fff;
    font-size: 14px;
    outline: none;
  }
}

.info-box {
  min-height: 56px;
  display: flex;
  align-items: center;
  padding: 0 18px;
  border-radius: 16px;
  background: #f6f6f6;
  color: #666;
}

.payment-methods {
  border: 1px solid #d9d9d9;
  border-radius: 20px;
  overflow: hidden;
}

.payment-option {
  border-bottom: 1px solid #e8e8e8;

  &:last-child {
    border-bottom: none;
  }

  &.disabled {
    opacity: 0.55;
  }
}

.option-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px;
}

.radio-wrap {
  width: 18px;
  height: 18px;
  border: 1px solid #cfcfcf;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.radio-inner {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #111;
}

.option-name {
  flex: 1;
  font-weight: 600;
}

.card-icons {
  display: flex;
  gap: 6px;
}

.card-icon {
  padding: 2px 6px;
  border: 1px solid #ececec;
  border-radius: 6px;
  background: #fff;
  font-size: 10px;
  font-weight: 700;
}

.brand-text {
  font-weight: 800;

  &.shop {
    color: #5a31f4;
  }

  &.paypal {
    color: #003087;
  }
}

.option-body {
  padding: 0 18px 18px;
  background: #fcfcfc;
}

.payment-note {
  margin: 0 0 14px;
  color: #666;
  font-size: 14px;
}

.action-stack {
  margin-top: 18px;
  display: grid;
  gap: 12px;
}

.pay-now-btn {
  min-height: 52px;
  border: none;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }

  &.ghost {
    background: #f0f0f0;
    color: #111;
  }
}

.status-message {
  margin: 12px 0 0;
  color: #666;
  font-size: 14px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.cart-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
}

.item-img-wrapper {
  position: relative;
  width: 70px;
  height: 70px;
  padding: 4px;
  border-radius: 16px;
  border: 1px solid #e7e7e7;
  background: #fff;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.item-qty {
  position: absolute;
  top: -6px;
  right: -6px;
  min-width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: #727272;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.item-title {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 700;
}

.item-variant {
  margin: 0;
  color: #696969;
  font-size: 12px;
  line-height: 1.4;
}

.item-price {
  font-weight: 700;
}

.points-banner,
.preview-state,
.payment-state {
  padding: 16px 18px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid #e7e7e7;
  margin-bottom: 20px;

  p {
    margin: 6px 0 0;
    color: #666;
    font-size: 13px;
    line-height: 1.5;
    word-break: break-all;
  }
}

.discount-section {
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e4e4e4;
}

.discount-input select {
  width: 100%;
  min-height: 48px;
  padding: 0 14px;
  border: 1px solid #d9d9d9;
  border-radius: 14px;
  background: #fff;
  font-size: 14px;
}

.claimable-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.claim-btn {
  border: 1px solid #111;
  border-radius: 999px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}

.summary-lines {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e4e4e4;
}

.line,
.total-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.line.discount {
  color: #c5221f;
}

.total-line {
  font-size: 18px;
  font-weight: 800;
}

@media (max-width: 1080px) {
  .checkout-container {
    grid-template-columns: 1fr;
  }

  .checkout-sidebar {
    border-left: none;
    border-top: 1px solid #ececec;
  }

  .sidebar-inner {
    position: static;
  }
}

@media (max-width: 720px) {
  .checkout-main,
  .checkout-sidebar {
    padding: 26px 18px 34px;
  }

  .form-row,
  .form-row.three-cols {
    grid-template-columns: 1fr;
  }

  .cart-item {
    grid-template-columns: auto 1fr;
  }

  .item-price {
    grid-column: 2;
  }
}
</style>
