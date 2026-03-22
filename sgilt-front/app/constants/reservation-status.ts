import type { ReservationStatus } from '~/types/event'

// ── Statuts valides ───────────────────────────────────────────────────────────

export const RESERVATION_STATUTS = [
  'nouvelle',
  'recontactee',
  'confirmee',
  'cloturee',
  'annulee',
] as const

export const RESERVATION_STATUS_ORDER: ReservationStatus[] = [
  'nouvelle',
  'recontactee',
  'confirmee',
  'cloturee',
  'annulee',
]

export type ReservationStatut = (typeof RESERVATION_STATUTS)[number]

// ── Config — source de vérité couleurs ───────────────────────────────────────

export const RESERVATION_STATUS_CONFIG: Record<
  ReservationStatus,
  {
    color: string // couleur principale : texte, bordure
    bgColor: string // fond pastel : pills compteur, badges soft
    pillBg: string // fond solid : StatusBadge
    pillText: string // texte solid : StatusBadge
  }
> = {
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
}

// ── Helper — fond overlay semi-transparent pour bandeau cover ─────────────────

export function getStatusOverlayStyle(status: ReservationStatus): {
  background: string
  color: string
} {
  const hex = RESERVATION_STATUS_CONFIG[status].color
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  return { background: `rgba(${r}, ${g}, ${b}, 0.45)`, color: '#fff' }
}
