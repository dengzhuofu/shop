<template>
  <footer class="app-footer">
    <div class="main-footer container">
      <div class="links-section">
        <div class="link-group">
          <h4>{{ copy.explore }}</h4>
          <ul>
            <li><NuxtLink to="/collections/electric-scooters">{{ t('electricScooters') }}</NuxtLink></li>
            <li><NuxtLink to="/collections/electric-bike">{{ t('electricBike') }}</NuxtLink></li>
            <li><NuxtLink to="/collections/electric-skateboard">{{ t('electricSkateboard') }}</NuxtLink></li>
            <li><NuxtLink to="/collections/accessories">{{ t('accessories') }}</NuxtLink></li>
            <li><NuxtLink to="/">{{ copy.home }}</NuxtLink></li>
          </ul>
        </div>
        <div class="link-group">
          <h4>{{ copy.support }}</h4>
          <ul>
            <li><NuxtLink to="/checkout">{{ t('checkout') }}</NuxtLink></li>
            <li><NuxtLink to="/account/orders">{{ t('orders') }}</NuxtLink></li>
            <li><NuxtLink to="/account/profile">{{ t('profile') }}</NuxtLink></li>
            <li><NuxtLink to="/login">{{ t('login') }}</NuxtLink></li>
            <li><NuxtLink to="/register">{{ t('register') }}</NuxtLink></li>
          </ul>
        </div>
        <div class="link-group">
          <h4>{{ copy.company }}</h4>
          <ul>
            <li><NuxtLink to="/account/profile">{{ copy.about }}</NuxtLink></li>
            <li><NuxtLink to="/account/profile">{{ copy.contact }}</NuxtLink></li>
            <li><NuxtLink to="/account/profile">{{ copy.privacy }}</NuxtLink></li>
            <li><NuxtLink to="/account/profile">{{ copy.terms }}</NuxtLink></li>
          </ul>
        </div>
      </div>

      <div class="divider" />

      <div class="newsletter-section">
        <h2>{{ copy.newsletterTitle }}</h2>
        <p class="newsletter-note">{{ t('footerNote') }}</p>
        <div class="input-group">
          <input type="email" :placeholder="copy.placeholder" />
          <button class="btn-submit" type="button">
            <ArrowRightIcon class="icon" />
          </button>
        </div>
        <div class="social-links">
          <a href="https://facebook.com" target="_blank" rel="noreferrer" class="social-icon">
            <FacebookIcon />
          </a>
          <a href="https://instagram.com" target="_blank" rel="noreferrer" class="social-icon">
            <InstagramIcon />
          </a>
          <a href="https://youtube.com" target="_blank" rel="noreferrer" class="social-icon">
            <YoutubeIcon />
          </a>
        </div>
      </div>
    </div>

    <div class="contact-info container">
      <p class="time">Mon-Fri: 10AM-6PM(PDT)</p>
      <p class="phone">+1(888)293-5788</p>
      <p class="email">support@isinwheel.com</p>
    </div>

    <div class="bottom-bar">
      <div class="container bottom-content">
        <div class="copyright-area">
          <p class="copyright">&copy; 2026 isinwheel storefront.</p>
          <div class="bottom-links">
            <NuxtLink to="/account/profile">{{ copy.privacy }}</NuxtLink>
            <NuxtLink to="/account/profile">{{ copy.terms }}</NuxtLink>
            <NuxtLink to="/account/profile">{{ copy.contact }}</NuxtLink>
          </div>
        </div>

        <div class="region-selector">
          <button class="btn-region" type="button">
            <span class="flag">🇺🇸</span> United States (USD $)
            <ChevronDownIcon class="icon-down" />
          </button>
        </div>

        <div class="payment-methods">
          <span class="pay-icon amex">Amex</span>
          <span class="pay-icon apple">Apple Pay</span>
          <span class="pay-icon discover">Discover</span>
          <span class="pay-icon google">Google Pay</span>
          <span class="pay-icon mastercard">Mastercard</span>
          <span class="pay-icon paypal">PayPal</span>
          <span class="pay-icon visa">Visa</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  ArrowRightIcon,
  ChevronDownIcon,
  FacebookIcon,
  InstagramIcon,
  YoutubeIcon,
} from 'lucide-vue-next'

const { lang, t } = useShopLocale()

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        explore: '探索',
        support: '支持',
        company: '公司',
        home: '首页',
        about: '关于我们',
        contact: '联系我们',
        privacy: '隐私政策',
        terms: '服务条款',
        newsletterTitle: '订阅我们的每周快讯',
        placeholder: '输入你的邮箱',
      }
    : {
        explore: 'EXPLORE',
        support: 'SUPPORT',
        company: 'COMPANY',
        home: 'Home',
        about: 'About Us',
        contact: 'Contact Us',
        privacy: 'Privacy Policy',
        terms: 'Terms of Service',
        newsletterTitle: 'Stay in the loop with our weekly newsletter',
        placeholder: 'Enter your email',
      },
)
</script>

<style scoped lang="scss">
.app-footer {
  background-color: #1a1a1a;
  color: #fff;
  padding-top: 80px;

  .main-footer {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding-bottom: 60px;
    position: relative;
    gap: 40px;

    @media (max-width: 1024px) {
      flex-direction: column;
    }
  }

  .links-section {
    display: flex;
    gap: 80px;
    flex: 1;

    @media (max-width: 768px) {
      flex-wrap: wrap;
      gap: 40px;
    }

    .link-group {
      h4 {
        font-size: 14px;
        font-weight: 700;
        margin-bottom: 24px;
        letter-spacing: 0.5px;
        text-transform: uppercase;
      }

      ul {
        list-style: none;
        padding: 0;
        margin: 0;
      }

      li {
        margin-bottom: 16px;
      }

      a {
        color: #d1d1d1;
        text-decoration: none;
        font-size: 14px;
        transition: color 0.3s ease;

        &:hover {
          color: #fff;
          text-decoration: underline;
        }
      }
    }
  }

  .divider {
    width: 1px;
    background-color: #333;
    height: 300px;
    margin: 0 40px;

    @media (max-width: 1024px) {
      display: none;
    }
  }

  .newsletter-section {
    flex: 1;
    max-width: 450px;

    h2 {
      font-size: 36px;
      font-weight: 700;
      line-height: 1.2;
      margin-bottom: 18px;
    }

    .newsletter-note {
      font-size: 14px;
      line-height: 1.7;
      color: #b8b8b8;
      margin-bottom: 24px;
    }

    .input-group {
      display: flex;
      align-items: center;
      background-color: #2a2a2a;
      border-radius: 30px;
      padding: 6px 6px 6px 20px;
      margin-bottom: 40px;

      input {
        flex: 1;
        background: transparent;
        border: none;
        color: #fff;
        font-size: 14px;
        outline: none;

        &::placeholder {
          color: #888;
        }
      }

      .btn-submit {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: #fff;
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.05);
        }

        .icon {
          color: #000;
          width: 20px;
          height: 20px;
        }
      }
    }

    .social-links {
      display: flex;
      gap: 20px;

      .social-icon {
        color: #fff;
        transition:
          color 0.3s ease,
          transform 0.3s ease;

        svg {
          width: 20px;
          height: 20px;
        }

        &:hover {
          color: #58cc02;
          transform: translateY(-2px);
        }
      }
    }
  }

  .contact-info {
    padding-bottom: 40px;

    .time {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 16px;
    }

    .phone {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .email {
      font-size: 16px;
      color: #d1d1d1;
      text-decoration: underline;
    }
  }

  .bottom-bar {
    border-top: 1px solid #333;
    padding: 20px 0;
    font-size: 12px;
    color: #888;

    .bottom-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 20px;

      @media (max-width: 768px) {
        flex-direction: column;
        align-items: center;
        text-align: center;
      }
    }

    .copyright-area {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .copyright {
        font-weight: 600;
        color: #fff;
      }

      .bottom-links {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;

        a {
          color: #888;
          text-decoration: none;

          &:hover {
            color: #fff;
          }
        }
      }
    }

    .region-selector {
      .btn-region {
        background: transparent;
        border: 1px solid #444;
        color: #fff;
        padding: 8px 16px;
        border-radius: 20px;
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        font-size: 12px;
        transition: border-color 0.3s ease;

        &:hover {
          border-color: #666;
        }

        .flag {
          font-size: 14px;
        }

        .icon-down {
          width: 14px;
          height: 14px;
        }
      }
    }

    .payment-methods {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;

      .pay-icon {
        background-color: #222;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 10px;
        font-weight: 600;
        color: #fff;
        border: 1px solid #333;
        display: inline-block;

        &.amex {
          color: #007bc1;
        }

        &.paypal {
          color: #003087;
        }

        &.visa {
          color: #1a1f71;
        }
      }
    }
  }
}
</style>
