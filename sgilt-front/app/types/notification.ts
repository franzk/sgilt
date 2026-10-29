/**
 * Types — notification in-app, telle que servie par sgilt-notifications. NotificationType est le
 * miroir de l'enum NotificationType côté back (en snake_case).
 */
export type NotificationType ='state_change' | 'new_note' | 'new_document' | 'new_request' | 'reminder'

export interface AppNotification {
  id: string
  type: NotificationType
  read: boolean
  createdAt: string // ISO string
  messageKey: string // clé i18n, résolue en `${messageKey}.title` / `${messageKey}.body`
  params: Record<string, string> // paramètres d'interpolation du message
  href: string // cible de navigation au clic
}
