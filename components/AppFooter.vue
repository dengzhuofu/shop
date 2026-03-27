<template>
  <footer class="app-footer">
    <div class="main-footer container">
      <!-- 左侧链接区 -->
      <div class="links-section">
        <div class="link-group">
          <h4>EXPLORE</h4>
          <ul>
            <li><a href="#">All Electric Scooter</a></li>
            <li><a href="#">Off Road Electric Scooter</a></li>
            <li><a href="#">Commuter Electric Scooter</a></li>
            <li><a href="#">Electric Bicycle</a></li>
            <li><a href="#">Electric Skateboards</a></li>
            <li><a href="#">Kids Scooter</a></li>
            <li><a href="#">Accessories</a></li>
            <li><a href="#">Exclusive Discount</a></li>
          </ul>
        </div>
        <div class="link-group">
          <h4>SUPPORT</h4>
          <ul>
            <li><a href="#">Shipping Policy</a></li>
            <li><a href="#">Refund & Return Policy</a></li>
            <li><a href="#">Warranty</a></li>
            <li><a href="#">Order Status</a></li>
            <li><a href="#">Payment Methods</a></li>
            <li><a href="#">Buy Now Pay Later</a></li>
            <li><a href="#">Unauthorized Warning</a></li>
            <li><a href="#">FAQ</a></li>
          </ul>
        </div>
        <div class="link-group">
          <h4>COMPANY</h4>
          <ul>
            <li><a href="#">About Us</a></li>
            <li><a href="#">Contact Us</a></li>
            <li><a href="#">Terms Of Conditions</a></li>
            <li><a href="#">Privacy Policy</a></li>
            <li><a href="#">Become A Dealer</a></li>
            <li><a href="#">Refer A Friend</a></li>
          </ul>
        </div>
      </div>

      <!-- 中间分隔线 -->
      <div class="divider"></div>

      <!-- 右侧订阅区 -->
      <div class="newsletter-section">
        <h2>Stay in the loop <br> with our weekly <br> newsletter</h2>
        <div class="input-group">
          <input type="email" placeholder="Enter your email" />
          <button class="btn-submit">
            <ArrowRightIcon class="icon" />
          </button>
        </div>
        <div class="social-links">
          <a href="#" class="social-icon"><FacebookIcon /></a>
          <a href="#" class="social-icon"><InstagramIcon /></a>
          <a href="#" class="social-icon"><YoutubeIcon /></a>
          <a href="#" class="social-icon"><svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="tiktok-icon"><path d="M9 12a4 4 0 1 0 4 4V4a5 5 0 0 0 5 5"></path></svg></a>
        </div>
      </div>
    </div>

    <!-- 左下角联系信息（绝对定位或使用 Flex 布局调整） -->
    <div class="contact-info container">
      <p class="time">Mon-Fri: 10AM-6PM(PDT)</p>
      <p class="phone">+1(888)293-5788</p>
      <p class="email">support@isinwheel.com</p>
    </div>

    <!-- 底部版权和支付方式 -->
    <div class="bottom-bar">
      <div class="container bottom-content">
        <div class="copyright-area">
          <p class="copyright">&copy; 2026 iSinwheel Official Store.</p>
          <div class="bottom-links">
            <a href="#">Refund policy</a>
            <a href="#">Privacy policy</a>
            <a href="#">Terms of service</a>
            <a href="#">Shipping policy</a>
            <a href="#">Contact Information</a>
          </div>
        </div>
        
        <div class="region-selector">
          <button class="btn-region">
            <span class="flag">🇺🇸</span> United States (USD $) <ChevronDownIcon class="icon-down" />
          </button>
        </div>

        <div class="payment-methods">
          <!-- 这里用占位图或简单的色块表示支付图标 -->
          <span class="pay-icon amex">Amex</span>
          <span class="pay-icon apple">Apple Pay</span>
          <span class="pay-icon diners">Diners Club</span>
          <span class="pay-icon discover">Discover</span>
          <span class="pay-icon google">Google Pay</span>
          <span class="pay-icon mastercard">Mastercard</span>
          <span class="pay-icon paypal">PayPal</span>
          <span class="pay-icon shop">Shop Pay</span>
          <span class="pay-icon venmo">Venmo</span>
          <span class="pay-icon visa">Visa</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ArrowRightIcon, FacebookIcon, InstagramIcon, YoutubeIcon, ChevronDownIcon } from 'lucide-vue-next'
</script>

<style lang="scss" scoped>
@use "sass:color";

.app-footer {
  background-color: #1a1a1a; // 近乎黑色的深灰背景
  color: #fff;
  padding-top: 80px; // 顶部留白
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;

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

  // 左侧链接区
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

        li {
          margin-bottom: 16px;

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
    }
  }

  // 中间细线分隔
  .divider {
    width: 1px;
    background-color: #333;
    height: 300px; // 根据实际内容高度调整
    margin: 0 40px;

    @media (max-width: 1024px) {
      display: none; // 移动端隐藏分隔线
    }
  }

  // 右侧订阅区
  .newsletter-section {
    flex: 1;
    max-width: 450px;

    h2 {
      font-size: 36px;
      font-weight: 700;
      line-height: 1.2;
      margin-bottom: 30px;
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
        transition: color 0.3s ease, transform 0.3s ease;

        svg {
          width: 20px;
          height: 20px;
        }

        &:hover {
          color: #58cc02; // 悬停变绿
          transform: translateY(-2px);
        }
      }
    }
  }

  // 联系信息 (定位在左下角)
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

  // 底部版权栏
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
        
        // 如果有真实图标可以替换背景图
        &.amex { color: #007bc1; }
        &.apple { color: #fff; }
        &.paypal { color: #003087; }
        &.visa { color: #1a1f71; }
      }
    }
  }
}
</style>
