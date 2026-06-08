import { getReservationMetaApi, cancelReservationApi, getActiveReservationsApi } from '../api/clientReservationApi'
import {
  getProReservationsApi,
  getProBoardCountsApi,
  getProReservationDetailApi,
  markContactedApi,
  confirmReservationApi,
  refuseReservationApi,
} from '../api/proReservationApi'
import type { ClientContactInfo } from '../domain/ClientContactInfo'
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ProReservationDetail } from '../domain/ProReservationDetail'
import type { ActiveReservations, ActiveReservationItem } from '../domain/ActiveReservation'
import type { ProBoardCounts } from '../domain/ProBoardCounts'
import type { ProReservationSummary } from '../domain/ProReservationSummary'
import type { ReservationMeta } from '../domain/ReservationMeta'
import type { ReservationStatus } from '../domain/ReservationStatus'

export async function fetchReservationMeta(reservationId: string): Promise<ReservationMeta> {
  const dto = await getReservationMetaApi(reservationId)
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

export async function cancelReservation(reservationId: string): Promise<void> {
  await cancelReservationApi(reservationId)
}

export async function fetchProReservations(): Promise<ProReservationSummary[]> {
  const dtos = await getProReservationsApi()
  return dtos.map((dto) => ({
    id: dto.id,
    titre: dto.evenementTitre,
    eventType: dto.evenementType ?? undefined,
    date: new Date(dto.datePrestation),
    statut: dto.statut as ReservationStatus,
    image: dto.image ?? undefined,
    unreadNotesCount: dto.unreadNotesCount,
  }))
}

export async function fetchProBoardCounts(): Promise<ProBoardCounts> {
  return getProBoardCountsApi()
}

export async function fetchProReservationDetail(reservationId: string): Promise<ProReservationDetail> {
  const dto = await getProReservationDetailApi(reservationId)

  const event: EventDetail = {
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

  const clientInfo: ClientContactInfo = {
    firstName: dto.clientFirstName,
    lastName: dto.clientLastName ?? undefined,
    phone: dto.clientPhone ?? '',
    email: dto.clientEmail,
  }

  return {
    id: dto.id,
    prestataireId: '',
    prestataireName: dto.prestataireName,
    prestatairePhoto: dto.prestatairePhoto ?? undefined,
    category: dto.category,
    status: dto.statut as ProReservationDetail['status'],
    unreadNotesCount: 0,
    event,
    clientInfo,
    progressType: null,
    progressValue: null,
    phraseInfoState: null,
  }
}

export async function markDemandeContacted(reservationId: string): Promise<void> {
  await markContactedApi(reservationId)
}

export async function confirmReservation(reservationId: string): Promise<void> {
  await confirmReservationApi(reservationId)
}

export async function refuseReservation(reservationId: string, reason: string, communicate: boolean): Promise<void> {
  await refuseReservationApi(reservationId, reason, communicate)
}

export async function fetchActiveReservations(): Promise<ActiveReservations> {
  const dto = await getActiveReservationsApi()
  const items: ActiveReservationItem[] = dto.items.map((item) => ({
    reservationId: item.reservationId,
    status: item.status as ReservationStatus,
    evenementId: item.evenementId,
    evenementTitle: item.evenementTitle,
    prestataireSlug: item.prestataireSlug,
    prestataireName: item.prestataireName,
    prestataireAvatar: item.prestataireAvatar,
  }))
  return { items, hasConfirmed: dto.hasConfirmed }
}
