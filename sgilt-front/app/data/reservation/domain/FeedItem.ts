/**
 * Domaine — éléments du fil de discussion d'une réservation (union Note | Document)
 */
import type { ReservationNote } from './ReservationNote'
import type { ReservationDocument } from './ReservationDocument'

export type FeedNote = ReservationNote & { _kind: 'note' }
export type FeedDocument = ReservationDocument & { _kind: 'document' }
export type FeedItem = FeedNote | FeedDocument
