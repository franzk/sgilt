import { markRaw, type Component } from 'vue'
import {
  Building2Icon,
  FlowerIcon,
  FolderIcon,
  HotelBedIcon,
  Music2Icon,
  RestaurantIcon,
} from '@remixicons/vue/line'

// ── Présentation des rubriques ────────────────────────────────────────────────
// Les rubriques (clés et ordre) viennent du template du type d'événement, servi par le back. Ici,
// seulement les visuels des clés connues ; une rubrique ajoutée au template sans visuel dédié
// prend ceux par défaut.

const RUBRIQUE_ICONS: Partial<Record<string, Component>> = {
  lieu: markRaw(Building2Icon),
  restauration: markRaw(RestaurantIcon),
  'musique-animation': markRaw(Music2Icon),
  decoration: markRaw(FlowerIcon),
  hebergement: markRaw(HotelBedIcon),
}
const DEFAULT_RUBRIQUE_ICON = markRaw(FolderIcon)

// Une couleur d'accent par rubrique — sert uniquement à les distinguer visuellement, pas des
// tokens de design partagés.
const RUBRIQUE_ACCENTS: Partial<Record<string, string>> = {
  lieu: '#a3334a',
  restauration: '#d68c00',
  'musique-animation': '#b0447e',
  decoration: '#5a8f6b',
  hebergement: '#2f6f73',
}
const DEFAULT_RUBRIQUE_ACCENT = '#6b6b6b'

export function rubriqueIcon(key: string): Component {
  return RUBRIQUE_ICONS[key] ?? DEFAULT_RUBRIQUE_ICON
}

export function rubriqueAccent(key: string): string {
  return RUBRIQUE_ACCENTS[key] ?? DEFAULT_RUBRIQUE_ACCENT
}

// Photos de couverture de la fiche rubrique : chemins d'images de la banque (servies via
// useImageUrl, comme BANK_IMAGE_PATHS). Partielle par construction : une rubrique sans entrée
// n'a pas de couverture, en attendant que les visuels soient fournis.
export const RUBRIQUE_COVERS: Partial<Record<string, string>> = {}
