export interface PrestataireCardDto {
  id: string
  name: string
  shortDescription: string
  heroImage: string
  slug: string
  categoryKey: string
}

export interface PrestataireDetailDto extends PrestataireCardDto {
  baseline: string
  youtubeId?: string
  subcatKeys: string[]
  photos: string[]
  badges: { icon: string; label: string; description: string; color: string }[]
  offerings: string[]
  identity?: { quote: string; bio: string }
  budget?: string
  testimonials?: { author: string; text: string; eventType?: string }[]
  logistics?: string[]
  technical?: string[]
  faq?: { question: string; answer: string }[]
}

export interface PrestataireSearchResponseDto {
  results: PrestataireCardDto[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
