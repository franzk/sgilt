import { markRaw, type Component } from 'vue'
import {
  Building2Icon,
  FlowerIcon,
  FolderIcon,
  HotelBedIcon,
  Music2Icon,
  RestaurantIcon,
} from '@remixicons/vue/line'

// ── Rubriques d'un événement ──────────────────────────────────────────────────
// Les rubriques (clés et ordre) viennent du template du type d'événement, servi par le back.
// Clé explicite ('musique-animation'), qui sert aussi de clé i18n du libellé.
export type RubriqueKey = string

// ── Présentation ──────────────────────────────────────────────────────────────
// Visuels associés aux clés connues ; une rubrique ajoutée au template sans visuel dédié prend
// ceux par défaut.

const RUBRIQUE_ICONS: Partial<Record<RubriqueKey, Component>> = {
  lieu: markRaw(Building2Icon),
  restauration: markRaw(RestaurantIcon),
  'musique-animation': markRaw(Music2Icon),
  decoration: markRaw(FlowerIcon),
  hebergement: markRaw(HotelBedIcon),
}
const DEFAULT_RUBRIQUE_ICON = markRaw(FolderIcon)

// Une couleur d'accent par rubrique — sert uniquement à les distinguer visuellement, pas des
// tokens de design partagés.
const RUBRIQUE_ACCENTS: Partial<Record<RubriqueKey, string>> = {
  lieu: '#a3334a',
  restauration: '#d68c00',
  'musique-animation': '#b0447e',
  decoration: '#5a8f6b',
  hebergement: '#2f6f73',
}
const DEFAULT_RUBRIQUE_ACCENT = '#6b6b6b'

export function rubriqueIcon(key: RubriqueKey): Component {
  return RUBRIQUE_ICONS[key] ?? DEFAULT_RUBRIQUE_ICON
}

export function rubriqueAccent(key: RubriqueKey): string {
  return RUBRIQUE_ACCENTS[key] ?? DEFAULT_RUBRIQUE_ACCENT
}

// Photos de couverture de la fiche rubrique : chemins d'images de la banque (servies via
// useImageUrl, comme BANK_IMAGE_PATHS). Partielle par construction : une rubrique sans entrée
// retombe sur la couverture de l'événement, en attendant que les visuels soient fournis.
export const RUBRIQUE_COVERS: Partial<Record<RubriqueKey, string>> = {}
