import type { CalendarEntry, Partner } from '@/data/domain/Partner'
import type { PartnerSearchViewModel } from '@/data/domain/viewmodels/PartnerSearchViewModel'
import type { PartnerQuery } from '@/data/api/query/PartnerQuery'
import dayjs from 'dayjs'

/**
 * Get all partners
 * @returns {Partner[]} partners
 */
export const findAllPartners = (): Partner[] => {
  return partners
}

// moke API to get random partners
export const getRandomPartners = (count: number): Promise<Partner[]> => {
  const shuffled = [...partners].sort(() => Math.random() - 0.5)
  return new Promise((resolve) => resolve(shuffled.slice(0, count)))
}

// moke API to search partners
export const queryPartners = async (query: PartnerQuery): Promise<PartnerSearchViewModel[]> => {
  const results = partners
    .filter((partner) => partner.entryPrice >= (query.minPrice || 0))
    .filter((partner) => partner.entryPrice <= (query.maxPrice || Number.MAX_VALUE))
    .filter(
      (partner) =>
        !query.tagsId ||
        query.tagsId.length === 0 ||
        partner.tags.some((tag) => query.tagsId?.includes(tag.id)),
    )
    .map((partner) => ({
      ...partner,
      ...{
        availability: query.dateFilter ? partnerAvailability(partner, query.dateFilter) : undefined,
      },
    }))

  return new Promise((resolve) => {
    resolve(results)
  })
}

const partnerAvailability = (partner: Partner, date: Date): string => {
  const calendar = partner.calendar || []
  const entry = calendar.find((entry) => dayjs(entry.date).isSame(date, 'day'))
  return entry ? entry.state : 'available'
}

// moke API to get a partner by slug
export const findPartnerBySlug = async (slug: string): Promise<Partner> => {
  // moke API to get a partner by id
  const partner = partners.find((partner) => partner.slug === slug)
  return new Promise((resolve, reject) => {
    if (!partner) {
      reject(new Error('404'))
    } else {
      resolve(partner)
    }
  })
}

export const partnerCalendar = async (id: string): Promise<CalendarEntry[]> => {
  const partner = partners.find((partner) => partner.id === id)
  return new Promise((resolve, reject) => {
    if (!partner) {
      reject(new Error('Partner not found'))
    } else {
      resolve(partner?.calendar || [])
    }
  })
}

export const relatedPartners = async (id: string): Promise<PartnerSearchViewModel[]> => {
  const partner = partners.find((partner) => partner.id === id)
  return new Promise((resolve, reject) => {
    if (!partner) {
      reject(new Error('Partner not found'))
    } else {
      const tagRelated = partners.filter(
        (p) =>
          p.id !== partner.id && p.tags.some((tag) => partner.tags.some((t) => t.id === tag.id)),
      )
      const categoryRelated = partners.filter(
        (p) =>
          p.id !== partner.id &&
          !tagRelated.includes(p) &&
          p.tags.some((tag) => partner.tags.some((t) => t.category === tag.category)),
      )
      const relateds = [...tagRelated, ...categoryRelated]

      resolve(relateds.map((p) => ({ ...p, ...{ availability: 'available' } })))
    }
  })
}

// Mock data
const partners: Partner[] = [
  // Category: Music
  {
    id: '1',
    title: 'Gypsy Reed Ensemble',
    slug: 'gypsy-reed-ensemble',
    description: 'Un groupe de jazz parfait pour animer vos soirées.',
    longDescription:
      'Plongez dans l’univers captivant du Gypsy Reed Ensemble, un groupe de jazz qui mêle élégance et passion. Leur musique envoûtante saura créer une ambiance raffinée et chaleureuse, idéale pour vos événements privés ou professionnels. Laissez-vous séduire par leurs mélodies uniques et leur maîtrise musicale incomparable.',
    imageUrl: './images/jazz-band.jpg',
    youtubeId: '_A6w3ECkN4k',
    tags: [{ id: '1', name: 'Jazz', category: 'music' }],
    entryPrice: 900,
    prices: [
      { id: '1', title: 'Formule 5 musiciens', price: 1500 },
      { id: '2', title: 'Formule 4 musiciens', price: 1200 },
      { id: '3', title: 'Formule 3 musiciens', price: 900 },
    ],
    calendar: [
      { id: '1', date: new Date('2025-01-24'), state: 'booked' },
      { id: '2', date: new Date('2025-01-25'), state: 'option' },
    ],
  },
  {
    id: '2',
    title: 'Starlight Pulse',
    slug: 'starlight-pulse',
    description: 'Un groupe pop-rock énergique pour une ambiance inoubliable.',
    longDescription:
      'Starlight Pulse apporte une énergie débordante et des rythmes entraînants pour faire vibrer votre soirée. Avec un répertoire varié, allant des classiques pop-rock aux hits modernes, ce groupe saura ravir tous vos invités. Offrez à votre événement une ambiance électrique qui restera gravée dans les mémoires.',
    imageUrl: './images/pop-rock-band.jpg',
    youtubeId: '4sErhkkcOUU',
    tags: [{ id: '2', name: 'Pop-Rock', category: 'music' }],
    entryPrice: 900,
    prices: [
      { id: '1', title: 'Formule 5 musiciens', price: 1500 },
      { id: '2', title: 'Formule 4 musiciens', price: 1200 },
      { id: '3', title: 'Formule 3 musiciens', price: 900 },
    ],
    calendar: [{ id: '1', date: new Date('2025-01-24'), state: 'booked' }],
  },
  {
    id: '3',
    title: 'DJ Animation',
    slug: 'dj-animation',
    description: 'Un DJ professionnel pour rythmer vos soirées.',
    longDescription:
      'Avec DJ Animation aux platines, transformez votre soirée en un moment inoubliable. Maître dans l’art de lire la piste de danse, ce DJ adapte sa playlist en temps réel pour maintenir une ambiance au top. Une soirée animée, rythmée et sur mesure vous attend.',
    imageUrl: './images/dj-animation.jpg',
    youtubeId: 'zznewKVtKtk',
    tags: [{ id: '3', name: 'D.J.', category: 'music' }],
    entryPrice: 200,
    prices: [
      { id: '1', title: 'Animation de 1h', price: 200 },
      { id: '2', title: 'Animation de 2h', price: 300 },
      { id: '3', title: 'Animation de 3h', price: 400 },
      { id: '4', title: 'Animation de 4h', price: 500 },
      { id: '5', title: 'Animation de 5h', price: 600 },
    ],
  },
  {
    id: '4',
    title: 'Swing Spark',
    slug: 'swing-spark',
    description: 'Un quatuor de jazz pour une ambiance feutrée.',
    longDescription:
      'Swing Spark combine le charme du jazz classique avec une touche contemporaine pour sublimer vos événements. Ce quatuor talentueux crée une atmosphère sophistiquée et intime, idéale pour des réceptions élégantes. Offrez à vos invités une expérience musicale mémorable et pleine de finesse.',
    imageUrl: './images/jazz-quartet.jpg',
    youtubeId: 'sAo3d0zD-Fg',
    tags: [{ id: '1', name: 'Jazz', category: 'music' }],
    entryPrice: 900,
    prices: [
      { id: '1', title: 'Formule 5 musiciens', price: 1500 },
      { id: '2', title: 'Formule 4 musiciens', price: 1200 },
      { id: '3', title: 'Formule 3 musiciens', price: 900 },
    ],
  },
  {
    id: '5',
    title: 'The Jive Rebels',
    slug: 'the-jive-rebels',
    description: 'Un groupe rock pour faire vibrer votre public.',
    longDescription:
      'Préparez-vous à une explosion de sons et d’énergie avec The Jive Rebels ! Ce groupe de rock met le feu à la scène avec des performances électrisantes et un répertoire qui ravira les fans de rock. Assurez le succès de votre événement avec une ambiance vibrante et inoubliable.',
    imageUrl: './images/rock-band.jpg',
    youtubeId: 'BOCaSJOARFM',
    tags: [{ id: '2', name: 'Pop-Rock', category: 'music' }],
    entryPrice: 900,
    prices: [
      { id: '1', title: 'Formule 5 musiciens', price: 1500 },
      { id: '2', title: 'Formule 4 musiciens', price: 1200 },
      { id: '3', title: 'Formule 3 musiciens', price: 900 },
    ],
  },

  // Category: Food
  {
    id: '6',
    title: 'Éclat Gourmet',
    slug: 'eclat-gourmet',
    description: 'Un service traiteur haut de gamme pour vos événements.',
    longDescription:
      'Éclat Gourmet propose une expérience culinaire haut de gamme pour vos réceptions. Avec des menus créatifs, élaborés à partir de produits frais et locaux, ce traiteur transformera votre événement en un moment gastronomique unique. Offrez à vos invités le luxe d’une cuisine raffinée et savoureuse.',
    imageUrl: './images/traiteur-gourmet.jpg',
    youtubeId: '6D4_3NIpvkI',
    tags: [{ id: '4', name: 'Traiteur', category: 'food' }],
    entryPrice: 40,
    prices: [
      {
        id: '1',
        title: 'Menu 1 : 40€ / personne',
        price: 40,
        unity: 'personne(s)',
        minQuantity: 20,
      },
      {
        id: '2',
        title: 'Menu 2 : 70€ / personne',
        price: 70,
        unity: 'personne(s)',
        minQuantity: 20,
      },
      {
        id: '3',
        title: 'Menu 3 : 100€ / personne',
        price: 100,
        unity: 'personne(s)',
        minQuantity: 20,
      },
    ],
  },
  {
    id: '7',
    title: 'Food Truck Burgers',
    slug: 'food-truck-burgers',
    description: 'Des burgers gourmands pour régaler vos invités.',
    longDescription:
      'Apportez une touche conviviale et délicieuse à votre événement avec Food Truck Burgers. Spécialisé dans les burgers gourmets préparés sur place, ce food truck ravira les amateurs de bonne cuisine. Une solution parfaite pour une ambiance décontractée et un plaisir gustatif garanti.',
    imageUrl: './images/food-truck-burgers.jpg',
    youtubeId: 'z2xjErKDd-o',
    tags: [{ id: '5', name: 'Food Truck', category: 'food' }],
    entryPrice: 30,
    prices: [
      { id: '1', title: 'Formule Food Truck', price: 30, unity: 'personne(s)', minQuantity: 50 },
    ],
  },
  {
    id: '8',
    title: 'Bar à Cocktails',
    slug: 'bar-a-cocktails',
    description: 'Un bar à cocktails pour une expérience unique.',
    longDescription:
      'Transformez vos événements en expériences inoubliables avec notre Bar à Cocktails. Savourez des créations originales préparées par des mixologues experts, alliant des saveurs uniques et une présentation soignée. Une animation parfaite pour surprendre vos invités et créer des souvenirs mémorables.',
    imageUrl: './images/bar-cocktails.jpg',
    youtubeId: 'EFuBvEt84OI',
    tags: [{ id: '6', name: 'Boissons', category: 'food' }],
    entryPrice: 150,
  },
  {
    id: '9',
    title: 'Buffet de Cuisine Française',
    slug: 'buffet-cuisine-francaise',
    description: 'Un buffet raffiné pour sublimer vos réceptions.',
    longDescription:
      'Découvrez l’élégance de la gastronomie française avec ce buffet soigneusement préparé. Des plats traditionnels revisités avec finesse pour satisfaire les palais les plus exigeants. Idéal pour vos réceptions haut de gamme, ce buffet apportera une touche de prestige à votre événement.',
    imageUrl: './images/cuisine-francaise.jpg',
    youtubeId: '30dp3iP66Gs',
    tags: [{ id: '4', name: 'Traiteur', category: 'food' }],
    entryPrice: 40,
    prices: [
      { id: '1', title: 'Menu 1', price: 40, unity: 'personne(s)', minQuantity: 20 },
      { id: '2', title: 'Menu 2', price: 70, unity: 'personne(s)', minQuantity: 20 },
      { id: '3', title: 'Menu 3', price: 100, unity: 'personne(s)', minQuantity: 20 },
    ],
  },
  {
    id: '10',
    title: 'Camion Pizza',
    slug: 'camion-pizza',
    description: 'Une délicieuse pizza cuite au feu de bois sur place.',
    longDescription:
      'Régalez vos invités avec des pizzas artisanales préparées sous leurs yeux grâce à notre camion pizza. La cuisson au feu de bois garantit une saveur authentique et incomparable. Une solution conviviale et savoureuse pour ravir petits et grands lors de vos événements.',
    imageUrl: './images/pizza-truck.jpg',
    youtubeId: 'HOZs4hWPiKk',
    tags: [{ id: '5', name: 'Food Truck', category: 'food' }],
    entryPrice: 20,
    prices: [
      { id: '1', title: 'Formule Food Truck', price: 20, unity: 'personne(s)', minQuantity: 50 },
    ],
  },

  // Category: Place
  {
    id: '11',
    title: 'Salle des Fêtes de Rathsamhausen',
    slug: 'salle-des-fetes-rathsamhausen',
    description: 'Une salle spacieuse pour vos événements.',
    longDescription:
      'La Salle des Fêtes de Rathsamhausen est idéale pour accueillir vos événements en grand. Spacieuse et modulable, elle peut s’adapter à tous vos besoins, que ce soit pour des mariages, anniversaires ou séminaires. Offrez à vos invités un cadre pratique et chaleureux pour des moments inoubliables.',
    imageUrl: './images/salle-fetes.png',
    youtubeId: 'SdAQjcIMjpo',
    tags: [{ id: '7', name: 'Salle', category: 'place' }],
    entryPrice: 1000,
    prices: [{ id: '1', title: 'Location 1 journée', price: 1000 }],
  },
  {
    id: '12',
    title: 'Château pour Mariage',
    slug: 'chateau-pour-mariage',
    description: 'Un cadre enchanteur pour célébrer votre union.',
    longDescription:
      'Réalisez votre rêve de conte de fées avec ce château enchanteur pour votre mariage. Entouré de magnifiques jardins, il offre un cadre romantique et prestigieux pour une célébration inoubliable. Faites de ce jour spécial un moment magique pour vous et vos invités.',
    imageUrl: './images/chateau-mariage.jpg',
    youtubeId: '615biPQMdJQ',
    tags: [{ id: '7', name: 'Salle', category: 'place' }],
    entryPrice: 5000,
    prices: [{ id: '1', title: 'Location 1 journée', price: 5000 }],
  },
  {
    id: '13',
    title: 'Gîte de la mangouste',
    slug: 'gite-de-la-mangouste',
    description: 'Un hébergement confortable pour vos invités.',
    longDescription:
      'Offrez à vos invités le confort et la convivialité du Gîte de la Mangouste. Idéal pour des groupes, ce lieu propose des chambres accueillantes et des espaces communs chaleureux. Une option parfaite pour prolonger les festivités dans une ambiance détendue.',
    imageUrl: './images/hebergement-groupe.jpg',
    youtubeId: '_2tBuidbF88',
    tags: [{ id: '8', name: 'Hébergement', category: 'place' }],
    entryPrice: 300,
    prices: [{ id: '1', title: 'Location 1 nuit', price: 300 }],
  },
  {
    id: '14',
    title: 'Restaurant À la Couronne',
    slug: 'restaurant-a-la-couronne',
    description: 'Un restaurant privatisé pour vos réceptions.',
    longDescription:
      'Profitez de l’intimité et du charme du Restaurant À la Couronne pour vos réceptions. Avec une cuisine raffinée et un service impeccable, ce lieu privatisé est idéal pour vos dîners d’exception. Offrez à vos invités une expérience gastronomique inoubliable dans un cadre élégant.',
    imageUrl: './images/restaurant-prive.jpg',
    youtubeId: 'VXVCs-yWkCU',
    tags: [{ id: '9', name: 'Restaurant', category: 'place' }],
    entryPrice: 2000,
  },
  {
    id: '15',
    title: 'Salle de Séminaire',
    slug: 'salle-de-seminaire',
    description: 'Une salle équipée pour vos séminaires et conférences.',
    longDescription:
      'Organisez vos événements professionnels dans cette salle moderne et parfaitement équipée. Avec des installations audiovisuelles de pointe et une configuration flexible, elle répond à tous vos besoins. Faites de vos séminaires et conférences un succès dans un environnement professionnel et confortable.',
    imageUrl: './images/salle-seminaire.jpg',
    youtubeId: '12PccQAwbgE',
    tags: [{ id: '7', name: 'Salle', category: 'place' }],
    entryPrice: 1200,
  },

  // Category: Photo
  {
    id: '16',
    title: 'Léo Clairmont Photographe',
    slug: 'leo-clairmont-photographe',
    description: 'Des souvenirs inoubliables pour votre grand jour.',
    longDescription:
      'Capturez les moments les plus précieux de votre événement avec Léo Clairmont. Expert en photographie événementielle, il transforme chaque instant en un souvenir intemporel. Offrez-vous des clichés professionnels qui reflètent l’émotion et la beauté de votre journée.',
    imageUrl: './images/photographe-mariage.jpg',
    youtubeId: 'wlPFNYKkGSA',
    tags: [{ id: '10', name: 'Photographe', category: 'photo' }],
    entryPrice: 500,
    prices: [
      { id: '1', title: '5 heures / 50 photos', price: 500 },
      { id: '2', title: '5 heures / 100 photos', price: 700 },
    ],
  },
  {
    id: '17',
    title: 'Photobooth Vintage',
    slug: 'photobooth-vintage',
    description: 'Un photobooth original pour des photos funs.',
    longDescription:
      'Ajoutez une touche rétro et ludique à votre événement avec notre Photobooth Vintage. Accessoires, filtres et impressions instantanées garantissent des souvenirs amusants pour vos invités. Une animation qui apportera rire et bonne humeur à votre réception.',
    imageUrl: './images/photobooth-vintage.jpg',
    youtubeId: 'ybJPbM_ExTk',
    tags: [{ id: '11', name: 'Photobooth', category: 'photo' }],
    entryPrice: 400,
    prices: [{ id: '1', title: 'Location 1 journée', price: 400 }],
  },
  {
    id: '18',
    title: 'Studio Photo Mobile',
    slug: 'studio-photo-mobile',
    description: 'Un studio photo pour des portraits professionnels.',
    longDescription:
      'Apportez la qualité d’un studio photo professionnel directement à votre événement. Avec un éclairage parfait et un photographe expérimenté, le Studio Photo Mobile offre des portraits d’exception à vos invités. Une touche élégante et originale pour immortaliser vos moments spéciaux.',
    imageUrl: './images/studio-photo-mobile.jpg',
    youtubeId: 'VoHDwCCxWA4',
    tags: [{ id: '10', name: 'Photographe', category: 'photo' }],
    entryPrice: 800,
    prices: [{ id: '1', title: 'Prestation 1 journée', price: 800 }],
  },
]
