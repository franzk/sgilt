import type { NoteAuthor, ReservationDetail, ReservationNote } from '~/types/event'

// ── Auteurs réutilisables ──────────────────────────────────────────────────────
const JULIE: NoteAuthor = {
  id: 'client-1',
  name: 'Julie M.',
  role: 'client',
  photo: 'https://picsum.photos/seed/julie-m/64/64',
}

const DJ: NoteAuthor = {
  id: 'presta-3',
  name: 'DJ Animation',
  role: 'prestataire',
  photo: '/images/prestataires/dj-animation.jpg',
}

const LEO: NoteAuthor = {
  id: 'presta-16',
  name: 'Léo Clairmont',
  role: 'prestataire',
  photo: '/images/prestataires/photographe-mariage.jpg',
}

const ECLAT: NoteAuthor = {
  id: 'presta-6',
  name: 'Éclat Gourmet',
  role: 'prestataire',
  photo: '/images/prestataires/traiteur-gourmet.jpg',
}

const MOCK_RESERVATIONS: ReservationDetail[] = [
  {
    id: 'res-001',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'confirmee',
    unreadNotesCount: 3,
    notes: [
      {
        id: 'note-001',
        author: JULIE,
        content:
          'Lieu : Domaine des Étoiles, 5 rue des Lilas, Obernai. Accès prestataires par le portail nord (code : 2714).',
        createdAt: '2026-02-10T10:15:00.000Z',
      },
      {
        id: 'note-002',
        author: JULIE,
        content:
          'Horaires : cocktail 17h–19h (jazz lounge), soirée dansante 20h–00h (électro). Installation dès 14h.',
        createdAt: '2026-02-10T14:30:00.000Z',
      },
      {
        id: 'note-003',
        author: DJ,
        content:
          'Formule retenue : set DJ 5h avec sonorisation complète + éclairage d\'ambiance. Tarif confirmé : 1 800 € TTC.',
        createdAt: '2026-02-12T09:00:00.000Z',
      },
      {
        id: 'note-004',
        author: DJ,
        content:
          'Besoins techniques : 2 prises 220V (16A), surface min. 10 m², accès véhicule utilitaire pour déchargement.',
        createdAt: '2026-02-12T11:45:00.000Z',
      },
      {
        id: 'note-005',
        author: JULIE,
        content:
          'Acompte de 30 % versé le 14/02/2026. Solde à régler 15 jours avant l\'événement.',
        createdAt: '2026-02-14T16:20:00.000Z',
      },
    ],
  },
  {
    id: 'res-002',
    prestataireId: '16',
    prestataireName: 'Léo Clairmont Photographe',
    prestatairePhoto: '/images/prestataires/photographe-mariage.jpg',
    category: 'Photo',
    status: 'recontactee',
    unreadNotesCount: 0,
    notes: [
      {
        id: 'note-006',
        author: JULIE,
        content:
          'Date : 14 septembre 2026. Lieu : Château de Pourtalès, Strasbourg. Cérémonie 15h, vin d\'honneur 17h, dîner 19h30.',
        createdAt: '2026-01-20T10:00:00.000Z',
      },
      {
        id: 'note-007',
        author: LEO,
        content:
          'Formule : reportage complet cérémonie + réception (8h). Style photojournalisme naturel. Tarif indicatif : 1 950 € TTC — devis en attente de validation.',
        createdAt: '2026-01-21T08:30:00.000Z',
      },
    ],
  },
  {
    id: 'res-003',
    prestataireId: '6',
    prestataireName: 'Éclat Gourmet',
    prestatairePhoto: '/images/prestataires/traiteur-gourmet.jpg',
    category: 'Restauration',
    status: 'envoyee',
    unreadNotesCount: 1,
    notes: [
      {
        id: 'note-008',
        author: JULIE,
        content:
          '120 couverts. Menu gastronomique 4 services + option végétarienne (15 personnes). Allergies notifiées : fruits à coque (3), gluten (1).',
        createdAt: '2026-02-01T14:00:00.000Z',
      },
    ],
  },
  {
    id: 'res-004',
    prestataireId: '1',
    prestataireName: 'Gypsy Reed Ensemble',
    prestatairePhoto: '/images/prestataires/jazz-band.jpg',
    category: 'Musique',
    status: 'brouillon',
    unreadNotesCount: 0,
    notes: [],
  },
  {
    id: 'res-005',
    prestataireId: '18',
    prestataireName: 'Studio Photo Mobile',
    prestatairePhoto: '/images/prestataires/studio-photo-mobile.jpg',
    category: 'Photo',
    status: 'cloturee',
    unreadNotesCount: 0,
    notes: [],
  },
  {
    id: 'res-006',
    prestataireId: '2',
    prestataireName: 'Starlight Pulse',
    prestatairePhoto: '/images/prestataires/pop-rock-band.jpg',
    category: 'Musique',
    status: 'annulee',
    unreadNotesCount: 0,
    notes: [],
  },
]

export const ReservationMockService = {
  async getById(reservationId: string): Promise<ReservationDetail | null> {
    await new Promise((r) => setTimeout(r, 200))
    return MOCK_RESERVATIONS.find((r) => r.id === reservationId) ?? null
  },

  async addNote(reservationId: string, content: string): Promise<ReservationNote> {
    await new Promise((r) => setTimeout(r, 300))
    const note: ReservationNote = {
      id: `note-${Date.now()}`,
      author: JULIE,
      content,
      createdAt: new Date().toISOString(),
    }
    return note
  },
}
