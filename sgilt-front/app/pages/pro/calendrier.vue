<!--
  Onglet « Calendrier » de l'espace prestataire (/pro/calendrier) : les réservations confirmées,
  c'est-à-dire ce à quoi le prestataire doit se préparer. Deux vues, portées par l'URL :
  liste à plat des prestations à venir (par défaut) ou calendrier mensuel (?vue=mois), où les
  dates réservées sont pointées et la liste se limite au mois affiché.
-->
<template>
  <div class="calendrier-page">
    <div class="switch">
      <button
        class="option"
        :class="{ active: !isMonthView }"
        type="button"
        @click="setMonthView(false)"
      >
        <Menu2Icon class="icon" />
        {{ $t('pro.calendrier.view-list') }}
      </button>
      <button
        class="option"
        :class="{ active: isMonthView }"
        type="button"
        @click="setMonthView(true)"
      >
        <CalendarIcon class="icon" />
        {{ $t('pro.calendrier.view-month') }}
      </button>
    </div>

    <div v-if="isMonthView" class="calendar">
      <SgiltDatePicker
        inline
        fullwidth
        :booked-dates="bookedDates"
        @update-month-year="visibleMonth = $event"
      />
    </div>

    <div class="list">
      <template v-if="loading">
        <ProBookingCard v-for="i in 3" :key="i" skeleton />
      </template>
      <p v-else-if="error" class="empty">{{ $t('pro.calendrier.error') }}</p>
      <p v-else-if="displayed.length === 0" class="empty">
        {{ isMonthView ? $t('pro.calendrier.month-empty') : $t('pro.calendrier.list-empty') }}
      </p>
      <ProBookingCard
        v-for="(reservation, index) in displayed"
        v-else
        :key="reservation.id"
        :reservation="reservation"
        :animation-delay="index * 60"
        @click="navigateTo(`/pro/reservations/${reservation.id}`)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Menu2Icon, CalendarIcon } from '@remixicons/vue/line'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import ProBookingCard from '~/components/pro/ProBookingCard.vue'
import { useProReservations } from '~/data/reservation/useProReservations'
import type { ProReservationSummary } from '~/data/reservation/domain/ProReservationSummary'

definePageMeta({ layout: 'pro' })

const route = useRoute()
const { reservations, loading, error } = useProReservations()

// ── Vue (URL) ──────────────────────────────────────────────────────────────────
const isMonthView = computed<boolean>(() => route.query.vue === 'mois')

function setMonthView(month: boolean): void {
  navigateTo({ query: month ? { vue: 'mois' } : {} }, { replace: true })
}

// ── Données ────────────────────────────────────────────────────────────────────
const confirmed = computed<ProReservationSummary[]>(() =>
  reservations.value
    .filter((r) => r.statut === 'confirmee')
    .sort((a, b) => a.date.getTime() - b.date.getTime()),
)

const bookedDates = computed<Date[]>(() => confirmed.value.map((r) => r.date))

const today = new Date()
const visibleMonth = ref<{ month: number; year: number }>({
  month: today.getMonth(),
  year: today.getFullYear(),
})

// Liste : à venir (aujourd'hui inclus) en vue liste, le mois affiché en vue mensuelle.
const displayed = computed<ProReservationSummary[]>(() => {
  if (isMonthView.value) {
    return confirmed.value.filter(
      (r) =>
        r.date.getFullYear() === visibleMonth.value.year &&
        r.date.getMonth() === visibleMonth.value.month,
    )
  }
  const startOfToday = new Date()
  startOfToday.setHours(0, 0, 0, 0)
  return confirmed.value.filter((r) => r.date >= startOfToday)
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.calendrier-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
  background-color: $brand-background-alt;

  .switch {
    display: flex;
    align-self: center;
    padding: 0.25rem;
    border-radius: 2rem;
    background: $surface-white;
    border: 1px solid $divider-color;

    .option {
      display: inline-flex;
      align-items: center;
      gap: 0.375rem;
      padding: 0.375rem 1rem;
      border: none;
      border-radius: 2rem;
      background: none;
      color: $text-secondary;
      font-size: 0.875rem;
      font-weight: 500;
      cursor: pointer;
      transition:
        background 150ms ease,
        color 150ms ease;

      &.active {
        background: $brand-accent;
        color: $brand-primary;
      }

      .icon {
        width: 1rem;
        height: 1rem;
      }
    }
  }

  .calendar {
    background: $surface-white;
    border-radius: $radius-md;
    overflow: hidden;
  }

  .list {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    .empty {
      margin: 0;
      padding: $spacing-m;
      text-align: center;
      font-size: 0.875rem;
      color: $text-secondary;
    }
  }
}
</style>
