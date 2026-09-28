/**
 * Mapper — conversions entre DTOs, domain et payloads du domaine prestataire
 */
import type { CategoryDto } from '../dto/CategoryDto'
import type { PrestataireCardDto } from '../dto/PrestataireCardDto'
import type { PrestataireDetailDto } from '../dto/PrestataireDetailDto'
import type { Category } from '../domain/Category'
import type { PrestataireCardDetail } from '../domain/PrestataireCardDetail'
import type { PrestataireDetail } from '../domain/PrestataireDetail'
import { heroRef } from '~/utils/mediaUtils'

export function mapPrestataireCard(dto: PrestataireCardDto): PrestataireCardDetail {
  return {
    id: dto.id,
    name: dto.name,
    shortDescription: dto.shortDescription,
    image: dto.heroImage,
    slug: dto.slug,
    categoryKey: dto.categoryKey,
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
  }
}

export function mapPrestataireDetail(dto: PrestataireDetailDto): PrestataireDetail {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    baseline: dto.baseline,
    shortDescription: dto.shortDescription,
    metaTitle: dto.metaTitle,
    metaDescription: dto.metaDescription,
    categoryKey: dto.categoryKey,
    subcatKey: dto.subcatKey,
    avatar: dto.avatar,
    medias: dto.medias ?? [],
    badges: dto.badges ?? [],
    offerings: dto.offerings ?? [],
    identity: { quote: dto.identity?.quote ?? null, bio: dto.identity?.bio ?? null },
    budget: dto.budget,
    testimonials: dto.testimonials ?? [],
    details: dto.details ?? [],
    faq: dto.faq ?? [],
    status: dto.status,
  }
}

export function mapCategory(dto: CategoryDto): Category {
  return {
    key: dto.key,
    name: dto.name,
    subcategories: dto.subcategories.map((subcategory) => ({
      key: subcategory.key,
      name: subcategory.name,
      categoryKey: subcategory.categoryKey,
    })),
  }
}
