/**
 * Domaine — information pratique libre (détail) d'un prestataire
 */
import type { DetailCategory } from '~/utils/constants'

export interface DetailItem {
  content: string
  category: DetailCategory
}
