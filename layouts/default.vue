<template>
  <div class="layout-default">
    <!-- 全局悬浮打折标签 -->
    <div class="global-discount-tag">
      $20 OFF
    </div>

    <AppHeader />
    <main class="main-content">
      <slot />
    </main>
    <AppFooter />

    <!-- 全局弹窗挂载点 -->
    <QuickViewModal 
      :isOpen="isQuickViewOpen" 
      :product="quickViewProduct" 
      @close="closeQuickView" 
    />

    <!-- 全局购物车侧边栏 -->
    <CartSidebar 
      :isOpen="isCartSidebarOpen"
      @close="closeCartSidebar"
    />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import QuickViewModal from '~/components/QuickViewModal.vue'
import CartSidebar from '~/components/CartSidebar.vue'

// 全局状态控制弹窗
const isQuickViewOpen = ref(false)
const quickViewProduct = ref({})

// 全局状态控制购物车侧边栏
const isCartSidebarOpen = ref(false)

const openQuickView = (product) => {
  quickViewProduct.value = product
  isQuickViewOpen.value = true
}

const closeQuickView = () => {
  isQuickViewOpen.value = false
  // 延迟清空数据，等动画结束
  setTimeout(() => {
    quickViewProduct.value = {}
  }, 300)
}

const openCartSidebar = () => {
  isCartSidebarOpen.value = true
}

const closeCartSidebar = () => {
  isCartSidebarOpen.value = false
}

// 提供给子组件调用
provide('openQuickView', openQuickView)
provide('openCartSidebar', openCartSidebar)
</script>

<style lang="scss" scoped>
.layout-default {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;

  .global-discount-tag {
    position: fixed;
    left: 0;
    top: 60%;
    transform-origin: top left;
    transform: rotate(-90deg) translateX(-50%);
    background-color: #58cc02;
    color: #fff;
    font-weight: 800;
    font-size: 14px;
    padding: 8px 24px;
    border-radius: 0 0 8px 8px; // 旋转后变成右侧圆角
    z-index: 999;
    letter-spacing: 1px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      padding-top: 12px; // 悬浮时稍微变宽
    }
  }

  .main-content {
    flex: 1;
  }
}
</style>
