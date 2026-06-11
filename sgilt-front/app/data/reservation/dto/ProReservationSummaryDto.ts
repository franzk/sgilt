export interface ProReservationSummaryDto {
  id: string
  evenementTitre: string
  evenementType: string | null
  image: string | null
  datePrestation: string
  statut: string
  unreadNotesCount: number
}
