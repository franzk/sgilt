# SGILT — Documentation de Déploiement

Ce document décrit le fonctionnement du workflow de déploiement continu (CI/CD) de SGILT via GitHub Actions.

---

## 🚀 Vue d'ensemble

Le déploiement est piloté par le fichier `.github/workflows/deploy.yml`. Il automatise la construction des images Docker, leur stockage sur GitHub Container Registry (GHCR) et leur mise à jour sur le serveur distant via SSH.

**Principe clé :** La version du code définit la version déployée. Il n'y a pas de configuration manuelle des versions dans des fichiers `.env` séparés.

---

## 🛠 Paramètres du Workflow

Le workflow se lance manuellement depuis l'onglet **Actions** de GitHub.

| Paramètre | Options | Description |
| :--- | :--- | :--- |
| `environment` | `staging`, `production` | Détermine les variables (`vars`) et secrets utilisés, ainsi que le préfixe de l'image Keycloak. |
| `mode` | `update`, `init` | **update** (défaut) : Déploiement standard.<br>**init** : Recrée le Realm Keycloak à partir du template. |

---

## 📦 Gestion des Versions

La version de chaque micro-service est gérée de manière atomique.

### 1. Source de vérité
Le workflow lit directement le contenu des fichiers `VERSION` situés à la racine de chaque dossier de service :
* `sgilt-front/VERSION`
* `sgilt-keycloak/VERSION`
* `sgilt-mailer/VERSION`
* `sgilt-smtp-bridge/VERSION`

### 2. Comportement du Build
Pour optimiser le temps de déploiement, le workflow utilise une vérification intelligente :
* **Si le tag existe déjà sur GHCR :** Le build est ignoré (skip), l'image existante sera utilisée.
* **Si le tag est nouveau :** L'image est construite et poussée sur le registre.

> **Note sur Keycloak :** Contrairement aux autres services, l'image Keycloak est liée à l'environnement. Le tag généré est au format `${env}-${version}` (ex: `staging-1.0.4`).

---

## 📋 Procédure de déploiement

### Étape 1 : Préparation (si changement de version)
Si vous souhaitez déployer une nouvelle version d'un service :
1. Modifiez le fichier `VERSION` du service concerné.
2. Committez et poussez sur la branche cible (ex: `main`).

### Étape 2 : Lancement
1. Allez dans **Actions** > **Build & Deploy**.
2. Cliquez sur **Run workflow**.
3. Sélectionnez la branche, l'environnement et le mode (`update` par défaut).
4. Cliquez sur le bouton vert **Run workflow**.

### Étape 3 : Que se passe-t-il sur le serveur ?
Le workflow génère dynamiquement un fichier `.env` sur le serveur lors du déploiement. Ce fichier contient :
* Les versions exactes extraites des fichiers `VERSION`.
* Les URLs et secrets (DB, SMTP) configurés dans GitHub.
* Les ports Nginx spécifiques à l'environnement.

---

## 🔄 Retour en arrière (Rollback)

Si une version déployée pose problème, suivez cette procédure :

1. Repérez le numéro de la version stable précédente dans l'historique Git.
2. Modifiez le fichier `VERSION` dans votre code pour y remettre cet ancien numéro.
3. Committez et lancez le workflow en mode `update`.
4. Le système détectera que l'image ancienne existe déjà sur le registre et l'installera immédiatement sur le serveur.

---

## 🔍 Vérification et Debugging

Une fois le déploiement terminé, vous pouvez vérifier l'état sur le serveur :

```bash
# Se connecter au dossier de déploiement
cd /chemin/vers/deploiement

# Vérifier les versions injectées dans le .env
cat deploy/deploy-bundle/.env

# Vérifier que les containers tournent avec les bonnes images
docker ps