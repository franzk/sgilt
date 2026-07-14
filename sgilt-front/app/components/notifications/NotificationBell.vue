<template>
  <div class="notif-bell">
    <button
      ref="bellRef"
      class="notif-bell__btn"
      type="button"
      :aria-label="`Notifications${unreadCount ? ` (${unreadCount} non lues)` : ''}`"
      aria-haspopup="true"
      :aria-expanded="popinOpen"
      aria-controls="notif-popin"
      @click="toggle"
    >
      <IconBell class="notif-bell__icon" />
      <span v-if="unreadCount > 0" class="notif-bell__badge">
        {{ unreadCount > 9 ? '9+' : unreadCount }}
      </span>
    </button>

    <NotificationPopin :open="popinOpen" :anchor-el="bellRef" @close="popinOpen = false" />
  </div>
</template>

<script setup lang="ts">
import IconBell from '~/components/icons/IconBell.vue'
import NotificationPopin from './NotificationPopin.vue'
import { useNotifications } from '~/data/notification/useNotifications'

const { unreadCount } = useNotifications()
const popinOpen = ref(false)
const bellRef = ref<HTMLElement | null>(null)

function toggle() {
  popinOpen.value = !popinOpen.value
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.notif-bell {
  position: relative;

  &__btn {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.25rem;
    height: 2.25rem;
    border-radius: 50%;
    border: none;
    background: none;
    color: $text-primary;
    cursor: pointer;
    transition: background 150ms ease;

    &:hover {
      background: $surface-soft;
    }
  }

  &__icon {
    width: 1.25rem;
    height: 1.25rem;
  }

  &__badge {
    position: absolute;
    top: 2px;
    right: 2px;
    min-width: 16px;
    height: 16px;
    padding: 0 3px;
    border-radius: 2rem;
    background: $brand-accent;
    color: $brand-primary;
    font-family: 'Inter', sans-serif;
    font-size: 0.6rem;
    font-weight: 700;
    line-height: 16px;
    text-align: center;
    border: 1.5px solid #fff;
  }
}
</style>
