import type { PrestataireStatus } from '~/data/prestataire/domain/PrestataireStatus'

export interface PrestataireAdminFormat {
  id: string
  name: string
  slug: string
  status: PrestataireStatus
}
