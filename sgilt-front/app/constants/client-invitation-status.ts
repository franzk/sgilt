/**
 * Couleurs des pastilles de statut des invitations client (BO, ClientInvitationStatusBadge).
 */
import type { ClientInvitationStatus } from '~/data/admin/domain/ClientInvitationStatus'

export const CLIENT_INVITATION_STATUS_CONFIG: Record<ClientInvitationStatus, { pillBg: string; pillText: string }> = {
  NOT_SENT: { pillBg: '#E0E0E0', pillText: '#6B6B6B' },
  SENT: { pillBg: '#E67E22', pillText: '#ffffff' },
  ACTIVATED: { pillBg: '#2E7D32', pillText: '#ffffff' },
}
