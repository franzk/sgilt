<template>
  <div class="event-picker">
    <div class="wrap">
      <div class="header">
        <PageHeroTitle
          :eyebrow="$t('event-picker.eyebrow')"
          :title="$t('event-picker.title')"
          :highlight="$t('event-picker.title-highlight')"
        />
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
import PageHeroTitle from '~/components/landing/PageHeroTitle.vue'

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
