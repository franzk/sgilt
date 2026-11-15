/**
 * Couche service — scan d'un e-billet à l'entrée d'un événement, par son organisateur.
 *
 * ⚠ PROVISOIRE (brique F3) : aucun appel au back, qui n'existe pas encore. Réponse simulée pour
 * construire l'écran de résultat : un QR d'e-billet Sgilt (SGILT:C:…) est validé au premier scan,
 * « déjà utilisé » ensuite (mémoire de la page) ; tout autre QR est inconnu. À remplacer par l'appel
 * à la route du back (brique B2), sans changer la signature.
 */
import type { ScanResult } from '../domain/Scan'

// Préfixe du contenu du QR d'un e-billet de commande (format défini par le back).
const MOCK_COMMANDE_QR_PREFIX = 'SGILT:C:'

// Contenu du QR → heure du premier scan (ISO).
const mockScannedAt = new Map<string, string>()

export async function scanEBillet(_eventId: string, qrContent: string): Promise<ScanResult> {
  if (!qrContent.startsWith(MOCK_COMMANDE_QR_PREFIX)) return { status: 'UNKNOWN' }

  const firstScan = mockScannedAt.get(qrContent)
  const scannedAt = firstScan ?? new Date().toISOString()
  if (!firstScan) mockScannedAt.set(qrContent, scannedAt)

  return {
    status: firstScan ? 'ALREADY_USED' : 'VALIDATED',
    commande: { buyerFirstName: 'Jean', buyerLastName: 'Dupont', quantity: 4, scannedAt },
  }
}
