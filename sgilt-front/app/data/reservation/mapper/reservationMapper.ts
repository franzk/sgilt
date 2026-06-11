import type { ReservationMetaDto } from '../dto/ReservationMetaDto'
import type { ProReservationSummaryDto } from '../dto/ProReservationSummaryDto'
import type { ProReservationDetailDto } from '../dto/ProReservationDetailDto'
import type { ActiveReservationItemDto, ActiveReservationsDto } from '../dto/ActiveReservationsDto'
import type { ReservationMeta } from '../domain/ReservationMeta'
import type { ProReservationSummary } from '../domain/ProReservationSummary'
import type { ProReservationDetail } from '../domain/ProReservationDetail'
import type { ActiveReservations, ActiveReservationItem } from '../domain/ActiveReservation'
import type { ReservationStatus } from '../domain/ReservationStatus'
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ClientContactInfo } from '../domain/ClientContactInfo'

export function mapReservationMeta(dto: ReservationMetaDto): ReservationMeta {
  return {
    id: dto.id,
    prestataireId: dto.prestataireId,
    prestataireName: dto.prestataireName,
    prestatairePhoto: dto.prestatairePhoto ?? undefined,
    category: dto.category,
    status: dto.status as ReservationMeta['status'],
    unreadNotesCount: dto.unreadNotesCount,
  }
}

export function mapProReservationSummary(dto: ProReservationSummaryDto): ProReservationSummary {
  return {
    id: dto.id,
    titre: dto.evenementTitre,
    eventType: dto.evenementType ?? undefined,
    date: new Date(dto.datePrestation),
    statut: dto.statut as ReservationStatus,
    image: dto.image ?? undefined,
    unreadNotesCount: dto.unreadNotesCount,
  }
}

function mapProReservationDetailEvent(reservationId: string, dto: ProReservationDetailDto): EventDetail {
  return {
    id: reservationId,
    title: dto.evenementTitre,
    date: dto.evenementDate ? new Date(dto.evenementDate) : undefined,
    eventType: dto.evenementType ?? undefined,
    ville: dto.evenementVille ?? undefined,
    coverImage: dto.evenementImagePath ?? null,
    sharedNote: '',
    reservations: [],
    journal: [],
    mood: 'defaut',
    countdown: 'serein',
  }
}

function mapProReservationDetailClientInfo(dto: ProReservationDetailDto): ClientContactInfo {
  return {
    firstName: dto.clientFirstName,
    lastName: dto.clientLastName ?? undefined,
    phone: dto.clientPhone ?? '',
    email: dto.clientEmail,
  }
}

export function mapProReservationDetail(reservationId: string, dto: ProReservationDetailDto): ProReservationDetail {
  return {
    id: dto.id,
    prestataireId: '',
    prestataireName: dto.prestataireName,
    prestatairePhoto: dto.prestatairePhoto ?? undefined,
    category: dto.category,
    status: dto.statut as ProReservationDetail['status'],
    unreadNotesCount: 0,
    event: mapProReservationDetailEvent(reservationId, dto),
    clientInfo: mapProReservationDetailClientInfo(dto),
    progressType: null,
    progressValue: null,
    phraseInfoState: null,
  }
}

function mapActiveReservationItem(dto: ActiveReservationItemDto): ActiveReservationItem {
  return {
    reservationId: dto.reservationId,
    status: dto.status as ReservationStatus,
    evenementId: dto.evenementId,
    evenementTitle: dto.evenementTitle,
    prestataireSlug: dto.prestataireSlug,
    prestataireName: dto.prestataireName,
    prestataireAvatar: dto.prestataireAvatar,
  }
}

export function mapActiveReservations(dto: ActiveReservationsDto): ActiveReservations {
  return {
    items: dto.items.map(mapActiveReservationItem),
    hasConfirmed: dto.hasConfirmed,
  }
}
