/**
 * DTO — contrat API du scan d'un e-billet à l'entrée d'un événement
 * (POST /user/events/{id}/page-publique/billetterie/scans). Toujours 200 : chaque statut est une
 * issue normale du scan.
 */
export interface ScanRequestDto {
  // Contenu brut du QR lu (SGILT:C:<code> pour un e-billet de commande).
  qrContent: string
}

export interface ScannedCommandeDto {
  buyerFirstName: string
  buyerLastName: string
  quantity: number
  // Heure locale du serveur (yyyy-MM-ddTHH:mm:ss…) du premier scan ; null si jamais scannée.
  scannedAt: string | null
}

export interface ScanResultDto {
  status: 'VALIDATED' | 'ALREADY_USED' | 'CANCELLED' | 'UNKNOWN'
  // null pour UNKNOWN.
  commande: ScannedCommandeDto | null
}
