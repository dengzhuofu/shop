<template>
  <button :class="['base-button', variant, size]" @click="$emit('click')">
    <slot />
  </button>
</template>

<script setup>
const props = defineProps({
  variant: {
    type: String,
    default: 'primary', // primary, secondary, outline
  },
  size: {
    type: String,
    default: 'md', // sm, md, lg
  }
})

defineEmits(['click'])
</script>

<style lang="scss" scoped>
@use "sass:color";

.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 30px;
  font-weight: 600;
  transition: all 0.3s ease;
  cursor: pointer;

  // Sizes
  &.sm {
    padding: 8px 16px;
    font-size: 14px;
  }
  &.md {
    padding: 12px 24px;
    font-size: 16px;
  }
  &.lg {
    padding: 16px 32px;
    font-size: 18px;
  }

  // Variants
  &.primary {
    background: #111;
    color: #fff;
    &:hover {
      background: #333;
    }
  }

  &.secondary {
    background: #58cc02;
    color: #fff;
    &:hover {
      background: color.adjust(#58cc02, $lightness: -10%);
    }
  }

  &.outline {
    background: transparent;
    border: 2px solid #111;
    color: #111;
    &:hover {
      background: #111;
      color: #fff;
    }
  }
}
</style>
