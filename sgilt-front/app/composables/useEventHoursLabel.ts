/**
 * Composable — horaires d'un événement public tels qu'affichés, sur la fiche (/e/[slug]) et le
 * récap des écrans d'achat : « 19h – 2h », « 19h » (début seul), « jusqu'à 2h » (fin seule), ou
 * null sans horaires.
 */
import { formatTimeOfDay } from '~/utils/dateUtils'

export function useEventHoursLabel() {
  const { t } = useI18n()

  function eventHoursLabel(startsAt: string | undefined, endsAt: string | undefined): string | null {
    const start = formatTimeOfDay(startsAt)
    const end = formatTimeOfDay(endsAt)
    if (start && end) return t('ticketing.event.time-label', { start, end })
    if (start) return start
    if (end) return t('ticketing.event.time-label-until', { end })
    return null
  }

  return { eventHoursLabel }
}
