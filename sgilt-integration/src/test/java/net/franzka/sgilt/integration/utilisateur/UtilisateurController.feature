Feature: UtilisateurController — profil de l'utilisateur connecté

  Background:
    * url baseUrl

  Scenario: un client (ROLE_USER) consulte son profil
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + tokenUser
    When method GET
    Then status 200
    And match response == { firstName: 'Alice', lastName: 'User', email: 'user-test@sgilt.test', avatarUrl: null }

  Scenario: un prestataire (ROLE_PRO) n'a pas accès au profil client
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + tokenPro
    When method GET
    Then status 403

  Scenario: un admin (ROLE_ADMIN) n'a pas accès au profil client
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + tokenAdmin
    When method GET
    Then status 403

  Scenario: sans token, l'accès au profil est refusé
    Given path '/api/v1/users/me'
    When method GET
    Then status 401

  Scenario: un JWT valide sans utilisateur correspondant en base retourne 404
    Given path '/api/v1/users/me'
    And header Authorization = 'Bearer ' + tokenOrphan
    When method GET
    Then status 404

  Scenario Outline: les 3 rôles peuvent consulter les champs éditables de leur profil
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + <token>
    When method GET
    Then status 200
    And match response.email == <email>

    Examples:
      | token      | email                     |
      | tokenUser  | 'user-test@sgilt.test'    |
      | tokenPro   | 'pro-test@sgilt.test'     |
      | tokenAdmin | 'admin-test@sgilt.test'   |

  Scenario: mise à jour partielle du profil — seul le prénom fourni est modifié
    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenPro
    And request { firstName: 'Paulette' }
    When method PATCH
    Then status 204

    Given path '/api/v1/users/me/edit'
    And header Authorization = 'Bearer ' + tokenPro
    When method GET
    Then status 200
    And match response == { firstName: 'Paulette', lastName: 'Pro', phone: null, email: 'pro-test@sgilt.test' }

  Scenario: sans token, la mise à jour du profil est refusée
    Given path '/api/v1/users/me/edit'
    And request { firstName: 'Intrus' }
    When method PATCH
    Then status 401
