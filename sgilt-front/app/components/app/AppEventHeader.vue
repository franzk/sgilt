<template>
  <header class="event-header">
    <NuxtLink to="/app/events" class="logo">
      <img src="/sgilt-logo.svg" alt="SGILT" />
    </NuxtLink>

    <div class="actions">
      <NuxtLink to="/app/notifications" class="action" aria-label="Notifications">
        <span class="bell-wrap">
          <IconBell class="icon" />
          <span v-if="hasNotifications" class="notif-dot" />
        </span>
      </NuxtLink>

      <NuxtLink to="/app/profile" class="action" aria-label="Profil">
        <div class="avatar">
          <img
            v-if="avatarUrl"
            :src="avatarUrl"
            :alt="initials"
            class="avatar-img"
          />
          <span v-else class="avatar-initials">{{ initials }}</span>
        </div>
      </NuxtLink>
    </div>
  </header>
</template>

<script setup lang="ts">
import IconBell from '~/components/icons/IconBell.vue'

defineProps<{
  hasNotifications?: boolean
  avatarUrl?: string
  initials?: string
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

  .avatar {
    width: 2rem;
    height: 2rem;
    border-radius: 50%;
    background: $brand-subtle;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-initials {
    font-family: 'Cormorant Garamond', serif;
    font-size: 0.85rem;
    font-weight: 500;
    color: $text-primary;
    line-height: 1;
  }
}
</style>
