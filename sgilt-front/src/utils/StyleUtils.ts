export const getCssVariable = (variable: string): string => {
  const value = getComputedStyle(document.documentElement).getPropertyValue(variable)
  return value.trim()
}

export const mobileBreakpoint = parseInt(getCssVariable('--breakpoint-mobile'))
export const tabletBreakpoint = parseInt(getCssVariable('--breakpoint-tablet'))

export const mobileView = window.matchMedia(`(max-width: ${mobileBreakpoint}px)`).matches

export const tabletView = window.matchMedia(
  `(min-width: ${mobileBreakpoint + 1}px) and (max-width: ${tabletBreakpoint}px)`,
).matches
