import type { UseFetchOptions } from 'nuxt/app'

export function useApiFetch<T>(path: string, options?: UseFetchOptions<T>) {
  const { public: { apiUrl } } = useRuntimeConfig()
  const { keycloak, isAuthenticated } = useKeycloak()

  return useFetch<T>(path, {
    baseURL: apiUrl,
    server: false,
    onRequest({ options: fetchOptions }) {
      if (isAuthenticated.value && keycloak.value?.token) {
        fetchOptions.headers = new Headers(fetchOptions.headers as HeadersInit | undefined)
        fetchOptions.headers.set('Authorization', `Bearer ${keycloak.value.token}`)
      }
    },
    ...options,
  })
}

export function apiFetch<T>(path: string, options?: Parameters<typeof $fetch>[1]) {
  const { public: { apiUrl } } = useRuntimeConfig()
  const { keycloak, isAuthenticated } = useKeycloak()

  const headers: Record<string, string> = {}
  if (isAuthenticated.value && keycloak.value?.token) {
    headers['Authorization'] = `Bearer ${keycloak.value.token}`
  }

  return $fetch<T>(path, {
    baseURL: apiUrl,
    headers,
    ...options,
  })
}
