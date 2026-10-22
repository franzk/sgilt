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
  evenementId: string
  evenementLieu: string | null
  evenementNbInvites: string | null
  evenementAmbiance: string | null
  evenementMomentCle: string | null
  evenementDescription: string | null
  evenementNotePartagee: string | null
  evenementLastUpdateDate: string | null
  clientFirstName: string
  clientLastName: string | null
  clientPhone: string | null
  clientEmail: string
}
