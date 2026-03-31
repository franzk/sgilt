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
