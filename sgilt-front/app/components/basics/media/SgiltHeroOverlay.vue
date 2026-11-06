<template>
  <div class="hero-overlay">
    <div class="gradient" aria-hidden="true" />
    <div class="content">
      <p v-if="tag" class="tag">{{ tag }}</p>
      <h1 class="title">{{ title }}</h1>
      <div class="subtitle">
        <slot name="subtitle" />
      </div>
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * Bandeau de texte superposé à une photo hero (dégradé + tag/titre/sous-titre), factorisé pour
 * partager la même taille de typographie entre la fiche prestataire (PrestataireHero.vue) et les
 * pages événement publiques (billetterie). Ne gère que la superposition texte : l'image/carousel
 * derrière reste à la charge de l'appelant (position: relative), ce composant se contente de
 * remplir cet ancêtre positionné (position: absolute; inset: 0).
 */
defineProps<{
  /** Eyebrow au-dessus du titre (ex. catégorie prestataire, édition de l'événement) */
  tag?: string
  title: string
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.hero-overlay {
  position: absolute;
  inset: 0;
}

.gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    transparent 30%,
    rgba(0, 0, 0, 0.25) 60%,
    rgba(0, 0, 0, 0.72) 100%
  );
}

.content {
  position: absolute;
  bottom: 1.5rem;
  left: 0;
  right: 0;
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $spacing-m;
  color: $text-inverted;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.4);

  @media (min-width: $breakpoint-desktop) {
    padding: 0 $section-padding-x;
  }
}

.tag {
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  opacity: 0.85;
  margin: 0 0 0.4rem;
  color: $brand-accent;
}

.title {
  font-family: 'Cormorant Garamond', serif;
  font-size: clamp(2rem, 6vw, 3.2rem);
  font-weight: 700;
  line-height: 1.1;
  margin: 0 0 0.5rem;
}

// Le slot #subtitle peut recevoir un <p> statique (billetterie) ou un <EditableText as="p">
// (fiche prestataire, édition inline) : :deep() cible le contenu quel que soit son origine,
// le scope Vue du composant parent qui fournit le slot ne matche pas celui-ci.
.subtitle {
  :deep(p),
  :deep(.editable-text) {
    font-size: 1rem;
    font-weight: 400;
    opacity: 0.9;
    margin: 0;
    max-width: 36rem;
  }
}
</style>
