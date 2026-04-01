<template>
  <header class="app-header" :class="{ 'is-scrolled': isScrolled }">
    <!-- 顶部公告栏 -->
    <div class="top-bar">
      <div class="container top-bar-content">
        <div class="offer-copy">
          <p class="offer-kicker">Limited Time Offer!</p>
          <p class="offer-title">Don't miss out</p>
        </div>

        <div class="countdown-panel">
          <div class="countdown">
            <template v-for="(item, index) in countdownItems" :key="item.label">
              <span class="time-block">{{ item.value }}</span>
              <span
                v-if="index < countdownItems.length - 1"
                class="time-separator"
                >:</span
              >
            </template>
          </div>
          <div class="countdown-labels">
            <span v-for="item in countdownItems" :key="`${item.label}-label`">
              {{ item.label }}
            </span>
          </div>
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
            <li
              class="nav-item"
              :class="{ 'is-active': activeMenu === 'scooter' }"
              @mouseenter="handleMouseEnter('scooter')"
              @mouseleave="handleMouseLeave"
            >
              <NuxtLink to="/collections/electric-scooters"
                >Electric Scooter</NuxtLink
              >
            </li>
            <li class="nav-item hot">
              <NuxtLink to="/collections/spring-sale">Spring Sale</NuxtLink>
              <span class="badge">HOT</span>
            </li>
            <li
              class="nav-item"
              :class="{ 'is-active': activeMenu === 'skateboard' }"
              @mouseenter="handleMouseEnter('skateboard')"
              @mouseleave="handleMouseLeave"
            >
              <NuxtLink to="/collections/electric-skateboard"
                >Electric Skateboard</NuxtLink
              >
            </li>
            <li
              class="nav-item"
              :class="{ 'is-active': activeMenu === 'bike' }"
              @mouseenter="handleMouseEnter('bike')"
              @mouseleave="handleMouseLeave"
            >
              <NuxtLink to="/collections/electric-bike">Electric Bike</NuxtLink>
            </li>
            <li
              class="nav-item"
              :class="{ 'is-active': activeMenu === 'accessories' }"
              @mouseenter="handleMouseEnter('accessories')"
              @mouseleave="handleMouseLeave"
            >
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
          <div
            class="language-switch"
            role="group"
            aria-label="Language switch"
          >
            <button
              type="button"
              class="lang-btn"
              :class="{ active: currentLanguage === 'zh' }"
              @click="setLanguage('zh')"
            >
              中
            </button>
            <button
              type="button"
              class="lang-btn"
              :class="{ active: currentLanguage === 'en' }"
              @click="setLanguage('en')"
            >
              EN
            </button>
          </div>
          <SearchIcon class="icon" />

          <!-- 用户图标与下拉菜单 -->
          <div
            class="user-menu-wrapper"
            @mouseenter="userMenuOpen = true"
            @mouseleave="userMenuOpen = false"
          >
            <UserIcon class="icon" @click="handleUserClick" />
            <div class="user-dropdown" v-show="userMenuOpen">
              <template v-if="isLoggedIn">
                <NuxtLink to="/account/profile" class="dropdown-item">Profile</NuxtLink>
                <NuxtLink to="/account/orders" class="dropdown-item">My Orders</NuxtLink>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item text-danger" @click="handleLogout">
                  Logout
                </button>
              </template>
              <template v-else>
                <NuxtLink to="/login" class="dropdown-item">Login</NuxtLink>
                <NuxtLink to="/register" class="dropdown-item"
                  >Register</NuxtLink
                >
              </template>
            </div>
          </div>

          <div class="cart-icon" @click="openCartSidebar">
            <ShoppingCartIcon class="icon" />
            <span class="cart-count">3</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Mega Menus -->
    <div
      class="mega-menus-container"
      @mouseenter="hoverMenuArea = true"
      @mouseleave="hoverMenuArea = false"
    >
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
import { ref, computed, watch, onMounted, onUnmounted, inject } from "vue";
import { useRouter, useCookie } from "#app";
import { SearchIcon, UserIcon, ShoppingCartIcon } from "lucide-vue-next";
import NavMegaMenu from "~/components/NavMegaMenu.vue";
import NavDropdown from "~/components/NavDropdown.vue";
import { useHttp } from "~/composables/useHttp";

const router = useRouter();
const tokenCookie = useCookie("token");

const isLoggedIn = computed(() => !!tokenCookie.value);
const userMenuOpen = ref(false);
const currentLanguage = ref("en");

const setLanguage = (lang) => {
  currentLanguage.value = lang;
};

const handleUserClick = () => {
  if (isLoggedIn.value) {
    router.push('/account/profile')
  } else {
    router.push("/login");
  }
};

const handleLogout = async () => {
  try {
    await useHttp("/api/auth/logout", { method: "POST" });
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    tokenCookie.value = null;
    userMenuOpen.value = false;
    router.push("/login");
  }
};

const openCartSidebar = inject("openCartSidebar", () => {
  console.warn("openCartSidebar not provided");
});

const isScrolled = ref(false);
const countdownTarget = ref(0);
const countdownMs = ref(24 * 60 * 60 * 1000);
let countdownTimer = null;

const handleScroll = () => {
  isScrolled.value = window.scrollY > 0;
};

const formatCountdownValue = (value) => String(value).padStart(2, "0");

const updateCountdown = () => {
  const remaining = Math.max(countdownTarget.value - Date.now(), 0);
  countdownMs.value = remaining;
};

const countdownParts = computed(() => {
  const totalSeconds = Math.floor(countdownMs.value / 1000);
  const days = Math.floor(totalSeconds / (24 * 60 * 60));
  const hours = Math.floor((totalSeconds % (24 * 60 * 60)) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  return {
    days,
    hours,
    minutes,
    seconds,
  };
});

const countdownItems = computed(() => [
  { label: "Days", value: formatCountdownValue(countdownParts.value.days) },
  { label: "Hours", value: formatCountdownValue(countdownParts.value.hours) },
  { label: "Mins", value: formatCountdownValue(countdownParts.value.minutes) },
  { label: "Secs", value: formatCountdownValue(countdownParts.value.seconds) },
]);

onMounted(() => {
  countdownTarget.value = Date.now() + 24 * 60 * 60 * 1000;
  updateCountdown();
  countdownTimer = window.setInterval(updateCountdown, 1000);
  handleScroll();
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  if (countdownTimer) {
    window.clearInterval(countdownTimer);
  }
  window.removeEventListener("scroll", handleScroll);
});

const hoverMenu = ref(null);
const hoverMenuArea = ref(false);

// 添加防抖防止闪烁
let hoverTimer = null;

const handleMouseEnter = (menu) => {
  clearTimeout(hoverTimer);
  hoverMenu.value = menu;
};

const handleMouseLeave = () => {
  hoverTimer = setTimeout(() => {
    hoverMenu.value = null;
  }, 100);
};

const activeMenu = computed(() => {
  return (
    hoverMenu.value || (hoverMenuArea.value ? activeMenuCache.value : null)
  );
});

const activeMenuCache = ref(null);

watch(hoverMenu, (newVal) => {
  if (newVal) {
    activeMenuCache.value = newVal;
  }
});

// 模拟 MegaMenu 数据
const megaMenuData = {
  scooter: {
    banner: {
      tag: "$20 OFF",
      trustpilot: "4.4",
      linkText: "10% Combo Sale",
      linkUrl: "#",
    },
    categories: [
      {
        id: "commuter",
        name: "Commuter Scooter",
        allLinkText: "All Commuter Scooter (7)",
        allLinkUrl: "#",
        products: [
          {
            id: 1,
            title: "isinwheel S9 Pro Pneumatic Tire Electric Scooter",
            price: "269.99",
            compareAtPrice: "399.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300",
              "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300",
            ],
            appImage: "https://via.placeholder.com/60x120?text=APP",
            tags: ["Spring Sale"],
          },
          {
            id: 2,
            title: "S Nova Commuting Electric Scooter",
            price: "349.99",
            compareAtPrice: "499.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300",
              "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300",
            ],
            appImage: "https://via.placeholder.com/60x120?text=APP",
            tags: ["NEW", "Spring Sale"],
          },
          {
            id: 3,
            title: "S Nova Pro Commuting Electric Scooter",
            price: "489.99",
            compareAtPrice: "599.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300",
            ],
            appImage: "https://via.placeholder.com/60x120?text=APP",
            tags: ["NEW", "Spring Sale"],
          },
          {
            id: 4,
            title: "isinwheel S10Max 1000W High-End Commuting Electric Scooter",
            price: "619.99",
            compareAtPrice: "899.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300",
              "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300",
              "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300",
            ],
            appImage: "https://via.placeholder.com/60x120?text=APP",
            tags: ["HOT", "Spring Sale"],
          },
        ],
      },
      {
        id: "offroad",
        name: "Off Road Scooter",
        allLinkText: "All Off Road Scooter (5)",
        allLinkUrl: "#",
        products: [
          {
            id: 5,
            title: "GT2 Off Road Scooter",
            price: "899.99",
            compareAtPrice: "1099.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["HOT"],
          },
        ],
      },
      {
        id: "kids",
        name: "Scooter for Kids",
        allLinkText: "All Kids Scooter (3)",
        allLinkUrl: "#",
        products: [],
      },
    ],
  },
  skateboard: {
    banner: {
      tag: "$20 OFF",
      trustpilot: "4.4",
      linkText: "All E-skateboards",
      linkUrl: "#",
    },
    categories: [
      {
        id: "eskate",
        name: "Electric Skateboard",
        allLinkText: "All Electric Skateboard (5)",
        allLinkUrl: "#",
        products: [
          {
            id: 1,
            title: "isinwheel V8 Electric Skateboard wit...",
            price: "399.99",
            compareAtPrice: "599.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
          {
            id: 2,
            title: "isinwheel V10 Off Road Electric...",
            price: "649.99",
            compareAtPrice: "1099.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
          {
            id: 3,
            title: "isinwheel V6 Electric Skateboard wit...",
            price: "219.99",
            compareAtPrice: "399.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
            soldOut: true,
          },
          {
            id: 4,
            title: "isinwheel V6 Pro Electric Skateboar...",
            price: "179.99",
            compareAtPrice: "399.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
            soldOut: true,
          },
        ],
      },
    ],
  },
  bike: {
    banner: {
      tag: "$20 OFF",
      trustpilot: "4.4",
      linkText: "All Ebikes",
      linkUrl: "#",
    },
    categories: [
      {
        id: "commuter-bike",
        name: "Commuter & City Road",
        allLinkText: "All Commuter & City Road (10)",
        allLinkUrl: "#",
        products: [
          {
            id: 1,
            title: "isinwheel U5 500W Folding Electric...",
            price: "559.99",
            compareAtPrice: "699.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
            soldOut: true,
          },
          {
            id: 2,
            title: "isinwheel M50 Mountain Ebike",
            price: "589.99",
            compareAtPrice: "799.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
          {
            id: 3,
            title: "isinwheel U7 Cargo Bike",
            price: "589.99",
            compareAtPrice: "799.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
          {
            id: 4,
            title: "isinwheel U8 Electric Bike for Adults",
            price: "609.99",
            compareAtPrice: "799.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
        ],
      },
      {
        id: "offroad-bike",
        name: "Off Road & All Terrain",
        allLinkText: "All Off Road & All Terrain (6)",
        allLinkUrl: "#",
        products: [],
      },
    ],
  },
  accessories: {
    banner: {
      tag: "$20 OFF",
      trustpilot: "4.4",
      linkText: "All Products",
      linkUrl: "#",
    },
    categories: [
      {
        id: "universal",
        name: "Universal Accessories",
        allLinkText: "All Universal Accessories (12)",
        allLinkUrl: "#",
        products: [],
      },
      {
        id: "scooter-acc",
        name: "E-Scooter Accessories",
        allLinkText: "All E-Scooter Accessories (24)",
        allLinkUrl: "#",
        products: [],
      },
      {
        id: "bike-acc",
        name: "E-Bike Accessories",
        allLinkText: "All E-Bike Accessories (15)",
        allLinkUrl: "#",
        products: [],
      },
      {
        id: "skate-acc",
        name: "E-Skateboard Accessories",
        allLinkText: "All E-Skateboard Accessories (18)",
        allLinkUrl: "#",
        products: [
          {
            id: 1,
            title: "Electric Skateboard Battery Pack for...",
            price: "124.99",
            compareAtPrice: "129.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
            soldOut: true,
          },
          {
            id: 2,
            title: "V8 Remote Control",
            price: "38.99",
            compareAtPrice: "45.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
          {
            id: 3,
            title: "isinwheel V8 Charger for Skateboard",
            price: "35.99",
            compareAtPrice: "39.99",
            isFrom: false,
            images: [
              "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
          },
          {
            id: 4,
            title: "105mm Urban All Terrain Electric...",
            price: "29.99",
            compareAtPrice: "69.99",
            isFrom: true,
            images: [
              "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=300",
            ],
            tags: ["Spring Sale"],
            soldOut: true,
          },
        ],
      },
    ],
  },
};

// Support 下拉菜单数据
const supportLinks = [
  { title: "About Us", url: "#" },
  { title: "Contact Us", url: "#" },
  { title: "FAQ", url: "#" },
  { title: "Photos", url: "#" },
  { title: "Blog", url: "#" },
  { title: "Video Labs", url: "#" },
  { title: "Track Your Order", url: "#" },
  { title: "Become A Dealer", url: "#" },
  { title: "Financing", url: "#" },
  { title: "Exclusive Discount", url: "#" },
];
</script>

<style lang="scss" scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: $white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;

  .top-bar {
    background-image: url("https://www.isinwheel.com/cdn/shop/files/4_fa32ee9a-10f9-4743-8a0a-f0c74bc54f07.png?v=1775033994");
    background-size: 100% 100%;
    background-position: center;
    background-repeat: no-repeat;
    color: #111;
    font-size: 12px;
    padding: 4px 0;
    max-height: 116px;
    overflow: hidden;
    transition:
      max-height 0.3s ease,
      padding 0.3s ease,
      opacity 0.3s ease;

    .top-bar-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      min-height: 86px;
      gap: 16px;

      .offer-copy {
        min-width: 260px;

        .offer-kicker {
          margin: 0;
          font-size: 12px;
          line-height: 1.2;
          font-weight: 700;
          letter-spacing: 0.01em;
        }

        .offer-title {
          margin: 2px 0 0;
          font-size: 30px;
          line-height: 1.02;
          letter-spacing: 0.01em;
          font-weight: 700;
          color: #090f1f;
        }
      }

      .countdown-panel {
        min-width: 300px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 12px;

        .countdown {
          display: flex;
          align-items: center;
          gap: 10px;
          background: transparent;
          padding: 0;

          .time-block {
            min-width: 50px;
            height: 50px;
            display: grid;
            place-items: center;
            border-radius: 10px;
            background: #8ad5d0;
            color: #39750a;
            font-size: 26px;
            line-height: 1;
            font-weight: 700;
          }

          .time-separator {
            font-size: 22px;
            font-weight: 700;
            line-height: 1;
            color: #37760a;
          }
        }

        .countdown-labels {
          width: 100%;
          display: grid;
          grid-template-columns: repeat(4, minmax(0, 1fr));
          gap: 12px;

          span {
            font-size: 11px;
            line-height: 1.2;
            text-align: center;
            color: #4f7e0b;
          }
        }
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
      margin-right: 16px;
      position: relative;
      z-index: 2;

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
        flex-wrap: nowrap;
        gap: 32px;
        align-items: center;

        .nav-item {
          font-weight: 600;
          font-size: 15px;
          position: relative;
          cursor: pointer;
          padding: 20px 0; // 扩大 hover 热区
          margin: -20px 0; // 抵消 padding 影响布局
          white-space: nowrap;

          a {
            padding: 8px 16px;
            border-radius: 20px;
            transition: all 0.3s ease;
            color: $text-color;
            text-decoration: none;
            display: inline-block;
            white-space: nowrap;
          }

          &:hover,
          &.is-active {
            a {
              background-color: #111;
              color: $white;
            }
          }

          &.hot {
            .badge {
              position: absolute;
              top: 15px;
              right: -20px;
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

      .language-switch {
        display: inline-flex;
        align-items: center;
        border: 1px solid $border-color;
        border-radius: 999px;
        overflow: hidden;

        .lang-btn {
          border: none;
          background: transparent;
          color: $text-color;
          font-size: 13px;
          font-weight: 700;
          line-height: 1;
          padding: 8px 10px;
          min-width: 40px;
          cursor: pointer;
          transition: all 0.2s ease;

          &.active {
            background: #111;
            color: $white;
          }
        }
      }

      .icon {
        width: 20px;
        height: 20px;
        cursor: pointer;
        &:hover {
          color: #58cc02;
        }
      }

      .user-menu-wrapper {
        position: relative;
        display: flex;
        align-items: center;
        height: 100%;

        .user-dropdown {
          position: absolute;
          top: 30px;
          right: -10px;
          background: #fff;
          border-radius: 8px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
          min-width: 150px;
          padding: 8px 0;
          z-index: 100;

          .dropdown-item {
            display: block;
            padding: 10px 20px;
            color: #333;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            width: 100%;
            text-align: left;
            border: none;
            background: none;

            &:hover {
              background-color: #f5f5f5;
              color: #58cc02;
            }

            &.text-danger {
              color: #e62332;
              &:hover {
                background-color: #ffebee;
              }
            }
          }

          .dropdown-divider {
            height: 1px;
            background-color: #eee;
            margin: 4px 0;
          }
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

@media (max-width: 1024px) {
  .app-header {
    .top-bar {
      max-height: 176px;
      padding: 8px 0;

      .top-bar-content {
        min-height: auto;
        justify-content: center;
        flex-wrap: wrap;
        gap: 18px;

        .offer-copy {
          width: 100%;
          text-align: center;

          .offer-kicker {
            font-size: 14px;
          }

          .offer-title {
            font-size: 28px;
          }
        }

        .countdown-panel {
          min-width: 0;
          width: 100%;
          max-width: 540px;
        }
      }
    }
  }
}

@media (max-width: 640px) {
  .app-header {
    .top-bar {
      padding: 8px 0;
      max-height: 220px;

      .top-bar-content {
        gap: 12px;

        .offer-copy {
          .offer-kicker {
            font-size: 12px;
          }

          .offer-title {
            margin-top: 6px;
            font-size: 20px;
          }
        }
        .countdown-panel {
          width: 100%;
          min-width: 0;
          gap: 10px;

          .countdown {
            gap: 10px;

            .time-block {
              min-width: 34px;
              height: 34px;
              font-size: 16px;
            }

            .time-separator {
              font-size: 14px;
            }
          }

          .countdown-labels {
            gap: 10px;

            span {
              font-size: 10px;
            }
          }
        }
      }
    }
  }
}
// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
