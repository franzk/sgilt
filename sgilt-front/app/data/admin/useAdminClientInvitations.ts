/**
 * Composable — invitations client du back-office : création du client avec son événement et sa
 * réservation confirmée (/admin/inviter-client), liste des invitations, envoi et relance du mail
 * d'invitation (/admin/invitations). Un échec est exposé dans `failure` (chargement de la liste),
 * `createFailure` (création) ou `sendErrorId` (envoi, avec l'invitation concernée).
 */
import {
  createClientInvitation,
  fetchClientInvitations,
  sendClientInvitation,
  toClientInvitationFailure,
} from './service/adminService'
import type { ClientInvitation, ClientInvitationCreation } from './domain/ClientInvitation'

// État propre à chaque appel : chaque écran repart d'un état vierge (pas de message resté affiché
// en revenant sur la page).
export function useAdminClientInvitations() {
  const rows = ref<ClientInvitation[]>([])
  const loading = ref(false)
  // Motif de l'échec du chargement (suffixe de la clé i18n admin.invitations.errors.*), ou null.
  const failure = ref<string | null>(null)
  const creating = ref(false)
  // Motif de l'échec de la création (suffixe de la clé i18n admin.invitations.form.errors.*), ou null.
  const createFailure = ref<string | null>(null)
  const lastCreatedEmail = ref<string | null>(null)
  const sendingId = ref<string | null>(null)
  const sendErrorId = ref<string | null>(null)
  const sentId = ref<string | null>(null)

  async function load() {
    loading.value = true
    failure.value = null
    try {
      rows.value = await fetchClientInvitations()
    } catch {
      failure.value = 'TECHNICAL'
    } finally {
      loading.value = false
    }
  }

  async function create(input: ClientInvitationCreation): Promise<boolean> {
    creating.value = true
    createFailure.value = null
    lastCreatedEmail.value = null
    try {
      await createClientInvitation(input)
      lastCreatedEmail.value = input.email
      return true
    } catch (error) {
      createFailure.value = toClientInvitationFailure(error)
      return false
    } finally {
      creating.value = false
    }
  }

  // Refus détecté par l'écran (formulaire incomplet), sans appel au back.
  function failCreate(reason: string) {
    lastCreatedEmail.value = null
    createFailure.value = reason
  }

  async function send(id: string): Promise<void> {
    sendingId.value = id
    sendErrorId.value = null
    sentId.value = null
    try {
      await sendClientInvitation(id)
      sentId.value = id
      await load()
    } catch {
      sendErrorId.value = id
    } finally {
      sendingId.value = null
    }
  }

  return {
    rows,
    loading,
    failure,
    load,
    create,
    creating,
    createFailure,
    failCreate,
    lastCreatedEmail,
    send,
    sendingId,
    sendErrorId,
    sentId,
  }
}
