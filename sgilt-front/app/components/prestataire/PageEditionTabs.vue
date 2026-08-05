<template>
  <nav class="page-edition-tabs" aria-label="Sections de la fiche">
    <NuxtLink
      v-for="tab in tabs"
      :key="tab.to"
      :to="tab.to"
      class="tab"
      active-class="active"
    >
      {{ tab.label }}
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
const { t } = useI18n()

const tabs = computed(() => [
  { to: '/pro/page-edition/edition', label: t('provider.edit.tabs.edition') },
  { to: '/pro/page-edition/preview', label: t('provider.edit.tabs.preview') },
  { to: '/pro/page-edition/ia', label: t('provider.edit.tabs.ia') },
])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.page-edition-tabs {
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
