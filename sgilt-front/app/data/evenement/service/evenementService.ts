/**
 * Couche service — orchestration des appels API evenement
 */
import { getEvenementsApi, getEventDetail, getEventCounts, getEventReservations, getEventJournal } from '../api/evenementApi'
import { mapEvenementSummary, mapEventDetail, mapEventCounts, mapEventReservation, mapJournalEntry } from '../mapper/evenementMapper'
import type { EventSummary } from '../domain/EventSummary'
import type { EventDetail } from '../domain/EventDetail'
import type { EventCounts } from '../domain/EventCounts'
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
