/**
 * Composable — construction d'URLs d'images à partir d'un imageId.
 * Utilise la variable d'environnement NUXT_PUBLIC_IMAGE_BASE_URL (défaut : http://localhost:5029).
 */
export function useImageUrl() {
  const config = useRuntimeConfig()
  const imageBaseUrl = (config.public.imageBaseUrl as string) || 'http://localhost:5029'

  function toUrl(imageId: string): string {
    return `${imageBaseUrl}/${imageId}/public`
  }

  return { toUrl }
}
