<template>
  <span class="status-badge" :style="{ background: config.pillBg, color: config.pillText }">
    {{ $t(`admin.reservations.status.${status}`) }}
  </span>
</template>

<script setup lang="ts">
import type { AdminReservationStatus } from '~/data/admin/domain/AdminReservationStatus'
import { ADMIN_RESERVATION_STATUS_CONFIG } from '~/constants/admin-reservation-status'

const props = defineProps<{ status: AdminReservationStatus }>()

const config = computed(() => ADMIN_RESERVATION_STATUS_CONFIG[props.status])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

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
