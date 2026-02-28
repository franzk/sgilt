// app/types/prestataire.ts
export interface PrestataireCard {
  id: string
  name: string
  shortDescription: string
  image: string
  slug: string
}

export interface PrestataireDetail extends PrestataireCard {
  longDescription: string
  youtubeId?: string
  calendar?: any[]
  category: string
  subcats: string[]
}

export interface PrestataireSearchResponse {
  results: PrestataireCard[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
