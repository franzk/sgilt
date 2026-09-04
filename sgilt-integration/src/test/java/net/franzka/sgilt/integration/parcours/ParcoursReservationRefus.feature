Feature: Parcours réservation — le prestataire refuse une demande

  Le client crée un événement (donc une réservation NEW). Le prestataire ciblé la refuse avant tout
  contact — vraie branche distincte du parcours confirmation/annulation déjà couvert ailleurs.
  Vérifie le statut réel, la note système générée dans le feed, et les transitions bloquées ensuite.

  Background:
    * url baseUrl
    * configure retry = { count: 10, interval: 500 }

  Scenario: Le prestataire refuse une demande avant tout contact — NEW -> REFUSED_PRE_CONTACT

    # ── 1. Le client crée un événement ciblant le prestataire ────────────────────────────────────
    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    And request { prestataireId: '#(prestataireId)', eventType: 'Anniversaire', date: '2027-11-08', ville: 'Nantes' }
    When method POST
    Then status 201
    * def eventId = response.eventId

    Given path '/api/v1/user/reservations'
    And header Authorization = 'Bearer ' + tokenUser
    And param eventId = eventId
    When method GET
    Then status 200
    * def reservationId = response[0].id
    And match response[0].status == 'nouvelle'

    # ── 2. Le prestataire (propriétaire de la fiche ciblée) refuse la demande ─────────────────────
    Given path '/api/v1/pro/reservations/' + reservationId + '/refuse'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { reason: 'Déjà réservé ce jour-là' }
    When method POST
    Then status 204

    Given path '/api/v1/user/reservations/' + reservationId
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response.status == 'refusee'

    # Conséquence : une note système "refused" apparaît dans le feed, avec le motif
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response[*].generatedKey contains 'feed.system.refused'
    And match response[*].content contains 'Déjà réservé ce jour-là'

    # Conséquence côté sgilt-notifications : le client (pas l'auteur, le prestataire) est notifié du refus
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenUser
    And retry until response.items[0].messageKey == 'notification.reservation.status.refused_pre_contact'
    When method GET
    Then status 200
    And match response.items[0].type == 'state_change'

    Given url baseUrl

    # Conséquence : impossible de refuser à nouveau une réservation déjà refusée (409, pas 204)
    Given path '/api/v1/pro/reservations/' + reservationId + '/refuse'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { reason: 'Nouvelle tentative' }
    When method POST
    Then status 409

    # Conséquence : impossible de confirmer une réservation refusée
    Given path '/api/v1/user/reservations/' + reservationId + '/confirm'
    And header Authorization = 'Bearer ' + tokenUser
    When method POST
    Then status 409

    # Un pro tiers (pas propriétaire de cette fiche) ne peut pas refuser à sa place
    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    And request { prestataireId: '#(prestataireId)', eventType: 'Anniversaire', date: '2027-11-09', ville: 'Nantes' }
    When method POST
    Then status 201
    * def secondEventId = response.eventId

    Given path '/api/v1/user/reservations'
    And header Authorization = 'Bearer ' + tokenUser
    And param eventId = secondEventId
    When method GET
    Then status 200
    * def secondReservationId = response[0].id

    Given path '/api/v1/pro/reservations/' + secondReservationId + '/refuse'
    And header Authorization = 'Bearer ' + tokenPro
    And request { reason: 'Usurpation' }
    When method POST
    Then status 403
