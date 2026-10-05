/**
 * Mapper — conversions DTO → domaine pour le module template
 */
import type { RubriqueDto } from '../dto/RubriqueDto'
import type { EventRubrique } from '~/data/evenement/domain/EventRubrique'

export function mapRubrique(dto: RubriqueDto): EventRubrique {
  return {
    key: dto.key,
    reservations: dto.reservations,
  }
}
