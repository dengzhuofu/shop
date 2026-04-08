<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <main class="checkout-main">
        <div class="checkout-main-inner">
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
            </div>

            <div class="address-selector-area">
              <div v-if="selectedSavedAddress" class="selected-address-card">
                <div class="address-details">
                  <strong>{{ selectedSavedAddress.firstName }} {{ selectedSavedAddress.lastName }}</strong>
                  <p>{{ selectedSavedAddress.addressLine1 }} {{ selectedSavedAddress.addressLine2 }}</p>
                  <p>{{ selectedSavedAddress.city }}, {{ selectedSavedAddress.state }} {{ selectedSavedAddress.zipCode }}</p>
                  <p>{{ selectedSavedAddress.country }}</p>
                  <p>{{ selectedSavedAddress.phone }}</p>
                </div>
                <button type="button" class="ghost-btn" @click="addressListOpen = true">
                  Change
                </button>
              </div>
              
              <div v-else class="empty-address-card">
                <button type="button" class="btn-select-address" @click="addressListOpen = true">
                  Select Saved Address
                </button>
              </div>
            </div>

            <form v-if="selectedAddressId === 'manual' || !selectedSavedAddress" class="address-form" @submit.prevent>
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
              <div class="payment-option" :class="{ selected: selectedPaymentMethod === 'alipay' }">
                <button type="button" class="option-header" @click="selectedPaymentMethod = 'alipay'">
                  <div class="radio-wrap">
                    <div v-if="selectedPaymentMethod === 'alipay'" class="radio-inner"></div>
                  </div>
                  <span class="option-name">Alipay</span>
                  <span class="brand-text alipay">Sandbox</span>
                </button>

                <div v-if="selectedPaymentMethod === 'alipay'" class="option-body">
                  <p class="payment-hint">{{ paymentUiCopy.alipayHint }}</p>
                </div>
              </div>

              <div class="payment-option" :class="{ selected: selectedPaymentMethod === 'credit_card' }">
                <button type="button" class="option-header" @click="selectedPaymentMethod = 'credit_card'">
                  <div class="radio-wrap">
                    <div v-if="selectedPaymentMethod === 'credit_card'" class="radio-inner"></div>
                  </div>
                  <span class="option-name">Credit card</span>
                  <div class="card-icons">
                    <span class="card-icon visa">VISA</span>
                    <span class="card-icon master">MC</span>
                    <span class="card-icon amex">AMEX</span>
                  </div>
                </button>

                <div v-if="selectedPaymentMethod === 'credit_card'" class="option-body">
                  <div class="card-form">
                    <input type="text" :placeholder="checkoutCopy.cardNumber" />
                    <div class="form-row">
                      <input type="text" :placeholder="checkoutCopy.expiry" />
                      <input type="text" :placeholder="checkoutCopy.cvc" />
                    </div>
                    <input type="text" :placeholder="checkoutCopy.nameOnCard" />
                  </div>
                  <p class="payment-hint">{{ paymentUiCopy.mockHint }}</p>
                </div>
              </div>

              <div class="payment-option disabled">
                <div class="option-header">
                  <div class="radio-wrap"></div>
                  <span class="option-name">PayPal</span>
                  <span class="brand-text paypal">PayPal</span>
                </div>
              </div>

              <div class="payment-option disabled">
                <div class="option-header">
                  <div class="radio-wrap"></div>
                  <span class="option-name">Affirm - Pay Over Time</span>
                  <span class="brand-text affirm">affirm</span>
                </div>
              </div>

              <div class="payment-option disabled">
                <div class="option-header">
                  <div class="radio-wrap"></div>
                  <span class="option-name">Klarna</span>
                  <span class="brand-text klarna">Klarna</span>
                </div>
              </div>
            </div>
            
            <div class="payment-note-container">
              <div class="save-info-checkbox">
                <input type="checkbox" id="save-info" />
                <label for="save-info">Save my information for a faster checkout</label>
              </div>
              <p class="payment-note">{{ checkoutCopy.secure }}</p>
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
                v-if="isMockFlow"
                type="button"
                class="pay-now-btn ghost"
                :disabled="!paymentIntent?.id || completingPayment"
                @click="completePayment"
              >
                {{ completingPayment ? checkoutCopy.completing : paymentUiCopy.completeMockPayment }}
              </button>
            </div>

            <p v-if="message" class="status-message">{{ message }}</p>
          </section>
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

          <div class="discount-section">
            <div class="discount-input">
              <input type="text" placeholder="Discount code" />
              <button type="button" class="btn-apply">Apply</button>
            </div>
          </div>

          <div class="summary-lines">
            <div class="line">
              <span>Subtotal</span>
              <span>{{ money(preview.subtotal || cart.subtotal.value) }}</span>
            </div>
            <div class="line">
              <span>{{ shippingMethod }}</span>
              <span>Free</span>
            </div>
          </div>

          <div class="total-line">
            <span>Total</span>
            <div class="total-price-group">
              <span class="currency-code">USD</span>
              <strong>{{ money(preview.totalAmount || cart.subtotal.value) }}</strong>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- Address Selection Modal -->
    <div v-if="addressListOpen" class="modal-overlay" @click.self="addressListOpen = false">
      <div class="modal-content address-list-modal">
        <div class="modal-header">
          <h2>Select Address</h2>
          <button class="close-btn" @click="addressListOpen = false">
            <span>✕</span>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="saved-addresses-modal">
            <label
              v-for="item in addresses"
              :key="item.id"
              class="saved-address"
              :class="{ 'is-selected': selectedAddressId === String(item.id) }"
            >
              <input v-model="selectedAddressId" :value="String(item.id)" type="radio" @change="addressListOpen = false" />
              <span class="saved-address__body">
                <span class="saved-address__header">
                  <strong>{{ item.firstName }} {{ item.lastName }}</strong>
                  <em v-if="item.isDefault">{{ checkoutCopy.defaultAddress }}</em>
                </span>
                <small>{{ item.addressLine1 }}, {{ item.city }}, {{ item.state }} {{ item.zipCode }}</small>
              </span>
            </label>
            
            <button type="button" class="btn-add-new-address" @click="openAddAddressModal">
              + Add new address
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add New Address Modal -->
    <AddAddressModal
      :is-open="addAddressModalOpen"
      @close="addAddressModalOpen = false"
      @save="handleSaveNewAddress"
    />
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
const selectedPaymentMethod = ref<'alipay' | 'credit_card'>('alipay')
const preview = ref<Record<string, any>>({})
const paymentIntent = ref<Record<string, any> | null>(null)
const message = ref('')
const bootstrapped = ref(false)
const previewing = ref(false)
const isPaying = ref(false)
const completingPayment = ref(false)

const addressListOpen = ref(false)
const addAddressModalOpen = ref(false)

const openAddAddressModal = () => {
  addressListOpen.value = false
  addAddressModalOpen.value = true
}

const handleSaveNewAddress = async (formData: any) => {
  const res = await useHttp('/api/address', {
    method: 'POST',
    body: {
      ...formData,
      addressLine1: formData.address,
      addressLine2: formData.apartment,
      isDefault: !addresses.value.length || formData.isDefault,
    },
  })
  if (res?.code === 200) {
    await fetchAddresses()
    selectedAddressId.value = String(res.data.id)
  }
}

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

const paymentUiCopy = computed(() =>
  lang.value === 'zh'
    ? {
        alipayHint: '优先走支付宝沙盒；支付完成后会回跳并在站内确认结果。',
        mockHint: '如果沙盒凭证还没配置好，会自动回退到 mock 支付流程。',
        completeMockPayment: '完成 mock 支付',
        redirectingToAlipay: '正在跳转到支付宝...',
      }
    : {
        alipayHint: 'Preferred path: redirect to Alipay Sandbox and confirm the result on return.',
        mockHint: 'If sandbox credentials are not ready yet, checkout can fall back to mock completion.',
        completeMockPayment: 'Complete mock payment',
        redirectingToAlipay: 'Redirecting to Alipay...',
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
  () => !cart.items.value.length || !canPreviewAddress.value || previewing.value,
)
const isMockFlow = computed(() => paymentIntent.value?.providerKey === 'mock')

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
          paymentMethod: selectedPaymentMethod.value,
        },
      })

      if (intentRes?.code === 200) {
        paymentIntent.value = intentRes.data
        message.value = intentRes.data?.displayMessage || t('paymentReady')
        if (intentRes.data?.nextAction === 'REDIRECT' && intentRes.data?.redirectUrl && process.client) {
          message.value = paymentUiCopy.value.redirectingToAlipay
          window.location.href = intentRes.data.redirectUrl
          return
        }
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
  min-height: 100vh;
  background: #fff;
}

.checkout-container {
  display: flex;
  margin: 0 auto;
  padding: 0;
  min-height: 100vh;
  border-top: 1px solid #e4e7ec;
}

.checkout-main {
  flex: 1.12;
  padding: 56px 48px;
  background: #fff;
  display: flex;
  justify-content: flex-end;
}

.checkout-main-inner {
  width: 100%;
  max-width: 640px;
}

.checkout-sidebar {
  flex: 0.88;
  padding: 56px 48px;
  background: #f9fafb;
  border-left: 1px solid #e4e7ec;
}

.sidebar-inner {
  width: 100%;
  max-width: 480px;
  position: sticky;
  top: 56px;
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

.address-selector-area {
  margin-bottom: 24px;
}

.selected-address-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  background: #fff;
}

.address-details {
  strong {
    display: block;
    margin-bottom: 4px;
    font-size: 15px;
    color: #111827;
  }
  p {
    margin: 0 0 2px;
    font-size: 14px;
    color: #475467;
  }
}

.empty-address-card {
  text-align: center;
}

.btn-select-address, .btn-add-new-address {
  width: 100%;
  padding: 14px;
  border: 1px dashed #d0d5dd;
  border-radius: 12px;
  background: transparent;
  color: #111827;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    border-color: #111827;
    background: #f9fafb;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content.address-list-modal {
  background: #fff;
  width: 100%;
  max-width: 500px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eaeaea;

  h2 {
    font-size: 18px;
    font-weight: 700;
    margin: 0;
    color: #111;
  }

  .close-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 16px;
    color: #666;
    padding: 4px;
  }
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
}

.saved-addresses-modal {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  border-radius: 8px;
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
  width: 100%;
  padding: 16px 18px;
  border: none;
  background: #fafafa;
  cursor: pointer;
  text-align: left;
}

.payment-option.selected .option-header {
  background: #fdfdfd;
}

.radio-wrap {
  width: 18px;
  height: 18px;
  border: 1px solid #cfd4dc;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.payment-option.selected .radio-wrap {
  border-color: #111;
  background: #111;
}

.radio-inner {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #fff;
}

.option-name {
  flex: 1;
  font-weight: 600;
  font-size: 14px;
  color: #111827;
}

.card-icons {
  display: flex;
  gap: 6px;
}

.card-icon {
  padding: 2px 6px;
  border: 1px solid #eaecf0;
  border-radius: 4px;
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
    font-style: italic;
  }

  &.alipay {
    color: #1677ff;
  }

  &.affirm {
    color: #000;
  }

  &.klarna {
    color: #ffb3c7;
  }
}

.option-body {
  padding: 18px;
  background: #fafafa;
  border-top: 1px solid #eaecf0;
}

.card-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-form input {
  min-height: 44px;
  padding: 0 14px;
  border: 1px solid #d0d5dd;
  border-radius: 4px;
  background: #fff;
  font-size: 14px;
  color: #111;
  outline: none;
}

.card-form input:focus {
  border-color: #111;
}

.payment-hint {
  margin: 0;
  color: #475467;
  font-size: 13px;
  line-height: 1.6;
}

.payment-note-container {
  margin-top: 18px;
}

.save-info-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;

  input[type="checkbox"] {
    width: 16px;
    height: 16px;
    border: 1px solid #d0d5dd;
    border-radius: 4px;
    cursor: pointer;
  }

  label {
    font-size: 14px;
    color: #111;
    cursor: pointer;
  }
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
