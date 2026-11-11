// app/utils/constants.ts

// ______________ Catégories et sous-catégories ______________
// Les catégories sont servies par le back (voir useCategories).

// Clé de la catégorie « Tous » : aucun filtre. Partagée avec le back (countsByCategory).
export const ALL_CATEGORY_KEY = 'all'

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

// ______________ Catégories d'information pratique ______________
export type DetailCategory = 'FORMAT' | 'PROCESS' | 'DELIVERABLE' | 'LOGISTICS' | 'OTHER'

export const DETAIL_CATEGORY_ORDER: DetailCategory[] = [
  'FORMAT',
  'PROCESS',
  'DELIVERABLE',
  'LOGISTICS',
  'OTHER',
]
