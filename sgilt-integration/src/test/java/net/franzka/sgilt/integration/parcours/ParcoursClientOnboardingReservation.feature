Feature: Parcours client — de la demande d'onboarding au cycle de vie complet d'une réservation

  Un client anonyme soumet une demande de réservation à un prestataire publié, confirme son compte
  par email, puis fait vivre sa réservation (contact, confirmation, échange de message, annulation
  par le prestataire) — en vérifiant à chaque étape les vraies conséquences métier (statut, feed,
  mails effectivement mis en file), pas seulement le code HTTP de chaque appel isolé.

  Background:
    * url baseUrl
    * def clientEmail = 'camille.client@sgilt.test'
    # sgilt-notifications consomme les évènements de domaine de façon asynchrone (RabbitMQ) — les
    # vérifications de notification retentent jusqu'à 10 fois (5s max) avant d'échouer.
    * configure retry = { count: 10, interval: 500 }

  Scenario: Onboarding, cycle de vie de la réservation et ses conséquences réelles

    # ── 1. Demande initiale de réservation à un prestataire publié ──────────────────────────────
    Given path '/api/v1/onboarding'
    And request
      """
      {
        firstName: 'Camille', lastName: 'Client', email: '#(clientEmail)',
        prestataireId: '#(prestataireId)', eventType: 'Mariage', date: '2027-06-12',
        ville: 'Lyon', telephone: '0612345678', prestataireMessage: 'Disponible le 12 juin ?'
      }
      """
    When method POST
    Then status 202
    And match response.email == clientEmail

    # Conséquence : le mail de vérification a bien été mis en file d'attente pour sgilt-mailer.
    # La queue mail.send est partagée avec sgilt-notifications (autre producteur, ex. notifications
    # de réservation) : on vide un lot et on cherche le message attendu, sans supposer sa position.
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def verificationMail = response.find(m => m.payload.includes('VERIFICATION_EMAIL') && m.payload.includes(clientEmail))
    And match verificationMail != null

    # ── 2. Le client se ravise et resoumet une demande au même prestataire ───────────────────────
    # capture du token de la 1ère session avant qu'elle soit annulée par la resoumission
    * def firstSessionToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildConfirmationTokenForEmail(clientEmail)

    Given url baseUrl
    And path '/api/v1/onboarding'
    And request
      """
      {
        firstName: 'Camille', lastName: 'Client', email: '#(clientEmail)',
        prestataireId: '#(prestataireId)', eventType: 'Mariage', date: '2027-06-12', ville: 'Lyon'
      }
      """
    When method POST
    Then status 202
    # le mail de vérification de cette 2e session est mis en file à son tour
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def secondVerificationMail = response.find(m => m.payload.includes('VERIFICATION_EMAIL') && m.payload.includes(clientEmail))
    And match secondVerificationMail != null

    # Conséquence réelle : le token de la 1ère session (désormais annulée par la resoumission) est
    # refusé — 403, la session n'est plus utilisable même si le lien email est encore dans une boîte mail
    Given url baseUrl
    And path '/api/v1/onboarding/verify'
    And param token = firstSessionToken
    When method GET
    Then status 403

    # ── 3. Vérification de l'email avec le token de la session courante (la 2e) ──────────────────
    * def currentToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildConfirmationTokenForEmail(clientEmail)
    Given path '/api/v1/onboarding/verify'
    And param token = currentToken
    When method GET
    Then status 200
    And match response.email == clientEmail
    * def setPasswordToken = response.setPasswordToken

    # Un token à la signature invalide (altéré/forgé) est rejeté — 400
    Given path '/api/v1/onboarding/verify'
    And param token = 'inconnu-0000000000000000'
    When method GET
    Then status 400

    # Un token correctement signé mais dont la session n'existe pas retourne 404
    * def unknownButValidToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildConfirmationTokenForPayload('ce-payload-n-existe-pas-en-base')
    Given path '/api/v1/onboarding/verify'
    And param token = unknownButValidToken
    When method GET
    Then status 404

    # ── 4. Confirmation du compte : crée le compte Keycloak + Utilisateur + Evenement + Reservation
    Given path '/api/v1/onboarding/confirm-account'
    And request { setPasswordToken: '#(setPasswordToken)', password: 'Test1234!', acceptedTerms: true }
    When method POST
    Then status 200
    And match response.loginUrl == '#present'

    # Conséquence : le mail de bienvenue a bien été mis en file
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def welcomeMail = response.find(m => m.payload.includes('WELCOME_EMAIL') && m.payload.includes(clientEmail))
    And match welcomeMail != null

    # Rejouer la confirmation avec le même setPasswordToken échoue : la session a été consommée
    Given url baseUrl
    And path '/api/v1/onboarding/confirm-account'
    And request { setPasswordToken: '#(setPasswordToken)', password: 'Autre1234!', acceptedTerms: true }
    When method POST
    Then status 404

    # ── 5. Le client se connecte réellement (vrai JWT KC) et retrouve sa réservation à l'état NEW ─
    * def clientToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').fetchTokenForUser(clientEmail, 'Test1234!')
    Given path '/api/v1/user/reservations/active'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.items[0].status == 'nouvelle'
    And match response.hasConfirmed == false
    * def reservationId = response.items[0].reservationId

    # ── 6. Le client marque la réservation comme contactée : NEW -> IN_DISCUSSION ────────────────
    Given path '/api/v1/user/reservations/' + reservationId + '/mark-contacted'
    And header Authorization = 'Bearer ' + clientToken
    When method POST
    Then status 204

    Given path '/api/v1/user/reservations/' + reservationId
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.status == 'en_discussion'

    # Conséquence : une note système "contacted" apparaît dans le feed, lisible par le client
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response[*].generatedKey contains 'feed.system.contacted'

    # Conséquence côté sgilt-notifications : le prestataire (pas le client, qui est l'auteur de
    # l'action) est notifié du changement de statut
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And retry until response.items[0].messageKey == 'notification.reservation.status.in_discussion'
    When method GET
    Then status 200
    And match response.items[0].type == 'state_change'
    And match response.items[0].read == false

    # Un pro tiers (pas propriétaire de cette fiche) ne peut ni voir ni agir sur cette réservation
    Given url baseUrl
    And path '/api/v1/pro/reservations/' + reservationId
    And header Authorization = 'Bearer ' + tokenPro
    When method GET
    Then status 403

    # ── 7. Le client confirme : IN_DISCUSSION -> CONFIRMED ───────────────────────────────────────
    Given path '/api/v1/user/reservations/' + reservationId + '/confirm'
    And header Authorization = 'Bearer ' + clientToken
    When method POST
    Then status 204

    Given path '/api/v1/user/reservations/active'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.hasConfirmed == true

    # Conséquence côté sgilt-notifications : le prestataire est notifié de la confirmation
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And retry until response.items[0].messageKey == 'notification.reservation.status.confirmed'
    When method GET
    Then status 200

    # Transition invalide : NEW/IN_DISCUSSION -> CONFIRMED déjà fait, reconfirmer échoue (409)
    Given url baseUrl
    And path '/api/v1/user/reservations/' + reservationId + '/confirm'
    And header Authorization = 'Bearer ' + clientToken
    When method POST
    Then status 409

    # ── 8. Le vrai prestataire ciblé (propriétaire de la fiche) échange un message sur le feed ────
    Given path '/api/v1/reservations/' + reservationId + '/feed/notes'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { title: 'Confirmation', content: 'Parfait, on se voit le jour J !', isPersonal: false }
    When method POST
    Then status 201
    And match response.content == 'Parfait, on se voit le jour J !'
    And match response.authorRole == 'prestataire'

    # Le client voit bien ce message dans son propre feed
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response[*].content contains 'Parfait, on se voit le jour J !'

    # Conséquence côté sgilt-notifications : le client (pas l'auteur, le prestataire) est notifié
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + clientToken
    And retry until response.items[0].messageKey == 'notification.reservation.note_added'
    When method GET
    Then status 200
    And match response.items[0].type == 'new_note'

    # ── 9. Le prestataire annule après confirmation : CONFIRMED -> CANCELED_BY_PRO_POST_CONFIRMATION
    Given url baseUrl
    And path '/api/v1/pro/reservations/' + reservationId + '/cancel'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And request { reason: 'Empêchement de dernière minute', isPersonal: false }
    When method POST
    Then status 204

    Given path '/api/v1/user/reservations/' + reservationId
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response.status == 'annulee'

    # Conséquence côté sgilt-notifications : le client est notifié de l'annulation
    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + clientToken
    And retry until response.items[0].messageKey == 'notification.reservation.status.canceled_by_pro_post_confirmation'
    When method GET
    Then status 200

    # Conséquence : impossible de reconfirmer une réservation annulée (409, pas 204)
    Given url baseUrl
    And path '/api/v1/user/reservations/' + reservationId + '/confirm'
    And header Authorization = 'Bearer ' + clientToken
    When method POST
    Then status 409

    # Conséquence : le motif d'annulation apparaît dans le feed, visible par le client
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + clientToken
    When method GET
    Then status 200
    And match response[*].generatedKey contains 'feed.system.cancelled'
