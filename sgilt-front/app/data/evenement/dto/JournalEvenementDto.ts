/**
 * DTO — contrat de réponse de l'API GET /events/:id/journal
 */

export interface JournalModificationDto {
  champ: string
  avant: string | null
  apres: string | null
}

export interface JournalEvenementDto {
  id: string
  createdAt: string
  modifications: JournalModificationDto[]
}

/** Sous-ensemble du Page<T> Spring utilisé par le front */
export interface JournalEvenementPageDto {
  content: JournalEvenementDto[]
  last: boolean
  totalElements: number
}
