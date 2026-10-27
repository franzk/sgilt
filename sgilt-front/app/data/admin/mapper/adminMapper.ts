/**
 * Mapper — conversions entre DTOs et domain du module admin
 */
import type { PrestataireAdminListItemDto } from '../dto/PrestataireAdminListItemDto'
import type { PrestataireAdminFormat } from '../domain/PrestataireAdminFormat'
import type { PrestataireOnboardingPendingDto } from '../dto/PrestataireOnboardingPendingDto'
import type { PrestataireOnboardingPending } from '../domain/PrestataireOnboardingPending'
import type { ProvisionPrestataireRequestDto } from '../dto/ProvisionPrestataireRequestDto'
import type { PrestataireProvisioning } from '../domain/PrestataireProvisioning'
import type { ProvisionPrestataireResponseDto } from '../dto/ProvisionPrestataireResponseDto'
import type { ProvisionResult } from '../domain/ProvisionResult'
import type { AdminReservationListItemDto } from '../dto/AdminReservationListItemDto'
import type { AdminReservationListItem } from '../domain/AdminReservationListItem'
import type { OnboardingPendingDto } from '../dto/OnboardingPendingDto'
import type { OnboardingPending } from '../domain/OnboardingPending'
import type { CategorieAdminDto, SousCategorieAdminDto } from '../dto/CategorieAdminDto'
import type { CategorieAdmin, SousCategorieAdmin } from '../domain/CategorieAdmin'
import type {
  AdminEvenementListItemDto,
  AdminEventPublicFormDto,
  AdminPageDto,
} from '../dto/AdminEvenementDto'
import type {
  AdminEvenementListItem,
  AdminEventPublicForm,
  AdminPageForm,
} from '../domain/AdminEvenement'

export function mapPrestataireAdminFormat(dto: PrestataireAdminListItemDto): PrestataireAdminFormat {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    status: dto.status,
    email: dto.email,
    categoryKey: dto.categoryKey,
    subcatKey: dto.subcatKey,
    reservationCounts: dto.reservationCounts,
  }
}

export function mapProvisionRequest(provisioning: PrestataireProvisioning): ProvisionPrestataireRequestDto {
  return {
    email: provisioning.email,
    firstName: provisioning.firstName,
    lastName: provisioning.lastName,
    slug: provisioning.slug,
    prestataireName: provisioning.prestataireName,
    category: provisioning.category,
    subcat: provisioning.subcat,
    cleEnMain: provisioning.cleEnMain,
  }
}

export function mapProvisionResult(dto: ProvisionPrestataireResponseDto): ProvisionResult {
  return {
    prestataireId: dto.prestataireId,
    utilisateurId: dto.utilisateurId,
    slug: dto.slug,
  }
}

export function mapPrestataireOnboardingPending(
  dto: PrestataireOnboardingPendingDto,
): PrestataireOnboardingPending {
  return {
    prestataireId: dto.prestataireId,
    prestataireName: dto.prestataireName,
    email: dto.email,
    linkSentAt: dto.linkSentAt,
    linkExpiresAt: dto.linkExpiresAt,
  }
}

export function mapAdminReservationListItem(dto: AdminReservationListItemDto): AdminReservationListItem {
  return {
    id: dto.id,
    eventTitle: dto.eventTitle,
    organizerEmail: dto.organizerEmail,
    providerEmail: dto.providerEmail,
    providerSlug: dto.providerSlug,
    status: dto.status,
    createdAt: dto.createdAt,
  }
}

export function mapOnboardingPending(dto: OnboardingPendingDto): OnboardingPending {
  return {
    id: dto.id,
    email: dto.email,
    eventType: dto.eventType,
    eventDate: dto.eventDate,
    demandeCount: dto.demandeCount,
    state: dto.state,
    createdAt: dto.createdAt,
    expiresAt: dto.expiresAt,
  }
}

export function mapSousCategorieAdmin(dto: SousCategorieAdminDto): SousCategorieAdmin {
  return {
    key: dto.key,
    name: dto.name,
    categoryKey: dto.categoryKey,
    prestataireCount: dto.prestataireCount,
  }
}

export function mapCategorieAdmin(dto: CategorieAdminDto): CategorieAdmin {
  return {
    key: dto.key,
    name: dto.name,
    subcategories: dto.subcategories.map(mapSousCategorieAdmin),
  }
}

export function mapAdminEvenementListItem(dto: AdminEvenementListItemDto): AdminEvenementListItem {
  return {
    id: dto.id,
    title: dto.title,
    date: dto.date,
    ownerEmail: dto.ownerEmail,
    slug: dto.slug ?? undefined,
  }
}

function mapAdminPageForm(dto: AdminPageDto): AdminPageForm {
  return {
    slug: dto.slug,
    tag: dto.tag ?? '',
    subtitle: dto.subtitle ?? '',
    sectionTitle: dto.sectionTitle ?? '',
    description: dto.description ?? '',
    importantInfo: dto.importantInfo,
    practicalInfo: dto.practicalInfo,
    pictos: dto.pictos,
  }
}

export function mapAdminEventPublicForm(dto: AdminEventPublicFormDto): AdminEventPublicForm {
  return {
    startTime: dto.startTime ?? '',
    endTime: dto.endTime ?? '',
    page: dto.page ? mapAdminPageForm(dto.page) : null,
    ticketing: dto.ticketing,
  }
}

// Champ texte vide → null, comme l'attend le back.
const emptyToNull = (value: string): string | null => value.trim() || null

function mapAdminPageRequest(form: AdminPageForm): AdminPageDto {
  return {
    slug: form.slug.trim(),
    tag: emptyToNull(form.tag),
    subtitle: emptyToNull(form.subtitle),
    sectionTitle: emptyToNull(form.sectionTitle),
    description: emptyToNull(form.description),
    importantInfo: form.importantInfo,
    practicalInfo: form.practicalInfo,
    pictos: form.pictos,
  }
}

export function mapAdminEventPublicFormRequest(form: AdminEventPublicForm): AdminEventPublicFormDto {
  return {
    startTime: form.startTime || null,
    endTime: form.endTime || null,
    page: form.page ? mapAdminPageRequest(form.page) : null,
    ticketing: form.ticketing,
  }
}
