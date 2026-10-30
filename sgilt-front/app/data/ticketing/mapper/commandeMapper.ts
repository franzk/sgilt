/**
 * Mapper — conversions entre DTOs et domain des commandes de billets
 */
import type { CommandeDto } from '../dto/CommandeDto'
import type { Commande } from '../domain/Commande'

export function mapCommande(dto: CommandeDto): Commande {
  return {
    id: dto.id,
    quantity: dto.quantity,
    totalCents: dto.totalCents,
    expiresAt: dto.expiresAt,
  }
}
