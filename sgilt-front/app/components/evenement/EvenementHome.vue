<template>
  <!-- ── Mobile : le board (couverture, réglages, rubriques), avec l'accueil autour ─── -->
  <EventBoard
    v-if="isMobile"
    :event-meta="localEvent"
    :rubriques="localEvent.rubriques"
    :cover-image="coverImage"
    @settings="navigateTo('/evenement/parametres')"
    @rubrique="(rubriqueKey) => navigateTo(`/evenement/${rubriqueKey}`)"
  >
    <template #intro>
      <div class="mobile-intro">
        <div class="hero-text">
          <h2 class="title">{{ $t(`evenement.home.hero.title.${typeKey}`) }}</h2>
          <p class="intro">{{ $t('evenement.home.hero.intro') }}</p>
        </div>
        <EvenementStartCards>
          <template #title>
            <h2 class="section-title">{{ $t('evenement.home.start.title') }}</h2>
          </template>
        </EvenementStartCards>
      </div>
    </template>

    <PrestataireSuggestions
      class="mobile-inspirations"
      :subcat-keys="inspirationSubcats"
      :count="INSPIRATIONS_COUNT"
      :more-label="$t('evenement.home.inspirations.see-all')"
      layout="carousel"
      @more="navigateTo('/search')"
      @select="(provider) => navigateTo(`/${provider.slug}`)"
    >
      <template #title>
        <h2 class="section-title">{{ $t('evenement.home.inspirations.title') }}</h2>
      </template>
    </PrestataireSuggestions>
  </EventBoard>

  <!-- ── Desktop : la carte et les rubriques sont dans la sidebar ─────────────────── -->
  <div v-if="isDesktop" class="evenement-home">
    <!-- ── Accroche ─────────────────────────────────────────────────────────────── -->
    <section class="hero">
      <div class="hero-image" :style="{ backgroundImage: `url(${coverImage})` }" />
      <div class="hero-text">
        <h1 class="title">{{ $t(`evenement.home.hero.title.${typeKey}`) }}</h1>
        <p class="intro">{{ $t('evenement.home.hero.intro') }}</p>
      </div>
    </section>

    <!-- ── Par où commencer ? ───────────────────────────────────────────────────── -->
    <EvenementStartCards>
      <template #title>
        <h2 class="section-title">{{ $t('evenement.home.start.title') }}</h2>
      </template>
    </EvenementStartCards>

    <!-- ── Votre événement en un coup d'œil ─────────────────────────────────────── -->
    <section class="glance">
      <h2 class="section-title">{{ $t('evenement.home.glance.title') }}</h2>
      <div class="glance-row">
        <div v-for="info in infos" :key="info.key" class="info">
          <component :is="info.icon" class="icon" aria-hidden="true" />
          <div class="info-text">
            <span class="label">{{ $t(`evenement.home.glance.${info.key}`) }}</span>
            <span class="value">{{ info.value || $t('evenement.home.glance.to-define') }}</span>
          </div>
        </div>
        <NuxtLink to="/evenement/parametres" class="edit">
          <PencilIcon class="icon" aria-hidden="true" />
          {{ $t('evenement.home.glance.edit') }}
        </NuxtLink>
      </div>
    </section>

    <!-- ── Des idées pour vous inspirer ─────────────────────────────────────────── -->
    <PrestataireSuggestions
      :subcat-keys="inspirationSubcats"
      :count="INSPIRATIONS_COUNT"
      :more-label="$t('evenement.home.inspirations.see-all')"
      layout="grid"
      @more="navigateTo('/search')"
      @select="(provider) => navigateTo(`/${provider.slug}`)"
    >
      <template #title>
        <h2 class="section-title">{{ $t('evenement.home.inspirations.title') }}</h2>
      </template>
    </PrestataireSuggestions>
  </div>
</template>

<script setup lang="ts">
// Accueil de l'événement local (parcours public, /evenement).
// Mobile : le board (couverture et réglages, rubriques) autour du texte d'accueil, de par où commencer
// et des idées de prestataires. Desktop (carte et rubriques dans la sidebar) : accroche, par où
// commencer, l'événement en un coup d'œil, idées de prestataires.
import {
  CalendarEventIcon,
  GroupIcon,
  Heart3Icon,
  MapPin2Icon,
  PencilIcon,
} from '@remixicons/vue/line'
import EventBoard from '~/components/evenement/EventBoard.vue'
import EvenementStartCards from '~/components/evenement/EvenementStartCards.vue'
import PrestataireSuggestions from '~/components/prestataire/PrestataireSuggestions.vue'
import { fetchRubriqueContenu } from '~/data/template/service/templateService'
import { eventTypeKey } from '~/types/evenement'
import { formatDate } from '~/utils/dateUtils'

defineProps<{
  coverImage: string
}>()

const INSPIRATIONS_COUNT = 4

const { isMobile, isDesktop } = useDevice()
const { localEvent, eventTypeLabel } = useLocalEvent()
const { nbInvitesLabel } = useNbInvitesLabel()

const typeKey = computed(() => eventTypeKey(localEvent.eventType) ?? 'autre')

// Valeur vide : « À définir ».
const infos = computed(() => [
  { key: 'date', icon: CalendarEventIcon, value: formatDate(localEvent.date) },
  { key: 'nb-invites', icon: GroupIcon, value: nbInvitesLabel(localEvent.nbInvites) },
  { key: 'type', icon: Heart3Icon, value: eventTypeLabel.value },
  {
    key: 'lieu',
    icon: MapPin2Icon,
    value: [localEvent.lieu, localEvent.ville].filter(Boolean).join(', '),
  },
])

// ── Des idées pour vous inspirer ─────────────────────────────────────────────
// Prestataires de ce que regroupent les rubriques de l'événement ; tous si elles ne regroupent rien,
// si l'événement n'a pas de rubrique ou si leur contenu n'a pas pu être chargé. Recalculé quand les
// rubriques changent (copiées du template à la première arrivée, après le montage).
const inspirationSubcats = ref<string[] | null>(null)
const { subcategoriesOf } = useCategories()
const rubriqueKeys = computed(() => localEvent.rubriques.map((rubrique) => rubrique.key))

async function loadInspirationSubcats(keys: string[]) {
  inspirationSubcats.value = null
  try {
    const contenus = await Promise.all(keys.map((key) => fetchRubriqueContenu(typeKey.value, key)))
    // Rubriques changées entre-temps : la recherche suivante l'emporte.
    if (keys !== rubriqueKeys.value) return
    inspirationSubcats.value = subcategoriesOf(
      contenus.flatMap((contenu) => contenu.categories),
      contenus.flatMap((contenu) => contenu.subcategories),
    )
  } catch (e) {
    console.error(e)
    if (keys === rubriqueKeys.value) inspirationSubcats.value = []
  }
}

onMounted(() => {
  watch(rubriqueKeys, loadInspirationSubcats, { immediate: true })
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Titres de section, sur mobile comme sur desktop (dont ceux passés aux slots des sections).
.section-title {
  margin: 0;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

// ── Mobile : texte d'accueil et par où commencer, entre la couverture et les rubriques ──
.mobile-intro {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;

  .hero-text {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: $font-size-2xl;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      line-height: 1.1;
    }

    .intro {
      margin: 0;
      color: $text-secondary;
      font-size: $font-size-sm;
      line-height: 1.5;
    }
  }
}

.evenement-home {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;
  padding: 0 $spacing-xl $spacing-xl;

  // ── Accroche ─────────────────────────────────────────────────────────────────
  $hero-text-width: 40rem;

  // La couverture n'apparaît qu'après le texte, en fondu depuis le fond de page : le texte ne passe
  // jamais sur l'image (une couverture sombre le rendrait illisible).
  .hero {
    position: relative;
    margin: 0 (-$spacing-xl);
    padding: $spacing-xxl $spacing-xl $spacing-l;

    .hero-image {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      background-size: cover;
      background-position: right center;
      mask-image: linear-gradient(
        to right,
        transparent calc(#{$spacing-xl} + #{$hero-text-width}),
        #000 calc(#{$spacing-xl} + #{$hero-text-width} + 12rem)
      );
    }

    .hero-text {
      position: relative;
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      max-width: $hero-text-width;

      .title {
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-size: $font-size-3xl;
        font-weight: $font-weight-semibold;
        color: $text-primary;
      }

      .intro {
        margin: 0;
        color: $text-secondary;
        font-size: $font-size-md;
        line-height: 1.5;
      }
    }
  }

  // ── Votre événement en un coup d'œil ─────────────────────────────────────────
  .glance {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    padding: $spacing-l;
    border-radius: $radius-lg;
    background: $surface-soft;


    .glance-row {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: $spacing-m;

      .info {
        flex: 1;
        display: flex;
        align-items: flex-start;
        gap: $spacing-s;
        min-width: 10rem;
        padding-right: $spacing-m;
        border-right: 1px solid $divider-color;

        &:nth-last-child(2) {
          border-right: none;
        }

        .icon {
          flex-shrink: 0;
          width: 1.5rem;
          height: 1.5rem;
          color: $text-secondary;
        }

        .info-text {
          display: flex;
          flex-direction: column;
          gap: 0.125rem;

          .label {
            font-size: $font-size-sm;
            color: $text-secondary;
          }

          .value {
            font-size: $font-size-sm;
            font-weight: $font-weight-semibold;
            color: $text-primary;
          }
        }
      }

      .edit {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xs;
        padding: $spacing-xs $spacing-m;
        border: 1px solid $divider-color;
        border-radius: 999px;
        background: $surface-white;
        color: $text-primary;
        font-size: $font-size-sm;
        font-weight: $font-weight-semibold;
        text-decoration: none;
        white-space: nowrap;

        .icon {
          width: 1rem;
          height: 1rem;
        }
      }
    }
  }
}
</style>
