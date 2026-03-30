import type { EventContext } from '~/types/search'

export function useAddPrestataireContext() {
  const eventContext = useState<EventContext | null>('addPrestataireEventContext', () => null)

  function setEventContext(ctx: EventContext) {
    eventContext.value = ctx
  }

  function abort() {
    const eventId = eventContext.value?.id
    eventContext.value = null
    useSearchUi().dateModel.value = undefined
    if (eventId) navigateTo(`/app/events/${eventId}`)
  }

  return { eventContext, setEventContext, abort }
}
