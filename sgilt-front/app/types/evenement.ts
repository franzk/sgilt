// app/types/evenement.ts

// Champs de l'événement envoyés au serveur, choix « autre » déjà résolus en texte libre.
export interface EvenementRequest {
  eventType: string | null
  ambiance: string | null
  momentCle: string | null
  description: string | null
  date: string | null
  ville: string | null
  nbInvites: string | null
  lieu: string | null
}

export interface EvenementOption {
  value: string
  label: string
  emoji: string
}

export const EVENT_TYPE_OPTIONS: EvenementOption[] = [
  { value: 'mariage', label: 'Mariage', emoji: '💍' },
  { value: 'soiree_privee', label: 'Soirée privée', emoji: '🥂' },
  { value: 'anniversaire', label: 'Anniversaire', emoji: '🎂' },
  { value: 'fete_entreprise', label: "Fête d'entreprise", emoji: '🏢' },
  { value: 'evenement_public', label: 'Événement public', emoji: '🎪' },
  { value: 'autre', label: 'Autre', emoji: '•••' },
]

export const AMBIANCE_OPTIONS: EvenementOption[] = [
  { value: 'festif', label: 'Festif et dansant', emoji: '🎉' },
  { value: 'chic', label: 'Chic et élégant', emoji: '✨' },
  { value: 'convivial', label: 'Convivial et détendu', emoji: '🤝' },
  { value: 'surprise', label: 'Surprise', emoji: '🎁' },
  { value: 'autre', label: 'Autre', emoji: '•••' },
]

export const MOMENT_CLE_OPTIONS: EvenementOption[] = [
  { value: 'danse', label: 'Tout le monde sur la piste de danse', emoji: '💃' },
  { value: 'buffet', label: "Ambiance autour d'un buffet", emoji: '🍽️' },
  { value: 'surprise', label: 'Moment surprise', emoji: '🎁' },
  { value: 'tard', label: 'Soirée qui finit très tard…', emoji: '🌙' },
  { value: 'autre', label: 'Autre', emoji: '•••' },
]

// Tranches proposées par le stepper d'organisation. Le libellé est stocké tel quel
// dans `nbInvites` (chaîne libre côté back, affichée telle quelle sur le board).
export const NB_INVITES_OPTIONS: string[] = [
  'Moins de 20',
  '20 – 50',
  '50 – 100',
  '100 – 200',
  '200 – 500',
  'Plus de 500',
]
