import type { NoteAuthor, ReservationDetail, ReservationDocument, ReservationNote } from '~/types/event'

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

const GYPSY: NoteAuthor = {
  id: 'presta-1',
  name: 'Gypsy Reed Ensemble',
  role: 'prestataire',
  photo: '/images/prestataires/jazz-band.jpg',
}

const STUDIO: NoteAuthor = {
  id: 'presta-18',
  name: 'Studio Photo Mobile',
  role: 'prestataire',
  photo: '/images/prestataires/studio-photo-mobile.jpg',
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
    documents: [
      {
        id: 'doc-001',
        name: 'Contrat_DJ_Animation_14sept2026.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedBy: JULIE,
        uploadedAt: '2026-02-10T10:30:00.000Z',
      },
      {
        id: 'doc-002',
        name: 'Rider_technique_et_fiche_besoins.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedBy: DJ,
        uploadedAt: '2026-02-12T12:00:00.000Z',
      },
      {
        id: 'doc-003',
        name: 'Photo_salle_cocktail_Domaine_Etoiles.jpg',
        fileType: 'image',
        url: '#',
        uploadedBy: DJ,
        uploadedAt: '2026-02-18T09:15:00.000Z',
      },
      {
        id: 'doc-004',
        name: 'Facture_acompte_30pct_DJ_Animation.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedBy: JULIE,
        uploadedAt: '2026-02-28T17:00:00.000Z',
      },
    ] satisfies ReservationDocument[],
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
    documents: [],
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
    documents: [],
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
    documents: [],
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
    documents: [],
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
    documents: [],
  },

  // ── Événement 2 : Anniversaire 50 ans de Marc ─────────────────────────────
  {
    id: 'res-010',
    prestataireId: '1',
    prestataireName: 'Gypsy Reed Ensemble',
    prestatairePhoto: '/images/prestataires/jazz-band.jpg',
    category: 'Musique',
    status: 'confirmee',
    unreadNotesCount: 2,
    notes: [
      {
        id: 'note-010',
        author: JULIE,
        content:
          'Lieu : Villa Florentine, 25 montée Saint-Barthélemy, Lyon 5e. Entrée prestataires côté nord. Installation possible dès 16h.',
        createdAt: '2026-03-01T10:30:00.000Z',
      },
      {
        id: 'note-011',
        author: GYPSY,
        content:
          'Formule retenue : trio jazz manouche 3h (19h–22h). Répertoire standards jazz, bossa nova et swing. Tarif confirmé : 1 200 € TTC.',
        createdAt: '2026-03-03T09:15:00.000Z',
      },
      {
        id: 'note-012',
        author: JULIE,
        content:
          'Acompte de 30 % (360 €) réglé par virement le 05/03/2026. Référence virement : MARC50-GYPSY.',
        createdAt: '2026-03-05T16:00:00.000Z',
      },
      {
        id: 'note-013',
        author: GYPSY,
        content:
          'Besoins techniques : 2 retours de scène 10", 1 prise secteur 220V (16A), surface min. 6 m². Pas de sonorisation lourde requise — acoustique naturelle.',
        createdAt: '2026-03-07T11:00:00.000Z',
      },
    ],
    documents: [],
  },
  {
    id: 'res-011',
    prestataireId: '6',
    prestataireName: 'Éclat Gourmet',
    prestatairePhoto: '/images/prestataires/traiteur-gourmet.jpg',
    category: 'Restauration',
    status: 'confirmee',
    unreadNotesCount: 1,
    notes: [
      {
        id: 'note-014',
        author: JULIE,
        content:
          '60 couverts. Cocktail dînatoire : 8 pièces chaudes + 8 pièces froides par personne. 6 personnes végétariennes. Allergie déclarée : lactose (2 personnes).',
        createdAt: '2026-02-28T14:00:00.000Z',
      },
      {
        id: 'note-015',
        author: ECLAT,
        content:
          'Formule cocktail dînatoire confirmée : 78 € HT / pers., soit 4 680 € HT pour 60 couverts. Acompte 40 % à la signature — devis envoyé par email.',
        createdAt: '2026-03-02T08:45:00.000Z',
      },
      {
        id: 'note-016',
        author: JULIE,
        content:
          'Devis signé et retourné le 10/03/2026. Acompte de 1 872 € réglé par CB. Solde dû 10 jours avant l\'événement.',
        createdAt: '2026-03-10T17:30:00.000Z',
      },
    ],
    documents: [],
  },
  {
    id: 'res-012',
    prestataireId: '18',
    prestataireName: 'Studio Photo Mobile',
    prestatairePhoto: '/images/prestataires/studio-photo-mobile.jpg',
    category: 'Photo',
    status: 'envoyee',
    unreadNotesCount: 0,
    notes: [
      {
        id: 'note-017',
        author: JULIE,
        content:
          'Événement : anniversaire 50 ans, 23 mai 2026, 19h–23h. Lieu : Villa Florentine, Lyon 5e. Thème festif, décoration rétro années 80.',
        createdAt: '2026-03-12T11:00:00.000Z',
      },
      {
        id: 'note-018',
        author: STUDIO,
        content:
          'Prestation disponible : photobooth autonome 4h avec tirages instantanés illimités + clé USB photos numériques remise en fin de soirée. Tarif indicatif : 750 € TTC.',
        createdAt: '2026-03-13T09:30:00.000Z',
      },
      {
        id: 'note-019',
        author: JULIE,
        content:
          'Option fond décor personnalisé "Marc 50 ans" demandée. En attente du devis définitif incluant cette option.',
        createdAt: '2026-03-14T15:20:00.000Z',
      },
    ],
    documents: [],
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
