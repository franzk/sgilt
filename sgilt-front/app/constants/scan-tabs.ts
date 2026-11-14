/**
 * Onglets de l'écran plein écran de scan des e-billets d'un événement (/app/events/{eventId}/scan).
 * Scan est l'écran lui-même (URL du scan, celle du bouton de la billetterie) ; Récap ajoute sa clé à
 * l'URL (/recap). Même forme que les onglets de la billetterie.
 */
export const SCAN_TABS = ['scan', 'recap'] as const

export type ScanTab = (typeof SCAN_TABS)[number]

// Onglet désigné par le segment d'URL qui suit le scan : Scan sans segment, null s'il est inconnu.
export function scanTabOf(segment: string | undefined): ScanTab | null {
  if (!segment) return 'scan'
  return SCAN_TABS.find((tab) => tab !== 'scan' && tab === segment) ?? null
}

// Chemin d'un onglet à partir du chemin du scan.
export function scanTabPath(scanPath: string, tab: ScanTab): string {
  return tab === 'scan' ? scanPath : `${scanPath}/${tab}`
}
