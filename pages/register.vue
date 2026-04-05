<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="copy-block">
        <p class="eyebrow">isinwheel</p>
        <h1>{{ t('registerTitle') }}</h1>
        <p>{{ t('registerHint') }}</p>
      </div>

      <form class="form-block" @submit.prevent="handleRegister">
        <div class="name-grid">
          <label>
            <span>{{ t('firstName') }}</span>
            <input v-model="form.firstName" type="text" autocomplete="given-name" required />
          </label>
          <label>
            <span>{{ t('lastName') }}</span>
            <input v-model="form.lastName" type="text" autocomplete="family-name" required />
          </label>
        </div>

        <label>
          <span>{{ t('email') }}</span>
          <input v-model="form.email" type="email" autocomplete="email" required />
        </label>

        <label>
          <span>{{ t('password') }}</span>
          <input v-model="form.password" type="password" autocomplete="new-password" minlength="6" required />
        </label>

        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? t('adding') : t('registerAction') }}
        </button>

        <p class="helper">
          {{ t('alreadyHaveAccount') }}
          <NuxtLink to="/login">{{ t('login') }}</NuxtLink>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'

definePageMeta({
  layout: 'blank',
})

const { t } = useShopLocale()
const session = useShopSession()

const form = reactive({
  email: '',
  password: '',
  firstName: '',
  lastName: '',
})

const loading = ref(false)
const errorMessage = ref('')

const handleRegister = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const res = await useHttp('/api/auth/register/email', {
      method: 'POST',
      body: form,
    })
    if (res?.code === 200) {
      session.applyAuthPayload(res.data)
      await navigateTo('/')
      return
    }
    errorMessage.value = res?.message || 'Registration failed'
  } catch (error: any) {
    errorMessage.value = error?.data?.message || error?.message || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(15, 118, 110, 0.18), transparent 28%),
    linear-gradient(135deg, #f8f7f1, #eef2ff);
}

.auth-card {
  width: min(960px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
  border-radius: 32px;
  overflow: hidden;
  background: white;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
}

.copy-block,
.form-block {
  padding: 36px;
}

.copy-block {
  background: linear-gradient(135deg, #0f172a, #134e4a);
  color: white;

  h1 {
    margin: 0 0 14px;
    font-size: clamp(34px, 5vw, 52px);
    line-height: 1.05;
  }

  p {
    max-width: 36ch;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.82);
  }
}

.eyebrow {
  margin: 0 0 16px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 12px;
  color: #99f6e4 !important;
}

.form-block {
  display: flex;
  flex-direction: column;
  gap: 18px;

  label {
    display: flex;
    flex-direction: column;
    gap: 8px;
    color: #0f172a;
    font-weight: 700;
  }

  input {
    min-height: 52px;
    border-radius: 16px;
    border: 1px solid rgba(15, 23, 42, 0.12);
    padding: 0 16px;
    font-size: 15px;
  }
}

.name-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.submit-btn {
  min-height: 54px;
  border-radius: 999px;
  border: none;
  background: #0f766e;
  color: white;
  font-weight: 800;
}

.helper,
.error-text {
  margin: 0;
}

.helper a {
  color: #0f766e;
  font-weight: 700;
}

.error-text {
  color: #dc2626;
}

@media (max-width: 760px) {
  .auth-card,
  .name-grid {
    grid-template-columns: 1fr;
  }
}
</style>
