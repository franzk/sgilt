<template>
  <!-- Définition seule (width/height 0, pas de rendu) — référencée par
       .photo (commencer.vue) en mobile via clip-path: url(#sgilt-photo-curve). -->
  <svg width="0" height="0" aria-hidden="true">
    <defs>
      <!-- objectBoundingBox : coordonnées entre 0 et 1 UNIQUEMENT (pas 0–100,
           pas de px) — 1 = 100% de la largeur/hauteur de l'élément qui
           référence ce clip-path. -->
      <clipPath id="sgilt-photo-curve" clipPathUnits="objectBoundingBox">
        <path d="M 0 0.53 C 0.38 0.53, 0.62 0.25, 1 0.25 L 1 1 L 0 1 Z" />
      </clipPath>
    </defs>
  </svg>
</template>
