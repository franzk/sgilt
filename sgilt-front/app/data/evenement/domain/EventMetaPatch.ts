/**
 * Domaine — champs modifiables d'un événement (requêtes PATCH)
 */
import type { EventMeta } from './EventMeta'

export type EventMetaPatch = Partial<
  Pick<
    EventMeta,
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
