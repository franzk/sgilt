<template>
  <div class="notif-page">
    <div class="notif-page__toolbar">
      <h1 class="notif-page__heading">{{ $t('notifications.page.heading') }}</h1>
      <button
        v-if="store.unreadCount > 0"
        class="notif-page__mark-all"
        type="button"
        @click="store.markAllAsRead()"
      >
        {{ $t('notifications.page.mark-all') }}
      </button>
    </div>

    <div class="notif-page__list">
      <p v-if="store.notifications.length === 0 && !store.loading" class="notif-page__empty">
        {{ $t('notifications.page.empty') }}
      </p>

      <template v-for="(group, index) in groups" :key="group.label">
        <div v-if="index > 0" class="notif-page__divider" />
        <p class="notif-page__group-label">{{ group.label }}</p>
        <NotificationItem
          v-for="n in group.items"
          :key="n.id"
          :notification="n"
          @click="onItemClick"
        />
      </template>

      <div ref="sentinelRef" class="notif-page__sentinel" />

      <p v-if="store.loading" class="notif-page__loading">{{ $t('notifications.page.loading') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import NotificationItem from '~/components/notifications/NotificationItem.vue'
import { useNotificationStore } from '~/stores/notification'

definePageMeta({ layout: 'account' })

const { t } = useI18n()
const store = useNotificationStore()
const sentinelRef = ref<HTMLElement | null>(null)

onMounted(() => {
  store.fetchInitial()

  const observer = new IntersectionObserver(
    (entries) => {
      if (entries[0]?.isIntersecting) store.fetchMore()
    },
    { rootMargin: '200px' },
  )
  if (sentinelRef.value) observer.observe(sentinelRef.value)
  onUnmounted(() => observer.disconnect())
})

type Group = { label: string; items: typeof store.notifications }

const groups = computed<Group[]>(() => {
  const unread = store.notifications.filter((n) => !n.read)
  const read = store.notifications.filter((n) => n.read)
  const result: Group[] = []
  if (unread.length) result.push({ label: t('notifications.page.group-unread'), items: unread })
  if (read.length) result.push({ label: t('notifications.page.group-read'), items: read })
  return result
})

function onItemClick(id: string) {
  store.markAsRead(id)
  const n = store.notifications.find((n) => n.id === id)
  if (n?.href) navigateTo(n.href)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.notif-page {
  max-width: 640px;
  margin: 0 auto;
  padding: $spacing-l $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__heading {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    margin: 0;
    color: $text-primary;
  }

  &__mark-all {
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.8rem;
    color: $brand-primary;
    cursor: pointer;
    padding: 0;
    opacity: 0.7;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 1;
    }
  }

  &__list {
    display: flex;
    flex-direction: column;
    background: #fff;
    border-radius: $radius-lg;
    border: 1px solid $divider-color;
    overflow: hidden;
  }

  &__group-label {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.07em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0;
    padding: $spacing-xs $spacing-m;
    background: $surface-soft;
  }

  &__divider {
    height: 1px;
    background: $divider-color;
    margin-top: $spacing-xs;
  }

  &__empty,
  &__loading {
    font-size: 0.85rem;
    color: $text-secondary;
    font-style: italic;
    text-align: center;
    padding: $spacing-xl;
    margin: 0;
  }

  &__sentinel {
    height: 1px;
  }
}
</style>
