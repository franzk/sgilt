/**
 * Payload PATCH /users/me.
 * null = ne pas toucher le champ, valeur = écrire tel quel.
 */
export interface UtilisateurUpdatePayload {
  firstName: string | null
  lastName: string | null
  phone: string | null
}
