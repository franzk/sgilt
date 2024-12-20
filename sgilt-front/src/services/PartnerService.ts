import type { Partner } from '@/domain/Partner'

export class PartnerService {
  static getHihglightedPartners = (): Partner[] => {
    return [
      {
        id: '1',
        title: 'Partner 1',
        description: 'Description 1',
        imageUrl: 'https://picsum.photos/300/250',
        category: 'music',
        entryPrice: 100,
      },
      {
        id: '2',
        title: 'Partner 2',
        description: 'Description 2',
        imageUrl: 'https://picsum.photos/300/250',
        category: 'food',
        entryPrice: 200,
      },
      {
        id: '3',
        title: 'Partner 3',
        description: 'Description 3',
        imageUrl: 'https://picsum.photos/300/250',
        category: 'place',
        entryPrice: 300,
      },
    ]
  }
}
