/**
 * Libellé du lieu d'un événement à partir de son lieu et de sa ville, tous deux facultatifs : la
 * fiche (/e/[slug]), le récapitulatif du parcours d'achat, l'e-billet et l'export calendrier.
 */

/**
 * Réunit le lieu et la ville présents : « Le Taennel · Strasbourg », « Strasbourg », « Le Taennel »,
 * ou '' sans l'un ni l'autre (pas de séparateur qui traîne).
 */
export function placeLabel(venue?: string, city?: string, separator = ' · '): string {
  return [venue, city].filter(Boolean).join(separator)
}
