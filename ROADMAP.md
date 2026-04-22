# ROADMAP — SGILT

## État actuel

- Flux d'onboarding déployé en staging et fonctionnel (tunnel de demande de réservation visiteur non connecté, email de validation, création de compte KC via Admin API, redirection espace client)
- Données prestataires et événements encore mockées dans le front

---

## Chantiers à venir

### 1. Service prestataire — `sgilt-core`
- Création de la table prestataire (entité, repository, service, controller)
- Script d'import des données mockées actuellement dans le front vers la base de données

### 2. Branchement front/back — prestataires
- Architecture de récupération des données depuis le front (à définir)
- Remplacement des données mockées par les appels API réels
- Affichage des prestataires réels dans la recherche et les fiches

### 3. Validation, polish et navigation clavier
- Validation des inputs et polish des formulaires du tunnel
- Navigation au clavier dans tout le front (prioritaire)

### 4. Thème Keycloak SGILT
- Création du thème custom (login, forgot/reset password, pages d'erreur)
- Pas de page register (la création de compte est gérée programmatiquement via l'Admin API KC)

### 5. Documentation — flux d'onboarding
- Rédaction de `doc/ONBOARDING_FLOW.md`
- Couvre : tunnel → email de validation → saisie mot de passe → création compte KC via Admin API → redirection espace client

### 6. Gestion des événements — `sgilt-core`
- Création de la table événement et des layers associés
- Affichage des événements réels (non mockés) dans l'espace client

### 7. Amélioration accessibilité (a11y)
- Audit et amélioration globale : contrastes, aria labels, focus management
- Chantier d'amélioration continue, non bloquant pour les livraisons précédentes

---

## Hors scope (pour l'instant)

- Espace pro (tableau de bord prestataire, gestion des demandes entrantes)