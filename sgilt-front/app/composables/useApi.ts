type ResolvedFetchData<T> = T extends void ? unknown : T
type UseApiFetchOptions<T> = Parameters<typeof useFetch<ResolvedFetchData<T>>>[1]

export function useApiFetch<T = unknown>(path: string, options?: UseApiFetchOptions<T>) {
  const {
    public: { apiUrl },
  } = useRuntimeConfig()
  const { keycloak, isAuthenticated } = useKeycloak()
  const userOnRequest = options?.onRequest

  return useFetch<ResolvedFetchData<T>>(path, {
    ...options,
    baseURL: `${apiUrl}/api/v1`,
    server: false,
    onRequest(context) {
      const { options: fetchOptions } = context
      if (isAuthenticated.value && keycloak.value?.token) {
        fetchOptions.headers = new Headers(fetchOptions.headers)
        fetchOptions.headers.set('Authorization', `Bearer ${keycloak.value.token}`)
      }

      if (typeof userOnRequest === 'function') {
        return userOnRequest(context)
      }
    },
  })
}

export function apiFetch<T>(path: string, options?: Parameters<typeof $fetch>[1]) {
  const {
    public: { apiUrl },
  } = useRuntimeConfig()
  const { keycloak, isAuthenticated } = useKeycloak()

  const headers: Record<string, string> = {}
  if (isAuthenticated.value && keycloak.value?.token) {
    headers['Authorization'] = `Bearer ${keycloak.value.token}`
  }

  return $fetch<T>(path, {
    baseURL: `${apiUrl}/api/v1`,
    headers,
    ...options,
  })
}
