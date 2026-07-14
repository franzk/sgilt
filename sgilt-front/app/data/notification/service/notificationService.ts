/**
 * Couche service — orchestration des appels API notification
 */
import {
  getNotificationsPageApi,
  markNotificationReadApi,
  markAllNotificationsReadApi,
} from '../api/notificationApi'
import { mapNotification } from '../mapper/notificationMapper'
import type { AppNotification } from '~/types/notification'

export async function fetchNotificationsPage(
  page: number,
): Promise<{ items: AppNotification[]; hasMore: boolean }> {
  const dto = await getNotificationsPageApi(page)
  return { items: dto.items.map(mapNotification), hasMore: dto.hasMore }
}

export async function markNotificationRead(id: string): Promise<void> {
  return markNotificationReadApi(id)
}

export async function markAllNotificationsRead(): Promise<void> {
  return markAllNotificationsReadApi()
}
