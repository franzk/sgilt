/**
 * Composable — détecte la hauteur du clavier virtuel via visualViewport.
 *
 * Expose --keyboard-offset (CSS var sur :root) = espace occupé par le clavier.
 * Utilise visualViewport.height, la seule valeur cross-device fiable sur Android
 * avec comportement adjustPan (window.innerHeight ne change pas dans ce mode).
 */
export function useVirtualKeyboard() {
  const keyboardOffset = ref(0)

  function update() {
    if (!import.meta.client || !window.visualViewport) return
    const offset = Math.max(0, window.innerHeight - window.visualViewport.height)
    keyboardOffset.value = offset
    document.documentElement.style.setProperty('--keyboard-offset', `${offset}px`)
  }

  function reset() {
    keyboardOffset.value = 0
    document.documentElement.style.setProperty('--keyboard-offset', '0px')
  }

  onMounted(() => {
    window.visualViewport?.addEventListener('resize', update)
    update()
  })

  onUnmounted(() => {
    window.visualViewport?.removeEventListener('resize', update)
    reset()
  })

  return { keyboardOffset }
}
