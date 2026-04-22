export default defineNuxtRouteMiddleware(async (to) => {
  // Le middleware ne s'exécute que côté client (KC est client-side only)
  if (import.meta.server) return

  const { isInitialized, isAuthenticated, hasRole, login } = useKeycloak()

  // Attendre que KC ait terminé son initialisation avant de vérifier l'état
  if (!isInitialized.value) {
    await new Promise<void>((resolve) => {
      const unwatch = watch(isInitialized, (val) => {
        if (val) {
          unwatch()
          resolve()
        }
      })
    })
  }

  const isAppRoute = to.path.startsWith('/app')
  const isProRoute = to.path.startsWith('/pro')

  if (!isAppRoute && !isProRoute) return

  if (!isAuthenticated.value) {
    await login()
    return
  }

  // Espace client (/app) : USER et ADMIN autorisés, PRO seul est refusé
  if (isAppRoute && hasRole('PRO') && !hasRole('USER') && !hasRole('ADMIN')) {
    return navigateTo('/unauthorized')
  }

  // Espace pro (/pro) : PRO et ADMIN autorisés, USER seul est refusé
  if (isProRoute && !hasRole('PRO') && !hasRole('ADMIN')) {
    return navigateTo('/unauthorized')
  }
})
