<!--
  Accueil d'un événement de l'espace connecté (/app/events/{eventId}).
  Desktop : titre de l'événement, invitation, grille des rubriques,
  choix libre (recherche dans tout le catalogue) et idées de prestataires sans filtre.
  Mobile : le board (couverture, réglages, rubriques), suivi du choix libre et des idées en carrousel.
  La recherche et les idées démarrent le parcours d'ajout d'un prestataire à l'événement.
-->
<template>
  <div class="event-page">
    <!-- ── Desktop ────────────────────────────────────────────────────────────── -->
    <div v-if="isDesktop" class="home">
      <template v-if="event">
        <div class="intro">
          <h1 class="title">{{ event.title }}</h1>
          <p class="invite">{{ $t('evenement.home.invite') }}</p>
        </div>

        <ul class="rubrique-grid">
          <li v-for="rubrique in rubriques" :key="rubrique.key">
            <NuxtLink :to="`/app/events/${eventId}/${rubrique.key}`" class="rubrique-card">
              <span
                class="badge"
                :style="{ background: rubriqueAccent(rubrique.key) }"
                aria-hidden="true"
              >
                <component :is="rubriqueIcon(rubrique.key)" class="icon" />
              </span>
              <span class="name">{{ $t(`evenement.rubriques.${rubrique.key}`) }}</span>
            </NuxtLink>
          </li>
        </ul>

        <EventFreeSearch @search="startAddPrestataireFlow()" />

        <PrestataireSuggestions
          :subcat-keys="[]"
          :count="INSPIRATIONS_COUNT"
          :more-label="$t('evenement.home.inspirations.see-all')"
          layout="grid"
          @more="startAddPrestataireFlow()"
          @select="(provider) => startAddPrestataireFlow(`/${provider.slug}`)"
        >
          <template #title>
            <h2 class="section-title">{{ $t('evenement.home.inspirations.title') }}</h2>
          </template>
        </PrestataireSuggestions>
      </template>
    </div>

    <!-- ── Mobile : même board que le parcours public ─────────────────────────── -->
    <template v-if="isMobile">
      <EventBoard
        v-if="!pending && event"
        :event-meta="event"
        :rubriques="rubriques"
        :cover-image="coverImage"
        @settings="navigateTo(`/app/events/${eventId}/parametres`)"
        @rubrique="(rubriqueKey) => navigateTo(`/app/events/${eventId}/${rubriqueKey}`)"
      >
        <EventFreeSearch @search="startAddPrestataireFlow()" />

        <PrestataireSuggestions
          :subcat-keys="[]"
          :count="INSPIRATIONS_COUNT"
          :more-label="$t('evenement.home.inspirations.see-all')"
          layout="carousel"
          @more="startAddPrestataireFlow()"
          @select="(provider) => startAddPrestataireFlow(`/${provider.slug}`)"
        >
          <template #title>
            <h2 class="section-title">{{ $t('evenement.home.inspirations.title') }}</h2>
          </template>
        </PrestataireSuggestions>
      </EventBoard>
      <Sk v-else class="cover-banner-skeleton" light />
    </template>
  </div>
</template>

<script setup lang="ts">
import Sk from '~/components/basics/Sk.vue'
import EventBoard from '~/components/evenement/EventBoard.vue'
import EventFreeSearch from '~/components/evenement/EventFreeSearch.vue'
import PrestataireSuggestions from '~/components/prestataire/PrestataireSuggestions.vue'
import { rubriqueAccent, rubriqueIcon } from '~/constants/event-rubriques'
import { useEventContext } from '~/data/evenement/useEventContext'
import { defaultCoverPath } from '~/utils/eventCovers'

definePageMeta({ layout: 'app' })

const INSPIRATIONS_COUNT = 4

const { eventId, event, rubriques, pending } = useEventContext()

const { isMobile, isDesktop } = useDevice()

// ── Cover image ────────────────────────────────────────────────────────────────
const { toUrl } = useImageUrl()
const coverImage = computed(() =>
  event.value ? toUrl(event.value.coverImage || defaultCoverPath(event.value.eventType)) : '',
)

// ── Flow ajout prestataire ────────────────────────────────────────────────────
const { start } = useFlow()
// Démarre par la recherche sans préfiltre, ou par la fiche d'une idée de prestataire (`to`).
const startAddPrestataireFlow = (to?: string) => {
  start(
    'add-prestataire',
    `Ajouter à ${event.value?.title ?? "l'événement"}`,
    {
      id: event.value?.id,
      nom: event.value?.title,
      date: event.value?.date ?? null,
      ville: event.value?.ville,
      ambiance: event.value?.ambiance,
      invites: event.value?.nbInvites,
    },
    to,
  )
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Titres de section (dont ceux passés aux slots des sections).
.section-title {
  margin: 0;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.event-page {
  flex: 1;
  display: flex;
  flex-direction: column;

  .home {
    display: flex;
    flex-direction: column;
    gap: $spacing-xl;
    padding: $spacing-xl;

    .intro {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;

      .title {
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-size: 2.25rem;
        font-weight: 600;
        color: $text-primary;
      }

      .invite {
        margin: 0;
        color: $text-secondary;
        font-size: $font-size-md;
      }
    }

    .rubrique-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(12rem, 1fr));
      gap: $spacing-m;
      margin: 0;
      padding: 0;
      list-style: none;

      .rubrique-card {
        display: flex;
        align-items: center;
        gap: $spacing-s;
        height: 100%;
        padding: $spacing-m;
        border: 1px solid $divider-color;
        border-radius: $radius-lg;
        background: $surface-white;
        color: $text-primary;
        text-decoration: none;
        transition: border-color 150ms ease;

        &:hover {
          border-color: $brand-primary;
        }

        .badge {
          display: flex;
          flex-shrink: 0;
          align-items: center;
          justify-content: center;
          width: 2.5rem;
          height: 2.5rem;
          border-radius: 50%;

          .icon {
            width: 1.25rem;
            height: 1.25rem;
          }
        }

        .name {
          font-size: $font-size-sm;
          font-weight: $font-weight-semibold;
        }
      }
    }
  }
}

.cover-banner-skeleton {
  height: 12.5rem;
}
</style>
