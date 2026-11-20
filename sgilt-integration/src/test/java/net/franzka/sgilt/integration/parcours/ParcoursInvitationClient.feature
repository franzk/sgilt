Feature: Parcours invitation client depuis le BO — création, silence, envoi, activation

  Un client a conclu son deal hors Sgilt avec un prestataire. L'admin crée depuis le BO son compte,
  son événement et sa réservation directement confirmée : le prestataire est prévenu, le client ne
  reçoit rien. Le prestataire dépose ses premiers échanges, toujours sans que le client soit notifié.
  L'admin envoie puis relance l'invitation ; le client active son compte via le lien et atterrit sur
  sa réservation. À partir de là seulement, il reçoit les notifications de son prestataire.

  Background:
    * url baseUrl
    * def clientEmail = 'invite.client@sgilt.test'
    * def eventDate = '2027-08-14'
    * configure retry = { count: 10, interval: 500 }

  Scenario: Création, silence avant activation, envoi, relance, activation et notifications

    # ── 1. L'admin crée l'invitation (email saisi avec des majuscules, téléphone suisse) ────────────
    # Un téléphone invalide est refusé, sans rien créer
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: 'Invite.Client@Sgilt.test', firstName: 'Inès', lastName: 'Invitée', telephone: '12 34',
        eventType: 'mariage', eventDate: '#(eventDate)', prestataireId: '#(prestataireId)'
      }
      """
    When method POST
    Then status 400

    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: 'Invite.Client@Sgilt.test', firstName: 'Inès', lastName: 'Invitée', telephone: '+41790000000',
        eventType: 'mariage', eventDate: '#(eventDate)', prestataireId: '#(prestataireId)'
      }
      """
    When method POST
    Then status 201
    # l'email est enregistré en minuscules, l'invitation n'est pas encore envoyée
    And match response.email == clientEmail
    And match response.status == 'NOT_SENT'
    And match response.prestataireName == 'Studio Test'
    * def invitationId = response.id

    # Le même email, à la casse près, est refusé : conflit avec le compte qui vient d'être créé
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: 'INVITE.CLIENT@sgilt.test', firstName: 'Inès', lastName: 'Invitée',
        eventType: 'mariage', eventDate: '#(eventDate)', prestataireId: '#(prestataireId)'
      }
      """
    When method POST
    Then status 409

    # Conséquence : l'invitation figure dans la liste du BO, non envoyée
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    * def invitation = response.find(i => i.id == invitationId)
    And match invitation.status == 'NOT_SENT'
    And match invitation.linkSentAt == null

    # Conséquence côté sgilt-notifications : le prestataire est prévenu par une notification dédiée
    # (pas le « X a confirmé la réservation » standard)
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And retry until response.items[0].messageKey == 'notification.reservation.added_by_sgilt'
    When method GET
    Then status 200

    # ── 2. Le prestataire retrouve la réservation confirmée et y dépose une note ───────────────────
    Given url baseUrl
    And path '/api/v1/pro/reservations'
    And header Authorization = 'Bearer ' + tokenPrestataire
    When method GET
    Then status 200
    * def reservation = response.find(r => r.datePrestation == eventDate)
    And match reservation.statut == 'confirmee'
    * def reservationId = reservation.id

    Given path '/api/v1/reservations/' + reservationId + '/feed/notes'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { title: 'Bienvenue', content: 'Voici le contrat signé.', isPersonal: false }
    When method POST
    Then status 201

    # ── 3. L'admin envoie l'invitation, puis la relance ────────────────────────────────────────────
    Given path '/api/v1/admin/invitations/' + invitationId + '/send'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request {}
    When method POST
    Then status 204

    # Conséquence : le mail d'invitation est mis en file
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def invitationMail = response.find(m => m.payload.includes('CLIENT_INVITATION_EMAIL') && m.payload.includes(clientEmail))
    And match invitationMail != null

    # Conséquence : l'invitation est envoyée, avec ses dates de lien
    Given url baseUrl
    And path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    * def invitation = response.find(i => i.id == invitationId)
    And match invitation.status == 'SENT'
    And match invitation.linkSentAt == '#notnull'
    And match invitation.linkExpiresAt == '#notnull'

    # Relance : un nouveau mail part, l'invitation reste envoyée
    Given path '/api/v1/admin/invitations/' + invitationId + '/send'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request {}
    When method POST
    Then status 204

    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def resentMail = response.find(m => m.payload.includes('CLIENT_INVITATION_EMAIL') && m.payload.includes(clientEmail))
    And match resentMail != null

    # ── 4. Le client active son compte via le lien (token d'action reconstruit depuis la base) ─────
    * def actionToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildActionTokenForEmail(clientEmail)
    Given url baseUrl
    And path '/api/v1/onboarding/verify'
    And param token = actionToken
    When method GET
    Then status 200
    And match response.email == clientEmail
    * def setPasswordToken = response.setPasswordToken

    Given path '/api/v1/onboarding/confirm-account'
    And request { setPasswordToken: '#(setPasswordToken)', password: 'Test1234!', acceptedTerms: true }
    When method POST
    Then status 200
    # La session SSO l'emmène directement sur sa réservation, retour vers l'event board
    * def redirectUri = decodeURIComponent(response.loginUrl.split('redirect_uri=')[1].split('&')[0])
    And match redirectUri contains '/reservations/' + reservationId + '?from=invitation'

    # Le lien est consommé : il ne sert pas deux fois
    Given path '/api/v1/onboarding/verify'
    And param token = actionToken
    When method GET
    Then status 404

    # Conséquence : le client activé sort de la liste des invitations
    Given path '/api/v1/admin/invitations'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].id !contains invitationId

    # ── 5. Le client se connecte et retrouve sa réservation confirmée ─────────────────────────────
    * def clientToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').fetchTokenForUser(clientEmail, 'Test1234!')
    Given path '/api/v1/user/reservations/' + reservationId
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.status == 'confirmee'

    # Son profil porte le téléphone saisi par l'admin
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.phone == '+41790000000'

    # Il voit la note déposée avant son activation...
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response[*].content contains 'Voici le contrat signé.'

    # ... mais n'en a reçu aucune notification : rien n'est parti vers lui avant l'activation
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.items == []

    # ── 6. Une fois activé, il est notifié des notes de son prestataire ───────────────────────────
    Given url baseUrl
    And path '/api/v1/reservations/' + reservationId + '/feed/notes'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { title: 'Planning', content: 'Le planning du jour J est en ligne.', isPersonal: false }
    When method POST
    Then status 201

    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + clientToken
    And retry until response.items.length == 1
    When method GET
    Then status 200
    And match response.items[0].messageKey == 'notification.reservation.note_added'
