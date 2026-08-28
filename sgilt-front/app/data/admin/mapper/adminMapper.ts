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

export function mapPrestataireAdminFormat(dto: PrestataireAdminListItemDto): PrestataireAdminFormat {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    status: dto.status,
    email: dto.email,
    categoryKey: dto.categoryKey,
    subcatKeys: dto.subcatKeys,
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
    subcats: provisioning.subcats,
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
    prestataireName: dto.prestataireName,
    state: dto.state,
    createdAt: dto.createdAt,
    expiresAt: dto.expiresAt,
  }
}
