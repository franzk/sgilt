// app/utils/mockData.ts
import type { PrestataireDetail } from '~/types/prestataire'

export const MOCK_PROVIDERS: PrestataireDetail[] = [
  // ─── MUSIQUE ──────────────────────────────────────────────────────────────

  {
    id: '3',
    name: 'DJ Animation',
    slug: 'dj-animation',
    baseline: 'Votre soirée mérite une bande-son sur mesure.',
    categoryId: '2',
    category: 'Musique',
    subcats: ['21'],
    image: '/images/prestataires/dj-animation.jpg',
    heroImage: '/images/prestataires/dj-animation.jpg',
    photos: ['https://picsum.photos/seed/dj1/800/600', 'https://picsum.photos/seed/dj2/800/600'],
    youtubeId: 'zznewKVtKtk',
    shortDescription: 'Un DJ professionnel pour rythmer vos soirées.',
    badges: [
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
    ],
    offerings: [
      'Animation DJ pour mariages et soirées privées',
      "Sonorisation complète incluse (jusqu'à 200 personnes)",
      "Éclairage d'ambiance",
      'Formule duo avec animateur micro sur demande',
      'Set DJ live ou playlist curatée selon vos préférences',
    ],
    identity: {
      quote: 'Chaque soirée est unique',
      bio: "Passionné de musique depuis plus de 15 ans, je mets autant de soin dans le choix d'un morceau que dans l'écoute de ce que vous attendez.",
    },
    budget: 'Tarif selon durée et configuration. Devis personnalisé sur demande.',
    unavailableDates: [
      '2026-04-04',
      '2026-04-11',
      '2026-04-18',
      '2026-05-02',
      '2026-05-09',
      '2026-06-06',
      '2026-06-20',
      '2026-06-27',
    ],
    testimonials: [
      {
        author: 'Sophie & Marc',
        text: "La piste de danse n'a pas désempli de la soirée. Un DJ à l'écoute, des transitions impeccables.",
        eventType: 'Mariage',
      },
      {
        author: 'Thomas D.',
        text: "On lui a donné carte blanche et il a su lire la salle parfaitement. Exactement ce qu'on voulait.",
        eventType: 'Anniversaire',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et départements limitrophes",
      'Installation : 1h30 avant le début de la prestation',
      'Espace requis : 4m² minimum, alimentation 220V',
      'Stationnement à prévoir à proximité pour le matériel',
    ],
    technical: [
      "Système de sonorisation Pioneer DJ (jusqu'à 200 personnes)",
      'Table de mixage Pioneer DJM-900NXS2',
      'Éclairage LED par défaut inclus',
      'Micro sans fil disponible sur demande',
    ],
    faq: [
      {
        question: "Est-ce que je peux soumettre des morceaux à l'avance ?",
        answer:
          'Oui, un échange est prévu avant la prestation pour construire ensemble une liste de souhaits et de morceaux à éviter.',
      },
      {
        question: 'La sono est-elle incluse dans le tarif ?',
        answer:
          "Oui, la sonorisation jusqu'à 200 personnes est incluse. Pour des jauges plus importantes, un devis spécifique est établi.",
      },
      {
        question: "Que se passe-t-il en cas d'imprévu ou d'annulation ?",
        answer:
          "Les conditions d'annulation sont précisées dans le contrat. En cas d'imprévu de ma part, je m'engage à proposer un remplaçant de confiance.",
      },
    ],
  },

  {
    id: '1',
    name: 'Gypsy Reed Ensemble',
    slug: 'gypsy-reed-ensemble',
    baseline: 'Le jazz manouche qui fait danser les âmes.',
    categoryId: '2',
    category: 'Musique',
    subcats: ['23'],
    image: '/images/prestataires/jazz-band.jpg',
    heroImage: '/images/prestataires/jazz-band.jpg',
    photos: [
      'https://picsum.photos/seed/jazz1/800/600',
      'https://picsum.photos/seed/jazz2/800/600',
    ],
    shortDescription: 'Un groupe de jazz parfait pour animer vos soirées.',
    youtubeId: '_A6w3ECkN4k',
    badges: [
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
    ],
    offerings: [
      'Quartet jazz manouche (guitare, contrebasse, violon, guitare rythmique)',
      'Formule trio disponible pour les petits espaces',
      'Répertoire : Reinhardt, Grappelli, standards jazz et bossa',
      'Musique de fond pour cocktail ou dîner',
      'Set dansant en soirée sur demande',
      'Sonorisation légère incluse pour les formules sonorisées',
    ],
    identity: {
      quote: 'Quatre musiciens, une passion commune : la musique qui voyage.',
      bio: 'On joue ensemble depuis 2018, et chaque concert est une conversation entre nos instruments. On aime autant jouer dans un château alsacien que dans une grange rénovée.',
    },
    budget:
      'À partir de 800€ pour une formule cocktail (2h). Tarif dégressif selon la durée. Devis sur demande.',
    unavailableDates: [
      '2026-04-25',
      '2026-05-16',
      '2026-05-23',
      '2026-06-13',
      '2026-07-04',
      '2026-07-11',
      '2026-08-01',
      '2026-08-15',
    ],
    testimonials: [
      {
        author: 'Claire & Antoine',
        text: "Nos invités en parlent encore. Une ambiance incroyable du cocktail jusqu'au dessert, exactement ce qu'on cherchait.",
        eventType: 'Mariage',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et Grand Est, déplacement possible au-delà sur devis",
      'Installation : 45 minutes avant la prestation',
      'Espace requis : 6m² minimum pour le quartet',
      'Formule acoustique : aucun besoin électrique',
      'Formule sonorisée : alimentation 220V nécessaire',
    ],
    technical: [
      'Quartet : 2 guitares, contrebasse, violon',
      'Sonorisation légère HK Audio disponible sur demande',
      'Microphones de scène pour les formules sonorisées',
    ],
    faq: [
      {
        question: 'Peut-on demander des morceaux spécifiques ?',
        answer:
          'Oui, nous acceptons les demandes en amont dans la limite de notre répertoire. Un échange par email est prévu après la réservation pour en discuter.',
      },
      {
        question: 'Jouez-vous en extérieur ?',
        answer:
          "Oui, à condition que le sol soit stable et qu'un espace couvert soit disponible en cas de pluie. La formule acoustique est particulièrement adaptée aux extérieurs.",
      },
    ],
  },

  {
    id: '2',
    name: 'Starlight Pulse',
    slug: 'starlight-pulse',
    baseline: 'Pop, rock, dancefloor — une énergie qui ne retombe pas.',
    categoryId: '2',
    category: 'Musique',
    subcats: ['22'],
    image: '/images/prestataires/pop-rock-band.jpg',
    heroImage: '/images/prestataires/pop-rock-band.jpg',
    photos: [
      'https://picsum.photos/seed/poprock1/800/600',
      'https://picsum.photos/seed/poprock2/800/600',
      'https://picsum.photos/seed/poprock3/800/600',
    ],
    shortDescription: 'Un groupe pop-rock énergique pour une ambiance inoubliable.',
    youtubeId: '4sErhkkcOUU',
    badges: [
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
      { icon: 'Eco', label: 'Éco', description: 'Éco-responsable', color: '#22C55E' },
    ],
    offerings: [
      'Groupe de 4 musiciens (guitare, basse, batterie, clavier)',
      'Deux chanteurs / chanteuses',
      "Répertoire pop-rock français et international des années 80 à aujourd'hui",
      'Formule concert (2 sets de 45min) ou animation continue',
      'Karaoké animé en option',
    ],
    identity: {
      quote: 'On est là pour que personne ne reste assis.',
      bio: 'Depuis 2015, on parcourt les salles alsaciennes avec une seule obsession : que vous repartiez avec des crampes aux jambes.',
    },
    budget:
      'Formule soirée complète à partir de 1 800€. Devis sur demande selon durée et configuration.',
    unavailableDates: [
      '2026-04-18',
      '2026-05-09',
      '2026-05-30',
      '2026-06-13',
      '2026-07-18',
      '2026-08-08',
    ],
    testimonials: [
      {
        author: 'Julie & Romain',
        text: 'Tout le monde dansait, même les grands-parents. Un groupe incroyable, pro du début à la fin.',
        eventType: 'Mariage',
      },
      {
        author: 'Comité des fêtes de Molsheim',
        text: "Troisième fois qu'on les fait venir. La salle est toujours pleine et le public repart ravi.",
        eventType: 'Fête communale',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et Grand Est",
      'Installation : 2h avant le début',
      'Espace scène requis : 12m² minimum',
      'Alimentation : 2 prises 16A nécessaires',
    ],
    technical: [
      'Système HK Audio (300 personnes)',
      'Backline complet fourni (batterie, amplis)',
      'Jeux de lumières scéniques inclus',
      'Retours de scène pour les musiciens',
    ],
    faq: [
      {
        question: 'Combien de temps dure une prestation type ?',
        answer:
          'Une soirée classique comprend 2 sets de 45 minutes avec une pause. Une animation continue de 3h est aussi possible sur demande.',
      },
      {
        question: 'Peut-on avoir une surprise musicale ?',
        answer:
          'Oui, on adore les moments surprise — chanson dédiée, entrée des mariés personnalisée. Dites-nous ce que vous avez en tête.',
      },
    ],
  },

  {
    id: '5',
    name: 'The Jive Rebels',
    slug: 'the-jive-rebels',
    baseline: 'Du rock qui transpire, des riffs qui restent.',
    categoryId: '2',
    category: 'Musique',
    subcats: ['22'],
    image: '/images/prestataires/rock-band.jpg',
    heroImage: '/images/prestataires/rock-band.jpg',
    photos: [
      'https://picsum.photos/seed/rock1/800/600',
      'https://picsum.photos/seed/rock2/800/600',
      'https://picsum.photos/seed/rock3/800/600',
    ],
    shortDescription: 'Un groupe rock pour faire vibrer votre public.',
    youtubeId: 'BOCaSJOARFM',
    badges: [
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
    ],
    offerings: [
      'Trio ou quartet rock (guitare, basse, batterie + chant)',
      'Répertoire : classic rock, hard rock, grunge des années 70 à 2000',
      '2 sets de 45 minutes ou set unique de 90 minutes',
      'Sonorisation incluse',
    ],
    identity: {
      quote: 'Juste du rock honnête pour un public qui aime ça.',
      bio: "On joue du rock parce que rien d'autre ne nous convient. Pas de compromis sur le son, pas de titre trop lisse.",
    },
    budget: 'À partir de 1 200€ pour une soirée. Devis sur demande.',
    unavailableDates: ['2026-05-02', '2026-05-23', '2026-06-27', '2026-07-11', '2026-08-22'],
    testimonials: [
      {
        author: 'Marc D.',
        text: "Une énergie de dingue. Le public n'en revenait pas — et pourtant c'était une salle de mairie !",
        eventType: 'Soirée privée',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et régions limitrophes",
      'Installation : 2h avant',
      'Scène minimum : 10m²',
      'Alimentation : 2 prises 16A',
    ],
    technical: [
      'Backline complet fourni',
      "Sonorisation Yamaha jusqu'à 250 personnes",
      'Éclairage de scène inclus',
    ],
    faq: [
      {
        question: 'Jouez-vous en extérieur ?',
        answer:
          'Oui, avec un espace couvert pour le matériel en cas de pluie et une surface stable pour la batterie.',
      },
    ],
  },

  // ─── RESTAURATION ─────────────────────────────────────────────────────────

  {
    id: '6',
    name: 'Éclat Gourmet',
    slug: 'eclat-gourmet',
    baseline: 'Une cuisine raffinée qui sublime chaque moment.',
    categoryId: '3',
    category: 'Restauration',
    subcats: ['31'],
    image: '/images/prestataires/traiteur-gourmet.jpg',
    heroImage: '/images/prestataires/traiteur-gourmet.jpg',
    photos: [
      'https://picsum.photos/seed/catering1/800/600',
      'https://picsum.photos/seed/catering2/800/600',
      'https://picsum.photos/seed/catering3/800/600',
    ],
    shortDescription: 'Un service traiteur haut de gamme pour vos événements.',
    youtubeId: '6D4_3NIpvkI',
    badges: [
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
      { icon: 'Eco', label: 'Éco', description: 'Éco-responsable', color: '#22C55E' },
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
    ],
    offerings: [
      'Cocktail dînatoire ou repas assis (de 20 à 300 personnes)',
      'Buffet froid ou chaud',
      'Service à table avec personnel inclus',
      'Vaisselle et nappage fournis sur demande',
      'Formule fromages et desserts alsaciens disponible',
    ],
    identity: {
      quote: 'On cuisine comme on reçoit.',
      bio: "Avec soin, sans se prendre trop au sérieux. Ce qui compte, c'est que vos invités se régalent et que vous profitiez de votre fête.",
    },
    budget:
      'À partir de 45€ par personne pour un cocktail dînatoire. Devis sur demande selon formule et nombre de convives.',
    unavailableDates: [
      '2026-04-11',
      '2026-05-02',
      '2026-05-23',
      '2026-06-06',
      '2026-06-20',
      '2026-07-04',
      '2026-08-01',
      '2026-08-29',
    ],
    testimonials: [
      {
        author: 'Famille Schneider',
        text: 'Tous nos invités nous ont demandé le contact du traiteur. Les plats étaient beaux et délicieux.',
        eventType: 'Anniversaire',
      },
      {
        author: 'Entreprise Strasbourgeoise',
        text: 'Impeccable pour notre séminaire. Ponctuel, professionnel, et les retours des collaborateurs ont été unanimes.',
        eventType: 'Séminaire',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace",
      'Installation : 2h avant le service',
      'Cuisine sur place possible si équipement disponible',
      'Livraison froide possible pour les petits événements',
    ],
    technical: [
      'Matériel de maintien en température fourni',
      'Personnel de service disponible (1 serveur pour 20 personnes)',
      'Vaisselle en location possible',
    ],
    faq: [
      {
        question: 'Proposez-vous des options végétariennes ou sans gluten ?',
        answer:
          'Oui, systématiquement. Tous les régimes alimentaires sont pris en compte dès la conception du menu.',
      },
      {
        question: "Jusqu'à combien de personnes pouvez-vous intervenir ?",
        answer:
          'Nous gérons des événements de 20 à 300 personnes. Au-delà, nous pouvons étudier la demande selon le format.',
      },
    ],
  },

  {
    id: '7',
    name: 'Food Truck Burgers',
    slug: 'food-truck-burgers',
    baseline: 'Des burgers qui méritent le détour, livrés à votre événement.',
    categoryId: '3',
    category: 'Restauration',
    subcats: ['32'],
    image: '/images/prestataires/food-truck-burgers.jpg',
    heroImage: '/images/prestataires/food-truck-burgers.jpg',
    photos: [
      'https://picsum.photos/seed/burger1/800/600',
      'https://picsum.photos/seed/burger2/800/600',
      'https://picsum.photos/seed/burger3/800/600',
    ],
    shortDescription: 'Des burgers gourmands pour régaler vos invités.',
    youtubeId: 'z2xjErKDd-o',
    badges: [
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
    ],
    offerings: [
      'Service au camion pour événements privés ou publics',
      'Carte de 4 à 6 burgers selon formule',
      'Options végétariennes et sans gluten disponibles',
      'Frites fraîches maison',
      'Boissons et desserts sur demande',
    ],
    identity: {
      quote: 'Chaque burger est une fierté.',
      bio: "Le food truck, c'est nous depuis 2017. On a commencé sur les marchés alsaciens, et on ne s'est jamais lassés.",
    },
    budget:
      'Forfait événement à partir de 600€ pour 50 personnes. Tarif au convive possible selon format.',
    unavailableDates: ['2026-04-25', '2026-05-16', '2026-06-27', '2026-07-18', '2026-08-15'],
    testimonials: [
      {
        author: 'Lucas M.',
        text: "Le meilleur burger que j'ai mangé de ma vie, et dans le jardin de mes parents en plus. Tout le monde a adoré.",
        eventType: 'Anniversaire',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et départements limitrophes",
      'Arrivée 1h avant le service',
      'Surface plane requise pour le stationnement du camion',
      'Branchement électrique 220V ou groupe électrogène fourni',
    ],
    technical: ['Camion équipé plancha et friteuse', 'Groupe électrogène disponible si besoin'],
    faq: [
      {
        question: 'Combien de burgers par heure le camion peut-il servir ?',
        answer:
          'Environ 80 à 100 couverts par heure selon la formule. Pour les grands événements, un second camion peut être mobilisé.',
      },
    ],
  },

  {
    id: '8',
    name: 'Bar à Cocktails',
    slug: 'bar-a-cocktails',
    baseline: 'Des cocktails qui racontent une histoire, à votre santé.',
    categoryId: '3',
    category: 'Restauration',
    subcats: ['31'],
    image: '/images/prestataires/bar-cocktails.jpg',
    heroImage: '/images/prestataires/bar-cocktails.jpg',
    photos: [
      'https://picsum.photos/seed/cocktail1/800/600',
      'https://picsum.photos/seed/cocktail2/800/600',
      'https://picsum.photos/seed/cocktail3/800/600',
    ],
    shortDescription: 'Un bar à cocktails pour une expérience unique.',
    youtubeId: 'EFuBvEt84OI',
    badges: [
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
    ],
    offerings: [
      'Bar mobile pour événements privés et professionnels',
      'Carte de cocktails classiques et signatures',
      'Mocktails et boissons sans alcool',
      'Animations bar : cours de cocktail, show flair sur demande',
      'Glace, garnitures et matériel fournis',
    ],
    identity: {
      quote: 'Chaque cocktail est une histoire à raconter.',
      bio: "On ne verse pas juste un liquide — on crée un moment. Notre bar s'adapte à votre décor et à votre ambiance.",
    },
    budget:
      'Forfait à partir de 500€ pour 50 personnes (2h de service). Tarif à la consommation possible.',
    unavailableDates: ['2026-04-18', '2026-05-09', '2026-06-06', '2026-07-04', '2026-08-01'],
    testimonials: [
      {
        author: 'Amélie R.',
        text: "Le cocktail signature qu'ils ont créé pour notre mariage était parfait. Les invités en ont reparlé toute la soirée.",
        eventType: 'Mariage',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace",
      'Installation du bar : 1h avant le service',
      'Espace requis : 4m² minimum',
      'Alimentation 220V nécessaire',
    ],
    technical: [
      'Bar mobile complet fourni',
      'Réfrigération embarquée',
      'Verrerie et matériel de bar inclus',
    ],
    faq: [
      {
        question: 'Peut-on avoir un bar ouvert toute la soirée ?',
        answer:
          'Oui, on propose des formules open bar ou à la consommation selon votre préférence et votre budget.',
      },
    ],
  },

  {
    id: '9',
    name: 'Buffet Cuisine Française',
    slug: 'buffet-cuisine-francaise',
    baseline: 'La générosité du buffet, la finesse de la cuisine française.',
    categoryId: '3',
    category: 'Restauration',
    subcats: ['31'],
    image: '/images/prestataires/cuisine-francaise.jpg',
    heroImage: '/images/prestataires/cuisine-francaise.jpg',
    photos: [
      'https://picsum.photos/seed/french1/800/600',
      'https://picsum.photos/seed/french2/800/600',
      'https://picsum.photos/seed/french3/800/600',
    ],
    shortDescription: 'Un buffet raffiné pour sublimer vos réceptions.',
    youtubeId: '30dp3iP66Gs',
    badges: [
      { icon: 'Eco', label: 'Éco', description: 'Éco-responsable', color: '#22C55E' },
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
    ],
    offerings: [
      'Buffet froid ou mixte (chaud/froid)',
      'Entrées, plats, fromages, desserts — formule complète ou à la carte',
      'Spécialités alsaciennes disponibles',
      'Service avec ou sans personnel',
      'Vaisselle et nappage fournis en option',
    ],
    identity: {
      quote: 'Pas de fioritures, juste du bon.',
      bio: "Notre cuisine est celle qu'on aimerait manger chez des amis : généreuse, vraie, faite avec du temps.",
    },
    budget: 'À partir de 35€ par personne. Devis personnalisé selon formule et nombre.',
    unavailableDates: ['2026-05-02', '2026-05-30', '2026-06-20', '2026-07-11', '2026-08-08'],
    testimonials: [
      {
        author: 'Association Sportive Obernai',
        text: 'Pour notre gala annuel, le buffet était à la hauteur. Copieux, savoureux, et le personnel très agréable.',
        eventType: 'Gala',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace",
      'Livraison et installation 1h30 avant le service',
      'Cuisine sur place possible selon équipement disponible',
    ],
    technical: ['Matériel de maintien en température fourni', 'Personnel de service en option'],
    faq: [
      {
        question: 'Peut-on intégrer des spécialités alsaciennes au menu ?',
        answer:
          "Absolument. Choucroute, baeckeoffe, tarte flambée, kougelhopf — on adore mettre la région à l'honneur.",
      },
    ],
  },

  {
    id: '10',
    name: 'Camion Pizza',
    slug: 'camion-pizza',
    baseline: 'Le four à bois se déplace, la vraie pizza aussi.',
    categoryId: '3',
    category: 'Restauration',
    subcats: ['32'],
    image: '/images/prestataires/pizza-truck.jpg',
    heroImage: '/images/prestataires/pizza-truck.jpg',
    photos: [
      'https://picsum.photos/seed/pizza1/800/600',
      'https://picsum.photos/seed/pizza2/800/600',
      'https://picsum.photos/seed/pizza3/800/600',
    ],
    shortDescription: 'Une délicieuse pizza cuite au feu de bois sur place.',
    youtubeId: 'HOZs4hWPiKk',
    badges: [
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
    ],
    offerings: [
      'Service pizza au feu de bois pour événements privés',
      'Carte de 6 à 8 pizzas classiques et spéciales',
      'Options végétariennes disponibles',
      'Pizza blanche (sans tomate) disponible',
      'Service continu ou par vagues selon format',
    ],
    identity: {
      quote: 'Un voyage en Italie.',
      bio: "On est tombés amoureux de la pizza napolitaine lors d'un voyage en Italie. Depuis 2019, on partage cette passion en Alsace avec un four qu'on a ramené de là-bas.",
    },
    budget: 'Forfait événement à partir de 550€ pour 50 personnes.',
    unavailableDates: ['2026-04-04', '2026-05-16', '2026-06-13', '2026-07-25', '2026-08-22'],
    testimonials: [
      {
        author: 'Théo & Camille',
        text: 'Les pizzas étaient incroyables et le spectacle du four à bois a fasciné tout le monde, même les enfants.',
        eventType: 'Mariage',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et Grand Est",
      'Surface plane et accessible pour le camion',
      'Distance de sécurité de 3m autour du four',
      'Autonomie totale, pas de branchement nécessaire',
    ],
    technical: [
      'Four à bois embarqué (400°C)',
      'Autonomie complète en bois et matériel',
      'Capacité : 80 à 120 pizzas par heure',
    ],
    faq: [
      {
        question: 'Le camion peut-il venir dans un jardin privé ?',
        answer:
          "Oui, si l'accès est suffisant (largeur min. 2,5m) et que le sol est stable. On vérifie ça ensemble en amont.",
      },
    ],
  },

  // ─── PHOTO ────────────────────────────────────────────────────────────────

  {
    id: '16',
    name: 'Léo Clairmont Photographe',
    slug: 'leo-clairmont-photographe',
    baseline: 'Des images qui gardent ce que la mémoire oublie.',
    categoryId: '4',
    category: 'Photo',
    subcats: ['41'],
    image: '/images/prestataires/photographe-mariage.jpg',
    heroImage: '/images/prestataires/photographe-mariage.jpg',
    photos: [
      'https://picsum.photos/seed/wedding1/800/600',
      'https://picsum.photos/seed/wedding2/800/600',
      'https://picsum.photos/seed/wedding3/800/600',
    ],
    shortDescription: 'Des souvenirs inoubliables pour votre grand jour.',
    youtubeId: 'wlPFNYKkGSA',
    badges: [
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
    ],
    offerings: [
      'Reportage photo mariage (journée complète ou demi-journée)',
      'Séance couple avant le mariage incluse dans la formule complète',
      'Photos de famille et portraits',
      'Reportage événementiel (anniversaire, baptême, séminaire)',
      'Tirage et album photo disponibles en option',
    ],
    identity: {
      quote: "Saisir l'instant présent",
      bio: "Je photographie des gens, pas des poses. Mon travail, c'est d'être là au bon moment — discret, patient, attentif. Les photos que je préfère sont celles où personne ne regardait l'objectif.",
    },
    budget: 'Formule mariage à partir de 1 400€ (demi-journée). Journée complète sur devis.',
    unavailableDates: [
      '2026-04-25',
      '2026-05-09',
      '2026-05-30',
      '2026-06-06',
      '2026-06-27',
      '2026-07-18',
      '2026-08-08',
      '2026-08-29',
    ],
    testimonials: [
      {
        author: 'Emma & Louis',
        text: "On a pleuré en découvrant les photos. Léo a capté des moments qu'on n'avait même pas vus sur le moment.",
        eventType: 'Mariage',
      },
      {
        author: 'Sarah M.',
        text: "Séance famille absolument parfaite. Les enfants l'ont adoré et les photos sont magnifiques.",
        eventType: 'Séance famille',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace, déplacement possible en France sur devis",
      'Repérage des lieux proposé avant le jour J',
      'Second photographe disponible en option pour les grands événements',
    ],
    technical: [
      'Boîtiers Sony A7 plein format (×2)',
      'Eclairage flash discret pour les intérieurs',
      'Sauvegarde double sur place',
    ],
    faq: [
      {
        question: 'Combien de photos livrez-vous ?',
        answer:
          'Pour un mariage journée complète, entre 400 et 600 photos retouchées. Toutes les photos sélectionnées sont livrées, sans restriction.',
      },
      {
        question: 'Peut-on avoir les photos brutes (RAW) ?',
        answer:
          'Je livre uniquement les fichiers retouchés en JPEG haute résolution. Les RAW ne font pas partie de la prestation.',
      },
    ],
  },

  {
    id: '17',
    name: 'Photobooth Vintage',
    slug: 'photobooth-vintage',
    baseline: 'Des souvenirs instantanés, tirés sur place en 10 secondes.',
    categoryId: '4',
    category: 'Photo',
    subcats: ['43'],
    image: '/images/prestataires/photobooth-vintage.jpg',
    heroImage: '/images/prestataires/photobooth-vintage.jpg',
    photos: [
      'https://picsum.photos/seed/photobooth1/800/600',
      'https://picsum.photos/seed/photobooth2/800/600',
      'https://picsum.photos/seed/photobooth3/800/600',
    ],
    shortDescription: 'Un photobooth original pour des photos funs.',
    youtubeId: 'ybJPbM_ExTk',
    badges: [
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
    ],
    offerings: [
      'Borne photobooth vintage avec tirage instantané',
      'Fond personnalisable (couleur, motif, décor)',
      'Accessoires et props inclus',
      'Galerie digitale partagée après événement',
      'Personnalisation du cadre photo (nom, date, logo)',
    ],
    identity: {
      quote: 'Un classique qui ne se démode jamais.',
      bio: 'On a chiné cette borne dans une vente aux enchères en 2016 et on en est tombés amoureux. Depuis, elle voyage de fête en fête et ne manque jamais de faire sourire.',
    },
    budget: 'Forfait soirée (4h) à partir de 450€. Heures supplémentaires en option.',
    unavailableDates: ['2026-04-11', '2026-05-02', '2026-06-13', '2026-07-04', '2026-08-01'],
    testimonials: [
      {
        author: 'Nina & Pierre',
        text: "Le photobooth a été le hit de la soirée. File d'attente non-stop et des souvenirs géniaux pour tout le monde.",
        eventType: 'Mariage',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace",
      'Installation 45 min avant le début',
      'Espace requis : 3m² + zone de prise de vue',
      'Alimentation 220V nécessaire',
    ],
    technical: [
      'Borne photobooth vintage rétro-éclairée',
      'Imprimante thermique (tirage en 10s)',
      'Fond interchangeable fourni',
    ],
    faq: [
      {
        question: 'Peut-on personnaliser le cadre avec notre prénom et la date ?',
        answer:
          "Oui, le cadre est entièrement personnalisable. On vous demande les éléments 1 semaine avant l'événement.",
      },
    ],
  },

  {
    id: '18',
    name: 'Studio Photo Mobile',
    slug: 'studio-photo-mobile',
    baseline: 'La qualité studio, dans votre salle de réception.',
    categoryId: '4',
    category: 'Photo',
    subcats: ['41'],
    image: '/images/prestataires/studio-photo-mobile.jpg',
    heroImage: '/images/prestataires/studio-photo-mobile.jpg',
    photos: [
      'https://picsum.photos/seed/studio1/800/600',
      'https://picsum.photos/seed/studio2/800/600',
      'https://picsum.photos/seed/studio3/800/600',
    ],
    shortDescription: 'Un studio photo pour des portraits professionnels.',
    youtubeId: 'VoHDwCCxWA4',
    badges: [
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
      { icon: 'Eco', label: 'Éco', description: 'Éco-responsable', color: '#22C55E' },
    ],
    offerings: [
      'Studio photo mobile monté sur place',
      'Portraits individuels ou en groupe',
      'Fond blanc, noir ou coloré au choix',
      'Retouche légère incluse',
      'Idéal pour séminaires, galas, événements corporate',
    ],
    identity: {
      quote: 'Des photos dont les gens sont fiers.',
      bio: 'On apporte la lumière et le cadre — vous apportez vos sourires. En 5 minutes par personne, on fait des photos dont les gens sont fiers.',
    },
    budget: "À partir de 600€ pour une session de 3h (jusqu'à 60 portraits). Devis sur demande.",
    unavailableDates: ['2026-04-18', '2026-05-23', '2026-06-06', '2026-07-11', '2026-08-15'],
    testimonials: [
      {
        author: 'DRH Groupe Alsace Industries',
        text: 'Parfait pour nos portraits collaborateurs. Rapide, pro, et tout le monde était content du résultat.',
        eventType: 'Séminaire corporate',
      },
    ],
    logistics: [
      "Zone d'intervention : Alsace et Grand Est",
      'Installation 1h avant',
      'Espace requis : 6m² minimum',
      'Alimentation 220V nécessaire (2 prises)',
    ],
    technical: [
      'Flash studio Godox (×2)',
      'Fond papier 2,7m de large',
      'Réflecteurs et boîtes à lumière inclus',
    ],
    faq: [
      {
        question: 'Combien de personnes peuvent être photographiées sur une session ?',
        answer:
          'Environ 20 personnes par heure pour des portraits individuels. Pour les groupes, comptez plus de temps.',
      },
    ],
  },

  // ─── SERVICES / LIEUX ─────────────────────────────────────────────────────

  {
    id: '11',
    name: 'Salle des Fêtes Rathsamhausen',
    slug: 'salle-fetes-rathsamhausen',
    baseline: 'Un espace chaleureux au cœur du vignoble alsacien.',
    categoryId: '5',
    category: 'Services',
    subcats: ['52'],
    image: '/images/prestataires/salle-fetes.png',
    heroImage: '/images/prestataires/salle-fetes.png',
    photos: [
      'https://picsum.photos/seed/venue1/800/600',
      'https://picsum.photos/seed/venue2/800/600',
      'https://picsum.photos/seed/venue3/800/600',
    ],
    shortDescription: 'Une salle spacieuse pour vos événements.',
    youtubeId: 'SdAQjcIMjpo',
    badges: [
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
    ],
    offerings: [
      'Location de salle pour mariages, anniversaires, séminaires',
      'Capacité : 30 à 200 personnes selon configuration',
      'Cuisine équipée disponible',
      'Terrasse et jardin privatifs',
      'Tables, chaises et nappage inclus',
      'Hébergement possible sur place (chambres en option)',
    ],
    identity: {
      quote: 'Chaque pierre a une histoire.',
      bio: "Cette salle, on l'a rénovée avec nos mains pendant trois ans. Chaque pierre a une histoire. On la loue à des gens qui savent prendre soin des lieux.",
    },
    budget: 'Location à partir de 800€ le week-end. Tarif journée disponible.',
    unavailableDates: [
      '2026-04-04',
      '2026-04-11',
      '2026-04-18',
      '2026-04-25',
      '2026-05-09',
      '2026-05-16',
      '2026-05-23',
      '2026-06-06',
      '2026-06-13',
      '2026-07-04',
      '2026-07-11',
    ],
    testimonials: [
      {
        author: 'Famille Brunner',
        text: "Un cadre magnifique, un accueil irréprochable. Nos invités ont été sous le charme dès l'arrivée.",
        eventType: 'Mariage',
      },
    ],
    logistics: [
      'Accès par la D35, parking 80 véhicules',
      'Livraisons acceptées la veille de 9h à 18h',
      'Remise des clés la veille au soir',
      'Ménage de fin de location inclus en option',
    ],
    technical: [
      'Cuisine équipée (four, plaques, réfrigération)',
      'Sono de base disponible (enceintes, micro)',
      'Vidéoprojecteur et écran disponibles',
      'Wifi haut débit',
    ],
    faq: [
      {
        question: 'Peut-on apporter son propre traiteur ?',
        answer:
          'Oui, la salle est libre de traiteur. Vous pouvez venir avec le prestataire de votre choix.',
      },
      {
        question: "Jusqu'à quelle heure peut-on faire de la musique ?",
        answer:
          "Musique amplifiée autorisée jusqu'à 2h du matin. Au-delà, musique d'ambiance uniquement.",
      },
    ],
  },

  {
    id: '12',
    name: 'Château pour Mariage',
    slug: 'chateau-mariage',
    baseline: 'Votre grand jour dans un écrin de pierre et de vigne.',
    categoryId: '5',
    category: 'Services',
    subcats: ['52'],
    image: '/images/prestataires/chateau-mariage.jpg',
    heroImage: '/images/prestataires/chateau-mariage.jpg',
    photos: [
      'https://picsum.photos/seed/chateau1/800/600',
      'https://picsum.photos/seed/chateau2/800/600',
      'https://picsum.photos/seed/chateau3/800/600',
    ],
    shortDescription: 'Un cadre enchanteur pour célébrer votre union.',
    youtubeId: '615biPQMdJQ',
    badges: [
      { icon: 'Clock', label: '48h', description: 'Réponse sous 48h', color: '#FACC15' },
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
      { icon: 'Tune', label: 'Adaptable', description: 'Prestation adaptable', color: '#FACC15' },
    ],
    offerings: [
      'Location exclusive du château et du parc',
      "Salle de réception (jusqu'à 250 personnes)",
      'Cérémonie laïque dans le parc ou dans la chapelle',
      '12 chambres pour hébergement des invités',
      'Cuisine professionnelle disponible',
      'Parking sécurisé pour 150 véhicules',
    ],
    identity: {
      quote: 'Un lieu chargé d’histoire, prêt à accueillir la vôtre.',
      bio: 'Ce château est dans notre famille depuis cinq générations. On le partage avec ceux qui veulent y créer leurs propres souvenirs.',
    },
    budget: 'Location week-end à partir de 4 500€. Devis sur demande selon saison et format.',
    unavailableDates: [
      '2026-04-25',
      '2026-05-02',
      '2026-05-09',
      '2026-05-16',
      '2026-05-23',
      '2026-05-30',
      '2026-06-06',
      '2026-06-13',
      '2026-06-20',
      '2026-06-27',
      '2026-07-04',
      '2026-07-11',
      '2026-07-18',
      '2026-07-25',
      '2026-08-01',
      '2026-08-08',
      '2026-08-15',
      '2026-08-22',
      '2026-08-29',
    ],
    testimonials: [
      {
        author: 'Laura & Mathieu',
        text: "Un lieu de rêve. Nos invités n'en revenaient pas. On a l'impression d'avoir vécu un conte de fées.",
        eventType: 'Mariage',
      },
      {
        author: 'Directrice IESEG Strasbourg',
        text: 'Cadre exceptionnel pour notre séminaire de direction. Prestations et accueil au niveau.',
        eventType: 'Séminaire',
      },
    ],
    logistics: [
      'Accès autoroute A35, sortie Sélestat Nord',
      'Parking sécurisé 150 véhicules',
      'Hélipad disponible sur demande',
      'Accès PMR sur demande',
    ],
    technical: [
      'Cuisine professionnelle complète',
      'Système son et lumière scénique',
      'Wifi haut débit dans tout le château',
      'Générateur de secours',
    ],
    faq: [
      {
        question: 'La location est-elle exclusive ?',
        answer:
          "Oui, nous ne faisons qu'un événement à la fois. Le château et son parc vous sont entièrement réservés.",
      },
      {
        question: 'Peut-on organiser la cérémonie civile sur place ?',
        answer:
          'La cérémonie civile se tient en mairie. En revanche, une cérémonie laïque ou religieuse peut être organisée dans la chapelle ou le parc.',
      },
    ],
  },

  {
    id: '13',
    name: 'Gîte de la mangouste',
    slug: 'gite-de-la-mangouste',
    baseline: 'Dormir sur place, réveillon ou lendemain de fête serein.',
    categoryId: '5',
    category: 'Services',
    subcats: ['52'],
    image: '/images/prestataires/hebergement-groupe.jpg',
    heroImage: '/images/prestataires/hebergement-groupe.jpg',
    photos: [
      'https://picsum.photos/seed/hebergement1/800/600',
      'https://picsum.photos/seed/hebergement2/800/600',
      'https://picsum.photos/seed/hebergement3/800/600',
    ],
    shortDescription: 'Un hébergement confortable pour vos invités.',
    youtubeId: '_2tBuidbF88',
    badges: [
      {
        icon: 'Handshake',
        label: 'Accompagné',
        description: 'Accompagnement personnalisé',
        color: '#FACC15',
      },
      { icon: 'Inventory_2', label: 'Équipé', description: 'Autonome et équipé', color: '#FACC15' },
      {
        icon: 'Person_Check',
        label: 'Interlocuteur Unique',
        description: 'Interlocuteur unique',
        color: '#FACC15',
      },
    ],
    offerings: [
      'Location du gîte complet (16 personnes)',
      'Location à la chambre possible hors saison',
      'Draps et serviettes fournis',
      'Petit-déjeuner en option',
      'Idéal pour week-ends de fête, enterrements de vie, séjours groupes',
    ],
    identity: {
      quote: "C'est chez vous !",
      bio: "On a aménagé ce gîte pour que les gens s'y sentent comme chez eux — parce que pendant leur séjour, c'est chez eux.",
    },
    budget: 'Location complète à partir de 350€ la nuit. Week-end (2 nuits) à partir de 600€.',
    unavailableDates: [
      '2026-04-04',
      '2026-04-05',
      '2026-04-11',
      '2026-04-12',
      '2026-05-09',
      '2026-05-10',
      '2026-06-20',
      '2026-06-21',
      '2026-06-27',
      '2026-06-28',
      '2026-07-04',
      '2026-07-05',
      '2026-07-11',
      '2026-07-12',
      '2026-07-18',
      '2026-07-19',
      '2026-07-25',
      '2026-07-26',
      '2026-08-01',
      '2026-08-02',
      '2026-08-08',
      '2026-08-09',
    ],
    testimonials: [
      {
        author: 'Groupe EVG de Julien',
        text: 'Parfait pour notre week-end. Grand, bien équipé, calme. On reviendra.',
        eventType: 'Enterrement de vie de garçon',
      },
    ],
    logistics: [
      'Accès : village de Bernardswiller, 20 min de Strasbourg',
      'Parking privé pour 8 véhicules',
      'Check-in à partir de 16h, check-out avant 11h',
      'Animaux acceptés sur demande',
    ],
    technical: [
      '4 chambres doubles + 1 dortoir (8 lits)',
      'Cuisine équipée (four, lave-vaisselle, réfrigérateur)',
      'Salon TV, cheminée',
      'Wifi inclus',
    ],
    faq: [
      {
        question: 'Peut-on organiser une fête dans le gîte ?',
        answer:
          "Musique modérée acceptée jusqu'à minuit. Pour les fêtes avec sono, merci de nous contacter au préalable.",
      },
      {
        question: 'Le linge de lit est-il fourni ?',
        answer: 'Oui, draps, taies et serviettes sont inclus et changés entre chaque séjour.',
      },
    ],
  },
]
