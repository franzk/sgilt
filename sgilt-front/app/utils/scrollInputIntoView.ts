/**
 * Trouve le premier ancêtre scrollable de `el`, puis scrolle ce conteneur
 * pour centrer `el` dans sa zone visible.
 *
 * Contrairement à `el.scrollIntoView()`, cela ne touche pas au scroll du
 * document — indispensable quand l'élément est dans une bottom sheet.
 */
export function scrollInputIntoView(el: HTMLElement): void {
  const container = findScrollableParent(el)
  if (!container) return

  const containerRect = container.getBoundingClientRect()
  const elRect = el.getBoundingClientRect()

  // Position de el relative au conteneur (en tenant compte du scroll actuel)
  const elTop = elRect.top - containerRect.top + container.scrollTop
  const elHeight = elRect.height
  const containerHeight = container.clientHeight

  const targetScrollTop = elTop - (containerHeight - elHeight) / 2

  container.scrollTo({ top: targetScrollTop, behavior: 'smooth' })
}

/**
 * Remonte les ancêtres du DOM jusqu'à trouver un élément scrollable
 * (overflow-y: auto | scroll et contenu dépassant la hauteur visible).
 */
function findScrollableParent(el: HTMLElement): HTMLElement | null {
  let node: HTMLElement | null = el.parentElement

  while (node && node !== document.body) {
    const { overflowY } = window.getComputedStyle(node)
    const isScrollable = overflowY === 'auto' || overflowY === 'scroll'
    if (isScrollable && node.scrollHeight > node.clientHeight) {
      return node
    }
    node = node.parentElement
  }

  return null
}
