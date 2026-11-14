<!--
  Bloc « Ventes » de l'écran Billetterie du board, au-dessus des onglets : places vendues sur la jauge (avec une barre),
  places bloquées par un paiement en cours, places restantes. Lecture seule ; affiché seulement si
  la billetterie existe.
-->
<template>
  <section class="sales-block">
    <h2 class="section-title">{{ $t('evenement.page-publique.sales.title') }}</h2>
    <div class="card">
      <p class="sold">
        <span class="count">{{ sales.soldTickets }}</span>
        {{ $t('evenement.page-publique.sales.sold', { capacity: sales.capacity }, sales.soldTickets) }}
      </p>
      <div
        class="bar"
        role="progressbar"
        :aria-valuenow="sales.soldTickets"
        aria-valuemin="0"
        :aria-valuemax="sales.capacity"
      >
        <span class="fill" :style="{ width: `${soldPercent}%` }" />
      </div>
      <p class="detail">{{ $t('evenement.page-publique.sales.remaining', remaining) }}</p>
      <p v-if="sales.heldTickets" class="detail">
        {{ $t('evenement.page-publique.sales.held', sales.heldTickets) }}
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { TicketingSales } from '~/data/ticketing/domain/OrganizerPublicPage'

const props = defineProps<{
  sales: TicketingSales
}>()

// Bornée à 100 % : une jauge baissée ou dépassée ne déborde pas.
const soldPercent = computed(() =>
  props.sales.capacity ? Math.min(100, (props.sales.soldTickets / props.sales.capacity) * 100) : 0,
)

// Ni les places vendues ni celles en cours de paiement ; jamais négatif.
const remaining = computed(() =>
  Math.max(0, props.sales.capacity - props.sales.soldTickets - props.sales.heldTickets),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.sales-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .section-title {
    margin: 0;
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  .card {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    padding: $spacing-m;
    border-radius: $radius-lg;
    background: $surface-soft;

    .sold {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-primary;

      .count {
        font-size: 1.5rem;
        font-weight: 700;
        color: $brand-primary;
      }
    }

    .bar {
      height: 0.5rem;
      border-radius: 9999px;
      background: $divider-color;
      overflow: hidden;

      .fill {
        display: block;
        height: 100%;
        border-radius: 9999px;
        background: $brand-accent;
      }
    }

    .detail {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}
</style>
