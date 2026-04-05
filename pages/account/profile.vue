<template>
  <div class="profile-page">
    <section class="card">
      <div class="section-head">
        <h2>{{ t('profile') }}</h2>
        <button type="button" class="minor-btn" @click="session.fetchMe()">{{ t('refreshData') }}</button>
      </div>
      <div class="info-grid" v-if="session.user.value">
        <div>
          <span>{{ t('firstName') }}</span>
          <strong>{{ session.user.value.firstName }}</strong>
        </div>
        <div>
          <span>{{ t('lastName') }}</span>
          <strong>{{ session.user.value.lastName }}</strong>
        </div>
        <div>
          <span>{{ t('email') }}</span>
          <strong>{{ session.user.value.email }}</strong>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>{{ t('addresses') }}</h2>
        <button type="button" class="minor-btn" @click="saveAddress">{{ t('saveAddress') }}</button>
      </div>

      <div class="address-list" v-if="addresses.length">
        <article v-for="address in addresses" :key="address.id" class="address-card">
          <strong>{{ address.firstName }} {{ address.lastName }}</strong>
          <p>{{ address.addressLine1 }} {{ address.addressLine2 }}</p>
          <p>{{ address.city }}, {{ address.state }} {{ address.zipCode }}</p>
          <small>{{ address.country }} · {{ address.phone }}</small>
        </article>
      </div>
      <p v-else class="helper-text">{{ t('noAddress') }}</p>

      <form class="address-form" @submit.prevent="saveAddress">
        <div class="two-col">
          <label>
            <span>{{ t('firstName') }}</span>
            <input v-model="form.firstName" type="text" required />
          </label>
          <label>
            <span>{{ t('lastName') }}</span>
            <input v-model="form.lastName" type="text" required />
          </label>
        </div>
        <label>
          <span>{{ t('addressLine1') }}</span>
          <input v-model="form.addressLine1" type="text" required />
        </label>
        <label>
          <span>{{ t('addressLine2') }}</span>
          <input v-model="form.addressLine2" type="text" />
        </label>
        <div class="three-col">
          <label>
            <span>{{ t('city') }}</span>
            <input v-model="form.city" type="text" required />
          </label>
          <label>
            <span>{{ t('state') }}</span>
            <input v-model="form.state" type="text" required />
          </label>
          <label>
            <span>{{ t('zipCode') }}</span>
            <input v-model="form.zipCode" type="text" required />
          </label>
        </div>
        <div class="two-col">
          <label>
            <span>{{ t('country') }}</span>
            <input v-model="form.country" type="text" />
          </label>
          <label>
            <span>{{ t('phone') }}</span>
            <input v-model="form.phone" type="tel" />
          </label>
        </div>
      </form>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>{{ t('myCoupons') }}</h2>
        <button type="button" class="minor-btn" @click="refreshCoupons">{{ t('refreshData') }}</button>
      </div>

      <div class="coupon-list" v-if="myCoupons.length">
        <article v-for="coupon in myCoupons" :key="coupon.couponUserId" class="coupon-card">
          <strong>{{ coupon.code }}</strong>
          <p>{{ coupon.title }}</p>
          <small>{{ money(coupon.discountAmount) }}</small>
        </article>
      </div>
      <p v-else class="helper-text">{{ t('noCoupons') }}</p>

      <div class="claim-list" v-if="claimableCoupons.length">
        <h3>{{ t('availableCoupons') }}</h3>
        <button
          v-for="coupon in claimableCoupons"
          :key="coupon.couponId"
          type="button"
          class="claim-btn"
          @click="claimCoupon(coupon.couponId)"
        >
          <span>{{ coupon.code }} · {{ coupon.title }}</span>
          <strong>{{ t('claim') }}</strong>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

definePageMeta({
  layout: 'account',
})

const { t } = useShopLocale()
const { money } = useShopFormat()
const session = useShopSession()

const addresses = ref<any[]>([])
const myCoupons = ref<any[]>([])
const claimableCoupons = ref<any[]>([])

const form = reactive({
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

const fetchAddresses = async () => {
  const res = await useHttp('/api/address/list')
  addresses.value = res?.code === 200 ? res.data || [] : []
}

const saveAddress = async () => {
  await useHttp('/api/address', {
    method: 'POST',
    body: {
      ...form,
      isDefault: !addresses.value.length,
    },
  })
  await fetchAddresses()
}

const refreshCoupons = async () => {
  const [myRes, availableRes] = await Promise.all([
    useHttp('/api/coupon/my'),
    useHttp('/api/coupon/available'),
  ])
  myCoupons.value = myRes?.code === 200 ? myRes.data || [] : []
  claimableCoupons.value =
    availableRes?.code === 200
      ? (availableRes.data || []).filter((coupon: any) => !coupon.claimed)
      : []
}

const claimCoupon = async (couponId: number) => {
  await useHttp(`/api/coupon/${couponId}/claim`, {
    method: 'POST',
  })
  await refreshCoupons()
}

onMounted(async () => {
  await session.fetchMe()
  await Promise.all([fetchAddresses(), refreshCoupons()])
})
</script>

<style scoped lang="scss">
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.card {
  border-radius: 28px;
  background: white;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 24px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.minor-btn {
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 999px;
  background: #f8fafc;
  padding: 10px 14px;
  font-weight: 700;
}

.info-grid,
.two-col,
.three-col,
.coupon-list,
.address-list {
  display: grid;
  gap: 14px;
}

.info-grid,
.coupon-list,
.address-list {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.two-col {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.three-col {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.info-grid div,
.coupon-card,
.address-card {
  border-radius: 20px;
  background: #f8fafc;
  padding: 16px;
}

.info-grid span,
.coupon-card small,
.address-card small,
.helper-text {
  color: #64748b;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 18px;

  label {
    display: flex;
    flex-direction: column;
    gap: 8px;
    font-weight: 700;
  }

  input {
    min-height: 46px;
    border: 1px solid rgba(15, 23, 42, 0.12);
    border-radius: 14px;
    padding: 0 14px;
  }
}

.claim-list {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.claim-btn {
  border: none;
  border-radius: 18px;
  background: #ecfeff;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-weight: 700;
}

@media (max-width: 980px) {
  .info-grid,
  .coupon-list,
  .address-list {
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
