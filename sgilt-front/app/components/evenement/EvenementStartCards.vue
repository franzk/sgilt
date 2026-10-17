<template>
  <!-- Rien si aucune rubrique de l'événement n'a sa carte. -->
  <section v-if="startRubriques.length > 0" class="start-cards">
    <!-- Titre fourni par la page, pour qu'il ait le style de ses autres sections. -->
    <slot name="title" />
    <div class="cards">
      <NuxtLink
        v-for="key in startRubriques"
        :key="key"
        :to="`/evenement/${key}`"
        class="start-card"
      >
        <span class="badge" :style="{ background: rubriqueAccent(key) }" aria-hidden="true">
          <component :is="rubriqueIcon(key)" class="icon" />
        </span>
        <span class="card-text">
          <span class="card-title">{{ $t(`evenement.home.start.${key}.title`) }}</span>
          <span class="card-description">{{ $t(`evenement.home.start.${key}.description`) }}</span>
        </span>
        <span class="arrow" aria-hidden="true"><ArrowRightSIcon class="icon" /></span>
      </NuxtLink>
    </div>
  </section>
</template>

<script setup lang="ts">
// Section « Par où commencer ? » de l'accueil de l'événement local : les premières rubriques de
// l'événement (ordre du template) qui ont un texte de carte. En ligne sur desktop, empilées sur mobile.
import { ArrowRightSIcon } from '@remixicons/vue/line'
import { rubriqueAccent, rubriqueIcon } from '~/constants/event-rubriques'

const START_CARDS_COUNT = 3

const { te } = useI18n()
const { localEvent } = useLocalEvent()

const startRubriques = computed(() =>
  localEvent.rubriques
    .map((rubrique) => rubrique.key)
    .filter((key) => te(`evenement.home.start.${key}.title`))
    .slice(0, START_CARDS_COUNT),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.start-cards {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    gap: $spacing-m;
  }

  .cards {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    @media (min-width: $breakpoint-desktop) {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: $spacing-m;
    }

    // Mobile : pastille, texte et flèche sur une ligne. Desktop : empilés, flèche en bas à droite.
    .start-card {
      position: relative;
      display: flex;
      align-items: center;
      gap: $spacing-s;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: $surface-soft;
      color: $text-primary;
      text-decoration: none;

      @media (min-width: $breakpoint-desktop) {
        flex-direction: column;
        align-items: flex-start;
        gap: $spacing-xs;
        padding: $spacing-l $spacing-l $spacing-xxl;
      }

      .badge {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 2.5rem;
        height: 2.5rem;
        border-radius: 50%;
        color: $text-inverted;

        @media (min-width: $breakpoint-desktop) {
          margin-bottom: $spacing-xs;
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }

      .card-text {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: $spacing-xxs;

        .card-title {
          font-size: $font-size-md;
          font-weight: $font-weight-bold;
        }

        .card-description {
          font-size: $font-size-sm;
          color: $text-secondary;
          line-height: 1.4;
        }
      }

      .arrow {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 2.25rem;
        height: 2.25rem;
        border-radius: 50%;
        background: $surface-white;
        box-shadow: 0 0.125rem 0.5rem $shadow-s;

        @media (min-width: $breakpoint-desktop) {
          position: absolute;
          right: $spacing-m;
          bottom: $spacing-m;
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }
  }
}
</style>
