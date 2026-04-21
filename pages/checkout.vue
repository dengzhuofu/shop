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
                {{ nextLangShortText }}
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
                  {{ checkoutCopy.change }}
                </button>
              </div>

              <div v-else class="empty-address-card">
                <button type="button" class="btn-select-address" @click="addressListOpen = true">
                  {{ checkoutCopy.selectAddress }}
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

              <button type="button" class="secondary-btn" @click="saveAddressFromForm">
                {{ checkoutCopy.saveAddress }}
              </button>
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
            </div>

            <div class="payment-note-container">
              <p class="payment-note">{{ checkoutCopy.secure }}</p>
              <p v-if="currentOrderId" class="payment-note strong">
                {{ paymentUiCopy.orderCreated }} #{{ currentOrderId }}
              </p>
              <p v-if="currentOrderExpireTime" class="payment-note">
                {{ paymentUiCopy.payBefore }} {{ dateTime(currentOrderExpireTime) }}
              </p>
              <p v-if="showCountdown" class="payment-note countdown" :class="{ expired: currentOrderExpired }">
                {{ paymentUiCopy.remaining }} {{ currentOrderCountdown }}
              </p>
            </div>

            <div class="action-stack">
              <button
                type="button"
                class="pay-now-btn"
                :disabled="isPayDisabled"
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
            <p v-if="currentOrderExpired" class="status-message expired-message">
              {{ paymentUiCopy.expired }}
            </p>

            <div v-if="currentOrderId" class="resume-box">
              <NuxtLink class="resume-link" to="/account/orders">
                {{ paymentUiCopy.goToOrders }}
              </NuxtLink>
            </div>
          </section>
        </div>
      </main>

      <aside class="checkout-sidebar">
        <div class="sidebar-inner">
          <div v-if="cart.items.value.length" class="cart-items">
            <div v-for="item in cart.items.value" :key="item.cartItemId" class="cart-item">
              <div class="item-img-wrapper">
                <img :src="item.productPic" :alt="item.title" class="item-img" />
                <span class="item-qty">{{ item.quantity }}</span>
              </div>
              <div class="item-info">
                <h4 class="item-title">{{ item.title }}</h4>
                <p class="item-variant">{{ attributeText(item.attributes) }}</p>
                <p v-if="item.addons?.length" class="item-variant">
                  + {{ item.addons.map((addon: any) => addon.name || addon.code).join(', ') }}
                </p>
              </div>
              <div class="item-price">{{ money(item.lineAmount) }}</div>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>{{ checkoutCopy.emptyCart }}</p>
          </div>

          <div class="summary-lines">
            <div class="line">
              <span>{{ checkoutCopy.subtotal }}</span>
              <span>{{ money(preview.subtotal || cart.subtotal.value) }}</span>
            </div>
            <div class="line">
              <span>{{ shippingMethod }}</span>
              <span>{{ money(preview.shippingAmount || 0) }}</span>
            </div>
            <div class="line" v-if="preview.taxAmount != null">
              <span>{{ checkoutCopy.tax }}</span>
              <span>{{ money(preview.taxAmount) }}</span>
            </div>
          </div>

          <div class="total-line">
            <span>{{ checkoutCopy.total }}</span>
            <div class="total-price-group">
              <span class="currency-code">USD</span>
              <strong>{{ money(preview.totalAmount || cart.subtotal.value) }}</strong>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <div v-if="addressListOpen" class="modal-overlay" @click.self="addressListOpen = false">
      <div class="modal-content address-list-modal">
        <div class="modal-header">
          <h2>{{ checkoutCopy.selectAddress }}</h2>
          <button class="close-btn" @click="addressListOpen = false">×</button>
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

            <button type="button" class="btn-select-address" @click="openAddAddressModal">
              + {{ checkoutCopy.addAddress }}
            </button>

            <button type="button" class="secondary-btn" @click="selectedAddressId = 'manual'; addressListOpen = false">
              {{ checkoutCopy.manualAddress }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <AddAddressModal
      :is-open="addAddressModalOpen"
      @close="addAddressModalOpen = false"
      @save="handleSaveNewAddress"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
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

const { lang, t, toggleLang, nextLangShortText } = useShopLocale()
const { money, attributeText, dateTime } = useShopFormat()
const { start: startDeadlineTicker, stop: stopDeadlineTicker, countdownText, isExpired } = usePaymentDeadline()
const cart = useShopCart()
const session = useShopSession()

const addresses = ref<any[]>([])
const selectedAddressId = ref<string>('manual')
const selectedPaymentMethod = ref<'alipay' | 'credit_card'>('alipay')
const preview = ref<Record<string, any>>({})
const paymentIntent = ref<Record<string, any> | null>(null)
const currentOrderId = ref<number | null>(null)
const currentOrderExpireTime = ref<string | null>(null)
const currentOrderStatus = ref<string | null>(null)
const currentOrderPaymentStatus = ref<string | null>(null)
const message = ref('')
const bootstrapped = ref(false)
const previewing = ref(false)
const isPaying = ref(false)
const completingPayment = ref(false)

const addressListOpen = ref(false)
const addAddressModalOpen = ref(false)
let currentOrderPollTimer: ReturnType<typeof setInterval> | null = null

const addressForm = reactive(createEmptyAddressDraft())

const checkoutCopy = computed(() =>
  lang.value === 'zh'
    ? {
        contact: '\u8054\u7cfb\u65b9\u5f0f',
        shippingAddress: '\u6536\u8d27\u5730\u5740',
        shippingMethod: '\u914d\u9001\u65b9\u5f0f',
        paymentTitle: '\u652f\u4ed8',
        secure: '\u652f\u4ed8\u53d1\u8d77\u540e\u4f1a\u5148\u521b\u5efa\u8ba2\u5355\uff0c\u82e5\u652f\u4ed8\u4e2d\u65ad\u53ef\u5728\u8ba2\u5355\u9875\u7ee7\u7eed\u652f\u4ed8\uff0c\u65e0\u9700\u91cd\u590d\u4e0b\u5355\u3002',
        cardNumber: '\u5361\u53f7',
        expiry: '\u6709\u6548\u671f',
        cvc: '\u5b89\u5168\u7801',
        nameOnCard: '\u6301\u5361\u4eba\u59d3\u540d',
        payNow: '\u7acb\u5373\u652f\u4ed8',
        processing: '\u6b63\u5728\u53d1\u8d77\u652f\u4ed8...',
        completing: '\u6b63\u5728\u5b8c\u6210\u652f\u4ed8...',
        defaultAddress: '\u9ed8\u8ba4',
        manualAddress: '\u624b\u52a8\u586b\u5199\u5730\u5740',
        addressReady: '\u5f53\u524d\u5730\u5740\u4fe1\u606f\u5b8c\u6574\uff0c\u53ef\u4ee5\u7ee7\u7eed\u9884\u89c8\u5e76\u4e0b\u5355\u3002',
        addressIncomplete: '\u8bf7\u586b\u5199\u56fd\u5bb6\u3001\u59d3\u540d\u3001\u5730\u5740 1\u3001\u57ce\u5e02\u3001\u5dde/\u7701\u548c\u90ae\u7f16\u540e\u7ee7\u7eed\u3002',
        addressSaved: '\u5730\u5740\u5df2\u4fdd\u5b58\u3002',
        addressAlreadySaved: '\u8be5\u5730\u5740\u5df2\u5b58\u5728\uff0c\u5df2\u81ea\u52a8\u4e3a\u4f60\u9009\u4e2d\u3002',
        addressRequired: '\u8bf7\u5148\u5b8c\u5584\u6536\u8d27\u5730\u5740\u3002',
        saveAddress: '\u4fdd\u5b58\u5f53\u524d\u5730\u5740',
        change: '\u66f4\u6362',
        selectAddress: '\u9009\u62e9\u5df2\u4fdd\u5b58\u5730\u5740',
        addAddress: '\u65b0\u589e\u5730\u5740',
        emptyCart: '\u8d2d\u7269\u8f66\u4e3a\u7a7a\uff0c\u8bf7\u5148\u6dfb\u52a0\u5546\u54c1\u3002',
        subtotal: '\u5c0f\u8ba1',
        tax: '\u7a0e\u8d39',
        total: '\u5408\u8ba1',
      }
    : {
        contact: 'Contact',
        shippingAddress: 'Shipping address',
        shippingMethod: 'Shipping method',
        paymentTitle: 'Payment',
        secure: 'An order is created before payment. If payment is interrupted, you can continue from your orders page.',
        cardNumber: 'Card number',
        expiry: 'Expiration date',
        cvc: 'Security code',
        nameOnCard: 'Name on card',
        payNow: 'Pay now',
        processing: 'Starting payment...',
        completing: 'Completing payment...',
        defaultAddress: 'Default',
        manualAddress: 'Use manual address',
        addressReady: 'This address is ready for preview and checkout.',
        addressIncomplete: 'Fill in country, name, address line 1, city, state, and ZIP code to continue.',
        addressSaved: 'Address saved.',
        addressAlreadySaved: 'This address is already saved.',
        addressRequired: 'Complete the shipping address before continuing.',
        saveAddress: 'Save this address',
        change: 'Change',
        selectAddress: 'Select saved address',
        addAddress: 'Add address',
        emptyCart: 'Your cart is empty.',
        subtotal: 'Subtotal',
        tax: 'Tax',
        total: 'Total',
      },
)

const paymentUiCopy = computed(() =>
  lang.value === 'zh'
    ? {
        alipayHint: '\u4f18\u5148\u8d70\u652f\u4ed8\u5b9d\u6c99\u76d2\u652f\u4ed8\uff0c\u652f\u4ed8\u5b8c\u6210\u540e\u4f1a\u56de\u8df3\u7ad9\u5185\u786e\u8ba4\u7ed3\u679c\u3002',
        mockHint: '\u5982\u679c\u6c99\u76d2\u914d\u7f6e\u6682\u4e0d\u53ef\u7528\uff0c\u4f1a\u81ea\u52a8\u56de\u9000\u5230 mock \u652f\u4ed8\u6d41\u7a0b\u3002',
        completeMockPayment: '\u5b8c\u6210 mock \u652f\u4ed8',
        redirectingToAlipay: '\u6b63\u5728\u8df3\u8f6c\u5230\u652f\u4ed8\u5b9d...',
        orderCreated: '\u5f53\u524d\u8ba2\u5355\u5df2\u521b\u5efa\uff0c\u53ef\u968f\u65f6\u53bb\u8ba2\u5355\u9875\u7ee7\u7eed\u652f\u4ed8',
        payBefore: '\u8bf7\u5728',
        remaining: '\u5269\u4f59\u652f\u4ed8\u65f6\u95f4',
        expired: '\u8be5\u8ba2\u5355\u5df2\u8d85\u65f6\uff0c\u5e93\u5b58\u5df2\u91ca\u653e\u3002\u5982\u9700\u7ee7\u7eed\u8d2d\u4e70\uff0c\u8bf7\u91cd\u65b0\u4e0b\u5355\u3002',
        orderCreatedContinueInOrders: '\u8ba2\u5355\u5df2\u521b\u5efa\uff0c\u4f46\u652f\u4ed8\u5c1a\u672a\u6210\u529f\u53d1\u8d77\u3002\u8bf7\u524d\u5f80\u8ba2\u5355\u9875\u7ee7\u7eed\u652f\u4ed8\u3002',
        goToOrders: '\u524d\u5f80\u8ba2\u5355\u9875\u7ee7\u7eed\u652f\u4ed8',
      }
    : {
        alipayHint: 'Preferred path: redirect to Alipay Sandbox and confirm the result on return.',
        mockHint: 'If sandbox credentials are unavailable, checkout falls back to the mock flow.',
        completeMockPayment: 'Complete mock payment',
        redirectingToAlipay: 'Redirecting to Alipay...',
        orderCreated: 'This order has been created and can be resumed from your orders page',
        payBefore: 'Please complete payment before',
        remaining: 'Time remaining',
        expired: 'This order has expired and inventory has been released. Create a new order to continue.',
        orderCreatedContinueInOrders: 'Your order was created, but payment did not start successfully. Continue payment from your orders page.',
        goToOrders: 'Go to orders to continue payment',
      },
)

const cartSignature = computed(() =>
  cart.items.value.map((item) => `${item.cartItemId}:${item.quantity}:${JSON.stringify(item.addons || [])}`).join(';'),
)
const selectedSavedAddress = computed(
  () => addresses.value.find((item) => String(item.id) === selectedAddressId.value) || null,
)
const canPreviewAddress = computed(
  () => Boolean(selectedSavedAddress.value) || isAddressDraftComplete(addressForm),
)
const currentOrderAwaitingPayment = computed(
  () => currentOrderStatus.value === 'PENDING_PAYMENT'
    && !['PAID', 'CANCELLED', 'EXPIRED'].includes(currentOrderPaymentStatus.value || ''),
)
const currentOrderExpired = computed(
  () => currentOrderStatus.value === 'EXPIRED'
    || currentOrderPaymentStatus.value === 'EXPIRED'
    || (Boolean(currentOrderExpireTime.value) && isExpired(currentOrderExpireTime.value)),
)
const currentOrderCountdown = computed(
  () => currentOrderExpireTime.value ? countdownText(currentOrderExpireTime.value, lang.value) : '',
)
const showCountdown = computed(
  () => Boolean(currentOrderId.value) && Boolean(currentOrderExpireTime.value) && currentOrderAwaitingPayment.value,
)
const isPayDisabled = computed(
  () => !cart.items.value.length
    || !canPreviewAddress.value
    || previewing.value
    || isPaying.value
    || currentOrderExpired.value,
)
const isMockFlow = computed(() => paymentIntent.value?.providerKey === 'mock')

const applyAddress = (address: any) => {
  Object.assign(addressForm, toAddressDraft(address))
}

const applyCurrentOrder = (order: any) => {
  if (!order) {
    return
  }

  currentOrderId.value = order.id ?? currentOrderId.value
  currentOrderExpireTime.value = order.paymentExpireTime || null
  currentOrderStatus.value = order.status || null
  currentOrderPaymentStatus.value = order.paymentStatus || null

  if (!currentOrderAwaitingPayment.value) {
    paymentIntent.value = null
  }
}

const resetCurrentOrder = () => {
  currentOrderId.value = null
  currentOrderExpireTime.value = null
  currentOrderStatus.value = null
  currentOrderPaymentStatus.value = null
  paymentIntent.value = null
}

const stopCurrentOrderPolling = () => {
  if (!currentOrderPollTimer) {
    return
  }

  clearInterval(currentOrderPollTimer)
  currentOrderPollTimer = null
}

const refreshCurrentOrderStatus = async (silent = false) => {
  if (!currentOrderId.value) {
    return null
  }

  const res = await useHttp(`/api/order/${currentOrderId.value}`, {
    showError: false,
  }).catch(() => null)

  if (res?.code !== 200) {
    return null
  }

  applyCurrentOrder(res.data)

  if (!silent && currentOrderStatus.value === 'EXPIRED') {
    message.value = paymentUiCopy.value.expired
  }

  return res.data
}

const startCurrentOrderPolling = () => {
  if (!process.client || currentOrderPollTimer || !currentOrderAwaitingPayment.value) {
    return
  }

  currentOrderPollTimer = window.setInterval(() => {
    refreshCurrentOrderStatus(true)
  }, 15000)
}

const syncVisibilityState = () => {
  if (!process.client || !currentOrderAwaitingPayment.value) {
    return
  }

  if (document.visibilityState === 'hidden') {
    return
  }

  refreshCurrentOrderStatus(true)
}

const createIntentForOrder = async (orderId: number) => {
  const intentRes = await useHttp('/api/payment/intent', {
    method: 'POST',
    body: {
      orderId,
      paymentMethod: selectedPaymentMethod.value,
    },
    showError: false,
  }).catch(() => null)

  if (intentRes?.code !== 200) {
    await refreshCurrentOrderStatus(true)
    message.value = intentRes?.message || paymentUiCopy.value.orderCreatedContinueInOrders
    if (intentRes?.message === 'Order payment window expired') {
      message.value = paymentUiCopy.value.expired
    }
    return null
  }

  paymentIntent.value = intentRes.data
  message.value = intentRes.data?.displayMessage || t('paymentReady')

  if (intentRes.data?.nextAction === 'REDIRECT' && intentRes.data?.redirectUrl && process.client) {
    message.value = paymentUiCopy.value.redirectingToAlipay
    window.location.href = intentRes.data.redirectUrl
    return intentRes.data
  }

  return intentRes.data
}

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
      message.value = lang.value === 'zh' ? '\u8ba2\u5355\u9884\u89c8\u5df2\u66f4\u65b0\u3002' : 'Order preview updated.'
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

  if (currentOrderId.value && currentOrderAwaitingPayment.value) {
    if (currentOrderExpired.value) {
      await refreshCurrentOrderStatus()
      message.value = paymentUiCopy.value.expired
      return
    }

    isPaying.value = true
    try {
      await createIntentForOrder(currentOrderId.value)
    } finally {
      isPaying.value = false
    }
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
    const orderRes = await useHttp('/api/order/create', {
      method: 'POST',
      body: {
        ...buildPreviewPayload(),
        previewToken: preview.value.previewToken,
        addressSnapshot: selectedSavedAddress.value ? undefined : toAddressDraft(addressForm),
        remark: 'Checkout creates order on pay action',
      },
      showError: false,
    }).catch(() => null)

    if (orderRes?.code !== 200) {
      message.value = orderRes?.message || (lang.value === 'zh'
        ? '\u8ba2\u5355\u521b\u5efa\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002'
        : 'Failed to create order.')
      return
    }

    applyCurrentOrder(orderRes.data)
    const intentRes = await createIntentForOrder(orderRes.data.id)

    if (!intentRes) {
      await navigateTo('/account/orders')
      return
    }
  } finally {
    isPaying.value = false
  }
}

const completePayment = async () => {
  if (!paymentIntent.value?.id) {
    return
  }

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
      await refreshCurrentOrderStatus(true)
      message.value = t('orderSuccess')
      await cart.refreshCart()
      await navigateTo('/account/orders')
    }
  } finally {
    completingPayment.value = false
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

watch([selectedAddressId, cartSignature], async () => {
  if (!bootstrapped.value) {
    return
  }

  resetCurrentOrder()
  await previewOrder()
})

watch(
  () => getAddressSignature(addressForm),
  async () => {
    if (!bootstrapped.value) {
      return
    }

    if (selectedSavedAddress.value && !addressesMatch(selectedSavedAddress.value, addressForm)) {
      selectedAddressId.value = 'manual'
    }

    if (selectedAddressId.value === 'manual') {
      resetCurrentOrder()
      await previewOrder()
    }
  },
)

watch(currentOrderAwaitingPayment, (active) => {
  if (active) {
    startDeadlineTicker()
    startCurrentOrderPolling()
    return
  }

  stopCurrentOrderPolling()
  stopDeadlineTicker()
})

watch(
  () => currentOrderExpireTime.value ? isExpired(currentOrderExpireTime.value) : false,
  async (expired) => {
    if (!expired || !currentOrderId.value || currentOrderStatus.value === 'EXPIRED') {
      return
    }

    await refreshCurrentOrderStatus()
  },
)

onMounted(async () => {
  await session.fetchMe()
  if (!session.isLoggedIn.value) {
    await navigateTo('/login')
    return
  }

  await Promise.all([cart.refreshCart(), fetchAddresses()])
  if (process.client) {
    window.addEventListener('focus', syncVisibilityState)
    document.addEventListener('visibilitychange', syncVisibilityState)
  }
  bootstrapped.value = true
  await previewOrder()
})

onBeforeUnmount(() => {
  stopCurrentOrderPolling()
  stopDeadlineTicker()
  if (process.client) {
    window.removeEventListener('focus', syncVisibilityState)
    document.removeEventListener('visibilitychange', syncVisibilityState)
  }
})
</script>

<style scoped lang="scss">
.checkout-page {
  min-height: 100vh;
  background: #fff;
}

.checkout-container {
  display: flex;
  min-height: 100vh;
  border-top: 1px solid #e4e7ec;
}

.checkout-main,
.checkout-sidebar {
  padding: 56px 48px;
}

.checkout-main {
  flex: 1.12;
  display: flex;
  justify-content: flex-end;
}

.checkout-sidebar {
  flex: 0.88;
  background: #f9fafb;
  border-left: 1px solid #e4e7ec;
}

.checkout-main-inner,
.sidebar-inner {
  width: 100%;
  max-width: 640px;
}

.sidebar-inner {
  max-width: 480px;
  position: sticky;
  top: 56px;
}

.checkout-section + .checkout-section {
  margin-top: 32px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

  h2 {
    margin: 6px 0 0;
    font-size: 28px;
    line-height: 1.1;
    color: #111827;
  }
}

.section-head.compact h2 {
  font-size: 20px;
}

.step-label {
  margin: 0;
  color: #667085;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.ghost-btn,
.secondary-btn,
.pay-now-btn,
.btn-select-address {
  cursor: pointer;
}

.ghost-btn,
.secondary-btn {
  min-height: 42px;
  padding: 0 16px;
  border: 1px solid #d0d5dd;
  border-radius: 999px;
  background: #fff;
  color: #111827;
  font-size: 14px;
  font-weight: 700;
}

.secondary-btn {
  width: fit-content;
}

.selected-address-card,
.info-box,
.resume-box,
.empty-state {
  padding: 16px 18px;
  border: 1px solid #e4e7ec;
  border-radius: 18px;
  background: #fff;
}

.selected-address-card {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.address-details p,
.payment-hint,
.payment-note,
.status-message,
.item-variant {
  color: #475467;
  line-height: 1.6;
}

.address-form,
.card-form,
.saved-addresses-modal {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.three-cols {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    font-size: 13px;
    font-weight: 700;
    color: #344054;
  }

  input {
    min-height: 48px;
    padding: 0 14px;
    border: 1px solid #d0d5dd;
    border-radius: 16px;
    background: #fff;
    font-size: 14px;
    color: #111827;
    outline: none;
  }

  input:focus {
    border-color: #111827;
    box-shadow: 0 0 0 4px rgba(17, 24, 39, 0.08);
  }
}

.address-help.ready {
  color: #027a48;
}

.payment-methods {
  border: 1px solid #d0d5dd;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.payment-option + .payment-option {
  border-top: 1px solid #eaecf0;
}

.option-header {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 16px 18px;
  border: none;
  background: #fafafa;
  text-align: left;
}

.payment-option.selected .option-header {
  background: #fff;
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
  color: #111827;
}

.option-body {
  padding: 18px;
  background: #fafafa;
}

.brand-text.alipay {
  color: #1677ff;
  font-weight: 800;
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

.payment-note-container,
.action-stack {
  margin-top: 18px;
}

.payment-note.strong {
  color: #111827;
  font-weight: 700;
}

.payment-note.countdown {
  color: #b54708;
  font-weight: 700;
}

.payment-note.countdown.expired,
.expired-message {
  color: #b42318;
  font-weight: 700;
}

.action-stack {
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
}

.pay-now-btn.ghost {
  background: #eef2f6;
  color: #111827;
}

.pay-now-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.resume-box {
  margin-top: 16px;
}

.resume-link {
  color: #111827;
  font-weight: 700;
  text-decoration: none;
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

.item-price,
.total-line {
  font-weight: 800;
  color: #111827;
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

.modal-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  z-index: 1000;
}

.modal-content {
  width: min(520px, calc(100vw - 32px));
  max-height: 90vh;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
}

.modal-header,
.modal-body,
.saved-address {
  display: flex;
}

.modal-header {
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #eaeaea;
}

.close-btn {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
}

.saved-address {
  gap: 12px;
  padding: 16px 18px;
  border: 1px solid #e4e7ec;
  border-radius: 18px;
  background: #fbfbfb;
}

.saved-address.is-selected {
  border-color: #111827;
  background: #f7fdf2;
}

.saved-address__header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.saved-address__header em {
  padding: 4px 10px;
  border-radius: 999px;
  background: #111827;
  color: #fff;
  font-size: 11px;
  font-style: normal;
  font-weight: 700;
}

@media (max-width: 1100px) {
  .checkout-container {
    flex-direction: column;
  }

  .sidebar-inner {
    position: static;
  }
}

@media (max-width: 720px) {
  .checkout-main,
  .checkout-sidebar {
    padding: 24px 16px;
  }

  .section-head,
  .selected-address-card,
  .cart-item,
  .line,
  .total-line {
    flex-direction: column;
    align-items: flex-start;
  }

  .form-row,
  .three-cols {
    grid-template-columns: 1fr;
  }
}
</style>
