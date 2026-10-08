<template>
  <div class="rubrique">
    <!-- ── Barre de navigation ──────────────────────────────────────────────────── -->
    <header class="top-bar">
      <button
        class="back"
        type="button"
        :aria-label="$t('evenement.rubrique.back-aria')"
        @click="$emit('back')"
      >
        <ArrowLeftSIcon class="icon" />
      </button>
      <span class="top-title">{{ name }}</span>
    </header>

    <!-- ── Couverture ─────────────────────────────────────────────────────────── -->
    <!-- Pas de couverture tant que la rubrique n'a pas son visuel. -->
    <div v-if="coverImage" class="cover" :style="{ backgroundImage: `url(${coverImage})` }" />

    <div class="content">
      <!-- ── Présentation ───────────────────────────────────────────────────────── -->
      <section class="intro">
        <span
          class="badge"
          :class="{ 'no-cover': !coverImage }"
          :style="{ color: rubriqueAccent(rubriqueKey) }"
          aria-hidden="true"
        >
          <component :is="rubriqueIcon(rubriqueKey)" class="icon" />
        </span>
        <h1 class="name">{{ name }}</h1>
        <p class="tagline">{{ $t(`evenement.rubrique.content.${rubriqueKey}.tagline`) }}</p>

        <!-- Recherche globale en attendant la correspondance rubrique → filtres du template. -->
        <button class="cta primary" type="button" @click="$emit('search')">
          <SearchIcon class="icon" aria-hidden="true" />
          {{ $t('evenement.rubrique.search') }}
        </button>
        <button class="cta secondary" type="button" @click="onAlreadyClick">
          {{ $t(`evenement.rubrique.content.${rubriqueKey}.already`) }}
        </button>
      </section>

      <p class="tip">
        <LightbulbFlashIcon class="icon" aria-hidden="true" />
        {{ $t(`evenement.rubrique.content.${rubriqueKey}.tip`) }}
      </p>

      <!-- ── Mes réservations ─────────────────────────────────────────────────── -->
      <section class="reservations">
        <div class="section-head">
          <h2 class="section-title">{{ $t('evenement.rubrique.reservations-title') }}</h2>
          <button class="add" type="button" @click="$emit('search')">
            {{ $t('evenement.rubrique.add') }}
            <AddIcon class="icon" aria-hidden="true" />
          </button>
        </div>
        <!-- Rempli par la page quand l'événement a des réservations dans cette rubrique. -->
        <slot name="reservations">
          <p class="empty">{{ $t('evenement.rubrique.empty') }}</p>
        </slot>
      </section>

      <!-- ── Nos inspirations ─────────────────────────────────────────────────── -->
      <section v-if="inspirationsLoading || inspirations.length > 0" class="inspirations">
        <div class="section-head">
          <h2 class="section-title">{{ $t('evenement.rubrique.inspirations-title') }}</h2>
          <button class="see-more" type="button" @click="$emit('search')">
            {{ $t('evenement.rubrique.see-more') }}
            <ArrowRightSIcon class="icon" aria-hidden="true" />
          </button>
        </div>
        <div class="carousel">
          <template v-if="inspirationsLoading">
            <PrestataireCard v-for="n in INSPIRATIONS_COUNT" :key="n" class="card" loading />
          </template>
          <template v-else>
            <PrestataireCard
              v-for="provider in inspirations"
              :key="provider.id"
              class="card"
              :provider="provider"
            />
          </template>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
// Page d'une rubrique, commune au parcours public (/evenement/:rubrique, événement local) et à
// l'espace connecté (/app/events/:id/:rubrique, événement en base) : présentation, conseil,
// réservations et inspirations. La page fournit la rubrique (et ses réservations) et décide de la
// navigation (retour, recherche d'un prestataire).
import {
  AddIcon,
  ArrowLeftSIcon,
  ArrowRightSIcon,
  LightbulbFlashIcon,
  SearchIcon,
} from '@remixicons/vue/line'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import { RUBRIQUE_COVERS, rubriqueAccent, rubriqueIcon } from '~/constants/event-rubriques'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'
import { searchPrestataires } from '~/data/prestataire/service/prestataireService'
import { ALL_CATEGORY_KEY } from '~/utils/constants'

const INSPIRATIONS_COUNT = 6

const props = defineProps<{
  rubriqueKey: string
}>()

defineEmits<{
  back: []
  search: []
}>()

const { t } = useI18n()

const name = computed(() => t(`evenement.rubriques.${props.rubriqueKey}`))

useHead(() => ({ title: t('evenement.rubrique.page-title', { rubrique: name.value }) }))

// ── Couverture ───────────────────────────────────────────────────────────────
// Visuel propre à la rubrique, aucun s'il n'existe pas.
const { toUrl } = useImageUrl()
const coverImage = computed(() => {
  const rubriqueCover = RUBRIQUE_COVERS[props.rubriqueKey]
  return rubriqueCover ? toUrl(rubriqueCover) : null
})

// ── Inspirations ─────────────────────────────────────────────────────────────
// Recherche globale en attendant la correspondance rubrique → filtres du template.
const inspirations = ref<PrestataireCardDetail[]>([])
const inspirationsLoading = ref(true)

onMounted(async () => {
  try {
    const { results } = await searchPrestataires({
      categoryKey: ALL_CATEGORY_KEY,
      subcatKeys: [],
    })
    inspirations.value = results.slice(0, INSPIRATIONS_COUNT)
  } catch (e) {
    console.error(e)
  } finally {
    inspirationsLoading.value = false
  }
})

// ── Action à venir ───────────────────────────────────────────────────────────
function onAlreadyClick() {
  // « J'ai déjà mon … » : lot suivant.
  console.log('stay tuned')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.rubrique {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: $surface-white;

  // ── Barre de navigation ──────────────────────────────────────────────────────
  .top-bar {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 3.25rem;
    padding: 0 $spacing-m;
    background: $surface-white;
    border-bottom: 1px solid $divider-color;

    .back {
      position: absolute;
      padding: 0;
      border: none;
      background: none;
      cursor: pointer;
      left: $spacing-s;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 2.5rem;
      height: 2.5rem;
      color: $text-primary;

      .icon {
        width: 1.5rem;
        height: 1.5rem;
      }
    }

    .top-title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.35rem;
      font-weight: 600;
      color: $text-primary;
    }
  }

  // ── Couverture ───────────────────────────────────────────────────────────────
  .cover {
    height: 12.5rem;
    background-size: cover;
    background-position: center;

    @media (min-width: $breakpoint-desktop) {
      height: 33vh;
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    width: 100%;
    max-width: 45rem;
    margin: 0 auto;
    padding: 0 $spacing-m $spacing-xl;

    // ── Présentation ───────────────────────────────────────────────────────────
    .intro {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;

      .badge {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 4.5rem;
        height: 4.5rem;
        // La pastille chevauche le bas de la couverture.
        margin-top: -2.25rem;
        border: 3px solid $surface-white;
        border-radius: 50%;
        background: $surface-white;
        box-shadow: 0 0.25rem 0.75rem rgba(0, 0, 0, 0.12);

        .icon {
          width: 2rem;
          height: 2rem;
        }

        // Sans couverture, la pastille ne chevauche rien.
        &.no-cover {
          margin-top: $spacing-l;
        }
      }

      .name {
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-size: 2.25rem;
        font-weight: 600;
        line-height: 1.1;
        color: $text-primary;
      }

      .tagline {
        margin: 0 0 $spacing-xs;
        color: $text-secondary;
        font-size: $font-size-sm;
        line-height: $line-height-normal;
      }

      .cta {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: $spacing-xs;
        width: 100%;
        height: 3.25rem;
        border-radius: 9999px;
        font-family: inherit;
        font-size: $font-size-md;
        font-weight: $font-weight-bold;
        text-decoration: none;
        cursor: pointer;

        &.primary {
          border: none;
          background: $brand-accent;
          color: $brand-primary;
          box-shadow: 0 0.25rem 0.625rem rgba($brand-primary, 0.18);
        }

        &.secondary {
          border: 1.5px solid $divider-color;
          background: $surface-white;
          color: $text-primary;
        }

        &:focus-visible {
          outline: 3px solid $brand-primary;
          outline-offset: 4px;
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }

    // ── Conseil ────────────────────────────────────────────────────────────────
    .tip {
      display: flex;
      align-items: center;
      gap: $spacing-s;
      margin: 0;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: $surface-soft;
      color: $text-primary;
      font-size: $font-size-sm;
      line-height: $line-height-normal;

      .icon {
        flex-shrink: 0;
        width: 2rem;
        height: 2rem;
        color: $brand-accent;
      }
    }

    .section-head {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: $spacing-s;

      .section-title {
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-size: 1.6rem;
        font-weight: 600;
        color: $text-primary;
      }
    }

    // ── Mes réservations ──────────────────────────────────────────────────────
    .reservations {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;

      .add {
        display: inline-flex;
        border: none;
        font-family: inherit;
        cursor: pointer;
        align-items: center;
        gap: $spacing-xxs;
        padding: $spacing-xs $spacing-s;
        border-radius: 9999px;
        background: $surface-soft;
        color: $text-primary;
        font-size: $font-size-sm;
        font-weight: $font-weight-semibold;
        text-decoration: none;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }

      .empty {
        margin: 0;
        color: $text-secondary;
        font-size: $font-size-sm;
      }
    }

    // ── Nos inspirations ──────────────────────────────────────────────────────
    .inspirations {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;

      .see-more {
        display: inline-flex;
        padding: 0;
        border: none;
        background: none;
        font-family: inherit;
        cursor: pointer;
        align-items: center;
        gap: $spacing-xxs;
        color: $text-primary;
        font-size: $font-size-sm;
        text-decoration: none;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }

      // Défilement horizontal : les cartes débordent jusqu'aux bords de l'écran.
      .carousel {
        display: flex;
        gap: $spacing-s;
        margin: 0 (-$spacing-m);
        // Marge verticale : laisse la place à l'élévation des cartes au survol.
        padding: $spacing-xs $spacing-m;
        overflow-x: auto;
        scroll-snap-type: x mandatory;
        scrollbar-width: none;

        &::-webkit-scrollbar {
          display: none;
        }

        .card {
          flex: 0 0 11rem;
          scroll-snap-align: start;
        }
      }
    }
  }
}
</style>
