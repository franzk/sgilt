package net.franzka.sgilt.core.admin.domain;

/**
 * État d'une invitation client du BO, déduit à la lecture (rien n'est stocké) : tant que le client
 * est INVITED, l'invitation est {@link #NOT_SENT} s'il n'a pas de lien d'activation en attente,
 * {@link #SENT} si son lien est encore valide, {@link #EXPIRED} s'il a expiré (une relance le
 * réarme). Une fois le compte activé, le client sort de la liste des invitations.
 */
public enum ClientInvitationStatus {
    NOT_SENT,
    SENT,
    EXPIRED
}
