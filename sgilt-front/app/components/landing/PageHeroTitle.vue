<template>
  <div class="page-hero-title">
    <p v-if="eyebrow" class="eyebrow">{{ eyebrow }}</p>
    <p class="title">
      <span class="title-thin">{{ titleParts.prefix }}</span>
      <span class="title-bold"
        >{{ highlight }}<span class="title-mark">{{ titleParts.suffix }}</span></span
      >
    </p>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  title: string
  highlight: string
  eyebrow?: string
}>()

// Sépare `title` autour de `highlight` pour appliquer le style accent à ce
// seul segment — si la sous-chaîne n'est pas trouvée, tout part en style
// "thin" plutôt que de planter.
const titleParts = computed(() => {
  const idx = props.title.indexOf(props.highlight)
  if (idx === -1) return { prefix: props.title, suffix: '' }
  return {
    prefix: props.title.slice(0, idx),
    suffix: props.title.slice(idx + props.highlight.length),
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Bloc eyebrow + titre partagé par tous les écrans du tunnel (fete.vue,
// LandingHeroScreen.vue...) — un seul endroit pour la police, le split
// thin/bold/mark et le centrage, pour garantir le même gabarit partout.
.page-hero-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.eyebrow {
  margin: 0 0 $spacing-xs;
  color: $text-secondary;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.title {
  margin: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: $brand-primary;

  // Desktop : titre sur une seule ligne
  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;
    align-items: baseline;
    gap: 0.5rem;
  }
}

.title-thin,
.title-mark {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 600;
  font-size: 2.5rem;
  line-height: 2.75rem;
}

// Police différente du reste du titre (sans-serif, hérite de
// $font-family-base) — pas de Cormorant Garamond ici, c'est ce qui fait
// ressortir le mot.
.title-bold {
  display: inline-flex;
  align-items: center;
  font-weight: 900;
  font-size: 3.2rem;
  line-height: 3rem;
  letter-spacing: 0.02em;
  margin-bottom: 0.875rem;
  color: $brand-accent;

  @media (min-width: $breakpoint-desktop) {
    margin-bottom: 0;
  }
}

// Le "?" reprend le style du début de phrase, seul le mot accentué garde la
// grosse typo.
.title-mark {
  margin-left: 0.08em;
  letter-spacing: normal;
  color: $brand-primary;
}
</style>
