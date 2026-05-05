/**
 * Domaine — entrée de journal d'un événement ; JournalModification est son value object
 */
export interface JournalModification {
  champ: string
  avant: string | null
  apres: string
}

export interface JournalEntry {
  id: string
  date: Date
  modifications: JournalModification[]
  isCreation: boolean
}
