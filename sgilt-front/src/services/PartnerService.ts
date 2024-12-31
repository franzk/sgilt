import type { Partner } from '@/domain/Partner'
import type { PartnerQuery } from '@/types/PartnerQuery'

/**
 * Partner service : communicate with the mocked API to fetch partners
 */
export class PartnerService {
  /**
   *
   * @returns 3 random partners
   */
  static getHihglightedPartners = (partnerCount: number): Partner[] => {
    // moke API to return 3 random partners
    const shuffled = [...this.partners].sort(() => Math.random() - 0.5)
    return shuffled.slice(0, partnerCount)
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
          !query.tagsId ||
          query.tagsId.length === 0 ||
          partner.tags.some((tag) => query.tagsId?.includes(tag.id)),
      )
    return new Promise((resolve) => {
      resolve(results)
    })
  }

  /**
   * Mocked data
   */
  private static partners: Partner[] = [
    // Category: Music
    {
      id: '1',
      title: 'Gypsy Reed Ensemble',
      description: 'Un groupe de jazz parfait pour animer vos soirées.',
      imageUrl: './images/jazz-band.jpg',
      tags: [{ id: '1', name: 'Jazz', category: 'music' }],
      entryPrice: 1200,
    },
    {
      id: '2',
      title: 'Starlight Pulse',
      description: 'Un groupe pop-rock énergique pour une ambiance inoubliable.',
      imageUrl: './images/pop-rock-band.jpg',
      tags: [{ id: '2', name: 'Pop-Rock', category: 'music' }],
      entryPrice: 800,
    },
    {
      id: '3',
      title: 'DJ Animation',
      description: 'Un DJ professionnel pour rythmer vos soirées.',
      imageUrl: './images/dj-animation.jpg',
      tags: [{ id: '3', name: 'D.J.', category: 'music' }],
      entryPrice: 600,
    },
    {
      id: '4',
      title: 'Swing Spark',
      description: 'Un quatuor de jazz pour une ambiance feutrée.',
      imageUrl: './images/jazz-quartet.jpg',
      tags: [{ id: '1', name: 'Jazz', category: 'music' }],
      entryPrice: 1000,
    },
    {
      id: '5',
      title: 'The Jive Rebels ',
      description: 'Un groupe rock pour faire vibrer votre public.',
      imageUrl: './images/rock-band.jpg',
      tags: [{ id: '2', name: 'Pop-Rock', category: 'music' }],
      entryPrice: 1500,
    },

    // Category: Food
    {
      id: '6',
      title: 'Éclat Gourmet',
      description: 'Un service traiteur haut de gamme pour vos événements.',
      imageUrl: './images/traiteur-gourmet.jpg',
      tags: [{ id: '4', name: 'Traiteur', category: 'food' }],
      entryPrice: 400,
    },
    {
      id: '7',
      title: 'Food Truck Burgers',
      description: 'Des burgers gourmands pour régaler vos invités.',
      imageUrl: './images/food-truck-burgers.jpg',
      tags: [{ id: '5', name: 'Food Truck', category: 'food' }],
      entryPrice: 300,
    },
    {
      id: '8',
      title: 'Bar à Cocktails',
      description: 'Un bar à cocktails pour une expérience unique.',
      imageUrl: './images/bar-cocktails.jpg',
      tags: [{ id: '6', name: 'Boissons', category: 'food' }],
      entryPrice: 150,
    },
    {
      id: '9',
      title: 'Buffet de Cuisine Française',
      description: 'Un buffet raffiné pour sublimer vos réceptions.',
      imageUrl: './images/cuisine-francaise.jpg',
      tags: [{ id: '4', name: 'Traiteur', category: 'food' }],
      entryPrice: 400,
    },
    {
      id: '10',
      title: 'Camion Pizza',
      description: 'Une délicieuse pizza cuite au feu de bois sur place.',
      imageUrl: './images/pizza-truck.jpg',
      tags: [{ id: '5', name: 'Food Truck', category: 'food' }],
      entryPrice: 200,
    },

    // Category: Place
    {
      id: '11',
      title: 'Salle des Fêtes de Rathsamhausen',
      description: 'Une salle spacieuse pour vos événements.',
      imageUrl: './images/salle-fetes.png',
      tags: [{ id: '7', name: 'Salle', category: 'place' }],
      entryPrice: 1000,
    },
    {
      id: '12',
      title: 'Château pour Mariage',
      description: 'Un cadre enchanteur pour célébrer votre union.',
      imageUrl: './images/chateau-mariage.jpg',
      tags: [{ id: '7', name: 'Salle', category: 'place' }],
      entryPrice: 5000,
    },
    {
      id: '13',
      title: 'Gîte de la mangouste',
      description: 'Un hébergement confortable pour vos invités.',
      imageUrl: './images/hebergement-groupe.jpg',
      tags: [{ id: '8', name: 'Hébergement', category: 'place' }],
      entryPrice: 300,
    },
    {
      id: '14',
      title: 'Restaurant À la Couronne',
      description: 'Un restaurant privatisé pour vos réceptions.',
      imageUrl: './images/restaurant-prive.jpg',
      tags: [{ id: '9', name: 'Restaurant', category: 'place' }],
      entryPrice: 2000,
    },
    {
      id: '15',
      title: 'Salle de Séminaire',
      description: 'Une salle équipée pour vos séminaires et conférences.',
      imageUrl: './images/salle-seminaire.jpg',
      tags: [{ id: '7', name: 'Salle', category: 'place' }],
      entryPrice: 1200,
    },

    // Category: Photo
    {
      id: '16',
      title: 'Léo Clairmont Photographe',
      description: 'Des souvenirs inoubliables pour votre grand jour.',
      imageUrl: './images/photographe-mariage.jpg',
      tags: [{ id: '10', name: 'Photographe', category: 'photo' }],
      entryPrice: 350,
    },
    {
      id: '17',
      title: 'Photobooth Vintage',
      description: 'Un photobooth original pour des photos funs.',
      imageUrl: './images/photobooth-vintage.jpg',
      tags: [{ id: '11', name: 'Photobooth', category: 'photo' }],
      entryPrice: 400,
    },
    {
      id: '18',
      title: 'Studio Photo Mobile',
      description: 'Un studio photo pour des portraits professionnels.',
      imageUrl: './images/studio-photo-mobile.jpg',
      tags: [{ id: '10', name: 'Photographe', category: 'photo' }],
      entryPrice: 800,
    },
  ]
}
