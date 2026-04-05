<template>
  <div class="profile-page">
    <section class="panel identity-panel">
      <div class="identity-grid">
        <div class="field-row">
          <span class="field-label">{{ profileCopy.name }}</span>
          <div class="field-value">
            <strong>{{ fullName }}</strong>
            <button type="button" class="edit-btn" @click="session.fetchMe()">✎</button>
          </div>
        </div>

        <div class="field-row">
          <span class="field-label">{{ t('email') }}</span>
          <div class="field-value">
            <strong>{{ session.user.value?.email || '--' }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="panel">
      <div class="panel-head">
        <h2>{{ t('addresses') }}</h2>
        <button type="button" class="inline-action" @click="addressFormOpen = !addressFormOpen">
          + {{ profileCopy.add }}
        </button>
      </div>

      <div v-if="addresses.length" class="address-list">
        <article v-for="address in addresses" :key="address.id" class="address-card">
          <strong>{{ address.firstName }} {{ address.lastName }}</strong>
          <p>{{ address.addressLine1 }} {{ address.addressLine2 }}</p>
          <p>{{ address.city }}, {{ address.state }} {{ address.zipCode }}</p>
          <small>{{ address.country }} · {{ address.phone }}</small>
        </article>
      </div>

      <div v-else class="empty-state">
        <span class="empty-icon">i</span>
        <p>{{ t('noAddress') }}</p>
      </div>

      <form v-show="addressFormOpen || !addresses.length" class="address-form" @submit.prevent="saveAddress">
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

        <div class="form-actions">
          <button type="submit" class="primary-btn">{{ t('saveAddress') }}</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <div class="panel-head">
        <h2>{{ t('myCoupons') }}</h2>
      </div>

      <div v-if="myCoupons.length" class="coupon-list">
        <article v-for="coupon in myCoupons" :key="coupon.couponUserId" class="coupon-card">
          <strong>{{ coupon.code }}</strong>
          <p>{{ coupon.title }}</p>
          <small>{{ money(coupon.discountAmount) }}</small>
        </article>
      </div>
      <p v-else class="helper-text">{{ t('noCoupons') }}</p>

      <div v-if="claimableCoupons.length" class="claim-list">
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
import { computed, onMounted, reactive, ref } from 'vue'

definePageMeta({
  layout: 'account',
})

const { lang, t } = useShopLocale()
const { money } = useShopFormat()
const session = useShopSession()

const addresses = ref<any[]>([])
const myCoupons = ref<any[]>([])
const claimableCoupons = ref<any[]>([])
const addressFormOpen = ref(false)

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

const profileCopy = computed(() =>
  lang.value === 'zh'
    ? {
        name: '姓名',
        add: '新增',
      }
    : {
        name: 'Name',
        add: 'Add',
      },
)

const fullName = computed(() => {
  const first = session.user.value?.firstName || ''
  const last = session.user.value?.lastName || ''
  return `${first} ${last}`.trim() || '--'
})

const resetForm = () => {
  form.country = 'United States'
  form.firstName = ''
  form.lastName = ''
  form.phone = ''
  form.addressLine1 = ''
  form.addressLine2 = ''
  form.city = ''
  form.state = ''
  form.zipCode = ''
}

const fetchAddresses = async () => {
  const res = await useHttp('/api/address/list')
  addresses.value = res?.code === 200 ? res.data || [] : []
}

const saveAddress = async () => {
  const res = await useHttp('/api/address', {
    method: 'POST',
    body: {
      ...form,
      isDefault: !addresses.value.length,
    },
  })
  if (res?.code === 200) {
    await fetchAddresses()
    addressFormOpen.value = false
    resetForm()
  }
}

const refreshCoupons = async () => {
  const [myRes, availableRes] = await Promise.all([useHttp('/api/coupon/my'), useHttp('/api/coupon/available')])
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
  gap: 24px;
}

.panel {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 24px;
  padding: 30px 32px;
}

.identity-grid {
  display: grid;
  gap: 28px;
}

.field-row {
  display: grid;
  gap: 8px;
}

.field-label {
  color: #76808f;
  font-size: 15px;
}

.field-value {
  display: flex;
  align-items: center;
  gap: 12px;

  strong {
    color: #111;
    font-size: 17px;
    font-weight: 600;
  }
}

.edit-btn,
.inline-action,
.primary-btn,
.claim-btn {
  border: none;
  background: transparent;
  cursor: pointer;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 22px;

  h2 {
    margin: 0;
    font-size: 18px;
    color: #111;
  }
}

.inline-action {
  color: #111;
  font-size: 16px;
  font-weight: 600;
}

.empty-state {
  min-height: 110px;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  border: 1px solid #eaeaea;
  border-radius: 18px;
  background: #fafafa;
  color: #636363;

  p {
    margin: 0;
  }
}

.empty-icon {
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #9a9a9a;
  border-radius: 999px;
  color: #7b7b7b;
  font-size: 14px;
  font-weight: 700;
}

.address-list,
.coupon-list {
  display: grid;
  gap: 14px;
}

.address-card,
.coupon-card {
  border-radius: 18px;
  background: #fafafa;
  border: 1px solid #eeeeee;
  padding: 18px;

  p,
  small {
    margin: 6px 0 0;
    color: #707070;
  }
}

.address-form {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;

  label {
    display: flex;
    flex-direction: column;
    gap: 8px;
    color: #111;
    font-size: 14px;
    font-weight: 600;
  }

  input {
    min-height: 48px;
    padding: 0 14px;
    border: 1px solid #dcdcdc;
    border-radius: 14px;
    font-size: 14px;
    outline: none;
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

.form-actions {
  display: flex;
  justify-content: flex-start;
}

.primary-btn {
  min-height: 46px;
  padding: 0 20px;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-weight: 700;
}

.helper-text {
  margin: 0;
  color: #777;
}

.claim-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 18px;
}

.claim-btn {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-radius: 18px;
  background: #f7f7f5;
  border: 1px solid #ececec;
  padding: 14px 16px;
  color: #111;
  font-weight: 600;
}

@media (max-width: 840px) {
  .two-col,
  .three-col {
    grid-template-columns: 1fr;
  }

  .panel {
    padding: 22px;
  }
}
</style>
