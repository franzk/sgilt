<!--
  Écran « Qu'est-ce qu'on fête ? » (/fete), première étape du parcours public : le choix du type
  d'événement. Les types sont rangés en deux groupes (pour moi et mes proches / pour une structure),
  suivis du lien « Projet personnalisé » (type Autre, hors groupe).
  - Mobile : une barre d'onglets toujours visible, qui affiche les tuiles du groupe choisi. Un
    onglet est toujours sélectionné : à l'arrivée, le premier, ou celui du groupe du type déjà
    choisi.
  - Desktop : les deux groupes en sections titrées, l'une sous l'autre.
  Un clic
  sur une tuile démarre l'événement local (type et titre par défaut) et passe à /date ; changer de
  type une fois l'organisation commencée demande confirmation (l'événement est effacé).
-->
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

      <template v-if="isMobile">
        <div class="groups" role="tablist">
          <button
              v-for="group in EVENT_TYPE_GROUPS"
              :key="group.key"
              class="group"
              :class="{ selected: selectedGroupKey === group.key }"
              type="button"
              role="tab"
              :aria-selected="selectedGroupKey === group.key"
              @click="selectedGroupKey = group.key"
          >
            <component :is="group.icon" class="icon" aria-hidden="true" />
            <span class="label">{{ $t(`event-picker.groups.${group.key}`) }}</span>
          </button>
        </div>

        <div class="grid" role="tabpanel">
          <EventTypeCard
              v-for="eventType in typesOf(selectedGroup)"
              :key="eventType.key"
              :label="eventType.label"
              :tagline="eventType.tagline"
              :image="eventType.image"
              @select="selectType(eventType.key)"
          />
        </div>
      </template>

      <template v-if="isDesktop">
        <section v-for="group in EVENT_TYPE_GROUPS" :key="group.key" class="section">
          <h2 class="title">
            <component :is="group.icon" class="icon" aria-hidden="true" />
            {{ $t(`event-picker.groups.${group.key}`) }}
          </h2>

          <div class="grid">
            <EventTypeCard
                v-for="eventType in typesOf(group)"
                :key="eventType.key"
                :label="eventType.label"
                :tagline="eventType.tagline"
                :image="eventType.image"
                @select="selectType(eventType.key)"
            />
          </div>
        </section>
      </template>

      <p class="custom">
        {{ $t('event-picker.custom.question') }}
        <button class="link" type="button" @click="selectType(EVENT_TYPE_AUTRE)">
          {{ $t('event-picker.custom.link') }}
        </button>
      </p>
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
import { eventTypeKey } from '~/types/evenement'
import {
  EVENT_TYPE_GROUPS,
  eventTypeDefinition,
  eventTypesOfGroup,
  type EventTypeGroup,
} from '~/utils/eventTypes'

useHead({ title: "Qu'est-ce qu'on fête ? - Sgilt" })

// Type choisi par le lien « Projet personnalisé », hors des groupes.
const EVENT_TYPE_AUTRE = 'autre'

const { t } = useI18n()
const { localEvent, start } = useLocalEvent()
const { isMobile, isDesktop } = useDevice()

// Onglet ouvert (mobile), toujours un : le premier par défaut. L'événement local vient du
// localStorage, absent au rendu serveur : l'onglet du type déjà choisi ne s'ouvre qu'une fois
// monté, pour ne pas casser l'hydratation. « Autre » n'a pas de groupe : le premier reste ouvert.
const selectedGroupKey = ref<string>(EVENT_TYPE_GROUPS[0]!.key)
onMounted(() => {
  const group = eventTypeDefinition(eventTypeKey(localEvent.eventType)).group
  if (group) selectedGroupKey.value = group
})

const selectedGroup = computed<EventTypeGroup>(
    () => EVENT_TYPE_GROUPS.find((group) => group.key === selectedGroupKey.value) ?? EVENT_TYPE_GROUPS[0]!,
)

interface EventTypeTile {
  key: string
  label: string
  tagline: string
  image: string
}

function typesOf(group: EventTypeGroup): EventTypeTile[] {
  return eventTypesOfGroup(group.key).map((type) => ({
    key: type.key,
    label: t(`event-picker.types.${type.key}.label`),
    tagline: t(`event-picker.types.${type.key}.tagline`),
    image: type.illustration,
  }))
}

// Le clic sur une tuile valide directement le choix et enchaîne sur l'écran suivant, sauf si
// un autre type effacerait un événement déjà en mode événement : on demande alors confirmation.
// Avant le mode événement, l'événement ne contient presque rien, on n'interrompt pas.
const changeTypeDialogOpen = ref(false)
const pendingType = ref<string | null>(null)

function selectType(key: string) {
  const wouldEraseEvent =
    localEvent.organisationStarted && eventTypeKey(localEvent.eventType) !== key
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
  start(key, t(`event-picker.types.${key}.default-title`))
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

// Barre d'onglets (mobile) : 2 colonnes égales, icône au-dessus du libellé. Bordure accent, icône
// et libellé noir chaud ; sélectionné, l'onglet passe sur fond accent.
.groups {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xs;
  margin-bottom: $spacing-m;

  .group {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: $spacing-xxs;
    padding: $spacing-xs $spacing-xxs;
    border: 1.5px solid $brand-accent;
    border-radius: $radius-lg;
    background: $surface-white;
    color: $text-primary;
    font-family: inherit;
    font-size: $font-size-sm;
    cursor: pointer;
    transition:
      border-color 180ms ease,
      background 180ms ease;

    &:focus-visible {
      outline: 3px solid $brand-accent;
      outline-offset: 2px;
    }

    .icon {
      width: 1.5rem;
      height: 1.5rem;
      color: $text-primary;
    }

    .label {
      color: $text-primary;
      font-weight: $font-weight-regular;
    }

    &.selected {
      background: $brand-accent;
    }
  }
}

// Sections (desktop) : un titre (icône + libellé du groupe) au-dessus des tuiles du groupe.
.section {
  margin-top: $spacing-xl;

  .title {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    margin: 0 0 $spacing-m;
    color: $text-primary;
    font-size: $font-size-lg;
    font-weight: $font-weight-regular;

    .icon {
      width: 1.5rem;
      height: 1.5rem;
    }
  }
}

.custom {
  margin: $spacing-xl 0 0;
  text-align: center;
  color: $text-secondary;
  font-size: $font-size-sm;

  .link {
    padding: 0;
    border: none;
    background: none;
    color: $text-primary;
    font: inherit;
    font-weight: $font-weight-semibold;
    text-decoration: underline;
    cursor: pointer;
  }
}

// Mobile : 2 colonnes, cartes carrées (aspect-ratio, voir EventTypeCard.vue). Desktop : 3 colonnes,
// tuiles horizontales — la hauteur vient du contenu, pas d'un aspect-ratio.
.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xs;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-l;
  }
}
</style>
