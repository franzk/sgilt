Feature: Parcours client — modification des informations personnelles

  Un client consulte puis modifie ses informations personnelles (prénom, nom, téléphone), avec la
  vraie sémantique du PATCH : un champ omis n'est pas touché, un champ envoyé vide écrase la valeur
  existante (distinct de "omis") — et vérifie que la mise à jour se reflète bien sur les deux
  endpoints qui exposent l'identité (profil et profil éditable), pas seulement celui qu'on vient
  d'appeler. Utilise un client dédié (user2-test) pour ne pas interférer avec l'identité de
  référence ("Alice User") vérifiée ailleurs.

  Background:
    * url baseUrl

  Scenario: Consultation, mise à jour partielle, écrasement par valeur vide, puis mise à jour complète

    # ── 1. État initial du profil éditable ────────────────────────────────────────────────────────
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 200
    And match response == { firstName: 'Ursula', lastName: 'Deuxieme', phone: null, email: 'user2-test@sgilt.test' }

    # ── 2. Mise à jour partielle : seul le téléphone est fourni, le reste n'est pas touché ─────────
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    And request { phone: '0611223344' }
    When method PATCH
    Then status 204

    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 200
    And match response == { firstName: 'Ursula', lastName: 'Deuxieme', phone: '0611223344', email: 'user2-test@sgilt.test' }

    # ── 3. Valeur vide explicite : écrase le champ (distinct d'un champ omis) ──────────────────────
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    And request { phone: '' }
    When method PATCH
    Then status 204

    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 200
    And match response.phone == ''
    And match response.firstName == 'Ursula'

    # ── 4. Mise à jour complète des trois champs ──────────────────────────────────────────────────
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    And request { firstName: 'Ursuline', lastName: 'Troisieme', phone: '0699887766' }
    When method PATCH
    Then status 204

    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 200
    And match response == { firstName: 'Ursuline', lastName: 'Troisieme', phone: '0699887766', email: 'user2-test@sgilt.test' }

    # Conséquence : le profil (autre endpoint) reflète bien le même changement de nom
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 200
    And match response.firstName == 'Ursuline'
    And match response.lastName == 'Troisieme'

    # ── 5. Body entièrement vide : aucun champ n'est touché (no-op idempotent) ─────────────────────
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    And request {}
    When method PATCH
    Then status 204

    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenUser2
    When method GET
    Then status 200
    And match response == { firstName: 'Ursuline', lastName: 'Troisieme', phone: '0699887766', email: 'user2-test@sgilt.test' }
