/**
 * Couche API — appels HTTP bruts vers /admin, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { PrestataireAdminListItemDto } from '../dto/PrestataireAdminListItemDto'
import type { PrestataireOnboardingPendingDto } from '../dto/PrestataireOnboardingPendingDto'
import type { ProvisionPrestataireRequestDto } from '../dto/ProvisionPrestataireRequestDto'
import type { ProvisionPrestataireResponseDto } from '../dto/ProvisionPrestataireResponseDto'
import type { AdminReservationListItemDto } from '../dto/AdminReservationListItemDto'
import type { AdminReservationStatus } from '../domain/AdminReservationStatus'
import type { OnboardingPendingDto } from '../dto/OnboardingPendingDto'
import type {
  CategorieAdminDto,
  SousCategorieAdminDto,
  SousCategorieCreateRequestDto,
  SousCategorieUpdateRequestDto,
} from '../dto/CategorieAdminDto'
import type { MoveDirection } from '../domain/CategorieAdmin'

/**
 * Liste tous les prestataires actifs avec leur statut, pour le back-office admin.
 */
export async function listAdminPrestatairesApi(): Promise<PrestataireAdminListItemDto[]> {
  return apiFetch<PrestataireAdminListItemDto[]>('/admin/prestataires')
}

/**
 * Publie une fiche prestataire (passe de IN_REVIEW à PUBLISHED).
 *
 * @param id identifiant du prestataire à publier
 */
export async function publishPrestataireApi(id: string): Promise<void> {
  return apiFetch<void>(`/admin/prestataires/${id}/publish`, { method: 'POST' })
}

/**
 * Renvoie une fiche publiée en revue — modération admin (passe de PUBLISHED à IN_REVIEW).
 *
 * @param id identifiant du prestataire à renvoyer en revue
 */
export async function sendPrestataireBackToReviewApi(id: string): Promise<void> {
  return apiFetch<void>(`/admin/prestataires/${id}/send-to-review`, { method: 'POST' })
}

/**
 * Provisionne un nouveau prestataire de bout en bout (Keycloak + DB + email d'activation).
 *
 * @param request les informations du prestataire à créer
 */
export async function provisionPrestataireApi(
  request: ProvisionPrestataireRequestDto,
): Promise<ProvisionPrestataireResponseDto> {
  return apiFetch<ProvisionPrestataireResponseDto>('/admin/prestataires', { method: 'POST', body: request })
}

/**
 * Liste les prestataires dont l'onboarding est en attente (lien envoyé par email, pas encore cliqué).
 */
export async function listPendingOnboardingsApi(): Promise<PrestataireOnboardingPendingDto[]> {
  return apiFetch<PrestataireOnboardingPendingDto[]>('/admin/prestataires/onboarding-pending')
}

/**
 * Renvoie le mail d'activation à un prestataire dont l'onboarding est en attente, en
 * réinitialisant la période de validité du lien.
 *
 * @param id identifiant du prestataire dont l'onboarding doit être relancé
 */
export async function resendOnboardingEmailApi(id: string): Promise<void> {
  return apiFetch<void>(`/admin/prestataires/${id}/resend-onboarding-email`, { method: 'POST' })
}

/**
 * Liste toutes les réservations pour le back-office admin, filtrées par statut si fourni.
 *
 * @param status le statut à filtrer, ou undefined pour toutes les réservations
 */
export async function listAdminReservationsApi(
  status?: AdminReservationStatus,
): Promise<AdminReservationListItemDto[]> {
  return apiFetch<AdminReservationListItemDto[]>('/admin/reservations', {
    params: status ? { status } : undefined,
  })
}

/**
 * Liste les sessions d'onboarding utilisateur (client) en attente.
 */
export async function listPendingUserOnboardingsApi(): Promise<OnboardingPendingDto[]> {
  return apiFetch<OnboardingPendingDto[]>('/admin/onboarding-pending')
}

/**
 * Liste les catégories et leurs sous-catégories, avec le nombre de prestataires de chacune.
 */
export async function listCategoriesAdminApi(): Promise<CategorieAdminDto[]> {
  return apiFetch<CategorieAdminDto[]>('/admin/categories')
}

/**
 * Crée une sous-catégorie, en dernière position de sa catégorie, et la renvoie.
 */
export async function createSousCategorieApi(body: SousCategorieCreateRequestDto): Promise<SousCategorieAdminDto> {
  return apiFetch<SousCategorieAdminDto>('/admin/sous-categories', { method: 'POST', body })
}

/**
 * Modifie le libellé et la catégorie d'une sous-catégorie, et la renvoie.
 */
export async function updateSousCategorieApi(
  key: string,
  body: SousCategorieUpdateRequestDto,
): Promise<SousCategorieAdminDto> {
  return apiFetch<SousCategorieAdminDto>(`/admin/sous-categories/${key}`, { method: 'PATCH', body })
}

/**
 * Déplace une sous-catégorie d'un rang dans l'ordre de sa catégorie, et la renvoie.
 */
export async function moveSousCategorieApi(key: string, direction: MoveDirection): Promise<SousCategorieAdminDto> {
  return apiFetch<SousCategorieAdminDto>(`/admin/sous-categories/${key}/move`, {
    method: 'POST',
    query: { direction },
  })
}

/**
 * Supprime une sous-catégorie qu'aucun prestataire n'utilise.
 */
export async function deleteSousCategorieApi(key: string): Promise<void> {
  return apiFetch<void>(`/admin/sous-categories/${key}`, { method: 'DELETE' })
}
