<template>
  <component
    :is="notification.href ? 'a' : 'div'"
    class="notif-item"
    :class="{ unread: !notification.read }"
    :href="notification.href || undefined"
    @click="onClick"
  >
    <div class="notif-item__icon" :class="notification.type">
      <ExchangeIcon v-if="notification.type === 'state_change'" />
      <Chat1Icon v-else-if="notification.type === 'new_note'" />
      <FileAddIcon v-else-if="notification.type === 'new_document'" />
      <MailSendIcon v-else-if="notification.type === 'new_request'" />
    </div>

    <div class="notif-item__body">
      <p class="notif-item__title">{{ notification.title }}</p>
      <p v-if="notification.body" class="notif-item__desc">{{ notification.body }}</p>
      <time class="notif-item__time">{{ formatDateTime(notification.createdAt) }}</time>
    </div>

    <span v-if="!notification.read" class="notif-item__dot" :aria-label="$t('notifications.item.unread-aria')" />
  </component>
</template>

<script setup lang="ts">
import type { AppNotification } from '~/types/notification'
import { ExchangeIcon, Chat1Icon, FileAddIcon, MailSendIcon } from '@remixicons/vue/line'

const props = defineProps<{ notification: AppNotification }>()
const emit = defineEmits<{ click: [id: string] }>()

function onClick(e: Event) {
  e.preventDefault()
  emit('click', props.notification.id)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.notif-item {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
  text-decoration: none;
  cursor: pointer;
  transition: background 120ms ease;
  position: relative;

  &:hover {
    background: $surface-soft;
  }

  &.unread {
    background: rgba($brand-accent, 0.05);
  }

  // ── Icône ────────────────────────────────────────────────────────────────────
  &__icon {
    flex-shrink: 0;
    width: 2rem;
    height: 2rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 2px;

    svg {
      width: 1rem;
      height: 1rem;
    }

    &.state_change {
      background: rgba($brand-accent, 0.15);
      color: #9a7a00;
    }
    &.new_note {
      background: rgba(#3b82f6, 0.1);
      color: #2563eb;
    }
    &.new_document {
      background: rgba(#8b5cf6, 0.1);
      color: #7c3aed;
    }
    &.new_request {
      background: rgba(#10b981, 0.1);
      color: #059669;
    }
  }

  // ── Corps ────────────────────────────────────────────────────────────────────
  &__body {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__title {
    font-family: 'Inter', sans-serif;
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
    line-height: 1.3;
  }

  &__desc {
    font-family: 'Inter', sans-serif;
    font-size: 0.78rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.4;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  &__time {
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    color: $text-secondary;
    opacity: 0.6;
    margin-top: 2px;
  }

  // ── Point non lu ─────────────────────────────────────────────────────────────
  &__dot {
    flex-shrink: 0;
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: $brand-accent;
    margin-top: 6px;
  }
}
</style>
