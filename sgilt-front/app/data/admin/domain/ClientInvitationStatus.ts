/**
 * Domaine — état d'une invitation client du BO : créée sans mail (NOT_SENT), mail d'invitation
 * envoyé et compte pas encore activé (SENT). Une fois son compte activé, le client sort de la liste.
 */
export type ClientInvitationStatus = 'NOT_SENT' | 'SENT'
