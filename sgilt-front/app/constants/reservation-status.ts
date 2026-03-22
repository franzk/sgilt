import type { ReservationStatus } from '~/types/event'

// ── Statuts pro-facing (5 valeurs) ────────────────────────────────────────────

export const RESERVATION_STATUTS = [
  'nouvelle',
  'recontactee',
  'confirmee',
  'cloturee',
  'annulee',
] as const

export type ReservationStatut = (typeof RESERVATION_STATUTS)[number]

// ── Config complète — couvre tous les ReservationStatus ───────────────────────

export const RESERVATION_STATUS_CONFIG: Record<
  ReservationStatus,
  {
    color: string    // couleur principale : texte, bordure
    bgColor: string  // fond pastel : badges soft, pills compteur
    pillBg: string   // fond solid : StatusBadge
    pillText: string // texte solid : StatusBadge
  }
> = {
  // ── Pro-facing ──────────────────────────────────────────────────────────────
  nouvelle: {
    color: '#D93025',
    bgColor: 'rgba(217, 48, 37, 0.07)',
    pillBg: '#D93025',
    pillText: '#ffffff',
  },
  recontactee: {
    color: '#E67E22',
    bgColor: 'rgba(230, 126, 34, 0.07)',
    pillBg: '#E67E22',
    pillText: '#ffffff',
  },
  confirmee: {
    color: '#2E7D32',
    bgColor: 'rgba(46, 125, 50, 0.06)',
    pillBg: '#2E7D32',
    pillText: '#ffffff',
  },
  cloturee: {
    color: '#6B6B6B',
    bgColor: 'rgba(0, 0, 0, 0.03)',
    pillBg: '#E0E0E0',
    pillText: '#6B6B6B',
  },
  annulee: {
    color: '#6B6B6B',
    bgColor: 'rgba(0, 0, 0, 0.03)',
    pillBg: '#E0E0E0',
    pillText: '#6B6B6B',
  },
  // ── Client-facing ───────────────────────────────────────────────────────────
  brouillon: {
    color: '#6B635C',
    bgColor: 'rgba(107, 99, 92, 0.06)',
    pillBg: '#EBEBEA',
    pillText: '#6B635C',
  },
  envoyee: {
    color: '#2C5CC5',
    bgColor: 'rgba(44, 92, 197, 0.07)',
    pillBg: '#2C5CC5',
    pillText: '#ffffff',
  },
  terminee: {
    color: '#6B635C',
    bgColor: 'rgba(107, 99, 92, 0.06)',
    pillBg: '#EBEBEA',
    pillText: '#6B635C',
  },
}

// ── Helper — fond overlay semi-transparent pour bandeau cover ─────────────────

export function getStatusOverlayStyle(
  status: ReservationStatus,
): { background: string; color: string } {
  const hex = RESERVATION_STATUS_CONFIG[status].color
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  return { background: `rgba(${r}, ${g}, ${b}, 0.45)`, color: '#fff' }
}
