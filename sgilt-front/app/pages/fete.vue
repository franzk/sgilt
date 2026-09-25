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

    <SgiltConfirmDialog
      v-model:open="changeTypeDialogOpen"
      :title="$t('event-picker.change-type-dialog.title')"
      :message="$t('event-picker.change-type-dialog.message')"
      :confirm-label="$t('event-picker.change-type-dialog.confirm')"
      :cancel-label="$t('event-picker.change-type-dialog.cancel')"
      destructive
      max-width="400px"
      @confirm="confirmTypeChange"
    />
  </div>
</template>

<script setup lang="ts">
import EventTypeCard from '~/components/cards/EventTypeCard.vue'
import PageHeroTitle from '~/components/landing/PageHeroTitle.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'

useHead({ title: "Qu'est-ce qu'on fête ? - Sgilt" })

const { t } = useI18n()
const { localEvent, start } = useLocalEvent()

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

// Le clic sur une tuile valide directement le choix et enchaîne sur l'écran suivant, sauf si
// un autre type effacerait un événement déjà en mode événement : on demande alors confirmation.
// Avant le mode événement, l'événement ne contient presque rien, on n'interrompt pas.
const changeTypeDialogOpen = ref(false)
const pendingType = ref<string | null>(null)

function selectType(key: string) {
  const wouldEraseEvent = localEvent.organisationStarted && localEvent.eventType !== key
  if (wouldEraseEvent) {
    pendingType.value = key
    changeTypeDialogOpen.value = true
    return
  }
  chooseType(key)
}

function confirmTypeChange() {
  if (pendingType.value) chooseType(pendingType.value)
}

function chooseType(key: string) {
  start(key)
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