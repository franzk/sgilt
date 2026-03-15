import type { EventDetail, EventPatch } from '~/types/event'

const MOCK_EVENT: EventDetail = {
  id: 'evt-001',
  title: 'Mariage de Julie & Thomas',
  date: '2026-09-14',
  eventType: 'mariage',
  ambiance: 'chic',
  ville: 'Strasbourg',
  nbInvites: '120',
  sharedNote: "Cérémonie à 14h, vin d'honneur à 16h. Accès PMR prévu.",
  sharedNoteUpdatedAt: '2026-02-20T11:30:00.000Z',
  journal: [
    {
      id: 'log-001',
      date: new Date('2026-01-10T09:15:00.000Z'),
      isCreation: true,
      modifications: [
        { champ: 'Titre', avant: null, apres: 'Mariage de Julie & Thomas' },
        { champ: 'Date', avant: null, apres: '14 sept. 2026' },
        { champ: 'Type', avant: null, apres: 'Mariage' },
      ],
    },
    {
      id: 'log-002',
      date: new Date('2026-01-22T14:05:00.000Z'),
      isCreation: false,
      modifications: [
        { champ: 'Ville', avant: null, apres: 'Strasbourg' },
        { champ: "Nombre d'invités", avant: null, apres: '120' },
      ],
    },
    {
      id: 'log-003',
      date: new Date('2026-02-05T16:40:00.000Z'),
      isCreation: false,
      modifications: [{ champ: 'Ambiance', avant: null, apres: 'Chic' }],
    },
    {
      id: 'log-004',
      date: new Date('2026-02-20T11:30:00.000Z'),
      isCreation: false,
      modifications: [
        {
          champ: 'Note partagée',
          avant: '',
          apres: "Cérémonie à 14h, vin d'honneur à 16h. Accès PMR prévu.",
        },
      ],
    },
  ],
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

  async patchEvent(id: string, patch: EventPatch): Promise<void> {
    await new Promise((r) => setTimeout(r, 400))
    if (id === MOCK_EVENT.id) Object.assign(MOCK_EVENT, patch)
  },
}
