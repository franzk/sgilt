export interface TicketingMockEvent {
  slug: string
  tag: string
  title: string
  subtitle: string
  // Clé R2 (sgilt-r2-mock/storage/bank/taennel.png) — comme un vrai media prestataire/événement,
  // pas un asset public Nuxt.
  heroImage: string
  dateLabel: string
  time: string
  venue: string
  city: string
  audienceLabel: string
  sectionTitle: string
  description: string
  unitPrice: number
  // Nombre de places restantes — viendra du back (disponibilité ET plafond du sélecteur de
  // quantité en découlent tous les deux, pas de champ `available` séparé à faire diverger).
  remainingTickets: number
  importantInfo: string[]
}

/**
 * Données de démonstration en dur pour ce brief — le vrai contenu (titre, date, lieu, tarif,
 * pictogrammes) viendra du modèle Outil/Billetterie, pas encore construit. Partagé entre la fiche
 * événement et la page de commande pour éviter que les deux écrans divergent.
 */
export const mockEvent: TicketingMockEvent = {
  slug: 'les-jeudis-du-taennel',
  tag: 'Édition du 12 mars',
  title: 'Les Jeudis du Taennel',
  subtitle: 'Concert & bar éphémère au cœur du vignoble',
  heroImage: 'bank/taennel.png',
  dateLabel: 'Jeudi 12 mars 2026',
  time: '19h30',
  venue: 'Le Taennel',
  city: 'Scherwiller (67)',
  audienceLabel: 'Tout public · Places debout',
  sectionTitle: 'Une soirée musicale et conviviale',
  description:
    "Le jeudi, le Taennel se transforme : concert acoustique, bar à vin nature et food-truck sur place. Une soirée conviviale pour démarrer le week-end en avance, en plein cœur du vignoble.",
  unitPrice: 12,
  remainingTickets: 9,
  importantInfo: [
    'Événement en plein air, places debout.',
    'Aucun billet physique. Présentez votre billet depuis votre smartphone à l’entrée.',
  ],
}
