/**
 * Couche API — appels HTTP bruts vers /admin, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { PrestataireAdminListItemDto } from '../dto/PrestataireAdminListItemDto'
import type { ProvisionPrestataireRequestDto } from '../dto/ProvisionPrestataireRequestDto'
import type { ProvisionPrestataireResponseDto } from '../dto/ProvisionPrestataireResponseDto'

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
