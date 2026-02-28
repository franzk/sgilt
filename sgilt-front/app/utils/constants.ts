// app/utils/constants.ts
import type { Category } from '~/types/category'

export const APP_CATEGORIES: Category[] = [
  { id: '1', name: 'Tous', picto: '', subcategories: [] },
  {
    id: '2',
    name: 'Musique',
    picto: '',
    subcategories: [
      { id: '21', name: 'DJ', categoryId: '2', picto: '' },
      { id: '22', name: 'Pop/Rock', categoryId: '2', picto: '' },
      { id: '23', name: 'Jazz', categoryId: '2', picto: '' },
    ],
  },
  {
    id: '3',
    name: 'Restauration',
    picto: '',
    subcategories: [
      { id: '31', name: 'Traiteur', categoryId: '3', picto: '' },
      { id: '32', name: 'Food Truck', categoryId: '3', picto: '' },
    ],
  },
  {
    id: '4',
    name: 'Photo',
    picto: '',
    subcategories: [
      { id: '41', name: 'Photographe', categoryId: '4', picto: '' },
      { id: '42', name: 'Vidéo', categoryId: '4', picto: '' },
      { id: '43', name: 'Photobooth', categoryId: '4', picto: '' },
    ],
  },
  {
    id: '5',
    name: 'Services',
    picto: '',
    subcategories: [
      { id: '51', name: 'Décoration', categoryId: '5', picto: '' },
      { id: '52', name: 'Location de matériel', categoryId: '5', picto: '' },
    ],
  },
]
