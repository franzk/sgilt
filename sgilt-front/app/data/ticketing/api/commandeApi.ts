/**
 * Couche API — appels HTTP bruts vers /commandes, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { CommandeDto, CommandeSummaryDto, CreateCommandeRequestDto, EBilletDto } from '../dto/CommandeDto'

export async function createCommandeApi(body: CreateCommandeRequestDto): Promise<CommandeDto> {
  return apiFetch<CommandeDto>('/commandes', { method: 'POST', body })
}

export async function getCommandeApi(id: string): Promise<CommandeSummaryDto> {
  return apiFetch<CommandeSummaryDto>(`/commandes/${id}`)
}

export async function getEBilletApi(id: string): Promise<EBilletDto> {
  return apiFetch<EBilletDto>(`/commandes/${id}/e-billet`)
}

// URL absolue du PDF de l'e-billet, pour un simple lien : route publique, le navigateur la
// télécharge sans header d'authentification (Content-Disposition: attachment côté back).
export function eBilletPdfUrlApi(id: string): string {
  const {
    public: { apiUrl },
  } = useRuntimeConfig()
  return `${apiUrl}/api/v1/commandes/${id}/e-billet.pdf`
}
