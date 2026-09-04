Feature: Parcours prestataire (flow autonome) — provisionnement, activation, édition et publication

  Un admin provisionne un prestataire (flow autonome). Le prestataire active son compte via le mail
  reçu, édite sa fiche encore en brouillon, la soumet pour revue. L'admin la publie, puis la renvoie
  en revue — en vérifiant à chaque étape les vraies conséquences (mails, visibilité publique,
  disparition de la liste des onboardings en attente), pas seulement le code HTTP de chaque appel.

  Background:
    * url baseUrl
    * def prestataireEmail = 'nouveau.prestataire@sgilt.test'
    * configure retry = { count: 10, interval: 500 }

  Scenario: Provisionnement, activation, édition, soumission, publication et retour en revue

    # ── 1. L'admin provisionne un nouveau prestataire (flow autonome) ────────────────────────────
    Given path '/api/v1/admin/prestataires'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: '#(prestataireEmail)', firstName: 'Nadia', lastName: 'Nouvelle',
        slug: 'nadia-nouvelle-photo', prestataireName: 'Nadia Nouvelle Photographie',
        category: 'photographe', subcats: '', cleEnMain: false
      }
      """
    When method POST
    Then status 201
    * def prestataireId = response.prestataireId

    # Conséquence : le mail d'activation a bien été mis en file. La queue mail.send est partagée
    # avec sgilt-notifications (autre producteur) : on vide un lot et on cherche le message attendu,
    # sans supposer sa position.
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def onboardingMail = response.find(m => m.payload.includes('PRESTATAIRE_ONBOARDING_EMAIL') && m.payload.includes(prestataireEmail))
    And match onboardingMail != null

    # Conséquence : le prestataire apparaît dans les onboardings admin en attente
    Given url baseUrl
    And path '/api/v1/admin/prestataires/onboarding-pending'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].email contains prestataireEmail

    # Un slug déjà pris est refusé
    Given path '/api/v1/admin/prestataires'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: 'autre-prestataire@sgilt.test', firstName: 'X', lastName: 'Y',
        slug: 'nadia-nouvelle-photo', prestataireName: 'Doublon',
        category: 'photographe', subcats: '', cleEnMain: false
      }
      """
    When method POST
    Then status 400

    # Un rôle non-admin n'a pas accès à cet endpoint
    Given path '/api/v1/admin/prestataires'
    And header Authorization = 'Bearer ' + tokenPro
    And request
      """
      {
        email: 'refuse@sgilt.test', firstName: 'X', lastName: 'Y', slug: 'refuse',
        prestataireName: 'X', category: 'photographe', subcats: '', cleEnMain: false
      }
      """
    When method POST
    Then status 403

    # ── 2. Le prestataire active son compte (token d'action reconstruit depuis la base) ──────────
    * def actionToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildActionTokenForEmail(prestataireEmail)
    Given path '/api/v1/onboarding/verify'
    And param token = actionToken
    When method GET
    Then status 200
    And match response.email == prestataireEmail
    * def setPasswordToken = response.setPasswordToken

    Given path '/api/v1/onboarding/confirm-account'
    And request { setPasswordToken: '#(setPasswordToken)', password: 'Test1234!', acceptedTerms: true }
    When method POST
    Then status 200
    And match response.loginUrl == '#present'

    # Conséquence : le token consommé, le prestataire ne figure plus dans les onboardings en attente
    Given path '/api/v1/admin/prestataires/onboarding-pending'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].email !contains prestataireEmail

    # ── 3. Le prestataire se connecte réellement et accède à sa fiche (statut DRAFT) ──────────────
    * def prestataireToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').fetchTokenForUser(prestataireEmail, 'Test1234!')
    Given path '/api/v1/pro/me'
    And header Authorization = 'Bearer ' + prestataireToken
    When method GET
    Then status 200
    And match response.slug == 'nadia-nouvelle-photo'

    Given path '/api/v1/prestataires/ma-fiche'
    And header Authorization = 'Bearer ' + prestataireToken
    When method GET
    Then status 200
    And match response.status == 'DRAFT'

    # ── 4. Le prestataire édite sa fiche ──────────────────────────────────────────────────────────
    Given path '/api/v1/prestataires/' + prestataireId
    And header Authorization = 'Bearer ' + prestataireToken
    And request { baseline: 'Photographe de mariage à Lyon', shortDescription: 'Capturer vos plus beaux moments.' }
    When method PATCH
    Then status 204

    Given path '/api/v1/prestataires/ma-fiche'
    And header Authorization = 'Bearer ' + prestataireToken
    When method GET
    Then status 200
    And match response.baseline == 'Photographe de mariage à Lyon'

    # Un pro tiers (pas propriétaire de cette fiche) ne peut pas l'éditer
    Given path '/api/v1/prestataires/' + prestataireId
    And header Authorization = 'Bearer ' + tokenPro
    And request { baseline: 'Usurpation' }
    When method PATCH
    Then status 403

    # ── 5. Le prestataire soumet sa fiche pour revue : DRAFT -> IN_REVIEW ─────────────────────────
    Given path '/api/v1/prestataires/ma-fiche/submit'
    And header Authorization = 'Bearer ' + prestataireToken
    When method POST
    Then status 204

    Given path '/api/v1/prestataires/ma-fiche'
    And header Authorization = 'Bearer ' + prestataireToken
    When method GET
    Then status 200
    And match response.status == 'IN_REVIEW'

    # Transition invalide : la fiche n'est plus en DRAFT, resoumettre échoue (409)
    Given path '/api/v1/prestataires/ma-fiche/submit'
    And header Authorization = 'Bearer ' + prestataireToken
    When method POST
    Then status 409

    # Conséquence : la fiche n'est pas encore visible publiquement (pas encore publiée)
    Given path '/api/v1/prestataires/nadia-nouvelle-photo'
    When method GET
    Then status 404

    # ── 6. L'admin publie la fiche : IN_REVIEW -> PUBLISHED ───────────────────────────────────────
    Given path '/api/v1/admin/prestataires/' + prestataireId + '/publish'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method POST
    Then status 204

    # Conséquence : le mail de publication a bien été mis en file
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def publishedMail = response.find(m => m.payload.includes('PRESTATAIRE_PUBLISHED_EMAIL'))
    And match publishedMail != null

    # Conséquence : la fiche est désormais visible publiquement, sans authentification
    Given url baseUrl
    And path '/api/v1/prestataires/nadia-nouvelle-photo'
    When method GET
    Then status 200
    And match response.status == 'PUBLISHED'

    # ── 7. L'admin renvoie la fiche en revue : PUBLISHED -> IN_REVIEW ─────────────────────────────
    Given path '/api/v1/admin/prestataires/' + prestataireId + '/send-to-review'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method POST
    Then status 204

    Given path '/api/v1/prestataires/ma-fiche'
    And header Authorization = 'Bearer ' + prestataireToken
    When method GET
    Then status 200
    And match response.status == 'IN_REVIEW'
