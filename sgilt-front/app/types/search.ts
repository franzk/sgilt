export interface EventContext {
  id: string
  nom: string
  date: string | null
  ville?: string
  ambiance?: string
  invites?: string
}

export interface SearchContextState {
  date: string | null
  eventContext: EventContext | null
}

export interface SearchContextInjection {
  searchContext: Ref<SearchContextState>
  abort: () => void
}
