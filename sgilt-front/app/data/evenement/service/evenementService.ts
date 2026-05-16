/**
 * Couche service — orchestration des appels API evenement
 */
import { getEvenementsApi, getEventDetail, getEventCounts, getEventReservations, getEventJournal, patchEventApi, uploadEventCoverApi, selectEventCoverApi, addReservationApi } from '../api/evenementApi'
import { mapEvenementSummary, mapEventDetail, mapEventCounts, mapEventReservation, mapJournalEntry } from '../mapper/evenementMapper'
import type { EventSummary } from '../domain/EventSummary'
import type { EventDetail } from '../domain/EventDetail'
import type { EventCounts } from '../domain/EventCounts'
import type { EventPatch } from '../domain/EventPatch'
import type { JournalEntry } from '../domain/JournalEntry'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { Reservation } from '~/data/reservation/domain/Reservation'

export async function getEvenements(): Promise<EventSummary[]> {
  const dtos = await getEvenementsApi()
  return dtos.map(mapEvenementSummary)
}

export async function fetchEventDetail(id: string): Promise<{ event: EventDetail; clientInfo: ClientContactInfo }> {
  const dto = await getEventDetail(id)
  return mapEventDetail(dto)
}

export async function fetchEventCounts(id: string): Promise<EventCounts> {
  const dto = await getEventCounts(id)
  return mapEventCounts(dto)
}

export async function fetchEventReservations(id: string): Promise<Reservation[]> {
  const dtos = await getEventReservations(id)
  return dtos.map(mapEventReservation)
}

export async function fetchEventJournal(id: string, page: number): Promise<{ entries: JournalEntry[]; last: boolean }> {
  const dto = await getEventJournal(id, page)
  return { entries: dto.content.map(mapJournalEntry), last: dto.last }
}

export async function patchEvent(eventId: string, patch: EventPatch): Promise<EventDetail> {
  const dto = await patchEventApi(eventId, patch)
  return mapEventDetail(dto).event
}

export async function uploadEventCover(eventId: string, file: File): Promise<string> {
  const dto = await uploadEventCoverApi(eventId, file)
  return dto.imagePath
}

export async function selectEventCover(eventId: string, imagePath: string): Promise<string> {
  const dto = await selectEventCoverApi(eventId, imagePath)
  return dto.imagePath
}

export async function addReservation(eventId: string, prestataireId: string, message: string | null): Promise<void> {
  await addReservationApi(eventId, prestataireId, message)
}
