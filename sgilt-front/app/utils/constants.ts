// app/utils/constants.ts

// ______________ Catégories et sous-catégories ______________
export type Category = {
  key: string // identifiant partagé front/back/DB ('all', 'musique'…)
  name: string
  subcategories: SubCategory[]
}

export type SubCategory = {
  key: string      // identifiant partagé front/back/DB ('dj', 'pop-rock'… — vide si pas en DB)
  name: string
  categoryKey: string
}

export const APP_CATEGORIES: Category[] = [
  { key: 'all', name: 'Tous', subcategories: [] },
  {
    key: 'musique',
    name: 'Musique',
    subcategories: [
      { key: 'dj',       name: 'DJ',       categoryKey: 'musique' },
      { key: 'pop-rock', name: 'Pop/Rock', categoryKey: 'musique' },
      { key: 'jazz',     name: 'Jazz',     categoryKey: 'musique' },
    ],
  },
  {
    key: 'restauration',
    name: 'Restauration',
    subcategories: [
      { key: 'traiteur',   name: 'Traiteur',   categoryKey: 'restauration' },
      { key: 'food-truck', name: 'Food Truck', categoryKey: 'restauration' },
    ],
  },
  {
    key: 'photo',
    name: 'Photo',
    subcategories: [
      { key: 'photographe', name: 'Photographe', categoryKey: 'photo' },
      { key: '',            name: 'Vidéo',       categoryKey: 'photo' },
      { key: 'photobooth',  name: 'Photobooth',  categoryKey: 'photo' },
    ],
  },
  {
    key: 'services',
    name: 'Services',
    subcategories: [
      { key: '',              name: 'Décoration',           categoryKey: 'services' },
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
export type EngagementBadge = {
  icon: string
  label: string
  color: string
  description?: string
}
export const engagementBadges: EngagementBadge[] = [
  { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
  { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
  {
    icon: 'Handshake',
    label: 'Accompagné',
    description: 'Accompagnement personnalisé',
    color: '#FACC15',
  },
  { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
  {
    icon: 'Person_Check',
    label: 'Interlocuteur Unique',
    description: 'Interlocuteur unique',
    color: '#FACC15',
  },
  { icon: 'Eco', label: 'Éco', description: 'Éco-responsable', color: '#22C55E' },
]
