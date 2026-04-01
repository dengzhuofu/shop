<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <!-- 左侧内容 -->
      <main class="checkout-main">
        <div class="checkout-header">
          <NuxtLink to="/" class="logo">
            <h2><i>isinwheel</i></h2>
          </NuxtLink>
        </div>

        <div class="checkout-section user-section">
          <div class="user-info">
            <span class="step-number">1</span>
            <span class="user-email">1873363854@qq.com</span>
            <button class="more-btn"><MoreVerticalIcon class="icon" /></button>
          </div>
        </div>

        <div class="checkout-section address-section">
          <form class="address-form">
            <div class="form-group">
              <label>Country/Region</label>
              <div class="select-wrapper">
                <select v-model="form.country">
                  <option value="US">United States</option>
                </select>
                <ChevronDownIcon class="select-icon" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <input type="text" v-model="form.firstName" placeholder="First name" />
              </div>
              <div class="form-group">
                <input type="text" v-model="form.lastName" placeholder="Last name" />
              </div>
            </div>

            <div class="form-group">
              <div class="input-with-icon">
                <input type="text" v-model="form.address" placeholder="Address" />
                <SearchIcon class="search-icon" />
              </div>
            </div>

            <div class="form-group">
              <input type="text" v-model="form.apartment" placeholder="Apartment, suite, etc. (optional)" />
            </div>

            <div class="form-row three-cols">
              <div class="form-group">
                <input type="text" v-model="form.city" placeholder="City" />
              </div>
              <div class="form-group">
                <div class="select-wrapper">
                  <select v-model="form.state">
                    <option value="" disabled selected>State</option>
                    <option value="AL">Alabama</option>
                    <option value="NY">New York</option>
                    <option value="CA">California</option>
                  </select>
                  <ChevronDownIcon class="select-icon" />
                </div>
              </div>
              <div class="form-group">
                <input type="text" v-model="form.zipCode" placeholder="ZIP code" />
              </div>
            </div>

            <div class="form-group">
              <div class="input-with-icon">
                <input type="tel" v-model="form.phone" placeholder="Phone" />
                <HelpCircleIcon class="help-icon" />
              </div>
            </div>

            <div class="checkbox-group">
              <input type="checkbox" id="news" v-model="form.subscribe" />
              <label for="news">Text me with news and offers</label>
            </div>
          </form>
        </div>

        <div class="checkout-section shipping-method">
          <h2 class="section-title">Shipping method</h2>
          <div class="info-box">
            Enter your shipping address to view available shipping methods.
          </div>
        </div>

        <div class="checkout-section payment-section">
          <h2 class="section-title">Payment</h2>
          <p class="section-desc">All transactions are secure and encrypted.</p>

          <div class="payment-methods">
            <div class="payment-option selected">
              <div class="option-header">
                <div class="radio-wrap">
                  <div class="radio-inner"></div>
                </div>
                <span class="option-name">Credit card</span>
                <div class="card-icons">
                  <span class="card-icon visa">VISA</span>
                  <span class="card-icon master">MC</span>
                  <span class="card-icon amex">AMEX</span>
                  <span class="card-icon more">+5</span>
                </div>
              </div>
              <div class="option-body">
                <div class="card-form">
                  <div class="form-group">
                    <div class="input-with-icon">
                      <input type="text" placeholder="Card number" />
                      <LockIcon class="lock-icon" />
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-group">
                      <input type="text" placeholder="Expiration date (MM / YY)" />
                    </div>
                    <div class="form-group">
                      <div class="input-with-icon">
                        <input type="text" placeholder="Security code" />
                        <HelpCircleIcon class="help-icon" />
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <input type="text" placeholder="Name on card" />
                  </div>
                  <div class="checkbox-group">
                    <input type="checkbox" id="billing" checked />
                    <label for="billing">Use shipping address as billing address</label>
                  </div>
                </div>
              </div>
            </div>

            <div class="payment-option">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">Shop Pay <span class="sub-text">· Pay in full or in installments</span></span>
                <span class="brand-text shop">shop</span>
              </div>
            </div>

            <div class="payment-option">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">PayPal</span>
                <span class="brand-text paypal">PayPal</span>
              </div>
            </div>

            <div class="payment-option">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">Affirm - Pay Over Time</span>
                <span class="brand-text affirm">affirm</span>
              </div>
            </div>

            <div class="payment-option">
              <div class="option-header">
                <div class="radio-wrap"></div>
                <span class="option-name">Klarna</span>
                <span class="brand-text klarna">Klarna.</span>
              </div>
            </div>
          </div>

          <div class="save-info-section">
            <h3 class="sub-title">Save my information for a faster checkout</h3>
            <div class="form-group">
              <div class="phone-input">
                <SmartphoneIcon class="phone-icon" />
                <div class="prefix">Mobile phone (optional)<br/>+1</div>
                <input type="tel" />
              </div>
            </div>
            <p class="terms-text">
              By providing your phone number, you agree to create a Shop account subject to Shop's 
              <a href="#">Terms</a> and <a href="#">Privacy Policy</a>.
            </p>
          </div>

          <button class="pay-now-btn">Pay now</button>
        </div>
      </main>

      <!-- 右侧订单摘要 -->
      <aside class="checkout-sidebar">
        <div class="sidebar-inner">
          <div class="cart-items">
            <div class="cart-item">
              <div class="item-img-wrapper">
                <img src="https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=100" alt="Scooter" class="item-img" />
                <span class="item-qty">1</span>
              </div>
              <div class="item-info">
                <h4 class="item-title">isinwheel S9 Pro Pneumatic Tire Electric Scooter</h4>
                <p class="item-variant">2026 Upgraded Edition / S9 Pro*1</p>
              </div>
              <div class="item-price">$279.99</div>
            </div>
          </div>

          <div class="points-banner">
            <div class="points-icon"><GiftIcon class="icon" /></div>
            <div class="points-text">
              <strong>Complete this purchase to earn up to 839 Points</strong>
              <p>Use your Points to redeem a discount on your next order.</p>
            </div>
          </div>

          <div class="discount-section">
            <div class="discount-input">
              <input type="text" placeholder="Discount code" />
              <button class="apply-btn">Apply</button>
            </div>
          </div>

          <div class="summary-lines">
            <div class="line">
              <span class="label">Subtotal</span>
              <span class="value">$279.99</span>
            </div>
            <div class="line shipping-line">
              <span class="label">
                UPS Ground/FedEx Home Delivery(2-5 Business Days)
                <HelpCircleIcon class="help-icon" />
              </span>
              <span class="value placeholder">Enter shipping address</span>
            </div>
          </div>

          <div class="total-line">
            <span class="label">Total</span>
            <div class="value">
              <span class="currency">USD</span>
              <span class="amount">$279.99</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  MoreVerticalIcon, ChevronDownIcon, SearchIcon, 
  HelpCircleIcon, LockIcon, SmartphoneIcon, GiftIcon 
} from 'lucide-vue-next'

definePageMeta({
  layout: 'blank'
})

// Mock address data
const mockSavedAddress = {
  country: 'US',
  firstName: 'John',
  lastName: 'Doe',
  address: '123 Main St',
  apartment: 'Apt 4B',
  city: 'New York',
  state: 'NY',
  zipCode: '10001',
  phone: '1234567890',
  subscribe: true
}

const form = ref({
  country: 'US',
  firstName: '',
  lastName: '',
  address: '',
  apartment: '',
  city: '',
  state: '',
  zipCode: '',
  phone: '',
  subscribe: false
})

// Simulate fetching user data and auto-filling
onMounted(() => {
  const hasSavedAddress = true // Change to false to test empty state
  if (hasSavedAddress) {
    form.value = { ...mockSavedAddress }
  }
})
</script>

<style lang="scss" scoped>
.checkout-page {
  min-height: 100vh;
  background-color: #fff;
  font-family: 'Montserrat', sans-serif;
  color: #333;
}

.checkout-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
}

.checkout-main {
  flex: 1;
  padding: 40px 5%;
  border-right: 1px solid #e6e6e6;
  padding-right: 6%;

  .checkout-header {
    margin-bottom: 40px;
    .logo {
      text-decoration: none;
      color: #111;
      h2 {
        font-size: 28px;
        font-weight: 800;
        margin: 0;
      }
    }
  }

  .checkout-section {
    margin-bottom: 40px;

    .section-title {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #111;
    }
    
    .section-desc {
      font-size: 14px;
      color: #666;
      margin-bottom: 16px;
    }
  }
}

.user-section {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  
  &::before {
    content: 'OR';
    position: absolute;
    top: -20px;
    left: 50%;
    transform: translateX(-50%);
    background: #fff;
    padding: 0 10px;
    font-size: 12px;
    color: #999;
  }

  &::after {
    content: '';
    position: absolute;
    top: -10px;
    left: 0;
    right: 0;
    height: 1px;
    background: #e6e6e6;
    z-index: -1;
  }

  .user-info {
    width: 100%;
    display: flex;
    align-items: center;
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    padding: 12px 16px;
    background: #fff;

    .step-number {
      width: 24px;
      height: 24px;
      background: #f0f0f0;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 600;
      margin-right: 12px;
    }

    .user-email {
      font-size: 14px;
      font-weight: 500;
      flex: 1;
    }

    .more-btn {
      background: none;
      border: none;
      cursor: pointer;
      color: #666;
      padding: 4px;
      .icon { width: 20px; height: 20px; }
    }
  }
}

.address-form, .card-form {
  display: flex;
  flex-direction: column;
  gap: 16px;

  .form-group {
    position: relative;

    label {
      position: absolute;
      top: 6px;
      left: 12px;
      font-size: 11px;
      color: #666;
      z-index: 1;
    }

    input[type="text"],
    input[type="tel"],
    select {
      width: 100%;
      padding: 12px;
      border: 1px solid #d9d9d9;
      border-radius: 6px;
      font-size: 14px;
      background: #fff;
      transition: all 0.2s;
      outline: none;

      &:focus {
        border-color: #58cc02;
        box-shadow: 0 0 0 1px #58cc02;
      }
      
      &::placeholder {
        color: #999;
      }
    }

    select {
      appearance: none;
      padding-top: 24px;
      padding-bottom: 8px;
    }

    .select-wrapper {
      position: relative;
      .select-icon {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        color: #666;
        pointer-events: none;
      }
    }

    .input-with-icon {
      position: relative;
      .search-icon, .help-icon, .lock-icon {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        color: #999;
      }
    }
  }

  .form-row {
    display: flex;
    gap: 16px;
    .form-group { flex: 1; }

    &.three-cols {
      .form-group { flex: 1; }
    }
  }

  .checkbox-group {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 8px;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
      accent-color: #58cc02;
      cursor: pointer;
    }

    label {
      font-size: 14px;
      color: #333;
      cursor: pointer;
      position: static;
    }
  }
}

.shipping-method {
  .info-box {
    background: #f5f5f5;
    padding: 16px;
    border-radius: 6px;
    font-size: 14px;
    color: #666;
    text-align: center;
  }
}

.payment-section {
  .payment-methods {
    border: 1px solid #d9d9d9;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 24px;

    .payment-option {
      border-bottom: 1px solid #d9d9d9;
      
      &:last-child {
        border-bottom: none;
      }

      &.selected {
        background: #fafafa;
      }

      .option-header {
        display: flex;
        align-items: center;
        padding: 16px;
        cursor: pointer;

        .radio-wrap {
          width: 18px;
          height: 18px;
          border: 1px solid #d9d9d9;
          border-radius: 50%;
          margin-right: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #fff;
        }

        .option-name {
          font-size: 14px;
          font-weight: 500;
          flex: 1;
          
          .sub-text {
            font-size: 12px;
            color: #666;
            font-weight: 400;
          }
        }

        .card-icons {
          display: flex;
          gap: 4px;
          
          .card-icon {
            font-size: 10px;
            font-weight: bold;
            padding: 2px 6px;
            border-radius: 2px;
            border: 1px solid #eee;
            background: #fff;
            
            &.visa { color: #1a1f71; }
            &.master { color: #ff5f00; }
            &.amex { color: #002663; }
            &.more { color: #666; }
          }
        }

        .brand-text {
          font-weight: 800;
          font-size: 16px;
          
          &.shop { color: #5a31f4; }
          &.paypal { color: #003087; }
          &.affirm { color: #000; }
          &.klarna { color: #ffb3c7; }
        }
      }

      &.selected .radio-wrap {
        border-color: #58cc02;
        .radio-inner {
          width: 10px;
          height: 10px;
          background: #58cc02;
          border-radius: 50%;
        }
      }

      .option-body {
        padding: 0 16px 16px 16px;
        background: #fafafa;
        
        .card-form {
          margin-top: 8px;
        }
      }
    }
  }

  .save-info-section {
    margin-bottom: 24px;
    
    .sub-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 16px;
    }

    .phone-input {
      display: flex;
      align-items: center;
      border: 1px solid #d9d9d9;
      border-radius: 6px;
      padding: 8px 12px;
      gap: 12px;

      .phone-icon {
        width: 24px;
        height: 24px;
        color: #666;
      }

      .prefix {
        font-size: 11px;
        color: #666;
        line-height: 1.2;
      }

      input {
        flex: 1;
        border: none;
        outline: none;
        font-size: 14px;
      }
    }

    .terms-text {
      font-size: 12px;
      color: #666;
      margin-top: 12px;
      line-height: 1.5;

      a {
        color: #111;
        text-decoration: underline;
      }
    }
  }

  .pay-now-btn {
    width: 100%;
    padding: 16px;
    background: #58cc02;
    color: #fff;
    border: none;
    border-radius: 6px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #46a302;
    }
  }
}

/* 右侧边栏 */
.checkout-sidebar {
  flex: 0 0 45%;
  background: #fafafa;
  border-left: 1px solid #e6e6e6;
  padding: 40px 5%;
  
  .sidebar-inner {
    position: sticky;
    top: 40px;
  }

  .cart-items {
    margin-bottom: 24px;
    
    .cart-item {
      display: flex;
      align-items: center;
      gap: 16px;

      .item-img-wrapper {
        position: relative;
        width: 64px;
        height: 64px;
        background: #fff;
        border: 1px solid #e6e6e6;
        border-radius: 8px;
        padding: 4px;

        .item-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .item-qty {
          position: absolute;
          top: -8px;
          right: -8px;
          width: 20px;
          height: 20px;
          background: rgba(114, 114, 114, 0.9);
          color: #fff;
          font-size: 12px;
          font-weight: 600;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
        }
      }

      .item-info {
        flex: 1;

        .item-title {
          font-size: 14px;
          font-weight: 600;
          margin-bottom: 4px;
          color: #333;
        }

        .item-variant {
          font-size: 12px;
          color: #666;
        }
      }

      .item-price {
        font-size: 14px;
        font-weight: 500;
        color: #333;
      }
    }
  }

  .points-banner {
    display: flex;
    gap: 12px;
    background: #f0f0f0;
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 24px;

    .points-icon {
      .icon {
        width: 20px;
        height: 20px;
        color: #555;
      }
    }

    .points-text {
      font-size: 13px;
      color: #333;
      
      strong {
        display: block;
        margin-bottom: 4px;
      }
      
      p {
        margin: 0;
        color: #666;
      }
    }
  }

  .discount-section {
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #e6e6e6;

    .discount-input {
      display: flex;
      gap: 12px;

      input {
        flex: 1;
        padding: 12px 16px;
        border: 1px solid #d9d9d9;
        border-radius: 6px;
        font-size: 14px;
        outline: none;

        &:focus {
          border-color: #58cc02;
        }
      }

      .apply-btn {
        padding: 0 24px;
        background: #f0f0f0;
        border: 1px solid #d9d9d9;
        border-radius: 6px;
        font-size: 14px;
        font-weight: 600;
        color: #999;
        cursor: not-allowed;

        &:hover {
          background: #e6e6e6;
        }
      }
    }
  }

  .summary-lines {
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #e6e6e6;

    .line {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: 14px;

      .label {
        color: #333;
        display: flex;
        align-items: center;
        gap: 4px;

        .help-icon {
          width: 14px;
          height: 14px;
          color: #999;
        }
      }

      .value {
        font-weight: 500;
        
        &.placeholder {
          font-size: 12px;
          color: #666;
          font-weight: 400;
          text-align: right;
          max-width: 150px;
        }
      }
    }
  }

  .total-line {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .label {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .value {
      display: flex;
      align-items: baseline;
      gap: 8px;

      .currency {
        font-size: 12px;
        color: #666;
      }

      .amount {
        font-size: 24px;
        font-weight: 700;
        color: #111;
      }
    }
  }
}

@media (max-width: 992px) {
  .checkout-container {
    flex-direction: column-reverse;
  }

  .checkout-main {
    border-right: none;
    padding: 40px 5%;
  }

  .checkout-sidebar {
    border-left: none;
    border-bottom: 1px solid #e6e6e6;
    padding: 40px 5%;
  }
}

@media (max-width: 640px) {
  .form-row, .form-row.three-cols {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
