# Conventions de développement — sgilt-core

## Transaction management
- Les annotations `@Transactional` sont placées sur les méthodes des **controllers**, pas sur les services.
- Les services sont appelés dans le contexte transactionnel ouvert par le controller.
- Exception : les méthodes de lecture seule peuvent porter `@Transactional(readOnly = true)` sur le service si nécessaire.

## Persistence
- **Hibernate est la règle** — on utilise le pattern classique : charger l'entité, modifier via les setters, `save()`.
- Les méthodes `@Query` dans les repositories sont **interdites sauf cas extrême** justifié par un commentaire explicite.
- Les méthodes `@Modifying` sont interdites — on passe toujours par l'entité.
- Pas de SQL natif sauf exception documentée.

## Entités JPA
- Annotations Lombok sur toutes les entités : `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- On utilise le **builder Lombok** pour la construction des entités — pas de factory methods statiques.
- Pas de `equals`/`hashCode` personnalisés sauf besoin explicite documenté.
- Les setters sont accessibles — on modifie les entités directement via les setters.

## Code
- **Pas de code dupliqué** — toute logique réutilisable est extraite dans un service ou une classe utilitaire.
- **Une méthode = une responsabilité** — si une méthode fait deux choses, elle doit être découpée.
- Les interfaces de service sont **optionnelles** — on ne crée pas d'interface si il n'y a pas plusieurs implémentations prévues.
- On utilise les **records Java** pour les DTOs.
- On utilise **Lombok** pour les entités JPA.

## Javadoc
- **Toutes les méthodes publiques** portent une Javadoc.
- Format minimal :
```java
  /**
   * Description courte de ce que fait la méthode.
   *
   * @param paramName description du paramètre
   * @return description de ce qui est retourné
   * @throws ExceptionType dans quel cas
   */
```
- Les méthodes privées portent un commentaire inline si leur rôle n'est pas évident.

## Gestion des erreurs
- Les exceptions métier sont des classes dédiées dans le package `exception/` du domaine concerné.
- Le mapping HTTP est centralisé dans un `@RestControllerAdvice` par domaine.
- On ne retourne jamais d'erreurs génériques sans contexte — chaque exception porte un message clair.
- Codes HTTP à respecter :
    - `400` — requête invalide, token malformé, mismatch de données
    - `403` — ressource existante mais accès refusé
    - `404` — ressource introuvable
    - `410` — ressource expirée
    - `500` — erreur serveur inattendue

## Architecture
- Structure **feature-first** : chaque domaine a ses propres packages `api`, `controller`, `dto`, `service`, `domain`, `repository`, `exception`, `mailer`.
- Les services techniques partagés (JWT, mailer client) sont dans des packages racine dédiés (`jwt/`, `mailer/`).

## Services et repositories
- Un service ne manipule **jamais** le repository d'une autre entité.
- Si un service a besoin de données d'un autre domaine, il passe par le service dédié de ce domaine.
- Les repositories sont injectés uniquement dans le service du domaine correspondant.

## Conception des services
- Un service expose uniquement des **intentions métier**, jamais des opérations CRUD ou de lookup génériques.
- Les méthodes `findById`, `findBy...`, `save`, `delete` ne doivent pas être exposées publiquement par un service.
- Le lookup en BDD est une responsabilité interne du service — l'appelant exprime une intention, pas une requête de données.

Exemples :
- ❌ `reservationService.findById(id)` + `reservationService.passToNouvelle(reservation)`
- ✅ `reservationService.activateReservation(id)`
- ❌ `confirmationTokenService.findByReservation(reservation).ifPresent(confirmationTokenService::delete)`
- ✅ `confirmationTokenService.deleteByReservation(reservationId)`

## Sécurité
- Aucun secret en dur dans le code ou les fichiers de configuration commitée.
- Les valeurs sensibles passent par des variables d'environnement.
- `application-local.yml` est gitignorée — ne jamais commiter de vraie valeur de secret.

## Tests
- Les noms de méthodes de test suivent le pattern `givenXxx_whenYyy_thenZzz`.
- `given` décrit le contexte ou l'état initial.
- `when` décrit l'action testée.
- `then` décrit le résultat attendu.
- Les TU utilisent **JUnit 5**, **Mockito** et **AssertJ** exclusivement.
- `@ExtendWith(MockitoExtension.class)` sur toutes les classes de TU.
- `@Mock` pour les dépendances, `@InjectMocks` pour la classe testée.
- Assertions avec `assertThat` (AssertJ) — jamais `assertEquals` JUnit.
- `@SpringBootTest` est interdit dans les TU — réservé aux TI uniquement.
- Les classes `@Nested` organisent les tests par méthode testée.

## Ce que Claude Code ne doit pas faire
- Toucher à Git (`git add`, `git commit`, `git push`, etc.)
- Créer des méthodes `@Query` ou `@Modifying` dans les repositories sans validation explicite
- Placer des `@Transactional` sur les services
- Créer des interfaces de service sans raison explicite
- Générer du code sans Javadoc sur les méthodes publiques
- Utiliser `@Data` sur les entités JPA
