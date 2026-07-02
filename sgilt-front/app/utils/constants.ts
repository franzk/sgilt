// app/utils/constants.ts

// ______________ Catégories et sous-catégories ______________
export type Category = {
  key: string // identifiant partagé front/back/DB ('all', 'musique'…)
  name: string
  subcategories: SubCategory[]
}

export type SubCategory = {
  key: string // identifiant partagé front/back/DB ('dj', 'pop-rock'… — vide si pas en DB)
  name: string
  categoryKey: string
}

export const APP_CATEGORIES: Category[] = [
  { key: 'all', name: 'Tous', subcategories: [] },
  {
    key: 'musique',
    name: 'Musique',
    subcategories: [
      { key: 'dj', name: 'DJ', categoryKey: 'musique' },
      { key: 'pop-rock', name: 'Pop/Rock', categoryKey: 'musique' },
      { key: 'jazz', name: 'Jazz', categoryKey: 'musique' },
    ],
  },
  {
    key: 'restauration',
    name: 'Restauration',
    subcategories: [
      { key: 'traiteur', name: 'Traiteur', categoryKey: 'restauration' },
      { key: 'food-truck', name: 'Food Truck', categoryKey: 'restauration' },
    ],
  },
  {
    key: 'photo',
    name: 'Photo',
    subcategories: [
      { key: 'photographe', name: 'Photographe', categoryKey: 'photo' },
      { key: '', name: 'Vidéo', categoryKey: 'photo' },
      { key: 'photobooth', name: 'Photobooth', categoryKey: 'photo' },
    ],
  },
  {
    key: 'services',
    name: 'Services',
    subcategories: [
      { key: '', name: 'Décoration', categoryKey: 'services' },
      { key: 'location-lieu', name: 'Location de matériel', categoryKey: 'services' },
    ],
  },
]

// ______________ Types d'événements ______________
export type EventType = {
  value: string
  label: string
}

export const eventTypes: EventType[] = [
  { value: '-1', label: 'Votre événement' },
  { value: '0', label: 'Mariage' },
  { value: '1', label: 'Anniversaire' },
  { value: '2', label: "Fête d'entreprise" },
  { value: '3', label: 'Autre' },
]

// ______________ Badges d'engagement ______________
export type EngagementKey =
  | 'REPONSE_48H'
  | 'ADAPTABLE'
  | 'ACCOMPAGNEMENT'
  | 'EQUIPE'
  | 'INTERLOCUTEUR_UNIQUE'
  | 'ECORESPONSABLE'

export const ENGAGEMENT_ICON_MAP: Record<EngagementKey, string> = {
  REPONSE_48H: 'Clock',
  ADAPTABLE: 'Tune',
  ACCOMPAGNEMENT: 'Handshake',
  EQUIPE: 'Inventory_2',
  INTERLOCUTEUR_UNIQUE: 'Person_Check',
  ECORESPONSABLE: 'Eco',
}
