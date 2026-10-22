/**
 * Vérifie le format d'une adresse email (contrôle de forme, pas d'existence).
 */
export function validateEmail(v: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim())
}

/**
 * Vérifie un numéro de téléphone : 7 à 15 chiffres, séparateurs usuels ignorés.
 * Même règle que la validation back (InitOnboardingRequest.telephone).
 */
export function validatePhone(v: string): boolean {
  const digits = v.replace(/[\s\-.()\/+]/g, '')
  return /^\d{7,15}$/.test(digits)
}
