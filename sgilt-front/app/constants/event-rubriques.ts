// ── Rubriques d'un événement ──────────────────────────────────────────────────
// Preset mocké en dur côté front (pas de backend à ce stade). Ordre = ordre d'affichage.

export const RUBRIQUE_KEYS = [
  'invites',
  'lieu',
  'restauration',
  'musique-animation',
  'decoration',
  'hebergement',
] as const

export type RubriqueKey = (typeof RUBRIQUE_KEYS)[number]

export interface EventRubrique {
  key: RubriqueKey
  itemCount: number
}

export const MARIAGE_RUBRIQUES: EventRubrique[] = RUBRIQUE_KEYS.map((key) => ({
  key,
  itemCount: 0,
}))
