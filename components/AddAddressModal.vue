<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Add address</h2>
        <button class="close-btn" @click="handleClose">
          <XIcon class="icon" />
        </button>
      </div>
      
      <div class="modal-body">
        <form class="address-form" @submit.prevent="handleSave">
          <div class="form-group">
            <label>Country/region</label>
            <div class="select-wrapper">
              <select v-model="form.country">
                <option value="US">United States</option>
                <option value="CA">Canada</option>
                <option value="UK">United Kingdom</option>
              </select>
              <ChevronDownIcon class="select-icon" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="sr-only">First name</label>
              <input type="text" v-model="form.firstName" placeholder="First name" />
            </div>
            <div class="form-group">
              <label class="sr-only">Last name</label>
              <input type="text" v-model="form.lastName" placeholder="Last name" />
            </div>
          </div>

          <div class="form-group">
            <label class="sr-only">Address</label>
            <div class="input-with-icon">
              <input type="text" v-model="form.address" placeholder="Address" />
              <SearchIcon class="search-icon" />
            </div>
          </div>

          <div class="form-group">
            <label class="sr-only">Apartment, suite, etc. (optional)</label>
            <input type="text" v-model="form.apartment" placeholder="Apartment, suite, etc. (optional)" />
          </div>

          <div class="form-row three-cols">
            <div class="form-group">
              <label class="sr-only">City</label>
              <input type="text" v-model="form.city" placeholder="City" />
            </div>
            <div class="form-group">
              <label class="sr-only">State</label>
              <div class="select-wrapper">
                <select v-model="form.state">
                  <option value="" disabled selected>State</option>
                  <option value="AL">Alabama</option>
                  <option value="CA">California</option>
                  <option value="NY">New York</option>
                </select>
                <ChevronDownIcon class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label class="sr-only">ZIP code</label>
              <input type="text" v-model="form.zipCode" placeholder="ZIP code" />
            </div>
          </div>

          <div class="form-group">
            <label class="sr-only">Phone</label>
            <div class="phone-input">
              <div class="phone-prefix">
                <span>+1</span>
                <span class="flag">🇺🇸</span>
                <ChevronDownIcon class="prefix-icon" />
              </div>
              <input type="tel" v-model="form.phone" placeholder="Phone" />
            </div>
          </div>

          <div class="checkbox-group">
            <input type="checkbox" id="default-address" v-model="form.isDefault" />
            <label for="default-address">This is my default address</label>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="handleClose">Cancel</button>
            <button type="submit" class="btn-save">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { XIcon, ChevronDownIcon, SearchIcon } from 'lucide-vue-next'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

const form = ref({
  country: 'US',
  firstName: '',
  lastName: '',
  address: '',
  apartment: '',
  city: '',
  state: 'AL',
  zipCode: '',
  phone: '',
  isDefault: false
})

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})

const handleClose = () => {
  emit('close')
}

const handleSave = () => {
  emit('save', form.value)
  handleClose()
}
</script>

<style lang="scss" scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  width: 100%;
  max-width: 600px;
  border-radius: 12px;
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
    font-size: 20px;
    font-weight: 700;
    margin: 0;
    color: #111;
  }

  .close-btn {
    background: none;
    border: none;
    cursor: pointer;
    color: #666;
    padding: 4px;
    
    &:hover {
      color: #111;
    }

    .icon {
      width: 20px;
      height: 20px;
    }
  }
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
}

.address-form {
  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    border: 0;
  }

  .form-group {
    margin-bottom: 16px;

    label:not(.sr-only) {
      display: block;
      font-size: 12px;
      color: #666;
      margin-bottom: 4px;
      padding-left: 12px;
    }

    input[type="text"],
    input[type="tel"],
    select {
      width: 100%;
      padding: 14px 16px;
      border: 1px solid #d1d5db;
      border-radius: 8px;
      font-size: 14px;
      color: #111;
      background: #fff;
      transition: border-color 0.2s;
      outline: none;

      &:focus {
        border-color: #111;
      }
      
      &::placeholder {
        color: #9ca3af;
      }
    }

    .select-wrapper {
      position: relative;
      
      select {
        appearance: none;
        padding-right: 40px;
      }

      .select-icon {
        position: absolute;
        right: 16px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        color: #666;
        pointer-events: none;
      }
    }

    .input-with-icon {
      position: relative;
      
      .search-icon {
        position: absolute;
        right: 16px;
        top: 50%;
        transform: translateY(-50%);
        width: 18px;
        height: 18px;
        color: #9ca3af;
      }
    }

    .phone-input {
      display: flex;
      border: 1px solid #d1d5db;
      border-radius: 8px;
      overflow: hidden;
      
      &:focus-within {
        border-color: #111;
      }

      .phone-prefix {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 0 16px;
        background: #f9fafb;
        border-right: 1px solid #d1d5db;
        font-size: 14px;
        color: #111;
        cursor: pointer;

        .flag {
          font-size: 16px;
        }

        .prefix-icon {
          width: 14px;
          height: 14px;
          color: #666;
        }
      }

      input {
        border: none;
        border-radius: 0;
        
        &:focus {
          border-color: transparent;
        }
      }
    }
  }

  .form-row {
    display: flex;
    gap: 16px;
    
    .form-group {
      flex: 1;
    }

    &.three-cols {
      .form-group {
        flex: 1;
      }
    }
  }

  .checkbox-group {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 24px 0;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
      border-radius: 4px;
      border: 1px solid #d1d5db;
      cursor: pointer;
      accent-color: #111;
    }

    label {
      font-size: 14px;
      color: #111;
      cursor: pointer;
    }
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    margin-top: 32px;

    button {
      padding: 12px 24px;
      border-radius: 6px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s;
    }

    .btn-cancel {
      background: none;
      border: none;
      color: #666;
      text-decoration: underline;

      &:hover {
        color: #111;
      }
    }

    .btn-save {
      background: #58cc02;
      border: none;
      color: #fff;
      
      &:hover {
        background: #46a302;
      }
    }
  }
}

@media (max-width: 640px) {
  .modal-content {
    height: 100vh;
    max-height: 100vh;
    border-radius: 0;
  }
  
  .form-row, .form-row.three-cols {
    flex-direction: column;
    gap: 0;
  }
}
</style>
