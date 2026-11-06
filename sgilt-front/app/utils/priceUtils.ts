/**
 * Utilitaires d'affichage des prix (montants en euros TTC), utilisés notamment par le parcours
 * d'achat de billets.
 */

/**
 * Formate un montant en euros, en français. Ex : 12 → "12 €", 12.5 → "12,50 €"
 * Les centimes ne sont affichés que s'il y en a.
 */
export function formatPrice(amount: number): string {
  return amount.toLocaleString('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: Number.isInteger(amount) ? 0 : 2,
  })
}
