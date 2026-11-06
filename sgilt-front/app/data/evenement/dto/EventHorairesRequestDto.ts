/**
 * DTO — corps de la requête PUT /events/:id/horaires : les deux heures ('HH:mm') remplacées à la
 * fois, null pour en effacer une.
 */
export interface EventHorairesRequestDto {
  startTime: string | null
  endTime: string | null
}
