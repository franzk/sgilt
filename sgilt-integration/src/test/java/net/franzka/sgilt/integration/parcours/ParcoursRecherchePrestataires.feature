Feature: Parcours découverte — recherche publique de prestataires

  Un visiteur non authentifié parcourt le catalogue public de prestataires, filtre par catégorie,
  puis consulte la fiche d'un résultat — le tout sans aucun token. Vérifie les compteurs par
  catégorie et le vrai filtrage (un DJ n'apparaît pas dans une recherche filtrée sur "photographe").

  Background:
    * url baseUrl

  Scenario: Recherche sans filtre, puis filtrée par catégorie, puis consultation d'une fiche

    # ── 1. Recherche sans filtre : les deux prestataires fixtures sont visibles ───────────────────
    Given path '/api/v1/prestataires'
    When method GET
    Then status 200
    And match response.results[*].slug contains 'studio-test'
    And match response.results[*].slug contains 'dj-test'
    And match response.countsByCategory contains { services: '#number', musique: '#number' }

    # ── 2. Recherche filtrée sur la catégorie "services" : le DJ n'apparaît plus ───────────────
    Given path '/api/v1/prestataires'
    And param categoryKey = 'services'
    When method GET
    Then status 200
    And match response.results[*].slug contains 'studio-test'
    And match response.results[*].slug !contains 'dj-test'
    And match each response.results[*].categoryKey == 'services'

    # ── 3. Consultation de la fiche trouvée, sans authentification ────────────────────────────────
    Given path '/api/v1/prestataires/studio-test'
    When method GET
    Then status 200
    And match response.name == 'Studio Test'
    And match response.status == 'PUBLISHED'

    # Une fiche inexistante retourne 404
    Given path '/api/v1/prestataires/ce-slug-n-existe-pas'
    When method GET
    Then status 404

    # Les clés d'engagement (édition de fiche) sont publiques
    Given path '/api/v1/prestataires/engagements'
    When method GET
    Then status 200
    * assert response.length > 0
