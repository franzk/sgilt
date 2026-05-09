import { defineStore } from 'pinia'
import type { JournalEntry } from '~/data/evenement/domain/JournalEntry'

export const useEventJournalStore = defineStore('eventJournal', () => {
  const journal = ref<JournalEntry[]>([])

  function load(entries: JournalEntry[]) {
    journal.value = entries
  }

  return { journal, load }
})
