import type { EventSummary } from '~/types/event'

interface EvenementSummaryResponse {
  id: string
  name: string
  date?: string
  ville?: string
  coverUrl?: string | null
  eventType?: string
  confirmedCount: number
  inDiscussionCount: number
}

export function useEvenements() {
  const { data, pending, error } = useApiFetch<EvenementSummaryResponse[]>('/events')

  const events = computed<EventSummary[]>(() =>
    (data.value ?? []).map((r) => ({
      id: r.id,
      title: r.name,
      date: r.date ? new Date(r.date) : undefined,
      ville: r.ville,
      coverImage: r.coverUrl ?? null,
      eventType: r.eventType,
      confirmedCount: r.confirmedCount,
      inDiscussionCount: r.inDiscussionCount,
    })),
  )

  return { events, loading: pending, error }
}
