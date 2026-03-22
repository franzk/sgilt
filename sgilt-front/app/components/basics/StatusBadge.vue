<template>
  <span class="status-badge" :style="{ background: config.pillBg, color: config.pillText }">
    {{ t(`reservation.statut.${status}`) }}
  </span>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'

const props = defineProps<{ status: ReservationStatus }>()

const { t } = useI18n()

const config = computed(() => RESERVATION_STATUS_CONFIG[props.status])
</script>

<style scoped lang="scss">
.status-badge {
  display: inline-block;
  padding: 2px $spacing-xs;
  border-radius: $radius-sm;
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem;
  font-weight: 600;
  white-space: nowrap;
  line-height: 1.4;
}
</style>
