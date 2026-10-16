<template>
  <div class="evenement-home">
    <!-- ── Accroche ─────────────────────────────────────────────────────────────── -->
    <section class="hero">
      <div class="hero-image" :style="{ backgroundImage: `url(${coverImage})` }" />
      <div class="hero-text">
        <h1 class="title">{{ $t(`evenement.home.hero.title.${typeKey}`) }}</h1>
        <p class="intro">{{ $t('evenement.home.hero.intro') }}</p>
      </div>
    </section>

    <!-- ── Par où commencer ? ───────────────────────────────────────────────────── -->
    <!-- Les premières rubriques de l'événement (ordre du template) ; rien si aucune n'a sa carte. -->
    <section v-if="startRubriques.length > 0" class="start">
      <h2 class="section-title">{{ $t('evenement.home.start.title') }}</h2>
      <div class="start-cards">
        <NuxtLink
          v-for="key in startRubriques"
          :key="key"
          :to="`/evenement/${key}`"
          class="start-card"
        >
          <span class="badge" :style="{ background: rubriqueAccent(key) }" aria-hidden="true">
            <component :is="rubriqueIcon(key)" class="icon" />
          </span>
          <span class="card-title">{{ $t(`evenement.home.start.${key}.title`) }}</span>
          <span class="card-description">{{ $t(`evenement.home.start.${key}.description`) }}</span>
          <span class="arrow" aria-hidden="true"><ArrowRightSIcon class="icon" /></span>
        </NuxtLink>
      </div>
    </section>

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
// Accueil desktop de l'événement local (parcours public, /evenement) : accroche, par où commencer,
// l'événement en un coup d'œil, idées de prestataires. La carte et les rubriques sont dans la sidebar.
import {
  ArrowRightSIcon,
  CalendarEventIcon,
  GroupIcon,
  Heart3Icon,
  MapPin2Icon,
  PencilIcon,
} from '@remixicons/vue/line'
import PrestataireSuggestions from '~/components/prestataire/PrestataireSuggestions.vue'
import { rubriqueAccent, rubriqueIcon } from '~/constants/event-rubriques'
import { fetchRubriqueContenu } from '~/data/template/service/templateService'
import { eventTypeKey } from '~/types/evenement'
import { formatDate } from '~/utils/dateUtils'

defineProps<{
  coverImage: string
}>()

const START_CARDS_COUNT = 3
const INSPIRATIONS_COUNT = 4

const { te } = useI18n()
const { localEvent, eventTypeLabel } = useLocalEvent()
const { nbInvitesLabel } = useNbInvitesLabel()

const typeKey = computed(() => eventTypeKey(localEvent.eventType) ?? 'autre')

// Premières rubriques de l'événement qui ont un texte de carte.
const startRubriques = computed(() =>
  localEvent.rubriques
    .map((rubrique) => rubrique.key)
    .filter((key) => te(`evenement.home.start.${key}.title`))
    .slice(0, START_CARDS_COUNT),
)

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

  .section-title {
    margin: 0;
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  // ── Par où commencer ? ───────────────────────────────────────────────────────
  .start {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;

    .start-cards {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: $spacing-m;

      .start-card {
        position: relative;
        display: flex;
        flex-direction: column;
        gap: $spacing-xs;
        padding: $spacing-l $spacing-l $spacing-xxl;
        border-radius: $radius-lg;
        background: $surface-soft;
        color: $text-primary;
        text-decoration: none;

        .badge {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 2.5rem;
          height: 2.5rem;
          margin-bottom: $spacing-xs;
          border-radius: 50%;
          color: $text-inverted;

          .icon {
            width: 1.25rem;
            height: 1.25rem;
          }
        }

        .card-title {
          font-size: $font-size-md;
          font-weight: $font-weight-bold;
        }

        .card-description {
          font-size: $font-size-sm;
          color: $text-secondary;
          line-height: 1.4;
        }

        .arrow {
          position: absolute;
          right: $spacing-m;
          bottom: $spacing-m;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 2.25rem;
          height: 2.25rem;
          border-radius: 50%;
          background: $surface-white;
          box-shadow: 0 0.125rem 0.5rem $shadow-s;

          .icon {
            width: 1.25rem;
            height: 1.25rem;
          }
        }
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
