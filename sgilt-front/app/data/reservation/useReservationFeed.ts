/**
 * Fil d'une réservation (frise du Suivi et onglet Documents), commun au client et au prestataire :
 * chargement une fois connecté, ajout de note, envoi de documents (plusieurs fichiers = un envoi),
 * téléchargement et suppression fichier par fichier. Utilisé par ReservationBoard.
 */
import {
  fetchFeed,
  addNote as addNoteService,
  uploadDocuments as uploadDocumentsService,
  downloadDocument,
  deleteDocument as deleteDocumentService,
} from './service/reservationFeedService'
import type { FeedItem } from './domain/FeedItem'

export function useReservationFeed(reservationId: string) {
  const { isAuthenticated } = useKeycloak()
  const feed = ref<FeedItem[]>([])
  const pending = ref(true)
  const error = ref<unknown>(null)

  async function load() {
    pending.value = true
    try {
      feed.value = await fetchFeed(reservationId)
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  }

  watch(
    isAuthenticated,
    (authenticated) => {
      if (authenticated) load()
    },
    { immediate: true },
  )

  const addingNote = ref(false)
  // Échec du dernier enregistrement de note (affiché dans la fenêtre d'ajout).
  const addNoteFailed = ref(false)

  async function addNote(title: string, content: string, isPersonal: boolean) {
    addingNote.value = true
    addNoteFailed.value = false
    try {
      const item = await addNoteService(reservationId, title, content, isPersonal)
      feed.value.unshift(item)
    } catch (e) {
      console.error(e)
      addNoteFailed.value = true
    } finally {
      addingNote.value = false
    }
  }

  const uploading = ref(false)
  // Noms des fichiers du dernier envoi qui n'ont pas pu être envoyés (affichés sous le bouton).
  const uploadFailedFiles = ref<string[]>([])

  // Les fichiers choisis ensemble forment un seul envoi : une seule carte dans la frise. Ceux qui
  // passent y sont, même si d'autres échouent.
  async function uploadDocuments(files: File[], isPersonal: boolean) {
    uploading.value = true
    uploadFailedFiles.value = []
    try {
      const { item, failedFiles } = await uploadDocumentsService(reservationId, files, isPersonal)
      if (item) feed.value.push(item)
      uploadFailedFiles.value = failedFiles
    } finally {
      uploading.value = false
    }
  }

  async function download(url: string, fileName: string) {
    await downloadDocument(url, fileName)
  }

  // Retire le fichier de son envoi ; l'envoi disparaît avec son dernier fichier.
  async function deleteDocument(id: string) {
    await deleteDocumentService(reservationId, id)
    feed.value = feed.value
      .map((item) =>
        item.files?.some((file) => file.id === id)
          ? { ...item, files: item.files.filter((file) => file.id !== id) }
          : item,
      )
      .filter((item) => item.type !== 'document' || (item.files?.length ?? 0) > 0)
  }

  return {
    feed,
    pending,
    error,
    uploading,
    uploadFailedFiles,
    addingNote,
    addNoteFailed,
    load,
    addNote,
    uploadDocuments,
    download,
    deleteDocument,
  }
}
