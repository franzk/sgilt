import type {
  ProDemandeDetail,
  ProDemandeSummary,
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
  getGreetingSubtitle(demandes: ProDemandeSummary[]): string {
    const n = demandes.filter((d) => d.statut === 'nouvelle').length
    const c = demandes.filter((d) => d.statut === 'confirmee').length
    if (n === 0 && c === 0) return 'Tout est à jour.'
    const nLabel = n > 0 ? `${n} nouvelle${n > 1 ? 's' : ''} demande${n > 1 ? 's' : ''}` : ''
    const cLabel = c > 0 ? `${c} événement${c > 1 ? 's' : ''} confirmé${c > 1 ? 's' : ''}` : ''
    if (nLabel && cLabel) return `Vous avez ${nLabel} et ${cLabel}.`
    if (nLabel) return `Vous avez ${nLabel}.`
    return `Vous avez ${cLabel}.`
  },

  async getBoardCounts(): Promise<ProBoardCounts> {
    await new Promise((r) => setTimeout(r, 250))
    return {
      countNouvelle: PRO_DEMANDES.filter((d) => d.status === 'nouvelle').length,
      countEnDiscussion: PRO_DEMANDES.filter((d) => d.status === 'en_discussion').length,
      countConfirmee: PRO_DEMANDES.filter((d) => d.status === 'confirmee').length,
    }
  },

  async getAllDemandes(): Promise<ProDemandeSummary[]> {
    // await new Promise((r) => setTimeout(r, 250))
    return []
  },

  async getDemandeById(id: string): Promise<ProDemandeDetail | null> {
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
export type { ProDemandeDetail, ProDemandeSummary, ProBoardCounts }
