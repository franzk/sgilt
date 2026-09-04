Feature: Parcours réservation — le client annule sa propre demande

  Le client crée un événement (donc une réservation NEW) puis se ravise et l'annule lui-même —
  distinct du parcours d'annulation par le prestataire déjà couvert ailleurs. Vérifie le statut réel,
  la note système générée (motif potentiellement privé), et les transitions bloquées ensuite.

  Background:
    * url baseUrl
    * configure retry = { count: 10, interval: 500 }

  Scenario: Le client annule sa demande avant tout contact — NEW -> CANCELED_BY_CLIENT_PRE_CONTACT

    # ── 1. Le client crée un événement ciblant le prestataire ────────────────────────────────────
    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    And request { prestataireId: '#(prestataireId)', eventType: 'Anniversaire', date: '2027-11-15', ville: 'Bordeaux' }
    When method POST
    Then status 201
    * def eventId = response.eventId

    Given path '/api/v1/user/reservations'
    And header Authorization = 'Bearer ' + tokenUser
    And param eventId = eventId
    When method GET
    Then status 200
    * def reservationId = response[0].id

    # ── 2. Le client annule sa propre demande ─────────────────────────────────────────────────────
    Given path '/api/v1/user/reservations/' + reservationId + '/cancel'
    And header Authorization = 'Bearer ' + tokenUser
    And request { reason: 'Finalement je pars ailleurs', isPersonal: true }
    When method POST
    Then status 204

    Given path '/api/v1/user/reservations/' + reservationId
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response.status == 'annulee'

    # Conséquence : une note système "cancelled" apparaît dans le feed du client (auteur)
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response[*].generatedKey contains 'feed.system.cancelled'

    # Conséquence côté sgilt-notifications : le prestataire (pas l'auteur, le client) est notifié
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And retry until response.items[0].messageKey == 'notification.reservation.status.canceled_by_client_pre_contact'
    When method GET
    Then status 200
    And match response.items[0].type == 'state_change'

    Given url baseUrl

    # Conséquence : impossible d'annuler à nouveau une réservation déjà annulée (409, pas 204)
    Given path '/api/v1/user/reservations/' + reservationId + '/cancel'
    And header Authorization = 'Bearer ' + tokenUser
    And request { reason: 'Encore', isPersonal: false }
    When method POST
    Then status 409

    # Conséquence : impossible de marquer comme contactée une réservation annulée
    Given path '/api/v1/user/reservations/' + reservationId + '/mark-contacted'
    And header Authorization = 'Bearer ' + tokenUser
    When method POST
    Then status 409

    # Un autre client (pas propriétaire de l'événement) ne peut pas annuler à sa place
    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    And request { prestataireId: '#(prestataireId)', eventType: 'Anniversaire', date: '2027-11-16', ville: 'Bordeaux' }
    When method POST
    Then status 201
    * def secondEventId = response.eventId

    Given path '/api/v1/user/reservations'
    And header Authorization = 'Bearer ' + tokenUser
    And param eventId = secondEventId
    When method GET
    Then status 200
    * def secondReservationId = response[0].id

    Given path '/api/v1/user/reservations/' + secondReservationId + '/cancel'
    And header Authorization = 'Bearer ' + tokenUser2
    And request { reason: 'Usurpation', isPersonal: false }
    When method POST
    Then status 403
