import type { EventReservationSummaryDto } from './EventReservationSummaryDto'

/**
 * DTO — rubrique d'un événement, avec les réservations qui y sont rangées
 */
export interface EventRubriqueDto {
  key: string
  reservations: EventReservationSummaryDto[]
}
