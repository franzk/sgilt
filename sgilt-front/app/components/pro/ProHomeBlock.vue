<!--
  Bloc condensé de l'accueil prestataire (/pro) : un titre (avec nombre éventuel), un lien vers la
  liste complète, et au plus les 3 premières réservations de la liste reçue (dans l'ordre où la
  page la fournit), regroupées dans une carte.
  Phrase courte si la liste est vide.
-->
<template>
  <section class="pro-home-block">
    <div class="header">
      <h2 class="title">
        {{ title }}<template v-if="count !== undefined"> ({{ count }})</template>
      </h2>
      <NuxtLink :to="to" class="link">
        {{ linkLabel }}
        <ArrowRightIcon class="icon" />
      </NuxtLink>
    </div>

    <div v-if="loading" class="card">
      <div v-for="i in 2" :key="i" class="row-skeleton">
        <Sk width="3.5rem" height="3.5rem" radius="0.625rem" />
        <div class="lines">
          <Sk width="70%" height="1rem" radius="0.25rem" />
          <Sk width="40%" height="1.25rem" radius="2rem" />
        </div>
      </div>
    </div>

    <p v-else-if="reservations.length === 0" class="empty">{{ emptyText }}</p>

    <div v-else class="card">
      <ProReservationRow
        v-for="reservation in reservations.slice(0, MAX_ITEMS)"
        :key="reservation.id"
        :reservation="reservation"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ArrowRightIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'
import ProReservationRow from '~/components/pro/ProReservationRow.vue'
import type { ProReservationSummary } from '~/data/reservation/domain/ProReservationSummary'

const MAX_ITEMS = 3

defineProps<{
  title: string
  /** Nombre total affiché à côté du titre ; absent = pas de nombre. */
  count?: number
  to: string
  linkLabel: string
  reservations: ProReservationSummary[]
  emptyText: string
  loading?: boolean
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-home-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: $spacing-s;
    padding: 0 0.25rem;

    .title {
      margin: 0;
      font-size: 1.375rem;
      font-weight: 600;
      color: $text-primary;
    }

    .link {
      display: inline-flex;
      align-items: center;
      gap: 0.25rem;
      flex-shrink: 0;
      font-size: 0.9375rem;
      font-weight: 500;
      color: $brand-accent-text;
      text-decoration: none;

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }
  }

  .card {
    display: flex;
    flex-direction: column;
    border-radius: $radius-lg;
    background: $surface-white;
    border: 1px solid $divider-color;
    box-shadow: 0 0.25rem 1rem $shadow-s;
    overflow: hidden;

    > * + * {
      border-top: 1px solid $divider-color;
    }

    .row-skeleton {
      display: flex;
      align-items: center;
      gap: $spacing-s;
      padding: $spacing-s $spacing-m;

      .lines {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
      }
    }
  }

  .empty {
    margin: 0;
    padding: 0 0.25rem;
    font-size: 0.875rem;
    color: $text-secondary;
  }
}
</style>
