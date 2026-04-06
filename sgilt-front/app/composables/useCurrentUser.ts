export interface CurrentUser {
  fullName: string
  email: string
  photo: string | null
}

export function useCurrentUser(): CurrentUser {
  // TODO: remplacer par les données Keycloak
  return {
    fullName: 'Julie Muller',
    email: 'julie.muller@email.fr',
    photo: null,
  }
}
