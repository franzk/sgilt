<template>
  <div class="pro-calendar">
    <SgiltDatePicker
      :key="calendarKey"
      v-model="clickedDate"
      inline
      :booked-dates="bookedDates"
      :indisponible-dates="indisponibleDates"
      class="calendar"
      @update-month-year="onMonthYearChange"
    />

    <!-- Liste du mois visible -->
    <div class="list">
      <p v-if="monthEntries.length === 0" class="list-empty">Aucun événement ce mois-ci.</p>
      <div v-for="entry in monthEntries" :key="entry.date">
        <NuxtLink
          v-if="entry.type === 'sgilt'"
          :to="`/pro/reservations/${entry.reservationId}`"
          class="list-item"
          :class="`list-item--${entry.type}`"
        >
          <span class="list-item-date">{{ formatDayShort(entry.date) }}</span>
          <span class="list-item-label">
            {{ entry.type === 'manual' ? 'Indisponible' : entry.label }}
          </span>
          <span class="list-item-link">Voir →</span>
        </NuxtLink>
        <div v-else class="list-item" :class="`list-item--${entry.type}`">
          <span class="list-item-date">{{ formatDayShort(entry.date) }}</span>
          <span class="list-item-label">
            {{ entry.type === 'manual' ? 'Indisponible' : entry.label }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import type { CalendarEntry } from '~/types/calendrier'
import { CalendarMockService } from '~/services/calendrier.mock'

// ── État ──────────────────────────────────────────────────────────────────────

const entries = ref<CalendarEntry[]>(CalendarMockService.getAll())
const clickedDate = ref<Date | undefined>(undefined)

const _today = new Date()
const visibleMonth = ref({ month: _today.getMonth(), year: _today.getFullYear() })

// Clé qui change à chaque modification des entrées pour forcer le re-rendu
// de SgiltDatePicker et recalculer getDayClass.
const calendarKey = computed(() => entries.value.map((e) => `${e.date}:${e.type}`).join('|'))

const bookedDates = computed(() =>
  entries.value.filter((e) => e.type === 'sgilt').map((e) => new Date(e.date)),
)

const indisponibleDates = computed(() =>
  entries.value.filter((e) => e.type === 'manual').map((e) => new Date(e.date)),
)

const monthEntries = computed(() =>
  entries.value
    .filter((e) => {
      const [y, m] = e.date.split('-').map(Number)
      return y === visibleMonth.value.year && m - 1 === visibleMonth.value.month
    })
    .sort((a, b) => a.date.localeCompare(b.date)),
)

// ── Helpers ───────────────────────────────────────────────────────────────────

function toDateKey(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function formatDayShort(iso: string): string {
  return new Date(iso).toLocaleDateString('fr-FR', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  })
}

function onMonthYearChange({ month, year }: { month: number; year: number }) {
  visibleMonth.value = { month, year }
}

// ── Gestion des clics ─────────────────────────────────────────────────────────

watch(clickedDate, async (date) => {
  if (!date) return

  const key = toDateKey(date)
  const entry = entries.value.find((e) => e.date === key)

  if (entry?.type === 'manual') {
    await CalendarMockService.removeUnavailability(key)
    entries.value = CalendarMockService.getAll()
  } else if (entry?.type !== 'sgilt') {
    await CalendarMockService.addUnavailability(key)
    entries.value = CalendarMockService.getAll()
  }

  await nextTick()
  clickedDate.value = undefined
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

:deep(.sgilt-date-picker) {
  width: 50%;
  flex-shrink: 0;

  .dp__outer_menu_wrap {
    width: 100%;
  }
}

.pro-calendar {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  @media (max-width: $breakpoint-desktop) {
    flex-direction: column;

    :deep(.sgilt-date-picker) {
      width: 100%;
    }
  }

  .list {
    width: 50%;
    @media (max-width: $breakpoint-desktop) {
      width: 100%;
    }

    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    border: 1px solid $divider-color;
    border-radius: $radius-lg;
    overflow: hidden;
    background: #fff;
  }
}

.list-empty {
  font-family: 'Inter', sans-serif;
  font-size: 0.82rem;
  color: $text-secondary;
  font-style: italic;
  text-align: center;
  padding: $spacing-m;
  margin: 0;
}

.list-item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-xs $spacing-m;
  border-bottom: 1px solid $divider-color;

  &:last-child {
    border-bottom: none;
  }

  &--sgilt {
    background: rgba($brand-accent, 0.04);
  }

  .list-item-date {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 500;
    color: $text-secondary;
    white-space: nowrap;
    min-width: 6rem;
    text-transform: capitalize;
  }

  .list-item-label {
    font-family: 'Inter', sans-serif;
    font-size: 0.82rem;
    color: $text-primary;
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .list-item-link {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 500;
    color: $brand-primary;
    text-decoration: none;
    white-space: nowrap;
    flex-shrink: 0;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.7;
    }
  }
}
</style>
