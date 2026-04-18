import type { UseFetchOptions } from 'nuxt/app'

export function useApiFetch<T>(path: string, options?: UseFetchOptions<T>) {
  const { public: { apiUrl } } = useRuntimeConfig()
  return useFetch<T>(path, {
    baseURL: apiUrl,
    server: false,
    ...options,
  })
}

export function apiFetch<T>(path: string, options?: Parameters<typeof $fetch>[1]) {
  const { public: { apiUrl } } = useRuntimeConfig()
  return $fetch<T>(path, {
    baseURL: apiUrl,
    ...options,
  })
}
