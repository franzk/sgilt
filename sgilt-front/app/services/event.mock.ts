import type { EventDetail, EventPatch } from '~/types/event'
import { MOCK_EVENTS } from '~/services/mock'

export const EventMockService = {
  async getAll(): Promise<EventDetail[]> {
    await new Promise((r) => setTimeout(r, 250))
    return MOCK_EVENTS.map((e) => structuredClone(e))
  },

  async getById(id: string): Promise<EventDetail | null> {
    await new Promise((r) => setTimeout(r, 250))
    const found = MOCK_EVENTS.find((e) => e.id === id)
    return found ? structuredClone(found) : null
  },

  async patchNote(id: string, note: string): Promise<{ updatedAt: string }> {
    await new Promise((r) => setTimeout(r, 300))
    const event = MOCK_EVENTS.find((e) => e.id === id)
    if (event) {
      event.sharedNote = note
      event.sharedNoteUpdatedAt = new Date().toISOString()
    }
    return {
      updatedAt:
        MOCK_EVENTS.find((e) => e.id === id)?.sharedNoteUpdatedAt ?? new Date().toISOString(),
    }
  },

  async patchEvent(id: string, patch: EventPatch): Promise<void> {
    await new Promise((r) => setTimeout(r, 400))
    const event = MOCK_EVENTS.find((e) => e.id === id)
    if (event) Object.assign(event, patch)
  },
}
