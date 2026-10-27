/**
 * Composable — saisie de la face publique d'un événement dans le back-office admin
 * (/admin/evenements-publics) : la liste des événements, puis, pour celui choisi, ses horaires, sa
 * page /e/[slug] et sa billetterie, lus et enregistrés d'un bloc. Un échec est exposé dans
 * `failure`.
 */
import {
  fetchAdminEvenements,
  fetchAdminEventPublicForm,
  saveAdminEventPublicForm,
  toSousCategorieFailure,
} from './service/adminService'
import type { AdminEvenementListItem, AdminEventPublicForm } from './domain/AdminEvenement'

// État propre à chaque appel (un seul écran l'utilise) : revenir sur l'écran repart d'un choix
// vide, sans formulaire d'une visite précédente.
export function useAdminEventPublicForm() {
  const evenements = ref<AdminEvenementListItem[]>([])
  // Formulaire de l'événement choisi, null tant qu'aucun n'est chargé.
  const form = ref<AdminEventPublicForm | null>(null)
  const loading = ref(false)
  const saving = ref(false)
  // Vrai après un enregistrement réussi, jusqu'au prochain enregistrement, échec ou choix.
  const saved = ref(false)
  // Motif du dernier échec (suffixe de la clé i18n admin.evenements-publics.errors.*), ou null.
  const failure = ref<string | null>(null)

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

  async function select(id: string) {
    form.value = null
    saved.value = false
    failure.value = null
    loading.value = true
    try {
      form.value = await fetchAdminEventPublicForm(id)
    } catch {
      failure.value = 'TECHNICAL'
    } finally {
      loading.value = false
    }
  }

  // Le seul refus métier qu'un formulaire valide puisse recevoir (400) est un slug déjà pris :
  // la billetterie sans page est empêchée par l'écran.
  async function save(id: string): Promise<boolean> {
    if (!form.value) return false
    saving.value = true
    saved.value = false
    failure.value = null
    try {
      form.value = await saveAdminEventPublicForm(id, form.value)
      saved.value = true
      await loadEvenements()
      return true
    } catch (e) {
      failure.value = toSousCategorieFailure(e, 'SLUG_ALREADY_USED')
      return false
    } finally {
      saving.value = false
    }
  }

  function fail(reason: string) {
    saved.value = false
    failure.value = reason
  }

  return {
    evenements,
    form,
    loading,
    saving,
    saved,
    failure,
    loadEvenements,
    select,
    save,
    fail,
  }
}
