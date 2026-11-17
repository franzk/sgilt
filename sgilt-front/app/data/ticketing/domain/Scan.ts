/**
 * Domaine — résultat du scan d'un e-billet à l'entrée d'un événement, par son organisateur
 * (/app/events/[eventId]/scan). Un e-billet est à usage unique : le premier scan fait entrer
 * toutes les places de sa commande, les suivants répondent « déjà utilisé ».
 */

// VALIDATED : entrée validée par ce scan. ALREADY_USED : déjà scanné. CANCELLED : commande de cet
// événement, annulée (remboursée), qui ne fait pas entrer. UNKNOWN : pas un e-billet de cet événement.
// (Pas de « non payé » : le QR d'une commande n'existe qu'une fois payée.)
export type ScanStatus = 'VALIDATED' | 'ALREADY_USED' | 'CANCELLED' | 'UNKNOWN'

// Commande du QR scanné.
export interface ScannedCommande {
  buyerFirstName: string
  buyerLastName: string
  quantity: number
  // Heure du premier scan ; pour VALIDATED, celle de ce scan. Null si jamais scannée.
  scannedAt: Date | null
}

export interface ScanResult {
  status: ScanStatus
  // Null pour UNKNOWN : rien d'une commande d'un autre événement n'est montré.
  commande: ScannedCommande | null
}

// Issue d'un scan affichée à l'entrée : le résultat du back, ou FAILED si la vérification a
// échoué (réseau, serveur).
export type ScanOutcome = ScanResult | { status: 'FAILED'; commande: null }
