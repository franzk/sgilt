<template>
  <div class="stat-card">
    <span v-if="icon" class="icon"><SgiltIcon :icon="icon" mobile /></span>
    <span v-if="label" class="label">{{ label }}</span>
    <span v-if="value" class="value">{{ value }}</span>
    <slot />
  </div>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  icon?: string
  label?: string
  value?: number
}>()
</script>

<style scoped lang="scss">
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  box-shadow:
    inset 0px 1px 2px rgba(255, 255, 255, 0.1),
    0px 4px 8px rgba(0, 0, 0, 0.1);
  color: #fff;
  font-weight: 600;
}

.success {
  background: rgba(76, 175, 80);
}

.pending {
  background: rgba(255, 193, 7);
}

.danger {
  background: rgba(244, 67, 54);
}

.icon svg {
  width: 1.5rem;
  height: 1.5rem;
}
</style>
