<template>
  <header class="event-header">
    <NuxtLink to="/app/events" class="logo">
      <img src="/sgilt-logo.svg" alt="SGILT" />
    </NuxtLink>

    <div class="actions">
      <NuxtLink to="/app/notifications" class="action" aria-label="Notifications">
        <span class="bell-wrap">
          <NotificationIcon class="icon" />
          <span v-if="hasNotifications" class="notif-dot" />
        </span>
      </NuxtLink>

      <NuxtLink to="/app/profile" class="action" aria-label="Profil">
        <UserAvatar :size="2" />
      </NuxtLink>
    </div>
  </header>
</template>

<script setup lang="ts">
import { NotificationIcon } from '@remixicons/vue/line'
import UserAvatar from '~/components/basics/UserAvatar.vue'

defineProps<{
  hasNotifications?: boolean
}>()
</script>

<style scoped lang="scss">
$header-h: 52px;

.event-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-header;
  height: $header-h;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-m;
  background: #fff;
  border-bottom: 1px solid $divider-color;

  .logo img {
    height: 1.6rem;
    display: block;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: $spacing-s;
  }

  .action {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.25rem;
    height: 2.25rem;
    border-radius: 50%;
    color: $text-primary;
    transition: background 150ms ease;

    &:hover {
      background: $surface-soft;
    }
  }

  .icon {
    width: 22px;
    height: 22px;
  }

  .bell-wrap {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .notif-dot {
    position: absolute;
    top: -1px;
    right: -1px;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: $brand-accent;
    border: 1.5px solid #fff;
  }

}
</style>
