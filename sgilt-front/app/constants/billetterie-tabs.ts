/**
 * Onglets de l'écran Billetterie d'un événement (/app/events/{eventId}/billetterie). Réglages est
 * l'écran lui-même (URL de la billetterie, celle de la sidebar et de la carte du board) ; Commandes
 * ajoute sa clé à l'URL (/commandes).
 */
export const BILLETTERIE_TABS = ['reglages', 'commandes'] as const

export type BilletterieTab = (typeof BILLETTERIE_TABS)[number]

// Onglet désigné par le segment d'URL qui suit la billetterie : Réglages sans segment, null s'il est inconnu.
export function billetterieTabOf(segment: string | undefined): BilletterieTab | null {
  if (!segment) return 'reglages'
  return BILLETTERIE_TABS.find((tab) => tab !== 'reglages' && tab === segment) ?? null
}

// Chemin d'un onglet à partir du chemin de la billetterie.
export function billetterieTabPath(billetteriePath: string, tab: BilletterieTab): string {
  return tab === 'reglages' ? billetteriePath : `${billetteriePath}/${tab}`
}
