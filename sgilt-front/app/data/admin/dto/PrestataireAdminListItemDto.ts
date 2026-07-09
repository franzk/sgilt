import type { PrestataireStatus } from '~/data/prestataire/domain/PrestataireStatus'

export interface PrestataireAdminListItemDto {
  id: string
  name: string
  slug: string
  status: PrestataireStatus
}
