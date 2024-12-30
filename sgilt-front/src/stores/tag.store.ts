import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Tag } from '@/domain/Tag'

/**
 * Tags store with mock data
 */
export const useTagsStore = defineStore('tags', () => {
  // reactive state
  const tags = ref<Tag[]>([
    { id: '1', name: 'Jazz', category: 'music' },
    { id: '2', name: 'Pop-Rock', category: 'music' },
    { id: '3', name: 'D.J.', category: 'music' },

    { id: '4', name: 'Traiteur', category: 'food' },
    { id: '5', name: 'Food Truck', category: 'food' },
    { id: '6', name: 'Boissons', category: 'food' },

    { id: '7', name: 'Salle', category: 'place' },
    { id: '8', name: 'Hébergement', category: 'place' },
    { id: '9', name: 'Restaurant', category: 'place' },

    { id: '10', name: 'Photographe', category: 'photo' },
    { id: '11', name: 'Photobooth', category: 'photo' },
  ])

  return { tags }
})
