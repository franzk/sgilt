import type { PrestataireStatus } from '~/data/prestataire/domain/PrestataireStatus'

export interface PrestataireReservationCountsDto {
  confirmedCount: number
  inDiscussionCount: number
  nouvelleCount: number
  refuseeCount: number
  annuleeCount: number
  realiseeCount: number
}

export interface PrestataireAdminListItemDto {
  id: string
  name: string
  slug: string
  status: PrestataireStatus
  email: string
  categoryKey: string
  subcatKeys: string[]
  reservationCounts: PrestataireReservationCountsDto
}
