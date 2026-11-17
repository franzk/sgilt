/**
 * Domaine — état d'une invitation client du BO : créée sans mail (NOT_SENT), mail d'invitation
 * envoyé et compte pas encore activé (SENT), compte activé par le client (ACTIVATED).
 */
export type ClientInvitationStatus = 'NOT_SENT' | 'SENT' | 'ACTIVATED'
