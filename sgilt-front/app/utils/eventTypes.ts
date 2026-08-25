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
