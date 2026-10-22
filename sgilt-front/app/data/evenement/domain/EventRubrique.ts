/**
 * Domaine — rubrique d'un événement, avec les réservations qui y sont rangées. Commune à
 * l'événement local du parcours public (réservations toujours vides) et à l'événement en base.
 */
import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'

export interface EventRubrique {
  key: string
  reservations: ReservationSummary[]
}
