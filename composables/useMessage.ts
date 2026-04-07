import { ref } from 'vue'

export interface Message {
  id: string
  type: 'success' | 'error' | 'info' | 'warning'
  text: string
  duration?: number
}

// 全局消息状态
const messages = ref<Message[]>([])

export const useMessage = () => {
  const addMessage = (message: Omit<Message, 'id'>) => {
    // 仅在客户端执行，避免 SSR 报错
    if (process.client) {
      const id = Math.random().toString(36).substring(2, 9)
      const newMsg = { ...message, id }
      messages.value.push(newMsg)

      if (message.duration !== 0) {
        setTimeout(() => {
          removeMessage(id)
        }, message.duration || 3000)
      }
    }
  }

  const success = (text: string, duration?: number) => addMessage({ type: 'success', text, duration })
  const error = (text: string, duration?: number) => addMessage({ type: 'error', text, duration })
  const info = (text: string, duration?: number) => addMessage({ type: 'info', text, duration })
  const warning = (text: string, duration?: number) => addMessage({ type: 'warning', text, duration })

  const removeMessage = (id: string) => {
    const index = messages.value.findIndex(m => m.id === id)
    if (index > -1) {
      messages.value.splice(index, 1)
    }
  }

  return {
    messages,
    success,
    error,
    info,
    warning,
    removeMessage
  }
}
