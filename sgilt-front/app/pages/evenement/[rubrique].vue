<template>
  <div v-if="rubriqueKey" class="rubrique">
    <!-- ── Barre de navigation ──────────────────────────────────────────────────── -->
    <header class="top-bar">
      <NuxtLink to="/evenement" class="back" :aria-label="$t('evenement.rubrique.back-aria')">
        <ArrowLeftSIcon class="icon" />
      </NuxtLink>
      <span class="top-title">{{ name }}</span>
    </header>

    <!-- ── Couverture ─────────────────────────────────────────────────────────── -->
    <div class="cover" :style="{ backgroundImage: `url(${coverImage})` }" />

    <div class="content">
      <!-- ── Présentation ───────────────────────────────────────────────────────── -->
      <section class="intro">
        <span class="badge" :style="{ color: RUBRIQUE_ACCENTS[rubriqueKey] }" aria-hidden="true">
          <component :is="RUBRIQUE_ICONS[rubriqueKey]" class="icon" />
        </span>
        <h1 class="name">{{ name }}</h1>
        <p class="tagline">{{ $t(`evenement.rubrique.content.${rubriqueKey}.tagline`) }}</p>

        <!-- Recherche globale en attendant la correspondance rubrique → filtres du template. -->
        <NuxtLink to="/search" class="cta primary">
          <SearchIcon class="icon" aria-hidden="true" />
          {{ $t('evenement.rubrique.search') }}
        </NuxtLink>
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
          <NuxtLink to="/search" class="add">
            {{ $t('evenement.rubrique.add') }}
            <AddIcon class="icon" aria-hidden="true" />
          </NuxtLink>
        </div>
        <div v-if="reservations.length > 0" class="list">
          <RubriqueReservationItem
            v-for="reservation in reservations"
            :key="reservation.prestataireSlug"
            :reservation="reservation"
          />
        </div>
        <p v-else class="empty">{{ $t('evenement.rubrique.empty') }}</p>
      </section>

      <!-- ── Nos inspirations ─────────────────────────────────────────────────── -->
      <section v-if="inspirationsLoading || inspirations.length > 0" class="inspirations">
        <div class="section-head">
          <h2 class="section-title">{{ $t('evenement.rubrique.inspirations-title') }}</h2>
          <NuxtLink to="/search" class="see-more">
            {{ $t('evenement.rubrique.see-more') }}
            <ArrowRightSIcon class="icon" aria-hidden="true" />
          </NuxtLink>
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
import {
  AddIcon,
  ArrowLeftSIcon,
  ArrowRightSIcon,
  LightbulbFlashIcon,
  SearchIcon,
} from '@remixicons/vue/line'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import RubriqueReservationItem from '~/components/evenement/RubriqueReservationItem.vue'
import {
  RUBRIQUE_ACCENTS,
  RUBRIQUE_COVERS,
  RUBRIQUE_ICONS,
  RUBRIQUE_KEYS,
} from '~/constants/event-rubriques'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'
import { searchPrestataires } from '~/data/prestataire/service/prestataireService'
import { ALL_CATEGORY_KEY } from '~/utils/constants'
import { resolveEventCover } from '~/utils/eventCovers'

definePageMeta({ layout: 'evenement' })

const INSPIRATIONS_COUNT = 6

const route = useRoute()
const { t } = useI18n()
const { localEvent } = useLocalEvent()

// ── Rubrique ─────────────────────────────────────────────────────────────────
// Clé inconnue (URL tapée à la main) : retour à l'event board.
const rubriqueKey = computed(() => {
  const raw = route.params.rubrique
  return RUBRIQUE_KEYS.find((key) => key === raw) ?? null
})

if (!rubriqueKey.value) {
  await navigateTo('/evenement', { replace: true })
}

const name = computed(() =>
  rubriqueKey.value ? t(`evenement.rubriques.${rubriqueKey.value}`) : '',
)

useHead(() => ({ title: t('evenement.rubrique.page-title', { rubrique: name.value }) }))

const reservations = computed(
  () =>
    localEvent.rubriques.find((rubrique) => rubrique.key === rubriqueKey.value)?.reservations ?? [],
)

// ── Couverture ───────────────────────────────────────────────────────────────
// Visuel propre à la rubrique s'il existe, sinon celui de l'événement.
const { toUrl } = useImageUrl()
const coverImage = computed(() => {
  const rubriqueCover = rubriqueKey.value ? RUBRIQUE_COVERS[rubriqueKey.value] : undefined
  return (
    rubriqueCover ??
    resolveEventCover({ coverImage: null, eventType: localEvent.eventType ?? undefined }, toUrl)
  )
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

      .list {
        display: flex;
        flex-direction: column;
        gap: $spacing-s;
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
