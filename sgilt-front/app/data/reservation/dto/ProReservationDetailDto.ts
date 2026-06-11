export interface ProReservationDetailDto {
  id: string
  statut: string
  category: string
  prestataireName: string
  prestatairePhoto: string | null
  evenementTitre: string
  evenementType: string | null
  evenementImagePath: string | null
  evenementDate: string | null
  evenementVille: string | null
  clientFirstName: string
  clientLastName: string | null
  clientPhone: string | null
  clientEmail: string
}
