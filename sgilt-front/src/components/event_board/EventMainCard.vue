<template>
  <div class="event-card">
    <div class="event-header">
      <h2>{{ eventTitle }}</h2>
      <span class="status">{{ status }}</span>
    </div>
    <div class="event-details">
      <p class="location">📍 {{ location }}</p>
      <p class="date">📅 {{ formattedDate }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { defineProps, computed } from 'vue'

const props = defineProps<{
  eventTitle?: string
  location?: string
  date?: Date
}>()

const formattedDate = computed(() =>
  props.date ? dayjs(new Date(props.date)).locale('fr').format('dddd DD MMM YYYY') : '',
)
</script>

<style scoped lang="scss">
.event-card {
  background: var(--color-secondary);
  // border-radius: 12px;
  box-shadow: 0 4px 12px var(--shadow-m);
  padding: 20px;
  width: 320px;
  transform: rotate(-2deg);
  transition: transform 0.2s ease-in-out;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.event-card:hover {
  transform: rotate(0deg) scale(1.02);
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.status {
  background: var(--color-accent);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9em;
  color: #333;
}

.event-details {
  margin-top: 10px;
  font-size: 1em;
}

p {
  margin: 5px 0;
}
</style>
