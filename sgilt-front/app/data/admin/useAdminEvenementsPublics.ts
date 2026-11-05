/**
 * Composable — face publique des événements dans le back-office admin (/admin/evenements-publics) :
 * la liste des événements et leurs interrupteurs (page publique, billetterie), enregistrés à
 * chaque clic. Un échec est exposé dans `failure` (chargement de la liste) ou `featuresFailure`
 * (interrupteurs, avec l'événement concerné).
 */
import { fetchAdminEvenements, saveAdminEvenementFeatures, toFeaturesFailure } from './service/adminService'
import type { AdminEvenementFeatures, AdminEvenementListItem } from './domain/AdminEvenement'

// Échec d'un changement d'interrupteurs : l'événement, et le motif (suffixe de la clé i18n
// admin.evenements-publics.features.errors.*).
export interface FeaturesFailure {
  id: string
  reason: string
}

// État propre à chaque appel (un seul écran l'utilise).
export function useAdminEvenementsPublics() {
  const evenements = ref<AdminEvenementListItem[]>([])
  const loading = ref(false)
  // Motif de l'échec du chargement (suffixe de la clé i18n admin.evenements-publics.errors.*), ou null.
  const failure = ref<string | null>(null)
  // Événement dont les interrupteurs sont en cours d'enregistrement, ou null.
  const savingFeaturesId = ref<string | null>(null)
  const featuresFailure = ref<FeaturesFailure | null>(null)

  async function loadEvenements() {
    loading.value = true
    failure.value = null
    try {
      evenements.value = await fetchAdminEvenements()
    } catch {
      failure.value = 'TECHNICAL'
    } finally {
      loading.value = false
    }
  }

  // Enregistre les interrupteurs d'un événement ; en cas d'échec, la liste garde son état.
  async function saveFeatures(id: string, features: AdminEvenementFeatures) {
    savingFeaturesId.value = id
    featuresFailure.value = null
    try {
      const updated = await saveAdminEvenementFeatures(id, features)
      evenements.value = evenements.value.map((evenement) => (evenement.id === id ? updated : evenement))
    } catch (e) {
      featuresFailure.value = { id, reason: toFeaturesFailure(e) }
    } finally {
      savingFeaturesId.value = null
    }
  }

  // Refus détecté par l'écran, sans appel au back.
  function failFeatures(id: string, reason: string) {
    featuresFailure.value = { id, reason }
  }

  return {
    evenements,
    loading,
    failure,
    savingFeaturesId,
    featuresFailure,
    loadEvenements,
    saveFeatures,
    failFeatures,
  }
}
