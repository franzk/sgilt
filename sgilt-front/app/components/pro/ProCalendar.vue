<template>
  <div class="pro-calendar">
    <div class="calendar">
      <SgiltDatePicker
        :key="calendarKey"
        v-model="clickedDate"
        inline
        :booked-dates="bookedDates"
        :indisponible-dates="indisponibleDates"
        @update-month-year="onMonthYearChange"
      />
    </div>

    <!-- Liste du mois visible -->
    <div class="list">
      <p v-if="monthEntries.length === 0" class="list-empty">Aucun événement ce mois-ci.</p>
      <TransitionGroup v-else name="entry" tag="div" class="list-items">
        <div
          v-for="entry in monthEntries"
          :key="entry.date"
          class="list-item-wrap"
          :class="{ 'is-highlighted': highlightKey === entry.date }"
          :ref="(el) => setItemRef(el as HTMLElement | null, entry.date)"
        >
          <NuxtLink
            v-if="entry.type === 'sgilt'"
            :to="`/pro/reservations/${entry.reservationId}`"
            class="list-item list-item--sgilt"
          >
            <span class="list-item-date">{{ formatDayShort(entry.date) }}</span>
            <span class="list-item-label">{{ entry.label }}</span>
            <span class="list-item-link">Voir →</span>
          </NuxtLink>
          <div v-else class="list-item list-item--manual">
            <span class="list-item-date">{{ formatDayShort(entry.date) }}</span>
            <span class="list-item-label">Indisponible</span>
          </div>
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import type { CalendarEntry } from '~/types/calendrier'
import { CalendarMockService } from '~/services/calendrier.mock'

const { isMobile } = useDevice()

// ── État ──────────────────────────────────────────────────────────────────────

const entries = ref<CalendarEntry[]>(CalendarMockService.getAll())
const clickedDate = ref<Date | undefined>(undefined)
const highlightKey = ref<string | null>(null)

const _today = new Date()
const visibleMonth = ref({ month: _today.getMonth(), year: _today.getFullYear() })

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

// ── Refs DOM pour scroll ───────────────────────────────────────────────────────

const itemRefs = new Map<string, HTMLElement>()

function setItemRef(el: HTMLElement | null, key: string): void {
  if (el) itemRefs.set(key, el)
  else itemRefs.delete(key)
}

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

function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

async function scrollEntryIntoView(key: string): Promise<void> {
  await nextTick()
  const el = itemRefs.get(key)
  if (!el) return
  el.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
  await delay(400)
}

// ── Gestion des clics ─────────────────────────────────────────────────────────

watch(clickedDate, async (date) => {
  if (!date) return

  const key = toDateKey(date)
  const entry = entries.value.find((e) => e.date === key)

  if (entry?.type === 'manual') {
    // Suppression : scroll → highlight → suppression animée
    if (isMobile.value) await scrollEntryIntoView(key)
    highlightKey.value = key
    await delay(400)
    highlightKey.value = null
    const savedScrollY = window.scrollY
    await CalendarMockService.removeUnavailability(key)
    entries.value = CalendarMockService.getAll()
    await nextTick()
    window.scrollTo({ top: savedScrollY, behavior: 'instant' })
  } else if (entry?.type !== 'sgilt') {
    // Ajout : ajout → scroll → highlight
    await CalendarMockService.addUnavailability(key)
    entries.value = CalendarMockService.getAll()
    await nextTick()
    if (isMobile.value) await scrollEntryIntoView(key)
    highlightKey.value = key
    await delay(1000)
    highlightKey.value = null
  }

  await nextTick()
  clickedDate.value = undefined
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.pro-calendar {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  @media (max-width: $desktop) {
    flex-direction: column;
    //gap: 0;
  }

  .list {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    border: 1px solid $divider-color;
    border-radius: $radius-lg;
    overflow: hidden;
    background: #fff;

    @media (max-width: $desktop) {
      border-radius: 0;
      border-left: none;
      border-right: none;
    }
  }
}

.calendar {
  width: 50%;
  flex-shrink: 0;

  :deep(.sgilt-date-picker),
  :deep(.dp__outer_menu_wrap) {
    width: 100%;
  }

  @media (max-width: $desktop) {
    width: 100%;
    position: sticky;
    top: $app-header-height;
    z-index: $z-header;
    background: #fff;
  }
}

// ── Liste ──────────────────────────────────────────────────────────────────────

.list-empty {
  font-family: 'Inter', sans-serif;
  font-size: 0.82rem;
  color: $text-secondary;
  font-style: italic;
  text-align: center;
  padding: $spacing-m;
  margin: 0;
}

.list-items {
  position: relative;
  display: flex;
  flex-direction: column;
}

.list-item-wrap {
  border-bottom: 1px solid $divider-color;

  &:last-child {
    border-bottom: none;
  }

  &.is-highlighted {
    animation: entry-highlight 1s ease forwards;
  }
}

.list-item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-xs $spacing-m;
  text-decoration: none;

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
    white-space: nowrap;
    flex-shrink: 0;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.7;
    }
  }
}

// ── Animations TransitionGroup ────────────────────────────────────────────────

.entry-enter-active {
  transition:
    opacity 250ms ease,
    transform 250ms ease;
}

.entry-leave-active {
  transition:
    opacity 200ms ease,
    transform 200ms ease;
  position: absolute;
  left: 0;
  right: 0;
}

.entry-move {
  transition: transform 250ms ease;
}

.entry-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}

.entry-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

// ── Highlight ─────────────────────────────────────────────────────────────────

@keyframes entry-highlight {
  0%   { background-color: transparent; }
  25%  { background-color: rgba($brand-accent, 0.18); }
  100% { background-color: transparent; }
}
</style>
