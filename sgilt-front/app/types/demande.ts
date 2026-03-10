// app/types/demande.ts

export interface DemandeOption {
  value: string
  label: string
  emoji: string
}

export interface DemandeState {
  eventType: string | null
  eventTypeAutre: string
  ambiance: string | null
  ambianceAutre: string
  momentCle: string | null
  momentCleAutre: string
  description: string
  date: Date | undefined
  ville: string
  nbInvites: string
  lieuDefini: boolean
  lieu: string
  email: string
  telephone: string
  prestataireMessage?: string
}

export const ETAPES_COUNT = 5

export const EVENT_TYPE_OPTIONS: DemandeOption[] = [
  { value: 'anniversaire', label: 'Anniversaire', emoji: '🎂' },
  { value: 'mariage', label: 'Mariage', emoji: '💍' },
  { value: 'soiree', label: 'Soirée privée', emoji: '🥂' },
  { value: 'entreprise', label: "Fête d'entreprise", emoji: '🏢' },
  { value: 'autre', label: 'Autre', emoji: '•••' },
]

export const eventDescriptionPlaceholders: Record<string, string> = {
  anniversaire:
    "Des bougies, des rires, les gens qu'on aime... racontez-nous ce moment comme vous le rêvez.",
  mariage:
    "La musique, l'émotion palpable, la fête qui éclate... décrivez le mariage que vous imaginez.",
  soiree:
    "Une playlist qui fait bouger les épaules, des éclats de rire jusqu'à tard... à quoi ressemble votre soirée idéale ?",
  entreprise:
    "Des fous rires hors des réunions, une ambiance qu'on ne voit pas venir... à quoi ressemble votre soirée d'entreprise ?",
  autre: "Fermez les yeux. Qu'est-ce que vous voyez ce soir-là ?",
}

export const AMBIANCE_OPTIONS: DemandeOption[] = [
  { value: 'festif', label: 'Festif et dansant', emoji: '🎉' },
  { value: 'chic', label: 'Chic et élégant', emoji: '✨' },
  { value: 'convivial', label: 'Convivial et détendu', emoji: '🤝' },
  { value: 'surprise', label: 'Surprise', emoji: '🎁' },
  { value: 'autre', label: 'Autre', emoji: '•••' },
]

export const MOMENT_CLE_OPTIONS: DemandeOption[] = [
  { value: 'danse', label: 'Tout le monde sur la piste de danse', emoji: '💃' },
  { value: 'buffet', label: "Ambiance autour d'un buffet", emoji: '🍽️' },
  { value: 'surprise', label: 'Moment surprise', emoji: '🎁' },
  { value: 'tard', label: 'Soirée qui finit très tard…', emoji: '🌙' },
  { value: 'autre', label: 'Autre', emoji: '•••' },
]
