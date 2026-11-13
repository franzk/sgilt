<!--
  Carte « Billetterie » de l'accueil du board (mobile) : affichée seulement si le back-office a
  activé la billetterie de l'événement ; ouvre l'écran où l'organisateur suit ses ventes et règle
  sa billetterie. La page décide de la navigation. Même forme que la carte « Page publique ».
-->
<template>
  <section class="billetterie">
    <h2 class="section-title">{{ $t('evenement.billetterie.title') }}</h2>
    <button class="card" type="button" @click="$emit('open')">
      <span class="icon-wrap" aria-hidden="true">
        <TicketIcon class="icon" />
      </span>
      <span class="text">
        <span class="name">{{ $t('evenement.billetterie.card.name') }}</span>
        <span class="detail">{{ $t('evenement.billetterie.card.detail') }}</span>
      </span>
      <ArrowRightSIcon class="chevron" aria-hidden="true" />
    </button>
  </section>
</template>

<script setup lang="ts">
import { ArrowRightSIcon, TicketIcon } from '@remixicons/vue/line'

defineEmits<{
  open: []
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Même présentation que la liste des rubriques du board.
.billetterie {
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

      .detail {
        font-size: 0.8rem;
        color: $text-secondary;
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
