import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type {
  NoteAuthor,
  ReservationMeta,
  ReservationDocument,
  ReservationNote,
  ProDemandeDetail,
} from '~/types/event'

// ── Auteurs réutilisables ──────────────────────────────────────────────────────

export const JULIE: NoteAuthor = {
  id: 'client-1',
  name: 'Julie M.',
  role: 'client',
  photo: 'https://picsum.photos/seed/julie-m/64/64',
}

export const SOPHIE: NoteAuthor = {
  id: 'client-2',
  name: 'Sophie L.',
  role: 'client',
  photo: 'https://picsum.photos/seed/sophie-l/64/64',
}

export const MARC_D: NoteAuthor = {
  id: 'client-3',
  name: 'Marc D.',
  role: 'client',
}

export const EMILIE_D: NoteAuthor = {
  id: 'client-4',
  name: 'Émilie D.',
  role: 'client',
}

export const THOMAS_B: NoteAuthor = {
  id: 'client-5',
  name: 'Thomas B.',
  role: 'client',
}

export const CLAIRE_R: NoteAuthor = {
  id: 'client-6',
  name: 'Claire R.',
  role: 'client',
}

export const AIDES: NoteAuthor = {
  id: 'client-7',
  name: 'Association AIDES',
  role: 'client',
}

export const DJ: NoteAuthor = {
  id: 'presta-3',
  name: 'DJ Animation',
  role: 'prestataire',
  photo: '/images/prestataires/dj-animation.jpg',
}

export const LEO: NoteAuthor = {
  id: 'presta-16',
  name: 'Léo Clairmont',
  role: 'prestataire',
  photo: '/images/prestataires/photographe-mariage.jpg',
}

export const ECLAT: NoteAuthor = {
  id: 'presta-6',
  name: 'Éclat Gourmet',
  role: 'prestataire',
  photo: '/images/prestataires/traiteur-gourmet.jpg',
}

export const GYPSY: NoteAuthor = {
  id: 'presta-1',
  name: 'Gypsy Reed Ensemble',
  role: 'prestataire',
  photo: '/images/prestataires/jazz-band.jpg',
}

export const STUDIO: NoteAuthor = {
  id: 'presta-18',
  name: 'Studio Photo Mobile',
  role: 'prestataire',
  photo: '/images/prestataires/studio-photo-mobile.jpg',
}

export const STARLIGHT: NoteAuthor = {
  id: 'presta-2',
  name: 'Starlight Pulse',
  role: 'prestataire',
  photo: '/images/prestataires/pop-rock-band.jpg',
}

// ── Réservations (côté client) ────────────────────────────────────────────────

export const MOCK_RESERVATIONS: ReservationMeta[] = []

// ── Demandes pro (réservation + événement associé) ────────────────────────────

export const PRO_DEMANDES: ProDemandeDetail[] = [
  // ── nouvelle — // urgencyLevel desc ──────────────────────────────────────────────
  /* {
    ...PRO_ONLY_RESERVATIONS[2],
    event: PRO_ONLY_EVENTS[2],
    progressType: 'deadline',
    progressValue: 0.15,
    phraseInfoState: 'Il reste <strong>4h</strong> pour répondre',
    clientInfo: {
      firstName: 'Émilie',
      phone: '+33 6 34 56 78 90',
      email: 'emilie.dubois@email.fr',
    },
  }, // nouvelle — // urgencyLevel 5
  {
    ...PRO_ONLY_RESERVATIONS[0],
    event: PRO_ONLY_EVENTS[0],
    progressType: 'deadline',
    progressValue: 0.85,
    phraseInfoState: 'Il reste <strong>36h</strong> pour répondre',
    clientInfo: {
      firstName: 'Sophie',
      phone: '+33 6 12 34 56 78',
      email: 'sophie.lambert@email.fr',
    },
  }, // nouvelle — // urgencyLevel 1
  // ── en_discussion ─────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[1],
    event: PRO_ONLY_EVENTS[1],
    progressType: 'duration',
    progressValue: 0.45,
    phraseInfoState: 'En négociation depuis <strong>5 jours</strong>',
    clientInfo: { firstName: 'Marc', phone: '+33 6 23 45 67 89', email: 'marc.dupont@email.fr' },
  }, // en_discussion — // urgencyLevel 3
  // ── confirmee — // urgencyLevel desc ─────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[3],
    event: PRO_ONLY_EVENTS[3],
    progressType: 'temporal',
    progressValue: 0.92,
    phraseInfoState: 'Événement dans <strong>6 jours</strong>',
    clientInfo: {
      firstName: 'Thomas',
      phone: '+33 6 45 67 89 01',
      email: 'thomas.bernot@email.fr',
    },
  }, // confirmee — // urgencyLevel 6
  {
    ...MOCK_RESERVATIONS[0],
    event: MOCK_EVENTS[0],
    progressType: 'temporal',
    progressValue: 0.35,
    phraseInfoState: 'Événement dans 178 jours',
    clientInfo: { firstName: 'Julie', phone: '+33 6 78 90 12 34', email: 'julie.martin@email.fr' },
  }, // confirmee — // urgencyLevel 2
  // ── refusee ───────────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[5],
    event: PRO_ONLY_EVENTS[5],
    progressType: null,
    progressValue: null,
    phraseInfoState: 'Refusée le <strong>21/06/2026</strong>',
    clientInfo: {
      firstName: 'Marie',
      phone: '+33 6 67 89 01 23',
      email: 'marie.contact@aides.org',
    },
  }, // refusee — // urgencyLevel 1
  // ── annulee ───────────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[4],
    event: PRO_ONLY_EVENTS[4],
    progressType: null,
    progressValue: null,
    phraseInfoState: 'Annulée par le client le <strong>08/03/2026</strong>',
    clientInfo: {
      firstName: 'Claire',
      phone: '+33 6 56 78 90 12',
      email: 'claire.rousseau@email.fr',
    },
  }, // annulee — // urgencyLevel 1
  // ── realisee ──────────────────────────────────────────────────────────────────
  {
    ...PRO_ONLY_RESERVATIONS[6],
    event: PRO_ONLY_EVENTS[6],
    progressType: null,
    progressValue: null,
    phraseInfoState: 'Réalisée le <strong>14/12/2025</strong>',
    clientInfo: { firstName: 'Julie', phone: '+33 6 78 90 12 34', email: 'julie.martin@email.fr' },
  }, // realisee — // urgencyLevel 1 */
]
