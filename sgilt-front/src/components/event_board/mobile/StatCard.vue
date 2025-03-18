<template>
  <div
    :class="[
      'stat-card',
      { success: state === 'success', pending: state === 'pending', danger: state === 'danger' },
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
  icon?: string
  label?: string
  value?: number
  state?: 'success' | 'pending' | 'danger'
}>()
</script>

<style scoped lang="scss">
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.8rem;

  // border-radius: 14px;
  // background: rgba(255, 255, 255, 0.2);
  // backdrop-filter: blur(10px);
  /*box-shadow:
    inset 0px 1px 2px rgba(255, 255, 255, 0.1),
    0px 4px 8px rgba(0, 0, 0, 0.1);*/
  color: #fff;
  font-weight: 600;
  border: 1px solid;
}

.success {
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

.danger {
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
