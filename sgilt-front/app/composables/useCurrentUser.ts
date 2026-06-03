export interface CurrentUser {
  firstName?: string
  lastName?: string
  email: string
  photo: string | null
  loading: boolean
}

interface UtilisateurProfileDto {
  firstName: string
  lastName: string
  email: string
  avatarUrl: string | null
}

const _user = reactive<CurrentUser>({
  firstName: '',
  lastName: '',
  email: '',
  photo: null,
  loading: true,
})
let _watcherStarted = false

interface ProMeDto {
  id: string
  email: string
  firstName: string
  lastName: string
}

export function useCurrentUser(): CurrentUser {
  if (!_watcherStarted && import.meta.client) {
    _watcherStarted = true
    const { isAuthenticated, hasRole } = useKeycloak()

    watch(
      isAuthenticated,
      async (auth) => {
        _user.loading = true
        if (!auth) {
          _user.firstName = ''
          _user.lastName = ''
          _user.email = ''
          _user.photo = null
          _user.loading = false
          return
        }
        try {
          if (hasRole('PRO')) {
            const data = await apiFetch<ProMeDto>('/pro/me')
            _user.firstName = data.firstName
            _user.lastName = data.lastName
            _user.email = data.email
            _user.photo = null
          } else {
            const data = await apiFetch<UtilisateurProfileDto>('/users/me')
            _user.firstName = data.firstName
            _user.lastName = data.lastName
            _user.email = data.email
            _user.photo = data.avatarUrl
          }
        } catch {
          // garder les valeurs vides en cas d'erreur réseau
        } finally {
          _user.loading = false
        }
      },
      { immediate: true },
    )
  }

  return _user
}
