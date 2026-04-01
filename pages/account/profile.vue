<template>
  <div class="profile-page">
    <h1 class="page-title">Profile</h1>
    
    <div class="profile-card">
      <div class="info-row">
        <div class="info-label">Name <Edit2Icon class="icon" /></div>
        <div class="info-value"></div>
      </div>
      <div class="info-row">
        <div class="info-label">Email</div>
        <div class="info-value">1873363854@qq.com</div>
      </div>
    </div>

    <div class="addresses-section">
      <div class="section-header">
        <h2 class="section-title">Addresses</h2>
        <button class="add-btn" @click="isAddModalOpen = true">+ Add</button>
      </div>
      
      <div class="addresses-content">
        <div class="address-item" v-for="address in addresses" :key="address.id">
          <div class="line">
            {{ address.firstName }} {{ address.lastName }}
            <span class="default-tag" v-if="address.isDefault">Default</span>
          </div>
          <div class="line">{{ address.addressLine1 }} {{ address.addressLine2 }}</div>
          <div class="line">{{ address.city }}, {{ address.state }} {{ address.zipCode }}</div>
          <div class="line">{{ address.country }} · {{ address.phone }}</div>
        </div>
        <div class="empty-addresses" v-if="!addresses.length">
          <InfoIcon class="icon" />
          <span>No addresses added</span>
        </div>
      </div>
    </div>

    <AddAddressModal 
      :isOpen="isAddModalOpen" 
      @close="isAddModalOpen = false" 
      @save="handleSaveAddress"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Edit2Icon, InfoIcon } from 'lucide-vue-next'
import AddAddressModal from '~/components/AddAddressModal.vue'

definePageMeta({
  layout: 'account'
})

const isAddModalOpen = ref(false)
const addresses = ref([])

const fetchAddresses = async () => {
  try {
    const res = await useHttp('/api/address/list')
    if (res?.code === 200) {
      addresses.value = res.data || []
    }
  } catch (error) {
    console.error('Failed to fetch addresses', error)
  }
}

const handleSaveAddress = async (form) => {
  await useHttp('/api/address', {
    method: 'POST',
    body: {
      country: form.country === 'US' ? 'United States' : form.country,
      firstName: form.firstName,
      lastName: form.lastName,
      phone: form.phone,
      addressLine1: form.address,
      addressLine2: form.apartment,
      city: form.city,
      state: form.state,
      zipCode: form.zipCode,
      isDefault: form.isDefault
    }
  })
  isAddModalOpen.value = false
  fetchAddresses()
}

onMounted(fetchAddresses)
</script>

<style lang="scss" scoped>
.profile-page {
  .page-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 24px;
    color: #111;
  }

  .profile-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    border: 1px solid #eaeaea;
    margin-bottom: 24px;

    .info-row {
      margin-bottom: 16px;
      
      &:last-child {
        margin-bottom: 0;
      }

      .info-label {
        font-size: 13px;
        color: #666;
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .icon {
          width: 12px;
          height: 12px;
          cursor: pointer;
          color: #111;
        }
      }

      .info-value {
        font-size: 14px;
        color: #111;
        font-weight: 500;
        min-height: 20px;
      }
    }
  }

  .addresses-section {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    border: 1px solid #eaeaea;

    .section-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 16px;

      .section-title {
        font-size: 16px;
        font-weight: 700;
        color: #111;
        margin: 0;
      }

      .add-btn {
        background: none;
        border: none;
        color: #111;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        padding: 0;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .addresses-content {
      background: #f9f9f9;
      border-radius: 6px;
      padding: 16px;

      .address-item {
        padding: 12px;
        border-radius: 6px;
        border: 1px solid #e8e8e8;
        background: #fff;
        margin-bottom: 10px;
        font-size: 13px;
        color: #333;
        line-height: 1.6;

        .line {
          display: flex;
          align-items: center;
          gap: 8px;
        }

        .default-tag {
          font-size: 11px;
          padding: 2px 8px;
          background: #ecfdf5;
          color: #059669;
          border-radius: 999px;
          font-weight: 600;
        }
      }

      .empty-addresses {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #666;
        font-size: 14px;

        .icon {
          width: 16px;
          height: 16px;
        }
      }
    }
  }
}
</style>
