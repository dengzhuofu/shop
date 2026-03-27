<template>
  <footer class="app-footer">
    <!-- 服务保障区 -->
    <div class="features-bar">
      <div class="container features-content">
        <div class="feature-item" v-for="item in features" :key="item.title">
          <component :is="item.icon" class="icon" />
          <div class="text">
            <h4>{{ item.title }}</h4>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="main-footer container">
      <!-- 订阅区 -->
      <div class="newsletter-section">
        <h3>Join Our Community</h3>
        <p>Subscribe to get special offers, free giveaways, and once-in-a-lifetime deals.</p>
        <div class="input-group">
          <input type="email" placeholder="Enter your email" />
          <button class="btn-subscribe">Subscribe</button>
        </div>
      </div>
      
      <!-- 链接区 -->
      <div class="links-section">
        <div class="link-group">
          <h4>Shop</h4>
          <ul>
            <li><a href="#">Electric Scooter</a></li>
            <li><a href="#">Electric Bike</a></li>
            <li><a href="#">Electric Skateboard</a></li>
            <li><a href="#">Accessories</a></li>
          </ul>
        </div>
        <div class="link-group">
          <h4>Support</h4>
          <ul>
            <li><a href="#">Contact Us</a></li>
            <li><a href="#">FAQs</a></li>
            <li><a href="#">Warranty</a></li>
            <li><a href="#">Shipping Policy</a></li>
          </ul>
        </div>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="container">
        <p>&copy; 2026 isinwheel Official Store. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { TruckIcon, ShieldCheckIcon, HeadphonesIcon, CreditCardIcon } from 'lucide-vue-next'
import { markRaw } from 'vue'

const features = [
  { icon: markRaw(TruckIcon), title: 'Fast & Reliable Shipping', desc: '2-5 Business Days Delivery' },
  { icon: markRaw(ShieldCheckIcon), title: '1-Year Warranty Service', desc: 'Backed by isinwheel Warranty' },
  { icon: markRaw(HeadphonesIcon), title: 'US-Based Support', desc: 'Live Chat and Phone Service' },
  { icon: markRaw(CreditCardIcon), title: 'Secure Payments', desc: 'PayPal/Credit Card' },
]
</script>

<style lang="scss" scoped>
@use "sass:color";

.app-footer {
  background-color: #111;
  color: #fff;
  margin-top: 60px;

  .features-bar {
    background-color: #222;
    padding: 30px 0;

    .features-content {
      display: flex;
      justify-content: space-between;
      gap: 20px;

      .feature-item {
        display: flex;
        align-items: center;
        gap: 16px;

        .icon {
          width: 32px;
          height: 32px;
          color: #58cc02;
        }

        .text {
          h4 {
            font-size: 14px;
            margin-bottom: 4px;
          }
          p {
            font-size: 12px;
            color: #999;
          }
        }
      }
    }
  }

  .main-footer {
    display: flex;
    justify-content: space-between;
    padding: 60px 0;

    .newsletter-section {
      max-width: 400px;
      
      h3 {
        font-size: 24px;
        margin-bottom: 12px;
      }
      
      p {
        color: #999;
        margin-bottom: 24px;
        font-size: 14px;
      }

      .input-group {
        display: flex;
        gap: 12px;

        input {
          flex: 1;
          padding: 12px 16px;
          border: 1px solid #333;
          background: #222;
          color: #fff;
          border-radius: 4px;
          outline: none;

          &:focus {
            border-color: #58cc02;
          }
        }

        .btn-subscribe {
          background: #58cc02;
          color: #fff;
          padding: 0 24px;
          border-radius: 4px;
          font-weight: bold;
          transition: background 0.3s;

          &:hover {
            background: color.adjust(#58cc02, $lightness: -10%);
          }
        }
      }
    }

    .links-section {
      display: flex;
      gap: 60px;

      .link-group {
        h4 {
          font-size: 16px;
          margin-bottom: 20px;
        }

        ul {
          li {
            margin-bottom: 12px;
            a {
              color: #999;
              font-size: 14px;
              transition: color 0.3s;
              
              &:hover {
                color: #58cc02;
              }
            }
          }
        }
      }
    }
  }

  .bottom-bar {
    border-top: 1px solid #222;
    padding: 20px 0;
    text-align: center;
    font-size: 12px;
    color: #666;
  }
}
</style>
