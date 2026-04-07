/**
 * Sérialise une Date en chaîne ISO (YYYY-MM-DD).
 * Retourne une chaîne vide si la valeur est null ou undefined.
 */
export function toISODate(d: Date | null | undefined): string {
  if (!d) return ''
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

/**
 * Formate une date en français, mois long. Ex : "14 septembre 2026"
 * Accepte Date, ISO string, null ou undefined. Retourne '' si absent.
 */
export function formatDate(d: Date | string | null | undefined): string {
  if (!d) return ''
  return new Date(d).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

/**
 * Formate une date en français, mois abrégé. Ex : "14 sept. 2026"
 * Accepte Date, ISO string, null ou undefined. Retourne '' si absent.
 */
export function formatDateShort(d: Date | string | null | undefined): string {
  if (!d) return ''
  return new Date(d).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

/**
 * Formate une date+heure en français. Ex : "14 sept. 2026 à 09:30"
 * Accepte Date, ISO string, null ou undefined. Retourne '' si absent.
 */
export function formatDateTime(d: Date | string | null | undefined): string {
  if (!d) return ''
  return new Date(d).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
