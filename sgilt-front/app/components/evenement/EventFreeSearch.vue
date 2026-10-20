<!--
  Section « choix libre » de l'accueil d'un événement de l'espace connecté : pour chercher un
  prestataire dans tout le catalogue, sans passer par une rubrique. La page décide de la recherche
  ouverte au clic (le parcours d'ajout à l'événement, sans préfiltre).
-->
<template>
  <section class="event-free-search">
    <div class="text">
      <h2 class="title">{{ $t('evenement.home.free-search.title') }}</h2>
      <p class="description">{{ $t('evenement.home.free-search.description') }}</p>
    </div>
    <button class="search-btn" type="button" @click="$emit('search')">
      <SearchIcon class="icon" aria-hidden="true" />
      {{ $t('evenement.home.free-search.button') }}
    </button>
  </section>
</template>

<script setup lang="ts">
import { SearchIcon } from '@remixicons/vue/line'

defineEmits<{
  search: []
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-free-search {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-l;
  border-radius: $radius-lg;
  background: $surface-soft;

  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }

  .text {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;

    .title {
      margin: 0;
      font-size: $font-size-lg;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .description {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-secondary;
      line-height: 1.5;
    }
  }

  .search-btn {
    display: inline-flex;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    padding: $spacing-s $spacing-l;
    border: 1px solid $brand-accent;
    border-radius: 9999px;
    background: $brand-accent;
    color: $brand-primary;
    font-family: inherit;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    cursor: pointer;

    &:hover {
      filter: brightness(0.96);
    }

    .icon {
      width: 1.125rem;
      height: 1.125rem;
    }
  }
}
</style>
