/**
 * Mapper — conversions entre DTOs et domain de la page publique d'un événement
 */
import type { EventPageDto, EventPagePictoDto } from '../dto/EventPageDto'
import type { PublicEvent, PublicEventPicto } from '../domain/PublicEvent'

function mapPicto(dto: EventPagePictoDto): PublicEventPicto {
  return {
    icon: dto.icon,
    label: dto.label,
    sublabel: dto.sublabel,
  }
}

export function mapPublicEvent(dto: EventPageDto): PublicEvent {
  return {
    slug: dto.slug,
    tag: dto.tag ?? undefined,
    title: dto.title,
    subtitle: dto.subtitle ?? undefined,
    heroImage: dto.heroImage ?? undefined,
    date: dto.date,
    startsAt: dto.startsAt ?? undefined,
    endsAt: dto.endsAt ?? undefined,
    venue: dto.venue,
    city: dto.city,
    sectionTitle: dto.sectionTitle ?? undefined,
    description: dto.description ?? undefined,
    ticketing: dto.ticketing ?? undefined,
    importantInfo: dto.importantInfo,
    pictos: dto.pictos.map(mapPicto),
    practicalInfo: dto.practicalInfo,
  }
}
