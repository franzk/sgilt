import type { Tag } from '@/data/domain/Tag'

/**
 * Get all tags
 * @returns {Tag[]} tags
 */
export const findAllTags = (): Tag[] => {
  return tags
}

// Mock data
const tags: Tag[] = [
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
]
