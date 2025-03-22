<template>
  <div
    :class="[
      'stat-card',
      { confirmed: id === 'confirmed', pending: id === 'pending', toBePaid: id === 'toBePaid' },
    ]"
  >
    <span v-if="icon" class="icon"><SgiltIcon :icon="icon" mobile /></span>
    <span v-if="label" class="label">{{ label }}</span>
    <span v-if="value !== undefined" class="value">{{ value }}</span>
    <slot />
  </div>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  id?: string
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
  padding: 0.8rem;

  color: #fff;
  font-weight: 600;
  border: 1px solid;
}

.confirmed {
  border-color: rgb(115, 255, 119);
  background: rgba(76, 175, 80, 0.2);
  .value {
    background: rgba(76, 175, 80, 1);
  }
}

.pending {
  border-color: rgb(255, 230, 157);
  background: rgba(255, 193, 7, 0.2);
  .value {
    background: rgba(255, 193, 7, 1);
  }
}

.toBePaid {
  border-color: rgb(254, 170, 164);
  background: rgba(244, 67, 54, 0.2);
  .value {
    background: rgba(244, 67, 54, 1);
  }
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
