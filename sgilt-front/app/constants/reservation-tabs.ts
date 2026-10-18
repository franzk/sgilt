// Onglets de la fiche d'une réservation, communs au client et au prestataire. Suivi est la fiche
// elle-même (URL de la réservation, celle des mails et notifications) ; les autres ajoutent leur clé
// à l'URL (/details, /documents, /prestataire).
export const RESERVATION_TABS = ['suivi', 'details', 'documents', 'prestataire'] as const

export type ReservationTab = (typeof RESERVATION_TABS)[number]

// Page à part, hors onglets : l'annulation après confirmation (/actions).
export const SPECIAL_ACTIONS_SEGMENT = 'actions'

// Onglet désigné par le segment d'URL qui suit la réservation : Suivi sans segment, null s'il est inconnu.
export function reservationTabOf(segment: string | undefined): ReservationTab | null {
  if (!segment) return 'suivi'
  return RESERVATION_TABS.find((tab) => tab !== 'suivi' && tab === segment) ?? null
}

// Chemin d'un onglet à partir du chemin de la réservation.
export function reservationTabPath(reservationPath: string, tab: ReservationTab): string {
  return tab === 'suivi' ? reservationPath : `${reservationPath}/${tab}`
}
