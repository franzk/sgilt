/**
 * Couche service — orchestration des appels API admin
 */
import {
  listAdminPrestatairesApi,
  publishPrestataireApi,
  sendPrestataireBackToReviewApi,
  provisionPrestataireApi,
} from '../api/adminApi'
import { mapPrestataireAdminFormat, mapProvisionRequest, mapProvisionResult } from '../mapper/adminMapper'
import type { PrestataireAdminFormat } from '../domain/PrestataireAdminFormat'
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
