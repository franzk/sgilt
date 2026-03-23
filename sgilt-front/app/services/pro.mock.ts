import type { ProDemandeDetail, ProDemandeSummary, ReservationNote, ReservationStatus } from '~/types/event'
import { PRO_DEMANDES, DJ } from '~/services/mock'

function formatDateFR(isoDate: string): string {
  return new Date(isoDate).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

export const ProMockService = {
  async getAllDemandes(): Promise<ProDemandeSummary[]> {
    await new Promise((r) => setTimeout(r, 250))
    return PRO_DEMANDES.map((d) => ({
      id: d.id,
      titre: d.event.title,
      date: d.event.date ? formatDateFR(d.event.date) : '',
      dateIso: d.event.date ?? '',
      dateReception: d.notes[0]?.createdAt ? formatDateFR(d.notes[0].createdAt) : '',
      statut: d.status,
      ligneContextuelle: d.ligneContextuelle,
      urgencyLevel: d.urgencyLevel,
      coverImage: d.event.coverImage,
      progressType: d.progressType,
      progressValue: d.progressValue,
      phraseUrgence: d.phraseUrgence,
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
      createdAt: new Date().toISOString(),
      isPersonal,
    }
    const demande = PRO_DEMANDES.find((d) => d.id === id)
    if (demande) demande.notes.push(note)
    return note
  },
}

// Re-export types for consumers
export type { ProDemandeDetail, ProDemandeSummary }
