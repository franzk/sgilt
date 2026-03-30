import type { EventContext } from '~/types/search'

export function useAddPrestataireContext() {
  const eventContext = useState<EventContext | null>('addPrestataireEventContext', () => null)

  function setEventContext(ctx: EventContext) {
    eventContext.value = ctx
    useContextBanner().show(ctx.nom, abort)
  }

  function abort() {
    const eventId = eventContext.value?.id
    eventContext.value = null
    useSearchUi().dateModel.value = undefined
    useContextBanner().hide()
    if (eventId) navigateTo(`/app/events/${eventId}`)
  }

  return { eventContext, setEventContext, abort }
}
