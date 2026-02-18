import { useMediaQuery } from '@vueuse/core'

export function useDevice() {
  const isDesktop = useMediaQuery('(min-width: 1024px)')
  const isTablet = useMediaQuery('(min-width: 768px)')
  const isMobile = useMediaQuery('(max-width: 767px)')

  return {
    isDesktop,
    isTablet,
    isMobile,
  }
}
