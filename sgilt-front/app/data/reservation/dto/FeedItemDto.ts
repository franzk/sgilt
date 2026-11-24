/**
 * DTO — élément du fil d'une réservation tel que renvoyé par le back (note ou envoi de documents).
 * Transformé en FeedItem par reservationFeedService.
 */
import type { FeedFileDto } from './FeedFileDto'

export interface FeedItemDto {
  type: 'note' | 'document'
  id: string
  authorId: string
  authorName: string
  authorPhoto?: string | null
  authorRole: 'client' | 'prestataire'
  createdAt: string
  title?: string | null

  // note
  content?: string | null
  isPersonal?: boolean | null
  isMessageInitial?: boolean | null

  // document : les fichiers de l'envoi
  files?: FeedFileDto[] | null

  // TODO brique back : champs d'un document seul, renvoyés par le back actuel ; à retirer quand le
  // fil renverra `files`.
  name?: string | null
  fileType?: string | null
  url?: string | null

  // note système
  generatedKey?: string | null
}
