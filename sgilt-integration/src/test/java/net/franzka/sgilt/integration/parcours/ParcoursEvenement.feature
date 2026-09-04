Feature: Parcours événement — création, ajout de prestataires, édition, journal

  Un client déjà authentifié crée un événement (indépendamment de l'onboarding) en ciblant un premier
  prestataire, y ajoute un second prestataire, édite les informations de l'événement, puis consulte
  les compteurs et le journal des modifications — avec les vraies règles d'accès : propriétaire de
  l'événement, mais aussi tout prestataire qui y a une réservation (pour le journal uniquement).

  Background:
    * url baseUrl
    * configure retry = { count: 10, interval: 500 }

  Scenario: Création, ajout d'un second prestataire, édition et consultation

    # ── 1. Le client crée un événement ciblant le premier prestataire ────────────────────────────
    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    And request
      """
      {
        prestataireId: '#(prestataireId)', eventType: 'Anniversaire', date: '2027-09-20',
        ville: 'Marseille', nbInvites: '50', prestataireMessage: 'Disponible le 20 septembre ?'
      }
      """
    When method POST
    Then status 201
    * def eventId = response.eventId

    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response[*].id contains eventId

    Given path '/api/v1/user/events/' + eventId
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response.ville == 'Marseille'

    # Conséquence : la création de l'événement a aussi créé sa réservation initiale
    Given path '/api/v1/user/events/' + eventId + '/counts'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response.nouvelleCount == 1

    # Un autre client (pas propriétaire) n'a pas accès à cet événement
    Given path '/api/v1/user/events/' + eventId
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 403

    # Événement inconnu -> 404
    Given path '/api/v1/user/events/00000000-0000-0000-0000-000000000000'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 404

    # ── 2. Le client ajoute un second prestataire au même événement ──────────────────────────────
    Given path '/api/v1/user/events/' + eventId + '/reservations'
    And header Authorization = 'Bearer ' + tokenUser
    And request { prestataireId: '#(prestataireId2)', message: 'Un DJ pour la soirée ?' }
    When method POST
    Then status 201

    Given path '/api/v1/user/events/' + eventId + '/counts'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response.nouvelleCount == 2

    # ── 3. Le client édite l'événement ────────────────────────────────────────────────────────────
    Given path '/api/v1/user/events/' + eventId
    And header Authorization = 'Bearer ' + tokenUser
    And request { title: 'Anniversaire surprise', lieu: 'Salle des fêtes' }
    When method PATCH
    Then status 200
    And match response.title == 'Anniversaire surprise'
    And match response.lieu == 'Salle des fêtes'

    # Conséquence : les modifications sont tracées dans le journal
    Given path '/api/v1/user/events/' + eventId + '/journal'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response.content[0].modifications[*].champ contains 'titre'

    # Conséquence : le prestataire qui a une réservation sur cet événement peut lire le journal
    Given path '/api/v1/user/events/' + eventId + '/journal'
    And header Authorization = 'Bearer ' + tokenPrestataire
    When method GET
    Then status 200

    # Un pro sans aucune réservation sur cet événement ne peut pas lire le journal
    Given path '/api/v1/user/events/' + eventId + '/journal'
    And header Authorization = 'Bearer ' + tokenPro
    When method GET
    Then status 403
