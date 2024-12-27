import type { Partner } from '@/domain/Partner'
import type { PartnerQuery } from '@/types/PartnerQuery'

export class PartnerService {
  static getHihglightedPartners = (): Partner[] => {
    // TODO renvoyer 3 partenaires tirés au hasard
    const shuffled = [...this.partners].sort(() => Math.random() - 0.5)
    return shuffled.slice(0, 3)
  }

  static search = async (query: PartnerQuery): Promise<Partner[]> => {
    console.log('Searching partners with query:', query)
    const results = PartnerService.partners
    // return promise
    return new Promise((resolve) => {
      resolve(results)
    })
  }

  private static partners: Partner[] = [
    // Category: Music
    {
      id: '1',
      title: 'Jazz & Swing Band',
      description: 'Un groupe de jazz parfait pour animer vos soirées.',
      imageUrl: './images/jazz-band.png',
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
      imageUrl: './images/pop-rock-band.png',
      category: {
        name: 'music',
        tags: [{ id: '2', name: 'Pop-Rock' }],
      },
      entryPrice: 1500,
    },
    {
      id: '3',
      title: 'DJ Animation',
      description: 'Un DJ professionnel pour rythmer vos soirées.',
      imageUrl: './images/dj-animation.png',
      category: {
        name: 'music',
        tags: [{ id: '3', name: 'D.J.' }],
      },
      entryPrice: 800,
    },
    {
      id: '4',
      title: 'Jazz Quartet',
      description: 'Un quatuor de jazz pour une ambiance feutrée.',
      imageUrl: './images/jazz-quartet.png',
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
      imageUrl: './images/rock-band.png',
      category: {
        name: 'music',
        tags: [{ id: '2', name: 'Pop-Rock' }],
      },
      entryPrice: 2000,
    },

    // Category: Food
    {
      id: '6',
      title: 'Traiteur Gourmet',
      description: 'Un service traiteur haut de gamme pour vos événements.',
      imageUrl: './images/traiteur-gourmet.png',
      category: {
        name: 'food',
        tags: [{ id: '4', name: 'Traiteur' }],
      },
      entryPrice: 30,
    },
    {
      id: '7',
      title: 'Food Truck Burgers',
      description: 'Des burgers gourmands pour régaler vos invités.',
      imageUrl: './images/food-truck-burgers.png',
      category: {
        name: 'food',
        tags: [{ id: '5', name: 'Food Truck' }],
      },
      entryPrice: 20,
    },
    {
      id: '8',
      title: 'Bar à Cocktails',
      description: 'Un bar à cocktails pour une expérience unique.',
      imageUrl: './images/bar-cocktails.png',
      category: {
        name: 'food',
        tags: [{ id: '6', name: 'Boissons' }],
      },
      entryPrice: 15,
    },
    {
      id: '9',
      title: 'Buffet de Cuisine Française',
      description: 'Un buffet raffiné pour sublimer vos réceptions.',
      imageUrl: './images/cuisine-francaise.png',
      category: {
        name: 'food',
        tags: [{ id: '4', name: 'Traiteur' }],
      },
      entryPrice: 50,
    },
    {
      id: '10',
      title: 'Camion Pizza',
      description: 'Une délicieuse pizza cuite au feu de bois sur place.',
      imageUrl: './images/pizza-truck.png',
      category: {
        name: 'food',
        tags: [{ id: '5', name: 'Food Truck' }],
      },
      entryPrice: 10,
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
      imageUrl: './images/chateau-mariage.png',
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
      imageUrl: './images/hebergement-groupe.png',
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
      imageUrl: './images/restaurant-prive.png',
      category: {
        name: 'place',
        tags: [{ id: '9', name: 'Restaurant' }],
      },
      entryPrice: 50,
    },
    {
      id: '15',
      title: 'Salle de Séminaire',
      description: 'Une salle équipée pour vos séminaires et conférences.',
      imageUrl: './images/salle-seminaire.png',
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
      imageUrl: './images/photographe-mariage.png',
      category: {
        name: 'photo',
        tags: [{ id: '10', name: 'Photographe' }],
      },
      entryPrice: 1500,
    },
    {
      id: '17',
      title: 'Photobooth Vintage',
      description: 'Un photobooth original pour des photos funs.',
      imageUrl: './images/photobooth-vintage.png',
      category: {
        name: 'photo',
        tags: [{ id: '11', name: 'Photobooth' }],
      },
      entryPrice: 600,
    },
    {
      id: '18',
      title: 'Studio Photo Mobile',
      description: 'Un studio photo pour des portraits professionnels.',
      imageUrl: './images/studio-photo-mobile.png',
      category: {
        name: 'photo',
        tags: [{ id: '10', name: 'Photographe' }],
      },
      entryPrice: 800,
    },
  ]
}
