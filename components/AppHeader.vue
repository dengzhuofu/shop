<template>
  <header class="app-header" :class="{ 'is-scrolled': isScrolled }">
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
            <li class="nav-item" :class="{ 'is-active': activeMenu === 'scooter' }" @mouseenter="handleMouseEnter('scooter')" @mouseleave="handleMouseLeave">
              <NuxtLink to="/collections/electric-scooter">Electric Scooter</NuxtLink>
            </li>
            <li class="nav-item hot">
              <NuxtLink to="/collections/spring-sale">Spring Sale</NuxtLink>
              <span class="badge">HOT</span>
            </li>
            <li class="nav-item" :class="{ 'is-active': activeMenu === 'skateboard' }" @mouseenter="handleMouseEnter('skateboard')" @mouseleave="handleMouseLeave">
              <NuxtLink to="/collections/electric-skateboard">Electric Skateboard</NuxtLink>
            </li>
            <li class="nav-item" :class="{ 'is-active': activeMenu === 'bike' }" @mouseenter="handleMouseEnter('bike')" @mouseleave="handleMouseLeave">
              <NuxtLink to="/collections/electric-bike">Electric Bike</NuxtLink>
            </li>
            <li class="nav-item" :class="{ 'is-active': activeMenu === 'accessories' }" @mouseenter="handleMouseEnter('accessories')" @mouseleave="handleMouseLeave">
              <NuxtLink to="/collections/accessories">Accessories</NuxtLink>
            </li>
            <li class="nav-item">
              <NuxtLink to="/pages/support">Support</NuxtLink>
              <NavDropdown :links="supportLinks" />
            </li>
          </ul>
        </nav>

        <!-- 右侧操作区 -->
        <div class="actions">
          <div class="country-pill">
            <span>🇺🇸 US</span>
          </div>
          <SearchIcon class="icon" />
          <UserIcon class="icon" />
          <div class="cart-icon" @click="openCartSidebar">
            <ShoppingCartIcon class="icon" />
            <span class="cart-count">3</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Mega Menus -->
    <div class="mega-menus-container" @mouseenter="hoverMenuArea = true" @mouseleave="hoverMenuArea = false">
      <transition name="fade">
        <NavMegaMenu 
          v-show="activeMenu === 'scooter'" 
          :menuData="megaMenuData.scooter" 
        />
      </transition>
      <transition name="fade">
        <NavMegaMenu 
          v-show="activeMenu === 'skateboard'" 
          :menuData="megaMenuData.skateboard" 
        />
      </transition>
      <transition name="fade">
        <NavMegaMenu 
          v-show="activeMenu === 'bike'" 
          :menuData="megaMenuData.bike" 
        />
      </transition>
      <transition name="fade">
        <NavMegaMenu 
          v-show="activeMenu === 'accessories'" 
          :menuData="megaMenuData.accessories" 
        />
      </transition>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, inject } from 'vue'
import { SearchIcon, UserIcon, ShoppingCartIcon } from 'lucide-vue-next'
import NavMegaMenu from '~/components/NavMegaMenu.vue'
import NavDropdown from '~/components/NavDropdown.vue'

const openCartSidebar = inject('openCartSidebar', () => {
  console.warn('openCartSidebar not provided')
})

const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 0
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const hoverMenu = ref(null)
const hoverMenuArea = ref(false)

// 添加防抖防止闪烁
let hoverTimer = null

const handleMouseEnter = (menu) => {
  clearTimeout(hoverTimer)
  hoverMenu.value = menu
}

const handleMouseLeave = () => {
  hoverTimer = setTimeout(() => {
    hoverMenu.value = null
  }, 100)
}

const activeMenu = computed(() => {
  return hoverMenu.value || (hoverMenuArea.value ? activeMenuCache.value : null)
})

const activeMenuCache = ref(null)

watch(hoverMenu, (newVal) => {
  if (newVal) {
    activeMenuCache.value = newVal
  }
})

// 模拟 MegaMenu 数据
const megaMenuData = {
  scooter: {
    banner: {
      tag: '$20 OFF',
      trustpilot: '4.4',
      linkText: '10% Combo Sale',
      linkUrl: '#'
    },
    categories: [
      {
        id: 'commuter',
        name: 'Commuter Scooter',
        allLinkText: 'All Commuter Scooter (7)',
        allLinkUrl: '#',
        products: [
          { 
            id: 1, 
            title: 'isinwheel S9 Pro Pneumatic Tire Electric Scooter', 
            price: '269.99', 
            compareAtPrice: '399.99', 
            isFrom: true, 
            images: [
              'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300',
              'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300'
            ],
            appImage: 'https://via.placeholder.com/60x120?text=APP',
            tags: ['Spring Sale'] 
          },
          { 
            id: 2, 
            title: 'S Nova Commuting Electric Scooter', 
            price: '349.99', 
            compareAtPrice: '499.99', 
            isFrom: true, 
            images: [
              'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300',
              'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300'
            ],
            appImage: 'https://via.placeholder.com/60x120?text=APP',
            tags: ['NEW', 'Spring Sale'] 
          },
          { 
            id: 3, 
            title: 'S Nova Pro Commuting Electric Scooter', 
            price: '489.99', 
            compareAtPrice: '599.99', 
            isFrom: false, 
            images: [
              'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300'
            ],
            appImage: 'https://via.placeholder.com/60x120?text=APP',
            tags: ['NEW', 'Spring Sale'] 
          },
          { 
            id: 4, 
            title: 'isinwheel S10Max 1000W High-End Commuting Electric Scooter', 
            price: '619.99', 
            compareAtPrice: '899.99', 
            isFrom: false, 
            images: [
              'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300',
              'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300',
              'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300'
            ],
            appImage: 'https://via.placeholder.com/60x120?text=APP',
            tags: ['HOT', 'Spring Sale'] 
          }
        ]
      },
      {
        id: 'offroad',
        name: 'Off Road Scooter',
        allLinkText: 'All Off Road Scooter (5)',
        allLinkUrl: '#',
        products: [
          { 
            id: 5, 
            title: 'GT2 Off Road Scooter', 
            price: '899.99', 
            compareAtPrice: '1099.99', 
            isFrom: false, 
            images: ['https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300'],
            tags: ['HOT'] 
          },
        ]
      },
      {
        id: 'kids',
        name: 'Scooter for Kids',
        allLinkText: 'All Kids Scooter (3)',
        allLinkUrl: '#',
        products: []
      }
    ]
  },
  skateboard: {
    banner: {
      tag: '$20 OFF',
      trustpilot: '4.4',
      linkText: 'All E-skateboards',
      linkUrl: '#'
    },
    categories: [
      {
        id: 'eskate',
        name: 'Electric Skateboard',
        allLinkText: 'All Electric Skateboard (5)',
        allLinkUrl: '#',
        products: [
          { id: 1, title: 'isinwheel V8 Electric Skateboard wit...', price: '399.99', compareAtPrice: '599.99', isFrom: false, images: ['https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] },
          { id: 2, title: 'isinwheel V10 Off Road Electric...', price: '649.99', compareAtPrice: '1099.99', isFrom: false, images: ['https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] },
          { id: 3, title: 'isinwheel V6 Electric Skateboard wit...', price: '219.99', compareAtPrice: '399.99', isFrom: true, images: ['https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'], soldOut: true },
          { id: 4, title: 'isinwheel V6 Pro Electric Skateboar...', price: '179.99', compareAtPrice: '399.99', isFrom: true, images: ['https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'], soldOut: true }
        ]
      }
    ]
  },
  bike: {
    banner: {
      tag: '$20 OFF',
      trustpilot: '4.4',
      linkText: 'All Ebikes',
      linkUrl: '#'
    },
    categories: [
      {
        id: 'commuter-bike',
        name: 'Commuter & City Road',
        allLinkText: 'All Commuter & City Road (10)',
        allLinkUrl: '#',
        products: [
          { id: 1, title: 'isinwheel U5 500W Folding Electric...', price: '559.99', compareAtPrice: '699.99', isFrom: true, images: ['https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'], soldOut: true },
          { id: 2, title: 'isinwheel M50 Mountain Ebike', price: '589.99', compareAtPrice: '799.99', isFrom: true, images: ['https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] },
          { id: 3, title: 'isinwheel U7 Cargo Bike', price: '589.99', compareAtPrice: '799.99', isFrom: true, images: ['https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] },
          { id: 4, title: 'isinwheel U8 Electric Bike for Adults', price: '609.99', compareAtPrice: '799.99', isFrom: true, images: ['https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] }
        ]
      },
      {
        id: 'offroad-bike',
        name: 'Off Road & All Terrain',
        allLinkText: 'All Off Road & All Terrain (6)',
        allLinkUrl: '#',
        products: []
      }
    ]
  },
  accessories: {
    banner: {
      tag: '$20 OFF',
      trustpilot: '4.4',
      linkText: 'All Products',
      linkUrl: '#'
    },
    categories: [
      { id: 'universal', name: 'Universal Accessories', allLinkText: 'All Universal Accessories (12)', allLinkUrl: '#', products: [] },
      { id: 'scooter-acc', name: 'E-Scooter Accessories', allLinkText: 'All E-Scooter Accessories (24)', allLinkUrl: '#', products: [] },
      { id: 'bike-acc', name: 'E-Bike Accessories', allLinkText: 'All E-Bike Accessories (15)', allLinkUrl: '#', products: [] },
      {
        id: 'skate-acc',
        name: 'E-Skateboard Accessories',
        allLinkText: 'All E-Skateboard Accessories (18)',
        allLinkUrl: '#',
        products: [
          { id: 1, title: 'Electric Skateboard Battery Pack for...', price: '124.99', compareAtPrice: '129.99', isFrom: false, images: ['https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'], soldOut: true },
          { id: 2, title: 'V8 Remote Control', price: '38.99', compareAtPrice: '45.99', isFrom: false, images: ['https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] },
          { id: 3, title: 'isinwheel V8 Charger for Skateboard', price: '35.99', compareAtPrice: '39.99', isFrom: false, images: ['https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'] },
          { id: 4, title: '105mm Urban All Terrain Electric...', price: '29.99', compareAtPrice: '69.99', isFrom: true, images: ['https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300'], tags: ['Spring Sale'], soldOut: true }
        ]
      }
    ]
  }
}

// Support 下拉菜单数据
const supportLinks = [
  { title: 'About Us', url: '#' },
  { title: 'Contact Us', url: '#' },
  { title: 'FAQ', url: '#' },
  { title: 'Photos', url: '#' },
  { title: 'Blog', url: '#' },
  { title: 'Video Labs', url: '#' },
  { title: 'Track Your Order', url: '#' },
  { title: 'Become A Dealer', url: '#' },
  { title: 'Financing', url: '#' },
  { title: 'Exclusive Discount', url: '#' }
]
</script>

<style lang="scss" scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: $white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: transform 0.3s ease;

  .top-bar {
    background-color: #58cc02; // 根据原图绿色
    color: $white;
    font-size: 12px;
    padding: 8px 0;
    max-height: 50px;
    overflow: hidden;
    transition: max-height 0.3s ease, padding 0.3s ease, opacity 0.3s ease;

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

  &.is-scrolled {
    .top-bar {
      max-height: 0;
      padding: 0;
      opacity: 0;
    }
  }

  .main-nav {
    padding: 16px 0;
    position: static; // 改为 static 方便 mega-menu 定位

    .nav-content {
      position: static;
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
      position: static;
      .nav-list {
        position: static;
        display: flex;
        gap: 32px;
        align-items: center;

        .nav-item {
          font-weight: 600;
          font-size: 15px;
          position: relative;
          cursor: pointer;
          padding: 20px 0; // 扩大 hover 热区
          margin: -20px 0; // 抵消 padding 影响布局
          
          a {
            padding: 8px 16px;
            border-radius: 20px;
            transition: all 0.3s ease;
            color: $text-color;
            text-decoration: none;
            display: inline-block;
          }

          &:hover, &.is-active {
            a {
              background-color: #111;
              color: $white;
            }
          }

          &.hot {
            .badge {
              position: absolute;
              top: 0px;
              right: -10px;
              background-color: $danger-color;
              color: $white;
              font-size: 10px;
              padding: 2px 6px;
              border-radius: 10px;
              font-style: italic;
              z-index: 2;
              pointer-events: none;
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
        cursor: pointer;
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

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
