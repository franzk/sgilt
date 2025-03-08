<template>
  <nav class="bottom-nav">
    <button @click="switchView('event')" :class="{ active: activeView === 'event' }">
      <SgiltIcon icon="Event" />
    </button>
    <button @click="switchView('reservations')" :class="{ active: activeView === 'reservations' }">
      <SgiltIcon icon="List" />
    </button>
    <button @click="switchView('activities')" :class="{ active: activeView === 'activities' }">
      <SgiltIcon icon="Bell" />
    </button>
    <button @click="switchView('help')" :class="{ active: activeView === 'help' }">
      <SgiltIcon icon="Help" />
    </button>
  </nav>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{ activeView: string }>()
const emit = defineEmits(['updateView'])

const switchView = (view: string) => {
  emit('updateView', view)
}
</script>

<style scoped lang="scss">
.bottom-nav {
  display: none;
  @include respond-to(mobile) {
    display: flex;
  }

  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;

  padding: 0 $spacing-s 0;

  justify-content: space-around;
  align-items: center;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);

  box-shadow: 0 -4px 10px rgba(0, 0, 0, 0.1);
  border-top: 2px solid rgba(255, 255, 255, 0.2);

  button {
    flex: 1;
    border: none;
    background: transparent;
    font-size: 1.8rem;
    color: #666;
    cursor: pointer;
    padding: 0.6rem;
    position: relative;

    svg {
      height: 2rem;
      aspect-ratio: 1;
    }

    transition:
      transform 0.2s ease-in-out,
      color 0.2s ease-in-out;

    &.active {
      color: #ff9f00;
      transform: scale(1.2);
    }

    &:hover {
      color: #ff9f00;
    }
  }
}
</style>
