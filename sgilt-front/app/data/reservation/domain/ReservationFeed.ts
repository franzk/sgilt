/**
 * Domaine — base commune à tous les éléments du feed d'une réservation
 */
import type { NoteAuthor } from './NoteAuthor'

export interface ReservationFeed {
  id: string
  title: string
  author: NoteAuthor
  createdAt: Date
}
