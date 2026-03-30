export type Flow = 'new-event' | 'add-prestataire' | null

export function useFlow() {
  const currentFlow = useState<Flow>('flow:current', () => null)
  const flowPayload = useState<any>('flow:payload', () => null)

  const flows: Record<
    NonNullable<Flow>,
    { start: () => void; abort: () => void; success: (() => void) | null }
  > = {
    'new-event': {
      start: () => {
        navigateTo('/')
      },
      abort: () => {
        useSearchUi().dateModel.value = undefined
        navigateTo('/app/reservations')
      },
      success: () => {
        // nettoyage post-succès si besoin
      },
    },

    'add-prestataire': {
      start: () => {
        const date = flowPayload.value?.date
        useSearchUi().dateModel.value = date ? new Date(date) : undefined
      },
      abort: () => {
        const eventId = flowPayload.value?.id
        useSearchUi().dateModel.value = undefined
        navigateTo(`/app/events/${eventId}`)
      },
      success: () => {
        const eventId = flowPayload.value?.id
        useSearchUi().dateModel.value = undefined
        navigateTo(`/app/events/${eventId}`)
      },
    },
  }

  function start(flow: NonNullable<Flow>, contextLabel: string, payload?: any) {
    currentFlow.value = flow
    flowPayload.value = payload ?? null
    useContextBanner().show(contextLabel, abort)
    flows[flow].start()
  }

  function abort() {
    flows[currentFlow.value!].abort()
    currentFlow.value = null
    flowPayload.value = null
    useContextBanner().hide()
  }

  function onFlowSuccess() {
    flows[currentFlow.value!].success?.()
    currentFlow.value = null
    flowPayload.value = null
    useContextBanner().hide()
  }

  return { currentFlow, flowPayload, start, abort, onFlowSuccess }
}
