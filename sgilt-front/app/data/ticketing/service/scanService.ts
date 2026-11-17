/**
 * Couche service — scan d'un e-billet à l'entrée d'un événement, par son organisateur. Usage
 * unique : le premier scan fait entrer toutes les places de la commande. Le contenu du QR est relu
 * par le back (le front ne connaît pas son format).
 */
import { postScanApi } from '../api/organizerPageApi'
import { mapScanResult } from '../mapper/scanMapper'
import type { ScanResult } from '../domain/Scan'

export async function scanEBillet(eventId: string, qrContent: string): Promise<ScanResult> {
  return mapScanResult(await postScanApi(eventId, { qrContent }))
}
