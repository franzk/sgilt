# CLAUDE.md — Conventions du projet SGILT

## TypeScript — Strict mode obligatoire

- Pas de `any` — utiliser `unknown` et affiner avec des guards si nécessaire
- Typer explicitement tous les props, retours de fonctions et paramètres
- Pas de `as SomeType` sauf cas exceptionnel documenté avec un commentaire

```ts
// ✅ Correct
interface Prestataire {
  id: string
  nom: string
  categorie: 'musique' | 'restauration' | 'lieu' | 'services'
  disponible: boolean
}

// ❌ Interdit
const p: any = fetchPrestataire()
```

---

## SCSS — Structure imbriquée qui suit le DOM

Pas de BEM. Le SCSS est écrit dans le bloc `<style lang="scss" scoped>` du composant `.vue`. Les classes sont **simples et sémantiques** (pas de BEM), et l'imbrication reflète fidèlement la hiérarchie du DOM.

```vue
<template>
  <div class="event-card">
    <div class="header">
      <img class="image" />
      <span class="badge">Confirmée</span>
    </div>
    <div class="body">
      <h2 class="title">Mariage de Julie & Thomas</h2>
      <p class="meta">14 sept. 2026 · Strasbourg</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.event-card {
  .header {
    .image { ... }
    .badge { ... }
  }
  .body {
    .title { ... }
    .meta { ... }
  }
}
</style>
```

Les états et variantes sont imbriqués avec `&` :

```scss
.tunnel-step {
  &.active { ... }
  &:hover { ... }

  .label { ... }
  .icon { ... }
}
```

---

## En-tête de fichier — obligatoire

Chaque fichier du front commence par un commentaire qui décrit ce qu'il fait : son rôle, où il est
utilisé, et ce qu'il faut savoir pour le comprendre (ex. les URL couvertes par une page).

- **`.ts`** : un bloc JSDoc `/** … */` tout en haut du fichier.
- **`.vue`** : un commentaire HTML `<!-- … -->` tout en haut du fichier, avant `<template>`.

Les fichiers existants sans en-tête se mettent en conformité quand on les crée ou les modifie (pas de
passe globale).

```vue
<!--
  Onglet Documents de la fiche d'une réservation, le même pour le client et le prestataire :
  tous les documents de la réservation, l'ajout, le téléchargement et la suppression des siens.
-->
<template>
  …
</template>
```

```ts
/**
 * Composable — état de la page de recherche de prestataires, porté par l'URL.
 */
export function useSearchUi() { … }
```

---

## Ce que Claude Code ne doit pas faire

- Ne jamais lancer de build du front (`nuxt build`, `nuxi typecheck`, `nuxt generate`, etc.) — l'environnement n'est pas fiable pour ça (échecs préexistants sans rapport avec le code modifié). Relire le code à la main et laisser l'utilisateur vérifier via `npm run dev`.
