/**
 * Mapper — conversion EvenementSummaryDto → EventSummary
 */
import type { EvenementSummaryDto } from '../dto/evenementDto'
import type { EventSummary } from '../domain/EventSummary'

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
