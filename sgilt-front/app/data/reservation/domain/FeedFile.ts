/**
 * Domaine — un fichier d'un envoi de documents du fil d'une réservation.
 * Son id est celui du document : c'est lui qu'on télécharge et qu'on supprime.
 */
export interface FeedFile {
  id: string
  name: string
  fileType: string | null
  url: string
}
