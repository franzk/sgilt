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
  listAdminReservationsApi,
  listPendingUserOnboardingsApi,
  listCategoriesAdminApi,
  createSousCategorieApi,
  updateSousCategorieApi,
  moveSousCategorieApi,
  deleteSousCategorieApi,
  listAdminEvenementsApi,
  putAdminEvenementFeaturesApi,
} from '../api/adminApi'
import {
  mapPrestataireAdminFormat,
  mapProvisionRequest,
  mapProvisionResult,
  mapPrestataireOnboardingPending,
  mapAdminReservationListItem,
  mapOnboardingPending,
  mapCategorieAdmin,
  mapSousCategorieAdmin,
  mapAdminEvenementListItem,
} from '../mapper/adminMapper'
import type { PrestataireAdminFormat } from '../domain/PrestataireAdminFormat'
import type { PrestataireOnboardingPending } from '../domain/PrestataireOnboardingPending'
import type { PrestataireProvisioning } from '../domain/PrestataireProvisioning'
import type { ProvisionResult } from '../domain/ProvisionResult'
import type { AdminReservationListItem } from '../domain/AdminReservationListItem'
import type { AdminReservationStatus } from '../domain/AdminReservationStatus'
import type { OnboardingPending } from '../domain/OnboardingPending'
import type { CategorieAdmin, MoveDirection, SousCategorieAdmin } from '../domain/CategorieAdmin'
import type { AdminEvenementFeatures, AdminEvenementListItem } from '../domain/AdminEvenement'
import { FetchError } from 'ofetch'

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

/**
 * Récupère toutes les réservations pour le back-office admin, filtrées par statut si fourni.
 *
 * @param status le statut à filtrer, ou undefined pour toutes les réservations
 */
export async function fetchAdminReservations(status?: AdminReservationStatus): Promise<AdminReservationListItem[]> {
  const dtos = await listAdminReservationsApi(status)
  return dtos.map(mapAdminReservationListItem)
}

/**
 * Récupère les sessions d'onboarding utilisateur (client) en attente.
 */
export async function fetchPendingUserOnboardings(): Promise<OnboardingPending[]> {
  const dtos = await listPendingUserOnboardingsApi()
  return dtos.map(mapOnboardingPending)
}

/**
 * Récupère les catégories et leurs sous-catégories pour le back-office.
 */
export async function fetchCategoriesAdmin(): Promise<CategorieAdmin[]> {
  return (await listCategoriesAdminApi()).map(mapCategorieAdmin)
}

export async function createSousCategorie(
  key: string,
  name: string,
  categoryKey: string,
): Promise<SousCategorieAdmin> {
  return mapSousCategorieAdmin(await createSousCategorieApi({ key, name, categoryKey }))
}

export async function updateSousCategorie(
  key: string,
  name: string,
  categoryKey: string,
): Promise<SousCategorieAdmin> {
  return mapSousCategorieAdmin(await updateSousCategorieApi(key, { name, categoryKey }))
}

export async function moveSousCategorie(key: string, direction: MoveDirection): Promise<SousCategorieAdmin> {
  return mapSousCategorieAdmin(await moveSousCategorieApi(key, direction))
}

export async function deleteSousCategorie(key: string): Promise<void> {
  await deleteSousCategorieApi(key)
}

/**
 * Motif d'échec d'une opération sur les sous-catégories (suffixe de la clé i18n
 * admin.sous-categories.errors.*). Le back répond 400 sans corps : un 400 est le refus métier de
 * l'opération (`refusal`), toute autre erreur est TECHNICAL.
 */
export function toSousCategorieFailure(error: unknown, refusal: string): string {
  return error instanceof FetchError && error.statusCode === 400 ? refusal : 'TECHNICAL'
}

/**
 * Récupère tous les événements, du plus lointain au plus ancien, pour le back-office admin.
 */
export async function fetchAdminEvenements(): Promise<AdminEvenementListItem[]> {
  return (await listAdminEvenementsApi()).map(mapAdminEvenementListItem)
}

/**
 * Active ou désactive la page publique et la billetterie d'un événement ; renvoie l'événement
 * mis à jour.
 */
export async function saveAdminEvenementFeatures(
  id: string,
  features: AdminEvenementFeatures,
): Promise<AdminEvenementListItem> {
  return mapAdminEvenementListItem(await putAdminEvenementFeaturesApi(id, features))
}

/**
 * Traduit l'échec d'un changement d'interrupteurs en motif d'erreur (suffixe de clé i18n) : une
 * billetterie qui a des ventes ne se coupe pas (409) ; tout le reste est technique (la billetterie
 * sans page est empêchée par l'écran).
 */
export function toFeaturesFailure(error: unknown): string {
  return error instanceof FetchError && error.statusCode === 409 ? 'BILLETTERIE_WITH_SALES' : 'TECHNICAL'
}
