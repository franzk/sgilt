/**
 * Composable — événement courant de l'espace connecté, chargé par la route parente
 * /app/events/:eventId et partagé avec ses pages (board, rubrique, prestataires, paramètres,
 * réservations) : un seul chargement, et la sidebar desktop reste affichée d'une page à l'autre.
 */
import type { InjectionKey } from 'vue'
import { useEvent } from './useEvenement'

type EventContext = ReturnType<typeof useEvent> & { eventId: string }

const EVENT_CONTEXT_KEY: InjectionKey<EventContext> = Symbol('event-context')

// Appelé une fois, par la route parente.
export function provideEventContext(eventId: string): EventContext {
  const context = { eventId, ...useEvent(eventId) }
  provide(EVENT_CONTEXT_KEY, context) // partage le context entre les composants de l'event board
  return context
}

export function useEventContext(): EventContext {
  const context = inject(EVENT_CONTEXT_KEY)
  if (!context) throw new Error('useEventContext : page hors de /app/events/:eventId')
  return context
}
