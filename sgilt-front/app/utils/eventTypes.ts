// app/utils/eventTypes.ts

import { markRaw, type Component } from 'vue'
import {
  HeartsIcon,
  CakeIcon,
  Goblet2Icon,
  BriefcaseIcon,
  TentIcon,
  SparklingIcon,
} from '@remixicons/vue/line'

export interface EventTypeMeta {
  key: string
  label: string
  icon: Component
}

/**
 * Catalogue des types d'événement (écran "Qu'est-ce qu'on fête ?").
 * `key` reprend les slugs déjà utilisés pour les vrais événements (voir BANK_IMAGE_PATHS
 * dans constants.ts) — pas les slugs de EVENT_TYPE_OPTIONS (types/demande.ts), propres au
 * tunnel de demande prestataire.
 *
 * Ordre calé sur la teinte de $event-* (colors.scss), pas sur l'ordre alphabétique/métier :
 * rouge profond → rose-rouge → orange → vert-sarcelle → vert-sarcelle plus sombre → neutre
 * (autre, désaturé, en dernier car hors progression).
 */
export const EVENT_TYPE_CATALOG: EventTypeMeta[] = [
  { key: 'mariage', label: 'Mariage', icon: markRaw(HeartsIcon) },
  { key: 'soiree_privee', label: 'Soirée privée', icon: markRaw(Goblet2Icon) },
  { key: 'anniversaire', label: 'Anniversaire', icon: markRaw(CakeIcon) },
  { key: 'fete_entreprise', label: "Fête d'entreprise", icon: markRaw(BriefcaseIcon) },
  { key: 'evenement_public', label: 'Événement public', icon: markRaw(TentIcon) },
  { key: 'autre', label: 'Autre', icon: markRaw(SparklingIcon) },
]

/**
 * Illustrations utilisées à l'écran "Qu'est-ce qu'on fête ?" (fete.vue) — mêmes
 * clés que EVENT_TYPE_CATALOG. Extrait ici pour être réutilisé tel quel par
 * l'écran de choix de démarche qui suit (continuité visuelle entre les deux
 * écrans, cf. brief).
 */
export const EVENT_TYPE_IMAGES: Record<string, string> = {
  mariage: '/images/sgilt-mariage.png',
  anniversaire: '/images/sgilt-anniversaire.png',
  soiree_privee: '/images/sgilt-soiree-privee.png',
  fete_entreprise: '/images/sgilt-soiree-entreprise.png',
  evenement_public: '/images/sgilt-evenement-public.png',
  autre: '/images/sgilt-autre.png',
}

/**
 * Photos de couverture par type d'événement, écran "commencer" (commencer.vue).
 * Partielle par construction : un type sans entrée ici n'affiche simplement
 * pas de photo, en attendant que les autres visuels soient fournis.
 */
export const EVENT_TYPE_COVERS: Record<string, string> = {
  mariage: '/images/commencer/mariage.jpg',
}
