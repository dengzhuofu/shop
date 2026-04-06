export type AddressDraft = {
  country: string
  firstName: string
  lastName: string
  phone: string
  addressLine1: string
  addressLine2: string
  city: string
  state: string
  zipCode: string
}

const normalizeText = (value: unknown) =>
  String(value || '')
    .trim()
    .replace(/\s+/g, ' ')
    .toLowerCase()

export const createEmptyAddressDraft = (): AddressDraft => ({
  country: 'United States',
  firstName: '',
  lastName: '',
  phone: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  zipCode: '',
})

export const toAddressDraft = (source: Partial<AddressDraft> | null | undefined): AddressDraft => ({
  country: String(source?.country || 'United States'),
  firstName: String(source?.firstName || ''),
  lastName: String(source?.lastName || ''),
  phone: String(source?.phone || ''),
  addressLine1: String(source?.addressLine1 || ''),
  addressLine2: String(source?.addressLine2 || ''),
  city: String(source?.city || ''),
  state: String(source?.state || ''),
  zipCode: String(source?.zipCode || ''),
})

export const getAddressSignature = (source: Partial<AddressDraft> | null | undefined) =>
  [
    normalizeText(source?.country),
    normalizeText(source?.firstName),
    normalizeText(source?.lastName),
    normalizeText(source?.phone),
    normalizeText(source?.addressLine1),
    normalizeText(source?.addressLine2),
    normalizeText(source?.city),
    normalizeText(source?.state),
    normalizeText(source?.zipCode),
  ].join('|')

export const addressesMatch = (
  left: Partial<AddressDraft> | null | undefined,
  right: Partial<AddressDraft> | null | undefined,
) => getAddressSignature(left) === getAddressSignature(right)

export const isAddressDraftComplete = (source: Partial<AddressDraft> | null | undefined) =>
  [
    source?.country,
    source?.firstName,
    source?.lastName,
    source?.addressLine1,
    source?.city,
    source?.state,
    source?.zipCode,
  ].every((value) => String(value || '').trim().length > 0)
