<template>
  <div class="event-picker">
    <div class="wrap">
      <div class="header">
        <p class="eyebrow">{{ $t('event-picker.eyebrow') }}</p>
        <h1 class="title">
          <span class="title-thin">{{ titleParts.prefix }}</span>
          <span class="title-bold"
            >{{ $t('event-picker.title-highlight')
            }}<span class="title-mark">{{ titleParts.suffix }}</span></span
          >
        </h1>
      </div>

      <div class="grid">
        <EventTypeCard
          v-for="eventType in eventTypes"
          :key="eventType.key"
          :label="eventType.label"
          :tagline="eventType.tagline"
          :image="eventType.image"
          :selected="selectedType === eventType.key"
          @select="selectType(eventType.key)"
        />
      </div>

      <div v-if="selectedType" class="bottom">
        <button type="button" class="cta" @click="confirmSelection">
          {{ $t('event-picker.cta') }} <span aria-hidden="true">→</span>
        </button>
        <p class="reassurance">
          <LockIcon class="icon" aria-hidden="true" />
          {{ $t('event-picker.reassurance') }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { LockIcon } from '@remixicons/vue/line'
import EventTypeCard from '~/components/cards/EventTypeCard.vue'

useHead({ title: "Qu'est-ce qu'on fête ? - Sgilt" })

const { t } = useI18n()
const { state } = useDemande()
const { isDesktop } = useDevice()

// Ordre d'affichage de la maquette (mariage/anniversaire d'abord).
const DISPLAY_ORDER = [
  'mariage',
  'anniversaire',
  'soiree_privee',
  'fete_entreprise',
  'evenement_public',
  'autre',
]

const IMAGES: Record<string, string> = {
  mariage: '/images/sgilt-mariage.png',
  anniversaire: '/images/sgilt-anniversaire.png',
  soiree_privee: '/images/sgilt-soiree-privee.png',
  fete_entreprise: '/images/sgilt-soiree-entreprise.png',
  evenement_public: '/images/sgilt-evenement-public.png',
  autre: '/images/sgilt-autre.png',
}

// Sépare le titre autour du mot accentué pour appliquer le style accent à ce
// seul segment (même logique que HomeHeroSection.vue).
const titleParts = computed(() => {
  const title = t('event-picker.title')
  const highlight = t('event-picker.title-highlight')
  const idx = title.indexOf(highlight)
  if (idx === -1) return { prefix: title, suffix: '' }
  return {
    prefix: title.slice(0, idx),
    suffix: title.slice(idx + highlight.length),
  }
})

const eventTypes = computed(() =>
  DISPLAY_ORDER.map((key) => ({
    key,
    label: t(`event-picker.types.${key}.label`),
    tagline: t(`event-picker.types.${key}.tagline`),
    image: IMAGES[key],
  })),
)

const selectedType = ref<string | null>(null)

function confirmSelection() {
  if (!selectedType.value) return
  state.eventType = selectedType.value
  navigateTo('/date')
}

// Desktop : les tuiles vont directement à l'écran suivant au clic (pas
// d'étape de confirmation, contrairement au mobile qui affiche un CTA).
function selectType(key: string) {
  selectedType.value = key
  if (isDesktop.value) confirmSelection()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.wrap {
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $section-padding-x;
  width: 100%;
}

.header {
  padding: $spacing-s 0;

  // .title est un bloc flex (voir plus bas) : text-align ne le centrerait pas,
  // il faut du flex centering (comme .hero dans LandingHeroScreen.vue).
  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-m 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}

.eyebrow {
  margin: 0 0 $spacing-xs;
  color: $text-secondary;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

// Copié tel quel de LandingHeroScreen.vue (même traitement que "QUAND" sur
// /date) : empilé sur 2 lignes en mobile, sur une ligne en desktop ; thin/mark
// en serif, bold en sans-serif (hérite de $font-family-base, pas de
// Cormorant Garamond dessus — c'est ce qui fait ressortir le mot).
.title {
  margin: 0 0 $spacing-s;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: $brand-primary;

  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;
    align-items: baseline;
    gap: 0.5rem;
  }

  .title-thin,
  .title-mark {
    font-family: 'Cormorant Garamond', serif;
    font-weight: 600;
    font-size: 2.5rem;
    line-height: 2.75rem;
  }

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

  .title-mark {
    margin-left: 0.08em;
    letter-spacing: normal;
    color: $brand-primary;
  }
}

// Mobile : 2 colonnes (3 rangées automatiques), cartes carrées (aspect-ratio,
// voir EventTypeCard.vue). Desktop : 3 colonnes (2 rangées), tuiles
// horizontales — la hauteur vient du contenu, pas d'un aspect-ratio.
.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xs;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-l;
    margin-top: $spacing-xl;
  }
}

// Desktop : les tuiles naviguent directement au clic (voir selectType dans
// le script), donc pas d'étape de confirmation à afficher.
.bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-s;
  padding-bottom: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    display: none;
  }
}

.cta {
  width: 100%;
  max-width: 24rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4em;
  height: 3.25rem;
  border: none;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  cursor: pointer;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba($brand-primary, 0.2);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 3px;
  }

  @media (min-width: $breakpoint-desktop) {
    width: auto;
    padding: 0 $spacing-xxl;
  }
}

.reassurance {
  margin: 0;
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  color: $text-secondary;
  font-size: $font-size-xs;

  .icon {
    width: 0.9rem;
    height: 0.9rem;
    flex-shrink: 0;
  }
}
</style>
