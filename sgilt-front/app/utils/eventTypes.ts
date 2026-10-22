/**
 * Types d'événement côté front, ce qui en dépend à l'affichage : icônes, illustrations de /fete et
 * photos de /commencer. Les textes par type (libellé, titre par défaut de l'événement, accroche du
 * board) sont dans i18n.
 */

import { markRaw, type Component } from 'vue'
import {
  HeartsIcon,
  CakeIcon,
  Goblet2Icon,
  BriefcaseIcon,
  TentIcon,
  SparklingIcon,
} from '@remixicons/vue/line'

const EVENT_TYPE_ICONS: Record<string, Component> = {
  mariage: markRaw(HeartsIcon),
  soiree_privee: markRaw(Goblet2Icon),
  anniversaire: markRaw(CakeIcon),
  fete_entreprise: markRaw(BriefcaseIcon),
  evenement_public: markRaw(TentIcon),
  autre: markRaw(SparklingIcon),
}

/**
 * Icône d'un type d'événement (bandeau de l'événement, carte « Type » des informations). `key`
 * reprend les slugs des vrais événements (voir BANK_IMAGE_PATHS dans constants.ts) — pas ceux de
 * EVENT_TYPE_OPTIONS (types/evenement.ts), propres au tunnel de demande prestataire. Type inconnu
 * ou non choisi : l'icône de « Autre ».
 */
export function eventTypeIcon(key: string | null | undefined): Component {
  return EVENT_TYPE_ICONS[key ?? ''] ?? EVENT_TYPE_ICONS.autre!
}

/**
 * Illustrations utilisées à l'écran "Qu'est-ce qu'on fête ?" (fete.vue) — mêmes
 * clés que eventTypeIcon. Extrait ici pour être réutilisé tel quel par
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
 * Photos de couverture par type d'événement, écran "commencer" (commencer.vue) : chemins
 * d'images de la banque (servies via useImageUrl, comme BANK_IMAGE_PATHS). Un type sans
 * entrée ici n'affiche simplement pas de photo.
 */
export const EVENT_TYPE_COVERS: Record<string, string> = {
  mariage: 'bank/mariage-commencer.jpg',
  anniversaire: 'bank/anniversaire.webp',
  soiree_privee: 'bank/soiree-privee.jpg',
  fete_entreprise: 'bank/fete-entreprise.webp',
  evenement_public: 'bank/evenement-public.webp',
  autre: 'bank/autre.webp',
}
