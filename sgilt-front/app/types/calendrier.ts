export type CalendarDayType = 'sgilt' | 'manual'

export interface CalendarEntry {
  date: string // YYYY-MM-DD
  type: CalendarDayType
  reservationId?: string
  label?: string // ex. "Cocktail lancement produit · Thomas B."
}
