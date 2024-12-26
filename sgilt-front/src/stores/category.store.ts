import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category } from '@/domain/Category'

export const useCategorysStore = defineStore('alerts', () => {
  // reactive state
  const categories = ref<Category[]>([
    { name: 'music', description: 'De la zik pas du boom boom', tags: [] },
    { name: 'food', description: 'Du lourd, pas de la malbouffe', tags: [] },
    { name: 'place', description: 'Des toits, des murs, des portes', tags: [] },
    { name: 'photo', description: "Des images contre l'Alzheimer", tags: [] },
  ])

  // actions
  const fetchTags = () => {
    if (categories.value.some((category) => category.tags.length > 0)) return

    categories.value
      .find((category) => category.name === 'music')
      ?.tags.push(
        ...[
          { id: '1', name: 'Jazz' },
          { id: '2', name: 'Pop-Rock' },
          { id: '3', name: 'D.J.' },
        ],
      )
    categories.value
      .find((category) => category.name === 'food')
      ?.tags.push(
        ...[
          { id: '4', name: 'Traiteur' },
          { id: '5', name: 'Food Truck' },
          { id: '6', name: 'Boissons' },
        ],
      )
    categories.value
      .find((category) => category.name === 'place')
      ?.tags.push(
        ...[
          { id: '7', name: 'Salle' },
          { id: '8', name: 'Hébergement' },
          { id: '9', name: 'Restaurant' },
        ],
      )
    categories.value
      .find((category) => category.name === 'photo')
      ?.tags.push(
        ...[
          { id: '10', name: 'Photographe' },
          { id: '11', name: 'Photobooth' },
        ],
      )
  }
  return { categories, fetchTags }
})
