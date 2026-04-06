export interface CurrentUser {
  fullName: string
  email: string
  photo: string | null
}

export function useCurrentUser(): CurrentUser {
  // TODO: remplacer par les données Keycloak
  return {
    fullName: 'Sophie Lambert',
    email: 'sophie.lambert@email.fr',
    photo: null,
  }
}
