export type ProDemandeStatut = 'nouvelle' | 'recontactee' | 'confirmee' | 'cloturee' | 'annulee'

export interface ProClientInfo {
  name: string
  eventName: string
  eventDate?: string
  eventType?: string
  nbInvites?: string
  ville?: string
}

export interface ProNote {
  id: string
  content: string
  createdAt: string
  isMessageInitial?: boolean
  isPersonal?: boolean
  authorName: string
  authorRole: 'client' | 'pro'
}

export interface ProDocument {
  id: string
  name: string
  fileType: 'pdf' | 'image' | 'other'
  url: string
  uploadedByRole: 'client' | 'pro'
  uploadedByName: string
  uploadedAt: string
}

export interface ProDemandeDetail {
  id: number
  titre: string
  date: string
  dateIso: string
  statut: ProDemandeStatut
  category: string
  clientInfo: ProClientInfo
  notes: ProNote[]
  documents: ProDocument[]
  urgencyLevel: number
}

const MOCK_DEMANDES: ProDemandeDetail[] = [
  {
    id: 1,
    titre: 'Mariage de Julie & Thomas',
    date: '14 septembre 2026',
    dateIso: '2026-09-14',
    statut: 'confirmee',
    category: 'Musique',
    urgencyLevel: 2,
    clientInfo: {
      name: 'Julie M.',
      eventName: 'Mariage de Julie & Thomas',
      eventDate: '14 septembre 2026',
      eventType: 'mariage',
      nbInvites: '120',
      ville: 'Obernai',
    },
    notes: [
      {
        id: 'pn-101',
        content:
          'Bonjour, je cherche un DJ pour notre mariage le 14 septembre 2026 au Domaine des Étoiles à Obernai. Nous souhaitons un cocktail jazz (17h–19h) puis une soirée dansante électro (20h–00h). Budget : 1 500–2 000 € TTC. Avez-vous des disponibilités ?',
        createdAt: '2026-02-01T09:30:00.000Z',
        isMessageInitial: true,
        authorName: 'Julie M.',
        authorRole: 'client',
      },
      {
        id: 'pn-102',
        content:
          'Bonjour Julie, merci pour votre demande. Nous sommes disponibles le 14 septembre et proposons exactement ce type de prestation bipartite. Je vous envoie notre fiche détaillée.',
        createdAt: '2026-02-02T10:15:00.000Z',
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
      {
        id: 'pn-103',
        content:
          'Formule retenue : set DJ 5h avec sonorisation complète + éclairage d\'ambiance. Tarif confirmé : 1 800 € TTC.',
        createdAt: '2026-02-12T09:00:00.000Z',
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
      {
        id: 'pn-104',
        content: 'Acompte de 30 % reçu le 14/02/2026. Merci, tout est en ordre côté administratif.',
        createdAt: '2026-02-15T14:00:00.000Z',
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
      {
        id: 'pn-priv-101',
        content:
          'Client très attentive au répertoire. Préparer une playlist de validation à envoyer 1 mois avant.',
        createdAt: '2026-02-16T08:30:00.000Z',
        isPersonal: true,
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
    ],
    documents: [
      {
        id: 'pd-101',
        name: 'Contrat_DJ_Animation_14sept2026.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedByRole: 'client',
        uploadedByName: 'Julie M.',
        uploadedAt: '2026-02-10T10:30:00.000Z',
      },
      {
        id: 'pd-102',
        name: 'Rider_technique_et_fiche_besoins.pdf',
        fileType: 'pdf',
        url: '#',
        uploadedByRole: 'pro',
        uploadedByName: 'DJ Animation',
        uploadedAt: '2026-02-12T12:00:00.000Z',
      },
    ],
  },
  {
    id: 2,
    titre: 'Anniversaire 50 ans de Marc',
    date: '23 mai 2026',
    dateIso: '2026-05-23',
    statut: 'nouvelle',
    category: 'Musique',
    urgencyLevel: 1,
    clientInfo: {
      name: 'Sophie L.',
      eventName: 'Anniversaire 50 ans de Marc',
      eventDate: '23 mai 2026',
      eventType: 'anniversaire',
      nbInvites: '60',
      ville: 'Lyon',
    },
    notes: [
      {
        id: 'pn-201',
        content:
          'Bonjour, nous organisons une surprise pour les 50 ans de mon mari le 23 mai à Lyon. Nous cherchons un groupe de jazz manouche pour animer la soirée de 19h à 22h (60 personnes). Pouvez-vous nous proposer une formule ?',
        createdAt: '2026-03-18T11:00:00.000Z',
        isMessageInitial: true,
        authorName: 'Sophie L.',
        authorRole: 'client',
      },
    ],
    documents: [],
  },
  {
    id: 3,
    titre: 'Soirée entreprise Alsace Tech',
    date: '12 avril 2026',
    dateIso: '2026-04-12',
    statut: 'recontactee',
    category: 'Animation',
    urgencyLevel: 3,
    clientInfo: {
      name: 'Marc D.',
      eventName: 'Soirée entreprise Alsace Tech',
      eventDate: '12 avril 2026',
      eventType: 'entreprise',
      nbInvites: '80',
      ville: 'Strasbourg',
    },
    notes: [
      {
        id: 'pn-301',
        content:
          'Bonjour, notre société organise sa soirée annuelle le 12 avril au Palais des Fêtes de Strasbourg (80 personnes). Nous cherchons une animation musicale et sonorisée pour 3h. Budget indicatif : 1 200–1 800 €.',
        createdAt: '2026-03-07T14:00:00.000Z',
        isMessageInitial: true,
        authorName: 'Marc D.',
        authorRole: 'client',
      },
      {
        id: 'pn-302',
        content:
          'Bonjour Marc, merci pour votre demande. Nous avons bien la disponibilité pour le 12 avril. Je vous propose notre formule "Soirée Pro" incluant sonorisation + 2 sets de 1h30. Devis à suivre.',
        createdAt: '2026-03-10T09:00:00.000Z',
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
      {
        id: 'pn-303',
        content:
          'Merci pour le retour. Nous étudions votre devis et revenons vers vous d\'ici fin de semaine.',
        createdAt: '2026-03-14T17:30:00.000Z',
        authorName: 'Marc D.',
        authorRole: 'client',
      },
      {
        id: 'pn-priv-301',
        content:
          'Négociation en cours sur le tarif. Client prêt à signer si on baisse de 150 €. À voir selon la marge.',
        createdAt: '2026-03-15T10:00:00.000Z',
        isPersonal: true,
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
    ],
    documents: [],
  },
  {
    id: 4,
    titre: 'Mariage Dupont',
    date: '6 juin 2026',
    dateIso: '2026-06-06',
    statut: 'nouvelle',
    category: 'Musique',
    urgencyLevel: 5,
    clientInfo: {
      name: 'Émilie D.',
      eventName: 'Mariage Dupont',
      eventDate: '6 juin 2026',
      eventType: 'mariage',
      nbInvites: '150',
      ville: 'Colmar',
    },
    notes: [
      {
        id: 'pn-401',
        content:
          'Bonjour, je cherche un DJ pour notre mariage le 6 juin à Colmar (150 invités). Ambiance festive, mix pop-rock et électro. Prestation de 19h à 2h du matin. Merci de me contacter au plus vite, nous avons peu de temps !',
        createdAt: '2026-03-17T16:45:00.000Z',
        isMessageInitial: true,
        authorName: 'Émilie D.',
        authorRole: 'client',
      },
    ],
    documents: [],
  },
  {
    id: 5,
    titre: 'Cocktail Lancement Produit',
    date: '4 avril 2026',
    dateIso: '2026-04-04',
    statut: 'confirmee',
    category: 'Musique',
    urgencyLevel: 6,
    clientInfo: {
      name: 'Thomas B.',
      eventName: 'Cocktail Lancement Produit',
      eventDate: '4 avril 2026',
      eventType: 'entreprise',
      nbInvites: '40',
      ville: 'Mulhouse',
    },
    notes: [
      {
        id: 'pn-501',
        content:
          'Lancement de notre nouveau produit le 4 avril à Mulhouse (40 personnes). Ambiance lounge, fond musical 2h. Budget serré : 600–800 €.',
        createdAt: '2026-03-01T10:00:00.000Z',
        isMessageInitial: true,
        authorName: 'Thomas B.',
        authorRole: 'client',
      },
      {
        id: 'pn-502',
        content:
          'Formule lounge 2h confirmée à 750 € TTC. Contrat signé, acompte reçu. Rendez-vous le 4 avril.',
        createdAt: '2026-03-05T09:30:00.000Z',
        authorName: 'DJ Animation',
        authorRole: 'pro',
      },
    ],
    documents: [],
  },
  {
    id: 6,
    titre: 'Soirée privée Strasbourg',
    date: '15 mars 2026',
    dateIso: '2026-03-15',
    statut: 'annulee',
    category: 'Musique',
    urgencyLevel: 1,
    clientInfo: {
      name: 'Claire R.',
      eventName: 'Soirée privée Strasbourg',
      eventDate: '15 mars 2026',
      eventType: 'soiree',
      nbInvites: '30',
      ville: 'Strasbourg',
    },
    notes: [
      {
        id: 'pn-601',
        content:
          'Soirée privée le 15 mars pour 30 personnes. Musique d\'ambiance 3h. Merci.',
        createdAt: '2026-02-20T14:00:00.000Z',
        isMessageInitial: true,
        authorName: 'Claire R.',
        authorRole: 'client',
      },
      {
        id: 'pn-602',
        content:
          'Suite à des imprévus personnels, nous devons annuler cet événement. Merci de votre compréhension.',
        createdAt: '2026-03-08T11:00:00.000Z',
        authorName: 'Claire R.',
        authorRole: 'client',
      },
    ],
    documents: [],
  },
  {
    id: 7,
    titre: 'Gala Humanitaire Grand Est',
    date: '20 juin 2026',
    dateIso: '2026-06-20',
    statut: 'cloturee',
    category: 'Musique',
    urgencyLevel: 1,
    clientInfo: {
      name: 'Association AIDES',
      eventName: 'Gala Humanitaire Grand Est',
      eventDate: '20 juin 2026',
      eventType: 'autre',
      nbInvites: '200',
      ville: 'Nancy',
    },
    notes: [
      {
        id: 'pn-701',
        content:
          'Gala de bienfaisance le 20 juin à Nancy (200 personnes). Musique classique en fond pour le dîner, puis ambiance festive pour la soirée dansante. Prestation 4h.',
        createdAt: '2026-01-15T10:00:00.000Z',
        isMessageInitial: true,
        authorName: 'Association AIDES',
        authorRole: 'client',
      },
    ],
    documents: [],
  },
]

export const ProMockService = {
  async getDemandeById(id: number): Promise<ProDemandeDetail | null> {
    await new Promise((r) => setTimeout(r, 200))
    return MOCK_DEMANDES.find((d) => d.id === id) ?? null
  },

  async updateStatut(id: number, statut: ProDemandeStatut): Promise<void> {
    await new Promise((r) => setTimeout(r, 300))
    const demande = MOCK_DEMANDES.find((d) => d.id === id)
    if (demande) demande.statut = statut
  },

  async addNote(id: number, content: string, isPersonal: boolean): Promise<ProNote> {
    await new Promise((r) => setTimeout(r, 300))
    const note: ProNote = {
      id: `pn-${Date.now()}`,
      content,
      createdAt: new Date().toISOString(),
      isPersonal,
      authorName: 'DJ Animation',
      authorRole: 'pro',
    }
    const demande = MOCK_DEMANDES.find((d) => d.id === id)
    if (demande) demande.notes.push(note)
    return note
  },
}
