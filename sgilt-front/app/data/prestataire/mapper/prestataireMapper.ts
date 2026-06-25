/**
 * Mapper — conversions entre DTOs, domain et payloads du domaine prestataire
 */
import type { PrestataireCardDto } from '../dto/PrestataireCardDto'
import type { PrestataireDetailDto } from '../dto/PrestataireDetailDto'
import type { PrestataireCardDetail } from '../domain/PrestataireCardDetail'
import type { PrestataireDetail } from '../domain/PrestataireDetail'
import type { PrestataireUpdatePayload } from '../dto/PrestataireUpdatePayload'

function categoryName(key: string): string {
  return APP_CATEGORIES.find((c) => c.key === key)?.name ?? key
}

export function mapPrestataireCard(dto: PrestataireCardDto): PrestataireCardDetail {
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

export function mapPrestataireDetail(dto: PrestataireDetailDto): PrestataireDetail {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    baseline: dto.baseline,
    shortDescription: dto.shortDescription,
    image: dto.heroImage,
    heroImage: dto.heroImage,
    youtubeId: dto.youtubeId ?? null,
    categoryKey: dto.categoryKey,
    category: categoryName(dto.categoryKey),
    subcats: dto.subcatKeys,
    photos: dto.photos ?? [],
    badges: dto.badges ?? [],
    offerings: dto.offerings ?? [],
    identity: { quote: dto.identity?.quote ?? null, bio: dto.identity?.bio ?? null },
    budget: dto.budget,
    testimonials: dto.testimonials ?? [],
    logistics: dto.logistics ?? [],
    technical: dto.technical ?? [],
    faq: dto.faq ?? [],
    unavailableDates: [],
  }
}

export function mapPrestataireUpdatePayload(p: PrestataireDetail): PrestataireUpdatePayload {
  return {
    baseline: p.baseline,
    heroImage: p.heroImage,
    youtubeId: p.youtubeId,
    shortDescription: p.shortDescription,
    photos: p.photos,
    badges: p.badges,
    offerings: p.offerings,
    identity: p.identity,
    budget: p.budget,
    testimonials: p.testimonials,
    logistics: p.logistics,
    technical: p.technical,
    faq: p.faq,
  }
}
