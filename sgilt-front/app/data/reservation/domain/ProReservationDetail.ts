import type { ReservationMeta } from './ReservationMeta'
import type { EventMeta } from '~/data/evenement/domain/EventMeta'
import type { ClientContactInfo } from './ClientContactInfo'

export interface ProReservationDetail extends ReservationMeta {
  event: EventMeta
  progressType: 'deadline' | 'duration' | 'temporal' | null
  progressValue: number | null
  phraseInfoState: string | null
  clientInfo: ClientContactInfo
}
