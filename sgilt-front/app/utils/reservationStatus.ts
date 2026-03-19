import type { ReservationStatus } from '~/types/event'

export const RESERVATION_STATUS_LABELS: Record<ReservationStatus, string> = {
  brouillon: 'Brouillon',
  envoyee: 'Envoyée',
  recontactee: 'Recontactée',
  confirmee: 'Confirmée',
  annulee: 'Annulée',
  cloturee: 'Clôturée',
  terminee: 'Terminée',
}

export const RESERVATION_STATUS_SECTION_LABELS: Record<ReservationStatus, string> = {
  confirmee: 'Confirmées',
  recontactee: 'Recontactées',
  envoyee: 'Envoyées',
  brouillon: 'Brouillons',
  cloturee: 'Clôturées',
  annulee: 'Annulées',
  terminee: 'Terminées',
}

export const RESERVATION_STATUS_ORDER: ReservationStatus[] = [
  'confirmee',
  'recontactee',
  'envoyee',
  'brouillon',
  'cloturee',
  'annulee',
  'terminee',
]

/** Couleur de la border-left des cards et badges selon le statut */
export const RESERVATION_STATUS_COLORS: Record<ReservationStatus, string> = {
  confirmee: '#3b6d11',
  recontactee: '#e6b800',
  envoyee: '#185fa5',
  brouillon: '#6b635c',
  cloturee: '#a32d2d',
  annulee: '#a32d2d',
  terminee: '#6b635c',
}
