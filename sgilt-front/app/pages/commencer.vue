<template>
  <!-- Desktop utilise une diagonale droite (clip-path: polygon() dans le
       style ci-dessous), donc pas besoin de ce clip-path SVG pour lui. -->
  <PhotoCurveClipPath />

  <div class="commencer-picker">
    <div class="content">
      <div class="intro">
        <div class="type-badge">
          <div class="icon-circle">
            <img :src="eventTypeImage" alt="" />
            <SparklingIcon class="sparkle" aria-hidden="true" />
          </div>
          <p class="type-label">{{ eventTypeLabel }}</p>
        </div>

        <p class="title">
          <span class="title-thin">{{ $t('commencer-picker.title') }}</span>
          <span class="title-bold">{{ $t('commencer-picker.title-highlight') }}</span>
        </p>
      </div>

      <p class="paragraph">{{ paragraph }}</p>

      <div class="choices">
        <SgiltCard tag="button" format="small" class="choice" @click="startGuidedOrganisation">
          <template #avatar>
            <div class="icon-badge"><TaskIcon class="icon" /></div>
          </template>
          <p class="label">{{ $t('commencer-picker.guided-cta.label') }}</p>
          <p class="subtitle">{{ $t('commencer-picker.guided-cta.subtitle') }}</p>
          <template #cta>
            <ArrowRightSIcon class="chevron" aria-hidden="true" />
          </template>
        </SgiltCard>

        <SgiltCard tag="button" format="small" class="choice" @click="goToSearch">
          <template #avatar>
            <div class="icon-badge"><SearchIcon class="icon" /></div>
          </template>
          <p class="label">{{ $t('commencer-picker.search-cta.label') }}</p>
          <p class="subtitle">{{ $t('commencer-picker.search-cta.subtitle') }}</p>
          <template #cta>
            <ArrowRightSIcon class="chevron" aria-hidden="true" />
          </template>
        </SgiltCard>
      </div>
    </div>

    <div v-if="coverImage" class="photo">
      <img :src="coverImage" :alt="photoAlt" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { SearchIcon, TaskIcon, ArrowRightSIcon, SparklingIcon } from '@remixicons/vue/line'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import PhotoCurveClipPath from '~/components/commencer/PhotoCurveClipPath.vue'
import { EVENT_TYPE_IMAGES, EVENT_TYPE_COVERS } from '~/utils/eventTypes'
import { toISODate } from '~/utils/dateUtils'

useHead({ title: "Vous avez l'idée - Sgilt" })

const { t } = useI18n()
const { state } = useDemande()

// Accès sans type choisi (arrivée directe sur l'URL) : renvoie au choix du type.
onMounted(() => {
  if (!state.eventType) {
    navigateTo('/fete')
  }
})

const eventTypeKey = computed(() => state.eventType ?? 'autre')
const eventTypeImage = computed(() => EVENT_TYPE_IMAGES[eventTypeKey.value])
const eventTypeLabel = computed(() => t(`event-picker.types.${eventTypeKey.value}.label`))
const paragraph = computed(() => t(`commencer-picker.paragraphs.${eventTypeKey.value}`))
const coverImage = computed(() => EVENT_TYPE_COVERS[eventTypeKey.value])
const photoAlt = computed(() => t(`commencer-picker.photo-alt.${eventTypeKey.value}`))

function goToSearch() {
  navigateTo({ path: '/search', query: state.date ? { date: toISODate(state.date) } : {} })
}

function startGuidedOrganisation() {
  navigateTo({ path: '/organisation', query: { step: 1 } })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.commencer-picker {
  position: relative;
  background: $surface-white;

  @media (min-width: $breakpoint-desktop) {
    display: grid;
    grid-template-columns: 1fr 1fr;
    // height (pas min-height) + overflow: hidden : la page ne doit jamais scroller
    height: calc(100dvh - $app-header-height);
    overflow: hidden;
  }
}

.content {
  position: relative;
  z-index: 1;
  padding: $spacing-m $section-padding-x $spacing-xl;
  display: flex;
  flex-direction: column;

  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-xxl;
    max-width: 32rem;
  }
}

// ── Icône + label + titre : ligne icône à gauche, titre à droite
.intro {
  display: flex;
  align-items: flex-start;
  gap: $spacing-m;
  text-align: left;
}

.type-badge {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-xxs;
}

.icon-circle {
  position: relative;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba($brand-accent, 0.18);

  img {
    width: 60%;
    height: 60%;
    object-fit: contain;
  }

  .sparkle {
    position: absolute;
    top: -0.35rem;
    left: -0.35rem;
    width: 1.1rem;
    height: 1.1rem;
    color: $brand-accent;
  }
}

.type-label {
  margin: 0;
  color: $text-secondary;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  white-space: nowrap;
}

.title {
  margin: 0;
  display: flex;
  flex-direction: column;
}

.title-thin,
.title-bold {
  font-family: 'Cormorant Garamond', serif;
  line-height: 1.25;
}

.title-thin {
  font-weight: $font-weight-medium;
  font-size: clamp(1.4rem, 5vw, 1.75rem);
  color: $text-primary;
}

.title-bold {
  font-weight: $font-weight-bold;
  font-size: clamp(1.6rem, 5.5vw, 2rem);
  color: $brand-accent;
}

// Espacement entre les blocs en vh (pourcentage de la hauteur d'écran) plutôt
// qu'en unités fixes : ça garde le rythme proportionnel à l'espace dispo et
// évite qu'un ajustement fasse déborder le contenu au-delà du viewport
// (rappel : la photo est fixe en bas sur mobile, donc surtout pas de scroll).
.paragraph {
  margin: 5vh 0 0;
  color: $text-secondary;
  font-size: $font-size-md;
  line-height: $line-height-relaxed;
}

.choices {
  display: flex;
  flex-direction: column;
  gap: 3vh;
  margin-top: 8vh;
}

.choice {
  .icon-badge {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba($brand-accent, 0.18);

    .icon {
      width: 1.4rem;
      height: 1.4rem;
      color: $brand-primary;
    }
  }

  .label {
    margin: 0;
    color: $text-primary;
    font-weight: $font-weight-bold;
    font-size: $font-size-md;
  }

  .subtitle {
    margin: $spacing-xxs 0 0;
    color: $text-secondary;
    font-size: $font-size-sm;
  }

  .chevron {
    width: 1.25rem;
    height: 1.25rem;
    color: $text-secondary;
  }
}

// ── Photo ─────────────────────────────────────────────────────────────────
// Mobile : fond fixe collé au bas de l'écran (position: fixed)
// séparation courbe (clip-path SVG, voir le <svg> en haut du template).
// Desktop : redevient une colonne de grille classique, comme HomeHeroSection.vue
// — séparation en diagonale droite (clip-path: polygon(), pas de courbe ici).
.photo {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  height: clamp(14rem, 38vh, 20rem);
  pointer-events: none;
  clip-path: url(#sgilt-photo-curve);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  @media (min-width: $breakpoint-desktop) {
    position: static;
    height: auto;
    min-height: 0;
    pointer-events: auto;
    clip-path: polygon(0 100%, 20% 0, 100% 0, 100% 100%); // séparation en ligne oblique
  }
}
</style>
