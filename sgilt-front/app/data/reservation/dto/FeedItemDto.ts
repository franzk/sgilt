export interface FeedItemDto {
  type: 'note' | 'document'
  id: string
  authorId: string
  authorName: string
  authorPhoto?: string | null
  authorRole: 'client' | 'prestataire'
  createdAt: string
  title?: string | null

  // note
  content?: string | null
  isPersonal?: boolean | null
  isMessageInitial?: boolean | null

  // document
  name?: string | null
  fileType?: string | null
  url?: string | null
}
