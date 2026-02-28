// app/utils/MockData.ts
import type { PrestataireDetail } from '~/types/prestataire'

export const MOCK_PROVIDERS: PrestataireDetail[] = [
  // --- MUSIQUE (ID: '2') ---
  {
    id: '1',
    name: 'Gypsy Reed Ensemble',
    slug: 'gypsy-reed-ensemble',
    category: '2',
    subcats: ['23'], // Jazz
    shortDescription: 'Un groupe de jazz parfait pour animer vos soirées.',
    longDescription: 'Plongez dans l’univers captivant du Gypsy Reed Ensemble...',
    image: '/images/jazz-band.jpg',
    youtubeId: '_A6w3ECkN4k',
  },
  {
    id: '2',
    name: 'Starlight Pulse',
    slug: 'starlight-pulse',
    category: '2',
    subcats: ['22'], // Pop-Rock
    shortDescription: 'Un groupe pop-rock énergique pour une ambiance inoubliable.',
    longDescription: 'Starlight Pulse apporte une énergie débordante...',
    image: '/images/pop-rock-band.jpg',
    youtubeId: '4sErhkkcOUU',
  },
  {
    id: '3',
    name: 'DJ Animation',
    slug: 'dj-animation',
    category: '2',
    subcats: ['21'], // DJ
    shortDescription: 'Un DJ professionnel pour rythmer vos soirées.',
    longDescription: 'Avec DJ Animation aux platines, transformez votre soirée...',
    image: '/images/dj-animation.jpg',
    youtubeId: 'zznewKVtKtk',
  },
  {
    id: '5',
    name: 'The Jive Rebels',
    slug: 'the-jive-rebels',
    category: '2',
    subcats: ['22'], // Pop-Rock
    shortDescription: 'Un groupe rock pour faire vibrer votre public.',
    longDescription: 'Préparez-vous à une explosion de sons et d’énergie...',
    image: '/images/rock-band.jpg',
    youtubeId: 'BOCaSJOARFM',
  },

  // --- RESTAURATION (ID: '3') ---
  {
    id: '6',
    name: 'Éclat Gourmet',
    slug: 'eclat-gourmet',
    category: '3',
    subcats: ['31'], // Traiteur
    shortDescription: 'Un service traiteur haut de gamme pour vos événements.',
    longDescription: 'Éclat Gourmet propose une expérience culinaire haut de gamme...',
    image: '/images/traiteur-gourmet.jpg',
    youtubeId: '6D4_3NIpvkI',
  },
  {
    id: '7',
    name: 'Food Truck Burgers',
    slug: 'food-truck-burgers',
    category: '3',
    subcats: ['32'], // Food Truck
    shortDescription: 'Des burgers gourmands pour régaler vos invités.',
    longDescription: 'Apportez une touche conviviale et délicieuse...',
    image: '/images/food-truck-burgers.jpg',
    youtubeId: 'z2xjErKDd-o',
  },
  {
    id: '8',
    name: 'Bar à Cocktails',
    slug: 'bar-a-cocktails',
    category: '3',
    subcats: ['31'], // Mis en Traiteur/Boissons
    shortDescription: 'Un bar à cocktails pour une expérience unique.',
    longDescription: 'Transformez vos événements en expériences inoubliables...',
    image: '/images/bar-cocktails.jpg',
    youtubeId: 'EFuBvEt84OI',
  },
  {
    id: '9',
    name: 'Buffet Cuisine Française',
    slug: 'buffet-cuisine-francaise',
    category: '3',
    subcats: ['31'], // Traiteur
    shortDescription: 'Un buffet raffiné pour sublimer vos réceptions.',
    longDescription: 'Découvrez l’élégance de la gastronomie française...',
    image: '/images/cuisine-francaise.jpg',
    youtubeId: '30dp3iP66Gs',
  },
  {
    id: '10',
    name: 'Camion Pizza',
    slug: 'camion-pizza',
    category: '3',
    subcats: ['32'], // Food Truck
    shortDescription: 'Une délicieuse pizza cuite au feu de bois sur place.',
    longDescription: 'Régalez vos invités avec des pizzas artisanales...',
    image: '/images/pizza-truck.jpg',
    youtubeId: 'HOZs4hWPiKk',
  },

  // --- PHOTO (ID: '4') ---
  {
    id: '16',
    name: 'Léo Clairmont Photographe',
    slug: 'leo-clairmont-photographe',
    category: '4',
    subcats: ['41'], // Photographe
    shortDescription: 'Des souvenirs inoubliables pour votre grand jour.',
    longDescription: 'Capturez les moments les plus précieux...',
    image: '/images/photographe-mariage.jpg',
    youtubeId: 'wlPFNYKkGSA',
  },
  {
    id: '17',
    name: 'Photobooth Vintage',
    slug: 'photobooth-vintage',
    category: '4',
    subcats: ['43'], // Photobooth
    shortDescription: 'Un photobooth original pour des photos funs.',
    longDescription: 'Ajoutez une touche rétro et ludique...',
    image: '/images/photobooth-vintage.jpg',
    youtubeId: 'ybJPbM_ExTk',
  },
  {
    id: '18',
    name: 'Studio Photo Mobile',
    slug: 'studio-photo-mobile',
    category: '4',
    subcats: ['41'], // Photographe
    shortDescription: 'Un studio photo pour des portraits professionnels.',
    longDescription: 'Apportez la qualité d’un studio photo professionnel...',
    image: '/images/studio-photo-mobile.jpg',
    youtubeId: 'VoHDwCCxWA4',
  },

  // --- SERVICES / LIEUX (ID: '5') ---
  {
    id: '11',
    name: 'Salle des Fêtes Rathsamhausen',
    slug: 'salle-fetes-rathsamhausen',
    category: '5',
    subcats: ['52'], // Location matériel/Lieu
    shortDescription: 'Une salle spacieuse pour vos événements.',
    longDescription: 'La Salle des Fêtes est idéale pour accueillir...',
    image: '/images/salle-fetes.png',
    youtubeId: 'SdAQjcIMjpo',
  },
  {
    id: '12',
    name: 'Château pour Mariage',
    slug: 'chateau-mariage',
    category: '5',
    subcats: ['52'],
    shortDescription: 'Un cadre enchanteur pour célébrer votre union.',
    longDescription: 'Réalisez votre rêve de conte de fées...',
    image: '/images/chateau-mariage.jpg',
    youtubeId: '615biPQMdJQ',
  },
  {
    id: '13',
    name: 'Gîte de la mangouste',
    slug: 'gite-de-la-mangouste',
    category: '5',
    subcats: ['52'],
    shortDescription: 'Un hébergement confortable pour vos invités.',
    longDescription: 'Offrez à vos invités le confort et la convivialité...',
    image: '/images/hebergement-groupe.jpg',
    youtubeId: '_2tBuidbF88',
  },
]
