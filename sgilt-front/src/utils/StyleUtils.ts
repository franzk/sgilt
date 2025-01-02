export const getCssVariable = (variable: string): string => {
  const value = getComputedStyle(document.documentElement).getPropertyValue(variable)
  return value.trim()
}

export const mobileView = window.matchMedia(
  `(max-width: ${parseInt(getCssVariable('--breakpoint-mobile'))}px)`,
).matches
//const mobileBreakpoint = parseInt(getCssVariable('--breakpoint-mobile'))
//const mobileView = window.matchMedia(`(max-width: ${mobileBreakpoint}px)`).matches
