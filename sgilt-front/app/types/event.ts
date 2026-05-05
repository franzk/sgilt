/**
 * Re-exports de compatibilité — préférer les imports directs depuis data / * / domain /
 */
export type { ReservationStatus } from '~/data/reservation/domain/ReservationStatus'
export type { Reservation } from '~/data/reservation/domain/Reservation'
export type { NoteAuthor } from '~/data/reservation/domain/NoteAuthor'
export type { ReservationNote } from '~/data/reservation/domain/ReservationNote'
export type { ReservationDocument } from '~/data/reservation/domain/ReservationDocument'
export type { FeedNote, FeedDocument, FeedItem } from '~/data/reservation/domain/FeedItem'
export type { ReservationDetail } from '~/data/reservation/domain/ReservationDetail'
export type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
export type { ProDemandeDetail } from '~/data/reservation/domain/ProDemandeDetail'
export type { ProBoardCounts } from '~/data/reservation/domain/ProBoardCounts'
export type { ProDemandeSummary } from '~/data/reservation/domain/ProDemandeSummary'
export type { EventDetail } from '~/data/evenement/domain/EventDetail'
