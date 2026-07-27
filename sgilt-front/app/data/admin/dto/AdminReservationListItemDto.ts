import type { AdminReservationStatus } from '~/data/admin/domain/AdminReservationStatus'

export interface AdminReservationListItemDto {
  id: string
  eventTitle: string
  organizerEmail: string
  providerEmail: string
  providerSlug: string
  status: AdminReservationStatus
  createdAt: string
}
