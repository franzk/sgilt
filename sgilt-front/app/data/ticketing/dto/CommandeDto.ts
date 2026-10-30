/**
 * DTO — contrat API des commandes de billets (/commandes)
 */
export interface CreateCommandeRequestDto {
  slug: string
  quantity: number
  firstName: string
  lastName: string
  email: string
}

export interface CommandeDto {
  id: string
  quantity: number
  totalCents: number
  // Fin du blocage des places faute de paiement, heure locale du serveur (yyyy-MM-ddTHH:mm:ss).
  expiresAt: string
}
