/**
 * Composable — expose l'état partagé des notifications (cloche, popin, page dédiée)
 */
import {
  fetchNotificationsPage,
  markNotificationRead,
  markAllNotificationsRead,
} from './service/notificationService'
import type { AppNotification } from '~/types/notification'

// ── Singleton state (module-level, shared across all useNotifications calls) ───

const _notifications = ref<AppNotification[]>([])
const _hasMore = ref(true)
const _loading = ref(true)
const _error = ref<unknown>(null)
let _page = 0
let _fetchStarted = false

// ── Composable ────────────────────────────────────────────────────────────────

export function useNotifications() {
  const unreadCount = computed(() => _notifications.value.filter((n) => !n.read).length)

  onMounted(async () => {
    if (_fetchStarted) return
    _fetchStarted = true
    try {
      const result = await fetchNotificationsPage(0)
      _notifications.value = result.items
      _hasMore.value = result.hasMore
      _page = 0
    } catch (e) {
      _error.value = e
    } finally {
      _loading.value = false
    }
  })

  async function fetchMore() {
    if (!_hasMore.value || _loading.value) return
    _loading.value = true
    try {
      const result = await fetchNotificationsPage(_page + 1)
      _notifications.value.push(...result.items)
      _hasMore.value = result.hasMore
      _page += 1
    } catch (e) {
      _error.value = e
    } finally {
      _loading.value = false
    }
  }

  async function markAsRead(id: string) {
    const n = _notifications.value.find((n) => n.id === id)
    if (n && !n.read) {
      n.read = true
      await markNotificationRead(id)
    }
  }

  async function markAllAsRead() {
    await markAllNotificationsRead()
    _notifications.value.forEach((n) => (n.read = true))
  }

  return {
    notifications: _notifications,
    unreadCount,
    hasMore: _hasMore,
    loading: _loading,
    error: _error,
    fetchMore,
    markAsRead,
    markAllAsRead,
  }
}
