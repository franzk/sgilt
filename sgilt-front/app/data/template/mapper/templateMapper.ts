/**
 * Mapper — conversions DTO → domaine pour le module template
 */
import type { RubriqueContenuDto } from '../dto/RubriqueContenuDto'
import type { RubriqueDto } from '../dto/RubriqueDto'
import type { RubriqueContenu } from '../domain/RubriqueContenu'
import type { EventRubrique } from '~/data/evenement/domain/EventRubrique'

export function mapRubrique(dto: RubriqueDto): EventRubrique {
  return {
    key: dto.key,
    reservations: dto.reservations,
  }
}

export function mapRubriqueContenu(dto: RubriqueContenuDto): RubriqueContenu {
  return {
    categories: dto.categories,
    subcategories: dto.subcategories,
  }
}
