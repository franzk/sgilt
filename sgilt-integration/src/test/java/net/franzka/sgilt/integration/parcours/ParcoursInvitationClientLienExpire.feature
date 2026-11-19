Feature: Parcours invitation client — lien d'activation expiré, puis relance

  L'admin envoie l'invitation, mais le client laisse passer la date de validité du lien. Le BO
  affiche l'invitation expirée, le lien est refusé (410). Une relance réarme le même lien : le client
  peut alors activer son compte normalement.

  Background:
    * url baseUrl
    * def clientEmail = 'invite.expire@sgilt.test'
    * def eventDate = '2027-09-18'

  Scenario: Lien expiré, affiché comme tel, refusé, puis réarmé par une relance

    # ── 1. L'admin crée et envoie l'invitation ───────────────────────────────────────────────────
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: '#(clientEmail)', firstName: 'Paul', lastName: 'Pressé',
        eventType: 'anniversaire', eventDate: '#(eventDate)', prestataireId: '#(prestataireId)'
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

    # ── 2. Le lien expire ─────────────────────────────────────────────────────────────────────────
    * Java.type('net.franzka.sgilt.integration.IntegrationTestContext').expireActionTokenForEmail(clientEmail)

    # Conséquence : le BO affiche l'invitation expirée, avec ses dates
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    * def invitation = response.find(i => i.id == invitationId)
    And match invitation.status == 'EXPIRED'
    And match invitation.linkExpiresAt == '#notnull'

    # Conséquence : le lien est refusé (410) ; le front conseille alors « mot de passe oublié »
    * def actionToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildActionTokenForEmail(clientEmail)
    Given path '/api/v1/onboarding/verify'
    And param token = actionToken
    When method GET
    Then status 410

    # ── 3. L'admin relance : le même lien est réarmé ─────────────────────────────────────────────
    Given path '/api/v1/admin/invitations/' + invitationId + '/send'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request {}
    When method POST
    Then status 204

    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    * def invitation = response.find(i => i.id == invitationId)
    And match invitation.status == 'SENT'

    # ── 4. Le même lien fonctionne de nouveau : le client active son compte ──────────────────────
    Given path '/api/v1/onboarding/verify'
    And param token = actionToken
    When method GET
    Then status 200
    * def setPasswordToken = response.setPasswordToken

    Given path '/api/v1/onboarding/confirm-account'
    And request { setPasswordToken: '#(setPasswordToken)', password: 'Test1234!', acceptedTerms: true }
    When method POST
    Then status 200

    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].id !contains invitationId
