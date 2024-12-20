export interface Partner {
  id: string
  title: string
  description: string
  imageUrl: string
  category: Category
  entryPrice: number
}

export type Category = 'music' | 'food' | 'place' | 'photo'
