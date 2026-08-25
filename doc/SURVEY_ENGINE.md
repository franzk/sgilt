# Survey engine — schéma de configuration

> Moteur de questionnaire générique et réutilisable. Un sondage est un fichier JSON statique
> versionné côté `sgilt-core`, servi au front par `GET /api/surveys/{slug}` (voir
> [[project_survey_engine]] pour les décisions d'architecture). Premier usage : sondage public
> organisateurs mariage.

## Emplacement

Un fichier JSON par sondage, dans `sgilt-core/src/main/resources/survey/{slug}.json`.

## Format `Survey`

| Champ         | Type         | Description                                                                                        |
|---------------|--------------|----------------------------------------------------------------------------------------------------|
| `slug`        | string       | Identifiant du sondage, utilisé dans l'URL (`/survey/{slug}`) et comme nom de fichier.             |
| `title`       | string       | Titre affiché en haut de la page.                                                                  |
| `description` | string       | Texte d'introduction, sous le titre.                                                               |
| `questions`   | `Question[]` | Liste ordonnée des questions, affichées dans cet ordre. Pas de logique conditionnelle entre elles. |

## Format `Question`

| Champ         | Type                                                        | Obligatoire | Description                                                                                                  |
|---------------|-------------------------------------------------------------|-------------|--------------------------------------------------------------------------------------------------------------|
| `id`          | string                                                      | oui         | Identifiant unique de la question au sein du sondage — c'est la clé utilisée dans `answers` à la soumission. |
| `type`        | `single_choice` \| `multi_choice` \| `open_text` \| `email` | oui         | Détermine le composant de rendu front et la validation back.                                                 |
| `label`       | string                                                      | oui         | Intitulé de la question.                                                                                     |
| `required`    | boolean                                                     | oui         | Si `true`, la question doit avoir une réponse non vide dans `answers` à la soumission.                       |
| `options`     | `Option[]`                                                  | selon type  | Uniquement pour `single_choice` / `multi_choice`.                                                            |
| `placeholder` | string                                                      | non         | Uniquement pour `open_text` — texte d'aide affiché dans le champ vide.                                       |

## Format `Option`

| Champ           | Type    | Obligatoire | Description                                                                                                                                                                                       |
|-----------------|---------|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `value`         | string  | oui         | Valeur stockée dans `answers` si cette option est sélectionnée.                                                                                                                                   |
| `label`         | string  | oui         | Texte affiché à l'utilisateur.                                                                                                                                                                    |
| `allowFreeText` | boolean | non         | Si `true`, sélectionner cette option affiche un champ texte libre conditionnel (cas "Autre"). La valeur saisie est stockée à part dans `answers`, sous la clé `{id}_texte_libre` (voir plus bas). |

## Types de question et forme de la réponse dans `answers`

| Type            | Forme de la réponse dans `answers`                   |
|-----------------|------------------------------------------------------|
| `single_choice` | `"{value}"` — une seule valeur d'`options`.          |
| `multi_choice`  | `["{value}", ...]` — tableau de valeurs d'`options`. |
| `open_text`     | `"texte libre"`.                                     |
| `email`         | `"adresse@email.tld"`.                               |

Si une option sélectionnée porte `allowFreeText: true`, le texte libre associé est stocké dans
`answers` sous une clé dédiée `{id}_texte_libre` (string), en plus de la valeur de l'option
elle-même dans `{id}`. Exemple pour la question `outils_utilises` (`multi_choice`) avec l'option
`autre` sélectionnée et un texte saisi :

```json
{
  "outils_utilises": ["papier", "autre"],
  "outils_utilises_texte_libre": "Un tableau Kanban perso"
}
```

## Exemple minimal

```json
{
  "slug": "exemple",
  "title": "Titre du sondage",
  "description": "Description courte.",
  "questions": [
    {
      "id": "q1",
      "type": "single_choice",
      "label": "Une question ?",
      "required": true,
      "options": [
        { "value": "oui", "label": "Oui" },
        { "value": "non", "label": "Non" },
        { "value": "autre", "label": "Autre", "allowFreeText": true }
      ]
    },
    {
      "id": "q2",
      "type": "open_text",
      "label": "Une question ouverte ?",
      "required": false,
      "placeholder": "Réponse libre..."
    }
  ]
}
```
