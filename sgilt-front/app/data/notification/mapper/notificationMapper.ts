/**
 * Mapper — conversions DTO → domaine pour le module notification
 */
import type { NotificationDto } from '../dto/NotificationDto'
import type { AppNotification, NotificationType } from '~/types/notification'

const NOTIFICATION_TYPES: readonly NotificationType[] = [
  'state_change',
  'new_note',
  'new_document',
  'new_request',
]

function isNotificationType(value: string): value is NotificationType {
  return (NOTIFICATION_TYPES as readonly string[]).includes(value)
}

export function mapNotification(dto: NotificationDto): AppNotification {
  if (!isNotificationType(dto.type)) {
    throw new Error(`Type de notification inconnu : ${dto.type}`)
  }

  return {
    id: dto.id,
    type: dto.type,
    read: dto.read,
    createdAt: dto.createdAt,
    messageKey: dto.messageKey,
    params: dto.params,
    href: dto.href,
  }
}
