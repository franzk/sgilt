/**
 * Mapper — conversions entre DTOs et domain des commandes de billets
 */
import type { CommandeDto, CommandeSummaryDto } from '../dto/CommandeDto'
import type { Commande, CommandeSummary } from '../domain/Commande'

export function mapCommande(dto: CommandeDto): Commande {
  return {
    id: dto.id,
    quantity: dto.quantity,
    totalCents: dto.totalCents,
    expiresAt: dto.expiresAt,
    clientSecret: dto.clientSecret,
  }
}

export function mapCommandeSummary(dto: CommandeSummaryDto): CommandeSummary {
  return {
    id: dto.id,
    status: dto.status,
    quantity: dto.quantity,
    unitPriceCents: dto.unitPriceCents,
    totalCents: dto.totalCents,
  }
}
