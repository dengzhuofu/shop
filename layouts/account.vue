<template>
  <div class="layout-account">
    <header class="account-header">
      <div class="container header-inner">
        <div class="left-nav">
          <NuxtLink to="/" class="logo">
            <h2><i>isinwheel</i></h2>
          </NuxtLink>
          <nav class="nav-links">
            <NuxtLink to="/account/orders" class="nav-link" active-class="active">Orders</NuxtLink>
            <NuxtLink to="/account/profile" class="nav-link" active-class="active">Profile</NuxtLink>
          </nav>
        </div>
        <div class="right-actions">
          <UserIcon class="icon" />
        </div>
      </div>
    </header>
    <main class="account-main">
      <slot />
    </main>
    <footer class="account-footer">
      <div class="container footer-inner">
        <div class="region">
          <GlobeIcon class="icon" />
          <span>United States ⌄</span>
        </div>
        <div class="footer-links">
          <a href="#">Refund policy</a>
          <a href="#">Shipping</a>
          <a href="#">Privacy policy</a>
          <a href="#">Terms of service</a>
          <a href="#">Contact information</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { UserIcon, GlobeIcon } from 'lucide-vue-next'
</script>

<style lang="scss" scoped>
.layout-account {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #fafafa;
  font-family: 'Montserrat', sans-serif;
}

.account-header {
  background: #fff;
  border-bottom: 1px solid #eaeaea;
  padding: 16px 0;

  .header-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .left-nav {
    display: flex;
    align-items: center;
    gap: 40px;

    .logo {
      text-decoration: none;
      color: #111;
      h2 {
        font-size: 24px;
        font-weight: 800;
        margin: 0;
      }
    }

    .nav-links {
      display: flex;
      gap: 20px;

      .nav-link {
        text-decoration: none;
        color: #666;
        font-size: 14px;
        font-weight: 500;
        padding-bottom: 4px;

        &:hover {
          color: #111;
        }

        &.active {
          color: #111;
          border-bottom: 2px solid #111;
        }
      }
    }
  }

  .right-actions {
    .icon {
      width: 24px;
      height: 24px;
      color: #111;
      cursor: pointer;
    }
  }
}

.account-main {
  flex: 1;
  padding: 40px 20px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}

.account-footer {
  border-top: 1px solid #eaeaea;
  padding: 20px 0;
  background: #fafafa;

  .footer-inner {
    display: flex;
    gap: 20px;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    font-size: 12px;
    color: #666;
    flex-wrap: wrap;

    .region {
      display: flex;
      align-items: center;
      gap: 4px;
      cursor: pointer;
      color: #333;

      .icon {
        width: 14px;
        height: 14px;
      }
    }

    .footer-links {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;

      a {
        color: #666;
        text-decoration: none;
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>
