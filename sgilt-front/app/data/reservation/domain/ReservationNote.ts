/**
 * Domaine — note échangée dans le fil d'une réservation
 */
import type { NoteAuthor } from './NoteAuthor'

export interface ReservationNote {
  id: string
  author: NoteAuthor
  titre: string
  content: string
  createdAt: Date
  isPersonal?: boolean
  isMessageInitial?: boolean
}
