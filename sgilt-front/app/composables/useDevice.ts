import { useMediaQuery } from '@vueuse/core'

export function useDevice() {
  const isDesktop = useMediaQuery('(min-width: 849px)')
  // const isTablet = useMediaQuery('(min-width: 768px)')
  const isMobile = useMediaQuery('(max-width: 848px)')

  return {
    isDesktop,
    // isTablet,
    isMobile,
  }
}
