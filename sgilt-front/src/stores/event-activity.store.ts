import type { EventActivityType } from '@/types/EventActivityType'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useEventActivityStore = defineStore('eventActivity', () => {
  const getColor = (id: string) =>
    eventActivities.value.find((activity) => activity.id === id)?.color || ''

  const eventActivities = ref<EventActivityType[]>([
    {
      id: 'reservation-sent',
      color: '#F4A261', // 🟠 Orange
    },
    {
      id: 'reservation-viewed',
      color: '#E9C46A', // 🟡 Yellow
    },
    {
      id: 'message-sent',
      color: '#219EBC', // 🔵 Light blue
    },
    {
      id: 'message-received',
      color: '#023047', // 🔵 dark blue
    },
    {
      id: 'reservation-approved',
      color: '#2A9D8F', // 🟢 green
    },
    {
      id: 'payment-request',
      color: '#E76F51', // 🟠 Dark Orange
    },
    {
      id: 'payment-success',
      color: '#2ECC71', // ✅ light green
    },
    {
      id: 'payment-failed',
      color: '#D62828', // ❌ Red
    },
    {
      id: 'reservation-cancelled-user',
      color: '#8D99AE', // ⚫ gray
    },
    {
      id: 'reservation-cancelled-partner',
      color: '#D90429', // ❌ bright red
    },
    {
      id: 'reservation-updated',
      color: '#264653', // 🔵 dark blue
    },
    {
      id: 'reservation-modified-user',
      color: '#4A90E2', // 🔵 blue
    },
    {
      id: 'partner-unavailable',
      color: '#6A0572', // 🟣 dark Violet
    },
  ])

  return { eventActivities, getColor }
})
