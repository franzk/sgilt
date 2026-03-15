import type { EventDetail } from '~/types/event'

const MOCK_EVENT: EventDetail = {
  id: 'evt-001',
  title: 'Mariage de Julie & Thomas',
  date: '2026-09-14',
  eventType: 'mariage',
  ambiance: 'chic',
  ville: 'Strasbourg',
  nbInvites: '120',
  sharedNote: '',
  sharedNoteUpdatedAt: undefined,
  reservations: [
    {
      id: 'res-001',
      prestataireName: 'DJ Marco',
      category: 'DJ',
      status: 'confirmee',
      hasNotes: true,
    },
    {
      id: 'res-002',
      prestataireName: 'Atelier Lumière',
      category: 'Photographe',
      status: 'recontactee',
      hasNotes: false,
    },
    {
      id: 'res-003',
      prestataireName: "Les Saveurs d'Alsace",
      category: 'Traiteur',
      status: 'envoyee',
      hasNotes: true,
    },
    {
      id: 'res-004',
      prestataireName: 'Maison des Fleurs',
      category: 'Fleuriste',
      status: 'brouillon',
      hasNotes: false,
    },
    {
      id: 'res-005',
      prestataireName: 'Studio Vidéo 67',
      category: 'Vidéaste',
      status: 'cloturee',
      hasNotes: false,
    },
    {
      id: 'res-006',
      prestataireName: 'Orchestre Harmonie',
      category: 'Musique live',
      status: 'annulee',
      hasNotes: false,
    },
  ],
}

export const EventMockService = {
  async getById(id: string): Promise<EventDetail | null> {
    await new Promise((r) => setTimeout(r, 250))
    return id === MOCK_EVENT.id ? structuredClone(MOCK_EVENT) : null
  },

  async patchNote(id: string, note: string): Promise<{ updatedAt: string }> {
    await new Promise((r) => setTimeout(r, 300))
    if (id === MOCK_EVENT.id) {
      MOCK_EVENT.sharedNote = note
      MOCK_EVENT.sharedNoteUpdatedAt = new Date().toISOString()
    }
    return { updatedAt: MOCK_EVENT.sharedNoteUpdatedAt! }
  },
}
