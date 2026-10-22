<template>
  <section class="home-providers">
    <div class="wrap">
      <div class="intro">
        <h2 class="title">{{ $t('landing-page.providers.title') }}</h2>
        <p class="lede">{{ $t('landing-page.providers.lede') }}</p>

        <p class="lines">
          {{ $t('landing-page.providers.line-lieu') }}
          {{ $t('landing-page.providers.line-traiteur') }}
          {{ $t('landing-page.providers.line-photographe') }}
          {{ $t('landing-page.providers.line-musiciens') }}
        </p>

        <p class="conclusion">{{ $t('landing-page.providers.conclusion') }}</p>

        <!-- Desktop uniquement : sur mobile le même CTA réapparaît sous les cartes. -->
        <NuxtLink to="/search" class="cta cta--desktop">
          {{ $t('landing-page.providers.cta') }} <span aria-hidden="true">→</span>
        </NuxtLink>
      </div>

      <div v-if="loading || providers.length > 0" class="cards">
        <template v-if="loading">
          <PrestataireCard v-for="n in DISPLAY_COUNT" :key="n" loading />
        </template>
        <template v-else>
          <PrestataireCard v-for="provider in providers" :key="provider.id" :provider="provider" />
        </template>
      </div>

      <NuxtLink to="/search" class="cta cta--mobile">
        {{ $t('landing-page.providers.cta') }} <span aria-hidden="true">→</span>
      </NuxtLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import { searchPrestataires } from '~/data/prestataire/service/prestataireService'
import { ALL_CATEGORY_KEY } from '~/utils/constants'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'

const DISPLAY_COUNT = 3

const providers = ref<PrestataireCardDetail[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const { results } = await searchPrestataires({
      categoryKey: ALL_CATEGORY_KEY,
      subcatKeys: [],
    })
    providers.value = results.slice(0, DISPLAY_COUNT)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-providers {
  padding: $spacing-m 0 $spacing-l;

  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-xxxl 0;
  }

  .wrap {
    max-width: $container-max-width;
    margin: 0 auto;
    padding: 0 $section-padding-x;
    display: flex;
    flex-direction: column;
    gap: $spacing-xl;

    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
      gap: $spacing-xxl;
    }
  }

  .intro {
    max-width: 56ch;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-s;

    @media (min-width: $breakpoint-desktop) {
      flex: 1 1 auto;
      min-width: 0;
    }
  }

  .title {
    margin: 0;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-primary;
  }

  .lede {
    margin: 0;
    color: $text-primary;
    font-weight: $font-weight-semibold;
    font-size: $font-size-lg;
  }

  .lines,
  .conclusion {
    margin: 0;
    color: $text-secondary;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
  }

  .cta {
    margin-top: $spacing-s;
    display: inline-flex;
    align-items: center;
    gap: 0.4em;
    padding: $spacing-m $spacing-xl;
    border-radius: 9999px;
    background: $brand-primary;
    color: $text-inverted;
    font-size: $font-size-md;
    font-weight: $font-weight-bold;
    text-decoration: none;
    transition:
      transform 160ms ease,
      box-shadow 160ms ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px $shadow-m;
    }

    &:focus-visible {
      outline: 3px solid $brand-accent;
      outline-offset: 4px;
    }

    // Mobile : le CTA vit sous les cartes, pas dans .intro. Desktop : l'inverse.
    &--desktop {
      display: none;
    }

    // Enfant direct de .wrap (flex column) en mobile : sans ça il s'étire en
    // pleine largeur au lieu de garder sa taille naturelle de pilule. Marge
    // négative pour absorber le gap de .wrap au-dessus (espacement voulu minimal).
    &--mobile {
      align-self: flex-start;
      margin-top: calc(-1 * #{$spacing-l});
    }

    @media (min-width: $breakpoint-desktop) {
      &--desktop {
        display: inline-flex;
      }

      &--mobile {
        display: none;
      }
    }
  }

  .cards {
    width: 100%;
    display: grid;
    // Même logique que la grille de /search (SgiltSearchResults) : 2 colonnes
    // par défaut en mobile, plutôt qu'empilées sur une colonne.
    grid-template-columns: repeat(2, 1fr);
    gap: $spacing-m;

    // 2 prestataires affichés sur mobile, le 3e n'apparaît qu'à partir du
    // desktop — display:none scopé en max-width plutôt qu'un display:block en
    // contrepartie, pour ne pas écraser le display:flex propre à PrestataireCard.
    @media (max-width: #{$breakpoint-desktop - 1px}) {
      > :nth-child(3) {
        display: none;
      }
    }

    // Cartes volontairement compactes — largeur fixe plutôt que 1fr, pour que
    // les 3 tiennent sur une seule ligne à côté du texte plutôt qu'en dessous.
    @media (min-width: $breakpoint-desktop) {
      flex-shrink: 0;
      width: auto;
      grid-template-columns: repeat(3, 160px);
      justify-content: start;
    }
  }
}
</style>
