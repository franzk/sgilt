import type { Partner } from '@/domain/Partner'
import type { PartnerQuery } from '@/types/PartnerQuery'

export class PartnerService {
  /**
   *
   * @returns 3 random partners
   */
  static getHihglightedPartners = (): Partner[] => {
    // moke API to return 3 random partners
    const shuffled = [...this.partners].sort(() => Math.random() - 0.5)
    return shuffled.slice(0, 3)
  }

  /**
   * query partners
   * @param query the query to search partners
   * @returns a list of partners matching the query
   */
  static search = async (query: PartnerQuery): Promise<Partner[]> => {
    // moke API to search partners
    const results = PartnerService.partners
      .filter((partner) => partner.entryPrice >= (query.minPrice || 0))
      .filter((partner) => partner.entryPrice <= (query.maxPrice || Number.MAX_VALUE))
      .filter(
        (partner) =>
          !query.categoryTags ||
          query.categoryTags.length === 0 ||
          query.categoryTags.some((queriedTag) =>
            partner.category.tags.some((partnerTag) => partnerTag.id === queriedTag.id),
          ),
      )
    return new Promise((resolve) => {
      resolve(results)
    })
  }

  /**
   * Mock data
   */
  private static partners: Partner[] = [
    // Category: Music
    {
      id: '1',
      title: 'Jazz & Swing Band',
      description: 'Un groupe de jazz parfait pour animer vos soirées.',
      imageUrl: './images/jazz-band.jpg',
      category: {
        name: 'music',
        tags: [{ id: '1', name: 'Jazz' }],
      },
      entryPrice: 1200,
    },
    {
      id: '2',
      title: 'Pop-Rock Vibes',
      description: 'Un groupe pop-rock énergique pour une ambiance inoubliable.',
      imageUrl: './images/pop-rock-band.jpg',
      category: {
        name: 'music',
        tags: [{ id: '2', name: 'Pop-Rock' }],
      },
      entryPrice: 800,
    },
    {
      id: '3',
      title: 'DJ Animation',
      description: 'Un DJ professionnel pour rythmer vos soirées.',
      imageUrl: './images/dj-animation.jpg',
      category: {
        name: 'music',
        tags: [{ id: '3', name: 'D.J.' }],
      },
      entryPrice: 600,
    },
    {
      id: '4',
      title: 'Jazz Quartet',
      description: 'Un quatuor de jazz pour une ambiance feutrée.',
      imageUrl: './images/jazz-quartet.jpg',
      category: {
        name: 'music',
        tags: [{ id: '1', name: 'Jazz' }],
      },
      entryPrice: 1000,
    },
    {
      id: '5',
      title: 'Rock & Roll Band',
      description: 'Un groupe rock pour faire vibrer votre public.',
      imageUrl: './images/rock-band.jpg',
      category: {
        name: 'music',
        tags: [{ id: '2', name: 'Pop-Rock' }],
      },
      entryPrice: 1500,
    },

    // Category: Food
    {
      id: '6',
      title: 'Traiteur Gourmet',
      description: 'Un service traiteur haut de gamme pour vos événements.',
      imageUrl: './images/traiteur-gourmet.jpg',
      category: {
        name: 'food',
        tags: [{ id: '4', name: 'Traiteur' }],
      },
      entryPrice: 400,
    },
    {
      id: '7',
      title: 'Food Truck Burgers',
      description: 'Des burgers gourmands pour régaler vos invités.',
      imageUrl: './images/food-truck-burgers.jpg',
      category: {
        name: 'food',
        tags: [{ id: '5', name: 'Food Truck' }],
      },
      entryPrice: 300,
    },
    {
      id: '8',
      title: 'Bar à Cocktails',
      description: 'Un bar à cocktails pour une expérience unique.',
      imageUrl: './images/bar-cocktails.jpg',
      category: {
        name: 'food',
        tags: [{ id: '6', name: 'Boissons' }],
      },
      entryPrice: 150,
    },
    {
      id: '9',
      title: 'Buffet de Cuisine Française',
      description: 'Un buffet raffiné pour sublimer vos réceptions.',
      imageUrl: './images/cuisine-francaise.jpg',
      category: {
        name: 'food',
        tags: [{ id: '4', name: 'Traiteur' }],
      },
      entryPrice: 400,
    },
    {
      id: '10',
      title: 'Camion Pizza',
      description: 'Une délicieuse pizza cuite au feu de bois sur place.',
      imageUrl: './images/pizza-truck.jpg',
      category: {
        name: 'food',
        tags: [{ id: '5', name: 'Food Truck' }],
      },
      entryPrice: 200,
    },

    // Category: Place
    {
      id: '11',
      title: 'Salle des Fêtes',
      description: 'Une salle spacieuse pour vos événements.',
      imageUrl: './images/salle-fetes.png',
      category: {
        name: 'place',
        tags: [{ id: '7', name: 'Salle' }],
      },
      entryPrice: 1000,
    },
    {
      id: '12',
      title: 'Château pour Mariage',
      description: 'Un cadre enchanteur pour célébrer votre union.',
      imageUrl: './images/chateau-mariage.jpg',
      category: {
        name: 'place',
        tags: [{ id: '7', name: 'Salle' }],
      },
      entryPrice: 5000,
    },
    {
      id: '13',
      title: 'Hébergement de Groupe',
      description: 'Un hébergement confortable pour vos invités.',
      imageUrl: './images/hebergement-groupe.jpg',
      category: {
        name: 'place',
        tags: [{ id: '8', name: 'Hébergement' }],
      },
      entryPrice: 300,
    },
    {
      id: '14',
      title: 'Restaurant Privé',
      description: 'Un restaurant privatisé pour vos réceptions.',
      imageUrl: './images/restaurant-prive.jpg',
      category: {
        name: 'place',
        tags: [{ id: '9', name: 'Restaurant' }],
      },
      entryPrice: 2000,
    },
    {
      id: '15',
      title: 'Salle de Séminaire',
      description: 'Une salle équipée pour vos séminaires et conférences.',
      imageUrl: './images/salle-seminaire.jpg',
      category: {
        name: 'place',
        tags: [{ id: '7', name: 'Salle' }],
      },
      entryPrice: 1200,
    },

    // Category: Photo
    {
      id: '16',
      title: 'Photographe de Mariage',
      description: 'Des souvenirs inoubliables pour votre grand jour.',
      imageUrl: './images/photographe-mariage.jpg',
      category: {
        name: 'photo',
        tags: [{ id: '10', name: 'Photographe' }],
      },
      entryPrice: 350,
    },
    {
      id: '17',
      title: 'Photobooth Vintage',
      description: 'Un photobooth original pour des photos funs.',
      imageUrl: './images/photobooth-vintage.jpg',
      category: {
        name: 'photo',
        tags: [{ id: '11', name: 'Photobooth' }],
      },
      entryPrice: 400,
    },
    {
      id: '18',
      title: 'Studio Photo Mobile',
      description: 'Un studio photo pour des portraits professionnels.',
      imageUrl: './images/studio-photo-mobile.jpg',
      category: {
        name: 'photo',
        tags: [{ id: '10', name: 'Photographe' }],
      },
      entryPrice: 800,
    },
  ]
}
