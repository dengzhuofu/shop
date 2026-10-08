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
        <span>{{ copy.metaHint }} <NuxtLink to="/pages/terms-of-service">{{ lang === 'zh' ? '服务条款' : 'Terms of service' }}</NuxtLink> · <NuxtLink to="/pages/privacy-policy">{{ lang === 'zh' ? '隐私政策' : 'Privacy policy' }}</NuxtLink></span>
        <span>
          {{ copy.switchPrompt }}
          <NuxtLink to="/login" class="auth-switch-link">{{ t('login') }}</NuxtLink>
        </span>
      </div>

      <p class="auth-footnote"><NuxtLink to="/pages/privacy-policy">{{ copy.footnote }}</NuxtLink></p>
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

const copy = computed(() => lang.value === 'zh' ? {"heroEyebrow": "CBJJ 官方商店", "heroTitle": "管理账户，准备下一次出行", "heroBody": "在同一个账户中保存收货地址、查看 CBJJ 订单。", "heroImage": "/cbjj/hero/hero.png", "heroImageAlt": "电动滑板车展示", "features": [{"title": "订单记录", "description": "查看订单及付款状态。"}, {"title": "保存地址", "description": "准备好常用配送信息。"}, {"title": "客户服务", "description": "查看真实联系方式与商店政策。"}], "metrics": [], "panelEyebrow": "创建账户", "panelSubtitle": "使用邮箱和密码访问 CBJJ 账户。", "backToStoreText": "返回商店", "emailPlaceholder": "you@example.com", "passwordPlaceholder": "至少 6 个字符", "loading": "正在创建账户…", "forgot": "忘记密码？请联系客服", "switchPrompt": "已有账户？", "footnote": "有关账户与订单资料的使用，请查看隐私政策。", "firstNamePlaceholder": "名字", "lastNamePlaceholder": "姓氏", "passwordHint": "请使用安全且独立的密码。", "metaHint": "创建账户表示同意服务条款，并知悉隐私政策。"} : {"heroEyebrow": "CBJJ official store", "heroTitle": "Your account, your next ride", "heroBody": "Save delivery addresses and review your CBJJ orders in one place.", "heroImage": "/cbjj/hero/hero.png", "heroImageAlt": "Electric scooter showcase", "features": [{"title": "Order history", "description": "View your orders and payment status."}, {"title": "Saved addresses", "description": "Keep your delivery information ready."}, {"title": "Customer support", "description": "Find contact details and published store policies."}], "metrics": [], "panelEyebrow": "Create account", "panelSubtitle": "Use your email and password to access your CBJJ account.", "backToStoreText": "Back to store", "emailPlaceholder": "you@example.com", "passwordPlaceholder": "At least 6 characters", "loading": "Creating account...", "forgot": "Forgot password? Contact support", "switchPrompt": "Already have an account?", "footnote": "See our privacy policy for how we use your account and order information.", "firstNamePlaceholder": "First name", "lastNamePlaceholder": "Last name", "passwordHint": "Use a strong, unique password.", "metaHint": "By creating an account you agree to our Terms of service and acknowledge our Privacy policy."})

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
