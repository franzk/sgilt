/**
 * DTO — un fichier d'un envoi de documents du fil d'une réservation, tel que renvoyé par le back.
 * Transformé en FeedFile par reservationFeedService.
 */
export interface FeedFileDto {
  id: string
  name: string
  fileType?: string | null
  url: string
}
