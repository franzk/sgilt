/**
 * Mapper — conversions entre DTOs et domain de la page publique d'un événement
 */
import type { EventPageDto, EventPagePictoDto, EventPageTicketingDto } from '../dto/EventPageDto'
import type { PublicEvent, PublicEventPicto, PublicEventTicketing } from '../domain/PublicEvent'
import { defaultCoverPath } from '~/utils/eventCovers'

function mapPicto(dto: EventPagePictoDto): PublicEventPicto {
  return {
    icon: dto.icon,
    label: dto.label,
    sublabel: dto.sublabel,
  }
}

function mapTicketing(dto: EventPageTicketingDto): PublicEventTicketing {
  return {
    status: dto.status,
    unitPriceCents: dto.unitPriceCents,
    priceNote: dto.priceNote ?? undefined,
    remainingTickets: dto.remainingTickets,
    maxPlacesPerCommande: dto.maxPlacesPerCommande,
  }
}

export function mapPublicEvent(dto: EventPageDto): PublicEvent {
  return {
    slug: dto.slug,
    tag: dto.tag ?? undefined,
    title: dto.title,
    subtitle: dto.subtitle ?? undefined,
    heroImage: dto.heroImage ?? defaultCoverPath(dto.eventType),
    date: dto.date,
    startsAt: dto.startsAt ?? undefined,
    endsAt: dto.endsAt ?? undefined,
    venue: dto.venue ?? undefined,
    city: dto.city ?? undefined,
    mapsUrl: dto.mapsUrl ?? undefined,
    sectionTitle: dto.sectionTitle ?? undefined,
    description: dto.description ?? undefined,
    ticketing: dto.ticketing ? mapTicketing(dto.ticketing) : undefined,
    importantInfo: dto.importantInfo,
    pictos: dto.pictos.map(mapPicto),
    practicalInfo: dto.practicalInfo,
  }
}
