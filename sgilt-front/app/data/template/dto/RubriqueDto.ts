import type { RubriqueReservation } from '~/constants/event-rubriques'

export interface RubriqueDto {
  key: string
  reservations: RubriqueReservation[]
}
