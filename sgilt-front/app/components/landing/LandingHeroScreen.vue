<template>
  <div class="home">
    <section class="search-form" :class="{ 'layout-stacked': layout === 'stacked' }">
      <!-- title -->
      <div class="hero">
        <p class="title">
          <span class="title-thin">{{ titleParts.prefix }}</span>
          <span class="title-bold"
            >{{ highlightedSubtext }}<span class="title-mark">{{ titleParts.suffix }}</span></span
          >
        </p>
        <p v-if="subtitle" class="tagline">{{ subtitle }}</p>
      </div>

      <div class="action-zone">
        <slot />
      </div>
    </section>
    <section class="photo-layer" aria-hidden="true"></section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  title: string
  highlightedSubtext: string
  subtitle?: string
  /**
   * Disposition desktop : `split` (défaut) = titre à gauche / zone d'action à
   * droite, deux colonnes (écran date). `stacked` = titre pleine largeur en
   * première ligne, zone d'action pleine largeur en dessous (écran d'accueil).
   */
  layout?: 'split' | 'stacked'
}>()

// Sépare `title` autour de `highlightedSubtext` pour appliquer le style accent
// à ce seul segment — si la sous-chaîne n'est pas trouvée, tout part en style
// "thin" plutôt que de planter.
const titleParts = computed(() => {
  const idx = props.title.indexOf(props.highlightedSubtext)
  if (idx === -1) return { prefix: props.title, suffix: '' }
  return {
    prefix: props.title.slice(0, idx),
    suffix: props.title.slice(idx + props.highlightedSubtext.length),
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens locaux ────────────────────────────────────────────────────────────
$background: white;
$padding-top: 0.75rem;
$hero-color: #000000;

$search-form-padding: $spacing-l $spacing-m 0;
$search-form-gap: clamp(0.5rem, 1.5vh, 1.5rem);

$title-thin-font-weight: 600;
$title-thin-font-size: 2.5rem;
$title-thin-line-height: 2.75rem;

$title-bold-font-weight: 900;
$title-bold-font-size: 3.2rem;
$title-bold-line-height: 3rem;
$title-bold-letter-spacing: 0.02em;
$title-bold-margin-bottom: 0.875rem;

$tagline-font-size: 1.05rem;
$tagline-line-height: 1.5;
$tagline-max-width: 26rem;

$photo-filter: brightness(1.03) contrast(1.03) saturate(1.06);

// ─── Composant ────────────────────────────────────────────────────────────────
.home {
  position: relative;
  width: 100%;
  height: calc(100dvh - $app-header-height);
  overflow: clip;
  background: $background;
  padding-top: $padding-top;

  display: flex;
  flex-direction: column;

  @media (max-width: #{$breakpoint-desktop - 1px}) {
    // Cacher l'ombre du header uniquement sur mobile
    :global(.app-header) {
      box-shadow: none;
    }
  }

  // ── Photo de fond ──────────────────────────────────────────────────────────
  .photo-layer {
    position: absolute;
    inset: 70% 0 0 0;
    z-index: 1;
    filter: $photo-filter;
    background-image: url('/images/hero-party-desktop.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 50% 30%;
    -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);
    mask-image: linear-gradient(to bottom, transparent 0%, #000 28%, #000 100%);
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-size: 100% 100%;
    mask-size: 100% 100%;
    pointer-events: none;

    @media (min-width: $breakpoint-desktop) {
      inset: 40% 0 0 0;
    }
  }

  // ── Zone de recherche ──────────────────────────────────────────────────────
  .search-form {
    z-index: 2;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $search-form-padding;
    justify-content: center;
    gap: $search-form-gap;
    // .home a maintenant une height fixe (plus min-height) pour ne jamais
    // dépasser la page — .search-form doit donc pouvoir se compresser au lieu
    // de rester bloqué à la taille naturelle de son contenu (comportement par
    // défaut d'un flex item, min-height:auto).
    min-height: 0;
    overflow: hidden;

    // Mobile uniquement
    @media (max-width: #{$breakpoint-desktop - 1px}) {
      flex: 0 70%;
      justify-content: space-around;
      overflow: visible;
    }

    // Propriétés communes aux deux dispositions desktop (split/stacked) —
    // seuls flex-direction/gap changent selon le mode, voir plus bas.
    @media (min-width: $breakpoint-desktop) {
      align-items: center;
      justify-content: center;
      width: 100%;
      max-width: $container-max-width;
      margin: 0 auto;
      padding: $spacing-xxl $spacing-xl;
      // conditionne l'affichage à la taille de la page
      flex: 1;
    }

    // Split (défaut, écran date) : titre à gauche / zone d'action à droite.
    &:not(.layout-stacked) {
      @media (min-width: $breakpoint-desktop) {
        flex-direction: row;
        gap: $spacing-xxl;
      }
    }

    // Stacked (écran d'accueil) : titre en pleine largeur, zone d'action
    // pleine largeur en dessous — même empilement que mobile, juste avec les
    // largeurs/espacements desktop ci-dessus. justify-content:flex-start (pas
    // center comme le split) pour remonter tout le bloc le plus haut possible
    // au lieu de le centrer dans l'espace disponible — l'objectif est de tenir
    // au-dessus de la photo, pas de centrer verticalement.
    &.layout-stacked {
      @media (min-width: $breakpoint-desktop) {
        flex-direction: column;
        justify-content: flex-start;
        padding-top: $spacing-l;
        gap: $spacing-xl;
      }
    }

    // Desktop uniquement : permet de scroller si la hauteur d'écran est trop
    // faible pour afficher tous les composants. En mobile, overflow reste
    // visible quelle que soit la hauteur (règle mobile ci-dessus).
    @media (min-width: $breakpoint-desktop) and (max-height: 530px) {
      overflow-y: auto;
      justify-content: flex-start;
    }
  }

  // ── Zone d'action (contenu spécifique à chaque écran) ────────────────────────
  .action-zone {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 0;
  }

  // En split (écran date), .hero et .action-zone se partagent la largeur à
  // égalité comme deux colonnes — pas de sens en stacked (écran d'accueil),
  // où .action-zone occupe déjà toute la largeur en dessous du titre.
  .search-form:not(.layout-stacked) .action-zone {
    @media (min-width: $breakpoint-desktop) {
      flex: 1 1 0;
    }

    // Uniquement en split : lorsqu'on permet le scroll pour une hauteur trop
    // faible, on aligne le contenu au début. En stacked, flex-direction:column
    // fait de align-self l'axe horizontal, pas vertical — pas ce qu'on veut ici.
    @media (min-width: $breakpoint-desktop) and (max-height: 530px) {
      align-self: flex-start;
    }
  }

  // ── Hero (titre + sous-titre) ─────────────────────────────────────────────────
  .hero {
    text-align: center;
    color: $color-primary;
    display: flex;
    flex-direction: column;
    align-items: center;

    @media (min-width: $breakpoint-desktop) {
      gap: $spacing-m;
    }
  }

  // Ne s'applique qu'en split — en stacked, .hero est déjà pleine largeur en
  // première ligne, pas de colonne à partager avec .action-zone.
  .search-form:not(.layout-stacked) .hero {
    @media (min-width: $breakpoint-desktop) {
      flex: 1 1 0;
    }
  }

  .title {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: $hero-color;
  }

  // Stacked desktop uniquement : titre sur une seule ligne plutôt qu'empilé
  // sur deux, pour remonter tout le bloc le plus haut possible au-dessus de
  // la photo (voir justify-content:flex-start sur .search-form.layout-stacked
  // plus haut).
  .search-form.layout-stacked .title {
    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      align-items: baseline;
      gap: 0.5rem;
    }
  }

  .search-form.layout-stacked .title-bold {
    @media (min-width: $breakpoint-desktop) {
      margin-bottom: 0;
    }
  }

  .title-thin {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;
  }

  .title-bold {
    display: inline-flex;
    align-items: center;
    font-weight: $title-bold-font-weight;
    font-size: $title-bold-font-size;
    line-height: $title-bold-line-height;
    letter-spacing: $title-bold-letter-spacing;
    margin-bottom: $title-bold-margin-bottom;
    color: $color-accent;
  }

  // Le "?" reprend le style du début de phrase, seul le mot accentué garde la grosse typo
  .title-mark {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;
    letter-spacing: normal;
    color: $hero-color;
  }

  .tagline {
    display: none;
    margin: 0;
    color: $text-secondary;

    @media (min-width: $breakpoint-desktop) {
      display: block;
      font-size: $tagline-font-size;
      line-height: $tagline-line-height;
      max-width: $tagline-max-width;
    }
  }

  // Stacked desktop : pas de max-width étroit comme le split, mais on accepte
  // le wrap sur plusieurs lignes si l'écran est trop étroit (ex. 1024px en
  // portrait tablette) — white-space:nowrap la faisait déborder à gauche et
  // à droite au lieu de retourner à la ligne.
  .search-form.layout-stacked .tagline {
    @media (min-width: $breakpoint-desktop) {
      max-width: none;
    }
  }

  // ── Background décoratif ───────────────────────────────────────────────────
  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;
    mask-image: none;
    -webkit-mask-image: none;
    pointer-events: none;
  }
}
</style>
