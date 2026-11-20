Feature: Parcours invitation client — activation par « mot de passe oublié », sans le lien

  Le client invité ne passe pas par le lien de son invitation (expiré, perdu…) : il choisit un mot de
  passe via « mot de passe oublié » sur Keycloak, puis se connecte. Sa première connexion
  (GET /users/me, appelé par le front à chaque connexion) active son compte : il sort de la liste
  des invitations et reçoit désormais les notifications de son prestataire.

  Background:
    * url baseUrl
    * def clientEmail = 'invite.oubli@sgilt.test'
    * def eventDate = '2027-10-09'
    * configure retry = { count: 10, interval: 500 }

  Scenario: Connexion sans le lien, activation à la première connexion, puis notifications

    # ── 1. L'admin crée et envoie l'invitation ───────────────────────────────────────────────────
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: '#(clientEmail)', firstName: 'Olga', lastName: 'Oublieuse',
        eventType: 'mariage', eventDate: '#(eventDate)', prestataireId: '#(prestataireId)'
      }
      """
    When method POST
    Then status 201
    * def invitationId = response.id

    Given path '/api/v1/admin/invitations/' + invitationId + '/send'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request {}
    When method POST
    Then status 204

    # ── 2. Le client choisit un mot de passe via Keycloak, sans le lien, et se connecte ──────────
    * Java.type('net.franzka.sgilt.integration.IntegrationTestContext').setPasswordForUser(clientEmail, 'Oubli1234!')
    * def clientToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').fetchTokenForUser(clientEmail, 'Oubli1234!')

    # Tant qu'il n'a pas chargé son profil, il est toujours invité
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].id contains invitationId

    # ── 3. Première connexion : le front charge son profil, ce qui active son compte ─────────────
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.email == clientEmail

    # Conséquence : il sort de la liste des invitations
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].id !contains invitationId

    # Un second chargement du profil n'a pas d'autre effet
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200

    # ── 4. Désormais actif, il est notifié des notes de son prestataire ──────────────────────────
    Given path '/api/v1/pro/reservations'
    And header Authorization = 'Bearer ' + tokenPrestataire
    When method GET
    Then status 200
    * def reservationId = response.find(r => r.datePrestation == eventDate).id

    Given path '/api/v1/reservations/' + reservationId + '/feed/notes'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { title: 'Devis', content: 'Le devis signé est en ligne.', isPersonal: false }
    When method POST
    Then status 201

    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + clientToken
    And retry until response.items.length == 1
    When method GET
    Then status 200
    And match response.items[0].messageKey == 'notification.reservation.note_added'
