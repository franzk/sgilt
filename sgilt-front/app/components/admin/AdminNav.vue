<template>
  <nav class="admin-nav" aria-label="Sections admin">
    <NuxtLink
      v-for="tab in tabs"
      :key="tab.to"
      :to="tab.to"
      class="tab"
      exact-active-class="active"
    >
      {{ tab.label }}
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
const { t } = useI18n()

const tabs = computed(() => [
  { to: '/admin', label: t('admin.nav.prestataires') },
  { to: '/admin/onboarding', label: t('admin.nav.onboarding') },
  { to: '/admin/onboarding-clients', label: t('admin.nav.onboarding-clients') },
  { to: '/admin/reservations', label: t('admin.nav.reservations') },
])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-nav {
  display: flex;
  gap: 0.3rem;
  width: 100%;
  padding: 0.3rem;
  background: $surface-soft;
  border-radius: 2rem;
  overflow-x: auto;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }

  @media (min-width: $breakpoint-desktop) {
    justify-content: center;
  }
}

.tab {
  flex-shrink: 0;
  text-align: center;
  padding: 0.5rem 1rem;
  border-radius: 2rem;
  font-size: 0.85rem;
  font-weight: 500;
  color: $text-secondary;
  text-decoration: none;
  white-space: nowrap;
  transition: background 150ms ease, color 150ms ease;

  &:hover { color: $text-primary; }

  &.active {
    background: $color-primary;
    color: $text-inverted;
    font-weight: 600;
  }
}
</style>
