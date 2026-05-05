/**
 * Composable — expose la liste d'événements réactive aux vues
 */
import { getEvenements } from './service/evenementService'
import type { EventSummary } from './domain/EventSummary'

export function useEvenements() {
  const events = ref<EventSummary[]>([])
  const loading = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      events.value = await getEvenements()
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  })

  return { events, loading, error }
}
