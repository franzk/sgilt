export interface ActiveReservationItemDto {
  reservationId: string
  status: string
  evenementId: string
  evenementTitle: string
  prestataireSlug: string
  prestataireName: string
  prestataireAvatar: string | null
}

export interface ActiveReservationsDto {
  items: ActiveReservationItemDto[]
  hasConfirmed: boolean
}
