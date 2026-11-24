/**
 * Appels HTTP du fil d'une réservation (frise du Suivi) : lecture du fil, ajout de note, envoi
 * (fichier par fichier, puis clôture) et suppression de document. Utilisé par reservationFeedService.
 */
import { apiFetch } from '~/composables/useApi'
import type { FeedItemDto } from '../dto/FeedItemDto'

export async function getFeedApi(reservationId: string): Promise<FeedItemDto[]> {
  return apiFetch<FeedItemDto[]>(`/reservations/${reservationId}/feed`)
}

// Un fichier par appel, rattaché à son envoi par uploadId (voir le service). Ne notifie pas : la
// notification part à la clôture de l'envoi.
export async function uploadDocumentApi(
  reservationId: string,
  file: File,
  isPersonal: boolean,
  uploadId: string,
): Promise<FeedItemDto> {
  const body = new FormData()
  body.append('file', file)
  body.append('isPersonal', String(isPersonal))
  body.append('uploadId', uploadId)
  return apiFetch<FeedItemDto>(`/reservations/${reservationId}/feed/documents`, {
    method: 'POST',
    body,
  })
}

// Clôture d'un envoi : une seule notification à l'autre partie pour tous ses fichiers.
export async function completeUploadApi(reservationId: string, uploadId: string): Promise<void> {
  return apiFetch<void>(`/reservations/${reservationId}/feed/uploads/${uploadId}/complete`, {
    method: 'POST',
  })
}

export async function deleteDocumentApi(reservationId: string, documentId: string): Promise<void> {
  return apiFetch<void>(`/reservations/${reservationId}/feed/documents/${documentId}`, {
    method: 'DELETE',
  })
}

export async function addNoteApi(
  reservationId: string,
  title: string,
  content: string,
  isPersonal: boolean,
): Promise<FeedItemDto> {
  return apiFetch<FeedItemDto>(`/reservations/${reservationId}/feed/notes`, {
    method: 'POST',
    body: { title, content, isPersonal },
  })
}
