package net.franzka.sgilt.core.reservation.event;

/**
 * Rôle de l'acteur à l'origine d'un évènement de domaine — porté explicitement dans le payload
 * plutôt que déduit implicitement du statut/de la routing key, pour rester lisible côté consommateur
 * (et exploitable telle quelle par une future notification admin, cf. chantier notifications point C).
 */
public enum ActorRole {
    USER,
    PRO,
    ADMIN
}
