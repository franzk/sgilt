package net.franzka.sgilt.core.admin.dto;

import java.util.UUID;

/**
 * La réservation d'un client invité depuis le BO, avec son événement : ce qu'il retrouve en
 * activant son compte.
 *
 * @param eventId       l'identifiant de son événement
 * @param reservationId l'identifiant de sa réservation confirmée
 */
public record InvitedReservation(UUID eventId, UUID reservationId) {}
