<template>
  <span class="status-badge" :style="{ background: config.pillBg, color: config.pillText }">
    {{ $t(`admin.prestataires.status.${status}`) }}
  </span>
</template>

<script setup lang="ts">
import type { PrestataireStatus } from '~/data/prestataire/domain/PrestataireStatus'
import { PRESTATAIRE_STATUS_CONFIG } from '~/constants/prestataire-status'

const props = defineProps<{ status: PrestataireStatus }>()

const config = computed(() => PRESTATAIRE_STATUS_CONFIG[props.status])
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
