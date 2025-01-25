import type { EventType } from '@/types/EventType'
import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * Event store with mock data
 */
export const useEventTypeStore = defineStore('eventType', () => {
  // reactive state
  // TODO : utiliser i18n et ne metter votre évenement dans le composant et pas ici
  const events = ref<EventType[]>([
    { id: '1', name: 'Mariage' },
    { id: '2', name: "Fête d'entreprise" },
    { id: '3', name: "Fête d'anniversaire" },
    { id: '4', name: 'Autre' },
  ])

  return { events }
})
