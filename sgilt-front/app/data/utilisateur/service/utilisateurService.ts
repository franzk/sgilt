/**
 * Couche service — orchestration des appels API utilisateur
 */
import { getUtilisateurEditProfileApi, patchUtilisateurApi } from '../api/utilisateurApi'
import type { UtilisateurEditProfile } from '../dto/UtilisateurEditProfile'
import type { UtilisateurUpdatePayload } from '../dto/UtilisateurUpdatePayload'

export async function fetchUtilisateurEditProfile(): Promise<UtilisateurEditProfile> {
  return getUtilisateurEditProfileApi()
}

export async function updateUtilisateurProfile(payload: Partial<UtilisateurUpdatePayload>): Promise<void> {
  await patchUtilisateurApi(payload)
}
