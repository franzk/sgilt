export const getCssVariable = (variable: string): string => {
  const value = getComputedStyle(document.documentElement).getPropertyValue(variable)
  return value.trim()
}
