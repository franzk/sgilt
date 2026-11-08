/**
 * Mapper — conversions entre DTOs et domain des commandes de billets
 */
import type { CommandeDto, CommandeSummaryDto, EBilletDto, EvenementInfosDto } from '../dto/CommandeDto'
import type { Commande, CommandeSummary, EBillet, EBilletEvenementInfos } from '../domain/Commande'
import { defaultCoverPath } from '~/utils/eventCovers'

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

function mapEvenementInfos(dto: EvenementInfosDto): EBilletEvenementInfos {
  return {
    tag: dto.tag ?? undefined,
    subtitle: dto.subtitle ?? undefined,
    date: dto.date,
    startsAt: dto.startsAt ?? undefined,
    endsAt: dto.endsAt ?? undefined,
    venue: dto.venue,
    city: dto.city,
    practicalInfo: dto.practicalInfo,
  }
}

export function mapEBillet(dto: EBilletDto): EBillet {
  return {
    title: dto.title,
    heroImage: dto.heroImage ?? defaultCoverPath(dto.eventType),
    evenementInfos: mapEvenementInfos(dto.evenementInfos),
    quantity: dto.quantity,
    qrImage: dto.qrImage,
    pageOnline: dto.pageOnline,
  }
}
