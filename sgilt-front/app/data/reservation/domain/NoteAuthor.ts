/**
 * Domaine — auteur d'une note ou d'un document (client ou prestataire)
 */
export interface NoteAuthor {
  id: string
  name: string
  photo?: string
  role: 'client' | 'prestataire'
}
