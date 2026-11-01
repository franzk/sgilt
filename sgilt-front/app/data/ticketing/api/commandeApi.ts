/**
 * Couche API — appels HTTP bruts vers /commandes, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { CommandeDto, CommandeSummaryDto, CreateCommandeRequestDto } from '../dto/CommandeDto'

export async function createCommandeApi(body: CreateCommandeRequestDto): Promise<CommandeDto> {
  return apiFetch<CommandeDto>('/commandes', { method: 'POST', body })
}

export async function getCommandeApi(id: string): Promise<CommandeSummaryDto> {
  return apiFetch<CommandeSummaryDto>(`/commandes/${id}`)
}
