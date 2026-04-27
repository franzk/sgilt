import { searchPrestatairesApi, getPrestataireBySlugApi } from '../api/prestataireApi'
import type { PrestataireCardDto, PrestataireDetailDto } from '../dto/PrestataireDto'
import type {
  PrestataireCardDetail,
  PrestataireDetail,
  PrestataireSearchResponse,
} from '../domain/prestataire'

// ── Mapping ───────────────────────────────────────────────────────────────────

function categoryName(key: string): string {
  return APP_CATEGORIES.find((c) => c.key === key)?.name ?? key
}

function mapCard(dto: PrestataireCardDto): PrestataireCardDetail {
  return {
    id: dto.id,
    name: dto.name,
    shortDescription: dto.shortDescription,
    image: dto.heroImage,
    slug: dto.slug,
    categoryKey: dto.categoryKey,
    categoryName: categoryName(dto.categoryKey),
  }
}

function mapDetail(dto: PrestataireDetailDto): PrestataireDetail {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    baseline: dto.baseline,
    shortDescription: dto.shortDescription,
    image: dto.heroImage,
    heroImage: dto.heroImage,
    youtubeId: dto.youtubeId,
    categoryKey: dto.categoryKey,
    category: categoryName(dto.categoryKey),
    subcats: dto.subcatKeys,
    photos: dto.photos ?? [],
    badges: dto.badges ?? [],
    offerings: dto.offerings ?? [],
    identity: dto.identity,
    budget: dto.budget,
    testimonials: dto.testimonials,
    logistics: dto.logistics,
    technical: dto.technical,
    faq: dto.faq,
    unavailableDates: [],
  }
}

// ── Service ───────────────────────────────────────────────────────────────────

export async function searchPrestataires(params: {
  date: string
  categoryKey: string
  subcatKeys: string[]
}): Promise<PrestataireSearchResponse> {
  const isAll = params.categoryKey === 'all' || !params.categoryKey
  const activeSubcats = params.subcatKeys.filter(Boolean)

  const query: { categoryKey?: string; subcatKey?: string[] } = {}
  if (activeSubcats.length > 0) {
    query.subcatKey = activeSubcats
  } else if (!isAll) {
    query.categoryKey = params.categoryKey
  }

  const dto = await searchPrestatairesApi(query)

  return {
    results: dto.results.map(mapCard),
    countsByCategory: dto.countsByCategory,
    subcatCounts: dto.subcatCounts,
  }
}

export async function fetchPrestataireBySlug(slug: string): Promise<PrestataireDetail | null> {
  try {
    const dto = await getPrestataireBySlugApi(slug)
    return mapDetail(dto)
  } catch {
    return null
  }
}
