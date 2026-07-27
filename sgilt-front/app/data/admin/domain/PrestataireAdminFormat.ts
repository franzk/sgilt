import type { PrestataireStatus } from '~/data/prestataire/domain/PrestataireStatus'

export interface PrestataireReservationCounts {
  confirmedCount: number
  inDiscussionCount: number
  nouvelleCount: number
  refuseeCount: number
  annuleeCount: number
  realiseeCount: number
}

export interface PrestataireAdminFormat {
  id: string
  name: string
  slug: string
  status: PrestataireStatus
  email: string
  categoryKey: string
  subcatKeys: string[]
  reservationCounts: PrestataireReservationCounts
}
