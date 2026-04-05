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
  background: #fff;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Montserrat', sans-serif;
  color: #111;
  padding: 32px 20px;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 1100px;
  background: #fff;
  border-radius: 28px;
  box-shadow: 0 24px 60px rgba(17, 17, 17, 0.08);
  overflow: hidden;
  border: 1px solid #ececec;
}

.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #f4ffe8 0%, #eef8ff 100%);
  padding: 56px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  h2 {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 16px;
    color: #111;
  }

  p {
    font-size: 16px;
    color: #4b5563;
    line-height: 1.5;
  }

  @media (max-width: 768px) {
    display: none;
  }
}

.login-form-wrapper {
  flex: 1;
  padding: 56px 48px;
  background: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  color: #111;
  text-align: center;
}

.login-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 32px;
  text-align: center;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  max-width: 420px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;

  label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    width: 100%;
    text-align: center;
  }

  input {
    background: #fff;
    border: 1px solid #d7dce3;
    border-radius: 14px;
    padding: 14px 18px;
    color: #111;
    font-family: 'Montserrat', sans-serif;
    font-size: 14px;
    transition: all 0.3s ease;
    box-sizing: border-box;
    width: 100%;
    text-align: center;

    &:focus {
      outline: none;
      border-color: #4caf50;
      box-shadow: 0 0 0 4px rgba(76, 175, 80, 0.12);
    }

    &::placeholder {
      color: #9ca3af;
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
  background: #111;
  color: #fff;
  border: none;
  border-radius: 999px;
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
    background: #2a2a2a;
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
  }

  &:disabled {
    background: #cfd4dc;
    color: #6b7280;
    cursor: not-allowed;
  }
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  font-size: 13px;
  color: #6b7280;
  gap: 12px;
  flex-wrap: wrap;

  a,
  .forgot-pwd {
    color: #111;
    text-decoration: none;
    transition: color 0.3s ease;
  }

  a:hover {
    color: #58cc02;
    text-decoration: underline;
  }
}

@media (max-width: 768px) {
  .login-container {
    max-width: 520px;
  }

  .login-form-wrapper {
    padding: 40px 24px;
  }

  .login-form {
    max-width: 100%;
  }
}
</style>
