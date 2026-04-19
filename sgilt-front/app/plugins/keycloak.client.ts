import Keycloak from 'keycloak-js'

export default defineNuxtPlugin(async () => {
  const { public: config } = useRuntimeConfig()
  const kcUrl = config.kcUrl
  const kcRealm = config.kcRealm
  const kcClientId = config.kcClientId

  if (!kcUrl || !kcRealm || !kcClientId) {
    console.error('[KC] Configuration manquante (kcUrl / kcRealm / kcClientId). KC non initialisé.')
    return
  }

  const kc = new Keycloak({ url: kcUrl, realm: kcRealm, clientId: kcClientId })

  kc.onTokenExpired = () => {
    kc.updateToken(30).catch(() => {
      console.warn('[KC] Échec du renouvellement du token — session expirée.')
    })
  }

  try {
    const authenticated = await kc.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
    })
    initKeycloak(kc, authenticated)
  } catch {
    console.error('[KC] Échec de l\'initialisation Keycloak.')
    initKeycloak(kc, false)
  }
})
