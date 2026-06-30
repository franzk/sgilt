import type { Media } from '~/data/prestataire/domain/Media'

/**
 * Retourne le ref du média en position minimale (image hero), ou null si le tableau est vide.
 */
export function heroRef(medias: Media[]): string | null {
  if (!medias.length) return null
  return medias.find((m) => m.position === 0)?.ref ?? null
}

/**
 * Retourne l'URL d'embed YouTube à partir d'un ID vidéo, ou chaîne vide si null.
 */
export function youtubeEmbedUrl(id: string | null): string {
  if (!id) return ''
  return `https://www.youtube.com/embed/${id}?autoplay=1`
}
