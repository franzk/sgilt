import { onMounted, onUnmounted, ref } from 'vue'
import { getCssVariable } from '@/utils/StyleUtils'

/**
 * Detects if the current view is a tablet view
 * @returns {isTabletView: Ref<boolean>} - A reactive reference to the current view
 */
export const useTabletView = () => {
  const isTabletView = ref(false)

  const tabletViewDetector = window.matchMedia(
    `(max-width: ${parseInt(getCssVariable('--breakpoint-tablet'))}px)`,
  )

  const updateTabletView = () => {
    isTabletView.value = tabletViewDetector.matches
  }

  onMounted(() => {
    updateTabletView()
    tabletViewDetector.addEventListener('change', () => updateTabletView())
  })

  onUnmounted(() => tabletViewDetector.removeEventListener('change', () => updateTabletView()))

  return { isTabletView }
}
