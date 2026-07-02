import {
  fetchFeed,
  addNote as addNoteService,
  uploadDocument as uploadDocumentService,
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

  async function addNote(title: string, content: string, isPersonal: boolean) {
    const item = await addNoteService(reservationId, title, content, isPersonal)
    feed.value.unshift(item)
  }

  const uploading = ref(false)

  async function uploadDocument(file: File, isPersonal: boolean) {
    uploading.value = true
    try {
      const item = await uploadDocumentService(reservationId, file, isPersonal)
      feed.value.push(item)
    } finally {
      uploading.value = false
    }
  }

  async function download(url: string, fileName: string) {
    await downloadDocument(url, fileName)
  }

  async function deleteDocument(id: string) {
    await deleteDocumentService(reservationId, id)
    feed.value = feed.value.filter((item) => item.id !== id)
  }

  return {
    feed,
    pending,
    error,
    uploading,
    load,
    addNote,
    uploadDocument,
    download,
    deleteDocument,
  }
}
