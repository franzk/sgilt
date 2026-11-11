/**
 * Composable — libellés des types d'événement, tirés d'i18n (event-picker.types.*.label) : la liste
 * des choix de type (éditeur des informations) et le libellé d'un type tel qu'affiché. Les types
 * eux-mêmes viennent de la liste unique EVENT_TYPES (utils/eventTypes.ts).
 */

import { computed, type ComputedRef } from 'vue'
import { choiceLabel } from '~/composables/useLocalEvent'
import type { EvenementOption } from '~/types/evenement'
import { EVENT_TYPES } from '~/utils/eventTypes'

export function useEventTypeLabel(): {
  eventTypeOptions: ComputedRef<EvenementOption[]>
  eventTypeLabel: (eventType: string | null | undefined) => string | null
} {
  const { t } = useI18n()

  const eventTypeOptions = computed<EvenementOption[]>(() =>
    EVENT_TYPES.map((type) => ({
      value: type.key,
      label: t(`event-picker.types.${type.key}.label`),
      emoji: type.emoji,
    })),
  )

  // Libellé du type de la liste, sinon le texte libre saisi sous « Autre » ; null si aucun type.
  function eventTypeLabel(eventType: string | null | undefined): string | null {
    return choiceLabel(eventTypeOptions.value, eventType ?? null)
  }

  return { eventTypeOptions, eventTypeLabel }
}
