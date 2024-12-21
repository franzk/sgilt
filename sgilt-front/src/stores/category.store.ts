import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category } from '@/domain/Category'

export const useCategorysStore = defineStore('alerts', () => {
  // reactive state
  const categories = ref<Category[]>([
    { name: 'music', tags: [] },
    { name: 'food', tags: [] },
    { name: 'place', tags: [] },
    { name: 'photo', tags: [] },
  ])

  // actions
  const fetchTags = () => {
    console.log('fetchTags')
    if (categories.value.some((category) => category.tags.length > 0)) return
    console.log('fetchTags 2')
    categories.value
      .find((category) => category.name === 'music')
      ?.tags.push(
        ...[
          { id: '1', name: 'jazz' },
          { id: '2', name: 'pop-rock' },
          { id: '3', name: 'dj' },
        ],
      )
    categories.value
      .find((category) => category.name === 'food')
      ?.tags.push(
        ...[
          { id: '4', name: 'traiteur' },
          { id: '5', name: 'foodtruck' },
          { id: '6', name: 'boissons' },
        ],
      )
    categories.value
      .find((category) => category.name === 'place')
      ?.tags.push(
        ...[
          { id: '7', name: 'salle' },
          { id: '8', name: 'hébergement' },
          { id: '9', name: 'restaurant' },
        ],
      )
    categories.value
      .find((category) => category.name === 'photo')
      ?.tags.push(
        ...[
          { id: '10', name: 'photographe' },
          { id: '11', name: 'photobooth' },
        ],
      )
    console.log('fetchTags 3', categories.value)
  }
  return { categories, fetchTags }
})
