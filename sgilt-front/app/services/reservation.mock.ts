import type { ReservationDetail, ReservationNote } from '~/types/event'
import { MOCK_RESERVATIONS, SOPHIE } from '~/services/mock'

export const ReservationMockService = {
  async getById(reservationId: string): Promise<ReservationDetail | null> {
    await new Promise((r) => setTimeout(r, 200))
    return MOCK_RESERVATIONS.find((r) => r.id === reservationId) ?? null
  },

  async addNote(
    reservationId: string,
    content: string,
    isPersonal: boolean = false,
  ): Promise<ReservationNote> {
    await new Promise((r) => setTimeout(r, 300))
    const note: ReservationNote = {
      id: `note-${Date.now()}`,
      author: SOPHIE,
      content,
      createdAt: new Date().toISOString(),
      isPersonal,
    }
    const reservation = MOCK_RESERVATIONS.find((r) => r.id === reservationId)
    if (reservation) reservation.notes.push(note)
    return note
  },
}
