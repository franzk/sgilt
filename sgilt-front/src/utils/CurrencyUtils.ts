export const truncateDecimals = (value: string, decimalCount: number): string => {
  if (value.includes('.')) {
    const [integer, decimals] = value.split('.')
    return `${integer}.${decimals.slice(0, decimalCount)}`
  } else {
    return value
  }
}

export const formatCurrency = (value?: number): string => {
  return value ? value.toString() : '' // TODO : format currency
}
