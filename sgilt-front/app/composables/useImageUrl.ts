/**
 * Composable — construction d'URLs d'images à partir d'un imagePath.
 * Utilise la variable d'environnement NUXT_PUBLIC_IMAGE_BASE_URL (défaut : http://localhost:5029).
 */
export function useImageUrl() {
  const config = useRuntimeConfig()
  const imageBaseUrl = config.public.imageBaseUrl || 'http://localhost:5029'

  function toUrl(imagePath: string): string {
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath
    }
    return `${imageBaseUrl}/${imagePath}`
  }

  return { toUrl }
}
