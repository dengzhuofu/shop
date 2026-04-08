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
    :panel-title="t('registerTitle')"
    :panel-subtitle="copy.panelSubtitle"
    :back-to-store-text="copy.backToStoreText"
  >
    <form class="auth-form" @submit.prevent="handleRegister">
      <div class="auth-grid">
        <div class="auth-field">
          <label class="auth-label" for="first-name">{{ t('firstName') }}</label>
          <input
            id="first-name"
            v-model="form.firstName"
            class="auth-input"
            type="text"
            :placeholder="copy.firstNamePlaceholder"
            autocomplete="given-name"
            required
          />
        </div>

        <div class="auth-field">
          <label class="auth-label" for="last-name">{{ t('lastName') }}</label>
          <input
            id="last-name"
            v-model="form.lastName"
            class="auth-input"
            type="text"
            :placeholder="copy.lastNamePlaceholder"
            autocomplete="family-name"
            required
          />
        </div>
      </div>

      <div class="auth-field">
        <label class="auth-label" for="register-email">{{ t('email') }}</label>
        <input
          id="register-email"
          v-model="form.email"
          class="auth-input"
          type="email"
          :placeholder="copy.emailPlaceholder"
          autocomplete="email"
          required
        />
      </div>

      <div class="auth-field">
        <label class="auth-label" for="register-password">{{ t('password') }}</label>
        <input
          id="register-password"
          v-model="form.password"
          class="auth-input"
          type="password"
          :placeholder="copy.passwordPlaceholder"
          autocomplete="new-password"
          minlength="6"
          required
        />
      </div>

      <p class="auth-inline-note">{{ copy.passwordHint }}</p>

      <div v-if="errorMessage" class="auth-error">{{ errorMessage }}</div>

      <button type="submit" class="auth-submit" :disabled="loading">
        {{ loading ? copy.loading : t('registerAction') }}
      </button>

      <div class="auth-meta-row">
        <span>{{ copy.metaHint }}</span>
        <span>
          {{ copy.switchPrompt }}
          <NuxtLink to="/login" class="auth-switch-link">{{ t('login') }}</NuxtLink>
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
  firstName: '',
  lastName: '',
})

const loading = ref(false)
const errorMessage = ref('')

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        heroEyebrow: '\u521b\u5efa\u4f60\u7684\u4f1a\u5458\u8d26\u6237',
        heroTitle: '\u4ece\u7b2c\u4e00\u5355\u5f00\u59cb\u5efa\u7acb\u9a91\u884c\u6863\u6848',
        heroBody:
          '\u6ce8\u518c\u540e\u53ef\u4ee5\u4fdd\u5b58\u5730\u5740\uff0c\u7ba1\u7406\u8ba2\u5355\uff0c\u9886\u53d6\u4f1a\u5458\u6d3b\u52a8\uff0c\u8ba9\u4e4b\u540e\u7684\u8d2d\u8f66\u548c\u8865\u8d27\u66f4\u987a\u624b\u3002',
        heroImage:
          'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1600',
        heroImageAlt: '\u7535\u52a8\u81ea\u884c\u8f66\u4e0e\u9a91\u884c\u573a\u666f',
        features: [
          {
            title: '\u4f1a\u5458\u8d2d\u7269\u4f53\u9a8c',
            description: '\u4e0b\u5355\uff0c\u67e5\u8be2\uff0c\u9886\u5238\u548c\u552e\u540e\u5165\u53e3\u4f1a\u96c6\u4e2d\u5728\u540c\u4e00\u4e2a\u8d26\u6237\u4e0b\u3002',
          },
          {
            title: '\u7ed1\u5b9a\u5e38\u7528\u4fe1\u606f',
            description: '\u5e38\u7528\u5730\u5740\u3001\u59d3\u540d\u4e0e\u8054\u7cfb\u65b9\u5f0f\u4f1a\u968f\u8ba2\u5355\u8fde\u7eed\u4fdd\u5b58\u3002',
          },
          {
            title: '\u5148\u5403\u6d3b\u52a8\u4e0e\u8865\u8d27',
            description: '\u65b0\u6b3e\u4e0a\u67b6\uff0c\u5957\u88c5\u4fc3\u9500\u548c\u914d\u4ef6\u5230\u8d27\u65f6\u80fd\u66f4\u5feb\u8ddf\u8fdb\u3002',
          },
        ],
        metrics: [
          { value: '1 Min', label: '\u5b8c\u6210\u6ce8\u518c' },
          { value: 'SSL', label: '\u52a0\u5bc6\u63d0\u4ea4\u4e0e\u652f\u4ed8\u4fdd\u62a4' },
          { value: 'VIP', label: '\u4f18\u60e0\u4e0e\u670d\u52a1\u8fdb\u7a0b\u540c\u6b65' },
        ],
        panelEyebrow: '\u65b0\u7528\u6237\u6ce8\u518c',
        panelSubtitle:
          '\u4f7f\u7528\u90ae\u7bb1\u5feb\u901f\u521b\u5efa iSinwheel Official Store \u8d26\u6237\uff0c\u4e3a\u540e\u7eed\u8d2d\u8f66\uff0c\u914d\u4ef6\u590d\u8d2d\u548c\u552e\u540e\u6d41\u7a0b\u505a\u597d\u51c6\u5907\u3002',
        backToStoreText: '\u8fd4\u56de\u5546\u57ce',
        firstNamePlaceholder: '\u4f8b\u5982\uff1aEason',
        lastNamePlaceholder: '\u4f8b\u5982\uff1aLi',
        emailPlaceholder: 'you@example.com',
        passwordPlaceholder: '\u81f3\u5c11 6 \u4f4d\u5bc6\u7801',
        passwordHint: '\u5efa\u8bae\u4f7f\u7528\u5305\u542b\u5b57\u6bcd\u548c\u6570\u5b57\u7684\u5bc6\u7801\uff0c\u540e\u7eed\u66f4\u65b9\u4fbf\u627e\u56de\u3002',
        loading: '\u6b63\u5728\u521b\u5efa\u8d26\u6237...',
        metaHint: '\u6ce8\u518c\u5373\u8868\u793a\u540c\u610f\u5e38\u89c4\u8d2d\u7269\u4e0e\u552e\u540e\u901a\u77e5',
        switchPrompt: '\u5df2\u7ecf\u6709\u8d26\u6237\uff1f',
        footnote:
          '\u6211\u4eec\u53ea\u4f1a\u5728\u8ba2\u5355\uff0c\u670d\u52a1\u548c\u4fc3\u9500\u76f8\u5173\u573a\u666f\u4e0b\u4f7f\u7528\u4f60\u63d0\u4ea4\u7684\u8054\u7cfb\u4fe1\u606f\u3002',
      }
    : {
        heroEyebrow: 'Create your member account',
        heroTitle: 'Build your rider profile before the first checkout',
        heroBody:
          'Register once to save addresses, manage orders, unlock member campaigns, and make future scooter or parts purchases feel effortless.',
        heroImage:
          'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1600',
        heroImageAlt: 'Electric bike and rider scene',
        features: [
          {
            title: 'One account for the full store',
            description: 'Checkout, support, saved addresses, and campaign access all live under a single rider profile.',
          },
          {
            title: 'Details that stay ready',
            description: 'Keep your name, address, and order preferences prepared for faster repeat purchases.',
          },
          {
            title: 'Closer to new drops',
            description: 'New launches, bundle offers, and parts restocks become easier to follow once your account is set.',
          },
        ],
        metrics: [
          { value: '1 Min', label: 'Typical setup time' },
          { value: 'SSL', label: 'Protected submission and checkout' },
          { value: 'VIP', label: 'Campaign and service access' },
        ],
        panelEyebrow: 'New Member Setup',
        panelSubtitle:
          'Create your iSinwheel Official Store account with email so future checkouts, accessory reorders, and service requests all start from the same place.',
        backToStoreText: 'Back to store',
        firstNamePlaceholder: 'First name',
        lastNamePlaceholder: 'Last name',
        emailPlaceholder: 'you@example.com',
        passwordPlaceholder: 'At least 6 characters',
        passwordHint: 'Use a password with letters and numbers so your account is easier to recover later.',
        loading: 'Creating account...',
        metaHint: 'Registration enables routine order, support, and campaign notifications',
        switchPrompt: 'Already have an account?',
        footnote:
          'Your contact details are used only for order, service, and relevant store communication.',
      },
)

const handleRegister = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const payload = {
      email: form.email.trim().toLowerCase(),
      password: form.password,
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
    }

    const response = await useHttp('/api/auth/register/email', {
      method: 'POST',
      body: payload,
      showError: false,
    })

    if (response?.code === 200) {
      session.applyAuthPayload(response.data)
      await navigateTo('/')
      return
    }

    errorMessage.value = response?.message || 'Registration failed'
  } catch (error: any) {
    errorMessage.value = error?.data?.message || error?.message || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>
