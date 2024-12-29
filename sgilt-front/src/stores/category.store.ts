import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category } from '@/domain/Category'

export const useCategorysStore = defineStore('alerts', () => {
  // reactive state
  const categories = ref<Category[]>([
    {
      name: 'music',
      imageUrl: './images/music.jpg',
      tags: [
        { id: '1', name: 'Jazz' },
        { id: '2', name: 'Pop-Rock' },
        { id: '3', name: 'D.J.' },
      ],
    },
    {
      name: 'food',
      imageUrl: './images/food.jpg',
      tags: [
        { id: '4', name: 'Traiteur' },
        { id: '5', name: 'Food Truck' },
        { id: '6', name: 'Boissons' },
      ],
    },
    {
      name: 'place',
      imageUrl: './images/place.jpg',
      tags: [
        { id: '7', name: 'Salle' },
        { id: '8', name: 'Hébergement' },
        { id: '9', name: 'Restaurant' },
      ],
    },
    {
      name: 'photo',
      imageUrl: './images/photo.jpg',
      tags: [
        { id: '10', name: 'Photographe' },
        { id: '11', name: 'Photobooth' },
      ],
    },
  ])

  return { categories }
})
