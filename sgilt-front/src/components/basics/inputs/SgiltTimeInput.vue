<template>
  <div class="sgilt-time-input">
    <IconClock class="icon" />
    <input type="time" v-model="time" class="time-input" />
  </div>
</template>

<script setup lang="ts">
import IconClock from '@/components/icons/IconClock.vue'

const time = defineModel()
</script>

<style scoped lang="scss">
.sgilt-time-input {
  position: relative;
  display: flex;
  height: 3rem;
}

.icon {
  position: absolute;
  width: 1.1rem;
  height: 1.1rem;
  left: 0.6rem;
  top: 0.9rem;
}

.time-input {
  flex: 1;
  padding: 0 2.5rem;
  background: $color-white;

  &::-webkit-calendar-picker-indicator {
    display: none;
  }
}
</style>
