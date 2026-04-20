import Keycloak, { type KeycloakTokenParsed } from 'keycloak-js'

// État partagé au niveau du module — singleton commun à tous les appels de useKeycloak()
const _keycloak = ref<Keycloak | null>(null)
const _isAuthenticated = ref(false)
const _isInitialized = ref(false)

function parseJwtPayload(token: string): Record<string, unknown> {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return {}
    const base64Url = parts[1]
    const base64 = base64Url?.replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64 || '')
    const result: Record<string, unknown> = JSON.parse(json)
    return result
  } catch {
    return {}
  }
}

export function useKeycloak() {
  function hasRole(role: string): boolean {
    if (!_isAuthenticated.value || !_keycloak.value) return false
    return _keycloak.value.hasRealmRole(role)
  }

  async function login(): Promise<void> {
    await _keycloak.value?.login()
  }

  async function logout(): Promise<void> {
    await _keycloak.value?.logout()
  }

  /**
   * Injecte des tokens Keycloak obtenus hors du flux KC standard (ex. onboarding).
   * Le cast vers KeycloakTokenParsed est nécessaire car on parse le JWT manuellement
   * sans passer par l'initialisation KC, mais la structure du payload est identique.
   */
  function injectTokens(accessToken: string, refreshToken: string): void {
    if (!_keycloak.value) return

    const parsed = parseJwtPayload(accessToken)

    _keycloak.value.token = accessToken
    _keycloak.value.refreshToken = refreshToken
    // Cast documenté : le payload JWT a la même structure que KeycloakTokenParsed
    _keycloak.value.tokenParsed = parsed as unknown as KeycloakTokenParsed
    _keycloak.value.authenticated = true
    _isAuthenticated.value = true
  }

  return {
    keycloak: readonly(_keycloak),
    isAuthenticated: readonly(_isAuthenticated),
    isInitialized: readonly(_isInitialized),
    hasRole,
    login,
    logout,
    injectTokens,
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
