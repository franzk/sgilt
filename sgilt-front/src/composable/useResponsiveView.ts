import { onMounted, onUnmounted, ref } from 'vue'
import { getCssVariable } from '@/utils/StyleUtils'

/**
 * Detects if the current view is a tablet view
 * @returns {isResponsiveView: Ref<boolean>} - A reactive reference to the current view
 */
export const useResponsiveView = (breakpointCssVariable: string) => {
  const isResponsiveView = ref(false)

  const tabletViewDetector = window.matchMedia(
    `(max-width: ${parseInt(getCssVariable(breakpointCssVariable))}px)`,
  )

  const updateTabletView = () => {
    isResponsiveView.value = tabletViewDetector.matches
  }

  onMounted(() => {
    updateTabletView()
    tabletViewDetector.addEventListener('change', () => updateTabletView())
  })

  onUnmounted(() => tabletViewDetector.removeEventListener('change', () => updateTabletView()))

  return { isResponsiveView }
}
