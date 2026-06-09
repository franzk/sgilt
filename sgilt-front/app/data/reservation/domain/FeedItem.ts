/**
 * Domaine — élément du fil de discussion d'une réservation (note ou document).
 * Le champ type discrimine le variant, les champs optionnels varient selon le type.
 */
import type { NoteAuthor } from './NoteAuthor'

export interface FeedItem {
  type: 'note' | 'document'
  id: string
  title: string
  author: NoteAuthor
  createdAt: Date

  // note
  content?: string | null
  isPersonal?: boolean | null
  isMessageInitial?: boolean | null

  // document
  name?: string | null
  fileType?: string | null
  url?: string | null

  // note système
  generatedKey?: string | null
}
