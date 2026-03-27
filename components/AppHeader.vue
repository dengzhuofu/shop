<template>
  <header class="app-header">
    <!-- 顶部公告栏 -->
    <div class="top-bar">
      <div class="container top-bar-content">
        <div class="social-links">
          <!-- 社交媒体图标占位 -->
          <span>f</span>
          <span>i</span>
          <span>y</span>
          <span>t</span>
        </div>
        <div class="promo-text">
          <div class="countdown">
            <span class="time-block">01<small>Days</small></span> : 
            <span class="time-block">01<small>Hours</small></span> : 
            <span class="time-block">59<small>Mins</small></span> : 
            <span class="time-block">10<small>Secs</small></span>
          </div>
          <span class="text">Bundle Sale: Buy Any Two Items to Save 10%</span>
        </div>
        <div class="region-selector">
          <span>🇺🇸 United States (USD $) ⌄</span>
        </div>
      </div>
    </div>

    <!-- 主导航区 -->
    <div class="main-nav">
      <div class="container nav-content">
        <!-- Logo -->
        <NuxtLink to="/" class="logo">
          <h2><i>isinwheel</i></h2>
        </NuxtLink>

        <!-- 导航菜单 -->
        <nav class="desktop-nav">
          <ul class="nav-list">
            <li class="nav-item"><NuxtLink to="/collections/electric-scooter">Electric Scooter</NuxtLink></li>
            <li class="nav-item hot">
              <NuxtLink to="/collections/spring-sale">Spring Sale</NuxtLink>
              <span class="badge">HOT</span>
            </li>
            <li class="nav-item"><NuxtLink to="/collections/electric-skateboard">Electric Skateboard</NuxtLink></li>
            <li class="nav-item"><NuxtLink to="/collections/electric-bike">Electric Bike</NuxtLink></li>
            <li class="nav-item"><NuxtLink to="/collections/accessories">Accessories</NuxtLink></li>
            <li class="nav-item"><NuxtLink to="/pages/support">Support</NuxtLink></li>
          </ul>
        </nav>

        <!-- 右侧操作区 -->
        <div class="actions">
          <div class="country-pill">
            <span>🇺🇸 US</span>
          </div>
          <SearchIcon class="icon" />
          <UserIcon class="icon" />
          <div class="cart-icon">
            <ShoppingCartIcon class="icon" />
            <span class="cart-count">0</span>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { SearchIcon, UserIcon, ShoppingCartIcon } from 'lucide-vue-next'
</script>

<style lang="scss" scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: $white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);

  .top-bar {
    background-color: #58cc02; // 根据原图绿色
    color: $white;
    font-size: 12px;
    padding: 8px 0;

    .top-bar-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .social-links {
        display: flex;
        gap: 12px;
        span { cursor: pointer; }
      }

      .promo-text {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .countdown {
          display: flex;
          align-items: center;
          gap: 4px;
          background: rgba(255,255,255,0.2);
          padding: 4px 12px;
          border-radius: 4px;

          .time-block {
            display: flex;
            flex-direction: column;
            align-items: center;
            font-weight: bold;
            small {
              font-size: 8px;
              font-weight: normal;
            }
          }
        }
      }

      .region-selector {
        cursor: pointer;
      }
    }
  }

  .main-nav {
    padding: 16px 0;

    .nav-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo {
      h2 {
        font-size: 24px;
        font-weight: 800;
        letter-spacing: -1px;
      }
    }

    .desktop-nav {
      .nav-list {
        display: flex;
        gap: 32px;
        align-items: center;

        .nav-item {
          font-weight: 600;
          font-size: 15px;
          position: relative;
          cursor: pointer;
          
          &:hover {
            color: #58cc02;
          }

          &.hot {
            .badge {
              position: absolute;
              top: -12px;
              right: -20px;
              background-color: $danger-color;
              color: $white;
              font-size: 10px;
              padding: 2px 6px;
              border-radius: 10px;
              font-style: italic;
            }
          }
        }
      }
    }

    .actions {
      display: flex;
      align-items: center;
      gap: 20px;

      .country-pill {
        border: 1px solid $border-color;
        border-radius: 20px;
        padding: 4px 12px;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
      }

      .icon {
        width: 20px;
        height: 20px;
        cursor: pointer;
        &:hover {
          color: #58cc02;
        }
      }

      .cart-icon {
        position: relative;
        .cart-count {
          position: absolute;
          top: -8px;
          right: -8px;
          background: $danger-color;
          color: $white;
          font-size: 10px;
          width: 16px;
          height: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
        }
      }
    }
  }
}
</style>
