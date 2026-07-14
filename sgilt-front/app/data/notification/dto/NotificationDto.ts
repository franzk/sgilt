/**
 * DTO — contrat de réponse de l'API GET /notifications
 */

export interface NotificationDto {
  id: string
  type: string
  read: boolean
  createdAt: string
  messageKey: string
  params: Record<string, string>
  href: string
}

/** Sous-ensemble du Page<T> Spring utilisé par le front */
export interface NotificationPageDto {
  items: NotificationDto[]
  hasMore: boolean
}
