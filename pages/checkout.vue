<template>
  <div class="checkout-page">
    <div class="checkout-shell">
      <div class="top-row">
        <NuxtLink to="/" class="brand">isinwheel</NuxtLink>
        <button type="button" class="lang-btn" @click="toggleLang">
          {{ lang === 'en' ? '中文' : 'EN' }}
        </button>
      </div>

      <div class="grid">
        <section class="panel">
          <div class="panel-head">
            <div>
              <p class="eyebrow">{{ t('checkout') }}</p>
              <h1>{{ session.user.value?.email || t('checkout') }}</h1>
            </div>
            <NuxtLink to="/account/profile" class="secondary-link">{{ t('profile') }}</NuxtLink>
          </div>

          <div class="block">
            <div class="block-head">
              <h2>{{ t('addressTitle') }}</h2>
              <button type="button" class="minor-btn" @click="saveAddressFromForm">
                {{ t('saveAddress') }}
              </button>
            </div>

            <p class="helper-text" v-if="!addresses.length">{{ t('noAddress') }}</p>

            <div v-if="addresses.length" class="saved-addresses">
              <label v-for="item in addresses" :key="item.id" class="saved-address">
                <input v-model="selectedAddressId" :value="item.id" type="radio" />
                <span>
                  <strong>{{ item.firstName }} {{ item.lastName }}</strong>
                  <small>
                    {{ item.addressLine1 }}, {{ item.city }}, {{ item.state }} {{ item.zipCode }}
                  </small>
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
              <div class="two-col">
                <label>
                  <span>{{ t('firstName') }}</span>
                  <input v-model="addressForm.firstName" type="text" required />
                </label>
                <label>
                  <span>{{ t('lastName') }}</span>
                  <input v-model="addressForm.lastName" type="text" required />
                </label>
              </div>

              <label>
                <span>{{ t('country') }}</span>
                <input v-model="addressForm.country" type="text" />
              </label>

              <label>
                <span>{{ t('addressLine1') }}</span>
                <input v-model="addressForm.addressLine1" type="text" required />
              </label>

              <label>
                <span>{{ t('addressLine2') }}</span>
                <input v-model="addressForm.addressLine2" type="text" />
              </label>

              <div class="three-col">
                <label>
                  <span>{{ t('city') }}</span>
                  <input v-model="addressForm.city" type="text" required />
                </label>
                <label>
                  <span>{{ t('state') }}</span>
                  <input v-model="addressForm.state" type="text" required />
                </label>
                <label>
                  <span>{{ t('zipCode') }}</span>
                  <input v-model="addressForm.zipCode" type="text" required />
                </label>
              </div>

              <label>
                <span>{{ t('phone') }}</span>
                <input v-model="addressForm.phone" type="tel" />
              </label>
            </form>
          </div>

          <div class="block">
            <div class="block-head">
              <h2>{{ t('myCoupons') }}</h2>
              <button type="button" class="minor-btn" @click="refreshCoupons">{{ t('refreshData') }}</button>
            </div>

            <div v-if="availableCoupons.length" class="coupon-list">
              <label v-for="coupon in availableCoupons" :key="coupon.couponId" class="coupon-card">
                <input v-model="selectedCouponUserId" :value="coupon.couponUserId" type="radio" />
                <span class="coupon-copy">
                  <strong>{{ coupon.code }}</strong>
                  <small>{{ coupon.title }}</small>
                </span>
                <span>{{ money(coupon.discountAmount) }}</span>
              </label>
            </div>

            <p v-else class="helper-text">{{ t('noCoupons') }}</p>

            <div class="claimable-list" v-if="claimableCoupons.length">
              <h3>{{ t('availableCoupons') }}</h3>
              <button
                v-for="coupon in claimableCoupons"
                :key="`claim-${coupon.couponId}`"
                type="button"
                class="claim-btn"
                @click="claimCoupon(coupon.couponId)"
              >
                <span>{{ coupon.code }} · {{ coupon.title }}</span>
                <strong>{{ t('claim') }}</strong>
              </button>
            </div>
          </div>

          <div class="block">
            <div class="action-stack">
              <button type="button" class="primary-btn" @click="previewOrder">
                {{ t('previewOrder') }}
              </button>
              <button type="button" class="primary-btn dark" :disabled="!preview.previewToken" @click="createOrder">
                {{ t('createOrder') }}
              </button>
              <button
                type="button"
                class="primary-btn ghost"
                :disabled="!paymentIntent?.id"
                @click="completePayment"
              >
                {{ t('completeMockPayment') }}
              </button>
            </div>
            <p v-if="message" class="message-text">{{ message }}</p>
          </div>
        </section>

        <aside class="panel summary-panel">
          <div class="panel-head">
            <div>
              <p class="eyebrow">{{ t('orderPreview') }}</p>
              <h2>{{ t('cart') }}</h2>
            </div>
          </div>

          <div class="item-list">
            <article v-for="item in cart.items.value" :key="item.cartItemId" class="summary-item">
              <img :src="item.productPic" :alt="item.title" />
              <div>
                <strong>{{ item.title }}</strong>
                <p>{{ attributeText(item.attributes) }}</p>
                <small v-if="item.addons?.length">
                  + {{ item.addons.map((addon: any) => addon.name).join(', ') }}
                </small>
              </div>
              <span>{{ money(item.lineAmount) }}</span>
            </article>
          </div>

          <div class="totals">
            <div class="line">
              <span>{{ t('subtotal') }}</span>
              <strong>{{ money(preview.subtotal || cart.subtotal.value) }}</strong>
            </div>
            <div class="line">
              <span>{{ t('shipping') }}</span>
              <strong>{{ money(preview.shippingAmount || 0) }}</strong>
            </div>
            <div class="line">
              <span>{{ t('tax') }}</span>
              <strong>{{ money(preview.taxAmount || 0) }}</strong>
            </div>
            <div class="line" v-if="preview.discountAmount">
              <span>{{ t('coupon') }}</span>
              <strong>-{{ money(preview.discountAmount || 0) }}</strong>
            </div>
            <div class="line total">
              <span>{{ t('total') }}</span>
              <strong>{{ money(preview.totalAmount || cart.subtotal.value) }}</strong>
            </div>
          </div>

          <div class="payment-box" v-if="paymentIntent">
            <p class="eyebrow">{{ t('paymentStep') }}</p>
            <strong>{{ paymentIntent.methodCode }}</strong>
            <p>{{ t('paymentReady') }}</p>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'

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
const createdOrder = ref<Record<string, any> | null>(null)
const paymentIntent = ref<Record<string, any> | null>(null)
const message = ref('')

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
  if (!address) {
    return
  }
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

const fetchAddresses = async () => {
  const res = await useHttp('/api/address/list')
  addresses.value = res?.code === 200 ? res.data || [] : []
  if (addresses.value.length && !selectedAddressId.value) {
    selectedAddressId.value = addresses.value[0].id
    applyAddress(addresses.value[0])
  }
}

const refreshCoupons = async () => {
  const [myRes, availableRes] = await Promise.all([
    useHttp('/api/coupon/my'),
    useHttp('/api/coupon/available'),
  ])
  availableCoupons.value = myRes?.code === 200 ? myRes.data || [] : []
  claimableCoupons.value =
    availableRes?.code === 200
      ? (availableRes.data || []).filter((coupon: any) => !coupon.claimed)
      : []
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
      remark: 'Nuxt checkout flow',
    },
  })

  if (res?.code === 200) {
    createdOrder.value = res.data
    message.value = t('orderCreated')
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
  if (!paymentIntent.value?.id) {
    return
  }

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
.checkout-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f7f1, #eef2ff);
  padding: 24px;
}

.checkout-shell {
  max-width: 1320px;
  margin: 0 auto;
}

.top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.brand {
  text-decoration: none;
  color: #0f172a;
  font-family: 'Poppins', sans-serif;
  font-size: 30px;
  font-weight: 800;
}

.lang-btn,
.minor-btn,
.secondary-link {
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 999px;
  padding: 10px 14px;
  background: white;
  color: #0f172a;
  text-decoration: none;
  font-weight: 700;
}

.grid {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(340px, 0.85fr);
  gap: 22px;
}

.panel {
  background: white;
  border-radius: 28px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 28px;
}

.panel-head,
.block-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.panel-head {
  margin-bottom: 20px;
}

.eyebrow {
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #0f766e;
}

h1,
h2,
h3 {
  margin: 0;
  color: #0f172a;
}

.block {
  padding: 22px 0;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
}

.helper-text,
.message-text {
  color: #64748b;
  line-height: 1.7;
}

.saved-addresses,
.coupon-list,
.claimable-list,
.item-list,
.action-stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.saved-address,
.coupon-card,
.claim-btn,
.summary-item {
  display: grid;
  gap: 12px;
  align-items: start;
  padding: 14px 16px;
  border-radius: 20px;
  background: #f8fafc;
}

.saved-address,
.coupon-card {
  grid-template-columns: auto minmax(0, 1fr) auto;
}

.claim-btn {
  grid-template-columns: minmax(0, 1fr) auto;
  border: none;
  text-align: left;
  background: #ecfeff;
}

.saved-address span,
.coupon-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.saved-address small,
.coupon-copy small,
.summary-item p,
.summary-item small {
  color: #64748b;
  line-height: 1.5;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 16px;

  label {
    display: flex;
    flex-direction: column;
    gap: 8px;
    font-weight: 700;
    color: #0f172a;
  }

  input {
    min-height: 48px;
    border-radius: 14px;
    border: 1px solid rgba(15, 23, 42, 0.12);
    padding: 0 14px;
  }
}

.two-col,
.three-col {
  display: grid;
  gap: 14px;
}

.two-col {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.three-col {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.action-stack .primary-btn {
  min-height: 52px;
  border-radius: 999px;
  border: none;
  background: #0f766e;
  color: white;
  font-weight: 800;

  &.dark {
    background: #0f172a;
  }

  &.ghost {
    background: #e0f2fe;
    color: #0f172a;
  }

  &:disabled {
    opacity: 0.45;
  }
}

.summary-panel .summary-item {
  grid-template-columns: 76px minmax(0, 1fr) auto;

  img {
    width: 76px;
    height: 76px;
    object-fit: cover;
    border-radius: 16px;
  }
}

.totals {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
}

.line {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;

  &.total {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid rgba(15, 23, 42, 0.08);
    font-size: 18px;
  }
}

.payment-box {
  margin-top: 18px;
  padding: 18px;
  border-radius: 22px;
  background: #0f172a;
  color: white;
}

@media (max-width: 1024px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .two-col,
  .three-col {
    grid-template-columns: 1fr;
  }
}
</style>
