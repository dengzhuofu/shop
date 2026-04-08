import { formatSkuAttributeKey } from '~/utils/skuAttributes'

export function useShopFormat() {
  const { lang, t } = useShopLocale()

  const money = (value: number | string | null | undefined) => {
    const numeric = Number(value || 0)
    return new Intl.NumberFormat(lang.value === 'zh' ? 'zh-CN' : 'en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
    }).format(numeric)
  }

  const attributeText = (attributes: any) => {
    if (!attributes) {
      return ''
    }
    const source = attributes.attributes || attributes.attributeDisplay || attributes
    const entries = Object.entries(source)
      .filter(([, value]) => value !== null && value !== undefined && `${value}`.trim() !== '')
      .map(([key, value]) => `${formatSkuAttributeKey(key)}: ${value}`)
    return entries.join(' / ')
  }

  const statusText = (status: string | null | undefined) => {
    switch (status) {
      case 'PENDING_PAYMENT':
        return t('statusPendingPayment')
      case 'PAYMENT_PROCESSING':
        return t('statusPaymentProcessing')
      case 'PAID':
        return t('statusPaid')
      case 'CANCELLED':
        return t('statusCancelled')
      default:
        return status || '--'
    }
  }

  const dateTime = (value: string | null | undefined) => {
    if (!value) {
      return '--'
    }
    const parsed = new Date(value)
    if (Number.isNaN(parsed.getTime())) {
      return value
    }
    return new Intl.DateTimeFormat(lang.value === 'zh' ? 'zh-CN' : 'en-US', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    }).format(parsed)
  }

  return {
    money,
    attributeText,
    statusText,
    dateTime,
  }
}
