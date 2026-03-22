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

