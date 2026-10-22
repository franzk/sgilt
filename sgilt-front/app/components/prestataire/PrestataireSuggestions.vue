<template>
  <!-- Rien si la recherche ne trouve personne. -->
  <section v-if="loading || providers.length > 0" class="prestataire-suggestions">
    <div class="head">
      <!-- Titre fourni par la page, pour qu'il ait le style de ses autres sections. -->
      <slot name="title" />
      <button class="more" type="button" @click="$emit('more')">
        {{ moreLabel }}
        <ArrowRightIcon class="icon" aria-hidden="true" />
      </button>
    </div>
    <div class="cards" :class="layout">
      <template v-if="loading">
        <PrestataireCard v-for="n in count" :key="n" class="card" loading />
      </template>
      <template v-else>
        <PrestataireCard
          v-for="provider in providers"
          :key="provider.id"
          class="card"
          :provider="provider"
          selectable
          @select="$emit('select', provider)"
        />
      </template>
    </div>
  </section>
</template>

<script setup lang="ts">
// Section de quelques prestataires suggérés (titre, « voir plus », cartes) : inspirations d'une
// rubrique, idées de l'accueil de l'événement. La page fournit les sous-catégories et décide de la
// navigation (« voir plus », clic sur une carte).
import { ArrowRightIcon } from '@remixicons/vue/line'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'
import { searchPrestataires } from '~/data/prestataire/service/prestataireService'
import { ALL_CATEGORY_KEY } from '~/utils/constants'

const props = defineProps<{
  // Sous-catégories cherchées : vide, tout le catalogue ; null, pas encore connues (chargement).
  subcatKeys: string[] | null
  count: number
  moreLabel: string
  // Carrousel horizontal (mobile, colonne étroite) ou grille d'une ligne.
  layout: 'carousel' | 'grid'
}>()

defineEmits<{
  more: []
  select: [provider: PrestataireCardDetail]
}>()

const providers = ref<PrestataireCardDetail[]>([])
const loading = ref(true)
// Seule la dernière recherche lancée est affichée (les sous-catégories peuvent changer en cours).
let lastRequest = 0

async function load(subcatKeys: string[] | null) {
  const request = ++lastRequest
  loading.value = true
  if (!subcatKeys) return
  try {
    const { results } = await searchPrestataires({ categoryKey: ALL_CATEGORY_KEY, subcatKeys })
    if (request === lastRequest) providers.value = results.slice(0, props.count)
  } catch (e) {
    console.error(e)
  } finally {
    if (request === lastRequest) loading.value = false
  }
}

// Côté client seulement : les suggestions ne sont jamais rendues en SSR.
onMounted(() => {
  watch(() => props.subcatKeys, load, { immediate: true })
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.prestataire-suggestions {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;

    .more {
      display: inline-flex;
      align-items: center;
      gap: $spacing-xs;
      padding: $spacing-xs $spacing-m;
      border: 1px solid $divider-color;
      border-radius: 9999px;
      background: $surface-white;
      color: $text-primary;
      font-family: inherit;
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;
      white-space: nowrap;
      cursor: pointer;

      .icon {
        width: 1rem;
        height: 1rem;
      }
    }
  }

  .cards {
    // Défilement horizontal : les cartes débordent jusqu'aux bords de l'écran.
    &.carousel {
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

    &.grid {
      display: grid;
      grid-template-columns: repeat(v-bind(count), minmax(0, 1fr));
      gap: $spacing-m;
    }
  }
}
</style>
