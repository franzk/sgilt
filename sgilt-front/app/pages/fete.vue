<template>
  <div class="event-picker">
    <div class="wrap">
      <div class="header">
        <p class="eyebrow">{{ $t('event-picker.eyebrow') }}</p>
        <h1 class="title">
          {{ $t('event-picker.title') }}
          <SparklingIcon class="sparkle" aria-hidden="true" />
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
import { SparklingIcon, LockIcon } from '@remixicons/vue/line'
import EventTypeCard from '~/components/cards/EventTypeCard.vue'

useHead({ title: "Que fête-t-on ? - Sgilt" })

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

  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-m 0;
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

.title {
  margin: 0 0 $spacing-s;
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  font-family: 'Cormorant Garamond', serif;
  font-weight: $font-weight-bold;
  font-size: clamp(1.8rem, 6vw, 2.5rem);
  color: $brand-primary;

  .sparkle {
    width: 1.25rem;
    height: 1.25rem;
    color: $brand-accent;
    flex-shrink: 0;
  }

  @media (min-width: $breakpoint-desktop) {
    font-size: 3.2rem;

    .sparkle {
      width: 1.75rem;
      height: 1.75rem;
    }
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
