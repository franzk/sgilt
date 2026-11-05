<!--
  Carte « Page publique » de l'accueil du board (mobile) : affichée seulement si le back-office a
  activé la page publique de l'événement ; montre son état (à créer, brouillon, publiée) et ouvre
  l'écran où l'organisateur l'administre. La page décide de la navigation.
-->
<template>
  <section class="public-page">
    <h2 class="section-title">{{ $t('evenement.page-publique.title') }}</h2>
    <button class="card" type="button" @click="$emit('open')">
      <span class="icon-wrap" aria-hidden="true">
        <GlobalIcon class="icon" />
      </span>
      <span class="text">
        <span class="name">{{ $t('evenement.page-publique.card.name') }}</span>
        <span class="status" :class="status.toLowerCase()">
          {{ $t(`evenement.page-publique.status.${status}.label`) }}
        </span>
      </span>
      <ArrowRightSIcon class="chevron" aria-hidden="true" />
    </button>
  </section>
</template>

<script setup lang="ts">
import { ArrowRightSIcon, GlobalIcon } from '@remixicons/vue/line'
import type { PublicPageStatus } from '~/data/ticketing/domain/OrganizerPublicPage'

defineProps<{
  status: PublicPageStatus
}>()

defineEmits<{
  open: []
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Même présentation que la liste des rubriques du board.
.public-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .section-title {
    margin: 0;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  .card {
    appearance: none;
    display: flex;
    align-items: center;
    gap: $spacing-s;
    width: auto;
    margin: 0 (-$spacing-m);
    padding: $spacing-m;
    border: none;
    background: $surface-white;
    font-family: 'Inter', sans-serif;
    text-align: left;
    color: $brand-primary;
    cursor: pointer;
    transition: background 120ms ease;

    &:active {
      background: $surface-soft;
    }

    .icon-wrap {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 2.5rem;
      height: 2.5rem;
      border-radius: 50%;
      flex-shrink: 0;
      background: $brand-primary;

      .icon {
        width: 1.25rem;
        height: 1.25rem;
        color: $surface-white;
      }
    }

    .text {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.125rem;
      min-width: 0;

      .name {
        font-size: 0.95rem;
        font-weight: 600;
      }

      .status {
        font-size: 0.8rem;
        color: $text-secondary;

        &.published {
          color: $state-success;
          font-weight: 600;
        }
      }
    }

    .chevron {
      width: 1.125rem;
      height: 1.125rem;
      flex-shrink: 0;
      color: $text-secondary;
    }
  }
}
</style>
