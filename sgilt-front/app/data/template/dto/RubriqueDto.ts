import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'

export interface RubriqueDto {
  key: string
  reservations: ReservationSummary[]
}
