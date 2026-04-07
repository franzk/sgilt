import type { CalendarEntry } from '~/types/calendrier'

// ── Réservations Sgilt confirmées — non modifiables ───────────────────────────
//
// Avril 2026 : mois chargé (4 réservations)
// Mai   2026 : mois léger (1 réservation)
// Juin  2026 : mois minimal (1 réservation)

const SGILT_ENTRIES: CalendarEntry[] = [
  // ── Avril ────────────────────────────────────────────────────────────────────
  {
    date: '2026-04-04',
    type: 'sgilt',
    reservationId: 'pro-005',
    label: 'Cocktail lancement produit · Thomas B.',
  },
  {
    date: '2026-04-12',
    type: 'sgilt',
    reservationId: 'pro-003',
    label: "Soirée d'entreprise · Marc D.",
  },
  {
    date: '2026-04-18',
    type: 'sgilt',
    reservationId: 'pro-006',
    label: 'Mariage de Sophie & Julien',
  },
  {
    date: '2026-04-25',
    type: 'sgilt',
    reservationId: 'pro-007',
    label: 'Anniversaire 40 ans de Claire',
  },
  // ── Mai ──────────────────────────────────────────────────────────────────────
  {
    date: '2026-05-23',
    type: 'sgilt',
    reservationId: 'pro-002',
    label: 'Anniversaire surprise de Marc · Sophie L.',
  },
  // ── Juin ─────────────────────────────────────────────────────────────────────
  {
    date: '2026-06-06',
    type: 'sgilt',
    reservationId: 'pro-004',
    label: "Mariage d'Émilie · Colmar",
  },
  // ── Septembre ────────────────────────────────────────────────────────────────
  {
    date: '2026-09-14',
    type: 'sgilt',
    reservationId: 'res-001',
    label: 'Mariage de Julie & Thomas',
  },
]

// ── Indisponibilités manuelles pré-saisies ────────────────────────────────────
let manualEntries: CalendarEntry[] = [
  // Avril : week-end + jour isolé
  { date: '2026-04-13', type: 'manual' },
  { date: '2026-04-14', type: 'manual' },
  { date: '2026-04-20', type: 'manual' },
  { date: '2026-04-21', type: 'manual' },
  // Mai : jour férié bloqué
  { date: '2026-05-01', type: 'manual' },
]

export const CalendarMockService = {
  getAll(): CalendarEntry[] {
    return [...SGILT_ENTRIES, ...manualEntries]
  },

  async addUnavailability(date: string): Promise<void> {
    await new Promise((r) => setTimeout(r, 150))
    if (!manualEntries.find((e) => e.date === date)) {
      manualEntries.push({ date, type: 'manual' })
    }
  },

  async removeUnavailability(date: string): Promise<void> {
    await new Promise((r) => setTimeout(r, 150))
    manualEntries = manualEntries.filter((e) => e.date !== date)
  },
}
