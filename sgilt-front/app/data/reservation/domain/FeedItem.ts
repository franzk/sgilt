/**
 * Domaine — élément du fil de discussion d'une réservation (note ou envoi de documents).
 * Le champ type discrimine le variant, les champs optionnels varient selon le type.
 * Un élément `document` est un envoi : les fichiers déposés ensemble, dans une seule carte.
 */
import type { FeedFile } from './FeedFile'
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

  // document : les fichiers de l'envoi
  files?: FeedFile[] | null

  // note système
  generatedKey?: string | null
}
