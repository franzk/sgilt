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
      prestataireId: '3',
      prestataireName: 'DJ Animation',
      prestatairePhoto: '/images/prestataires/dj-animation.jpg',
      category: 'Musique',
      status: 'confirmee',
      unreadNotesCount: 3,
    },
    {
      id: 'res-002',
      prestataireId: '16',
      prestataireName: 'Léo Clairmont Photographe',
      prestatairePhoto: '/images/prestataires/photographe-mariage.jpg',
      category: 'Photo',
      status: 'recontactee',
      unreadNotesCount: 0,
    },
    {
      id: 'res-003',
      prestataireId: '6',
      prestataireName: 'Éclat Gourmet',
      prestatairePhoto: '/images/prestataires/traiteur-gourmet.jpg',
      category: 'Restauration',
      status: 'envoyee',
      unreadNotesCount: 1,
    },
    {
      id: 'res-004',
      prestataireId: '1',
      prestataireName: 'Gypsy Reed Ensemble',
      prestatairePhoto: '/images/prestataires/jazz-band.jpg',
      category: 'Musique',
      status: 'brouillon',
      unreadNotesCount: 0,
    },
    {
      id: 'res-005',
      prestataireId: '18',
      prestataireName: 'Studio Photo Mobile',
      prestatairePhoto: '/images/prestataires/studio-photo-mobile.jpg',
      category: 'Photo',
      status: 'cloturee',
      unreadNotesCount: 0,
    },
    {
      id: 'res-006',
      prestataireId: '2',
      prestataireName: 'Starlight Pulse',
      prestatairePhoto: '/images/prestataires/pop-rock-band.jpg',
      category: 'Musique',
      status: 'annulee',
      unreadNotesCount: 0,
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
