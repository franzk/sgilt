import type {
  NoteAuthor,
  EventDetail,
  ReservationDetail,
  ReservationDocument,
  ReservationNote,
  ProDemandeDetail,
} from '~/types/event'

// ── Auteurs réutilisables ──────────────────────────────────────────────────────

export const JULIE: NoteAuthor = {
  id: 'client-1',
  name: 'Julie M.',
  role: 'client',
  photo: 'https://picsum.photos/seed/julie-m/64/64',
}

export const SOPHIE: NoteAuthor = {
  id: 'client-2',
  name: 'Sophie L.',
  role: 'client',
  photo: 'https://picsum.photos/seed/sophie-l/64/64',
}

export const MARC_D: NoteAuthor = {
  id: 'client-3',
  name: 'Marc D.',
  role: 'client',
}

export const EMILIE_D: NoteAuthor = {
  id: 'client-4',
  name: 'Émilie D.',
  role: 'client',
}

export const THOMAS_B: NoteAuthor = {
  id: 'client-5',
  name: 'Thomas B.',
  role: 'client',
}

export const CLAIRE_R: NoteAuthor = {
  id: 'client-6',
  name: 'Claire R.',
  role: 'client',
}

export const AIDES: NoteAuthor = {
  id: 'client-7',
  name: 'Association AIDES',
  role: 'client',
}

export const DJ: NoteAuthor = {
  id: 'presta-3',
  name: 'DJ Animation',
  role: 'prestataire',
  photo: '/images/prestataires/dj-animation.jpg',
}

export const LEO: NoteAuthor = {
  id: 'presta-16',
  name: 'Léo Clairmont',
  role: 'prestataire',
  photo: '/images/prestataires/photographe-mariage.jpg',
}

export const ECLAT: NoteAuthor = {
  id: 'presta-6',
  name: 'Éclat Gourmet',
  role: 'prestataire',
  photo: '/images/prestataires/traiteur-gourmet.jpg',
}

export const GYPSY: NoteAuthor = {
  id: 'presta-1',
  name: 'Gypsy Reed Ensemble',
  role: 'prestataire',
  photo: '/images/prestataires/jazz-band.jpg',
}

export const STUDIO: NoteAuthor = {
  id: 'presta-18',
  name: 'Studio Photo Mobile',
  role: 'prestataire',
  photo: '/images/prestataires/studio-photo-mobile.jpg',
}

export const STARLIGHT: NoteAuthor = {
  id: 'presta-2',
  name: 'Starlight Pulse',
  role: 'prestataire',
  photo: '/images/prestataires/pop-rock-band.jpg',
}

// ── Réservations (côté client) ────────────────────────────────────────────────

export const MOCK_RESERVATIONS: ReservationDetail[] = [
  // ── evt-001 : Mariage de Julie & Thomas ─────────────────────────────────────
  {
    id: 'res-001',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'confirmee',
    unreadNotesCount: 3,
    urgencyLevel: 2,
    ligneContextuelle: 'Événement dans 178 jours',
    notes: [
      {
        id: 'note-m001',
        author: JULIE,
        content:
          'Mariage 14 sept. 2026, Obernai. Cocktail jazz 17h–19h + soirée dansante électro 20h–00h. 120 invités. Budget : 1 500–2 000 € TTC.',
        createdAt: new Date('2026-02-01T09:30:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'note-001',
        author: JULIE,
        content:
          'Lieu : Domaine des Étoiles, 5 rue des Lilas, Obernai. Accès prestataires par le portail nord (code : 2714).',
        createdAt: new Date('2026-02-10T10:15:00.000Z'),
      },
      {
        id: 'note-002',
        author: JULIE,
        content: 'Installation possible dès 14h. Cocktail 17h–19h, soirée dansante 20h–00h.',
        createdAt: new Date('2026-02-10T14:30:00.000Z'),
      },
      {
        id: 'note-003',
        author: DJ,
        content:
          "Formule retenue : set DJ 5h avec sonorisation complète + éclairage d'ambiance. Tarif confirmé : 1 800 € TTC.",
        createdAt: new Date('2026-02-12T09:00:00.000Z'),
      },
      {
        id: 'note-004',
        author: DJ,
        content:
          'Besoins techniques : 2 prises 220V (16A), surface min. 10 m², accès véhicule utilitaire pour déchargement.',
        createdAt: new Date('2026-02-12T11:45:00.000Z'),
      },
      {
        id: 'note-005',
        author: JULIE,
        content: "Acompte de 30 % versé le 14/02/2026. Solde à régler 15 jours avant l'événement.",
        createdAt: new Date('2026-02-14T16:20:00.000Z'),
      },
      {
        id: 'note-priv-001',
        author: DJ,
        content:
          'Client très exigeant sur le répertoire. Prévoir une sélection de musiques sans paroles pour le repas.',
        createdAt: new Date('2026-02-15T09:00:00.000Z'),
        isPersonal: true,
      },
      {
        id: 'note-priv-002',
        author: DJ,
        content:
          'Penser à apporter le câble XLR de secours — la sono du Domaine des Étoiles est vieillissante.',
        createdAt: new Date('2026-02-20T18:30:00.000Z'),
        isPersonal: true,
      },
    ] satisfies ReservationNote[],
    documents: [
      {
        id: 'doc-001',
        name: 'Contrat_DJ_Animation_14sept2026.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedBy: JULIE,
        uploadedAt: new Date('2026-02-10T10:30:00.000Z'),
      },
      {
        id: 'doc-002',
        name: 'Rider_technique_et_fiche_besoins.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedBy: DJ,
        uploadedAt: new Date('2026-02-12T12:00:00.000Z'),
      },
      {
        id: 'doc-003',
        name: 'Photo_salle_cocktail_Domaine_Etoiles.jpg',
        fileType: 'image',
        url: '#',
        uploadedBy: DJ,
        uploadedAt: new Date('2026-02-18T09:15:00.000Z'),
      },
      {
        id: 'doc-004',
        name: 'Facture_acompte_30pct_DJ_Animation.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedBy: JULIE,
        uploadedAt: new Date('2026-02-28T17:00:00.000Z'),
      },
    ] satisfies ReservationDocument[],
  },
  {
    id: 'res-002',
    prestataireId: '16',
    prestataireName: 'Léo Clairmont Photographe',
    prestatairePhoto: '/images/prestataires/photographe-mariage.jpg',
    category: 'Photo',
    status: 'en_discussion',
    unreadNotesCount: 0,
    urgencyLevel: 3,
    ligneContextuelle: 'En négociation depuis 58 jours',
    notes: [
      {
        id: 'note-006',
        author: JULIE,
        content:
          "Mariage 14 sept. 2026, Strasbourg. Cérémonie 15h, vin d'honneur 17h, dîner 19h30.",
        createdAt: new Date('2026-01-20T10:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'note-007',
        author: LEO,
        content:
          'Formule : reportage complet cérémonie + réception (8h). Style photojournalisme naturel. Tarif indicatif : 1 950 € TTC — devis en attente de validation.',
        createdAt: new Date('2026-01-21T08:30:00.000Z'),
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
    status: 'nouvelle',
    unreadNotesCount: 1,
    urgencyLevel: 2,
    ligneContextuelle: 'Reçue il y a 47 jours',
    notes: [
      {
        id: 'note-008',
        author: JULIE,
        content:
          '120 couverts. Menu gastronomique 4 services + option végétarienne (15 personnes). Allergies notifiées : fruits à coque (3), gluten (1).',
        createdAt: new Date('2026-02-01T14:00:00.000Z'),
        isMessageInitial: true,
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
    status: 'nouvelle',
    unreadNotesCount: 0,
    urgencyLevel: 1,
    ligneContextuelle: 'En attente de réponse',
    notes: [],
    documents: [],
  },
  {
    id: 'res-005',
    prestataireId: '18',
    prestataireName: 'Studio Photo Mobile',
    prestatairePhoto: '/images/prestataires/studio-photo-mobile.jpg',
    category: 'Photo',
    status: 'refusee',
    unreadNotesCount: 0,
    urgencyLevel: 1,
    ligneContextuelle: 'Refusée le 12 mars',
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
    urgencyLevel: 1,
    ligneContextuelle: 'Annulée le 8 mars',
    notes: [],
    documents: [],
  },

  // ── evt-002 : Anniversaire 50 ans de Marc ────────────────────────────────────
  {
    id: 'res-010',
    prestataireId: '1',
    prestataireName: 'Gypsy Reed Ensemble',
    prestatairePhoto: '/images/prestataires/jazz-band.jpg',
    category: 'Musique',
    status: 'confirmee',
    unreadNotesCount: 2,
    urgencyLevel: 1,
    ligneContextuelle: 'Événement dans 63 jours',
    notes: [
      {
        id: 'note-009',
        author: SOPHIE,
        content:
          'Anniversaire 50 ans, 23 mai 2026, Lyon. 60 invités. Jazz manouche recherché, 19h–22h. Soirée surprise — Marc ne doit pas être informé.',
        createdAt: new Date('2026-02-20T09:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'note-010',
        author: SOPHIE,
        content:
          'Lieu : Villa Florentine, 25 montée Saint-Barthélemy, Lyon 5e. Entrée prestataires côté nord. Installation possible dès 16h.',
        createdAt: new Date('2026-03-01T10:30:00.000Z'),
      },
      {
        id: 'note-011',
        author: GYPSY,
        content:
          'Formule retenue : trio jazz manouche 3h (19h–22h). Répertoire standards jazz, bossa nova et swing. Tarif confirmé : 1 200 € TTC.',
        createdAt: new Date('2026-03-03T09:15:00.000Z'),
      },
      {
        id: 'note-012',
        author: SOPHIE,
        content:
          'Acompte de 30 % (360 €) réglé par virement le 05/03/2026. Référence virement : MARC50-GYPSY.',
        createdAt: new Date('2026-03-05T16:00:00.000Z'),
      },
      {
        id: 'note-013',
        author: GYPSY,
        content:
          'Besoins techniques : 2 retours de scène 10", 1 prise secteur 220V (16A), surface min. 6 m². Acoustique naturelle — pas de sonorisation lourde.',
        createdAt: new Date('2026-03-07T11:00:00.000Z'),
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
    urgencyLevel: 2,
    ligneContextuelle: 'Événement dans 63 jours',
    notes: [
      {
        id: 'note-014',
        author: SOPHIE,
        content:
          '60 couverts. Cocktail dînatoire : 8 pièces chaudes + 8 pièces froides par personne. 6 personnes végétariennes. Allergie déclarée : lactose (2 personnes).',
        createdAt: new Date('2026-02-28T14:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'note-015',
        author: ECLAT,
        content:
          'Formule cocktail dînatoire confirmée : 78 € HT / pers., soit 4 680 € HT pour 60 couverts. Acompte 40 % à la signature — devis envoyé par email.',
        createdAt: new Date('2026-03-02T08:45:00.000Z'),
      },
      {
        id: 'note-016',
        author: SOPHIE,
        content:
          "Devis signé et retourné le 10/03/2026. Acompte de 1 872 € réglé par CB. Solde dû 10 jours avant l'événement.",
        createdAt: new Date('2026-03-10T17:30:00.000Z'),
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
    status: 'nouvelle',
    unreadNotesCount: 0,
    urgencyLevel: 2,
    ligneContextuelle: 'Reçue il y a 8 jours',
    notes: [
      {
        id: 'note-017',
        author: SOPHIE,
        content:
          'Anniversaire 50 ans, 23 mai 2026, Lyon. Thème festif rétro années 80. 60 invités, 19h–23h.',
        createdAt: new Date('2026-03-12T11:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'note-018',
        author: STUDIO,
        content:
          'Photobooth autonome 4h : tirages instantanés illimités + clé USB photos numériques remise en fin de soirée. Tarif indicatif : 750 € TTC.',
        createdAt: new Date('2026-03-13T09:30:00.000Z'),
      },
      {
        id: 'note-019',
        author: SOPHIE,
        content:
          'Option fond décor personnalisé "Marc 50 ans" demandée. En attente du devis définitif incluant cette option.',
        createdAt: new Date('2026-03-14T15:20:00.000Z'),
      },
    ],
    documents: [],
  },
]

// ── Réservations pro uniquement (pas de miroir côté client) ───────────────────

const PRO_ONLY_RESERVATIONS: ReservationDetail[] = [
  {
    id: 'pro-002',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'nouvelle',
    unreadNotesCount: 1,
    urgencyLevel: 1,
    ligneContextuelle: 'Reçue il y a 2h',
    notes: [
      {
        id: 'pn-201',
        author: SOPHIE,
        content:
          'Anniversaire surprise, 23 mai 2026, Lyon. 60 invités. Jazz manouche recherché, 19h–22h.',
        createdAt: new Date('2026-03-18T11:00:00.000Z'),
        isMessageInitial: true,
      },
    ],
    documents: [],
  },
  {
    id: 'pro-003',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Animation',
    status: 'en_discussion',
    unreadNotesCount: 0,
    urgencyLevel: 3,
    ligneContextuelle: 'En négociation depuis 5 jours',
    notes: [
      {
        id: 'pn-301',
        author: MARC_D,
        content:
          'Soirée entreprise, 12 avril 2026, Strasbourg. 80 personnes. Animation musicale sonorisée 3h. Budget : 1 200–1 800 €.',
        createdAt: new Date('2026-03-07T14:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'pn-302',
        author: DJ,
        content:
          "Disponible le 12 avril. Formule Soirée Pro : sonorisation + 2 sets 1h30. Devis en cours d'envoi.",
        createdAt: new Date('2026-03-10T09:00:00.000Z'),
      },
      {
        id: 'pn-priv-301',
        author: DJ,
        content:
          'Négociation en cours sur le tarif. Client prêt à signer si on baisse de 150 €. À voir selon la marge.',
        createdAt: new Date('2026-03-15T10:00:00.000Z'),
        isPersonal: true,
      },
    ],
    documents: [],
  },
  {
    id: 'pro-004',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'nouvelle',
    unreadNotesCount: 1,
    urgencyLevel: 5,
    ligneContextuelle: 'Reçue il y a 3 jours',
    notes: [
      {
        id: 'pn-401',
        author: EMILIE_D,
        content:
          'Mariage, 6 juin 2026, Colmar. 150 invités. Mix pop-rock et électro. Prestation 19h–2h. Délai de réponse souhaité : rapide.',
        createdAt: new Date('2026-03-17T16:45:00.000Z'),
        isMessageInitial: true,
      },
    ],
    documents: [],
  },
  {
    id: 'pro-005',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'confirmee',
    unreadNotesCount: 0,
    urgencyLevel: 6,
    ligneContextuelle: 'Événement dans 6 jours',
    notes: [
      {
        id: 'pn-501',
        author: THOMAS_B,
        content:
          'Cocktail lancement produit, 4 avril 2026, Mulhouse. 40 personnes. Ambiance lounge, fond musical 2h. Budget : 600–800 €.',
        createdAt: new Date('2026-03-01T10:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'pn-502',
        author: DJ,
        content: 'Formule lounge 2h confirmée à 750 € TTC. Contrat signé, acompte reçu.',
        createdAt: new Date('2026-03-05T09:30:00.000Z'),
      },
    ],
    documents: [],
  },
  {
    id: 'pro-006',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'annulee',
    unreadNotesCount: 0,
    urgencyLevel: 1,
    ligneContextuelle: 'Annulée le 8 mars',
    notes: [
      {
        id: 'pn-601',
        author: CLAIRE_R,
        content: "Soirée privée, 15 mars 2026, Strasbourg. 30 personnes. Musique d'ambiance 3h.",
        createdAt: new Date('2026-02-20T14:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'pn-602',
        author: CLAIRE_R,
        content: 'Événement annulé par le client le 08/03/2026.',
        createdAt: new Date('2026-03-08T11:00:00.000Z'),
      },
    ],
    documents: [],
  },
  {
    id: 'pro-007',
    prestataireId: '3',
    prestataireName: 'DJ Animation',
    prestatairePhoto: '/images/prestataires/dj-animation.jpg',
    category: 'Musique',
    status: 'refusee',
    unreadNotesCount: 0,
    urgencyLevel: 1,
    ligneContextuelle: 'Refusée le 12 mars',
    notes: [
      {
        id: 'pn-701',
        author: AIDES,
        content:
          'Gala humanitaire, 20 juin 2026, Nancy. 200 personnes. Fond musical classique (dîner) puis animation festive (soirée dansante). Prestation 4h.',
        createdAt: new Date('2026-01-15T10:00:00.000Z'),
        isMessageInitial: true,
      },
    ],
    documents: [],
  },
  {
    id: 'pro-008',
    prestataireId: '16',
    prestataireName: 'Léo Clairmont Photographe',
    prestatairePhoto: '/images/prestataires/photographe-mariage.jpg',
    category: 'Photo',
    status: 'realisee',
    unreadNotesCount: 0,
    urgencyLevel: 1,
    ligneContextuelle: 'Réalisée le 14 décembre 2025',
    notes: [
      {
        id: 'pn-801',
        author: JULIE,
        content:
          'Mariage de Thomas & Clara, 14 décembre 2025, Metz. Reportage complet cérémonie + soirée. Très belle collaboration.',
        createdAt: new Date('2025-11-10T10:00:00.000Z'),
        isMessageInitial: true,
      },
      {
        id: 'pn-802',
        author: LEO,
        content:
          'Prestation effectuée. Album photo livré le 20/01/2026. Solde réglé. Merci pour la confiance.',
        createdAt: new Date('2026-01-20T09:00:00.000Z'),
      },
    ],
    documents: [],
  },
]

// ── Événements (côté client) ──────────────────────────────────────────────────

export const MOCK_EVENTS: EventDetail[] = [
  {
    id: 'evt-001',
    title: 'Mariage de Julie & Thomas',
    date: new Date('2026-09-14'),
    eventType: 'mariage',
    ambiance: 'chic',
    ville: 'Strasbourg',
    nbInvites: '120',
    coverImage:
      'https://images.unsplash.com/photo-1519741497674-611481863552?w=600&auto=format&fit=crop',
    sharedNote: "Cérémonie à 14h, vin d'honneur à 16h. Accès PMR prévu.",
    sharedNoteUpdatedAt: new Date('2026-02-20T11:30:00.000Z'),
    phrase: 'Votre mariage prend forme ✨',
    phraseSubtitle: "Plus que 87 jours — vos prestataires s'organisent",
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
        status: 'en_discussion',
        unreadNotesCount: 0,
      },
      {
        id: 'res-003',
        prestataireId: '6',
        prestataireName: 'Éclat Gourmet',
        prestatairePhoto: '/images/prestataires/traiteur-gourmet.jpg',
        category: 'Restauration',
        status: 'nouvelle',
        unreadNotesCount: 1,
      },
      {
        id: 'res-004',
        prestataireId: '1',
        prestataireName: 'Gypsy Reed Ensemble',
        prestatairePhoto: '/images/prestataires/jazz-band.jpg',
        category: 'Musique',
        status: 'nouvelle',
        unreadNotesCount: 0,
      },
      {
        id: 'res-005',
        prestataireId: '18',
        prestataireName: 'Studio Photo Mobile',
        prestatairePhoto: '/images/prestataires/studio-photo-mobile.jpg',
        category: 'Photo',
        status: 'refusee',
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
  },
  {
    id: 'evt-002',
    title: 'Anniversaire 50 ans de Marc',
    date: new Date('2026-05-23'),
    eventType: 'anniversaire',
    ambiance: 'festif',
    ville: 'Lyon',
    nbInvites: '60',
    sharedNote:
      'Soirée surprise — Marc ne doit pas être informé avant le 23 mai. Accès prestataires dès 16h (entrée de service, côté nord). Tenue de soirée exigée.',
    sharedNoteUpdatedAt: new Date('2026-03-08T17:45:00.000Z'),
    phrase: "Tout s'organise pour la fête 🎉",
    phraseSubtitle: '2 prestataires confirmés, 1 en attente de réponse',
    journal: [
      {
        id: 'log-005',
        date: new Date('2026-02-14T11:20:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Titre', avant: null, apres: 'Anniversaire 50 ans de Marc' },
          { champ: 'Date', avant: null, apres: '23 mai 2026' },
          { champ: 'Type', avant: null, apres: 'Anniversaire' },
          { champ: 'Ville', avant: null, apres: 'Lyon' },
          { champ: "Nombre d'invités", avant: null, apres: '60' },
        ],
      },
    ],
    reservations: [
      {
        id: 'res-010',
        prestataireId: '1',
        prestataireName: 'Gypsy Reed Ensemble',
        prestatairePhoto: '/images/prestataires/jazz-band.jpg',
        category: 'Musique',
        status: 'confirmee',
        unreadNotesCount: 0,
      },
      {
        id: 'res-011',
        prestataireId: '6',
        prestataireName: 'Éclat Gourmet',
        prestatairePhoto: '/images/prestataires/traiteur-gourmet.jpg',
        category: 'Restauration',
        status: 'confirmee',
        unreadNotesCount: 1,
      },
      {
        id: 'res-012',
        prestataireId: '18',
        prestataireName: 'Studio Photo Mobile',
        prestatairePhoto: '/images/prestataires/studio-photo-mobile.jpg',
        category: 'Photo',
        status: 'nouvelle',
        unreadNotesCount: 0,
      },
    ],
  },
]

// ── Événements pro uniquement ─────────────────────────────────────────────────

const PRO_ONLY_EVENTS: EventDetail[] = [
  {
    id: 'evt-pro-002',
    title: 'Anniversaire 50 ans de Marc',
    date: new Date('2026-05-23'),
    eventType: 'anniversaire',
    coverImage:
      'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=600&auto=format&fit=crop',
    ville: 'Lyon',
    nbInvites: '60',
    sharedNote: '',
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-201',
        date: new Date('2026-03-18T11:00:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Anniversaire 50 ans de Marc' },
        ],
      },
    ],
    reservations: [],
  },
  {
    id: 'evt-pro-003',
    title: 'Soirée entreprise Alsace Tech',
    date: new Date('2026-04-12'),
    eventType: 'entreprise',
    coverImage:
      'https://images.unsplash.com/photo-1511578314322-379afb476865?w=600&auto=format&fit=crop',
    ville: 'Strasbourg',
    nbInvites: '80',
    sharedNote:
      'Palais des Fêtes de Strasbourg — salle Erasme. Installation prestataires dès 17h. Scène côté jardin, accès parking sous-terrain.',
    sharedNoteUpdatedAt: new Date('2026-03-10T09:30:00.000Z'),
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-301',
        date: new Date('2026-03-07T14:00:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Soirée entreprise Alsace Tech' },
        ],
      },
      {
        id: 'pj-302',
        date: new Date('2026-03-10T09:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Nouvelle', apres: 'En discussion' }],
      },
    ],
    reservations: [],
  },
  {
    id: 'evt-pro-004',
    title: 'Mariage Dupont',
    date: new Date('2026-06-06'),
    eventType: 'mariage',
    coverImage:
      'https://images.unsplash.com/photo-1519741497674-611481863552?w=600&auto=format&fit=crop',
    ville: 'Colmar',
    nbInvites: '150',
    sharedNote: '',
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-401',
        date: new Date('2026-03-17T16:45:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Mariage Dupont' },
        ],
      },
    ],
    reservations: [],
  },
  {
    id: 'evt-pro-005',
    title: 'Cocktail Lancement Produit',
    date: new Date('2026-04-04'),
    eventType: 'entreprise',
    coverImage:
      'https://images.unsplash.com/photo-1511578314322-379afb476865?w=600&auto=format&fit=crop',
    ville: 'Mulhouse',
    nbInvites: '40',
    sharedNote:
      'Espace événementiel Le Totem, Mulhouse. Accès prestataires entrée latérale (rue de la Paix). Sono légère suffisante, pas de scène.',
    sharedNoteUpdatedAt: new Date('2026-03-05T10:00:00.000Z'),
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-501',
        date: new Date('2026-03-01T10:00:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Cocktail Lancement Produit' },
        ],
      },
      {
        id: 'pj-502',
        date: new Date('2026-03-03T14:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Nouvelle', apres: 'En discussion' }],
      },
      {
        id: 'pj-503',
        date: new Date('2026-03-05T09:30:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'En discussion', apres: 'Confirmée' }],
      },
    ],
    reservations: [],
  },
  {
    id: 'evt-pro-006',
    title: 'Soirée privée Strasbourg',
    date: new Date('2026-03-15'),
    eventType: 'soiree',
    coverImage:
      'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop',
    ville: 'Strasbourg',
    nbInvites: '30',
    sharedNote: '',
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-601',
        date: new Date('2026-02-20T14:00:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Soirée privée Strasbourg' },
        ],
      },
      {
        id: 'pj-602',
        date: new Date('2026-03-08T11:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Nouvelle', apres: 'Annulée' }],
      },
    ],
    reservations: [],
  },
  {
    id: 'evt-pro-007',
    title: 'Gala Humanitaire Grand Est',
    date: new Date('2026-06-20'),
    eventType: 'autre',
    coverImage:
      'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop',
    ville: 'Nancy',
    nbInvites: '200',
    sharedNote:
      'Opéra national de Lorraine, Nancy. Accès prestataires rue Sainte-Catherine. Répétition de balance autorisée dès 15h. Tenue correcte exigée.',
    sharedNoteUpdatedAt: new Date('2026-01-20T09:00:00.000Z'),
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-701',
        date: new Date('2026-01-15T10:00:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Gala Humanitaire Grand Est' },
        ],
      },
      {
        id: 'pj-702',
        date: new Date('2026-01-18T11:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Nouvelle', apres: 'En discussion' }],
      },
      {
        id: 'pj-703',
        date: new Date('2026-01-22T14:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'En discussion', apres: 'Confirmée' }],
      },
      {
        id: 'pj-704',
        date: new Date('2026-06-21T10:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Confirmée', apres: 'Refusée' }],
      },
    ],
    reservations: [],
  },
  {
    id: 'evt-pro-008',
    title: 'Mariage Thomas & Clara',
    date: new Date('2025-12-14'),
    eventType: 'mariage',
    coverImage:
      'https://images.unsplash.com/photo-1519741497674-611481863552?w=600&auto=format&fit=crop',
    ville: 'Metz',
    nbInvites: '100',
    sharedNote: '',
    phrase: '',
    phraseSubtitle: '',
    journal: [
      {
        id: 'pj-801',
        date: new Date('2025-11-10T10:00:00.000Z'),
        isCreation: true,
        modifications: [
          { champ: 'Statut', avant: null, apres: 'Nouvelle' },
          { champ: 'Événement', avant: null, apres: 'Mariage Thomas & Clara' },
        ],
      },
      {
        id: 'pj-802',
        date: new Date('2025-11-15T14:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Nouvelle', apres: 'En discussion' }],
      },
      {
        id: 'pj-803',
        date: new Date('2025-11-20T10:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'En discussion', apres: 'Confirmée' }],
      },
      {
        id: 'pj-804',
        date: new Date('2025-12-15T10:00:00.000Z'),
        isCreation: false,
        modifications: [{ champ: 'Statut', avant: 'Confirmée', apres: 'Réalisée' }],
      },
    ],
    reservations: [],
  },
]

// ── Demandes pro (réservation + événement associé) ────────────────────────────

export const PRO_DEMANDES: ProDemandeDetail[] = [
  // ── nouvelle — urgencyLevel desc ──────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[2],
    event: PRO_ONLY_EVENTS[2],
    progressType: 'deadline',
    progressValue: 0.15,
    phraseInfoState: 'Il reste <strong>4h</strong> pour répondre',
    clientInfo: {
      firstName: 'Émilie',
      phone: '+33 6 34 56 78 90',
      email: 'emilie.dubois@email.fr',
    },
  }, // nouvelle — urgencyLevel 5
  {
    ...PRO_ONLY_RESERVATIONS[0],
    event: PRO_ONLY_EVENTS[0],
    progressType: 'deadline',
    progressValue: 0.85,
    phraseInfoState: 'Il reste <strong>36h</strong> pour répondre',
    clientInfo: {
      firstName: 'Sophie',
      phone: '+33 6 12 34 56 78',
      email: 'sophie.lambert@email.fr',
    },
  }, // nouvelle — urgencyLevel 1
  // ── en_discussion ─────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[1],
    event: PRO_ONLY_EVENTS[1],
    progressType: 'duration',
    progressValue: 0.45,
    phraseInfoState: 'En négociation depuis <strong>5 jours</strong>',
    clientInfo: { firstName: 'Marc', phone: '+33 6 23 45 67 89', email: 'marc.dupont@email.fr' },
  }, // en_discussion — urgencyLevel 3
  // ── confirmee — urgencyLevel desc ─────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[3],
    event: PRO_ONLY_EVENTS[3],
    progressType: 'temporal',
    progressValue: 0.92,
    phraseInfoState: 'Événement dans <strong>6 jours</strong>',
    clientInfo: {
      firstName: 'Thomas',
      phone: '+33 6 45 67 89 01',
      email: 'thomas.bernot@email.fr',
    },
  }, // confirmee — urgencyLevel 6
  {
    ...MOCK_RESERVATIONS[0],
    event: MOCK_EVENTS[0],
    progressType: 'temporal',
    progressValue: 0.35,
    phraseInfoState: 'Événement dans 178 jours',
    clientInfo: { firstName: 'Julie', phone: '+33 6 78 90 12 34', email: 'julie.martin@email.fr' },
  }, // confirmee — urgencyLevel 2
  // ── refusee ───────────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[5],
    event: PRO_ONLY_EVENTS[5],
    progressType: null,
    progressValue: null,
    phraseInfoState: 'Refusée le <strong>21/06/2026</strong>',
    clientInfo: {
      firstName: 'Marie',
      phone: '+33 6 67 89 01 23',
      email: 'marie.contact@aides.org',
    },
  }, // refusee — urgencyLevel 1
  // ── annulee ───────────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[4],
    event: PRO_ONLY_EVENTS[4],
    progressType: null,
    progressValue: null,
    phraseInfoState: 'Annulée par le client le <strong>08/03/2026</strong>',
    clientInfo: {
      firstName: 'Claire',
      phone: '+33 6 56 78 90 12',
      email: 'claire.rousseau@email.fr',
    },
  }, // annulee — urgencyLevel 1
  // ── realisee ──────────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[6],
    event: PRO_ONLY_EVENTS[6],
    progressType: null,
    progressValue: null,
    phraseInfoState: 'Réalisée le <strong>14/12/2025</strong>',
    clientInfo: { firstName: 'Julie', phone: '+33 6 78 90 12 34', email: 'julie.martin@email.fr' },
  }, // realisee — urgencyLevel 1
]
