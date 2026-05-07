/**
 * DTO — contrat de réponse de l'API GET /events/:id/reservations
 */
export interface EventReservationSummaryDto {
  id: string
  prestataireId: string
  prestataireName: string
  prestatairePhoto?: string | null
  category: string
  status: string
  unreadNotesCount: number
}
