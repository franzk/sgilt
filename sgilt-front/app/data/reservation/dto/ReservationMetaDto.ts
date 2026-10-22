/**
 * DTO — en-tête d'une réservation côté client, tel que renvoyé par GET /user/reservations/{id}
 */
export interface ReservationMetaDto {
  id: string
  prestataireId: string
  prestataireName: string
  prestataireSlug: string
  prestatairePhoto?: string | null
  category: string
  status: string
  unreadNotesCount: number
}
