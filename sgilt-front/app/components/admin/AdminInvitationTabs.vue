<!--
  Onglets de la section Invitations du BO : la liste des invitations client et le formulaire
  d'invitation (création du client, de son événement et de sa réservation confirmée).
-->
<template>
  <nav class="admin-invitation-tabs" aria-label="Sections invitations">
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
  { to: '/admin/invitations', label: t('admin.invitations.tabs.list') },
  { to: '/admin/inviter-client', label: t('admin.invitations.tabs.create') },
])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-invitation-tabs {
  display: flex;
  gap: 0.3rem;
  width: 100%;
  padding: 0.3rem;
  background: $surface-soft;
  border-radius: 2rem;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 0.5rem 1rem;
  border-radius: 2rem;
  font-size: 0.85rem;
  font-weight: 500;
  color: $text-secondary;
  text-decoration: none;
  transition:
    background 150ms ease,
    color 150ms ease;

  &:hover {
    color: $text-primary;
  }

  &.active {
    background: $color-primary;
    color: $text-inverted;
    font-weight: 600;
  }
}
</style>
