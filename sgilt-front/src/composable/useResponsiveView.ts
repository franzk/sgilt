import { onMounted, onUnmounted, ref } from 'vue'
import { getCssVariable } from '@/utils/StyleUtils'

/**
 * Detects if the current view is a mobile or a tablet view
 * @returns isMobileView - A boolean ref that indicates if the current view is a mobile view
 * @returns isTabletView - A boolean ref that indicates if the current view is a tablet view
 */
export const useResponsiveView = () => {
  const isMobileView = ref(false)
  const isTabletView = ref(false)

  const mobileViewDetector = window.matchMedia(
    `(max-width: ${parseInt(getCssVariable('--breakpoint-mobile'))}px)`,
  )

  const tabletViewDetector = window.matchMedia(
    `(min-width: ${parseInt(getCssVariable('--breakpoint-mobile'))}px) and (max-width: ${parseInt(getCssVariable('--breakpoint-tablet'))}px)`,
  )

  const updateTabletView = () => {
    isTabletView.value = tabletViewDetector.matches
  }

  const updateMobileView = () => {
    isMobileView.value = mobileViewDetector.matches
  }

  onMounted(() => {
    updateMobileView()
    updateTabletView()
    tabletViewDetector.addEventListener('change', () => updateTabletView())
    mobileViewDetector.addEventListener('change', () => updateMobileView())
  })

  onUnmounted(() => tabletViewDetector.removeEventListener('change', () => updateTabletView()))
  onUnmounted(() => mobileViewDetector.removeEventListener('change', () => updateMobileView()))

  return { isMobileView, isTabletView }
}
