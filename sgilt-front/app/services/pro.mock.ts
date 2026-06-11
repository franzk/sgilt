import type {
  ProReservationDetail,
  ProReservationSummary,
  ProBoardCounts,
  ReservationNote,
  ReservationStatus,
} from '~/types/event'
import { PRO_DEMANDES, DJ } from '~/services/mock'

function formatDateFR(input: Date): string {
  return input.toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

export const ProMockService = {
  async getDemandeById(id: string): Promise<ProReservationDetail | null> {
    await new Promise((r) => setTimeout(r, 200))
    return PRO_DEMANDES.find((d) => d.id === id) ?? null
  },

  async updateStatut(id: string, status: ReservationStatus): Promise<void> {
    await new Promise((r) => setTimeout(r, 300))
    const demande = PRO_DEMANDES.find((d) => d.id === id)
    if (demande) demande.status = status
  },
}

// Re-export types for consumers
export type { ProReservationDetail, ProReservationSummary, ProBoardCounts }
