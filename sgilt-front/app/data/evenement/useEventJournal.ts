/**
 * Composable — journal des modifications d'un événement, chargé page par page
 */
import { fetchEventJournal } from './service/evenementService'
import type { JournalEntry } from './domain/JournalEntry'

export function useEventJournal(eventId: string) {
  const entries = ref<JournalEntry[]>([])
  const hasMore = ref(false)
  const loading = ref(false)
  let page = 0

  // Page suivante, ajoutée aux entrées déjà chargées.
  async function loadNextPage(): Promise<void> {
    loading.value = true
    try {
      const result = await fetchEventJournal(eventId, page)
      entries.value = [...entries.value, ...result.entries]
      hasMore.value = !result.last
      page++
    } finally {
      loading.value = false
    }
  }

  // Repart de la première page (à chaque ouverture du journal).
  async function loadFirstPage(): Promise<void> {
    entries.value = []
    hasMore.value = false
    page = 0
    await loadNextPage()
  }

  return { entries, hasMore, loading, loadFirstPage, loadNextPage }
}