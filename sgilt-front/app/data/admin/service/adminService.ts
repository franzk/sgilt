/**
 * Couche service — orchestration des appels API admin
 */
import {
  listAdminPrestatairesApi,
  publishPrestataireApi,
  sendPrestataireBackToReviewApi,
  provisionPrestataireApi,
  listPendingOnboardingsApi,
  resendOnboardingEmailApi,
} from '../api/adminApi'
import {
  mapPrestataireAdminFormat,
  mapProvisionRequest,
  mapProvisionResult,
  mapPrestataireOnboardingPending,
} from '../mapper/adminMapper'
import type { PrestataireAdminFormat } from '../domain/PrestataireAdminFormat'
import type { PrestataireOnboardingPending } from '../domain/PrestataireOnboardingPending'
import type { PrestataireProvisioning } from '../domain/PrestataireProvisioning'
import type { ProvisionResult } from '../domain/ProvisionResult'

/**
 * Récupère tous les prestataires actifs avec leur statut, pour le back-office admin.
 */
export async function fetchAdminPrestataires(): Promise<PrestataireAdminFormat[]> {
  const dtos = await listAdminPrestatairesApi()
  return dtos.map(mapPrestataireAdminFormat)
}

/**
 * Publie une fiche prestataire (passe de IN_REVIEW à PUBLISHED).
 *
 * @param id identifiant du prestataire à publier
 */
export async function publishPrestataire(id: string): Promise<void> {
  await publishPrestataireApi(id)
}

/**
 * Renvoie une fiche publiée en revue — modération admin (passe de PUBLISHED à IN_REVIEW).
 *
 * @param id identifiant du prestataire à renvoyer en revue
 */
export async function sendPrestataireBackToReview(id: string): Promise<void> {
  await sendPrestataireBackToReviewApi(id)
}

/**
 * Provisionne un nouveau prestataire de bout en bout (Keycloak + DB + email d'activation).
 *
 * @param provisioning les informations du prestataire à créer
 */
export async function provisionPrestataire(provisioning: PrestataireProvisioning): Promise<ProvisionResult> {
  const dto = await provisionPrestataireApi(mapProvisionRequest(provisioning))
  return mapProvisionResult(dto)
}

/**
 * Récupère les prestataires dont l'onboarding est en attente (lien envoyé par email, pas encore cliqué).
 */
export async function fetchPendingOnboardings(): Promise<PrestataireOnboardingPending[]> {
  const dtos = await listPendingOnboardingsApi()
  return dtos.map(mapPrestataireOnboardingPending)
}

/**
 * Renvoie le mail d'activation à un prestataire dont l'onboarding est en attente, en
 * réinitialisant la période de validité du lien.
 *
 * @param id identifiant du prestataire dont l'onboarding doit être relancé
 */
export async function resendOnboardingEmail(id: string): Promise<void> {
  await resendOnboardingEmailApi(id)
}
