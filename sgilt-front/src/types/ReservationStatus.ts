export type ReservationStatusKey =
  | 'pending'
  | 'viewed'
  | 'approved'
  | 'paid'
  | 'expired'
  | 'cancelled'

export interface ReservationStatus {
  colors: string[]
  icon: string
  state: string
  action: string
  actionColor?: string
  subtext: string
}
