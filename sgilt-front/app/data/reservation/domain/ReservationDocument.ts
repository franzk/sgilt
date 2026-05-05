/**
 * Domaine — document joint à une réservation
 */
import type { NoteAuthor } from './NoteAuthor'

export interface ReservationDocument {
  id: string
  name: string
  fileType: 'pdf' | 'image' | 'other'
  url: string
  uploadedBy: NoteAuthor
  uploadedAt: Date
}
