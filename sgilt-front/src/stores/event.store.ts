import type { EventType } from '@/types/EventType'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useEventStore = defineStore('event', () => {
  // reactive state
  // TODO : utiliser i18n et ne metter votre évenement dans le composant et pas ici
  const events = ref<EventType[]>([
    { id: '1', name: 'Votre événement' },
    { id: '2', name: 'Mariage' },
    { id: '3', name: "Fête d'entreprise" },
    { id: '4', name: "Fête d'anniversaire" },
  ])

  return { events }
})
