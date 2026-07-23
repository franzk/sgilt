/**
 * Mapper — conversions entre DTOs, domain et payloads du domaine prestataire
 */
import type { PrestataireCardDto } from '../dto/PrestataireCardDto'
import type { PrestataireDetailDto } from '../dto/PrestataireDetailDto'
import type { PrestataireCardDetail } from '../domain/PrestataireCardDetail'
import type { PrestataireDetail } from '../domain/PrestataireDetail'
import { heroRef } from '~/utils/mediaUtils'

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

/** Adapte la fiche complète à la forme attendue par la card de résultats de recherche. */
export function mapPrestataireDetailToCard(prestataire: PrestataireDetail): PrestataireCardDetail {
  return {
    id: prestataire.id,
    name: prestataire.name,
    shortDescription: prestataire.shortDescription,
    image: heroRef(prestataire.medias) ?? '',
    slug: prestataire.slug,
    categoryKey: prestataire.categoryKey,
    categoryName: prestataire.category,
  }
}

export function mapPrestataireDetail(dto: PrestataireDetailDto): PrestataireDetail {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    baseline: dto.baseline,
    shortDescription: dto.shortDescription,
    categoryKey: dto.categoryKey,
    category: categoryName(dto.categoryKey),
    subcats: dto.subcatKeys,
    avatar: dto.avatar,
    medias: dto.medias ?? [],
    badges: dto.badges ?? [],
    offerings: dto.offerings ?? [],
    identity: { quote: dto.identity?.quote ?? null, bio: dto.identity?.bio ?? null },
    budget: dto.budget,
    testimonials: dto.testimonials ?? [],
    details: dto.details ?? [],
    faq: dto.faq ?? [],
    unavailableDates: [],
    status: dto.status,
  }
}
