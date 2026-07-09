import type { PrestataireStatus } from '~/data/prestataire/domain/PrestataireStatus'

export const PRESTATAIRE_STATUS_CONFIG: Record<PrestataireStatus, { pillBg: string; pillText: string }> = {
  DRAFT: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  IN_REVIEW: { pillBg: '#E67E22', pillText: '#ffffff' },
  PUBLISHED: { pillBg: '#2E7D32', pillText: '#ffffff' },
}
