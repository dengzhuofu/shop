import { onBeforeUnmount, ref } from 'vue'

export function usePaymentDeadline() {
  const now = ref(Date.now())
  let timer: ReturnType<typeof setInterval> | null = null

  const tick = () => {
    now.value = Date.now()
  }

  const start = () => {
    if (!process.client || timer) {
      return
    }

    tick()
    timer = window.setInterval(tick, 1000)
  }

  const stop = () => {
    if (!timer) {
      return
    }

    clearInterval(timer)
    timer = null
  }

  const toTimestamp = (value: string | null | undefined) => {
    if (!value) {
      return null
    }

    const timestamp = new Date(value).getTime()
    return Number.isNaN(timestamp) ? null : timestamp
  }

  const remainingMs = (value: string | null | undefined) => {
    const deadline = toTimestamp(value)
    if (deadline == null) {
      return 0
    }

    return Math.max(0, deadline - now.value)
  }

  const isExpired = (value: string | null | undefined) => {
    const deadline = toTimestamp(value)
    return deadline == null ? false : deadline <= now.value
  }

  const countdownText = (value: string | null | undefined, language = 'en') => {
    const remaining = remainingMs(value)
    if (remaining <= 0) {
      return language === 'zh' ? '\u5df2\u8d85\u65f6' : 'Expired'
    }

    const totalSeconds = Math.ceil(remaining / 1000)
    const hours = Math.floor(totalSeconds / 3600)
    const minutes = Math.floor((totalSeconds % 3600) / 60)
    const seconds = totalSeconds % 60

    if (hours > 0) {
      return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    }

    return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  }

  onBeforeUnmount(stop)

  return {
    now,
    start,
    stop,
    toTimestamp,
    remainingMs,
    isExpired,
    countdownText,
  }
}
