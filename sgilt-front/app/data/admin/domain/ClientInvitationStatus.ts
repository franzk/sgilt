/**
 * Domaine — état d'une invitation client du BO : créée sans mail (NOT_SENT), mail d'invitation
 * envoyé avec un lien encore valide (SENT), lien expiré (EXPIRED, une relance le réarme). Une fois
 * son compte activé, le client sort de la liste.
 */
export type ClientInvitationStatus = 'NOT_SENT' | 'SENT' | 'EXPIRED'
