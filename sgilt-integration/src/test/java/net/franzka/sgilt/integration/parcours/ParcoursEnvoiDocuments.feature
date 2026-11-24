Feature: Parcours envoi de plusieurs documents — un envoi, une carte, une notification

  Le client dépose plusieurs fichiers d'un coup sur une réservation : un POST par fichier, tous
  rattachés au même uploadId, puis la clôture de l'envoi. Vérifie que le feed les regroupe en un seul
  élément (pour le client comme pour le prestataire), que le prestataire ne reçoit qu'une seule
  notification, que la suppression d'un fichier laisse l'envoi en place avec les autres, qu'un
  tiers ne peut pas clore l'envoi, et que l'autre partie ne peut pas glisser un fichier dans l'envoi
  (son uploadId est public : c'est l'id de la carte dans le feed).

  Background:
    * url baseUrl
    * configure retry = { count: 10, interval: 500 }

  Scenario: Le client dépose trois fichiers en un envoi — une carte, une notification au prestataire

    # ── 1. Le client crée un événement avec une demande au prestataire ────────────────────────────
    Given path '/api/v1/user/events'
    And header Authorization = 'Bearer ' + tokenUser
    And request { eventType: 'anniversaire', date: '2027-12-04', ville: 'Lille', demande: { prestataireId: '#(prestataireId)' } }
    When method POST
    Then status 201
    * def eventId = response.eventId

    Given path '/api/v1/user/events/' + eventId
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    * def reservationId = karate.jsonPath(response, '$.rubriques[*].reservations[*]')[0].id

    # ── 2. Trois fichiers, un POST chacun, rattachés au même envoi ────────────────────────────────
    * def uploadId = java.util.UUID.randomUUID() + ''

    Given path '/api/v1/reservations/' + reservationId + '/feed/documents'
    And header Authorization = 'Bearer ' + tokenUser
    And multipart file file = { value: 'devis', filename: 'devis.pdf', contentType: 'application/pdf' }
    And multipart field uploadId = uploadId
    When method POST
    Then status 201
    # Chaque réponse est l'envoi avec ce seul fichier
    And match response.id == uploadId
    And match response.files == '#[1]'
    And match response.files[0].name == 'devis.pdf'

    Given path '/api/v1/reservations/' + reservationId + '/feed/documents'
    And header Authorization = 'Bearer ' + tokenUser
    And multipart file file = { value: 'contrat', filename: 'contrat.pdf', contentType: 'application/pdf' }
    And multipart field uploadId = uploadId
    When method POST
    Then status 201

    Given path '/api/v1/reservations/' + reservationId + '/feed/documents'
    And header Authorization = 'Bearer ' + tokenUser
    And multipart file file = { value: 'plan', filename: 'plan.png', contentType: 'image/png' }
    And multipart field uploadId = uploadId
    When method POST
    Then status 201

    # Un fichier sans envoi est refusé
    Given path '/api/v1/reservations/' + reservationId + '/feed/documents'
    And header Authorization = 'Bearer ' + tokenUser
    And multipart file file = { value: 'seul', filename: 'seul.pdf', contentType: 'application/pdf' }
    When method POST
    Then status 400

    # Le prestataire ne peut pas glisser un fichier dans l'envoi du client
    Given path '/api/v1/reservations/' + reservationId + '/feed/documents'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And multipart file file = { value: 'intrus', filename: 'intrus.pdf', contentType: 'application/pdf' }
    And multipart field uploadId = uploadId
    When method POST
    Then status 409

    # Ni le client y mêler un fichier d'une autre visibilité
    Given path '/api/v1/reservations/' + reservationId + '/feed/documents'
    And header Authorization = 'Bearer ' + tokenUser
    And multipart file file = { value: 'perso', filename: 'perso.pdf', contentType: 'application/pdf' }
    And multipart field uploadId = uploadId
    And multipart field isPersonal = 'true'
    When method POST
    Then status 409

    # ── 3. Le feed les regroupe en un seul élément, côté client comme côté prestataire ────────────
    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    * def documents = karate.filter(response, function(item){ return item.type == 'document' })
    And match documents == '#[1]'
    And match documents[0].id == uploadId
    And match documents[0].authorRole == 'client'
    And match documents[0].files[*].name == ['devis.pdf', 'contrat.pdf', 'plan.png']
    * def devisId = documents[0].files[0].id

    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + tokenPrestataire
    When method GET
    Then status 200
    * def documents = karate.filter(response, function(item){ return item.type == 'document' })
    And match documents == '#[1]'
    And match documents[0].files == '#[3]'

    # ── 4. Clôture de l'envoi : une seule notification au prestataire ─────────────────────────────
    # Un tiers (autre client) ne peut pas clore l'envoi d'une réservation qui n'est pas la sienne
    Given path '/api/v1/reservations/' + reservationId + '/feed/uploads/' + uploadId + '/complete'
    And header Authorization = 'Bearer ' + tokenUser2
    And request {}
    When method POST
    Then status 403

    Given path '/api/v1/reservations/' + reservationId + '/feed/uploads/' + uploadId + '/complete'
    And header Authorization = 'Bearer ' + tokenUser
    And request {}
    When method POST
    Then status 204

    * def documentNotifications = function(items){ return karate.filter(items, function(n){ return n.messageKey == 'notification.reservation.document_added' && n.href.indexOf(reservationId) >= 0 }) }

    Given url notificationsBaseUrl
    And path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenPrestataire
    And retry until documentNotifications(response.items).length > 0
    When method GET
    Then status 200

    # Laisse le temps à d'éventuelles notifications en trop d'arriver avant de compter
    * java.lang.Thread.sleep(2000)
    Given path '/api/v1/notifications'
    And header Authorization = 'Bearer ' + tokenPrestataire
    When method GET
    Then status 200
    And match documentNotifications(response.items) == '#[1]'
    And match documentNotifications(response.items)[0].type == 'new_document'

    Given url baseUrl

    # ── 5. Suppression d'un fichier : l'envoi reste, avec les deux autres ─────────────────────────
    Given path '/api/v1/reservations/' + reservationId + '/feed/documents/' + devisId
    And header Authorization = 'Bearer ' + tokenUser
    When method DELETE
    Then status 204

    Given path '/api/v1/reservations/' + reservationId + '/feed'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    * def documents = karate.filter(response, function(item){ return item.type == 'document' })
    And match documents == '#[1]'
    And match documents[0].id == uploadId
    And match documents[0].files[*].name == ['contrat.pdf', 'plan.png']
