<template>
  <div class="pills-scroll">
    <button
      v-for="pill in RESERVATION_STATUS_PILLS"
      :key="pill.id"
      class="pill"
      :class="{ active: isPillActive(pill.id) }"
      :style="getPillStyle(pill.id)"
      type="button"
      @click="togglePill(pill.id)"
    >
      {{ t(`reservation.statut.${pill.id}`) }}
    </button>
  </div>
</template>

<script setup lang="ts">
import {
  RESERVATION_STATUS_PILLS,
  RESERVATION_STATUS_CONFIG,
} from '~/constants/reservation-status'
import type { ReservationStatut } from '~/constants/reservation-status'

const { t } = useI18n()

const modelValue = defineModel<ReservationStatut | null>({ default: null })

function isPillActive(id: string): boolean {
  if (id === 'toutes') return modelValue.value === null
  return modelValue.value === (id as ReservationStatut)
}

function togglePill(id: string) {
  modelValue.value = id === 'toutes' ? null : (id as ReservationStatut)
}

function getPillStyle(id: string): Record<string, string> {
  if (id === 'toutes') return {}
  const cfg = RESERVATION_STATUS_CONFIG[id as ReservationStatut]
  if (!cfg) return {}
  return { '--pill-color': cfg.color, '--pill-bg': cfg.bgColor }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pills-scroll {
  display: flex;
  gap: $spacing-xs;
  overflow-x: auto;
  scrollbar-width: none;
  padding-bottom: 2px;

  &::-webkit-scrollbar {
    display: none;
  }
}

.pill {
  position: relative;
  flex-shrink: 0;
  padding: 5px 12px;
  margin-bottom: 2px;
  border-radius: 2rem;
  border: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  color: var(--pill-color, $text-secondary);
  background: var(--pill-bg, transparent);
  transition:
    background 120ms ease,
    color 120ms ease;

  &.active {
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: -5px;
      left: 0;
      right: 0;
      height: 2px;
      border-radius: 999px;
      background: $brand-accent;
    }
  }
}
</style>
