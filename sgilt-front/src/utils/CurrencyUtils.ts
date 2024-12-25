/**
 * Truncate a number (in string format) to a certain number of decimal places
 * @param value Value to truncate
 * @param decimalCount number of decimal places to keep
 * @returns Truncated value
 */
export const truncateDecimals = (value: string, decimalCount: number): string => {
  value.replace('.', ',')
  if (value.includes(',')) {
    const [integer, decimals] = value.split(',')
    return `${integer}.${decimals.slice(0, decimalCount)}`
  } else {
    return value
  }
}

/**
 * format a number to a currency string :
 * - if the number is an integer, it will be returned as is
 * - if the number is a float, it will be rounded to 2 decimal places
 * @param value number to format
 * @returns formatted currency string
 */
export const formatCurrency = (value?: number): string => {
  return value ? (Number.isInteger(value) ? value.toString() : value.toFixed(2)) : ''
}
