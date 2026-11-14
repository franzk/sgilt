/**
 * Composable — page publique d'un événement administrée par son organisateur
 * (/app/events/[eventId]/page-publique) et sa billetterie (/app/events/[eventId]/billetterie),
 * servies par le même appel : chargées une fois, puis remplacées par l'état renvoyé à
 * chaque modification (contenu, publication, billetterie). Chaque bloc d'écran a son
 * enregistrement et son motif d'échec (suffixe de clé i18n evenement.page-publique.errors.*).
 */
import type { OrganizerPublicPage, PageContentForm, TicketingSettings } from './domain/OrganizerPublicPage'
import {
  fetchOrganizerPublicPage,
  isPublicPageNotFound,
  publishOrganizerPage,
  saveOrganizerPageContent,
  saveOrganizerTicketing,
  toContentFailure,
  toTicketingFailure,
  unpublishOrganizerPage,
} from './service/organizerPageService'

// État propre à chaque appel (chaque écran a le sien).
export function useOrganizerPublicPage(eventId: string) {
  const publicPage = ref<OrganizerPublicPage | null>(null)
  const loading = ref(true)
  // Chargement impossible (hors 404, renvoyé à l'appelant par `notFound`).
  const loadFailed = ref(false)
  // Page publique pas activée par le back-office.
  const notFound = ref(false)

  const savingContent = ref(false)
  const contentSaved = ref(false)
  const contentFailure = ref<string | null>(null)

  const publishing = ref(false)
  const publicationFailure = ref<string | null>(null)

  const savingTicketing = ref(false)
  const ticketingSaved = ref(false)
  const ticketingFailure = ref<string | null>(null)

  async function load() {
    loading.value = true
    loadFailed.value = false
    try {
      publicPage.value = await fetchOrganizerPublicPage(eventId)
    } catch (e) {
      if (isPublicPageNotFound(e)) notFound.value = true
      else loadFailed.value = true
    } finally {
      loading.value = false
    }
  }

  async function saveContent(form: PageContentForm) {
    savingContent.value = true
    contentSaved.value = false
    contentFailure.value = null
    try {
      publicPage.value = await saveOrganizerPageContent(eventId, form)
      contentSaved.value = true
    } catch (e) {
      contentFailure.value = toContentFailure(e)
    } finally {
      savingContent.value = false
    }
  }

  // Refus détecté par l'écran, sans appel au back.
  function failContent(reason: string) {
    contentSaved.value = false
    contentFailure.value = reason
  }

  // Publie ou dépublie. Une page pas encore créée ne se publie pas : l'écran le signale avant.
  async function setPublished(published: boolean) {
    publishing.value = true
    publicationFailure.value = null
    try {
      publicPage.value = published ? await publishOrganizerPage(eventId) : await unpublishOrganizerPage(eventId)
    } catch {
      publicationFailure.value = 'TECHNICAL'
    } finally {
      publishing.value = false
    }
  }

  function failPublication(reason: string) {
    publicationFailure.value = reason
  }

  async function saveTicketing(settings: TicketingSettings) {
    savingTicketing.value = true
    ticketingSaved.value = false
    ticketingFailure.value = null
    try {
      publicPage.value = await saveOrganizerTicketing(eventId, settings)
      ticketingSaved.value = true
    } catch (e) {
      ticketingFailure.value = toTicketingFailure(e)
    } finally {
      savingTicketing.value = false
    }
  }

  // Refus détecté par l'écran, sans appel au back.
  function failTicketing(reason: string) {
    ticketingSaved.value = false
    ticketingFailure.value = reason
  }

  return {
    publicPage,
    loading,
    loadFailed,
    notFound,
    savingContent,
    contentSaved,
    contentFailure,
    publishing,
    publicationFailure,
    savingTicketing,
    ticketingSaved,
    ticketingFailure,
    load,
    saveContent,
    failContent,
    setPublished,
    failPublication,
    saveTicketing,
    failTicketing,
  }
}
