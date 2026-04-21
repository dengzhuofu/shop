<template>
  <ClientOnly>
    <div class="msg-container">
      <TransitionGroup name="msg-slide">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="msg-item"
          :class="['msg-' + msg.type]"
        >
          <!-- 图标 -->
          <div class="msg-icon">
            <!-- 成功图标 -->
            <svg v-if="msg.type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <!-- 错误图标 -->
            <svg v-else-if="msg.type === 'error'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <!-- 警告图标 -->
            <svg v-else-if="msg.type === 'warning'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <!-- 提示图标 -->
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>

          <!-- 消息内容 -->
          <div class="msg-content">
            {{ msg.text }}
          </div>

          <!-- 关闭按钮 -->
          <button
            @click="removeMessage(msg.id)"
            class="msg-close"
            aria-label="关闭"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </ClientOnly>
</template>

<script setup lang="ts">
import { useMessage } from '~/composables/useMessage'

const { messages, removeMessage } = useMessage()
</script>

<style scoped lang="scss">
.msg-container {
  position: fixed;
  top: 1rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  pointer-events: none;
  width: 100%;
  max-width: 24rem;
  padding: 0 1rem;
}

.msg-item {
  pointer-events: auto;
  display: flex;
  align-items: flex-start;
  padding: 1rem;
  border-radius: 0.75rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  border: 1px solid transparent;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition: all 0.3s ease;
  font-family: var(--shop-font-family-sans);
}

.msg-icon {
  flex-shrink: 0;
  margin-right: 0.75rem;
  margin-top: 0.125rem;
  
  svg {
    width: 1.25rem;
    height: 1.25rem;
  }
}

.msg-content {
  flex: 1;
  font-size: 0.875rem;
  font-weight: 500;
  line-height: 1.5;
}

.msg-close {
  flex-shrink: 0;
  margin-left: 0.75rem;
  color: currentColor;
  opacity: 0.6;
  transition: opacity 0.2s ease;
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:hover {
    opacity: 1;
  }
  
  svg {
    width: 1rem;
    height: 1rem;
  }
}

/* 不同类型的主题颜色 */
.msg-success {
  background-color: rgba(236, 253, 245, 0.95);
  border-color: #a7f3d0;
  color: #065f46;
}

.msg-error {
  background-color: rgba(255, 241, 242, 0.95);
  border-color: #fecdd3;
  color: #9f1239;
}

.msg-warning {
  background-color: rgba(255, 251, 235, 0.95);
  border-color: #fde68a;
  color: #92400e;
}

.msg-info {
  background-color: rgba(239, 246, 255, 0.95);
  border-color: #bfdbfe;
  color: #1e40af;
}

/* 消息滑动进出动画 */
.msg-slide-enter-active,
.msg-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.msg-slide-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}
.msg-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}
</style>
