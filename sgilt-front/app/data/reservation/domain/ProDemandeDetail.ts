/**
 * Domaine — vue pro d'une réservation détaillée (étend ReservationDetail)
 */
import type { ReservationDetail } from './ReservationDetail'
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ClientContactInfo } from './ClientContactInfo'

export interface ProDemandeDetail extends ReservationDetail {
  event: EventDetail
  progressType: 'deadline' | 'duration' | 'temporal' | null
  progressValue: number | null
  phraseInfoState: string | null
  clientInfo: ClientContactInfo
}
