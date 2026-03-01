// app/types/prestataire.ts
export interface PrestataireCardDetail {
  id: string
  name: string
  shortDescription: string
  image: string
  slug: string
  categoryPicto?: Component
  categoryName?: string
}

export interface PrestataireDetail extends PrestataireCardDetail {
  longDescription: string
  youtubeId?: string
  calendar?: any[]
  categoryId: string
  subcats: string[]
}

export interface PrestataireSearchResponse {
  results: PrestataireCardDetail[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
