<template>
  <div class="pro-greeting">
    <p class="title">{{ $t('pro.board.greeting.title') }}</p>
    <div v-if="loading" class="skeleton skeleton-text" />

    <div v-if="!loading && hasCounts" class="counts">
      <button
        v-for="card in visibleCards"
        :key="card.statut"
        class="count-card"
        :style="{ '--card-color': card.color, '--card-bg': card.bg }"
        type="button"
        @click="$emit('filter', card.statut)"
      >
        <span class="number">{{ card.value }}</span>
        <span class="label mobile-label">{{ card.labelMobile }}</span>
        <span class="label desktop-label">{{ card.labelDesktop }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ProBoardCounts } from '~/types/event'
import type { ReservationStatut } from '~/constants/reservation-status'

const props = defineProps<{
  subtitle: string
  loading?: boolean
  counts: ProBoardCounts
}>()

defineEmits<{
  filter: [statut: ReservationStatut]
}>()

const { t } = useI18n()

const hasCounts = computed(
  () =>
    props.counts.countNouvelle > 0 ||
    props.counts.countEnDiscussion > 0 ||
    props.counts.countConfirmee > 0,
)

const visibleCards = computed(() =>
  [
    {
      statut: 'nouvelle' as ReservationStatut,
      value: props.counts.countNouvelle,
      labelMobile: t('pro.greeting.count.nouvelle.mobile'),
      labelDesktop: t('pro.greeting.count.nouvelle.desktop', props.counts.countNouvelle),
      color: '#dc2626',
      bg: 'rgba(220, 38, 38, 0.05)',
    },
    {
      statut: 'en_discussion' as ReservationStatut,
      value: props.counts.countEnDiscussion,
      labelMobile: t('pro.greeting.count.en-discussion.mobile'),
      labelDesktop: t('pro.greeting.count.en-discussion.desktop', props.counts.countEnDiscussion),
      color: '#ea580c',
      bg: 'rgba(234, 88, 12, 0.05)',
    },
    {
      statut: 'confirmee' as ReservationStatut,
      value: props.counts.countConfirmee,
      labelMobile: t('pro.greeting.count.confirmee.mobile'),
      labelDesktop: t('pro.greeting.count.confirmee.desktop', props.counts.countConfirmee),
      color: '#16a34a',
      bg: 'rgba(22, 163, 74, 0.05)',
    },
  ].filter((c) => c.value > 0),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-greeting {
  display: flex;
  flex-direction: column;

  @media (min-width: $breakpoint-desktop) {
    gap: $spacing-l;
  }

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 2.1rem;
    font-weight: 700;
    color: $brand-primary;
    margin: 0;
    line-height: 1.1;
  }

  .subtitle {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
    margin: 0;
    margin-top: 4px;
  }

  .skeleton {
    margin-top: 6px;
    height: 0.8rem;
    width: 220px;
    border-radius: 4px;
  }

  // ── Cartes compteurs ────────────────────────────────────────────────────────
  .counts {
    display: flex;
    flex-direction: row;
    gap: $spacing-xs;
    margin-top: $spacing-s;
    overflow-x: auto;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }

    @media (min-width: $breakpoint-desktop) {
      flex-direction: column;
      overflow-x: visible;
      gap: $spacing-xs;
      margin-top: 0;
    }
  }

  .count-card {
    flex-shrink: 0;
    width: 90px;
    height: 90px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
    border: 1.5px solid var(--card-color);
    border-radius: $radius-md;
    background: var(--card-bg);
    cursor: pointer;
    padding: $spacing-xs;
    transition:
      transform 120ms ease,
      box-shadow 120ms ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    @media (min-width: $breakpoint-desktop) {
      width: 100%;
      height: auto;
      flex-direction: row;
      justify-content: flex-start;
      padding: $spacing-s $spacing-m;
      gap: $spacing-s;
    }

    .number {
      font-family: 'Inter', sans-serif;
      font-size: 1.6rem;
      font-weight: 600;
      line-height: 1;
      color: var(--card-color);

      @media (min-width: $breakpoint-desktop) {
        font-size: 1.6rem;
      }
    }

    .label {
      font-family: 'Inter', sans-serif;
      font-size: 0.65rem;
      font-weight: 600;
      letter-spacing: 0.04em;
      color: var(--card-color);
      text-align: center;

      @media (min-width: $breakpoint-desktop) {
        font-size: 0.78rem;
        text-align: left;
      }
    }

    .desktop-label {
      display: none;

      @media (min-width: $breakpoint-desktop) {
        display: inline;
      }
    }

    .mobile-label {
      @media (min-width: $breakpoint-desktop) {
        display: none;
      }
    }
  }
}
</style>
