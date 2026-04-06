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
              {{ lang.value === 'zh' ? 'EN' : 'ZH' }}
            </button>
          </div>
        </section>

        <section class="checkout-section">
          <div class="section-head">
            <div>
              <p class="step-label">{{ t('addressTitle') }}</p>
              <h2>{{ checkoutCopy.shippingAddress }}</h2>
            </div>
            <button type="button" class="ghost-btn" @click="saveAddressFromForm">
              {{ t('saveAddress') }}
            </button>
          </div>

          <div class="saved-addresses">
            <label
              v-for="item in addresses"
              :key="item.id"
              class="saved-address"
              :class="{ 'is-selected': selectedAddressId === String(item.id) }"
            >
              <input v-model="selectedAddressId" :value="String(item.id)" type="radio" />
              <span class="saved-address__body">
                <span class="saved-address__header">
                  <strong>{{ item.firstName }} {{ item.lastName }}</strong>
                  <em v-if="item.isDefault">{{ checkoutCopy.defaultAddress }}</em>
                </span>
                <small>{{ item.addressLine1 }}, {{ item.city }}, {{ item.state }} {{ item.zipCode }}</small>
              </span>
            </label>

            <label class="saved-address" :class="{ 'is-selected': selectedAddressId === 'manual' }">
              <input v-model="selectedAddressId" value="manual" type="radio" />
              <span class="saved-address__body">
                <span class="saved-address__header">
                  <strong>{{ checkoutCopy.manualAddress }}</strong>
                </span>
                <small>{{ addresses.length ? checkoutCopy.manualAddressHint : t('noAddress') }}</small>
              </span>
            </label>
          </div>

          <form class="address-form" @submit.prevent>
            <div class="form-group">
              <label>{{ t('country') }}</label>
              <input v-model="addressForm.country" type="text" autocomplete="country-name" />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>{{ t('firstName') }}</label>
                <input v-model="addressForm.firstName" type="text" autocomplete="given-name" />
              </div>
              <div class="form-group">
                <label>{{ t('lastName') }}</label>
                <input v-model="addressForm.lastName" type="text" autocomplete="family-name" />
              </div>
            </div>

            <div class="form-group">
              <label>{{ t('addressLine1') }}</label>
              <input v-model="addressForm.addressLine1" type="text" autocomplete="address-line1" />
            </div>

            <div class="form-group">
              <label>{{ t('addressLine2') }}</label>
              <input v-model="addressForm.addressLine2" type="text" autocomplete="address-line2" />
            </div>

            <div class="form-row three-cols">
              <div class="form-group">
                <label>{{ t('city') }}</label>
                <input v-model="addressForm.city" type="text" autocomplete="address-level2" />
              </div>
              <div class="form-group">
                <label>{{ t('state') }}</label>
                <input v-model="addressForm.state" type="text" autocomplete="address-level1" />
              </div>
              <div class="form-group">
                <label>{{ t('zipCode') }}</label>
                <input v-model="addressForm.zipCode" type="text" autocomplete="postal-code" />
              </div>
            </div>

            <div class="form-group">
              <label>{{ t('phone') }}</label>
              <input v-model="addressForm.phone" type="tel" autocomplete="tel" />
            </div>
          </form>

          <p class="address-help" :class="{ ready: canPreviewAddress }">
            {{ canPreviewAddress ? checkoutCopy.addressReady : checkoutCopy.addressIncomplete }}
          </p>
        </section>

        <section class="checkout-section">
          <div class="section-head compact">
            <div>
              <p class="step-label">{{ t('shipping') }}</p>
              <h2>{{ checkoutCopy.shippingMethod }}</h2>
            </div>
          </div>
          <div class="info-box">{{ shippingMethod }}</div>
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
                {{ coupon.code }} - {{ t('claim') }}
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
            <p>{{ paymentIntent.methodCode }} - {{ t('paymentReady') }}</p>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  addressesMatch,
  createEmptyAddressDraft,
  getAddressSignature,
  isAddressDraftComplete,
  toAddressDraft,
} from '~/utils/addressBook'

definePageMeta({
  layout: 'checkout',
})

const shippingMethod = 'UPS Ground/FedEx Home Delivery(2-5 Business Days)'

const { lang, setLang, t } = useShopLocale()
const { money, attributeText } = useShopFormat()
const cart = useShopCart()
const session = useShopSession()

const addresses = ref<any[]>([])
const availableCoupons = ref<any[]>([])
const claimableCoupons = ref<any[]>([])
const selectedAddressId = ref<string>('manual')
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
        contact: '联系方式',
        shippingAddress: '收货地址',
        shippingMethod: '配送方式',
        paymentTitle: '支付',
        secure: '当前保留支付层，先以模拟流程完成联调与验收。',
        cardNumber: '卡号',
        expiry: '有效期',
        cvc: '安全码',
        nameOnCard: '持卡人姓名',
        pointsTitle: '完成本次下单后可获得积分',
        pointsSubtitle: '积分逻辑后续会与会员系统打通。',
        noCoupon: '暂不使用优惠券',
        previewReady: '订单预览已生成',
        payNow: '立即支付',
        processing: '支付处理中...',
        completing: '完成中...',
        defaultAddress: '默认',
        manualAddress: '使用手动填写地址',
        manualAddressHint: '选择此项后，可直接编辑下方表单且不会重复保存相同地址。',
        addressReady: '当前地址信息完整，可以继续预览与结算。',
        addressIncomplete: '请补全国家、姓名、地址 1、城市、州/省和邮编后继续。',
        addressSaved: '地址已保存。',
        addressAlreadySaved: '该地址已存在，已自动为你选中。',
        addressRequired: '请先完善收货地址。',
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
        defaultAddress: 'Default',
        manualAddress: 'Use manual address',
        manualAddressHint: 'Select this to edit the form below without creating a duplicate saved address.',
        addressReady: 'This address is ready for preview and checkout.',
        addressIncomplete: 'Fill in country, name, address line 1, city, state, and ZIP code to continue.',
        addressSaved: 'Address saved.',
        addressAlreadySaved: 'This address is already saved.',
        addressRequired: 'Complete the shipping address before continuing.',
      },
)

const addressForm = reactive(createEmptyAddressDraft())

const cartSignature = computed(() =>
  cart.items.value.map((item) => `${item.cartItemId}:${item.quantity}:${(item.addons || []).join('|')}`).join(';'),
)
const selectedSavedAddress = computed(
  () => addresses.value.find((item) => String(item.id) === selectedAddressId.value) || null,
)
const canPreviewAddress = computed(
  () => Boolean(selectedSavedAddress.value) || isAddressDraftComplete(addressForm),
)
const isPayDisabled = computed(
  () => !preview.value.previewToken || !cart.items.value.length || !canPreviewAddress.value,
)

const applyAddress = (address: any) => {
  Object.assign(addressForm, toAddressDraft(address))
}

const fetchAddresses = async () => {
  const res = await useHttp('/api/address/list')
  addresses.value = res?.code === 200 ? res.data || [] : []

  const currentExists = addresses.value.some((item) => String(item.id) === selectedAddressId.value)
  if (currentExists && selectedSavedAddress.value) {
    applyAddress(selectedSavedAddress.value)
    return
  }

  if (addresses.value.length) {
    selectedAddressId.value = String(addresses.value[0].id)
    applyAddress(addresses.value[0])
    return
  }

  selectedAddressId.value = 'manual'
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

const findExistingAddress = () => addresses.value.find((item) => addressesMatch(item, addressForm)) || null

const saveAddressFromForm = async () => {
  if (!isAddressDraftComplete(addressForm)) {
    message.value = checkoutCopy.value.addressRequired
    return
  }

  const existing = findExistingAddress()
  if (existing) {
    selectedAddressId.value = String(existing.id)
    applyAddress(existing)
    message.value = checkoutCopy.value.addressAlreadySaved
    return
  }

  const res = await useHttp('/api/address', {
    method: 'POST',
    body: {
      ...toAddressDraft(addressForm),
      isDefault: !addresses.value.length,
    },
  })

  if (res?.code === 200) {
    await fetchAddresses()
    selectedAddressId.value = String(res.data.id)
    message.value = checkoutCopy.value.addressSaved
  }
}

const buildPreviewPayload = () => ({
  source: 'cart',
  cartItemIds: cart.items.value.map((item) => item.cartItemId),
  addressId: selectedSavedAddress.value?.id,
  shippingMethod,
  couponUserId: selectedCouponUserId.value || undefined,
})

const previewOrder = async () => {
  if (previewing.value || !cart.items.value.length || !canPreviewAddress.value) {
    preview.value = {}
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
  if (!canPreviewAddress.value) {
    message.value = checkoutCopy.value.addressRequired
    return
  }

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
        addressSnapshot: selectedSavedAddress.value ? undefined : toAddressDraft(addressForm),
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
      await cart.refreshCart()
      await navigateTo('/account/orders')
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

watch(selectedAddressId, (value) => {
  if (value === 'manual') {
    return
  }

  const matched = addresses.value.find((item) => String(item.id) === value)
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

watch(
  () => getAddressSignature(addressForm),
  () => {
    if (!bootstrapped.value || !selectedSavedAddress.value) {
      return
    }

    if (!addressesMatch(selectedSavedAddress.value, addressForm)) {
      selectedAddressId.value = 'manual'
    }
  },
)

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
  min-height: calc(100vh - 88px);
}

.checkout-container {
  max-width: 1460px;
  margin: 0 auto;
  padding: 32px 24px 56px;
  display: grid;
  grid-template-columns: minmax(0, 1.12fr) minmax(360px, 0.88fr);
  gap: 24px;
}

.checkout-main,
.checkout-sidebar {
  border: 1px solid rgba(17, 24, 39, 0.08);
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.checkout-main {
  padding: 36px;
}

.checkout-sidebar {
  padding: 32px;
  background:
    radial-gradient(circle at top, rgba(88, 204, 2, 0.08), transparent 34%),
    #fafaf7;
}

.sidebar-inner {
  position: sticky;
  top: 112px;
}

.checkout-section + .checkout-section {
  margin-top: 32px;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

  h2 {
    margin: 6px 0 0;
    font-size: 28px;
    line-height: 1.1;
    color: #111827;
  }

  &.compact h2 {
    font-size: 20px;
  }
}

.step-label {
  margin: 0;
  color: #667085;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.ghost-btn {
  min-height: 42px;
  padding: 0 16px;
  border: 1px solid #d0d5dd;
  border-radius: 999px;
  background: #fff;
  color: #111827;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.saved-addresses {
  display: grid;
  gap: 12px;
  margin-bottom: 18px;
}

.saved-address {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: start;
  gap: 12px;
  padding: 16px 18px;
  border: 1px solid #e4e7ec;
  border-radius: 20px;
  background: #fbfbfb;
  cursor: pointer;
  transition:
    border-color 0.2s ease,
    background 0.2s ease,
    transform 0.2s ease,
    box-shadow 0.2s ease;

  input {
    margin-top: 3px;
  }

  &:hover {
    transform: translateY(-1px);
    border-color: #111827;
  }

  &.is-selected {
    border-color: #111827;
    background: #f7fdf2;
    box-shadow: inset 0 0 0 1px rgba(17, 24, 39, 0.04);
  }

  small {
    margin-top: 6px;
    display: block;
    color: #667085;
    line-height: 1.5;
  }
}

.saved-address__header {
  display: flex;
  align-items: center;
  gap: 8px;

  strong {
    color: #111827;
    font-size: 15px;
  }

  em {
    padding: 4px 10px;
    border-radius: 999px;
    background: #111827;
    color: #fff;
    font-size: 11px;
    font-style: normal;
    font-weight: 700;
  }
}

.saved-address__body {
  min-width: 0;
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
    color: #344054;
    font-size: 13px;
    font-weight: 700;
  }

  input,
  select {
    min-height: 50px;
    padding: 0 14px;
    border: 1px solid #d0d5dd;
    border-radius: 16px;
    background: #fff;
    font-size: 14px;
    color: #111827;
    outline: none;
  }

  input:focus,
  select:focus {
    border-color: #111827;
    box-shadow: 0 0 0 4px rgba(17, 24, 39, 0.08);
  }
}

.address-help {
  margin: 14px 0 0;
  color: #b54708;
  font-size: 13px;
  line-height: 1.6;

  &.ready {
    color: #027a48;
  }
}

.info-box {
  min-height: 58px;
  display: flex;
  align-items: center;
  padding: 0 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f9fafb 0%, #f3f4f6 100%);
  color: #475467;
}

.payment-methods {
  border: 1px solid #d0d5dd;
  border-radius: 24px;
  overflow: hidden;
  background: #fff;
}

.payment-option {
  border-bottom: 1px solid #eaecf0;

  &:last-child {
    border-bottom: none;
  }

  &.disabled {
    opacity: 0.5;
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
  border: 1px solid #cfd4dc;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.radio-inner {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #111827;
}

.option-name {
  flex: 1;
  font-weight: 700;
  color: #111827;
}

.card-icons {
  display: flex;
  gap: 6px;
}

.card-icon {
  padding: 3px 7px;
  border: 1px solid #eaecf0;
  border-radius: 8px;
  background: #fff;
  font-size: 10px;
  font-weight: 800;
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
  background: #fcfcfd;
}

.payment-note {
  margin: 0 0 14px;
  color: #667085;
  font-size: 14px;
}

.action-stack {
  margin-top: 18px;
  display: grid;
  gap: 12px;
}

.pay-now-btn {
  min-height: 54px;
  border: none;
  border-radius: 999px;
  background: #111827;
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }

  &.ghost {
    background: #eef2f6;
    color: #111827;
  }
}

.status-message {
  margin: 12px 0 0;
  color: #475467;
  font-size: 14px;
  line-height: 1.6;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 22px;
}

.cart-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
}

.item-img-wrapper {
  position: relative;
  width: 72px;
  height: 72px;
  padding: 4px;
  border-radius: 18px;
  border: 1px solid #e4e7ec;
  background: #fff;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 14px;
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
  background: #6b7280;
  color: #fff;
  font-size: 12px;
  font-weight: 800;
}

.item-title {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 700;
  color: #111827;
}

.item-variant {
  margin: 0;
  color: #667085;
  font-size: 12px;
  line-height: 1.45;
}

.item-price {
  font-weight: 800;
  color: #111827;
}

.points-banner,
.preview-state,
.payment-state {
  padding: 16px 18px;
  border-radius: 20px;
  background: #fff;
  border: 1px solid #e4e7ec;
  margin-bottom: 18px;

  p {
    margin: 6px 0 0;
    color: #667085;
    font-size: 13px;
    line-height: 1.55;
    word-break: break-all;
  }
}

.discount-section {
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #dde3ea;
}

.discount-input select {
  width: 100%;
  min-height: 48px;
  padding: 0 14px;
  border: 1px solid #d0d5dd;
  border-radius: 16px;
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
  border: 1px solid #111827;
  border-radius: 999px;
  background: #fff;
  padding: 8px 12px;
  font-weight: 700;
  cursor: pointer;
}

.summary-lines {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #dde3ea;
}

.line,
.total-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.line {
  color: #475467;
}

.line.discount {
  color: #c5221f;
}

.total-line {
  font-size: 20px;
  font-weight: 800;
  color: #111827;
}

@media (max-width: 1100px) {
  .checkout-container {
    grid-template-columns: 1fr;
  }

  .sidebar-inner {
    position: static;
  }
}

@media (max-width: 720px) {
  .checkout-container {
    padding: 20px 16px 36px;
  }

  .checkout-main,
  .checkout-sidebar {
    padding: 22px 18px;
    border-radius: 24px;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;

    h2 {
      font-size: 24px;
    }
  }

  .form-row,
  .form-row.three-cols,
  .cart-item {
    grid-template-columns: 1fr;
  }

  .item-price {
    justify-self: start;
  }
}
</style>
