/**
 * Mapper — conversions DTO → domaine pour le module evenement
 */
import type { EvenementSummaryDto } from '../dto/EvenementSummaryDto'
import type { EventDetailDto } from '../dto/EventDetailDto'
import type { EventCountsDto } from '../dto/EventCountsDto'
import type { EventReservationSummaryDto } from '../dto/EventReservationSummaryDto'
import type { JournalEvenementDto } from '../dto/JournalEvenementDto'
import type { EventSummary } from '../domain/EventSummary'
import type { EventDetail } from '../domain/EventDetail'
import type { EventCounts } from '../domain/EventCounts'
import type { JournalEntry } from '../domain/JournalEntry'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { Reservation } from '~/data/reservation/domain/Reservation'
import type { ReservationStatus } from '~/data/reservation/domain/ReservationStatus'

export function mapEvenementSummary(dto: EvenementSummaryDto): EventSummary {
  return {
    id: dto.id,
    title: dto.name,
    date: dto.date ? new Date(dto.date) : undefined,
    ville: dto.ville,
    coverImage: dto.coverUrl ?? null,
    eventType: dto.eventType,
    confirmedCount: dto.confirmedCount,
    inDiscussionCount: dto.inDiscussionCount,
  }
}

export function mapEventDetail(dto: EventDetailDto): {
  event: EventDetail
  clientInfo: ClientContactInfo
} {
  return {
    event: {
      id: dto.id,
      title: dto.title,
      date: dto.date ? new Date(dto.date) : undefined,
      eventType: dto.eventType,
      ambiance: dto.ambiance,
      momentCle: dto.momentCle,
      description: dto.description,
      ville: dto.ville,
      lieu: dto.lieu,
      nbInvites: dto.nbInvites,
      coverImage: dto.coverUrl ?? null,
      sharedNote: dto.sharedNote,
      countdown: dto.countdown,
      lastUpdateDate: dto.lastUpdateDate ? new Date(dto.lastUpdateDate) : null,
      mood: 'defaut',
      reservations: [],
      journal: [],
    },
    clientInfo: {
      firstName: dto.clientInfo.firstName,
      lastName: dto.clientInfo.lastName ?? '',
      phone: dto.clientInfo.phone ?? '',
      email: dto.clientInfo.email,
    },
  }
}

export function mapEventCounts(dto: EventCountsDto): EventCounts {
  return {
    mood: dto.mood,
    confirmedCount: dto.confirmedCount,
    inDiscussionCount: dto.inDiscussionCount,
    nouvelleCount: dto.nouvelleCount,
    refuseeCount: dto.refuseeCount,
    annuleeCount: dto.annuleeCount,
    realiseeCount: dto.realiseeCount,
  }
}

export function mapJournalEntry(dto: JournalEvenementDto): JournalEntry {
  return {
    id: dto.id,
    date: new Date(dto.createdAt),
    modifications: dto.modifications.map(m => ({ champ: m.champ, avant: m.avant, apres: m.apres })),
    isCreation: false,
  }
}

export function mapEventReservation(dto: EventReservationSummaryDto): Reservation {
  return {
    id: dto.id,
    prestataireId: dto.prestataireId,
    prestataireName: dto.prestataireName,
    prestatairePhoto: dto.prestatairePhoto ?? undefined,
    category: dto.category,
    status: dto.status as ReservationStatus,
    unreadNotesCount: dto.unreadNotesCount,
  }
}
