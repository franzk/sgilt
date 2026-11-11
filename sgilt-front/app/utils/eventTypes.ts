/**
 * Types d'événement côté front : la liste unique des types (EVENT_TYPES) et tout ce qui en dépend à
 * l'affichage (groupe de /fete, emoji, icône, illustrations, couvertures). Ajouter un type, c'est
 * ajouter une entrée ici, plus ses textes dans i18n (libellé, accroche, titre par défaut, accroche
 * du board…) et son template côté back. Les choix de type (composables/useEventTypeLabel.ts) et
 * BANK_IMAGE_PATHS (eventCovers.ts) en sont dérivés.
 */

import { markRaw, type Component } from 'vue'
import {
  HeartsIcon,
  CakeIcon,
  Goblet2Icon,
  BriefcaseIcon,
  TentIcon,
  SparklingIcon,
  UserHeartIcon,
  Building4Icon,
  TeamIcon,
} from '@remixicons/vue/line'

/**
 * Groupes de types d'événement : onglets (mobile) ou sections (desktop) de l'écran « Qu'est-ce
 * qu'on fête ? » (fete.vue). Libellés dans i18n (event-picker.groups.*).
 */
export interface EventTypeGroup {
  key: string
  icon: Component
}

export const EVENT_TYPE_GROUPS: EventTypeGroup[] = [
  { key: 'proches', icon: markRaw(UserHeartIcon) },
  { key: 'structure', icon: markRaw(Building4Icon) },
]

/**
 * Un type d'événement. L'ordre de EVENT_TYPES est l'ordre d'affichage partout (tuiles de /fete,
 * listes de choix, choix de la couverture).
 *
 * - `key` : la valeur stockée sur l'événement (event_type), clé des textes i18n (dont le libellé,
 *   event-picker.types.*.label) et du template.
 * - `group` : groupe de /fete, null pour « autre » (proposé par un lien sous les tuiles).
 * - `illustration` : image de la tuile de /fete, reprise sur /commencer (dossier public).
 * - `commencerCover` : photo de /commencer (banque d'images, servie via useImageUrl).
 * - `defaultCover` : couverture par défaut de l'événement (banque d'images), aussi proposée dans
 *   le choix de la couverture.
 */
export interface EventTypeDefinition {
  key: string
  emoji: string
  group: string | null
  icon: Component
  illustration: string
  commencerCover: string
  defaultCover: string
}

export const EVENT_TYPES: EventTypeDefinition[] = [
  {
    key: 'mariage',
    emoji: '💍',
    group: 'proches',
    icon: markRaw(HeartsIcon),
    illustration: '/images/sgilt-mariage.png',
    commencerCover: 'bank/mariage-commencer.jpg',
    defaultCover: 'bank/mariage.jpg',
  },
  {
    key: 'anniversaire',
    emoji: '🎂',
    group: 'proches',
    icon: markRaw(CakeIcon),
    illustration: '/images/sgilt-anniversaire.png',
    commencerCover: 'bank/anniversaire.webp',
    defaultCover: 'bank/anniversaire.webp',
  },
  {
    key: 'soiree_privee',
    emoji: '🥂',
    group: 'proches',
    icon: markRaw(Goblet2Icon),
    illustration: '/images/sgilt-soiree-privee.png',
    commencerCover: 'bank/soiree-privee.jpg',
    defaultCover: 'bank/soiree-privee.jpg',
  },
  {
    key: 'fete_entreprise',
    emoji: '🏢',
    group: 'structure',
    icon: markRaw(BriefcaseIcon),
    illustration: '/images/sgilt-soiree-entreprise.png',
    commencerCover: 'bank/fete-entreprise.webp',
    defaultCover: 'bank/fete-entreprise.webp',
  },
  {
    key: 'seminaire',
    emoji: '💼',
    group: 'structure',
    icon: markRaw(TeamIcon),
    illustration: '/images/sgilt-seminaire.png',
    commencerCover: 'bank/seminaire.png',
    defaultCover: 'bank/seminaire.png',
  },
  {
    key: 'evenement_public',
    emoji: '🎪',
    group: 'structure',
    icon: markRaw(TentIcon),
    illustration: '/images/sgilt-evenement-public.png',
    commencerCover: 'bank/evenement-public.webp',
    defaultCover: 'bank/evenement-public.webp',
  },
  {
    key: 'autre',
    emoji: '•••',
    group: null,
    icon: markRaw(SparklingIcon),
    illustration: '/images/sgilt-autre.png',
    commencerCover: 'bank/autre.webp',
    defaultCover: 'bank/autre.webp',
  },
]

// Repli pour un type inconnu ou non choisi : « Autre », toujours présent dans EVENT_TYPES.
const AUTRE_TYPE: EventTypeDefinition = EVENT_TYPES.find((type) => type.key === 'autre')!

/**
 * Définition d'un type d'événement par sa clé. Type inconnu ou non choisi : celle de « Autre ».
 */
export function eventTypeDefinition(key: string | null | undefined): EventTypeDefinition {
  return EVENT_TYPES.find((type) => type.key === key) ?? AUTRE_TYPE
}

/** Types d'un groupe de /fete, dans l'ordre d'affichage. */
export function eventTypesOfGroup(groupKey: string): EventTypeDefinition[] {
  return EVENT_TYPES.filter((type) => type.group === groupKey)
}

/**
 * Icône d'un type d'événement (bandeau de l'événement, carte « Type » des informations). Type
 * inconnu ou non choisi : l'icône de « Autre ».
 */
export function eventTypeIcon(key: string | null | undefined): Component {
  return eventTypeDefinition(key).icon
}
