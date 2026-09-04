Feature: Parcours prestataire (flow clé en main) — pas de mail avant publication

  L'équipe Sgilt provisionne une fiche prestataire clé en main (aucune interaction du prestataire à
  ce stade) et la publie directement — sans que le prestataire se soit jamais connecté. Vérifie les
  conséquences qui distinguent ce flow de l'autonome : aucun mail/aucune entrée "en attente" avant la
  publication, fiche publique dès la publication, mail d'activation seulement à ce moment-là.

  Background:
    * url baseUrl
    * def cleEnMainEmail = 'karim.cleenmain@sgilt.test'
    * configure retry = { count: 10, interval: 500 }

  Scenario: Provisionnement clé en main — pas de mail avant publication, fiche visible dès la publication

    # ── 1. L'admin provisionne en flow clé-en-main : statut WAITING_FOR_CREATION_SERVICE ──────────
    # Personne n'a encore interagi avec Sgilt à ce stade (l'équipe construit la fiche elle-même,
    # hors périmètre de cette API) — donc aucune notification n'est due ici.
    Given path '/api/v1/admin/prestataires'
    And header Authorization = 'Bearer ' + tokenAdmin
    And request
      """
      {
        email: '#(cleEnMainEmail)', firstName: 'Karim', lastName: 'Cléenmain',
        slug: 'karim-cle-en-main-dj', prestataireName: 'Karim DJ',
        category: 'dj', subcats: '', cleEnMain: true
      }
      """
    When method POST
    Then status 201
    * def cleEnMainId = response.prestataireId

    # Conséquence : contrairement au flow autonome, aucun mail n'est envoyé à la création
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def prematureMail = response.find(m => m.payload.includes(cleEnMainEmail))
    And match prematureMail == null

    # Conséquence : la fiche n'apparaît pas (encore) dans les onboardings en attente — aucun
    # ActionToken n'existe tant qu'elle n'est pas publiée
    Given url baseUrl
    And path '/api/v1/admin/prestataires/onboarding-pending'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].email !contains cleEnMainEmail

    # ── 2. L'admin publie directement depuis WAITING_FOR_CREATION_SERVICE ─────────────────────────
    Given path '/api/v1/admin/prestataires/' + cleEnMainId + '/publish'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method POST
    Then status 204

    # Conséquence : la fiche est immédiatement visible publiquement — avant même que le prestataire
    # se soit connecté une seule fois
    Given path '/api/v1/prestataires/karim-cle-en-main-dj'
    When method GET
    Then status 200
    And match response.status == 'PUBLISHED'

    # Conséquence : c'est seulement maintenant que le mail (avec le lien vers la page déjà en ligne)
    # part réellement, et que la fiche apparaît dans les onboardings en attente
    Given url mailSendQueueUrl
    And header Authorization = mailQueueAuth
    And request { count: 20, ackmode: 'ack_requeue_false', encoding: 'auto' }
    When method POST
    Then status 200
    * def pageReadyMail = response.find(m => m.payload.includes('PRESTATAIRE_PAGE_READY_EMAIL') && m.payload.includes(cleEnMainEmail))
    And match pageReadyMail != null

    Given url baseUrl
    And path '/api/v1/admin/prestataires/onboarding-pending'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 200
    And match response[*].email contains cleEnMainEmail

    # ── 3. Le prestataire active son compte et retrouve sa fiche déjà publiée ─────────────────────
    * def cleEnMainToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').buildActionTokenForEmail(cleEnMainEmail)
    Given path '/api/v1/onboarding/verify'
    And param token = cleEnMainToken
    When method GET
    Then status 200
    * def cleEnMainSetPasswordToken = response.setPasswordToken

    Given path '/api/v1/onboarding/confirm-account'
    And request { setPasswordToken: '#(cleEnMainSetPasswordToken)', password: 'Test1234!', acceptedTerms: true }
    When method POST
    Then status 200
    And match response.loginUrl == '#present'

    * def cleEnMainPrestataireToken = Java.type('net.franzka.sgilt.integration.IntegrationTestContext').fetchTokenForUser(cleEnMainEmail, 'Test1234!')
    Given path '/api/v1/prestataires/ma-fiche'
    And header Authorization = 'Bearer ' + cleEnMainPrestataireToken
    When method GET
    Then status 200
    And match response.status == 'PUBLISHED'
    And match response.slug == 'karim-cle-en-main-dj'
