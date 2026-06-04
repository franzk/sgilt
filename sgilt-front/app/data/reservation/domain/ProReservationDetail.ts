import type { ReservationMeta } from './ReservationMeta'
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ClientContactInfo } from './ClientContactInfo'

export interface ProReservationDetail extends ReservationMeta {
  event: EventDetail
  progressType: 'deadline' | 'duration' | 'temporal' | null
  progressValue: number | null
  phraseInfoState: string | null
  clientInfo: ClientContactInfo
}
