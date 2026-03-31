import type {
  ProDemandeDetail,
  ProDemandeSummary,
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

  async getAllDemandes(): Promise<ProDemandeSummary[]> {
    await new Promise((r) => setTimeout(r, 250))
    return PRO_DEMANDES.map((d) => ({
      id: d.id,
      titre: d.event.title,
      date: d.event.date,
      dateIso: d.event.date ? d.event.date.toISOString() : '',
      dateReception: d.notes[0]?.createdAt ? formatDateFR(new Date(d.notes[0].createdAt)) : '',
      statut: d.status,
      ligneContextuelle: d.ligneContextuelle,
      urgencyLevel: d.urgencyLevel,
      coverImage: d.event.coverImage,
      progressType: d.progressType,
      progressValue: d.progressValue,
      phraseInfoState: d.phraseInfoState,
      unreadNotesCount: d.unreadNotesCount,
    }))
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

  async addNote(id: string, content: string, isPersonal: boolean): Promise<ReservationNote> {
    await new Promise((r) => setTimeout(r, 300))
    const note: ReservationNote = {
      id: `pn-${Date.now()}`,
      author: DJ,
      content,
      createdAt: new Date(),
      isPersonal,
    }
    const demande = PRO_DEMANDES.find((d) => d.id === id)
    if (demande) demande.notes.push(note)
    return note
  },
}

// Re-export types for consumers
export type { ProDemandeDetail, ProDemandeSummary }
