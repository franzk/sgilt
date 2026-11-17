/**
 * Mapper — conversions entre DTOs et domain du scan d'un e-billet
 */
import type { ScanResultDto } from '../dto/ScanDto'
import type { ScanResult } from '../domain/Scan'

export function mapScanResult(dto: ScanResultDto): ScanResult {
  return {
    status: dto.status,
    commande: dto.commande
      ? {
          buyerFirstName: dto.commande.buyerFirstName,
          buyerLastName: dto.commande.buyerLastName,
          quantity: dto.commande.quantity,
          scannedAt: dto.commande.scannedAt ? new Date(dto.commande.scannedAt) : null,
        }
      : null,
  }
}
