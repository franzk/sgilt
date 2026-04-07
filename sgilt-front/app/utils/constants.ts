// app/utils/constants.ts

// ______________ Catégories et sous-catégories ______________
export type Category = {
  id: string
  name: string
  subcategories: SubCategory[]
}

export type SubCategory = {
  id: string
  name: string
  categoryId: string
}

export const APP_CATEGORIES: Category[] = [
  { id: '1', name: 'Tous', subcategories: [] },
  {
    id: '2',
    name: 'Musique',
    subcategories: [
      { id: '21', name: 'DJ', categoryId: '2' },
      { id: '22', name: 'Pop/Rock', categoryId: '2' },
      { id: '23', name: 'Jazz', categoryId: '2' },
    ],
  },
  {
    id: '3',
    name: 'Restauration',
    subcategories: [
      { id: '31', name: 'Traiteur', categoryId: '3' },
      { id: '32', name: 'Food Truck', categoryId: '3' },
    ],
  },
  {
    id: '4',
    name: 'Photo',
    subcategories: [
      { id: '41', name: 'Photographe', categoryId: '4' },
      { id: '42', name: 'Vidéo', categoryId: '4' },
      { id: '43', name: 'Photobooth', categoryId: '4' },
    ],
  },
  {
    id: '5',
    name: 'Services',
    subcategories: [
      { id: '51', name: 'Décoration', categoryId: '5' },
      { id: '52', name: 'Location de matériel', categoryId: '5' },
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
