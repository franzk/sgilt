// app/utils/constants.ts
import IconMusic from '~/components/icons/IconMusic.vue'
import IconRocket from '~/components/icons/IconRocket.vue'
import IconFood from '~/components/icons/IconFood.vue'
import type { Category } from '~/types/category'
import IconPhoto from '~/components/icons/IconPhoto.vue'
import IconStar from '~/components/icons/IconStar.vue'

export const APP_CATEGORIES: Category[] = [
  { id: '1', name: 'Tous', picto: IconRocket, subcategories: [] },
  {
    id: '2',
    name: 'Musique',
    picto: IconMusic,
    subcategories: [
      { id: '21', name: 'DJ', categoryId: '2', picto: IconMusic },
      { id: '22', name: 'Pop/Rock', categoryId: '2', picto: IconMusic },
      { id: '23', name: 'Jazz', categoryId: '2', picto: IconMusic },
    ],
  },
  {
    id: '3',
    name: 'Restauration',
    picto: IconFood,
    subcategories: [
      { id: '31', name: 'Traiteur', categoryId: '3', picto: IconFood },
      { id: '32', name: 'Food Truck', categoryId: '3', picto: IconFood },
    ],
  },
  {
    id: '4',
    name: 'Photo',
    picto: IconPhoto,
    subcategories: [
      { id: '41', name: 'Photographe', categoryId: '4', picto: IconPhoto },
      { id: '42', name: 'Vidéo', categoryId: '4', picto: IconPhoto },
      { id: '43', name: 'Photobooth', categoryId: '4', picto: IconPhoto },
    ],
  },
  {
    id: '5',
    name: 'Services',
    picto: IconStar,
    subcategories: [
      { id: '51', name: 'Décoration', categoryId: '5', picto: IconStar },
      { id: '52', name: 'Location de matériel', categoryId: '5', picto: IconStar },
    ],
  },
]
