<!--
  Recherche de prestataires, affichée par la page /search : filtres de catégorie et de
  sous-catégorie, collés en haut sous le header de l'app (et le bandeau s'il est affiché), puis la
  liste des résultats. L'état de la recherche est porté par l'URL (useSearchUi). Lancée depuis une
  rubrique, elle ne montre que ce que la rubrique regroupe, avec un bouton retour vers la rubrique.
  La page décide de ce que fait le clic sur un résultat (`select`).
-->
<template>
  <div class="search-page">
    <section class="search-header">
      <!-- Recherche lancée depuis une rubrique : retour à la rubrique, comme le retour de la fiche. -->
      <SgiltCategoryFilter
        v-model="categoryKey"
        :visible-keys="visibleCategoryKeys"
        :show-back="isRestricted"
        @back="router.back()"
      />

      <SgiltSubCategoryFilter
        v-if="!isAllCategories"
        :category-key="categoryKey"
        :active-subcats="currentSubcats"
        :counts="subcatCounts"
        :visible-keys="visibleSubcatKeysOfCategory(categoryKey)"
        @toggle="toggleSubcat"
      />

      <div v-else class="results-meta">
        <div class="count">
          <span v-if="!loading && !error">
            {{ $t('search.results-count', { count: results.length }) }}
          </span>
          <Sk v-else-if="loading" width="20rem" height="1.2rem" radius="4px" />
        </div>
      </div>
    </section>

    <section class="search-results">
      <SgiltSearchResults
        :results="results"
        :loading="loading"
        :error="error"
        :selectable="selectable"
        @select="$emit('select', $event)"
      />
    </section>
  </div>
</template>

<script setup lang="ts">
import Sk from '~/components/basics/Sk.vue'
import SgiltCategoryFilter from '~/components/composed/SgiltCategoryFilter.vue'
import SgiltSubCategoryFilter from '~/components/composed/SgiltSubCategoryFilter.vue'
import SgiltSearchResults from '~/components/composed/SgiltSearchResults.vue'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'

const props = defineProps<{
  selectable?: boolean
}>()

defineEmits<{ select: [provider: PrestataireCardDetail] }>()

// ── Search state ──────────────────────────────────────────────────────────────
const {
  categoryKey,
  currentSubcats,
  toggleSubcat,
  isRestricted,
  visibleCategoryKeys,
  visibleSubcatKeysOfCategory,
} = useSearchUi()
const router = useRouter()
const { results, loading, subcatCounts, error } = useSearchFetch()

const isAllCategories = computed(() => categoryKey.value === ALL_CATEGORY_KEY)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.search-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.search-results {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.search-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  position: sticky;
  top: var(--content-top, 0);
  z-index: 10;
  background-color: #fff;
}

.results-meta {
  width: 100%;
  max-width: 1400px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  align-items: start;
  justify-content: center;

  .count {
    font-size: 1rem;
    font-weight: 600;
    color: $text-secondary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    @media (min-width: 640px) {
      font-size: 1.125rem;
    }
  }

  * {
    margin: 0;
    padding: 0;
  }
}
</style>
