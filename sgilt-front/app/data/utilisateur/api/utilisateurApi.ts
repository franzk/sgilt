/**
 * Couche API — appels HTTP bruts vers /users, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { UtilisateurEditProfile } from '../dto/UtilisateurEditProfile'
import type { UtilisateurUpdatePayload } from '../dto/UtilisateurUpdatePayload'

export async function getUtilisateurEditProfileApi(): Promise<UtilisateurEditProfile> {
  return apiFetch<UtilisateurEditProfile>('/users/me/edit')
}

export async function patchUtilisateurApi(payload: Partial<UtilisateurUpdatePayload>): Promise<void> {
  return apiFetch<void>('/users/me/edit', { method: 'PATCH', body: payload })
}
