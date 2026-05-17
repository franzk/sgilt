export interface ReservationMetaDto {
  id: string
  prestataireId: string
  prestataireName: string
  prestatairePhoto?: string | null
  category: string
  status: string
  unreadNotesCount: number
}
