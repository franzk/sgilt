import type { AdminReservationStatus } from '~/data/admin/domain/AdminReservationStatus'

export const ADMIN_RESERVATION_STATUS_CONFIG: Record<AdminReservationStatus, { pillBg: string; pillText: string }> = {
  NEW: { pillBg: '#D93025', pillText: '#ffffff' },
  IN_DISCUSSION: { pillBg: '#E67E22', pillText: '#ffffff' },
  CONFIRMED: { pillBg: '#2E7D32', pillText: '#ffffff' },
  DONE: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  REFUSED_PRE_CONTACT: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  REFUSED_POST_CONTACT: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  CANCELED_BY_CLIENT_PRE_CONTACT: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  CANCELED_BY_CLIENT_POST_CONTACT: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  CANCELED_BY_CLIENT_POST_CONFIRMATION: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  CANCELED_BY_PRO_POST_CONFIRMATION: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
}
