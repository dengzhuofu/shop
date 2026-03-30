<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧装饰区域：品牌口号或欢迎语 -->
      <div class="login-banner">
        <h2>Welcome Back</h2>
        <p>Sign in to continue your journey with us.</p>
      </div>

      <!-- 右侧表单区域 -->
      <div class="login-form-wrapper">
        <h1 class="login-title">Login</h1>
        <p class="login-subtitle">请输入您的账号和密码</p>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="username">用户名 / 邮箱</label>
            <input
              id="username"
              v-model="loginForm.username"
              type="text"
              placeholder="请输入用户名或邮箱"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input
              id="password"
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              required
            />
          </div>

          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>

          <div class="form-footer">
            <a href="#" class="forgot-pwd">忘记密码？</a>
            <span
              >还没有账号？
              <nuxt-link to="/register" class="register-link"
                >立即注册</nuxt-link
              ></span
            >
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

// 路由实例，用于登录成功后跳转
const router = useRouter()

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
})

// 加载状态与错误信息
const loading = ref(false)
const errorMessage = ref('')

// 处理登录请求
const handleLogin = async () => {
  // 简单的前端校验
  if (!loginForm.username || !loginForm.password) {
    errorMessage.value = '请填写完整的账号和密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 对接 /api/auth/login 接口
    const response = await useHttp('/api/auth/login', {
      method: 'POST',
      body: {
        username: loginForm.username,
        password: loginForm.password,
      },
    })

    // 根据通用后端接口约定，尝试获取 token
    // 后端返回格式: { code: 200, data: { tokenName: "Authorization", tokenValue: "xxx" } }
    const token =
      response?.data?.tokenValue || response?.tokenValue || response?.token

    if (token) {
      // 成功后保存 token 到 Cookie 中，这里设置过期时间为7天
      const tokenCookie = useCookie('token', { maxAge: 60 * 60 * 24 * 7 })
      tokenCookie.value = token

      // 登录成功后跳转至首页
      router.push('/')
    } else {
      // 接口请求成功但没有返回 token，显示错误信息
      errorMessage.value = response?.message || '登录失败，请检查账号和密码'
    }
  } catch (error) {
    // 捕获请求异常，例如 400/401/500 等
    console.error('登录请求失败:', error)
    errorMessage.value =
      error?.data?.message || '网络异常或服务器错误，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
/* 引入 Montserrat 字体 */
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap');

.login-page {
  /* 使用指定的黑绿主题：背景使用纯黑，字体默认白色 */
  background-color: #000000;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Montserrat', sans-serif;
  color: #ffffff;
  padding: 20px;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 900px;
  background-color: #111111; /* 稍微提亮的黑色作为表单容器背景 */
  border-radius: 16px;
  /* 绿色微弱发光阴影，呼应主题色 */
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.15);
  overflow: hidden;
  border: 1px solid #222222;
}

.login-banner {
  flex: 1;
  /* 左侧加入渐变背景，融合绿色和黑色 */
  background: linear-gradient(
    135deg,
    rgba(76, 175, 80, 0.8) 0%,
    rgba(0, 0, 0, 0.9) 100%
  );
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  h2 {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 16px;
    color: #ffffff;
  }

  p {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    line-height: 1.5;
  }

  @media (max-width: 768px) {
    display: none; /* 在移动端隐藏左侧 Banner，节省空间 */
  }
}

.login-form-wrapper {
  flex: 1;
  padding: 48px 40px;
  background-color: #111111;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #4caf50; /* 使用指定的绿色作为主标题颜色 */
}

.login-subtitle {
  font-size: 14px;
  color: #888888;
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
    color: #cccccc;
  }

  input {
    background-color: #000000;
    border: 1px solid #333333;
    border-radius: 8px;
    padding: 12px 16px;
    color: #ffffff;
    font-family: 'Montserrat', sans-serif;
    font-size: 14px;
    transition: all 0.3s ease;
    box-sizing: border-box;
    width: 100%;

    &:focus {
      outline: none;
      border-color: #4caf50; /* 聚焦时边框变绿 */
      box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
    }

    &::placeholder {
      color: #555555;
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
  background-color: #4caf50; /* 指定的绿色 */
  color: #ffffff;
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
    background-color: #45a049; /* 稍微深一点的绿色 */
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
  }

  &:disabled {
    background-color: #2e5c31;
    color: #888888;
    cursor: not-allowed;
  }
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  font-size: 13px;
  color: #888888;

  a {
    color: #4caf50;
    text-decoration: none;
    transition: color 0.3s ease;

    &:hover {
      color: #66bb6a;
      text-decoration: underline;
    }
  }
}
</style>
