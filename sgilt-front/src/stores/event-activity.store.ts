import type { EventActivityType } from '@/data/domain/EventActivity'
import { defineStore } from 'pinia'

export const useEventActivityStore = defineStore('eventActivity', () => {
  const getColor = (id: EventActivityType) => eventActivitiesColors.get(id) || ''

  const eventActivitiesColors: Map<EventActivityType, string> = new Map([
    ['reservation-sent', '#F4A261'], // 🟠 Orange
    ['reservation-viewed', '#E9C46A'], // 🟡 Yellow
    ['message-sent', '#219EBC'], // 🔵 Light blue
    ['message-received', '#023047'], // 🔵 dark blue
    ['reservation-approved', '#2A9D8F'], // 🟢 green
    ['payment-request', '#E76F51'], // 🟠 Dark Orange
    ['payment-success', '#2ECC71'], // ✅ light green
    ['payment-failed', '#D62828'], // ❌ Red
    ['reservation-cancelled-user', '#8D99AE'], // ⚫ gray
    ['reservation-cancelled-partner', '#C91C1C'], // ❌ slightly brighter red
    ['reservation-updated', '#357ABD'], // 🔵 Slightly brighter blue
    ['reservation-modified-user', '#4A90E2'], // 🔵 blue
    ['partner-unavailable', '#6A0572'], // 🟣 dark Violet
  ])

  return { getColor }
})
