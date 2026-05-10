/**
 * Domaine — champs modifiables d'un événement (requêtes PATCH)
 */
import type { EventDetail } from './EventDetail'

export type EventPatch = Partial<
  Pick<
    EventDetail,
    | 'title'
    | 'coverImage'
    | 'eventType'
    | 'ambiance'
    | 'ville'
    | 'lieu'
    | 'nbInvites'
    | 'sharedNote'
    | 'description'
    | 'momentCle'
  >
>
