<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-banner">
        <h2>{{ copy.welcome }}</h2>
        <p>{{ t('signInHint') }}</p>
      </div>

      <div class="login-form-wrapper">
        <h1 class="login-title">{{ t('signInTitle') }}</h1>
        <p class="login-subtitle">{{ copy.subtitle }}</p>

        <form class="login-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="email">{{ t('email') }}</label>
            <input id="email" v-model="form.email" type="email" autocomplete="email" required />
          </div>

          <div class="form-group">
            <label for="password">{{ t('password') }}</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              autocomplete="current-password"
              required
            />
          </div>

          <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? copy.loading : t('signInAction') }}
          </button>

          <div class="form-footer">
            <span class="forgot-pwd">{{ copy.forgot }}</span>
            <span>
              {{ t('needAccount') }}
              <NuxtLink to="/register" class="register-link">{{ t('register') }}</NuxtLink>
            </span>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'

definePageMeta({
  layout: 'blank',
})

const { lang, t } = useShopLocale()
const session = useShopSession()

const form = reactive({
  email: '',
  password: '',
})

const loading = ref(false)
const errorMessage = ref('')

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        welcome: '欢迎回来',
        subtitle: '请输入你的邮箱和密码',
        loading: '登录中...',
        forgot: '忘记密码？',
      }
    : {
        welcome: 'Welcome Back',
        subtitle: 'Sign in with your email and password',
        loading: 'Signing in...',
        forgot: 'Forgot password?',
      },
)

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await useHttp('/api/auth/login', {
      method: 'POST',
      body: form,
    })

    if (response?.code === 200) {
      session.applyAuthPayload(response.data)
      await navigateTo('/')
      return
    }

    errorMessage.value = response?.message || 'Login failed'
  } catch (error: any) {
    errorMessage.value = error?.data?.message || error?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap');

.login-page {
  background-color: #000;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Montserrat', sans-serif;
  color: #fff;
  padding: 20px;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 900px;
  background-color: #111;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.15);
  overflow: hidden;
  border: 1px solid #222;
}

.login-banner {
  flex: 1;
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.8) 0%, rgba(0, 0, 0, 0.9) 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  h2 {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 16px;
    color: #fff;
  }

  p {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    line-height: 1.5;
  }

  @media (max-width: 768px) {
    display: none;
  }
}

.login-form-wrapper {
  flex: 1;
  padding: 48px 40px;
  background-color: #111;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  color: #4caf50;
}

.login-subtitle {
  font-size: 14px;
  color: #888;
  margin-bottom: 32px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    font-size: 14px;
    font-weight: 500;
    color: #ccc;
  }

  input {
    background-color: #000;
    border: 1px solid #333;
    border-radius: 8px;
    padding: 12px 16px;
    color: #fff;
    font-family: 'Montserrat', sans-serif;
    font-size: 14px;
    transition: all 0.3s ease;
    box-sizing: border-box;
    width: 100%;

    &:focus {
      outline: none;
      border-color: #4caf50;
      box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
    }

    &::placeholder {
      color: #555;
    }
  }
}

.error-message {
  color: #ff5252;
  font-size: 13px;
  margin-top: -5px;
  margin-bottom: 5px;
}

.submit-btn {
  background-color: #4caf50;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  font-family: 'Montserrat', sans-serif;
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    transform 0.1s ease;
  margin-top: 10px;
  width: 100%;

  &:hover:not(:disabled) {
    background-color: #45a049;
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
  }

  &:disabled {
    background-color: #2e5c31;
    color: #888;
    cursor: not-allowed;
  }
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  font-size: 13px;
  color: #888;
  gap: 12px;
  flex-wrap: wrap;

  a,
  .forgot-pwd {
    color: #4caf50;
    text-decoration: none;
    transition: color 0.3s ease;
  }

  a:hover {
    color: #66bb6a;
    text-decoration: underline;
  }
}
</style>
