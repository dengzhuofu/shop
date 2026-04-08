<template>
  <AuthCommerceShell
    :hero-eyebrow="copy.heroEyebrow"
    :hero-title="copy.heroTitle"
    :hero-body="copy.heroBody"
    :hero-image="copy.heroImage"
    :hero-image-alt="copy.heroImageAlt"
    :features="copy.features"
    :metrics="copy.metrics"
    :panel-eyebrow="copy.panelEyebrow"
    :panel-title="t('signInTitle')"
    :panel-subtitle="copy.panelSubtitle"
    :back-to-store-text="copy.backToStoreText"
  >
    <form class="auth-form" @submit.prevent="handleLogin">
      <div class="auth-field">
        <label class="auth-label" for="email">{{ t('email') }}</label>
        <input
          id="email"
          v-model="form.email"
          class="auth-input"
          type="email"
          :placeholder="copy.emailPlaceholder"
          autocomplete="email"
          required
        />
      </div>

      <div class="auth-field">
        <label class="auth-label" for="password">{{ t('password') }}</label>
        <input
          id="password"
          v-model="form.password"
          class="auth-input"
          type="password"
          :placeholder="copy.passwordPlaceholder"
          autocomplete="current-password"
          required
        />
      </div>

      <div v-if="errorMessage" class="auth-error">{{ errorMessage }}</div>

      <button type="submit" class="auth-submit" :disabled="loading">
        {{ loading ? copy.loading : t('signInAction') }}
      </button>

      <div class="auth-meta-row">
        <span>{{ copy.forgot }}</span>
        <span>
          {{ copy.switchPrompt }}
          <NuxtLink to="/register" class="auth-switch-link">{{ t('register') }}</NuxtLink>
        </span>
      </div>

      <p class="auth-footnote">{{ copy.footnote }}</p>
    </form>
  </AuthCommerceShell>
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
        heroEyebrow: '\u5b98\u65b9\u9a91\u884c\u5546\u5e97',
        heroTitle: '\u56de\u5230\u4f60\u7684\u9a91\u884c\u8d26\u6237',
        heroBody:
          '\u767b\u5f55\u540e\u53ef\u4ee5\u7ee7\u7eed\u67e5\u770b\u8ba2\u5355\uff0c\u8ddf\u8e2a\u914d\u4ef6\u8865\u8d27\uff0c\u5e76\u4fdd\u7559\u4f60\u7684\u6536\u8d27\u4e0e\u4ed8\u6b3e\u4fe1\u606f\u3002',
        heroImage:
          'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1600',
        heroImageAlt: '\u57ce\u5e02\u7535\u52a8\u6ed1\u677f\u8f66',
        features: [
          {
            title: '\u5feb\u901f\u627e\u56de\u8ba2\u5355',
            description: '\u968f\u65f6\u67e5\u770b\u53d1\u8d27\u8fdb\u5ea6\uff0c\u552e\u540e\u4e0e\u96f6\u914d\u4ef6\u7533\u8bf7\u66f4\u7701\u5fc3\u3002',
          },
          {
            title: '\u4fdd\u5b58\u5e38\u7528\u5730\u5740',
            description: '\u590d\u8d2d\u65f6\u76f4\u63a5\u8c03\u7528\u5e38\u7528\u6536\u8d27\u4fe1\u606f\uff0c\u7f29\u77ed\u4e0b\u5355\u8def\u5f84\u3002',
          },
          {
            title: '\u5b98\u65b9\u670d\u52a1\u652f\u6301',
            description: '\u4fdd\u4fee\u3001\u914d\u4ef6\u548c\u9a91\u884c\u5efa\u8bae\u90fd\u4f1a\u4e0e\u8d26\u6237\u4fe1\u606f\u4e00\u8d77\u8bb0\u5f55\u3002',
          },
        ],
        metrics: [
          { value: '24/7', label: '\u8d26\u6237\u4e0e\u8ba2\u5355\u67e5\u8be2' },
          { value: '2-Step', label: '\u5b89\u5168\u767b\u5f55\u4e0e\u652f\u4ed8\u4fdd\u62a4' },
          { value: 'US', label: '\u672c\u5730\u4ed3\u4e0e\u5ba2\u670d\u652f\u6301' },
        ],
        panelEyebrow: '\u4f1a\u5458\u767b\u5f55',
        panelSubtitle:
          '\u4f7f\u7528\u4f60\u7684\u90ae\u7bb1\u548c\u5bc6\u7801\u8fdb\u5165 iSinwheel Official Store\uff0c\u7ee7\u7eed\u5b8c\u6210\u4e0b\u5355\u6216\u7ba1\u7406\u8d26\u6237\u3002',
        backToStoreText: '\u8fd4\u56de\u5546\u57ce',
        emailPlaceholder: 'you@example.com',
        passwordPlaceholder: '\u8f93\u5165\u4f60\u7684\u5bc6\u7801',
        loading: '\u6b63\u5728\u767b\u5f55...',
        forgot: '\u5fd8\u8bb0\u5bc6\u7801\uff1f\u53ef\u8054\u7cfb\u5ba2\u670d\u627e\u56de',
        switchPrompt: '\u8fd8\u6ca1\u6709\u8d26\u6237\uff1f',
        footnote:
          '\u767b\u5f55\u540e\u4f60\u7684\u8d2d\u7269\u8f66\u3001\u6536\u8d27\u5730\u5740\u548c\u8ba2\u5355\u8bb0\u5f55\u4f1a\u5728\u540c\u4e00\u8d26\u6237\u4e0b\u540c\u6b65\u3002',
      }
    : {
        heroEyebrow: 'Official Rider Store',
        heroTitle: 'Pick up where your last ride left off',
        heroBody:
          'Sign in to review orders, track replacement parts, and keep your saved checkout details ready for the next purchase.',
        heroImage:
          'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1600',
        heroImageAlt: 'Urban electric scooter',
        features: [
          {
            title: 'Order history on demand',
            description: 'See delivery progress, support requests, and service follow-ups without digging through email.',
          },
          {
            title: 'Faster repeat checkout',
            description: 'Your saved address and account details stay ready when you come back for parts or another ride.',
          },
          {
            title: 'Official store support',
            description: 'Warranty questions, accessories, and riding guidance stay connected to one rider profile.',
          },
        ],
        metrics: [
          { value: '24/7', label: 'Account and order access' },
          { value: '2-Step', label: 'Protected sign-in and payment flow' },
          { value: 'US', label: 'Warehouse and support coverage' },
        ],
        panelEyebrow: 'Member Sign In',
        panelSubtitle:
          'Use your email and password to access iSinwheel Official Store and continue checkout or manage your rider account.',
        backToStoreText: 'Back to store',
        emailPlaceholder: 'you@example.com',
        passwordPlaceholder: 'Enter your password',
        loading: 'Signing in...',
        forgot: 'Forgot password? Contact support to recover access',
        switchPrompt: "Don't have an account yet?",
        footnote:
          'Once signed in, your cart, saved addresses, and order records stay synced under the same account.',
      },
)

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const payload = {
      email: form.email.trim().toLowerCase(),
      password: form.password,
    }

    const response = await useHttp('/api/auth/login', {
      method: 'POST',
      body: payload,
      showError: false,
      handleAuthError: false,
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
