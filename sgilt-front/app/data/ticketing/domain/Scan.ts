/**
 * Domaine — résultat du scan d'un e-billet à l'entrée d'un événement, par son organisateur
 * (/app/events/[eventId]/scan). Un e-billet est à usage unique : le premier scan fait entrer
 * toutes les places de sa commande, les suivants répondent « déjà utilisé ».
 */

// VALIDATED : entrée validée par ce scan. ALREADY_USED : déjà scanné. CANCELLED, NOT_PAID : commande
// de cet événement, mais qui ne fait pas entrer. UNKNOWN : pas un e-billet de cet événement.
export const SCAN_STATUSES = ['VALIDATED', 'ALREADY_USED', 'CANCELLED', 'NOT_PAID', 'UNKNOWN'] as const

export type ScanStatus = (typeof SCAN_STATUSES)[number]

// Commande du QR scanné. Absente pour UNKNOWN : rien d'une commande d'un autre événement n'est montré.
export interface ScannedCommande {
  buyerFirstName: string
  buyerLastName: string
  quantity: number
  // Heure du premier scan (ISO) ; pour VALIDATED, celle de ce scan. Null si jamais scannée.
  scannedAt: string | null
}

export interface ScanResult {
  status: ScanStatus
  commande?: ScannedCommande
}
