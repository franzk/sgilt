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
  categorie: 'musique' | 'restauration' | 'photo' | 'services'
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
