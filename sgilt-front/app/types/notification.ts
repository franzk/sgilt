export type NotificationType = 'state_change' | 'new_note' | 'new_document' | 'new_request'

export interface AppNotification {
  id: string
  type: NotificationType
  read: boolean
  createdAt: string // ISO string
  messageKey: string // clé i18n, résolue en `${messageKey}.title` / `${messageKey}.body`
  params: Record<string, string> // paramètres d'interpolation du message
  href: string // cible de navigation au clic
}
