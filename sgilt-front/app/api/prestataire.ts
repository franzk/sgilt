import type {
  PrestataireCardDetail,
  PrestataireDetail,
  PrestataireSearchResponse,
} from '~/types/prestataire'

// ── Types réponse API back-end ────────────────────────────────────────────────

interface ApiCard {
  id: string
  name: string
  shortDescription: string
  heroImage: string
  slug: string
  categoryKey: string
}

interface ApiDetail extends ApiCard {
  baseline: string
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

interface ApiSearchResponse {
  results: ApiCard[]
  countsByCategory: Record<string, number> // clés catégorie ('musique', 'all'…)
  subcatCounts: Record<string, number>      // clés sous-catégorie ('dj', 'pop-rock'…)
}

// ── Helpers ───────────────────────────────────────────────────────────────────

function categoryName(key: string): string {
  return APP_CATEGORIES.find((c) => c.key === key)?.name ?? key
}

function mapCard(card: ApiCard): PrestataireCardDetail {
  return {
    id: card.id,
    name: card.name,
    shortDescription: card.shortDescription,
    image: card.heroImage,
    slug: card.slug,
    categoryKey: card.categoryKey,
    categoryName: categoryName(card.categoryKey),
  }
}

function mapDetail(d: ApiDetail): PrestataireDetail {
  return {
    id: d.id,
    name: d.name,
    slug: d.slug,
    baseline: d.baseline,
    shortDescription: d.shortDescription,
    image: d.heroImage,
    heroImage: d.heroImage,
    categoryKey: d.categoryKey,
    category: categoryName(d.categoryKey),
    subcats: d.subcatKeys,
    photos: d.photos ?? [],
    badges: d.badges ?? [],
    offerings: d.offerings ?? [],
    identity: d.identity,
    budget: d.budget,
    testimonials: d.testimonials,
    logistics: d.logistics,
    technical: d.technical,
    faq: d.faq,
    unavailableDates: [],
  }
}

// ── Fonctions publiques ───────────────────────────────────────────────────────

export async function searchPrestataires(params: {
  date: string
  categoryKey: string
  subcatKeys: string[]
}): Promise<PrestataireSearchResponse> {
  const isAll = params.categoryKey === 'all' || !params.categoryKey
  const hasSubcats = params.subcatKeys.filter(Boolean).length > 0

  const query: Record<string, unknown> = {}

  // Filtre exclusif : sous-catégories OU catégorie, pas les deux
  if (hasSubcats) {
    query.subcatKey = params.subcatKeys.filter(Boolean)
  } else if (!isAll) {
    query.categoryKey = params.categoryKey
  }

  const data = await apiFetch<ApiSearchResponse>('/prestataires', { query })

  return {
    results: data.results.map(mapCard),
    countsByCategory: data.countsByCategory,
    subcatCounts: data.subcatCounts,
  }
}

export async function fetchPrestataireBySlug(slug: string): Promise<PrestataireDetail | null> {
  try {
    const data = await apiFetch<ApiDetail>(`/prestataires/${slug}`)
    return mapDetail(data)
  } catch {
    return null
  }
}
