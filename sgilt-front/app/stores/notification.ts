import { defineStore } from 'pinia'
import type { AppNotification } from '~/types/notification'
import { NotificationMockService } from '~/services/notification.mock'

const PAGE_SIZE = 10

export const useNotificationStore = defineStore('notifications', () => {
  const notifications = ref<AppNotification[]>([])
  const hasMore = ref(true)
  const loading = ref(false)

  const unreadCount = computed(() => notifications.value.filter((n) => !n.read).length)

  async function fetchInitial() {
    if (notifications.value.length > 0) return
    loading.value = true
    notifications.value = await NotificationMockService.fetchPage(0)
    hasMore.value = notifications.value.length === PAGE_SIZE
    loading.value = false
  }

  async function fetchMore() {
    if (!hasMore.value || loading.value) return
    loading.value = true
    const page = Math.floor(notifications.value.length / PAGE_SIZE)
    const next = await NotificationMockService.fetchPage(page)
    if (next.length < PAGE_SIZE) hasMore.value = false
    notifications.value.push(...next)
    loading.value = false
  }

  function markAsRead(id: string) {
    const n = notifications.value.find((n) => n.id === id)
    if (n && !n.read) {
      n.read = true
      NotificationMockService.markAsRead(id)
    }
  }

  async function markAllAsRead() {
    await NotificationMockService.markAllAsRead()
    notifications.value.forEach((n) => (n.read = true))
  }

  return { notifications, unreadCount, hasMore, loading, fetchInitial, fetchMore, markAsRead, markAllAsRead }
})
