export type NotificationType = 'state_change' | 'new_note' | 'new_document' | 'new_request'

export interface AppNotification {
  id: string
  type: NotificationType
  read: boolean
  createdAt: string // ISO string
  title: string
  body?: string
  href: string // cible de navigation au clic
}
