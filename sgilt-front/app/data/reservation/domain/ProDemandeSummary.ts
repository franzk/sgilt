/**
 * Domaine — résumé d'une demande pour le tableau de bord pro
 */
import type { ReservationStatus } from './ReservationStatus'

export interface ProDemandeSummary {
  id: string
  titre: string
  date: Date
  dateIso: string
  dateReception: Date
  statut: ReservationStatus
  ligneContextuelle: string
  urgencyLevel: number
  coverImage?: string
  progressType: 'deadline' | 'duration' | 'temporal' | null
  progressValue: number | null
  phraseInfoState: string | null
  unreadNotesCount: number
}
