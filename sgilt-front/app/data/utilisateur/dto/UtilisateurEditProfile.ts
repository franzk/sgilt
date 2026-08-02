/**
 * Champs éditables du profil de l'utilisateur connecté — GET /users/me/edit.
 */
export interface UtilisateurEditProfile {
  firstName: string
  lastName: string
  phone: string | null
  email: string
}
