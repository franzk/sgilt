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
            @select="selectType(eventType.key)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import EventTypeCard from '~/components/cards/EventTypeCard.vue'
import PageHeroTitle from '~/components/landing/PageHeroTitle.vue'

useHead({ title: "Qu'est-ce qu'on fête ? - Sgilt" })

const { t } = useI18n()
const { state } = useDemande()

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
      image: IMAGES[key] ?? '',
    })),
)

// Le clic sur une tuile valide directement le choix et enchaîne sur l'écran suivant
// (pas d'étape de confirmation intermédiaire).
function selectType(key: string) {
  state.eventType = key
  navigateTo('/date')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.wrap {
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $section-padding-x $spacing-s;
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
</style>