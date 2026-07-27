import type { AdminReservationStatus } from '~/data/admin/domain/AdminReservationStatus'

export interface AdminReservationListItem {
  id: string
  eventTitle: string
  organizerEmail: string
  providerEmail: string
  providerSlug: string
  status: AdminReservationStatus
  createdAt: string
}
