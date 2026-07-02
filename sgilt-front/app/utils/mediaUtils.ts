import type { Media } from '~/data/prestataire/domain/Media'

/**
 * Retourne le ref du média en position minimale (image hero), ou null si le tableau est vide.
 */
export function heroRef(medias: Media[]): string | null {
  if (!medias.length) return null
  return medias.find((m) => m.position === 0)?.ref ?? null
}

/**
 * Retourne l'URL de visionnage YouTube à partir d'un ID vidéo, ou chaîne vide si null.
 */
export function youtubeWatchUrl(id: string | null): string {
  if (!id) return ''
  return `https://www.youtube.com/watch?v=${id}`
}

/**
 * Retourne l'URL d'embed YouTube à partir d'un ID vidéo, ou chaîne vide si null.
 */
export function youtubeEmbedUrl(id: string | null): string {
  if (!id) return ''
  return `https://www.youtube.com/embed/${id}?autoplay=1`
}

/**
 * Extrait l'ID YouTube depuis une URL (watch?v=, youtu.be/, embed/) ou un ID brut de 11 chars.
 * Retourne null si le format n'est pas reconnu.
 */
export function youtubeThumbnailUrl(id: string): string {
  return `https://img.youtube.com/vi/${id}/hqdefault.jpg`
}

export function extractYoutubeId(input: string): string | null {
  const s = input.trim()
  const patterns = [
    /[?&]v=([A-Za-z0-9_-]{11})/, // youtube.com/watch?v=ID
    /youtu\.be\/([A-Za-z0-9_-]{11})/, // youtu.be/ID
    /embed\/([A-Za-z0-9_-]{11})/, // youtube.com/embed/ID
  ]
  for (const re of patterns) {
    const m = s.match(re)
    if (m?.[1]) return m[1]
  }
  if (/^[A-Za-z0-9_-]{11}$/.test(s)) return s
  return null
}
