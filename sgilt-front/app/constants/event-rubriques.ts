import { markRaw, type Component } from 'vue'
import {
  Building2Icon,
  FlowerIcon,
  HotelBedIcon,
  Music2Icon,
  RestaurantIcon,
} from '@remixicons/vue/line'

// ── Rubriques d'un événement ──────────────────────────────────────────────────
// Preset mocké en dur côté front (pas de backend à ce stade). Ordre = ordre d'affichage.

export const RUBRIQUE_KEYS = [
  'lieu',
  'restauration',
  'musique-animation',
  'decoration',
  'hebergement',
] as const

export type RubriqueKey = (typeof RUBRIQUE_KEYS)[number]

// ── Réservations d'une rubrique ───────────────────────────────────────────────
// Demande envoyée à un prestataire avant vérification de l'email : elle n'existe pas encore
// en base, ce statut est propre à l'événement local (ce n'est pas un ReservationStatus).
export const RUBRIQUE_RESERVATION_STATUSES = ['en_attente_verification'] as const

export type RubriqueReservationStatus = (typeof RUBRIQUE_RESERVATION_STATUSES)[number]

export interface RubriqueReservation {
  prestataireSlug: string
  prestataireName: string
  prestataireImage: string
  status: RubriqueReservationStatus
  // Date d'envoi, au format 'YYYY-MM-DD'.
  sentAt: string
}

export interface EventRubrique {
  key: RubriqueKey
  reservations: RubriqueReservation[]
}

export const MARIAGE_RUBRIQUES: EventRubrique[] = RUBRIQUE_KEYS.map((key) => ({
  key,
  reservations: [],
}))

// ── Présentation ──────────────────────────────────────────────────────────────

export const RUBRIQUE_ICONS: Record<RubriqueKey, Component> = {
  lieu: markRaw(Building2Icon),
  restauration: markRaw(RestaurantIcon),
  'musique-animation': markRaw(Music2Icon),
  decoration: markRaw(FlowerIcon),
  hebergement: markRaw(HotelBedIcon),
}

// Une couleur d'accent par rubrique — sert uniquement à les distinguer visuellement, pas des
// tokens de design partagés.
export const RUBRIQUE_ACCENTS: Record<RubriqueKey, string> = {
  lieu: '#a3334a',
  restauration: '#d68c00',
  'musique-animation': '#b0447e',
  decoration: '#5a8f6b',
  hebergement: '#2f6f73',
}

// Photos de couverture de la fiche rubrique. Partielle par construction, comme
// EVENT_TYPE_COVERS : une rubrique sans entrée retombe sur la couverture de l'événement,
// en attendant que les visuels soient fournis.
export const RUBRIQUE_COVERS: Partial<Record<RubriqueKey, string>> = {}
