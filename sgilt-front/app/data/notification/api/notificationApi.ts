/**
 * Couche API — appels HTTP vers /notifications
 */
import { apiFetch } from '~/composables/useApi'
import type { NotificationPageDto } from '../dto/NotificationDto'

export async function getNotificationsPageApi(page: number): Promise<NotificationPageDto> {
  return apiFetch<NotificationPageDto>('/notifications', { params: { page } })
}

export async function markNotificationReadApi(id: string): Promise<void> {
  return apiFetch<void>(`/notifications/${id}/read`, { method: 'PATCH' })
}

export async function markAllNotificationsReadApi(): Promise<void> {
  return apiFetch<void>('/notifications/read', { method: 'PATCH' })
}
