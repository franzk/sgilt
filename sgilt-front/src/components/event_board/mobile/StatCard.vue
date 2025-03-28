<template>
  <div :style="boxStyle" class="stat-card">
    <span v-if="icon" class="icon"><SgiltIcon :icon="icon" mobile /></span>
    <span v-if="label" class="label">{{ label }}</span>
    <span v-if="value !== undefined" class="value">{{ value }}</span>
    <slot />
  </div>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import { useReservationStatusStore } from '@/stores/reservation-status.store'
import type { ReservationStatusKey } from '@/types/ReservationStatus'
import { computed } from 'vue'

const props = defineProps<{
  statusKeyStyle?: ReservationStatusKey
  icon?: string
  label?: string
  value?: number
}>()

const reservationStatusStore = useReservationStatusStore()
const boxStyle = computed(() =>
  props.statusKeyStyle
    ? reservationStatusStore.getBoxStyle(props.statusKeyStyle, new Date(new Date().getDate() + 15))
    : {},
)
</script>

<style scoped lang="scss">
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.8rem;

  color: #fff;
  font-weight: 600;
  border: 1px solid;
}

.value {
  border: 2px solid;
  border-radius: 100rem;
  padding: 0.5rem 1rem;
  border-color: inherit;
}

.icon svg {
  width: 1.5rem;
  height: 1.5rem;
}
</style>
