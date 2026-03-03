<template>
  <ClientOnly>
    <div class="search-page">
      <section class="search-header">
        <div class="date-filter">
          <SgiltDateFilter v-model="dateModel" />
        </div>

        <SgiltCategoryFilter v-model="categoryId" />

        <SgiltSubCategoryFilter
          v-if="categoryId !== '1'"
          :categoryId="categoryId"
          :activeSubcats="currentSubcats"
          :counts="subcatCounts"
          @toggle="toggleSubcat"
        />

        <div v-else class="results-meta" :class="{ 'is-onboarding': showOnboarding }">
          <Transition name="vibe-collapse">
            <h2 v-if="showOnboarding" class="vibe-text">Votre événement prend forme.</h2>
          </Transition>

          <p class="count">
            <span v-if="!loading && !error">
              {{ results.length }} prestataires sont disponibles à votre date.
            </span>
            <div v-else-if="loading" class="skeleton-text"></div>
          </p>
        </div>
      </section>

      <div
        class="margin-for-header"
        :class="{ onboarding: categoryId === '1' && showOnboarding }"
      />

      <section class="search-results">
        <SgiltSearchResults :results="results" :loading="loading" :error="error" />
      </section>
    </div>
  </ClientOnly>
</template>

<script setup lang="ts">
import SgiltDateFilter from '~/components/composed/SgiltDateFilter.vue'
import SgiltCategoryFilter from '~/components/composed/SgiltCategoryFilter.vue'
import SgiltSubCategoryFilter from '~/components/composed/SgiltSubCategoryFilter.vue'
import SgiltSearchResults from '~/components/composed/SgiltSearchResults.vue'

// On récupère l'état de l'UI
const { dateModel, categoryId, showOnboarding, currentSubcats, toggleSubcat } = useSearchUi()

// On récupère les données (le watcher interne lance le fetch auto)
const { results, loading, subcatCounts, error } = useSearchFetch()

watch(categoryId, (newCat) => {
  showOnboarding.value = false
  loading.value = true
  results.value = []
})
</script>

<style scoped lang="scss">
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

.margin-for-header {
  margin-top: 11.875rem; 
  transition: margin-top 180ms ease;

  &.onboarding {
    margin-top: 13.125rem; 
  }
}

.search-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  position: fixed;
  z-index: 10;
  background-color: #ffffff;
}

.date-filter {
  width: 100%;
  max-width: 32rem; 
}

.results-meta {
  max-width: 1400px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  &:not(:has(.vibe-text)) {
    width: 100%;
    align-items: start;
  }

  .vibe-text {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
  }

  .count {
    font-size: 1rem;
    font-weight: 600;
    color: #555555;

    // limite à 1 ligne et ajoute des "..." si c'est trop long
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

.skeleton-text {
  min-width: 20rem;
  height: 1.2rem;
}

/* Fade + slide out/in (l'in ne se produira qu'une seule fois au début) */
.vibe-collapse-enter-active,
.vibe-collapse-leave-active {
  transition:
    opacity 320ms cubic-bezier(0.4, 0, 0.2, 1),
    transform 320ms cubic-bezier(0.4, 0, 0.2, 1);
}

.vibe-collapse-enter-from,
.vibe-collapse-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.vibe-collapse-enter-to,
.vibe-collapse-leave-from {
  opacity: 1;
  transform: translateY(0);
}
</style>
