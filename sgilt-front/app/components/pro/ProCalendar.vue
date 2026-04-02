<template>
  <div class="pro-calendar">
    <SgiltDatePicker
      :key="calendarKey"
      v-model="clickedDate"
      inline
      :booked-dates="bookedDates"
      :indisponible-dates="indisponibleDates"
      class="pro-calendar__picker"
      @update-month-year="onMonthYearChange"
    />

    <!-- Légende -->
    <div class="pro-calendar__legend">
      <span class="legend-item">
        <span class="dot dot--sgilt" />
        Réservé via Sgilt
      </span>
      <span class="legend-item">
        <span class="dot dot--unavailable" />
        Indisponible
      </span>
    </div>

    <!-- Liste du mois visible -->
    <div class="pro-calendar__list">
      <p v-if="monthEntries.length === 0" class="list-empty">Aucun événement ce mois-ci.</p>
      <div
        v-for="entry in monthEntries"
        :key="entry.date"
        class="list-item"
        :class="`list-item--${entry.type}`"
      >
        <span class="list-item-date">{{ formatDayShort(entry.date) }}</span>
        <span class="list-item-label">
          {{ entry.type === 'manual' ? 'Indisponible' : entry.label }}
        </span>
        <NuxtLink
          v-if="entry.type === 'sgilt'"
          :to="`/pro/reservations/${entry.reservationId}`"
          class="list-item-link"
        >
          Voir →
        </NuxtLink>
      </div>
    </div>

    <!-- Détail réservation Sgilt -->
    <Transition name="detail">
      <div v-if="selectedEntry" class="pro-calendar__detail">
        <div class="detail-body">
          <p class="detail-label">{{ selectedEntry.label }}</p>
          <p class="detail-date">{{ formatDate(selectedEntry.date) }}</p>
        </div>
        <div class="detail-actions">
          <NuxtLink
            :to="`/pro/reservations/${selectedEntry.reservationId}`"
            class="detail-link"
          >
            Voir la demande →
          </NuxtLink>
          <button class="detail-close" type="button" aria-label="Fermer" @click="selectedEntry = null">
            ✕
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import type { CalendarEntry } from '~/types/calendrier'
import { CalendarMockService } from '~/services/calendrier.mock'

// ── État ──────────────────────────────────────────────────────────────────────

const entries = ref<CalendarEntry[]>(CalendarMockService.getAll())
const selectedEntry = ref<CalendarEntry | null>(null)
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

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
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
  selectedEntry.value = null
}

// ── Gestion des clics ─────────────────────────────────────────────────────────

watch(clickedDate, async (date) => {
  if (!date) return

  const key = toDateKey(date)
  const entry = entries.value.find((e) => e.date === key)

  if (entry?.type === 'sgilt') {
    selectedEntry.value = entry
  } else if (entry?.type === 'manual') {
    selectedEntry.value = null
    await CalendarMockService.removeUnavailability(key)
    entries.value = CalendarMockService.getAll()
  } else {
    selectedEntry.value = null
    await CalendarMockService.addUnavailability(key)
    entries.value = CalendarMockService.getAll()
  }

  await nextTick()
  clickedDate.value = undefined
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-calendar {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  // ── Légende ────────────────────────────────────────────────────────────────

  &__legend {
    display: flex;
    gap: $spacing-m;
    justify-content: center;
  }
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  color: $text-secondary;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  flex-shrink: 0;

  &--sgilt {
    background: rgba($brand-accent, 0.55);
  }

  &--unavailable {
    background: repeating-linear-gradient(
      -45deg,
      rgba(0, 0, 0, 0.15),
      rgba(0, 0, 0, 0.15) 2px,
      #e5e5e5 2px,
      #e5e5e5 5px
    );
  }
}

// ── Liste du mois ─────────────────────────────────────────────────────────────

.pro-calendar__list {
  display: flex;
  flex-direction: column;
  border: 1px solid $divider-color;
  border-radius: $radius-lg;
  overflow: hidden;
  background: #fff;
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

// ── Détail réservation ─────────────────────────────────────────────────────────

.pro-calendar__detail {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
  background: rgba($brand-accent, 0.08);
  border: 1px solid rgba($brand-accent, 0.3);
  border-radius: $radius-md;

  .detail-body {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
  }

  .detail-label {
    font-family: 'Inter', sans-serif;
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .detail-date {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    color: $text-secondary;
    margin: 0;
    text-transform: capitalize;
  }

  .detail-actions {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    flex-shrink: 0;
  }

  .detail-link {
    font-family: 'Inter', sans-serif;
    font-size: 0.78rem;
    font-weight: 500;
    color: $brand-primary;
    text-decoration: none;
    white-space: nowrap;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.7;
    }
  }

  .detail-close {
    background: none;
    border: none;
    font-size: 0.75rem;
    color: $text-secondary;
    cursor: pointer;
    padding: 2px 4px;
    line-height: 1;
    border-radius: $radius-sm;
    transition: color 150ms ease;

    &:hover {
      color: $text-primary;
    }
  }
}

// ── Transition détail ─────────────────────────────────────────────────────────

.detail-enter-active,
.detail-leave-active {
  transition:
    opacity 150ms ease,
    transform 150ms ease;
}

.detail-enter-from,
.detail-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
