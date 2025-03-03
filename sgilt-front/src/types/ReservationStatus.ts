export type ReservationStatusKey = 'pending' | 'viewed' | 'approved' | 'paid' | 'cancelled'

export interface ReservationStatus {
  color: string
  icon: string
  state: string
  action: string
  actionColor?: string
  subtext: string
}
