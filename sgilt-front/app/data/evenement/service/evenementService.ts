/**
 * Couche service — orchestration des appels API evenement
 */
import {
  getEvenementsApi,
  getEventApi,
  getEventCountsApi,
  getEventJournalApi,
  patchEventApi,
  uploadEventCoverApi,
  selectEventCoverApi,
  addReservationApi,
} from '../api/evenementApi'
import { getReservationsByEventApi } from '~/data/reservation/api/reservationApi'
import {
  mapEvenementSummary,
  mapEventMeta,
  mapEventRubrique,
  mapEventCounts,
  mapEventReservation,
  mapJournalEntry,
} from '../mapper/evenementMapper'
import type { EventSummary } from '../domain/EventSummary'
import type { EventMeta } from '../domain/EventMeta'
import type { EventRubrique } from '../domain/EventRubrique'
import type { EventCounts } from '../domain/EventCounts'
import type { EventMetaPatch } from '../domain/EventMetaPatch'
import type { JournalEntry } from '../domain/JournalEntry'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'

export async function getEvenements(): Promise<EventSummary[]> {
  const dtos = await getEvenementsApi()
  return dtos.map(mapEvenementSummary)
}

export async function fetchEvent(id: string): Promise<{
  event: EventMeta
  clientInfo: ClientContactInfo
  rubriques: EventRubrique[]
}> {
  const dto = await getEventApi(id)
  return { ...mapEventMeta(dto.meta), rubriques: dto.rubriques.map(mapEventRubrique) }
}

export async function fetchEventCounts(id: string): Promise<EventCounts> {
  const dto = await getEventCountsApi(id)
  return mapEventCounts(dto)
}

export async function fetchEventReservations(id: string): Promise<ReservationSummary[]> {
  const dtos = await getReservationsByEventApi(id)
  return dtos.map(mapEventReservation)
}

export async function fetchEventJournal(
  id: string,
  page: number,
): Promise<{ entries: JournalEntry[]; last: boolean }> {
  const dto = await getEventJournalApi(id, page)
  return { entries: dto.content.map(mapJournalEntry), last: dto.last }
}

export async function patchEvent(eventId: string, patch: EventMetaPatch): Promise<EventMeta> {
  const dto = await patchEventApi(eventId, patch)
  return mapEventMeta(dto).event
}

export async function uploadEventCover(eventId: string, file: File): Promise<string> {
  const dto = await uploadEventCoverApi(eventId, file)
  return dto.imagePath
}

export async function selectEventCover(eventId: string, imagePath: string): Promise<string> {
  const dto = await selectEventCoverApi(eventId, imagePath)
  return dto.imagePath
}

export async function addReservation(
  eventId: string,
  prestataireId: string,
  message: string | null,
): Promise<void> {
  await addReservationApi(eventId, prestataireId, message)
}
