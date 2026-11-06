/**
 * Composable — horaires d'un événement tels qu'affichés : « 19h – 2h », « 19h » (début seul),
 * « jusqu'à 2h » (fin seule), ou null sans horaires. Depuis des dates complètes pour l'événement
 * public (fiche /e/[slug], récap des écrans d'achat), depuis des heures 'HH:mm' pour les
 * paramètres de l'événement.
 */
import { formatClockTime, formatTimeOfDay } from '~/utils/dateUtils'

export function useEventHoursLabel() {
  const { t } = useI18n()

  // Heures déjà formatées (« 19h ») : '' quand absentes.
  function hoursLabel(start: string, end: string): string | null {
    if (start && end) return t('ticketing.event.time-label', { start, end })
    if (start) return start
    if (end) return t('ticketing.event.time-label-until', { end })
    return null
  }

  function eventHoursLabel(startsAt: string | undefined, endsAt: string | undefined): string | null {
    return hoursLabel(formatTimeOfDay(startsAt), formatTimeOfDay(endsAt))
  }

  function clockHoursLabel(startTime: string | undefined, endTime: string | undefined): string | null {
    return hoursLabel(formatClockTime(startTime), formatClockTime(endTime))
  }

  return { eventHoursLabel, clockHoursLabel }
}
