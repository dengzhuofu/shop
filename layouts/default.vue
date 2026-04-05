<template>
  <div class="layout-default">
    <div class="global-discount-tag">$20 OFF</div>
    <AppHeader />
    <main class="main-content">
      <slot />
    </main>
    <AppFooter />
    <CartSidebar />
    <QuickViewModal
      :is-open="quickViewOpen"
      :product="quickViewProduct"
      @close="closeQuickView"
    />
  </div>
</template>

<script setup lang="ts">
const { isOpen: quickViewOpen, product: quickViewProduct, closeQuickView } = useQuickView()
</script>

<style scoped lang="scss">
.layout-default {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
  background: #fff;

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
    border-radius: 0 0 8px 8px;
    z-index: 999;
    letter-spacing: 1px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      padding-top: 12px;
    }
  }

  .main-content {
    flex: 1;
  }
}

@media (max-width: 900px) {
  .layout-default {
    .global-discount-tag {
      display: none;
    }
  }
}
</style>
