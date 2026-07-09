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
  const isAdminRoute = to.path.startsWith('/admin')
  const isRedirectRoute = to.path === '/auth/redirect'

  if (!isAppRoute && !isProRoute && !isAdminRoute && !isRedirectRoute) return

  if (!isAuthenticated.value) {
    await login({ redirectUri: globalThis.location.origin + '/auth/redirect' })
    return
  }

  if (isRedirectRoute) {
    if (hasRole('ADMIN')) return navigateTo('/admin')
    return navigateTo(hasRole('PRO') ? '/pro' : '/app')
  }

  // Espace admin : réservé aux ADMIN, qui n'ont accès à aucun autre espace protégé
  if (hasRole('ADMIN')) {
    if (isAdminRoute) return
    return navigateTo('/admin')
  }

  // Espace client (/app) : USER autorisé, PRO refusé (ADMIN déjà traité ci-dessus)
  if (isAppRoute && hasRole('PRO') && !hasRole('USER')) {
    return navigateTo('/pro')
  }

  // Espace pro (/pro) : PRO autorisé, USER refusé (ADMIN déjà traité ci-dessus)
  if (isProRoute && !hasRole('PRO')) {
    return navigateTo('/app')
  }

  // Espace admin (/admin) : non-ADMIN à ce stade (traité plus haut sinon), donc refusé
  if (isAdminRoute) {
    return navigateTo('/app')
  }
})
