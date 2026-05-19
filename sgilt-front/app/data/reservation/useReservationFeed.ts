import { fetchFeed, addNote as addNoteService, uploadDocument as uploadDocumentService, downloadDocument } from './service/reservationFeedService'
import type { FeedItem } from './domain/FeedItem'

export function useReservationFeed(reservationId: string) {
  const feed = ref<FeedItem[]>([])
  const pending = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      feed.value = await fetchFeed(reservationId)
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  })

  async function addNote(title: string, content: string, isPersonal: boolean) {
    const item = await addNoteService(reservationId, title, content, isPersonal)
    feed.value.unshift(item)
  }

  async function uploadDocument(file: File, isPersonal: boolean) {
    const item = await uploadDocumentService(reservationId, file, isPersonal)
    feed.value.push(item)
  }

  async function download(url: string, fileName: string) {
    await downloadDocument(url, fileName)
  }

  function removeItem(id: string) {
    feed.value = feed.value.filter((item) => item.id !== id)
  }

  return { feed, pending, error, addNote, uploadDocument, download, removeItem }
}
