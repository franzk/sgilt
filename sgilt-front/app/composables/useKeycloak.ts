import Keycloak, { type KeycloakLoginOptions } from 'keycloak-js'

// État partagé au niveau du module — singleton commun à tous les appels de useKeycloak()
const _keycloak = ref<Keycloak | null>(null)
const _isAuthenticated = ref(false)
const _isInitialized = ref(false)

export function useKeycloak() {
  function hasRole(role: string): boolean {
    if (!_isAuthenticated.value || !_keycloak.value) return false
    return _keycloak.value.hasRealmRole(role)
  }

  async function login(options?: KeycloakLoginOptions): Promise<void> {
    await _keycloak.value?.login(options)
  }

  async function logout(): Promise<void> {
    await _keycloak.value?.logout()
  }

  return {
    keycloak: readonly(_keycloak),
    isAuthenticated: readonly(_isAuthenticated),
    isInitialized: readonly(_isInitialized),
    hasRole,
    login,
    logout,
  }
}

/**
 * Initialise l'état KC partagé — réservé au plugin keycloak.client.ts.
 */
export function initKeycloak(kc: Keycloak, authenticated: boolean): void {
  _keycloak.value = kc
  _isAuthenticated.value = authenticated
  _isInitialized.value = true
}
