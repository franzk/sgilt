export interface CurrentUser {
  firstName?: string
  lastName?: string
  email: string
  photo: string | null
}

interface UtilisateurProfileDto {
  firstName: string
  lastName: string
  email: string
  avatarUrl: string | null
}

const _user = reactive<CurrentUser>({ firstName: '', lastName: '', email: '', photo: null })
let _watcherStarted = false

export function useCurrentUser(): CurrentUser {
  if (!_watcherStarted && import.meta.client) {
    _watcherStarted = true
    const { isAuthenticated } = useKeycloak()

    watch(
      isAuthenticated,
      async (auth) => {
        if (!auth) {
          _user.firstName = ''
          _user.lastName = ''
          _user.email = ''
          _user.photo = null
          return
        }
        try {
          const data = await apiFetch<UtilisateurProfileDto>('/users/me')
          _user.firstName = data.firstName
          _user.lastName = data.lastName
          _user.email = data.email
          _user.photo = data.avatarUrl
        } catch {
          // garder les valeurs vides en cas d'erreur réseau
        }
      },
      { immediate: true },
    )
  }

  return _user
}
