<template>
  <div class="search-page">
    <section class="search-header" ref="headerRef">
      <div class="date-filter">
        <SgiltDateFilter v-model="dateModel" />
      </div>

      <!-- Category + subcat filters -->
      <SgiltCategoryFilter v-model="categoryId" />

      <SgiltSubCategoryFilter
        v-if="!isAllCategories"
        :categoryId="categoryId"
        :activeSubcats="currentSubcats"
        :counts="subcatCounts"
        @toggle="toggleSubcat"
      />

      <!-- Résumé des résultats -->
      <div v-else class="results-meta" :class="{ 'is-onboarding': showOnboarding }">
        <Transition name="vibe-collapse">
          <h2 v-if="showOnboarding" class="vibe-text">Votre événement prend forme.</h2>
        </Transition>
        <div class="count">
          <span v-if="!loading && !error">
            {{ results.length }} prestataires sont disponibles à votre date.
          </span>
          <span v-else-if="loading" class="skeleton-text"></span>
        </div>
      </div>
    </section>

    <!-- Marge dynamique pour compenser le header fixe -->
    <div class="margin-for-header" :style="{ marginTop: headerHeight + 'px' }" />

    <!-- Résultats — rendus en SSR pour le SEO -->
    <section class="search-results">
      <SgiltSearchResults :results="results" :loading="loading" :error="error" />
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltDateFilter from '~/components/composed/SgiltDateFilter.vue'
import SgiltCategoryFilter from '~/components/composed/SgiltCategoryFilter.vue'
import SgiltSubCategoryFilter from '~/components/composed/SgiltSubCategoryFilter.vue'
import SgiltSearchResults from '~/components/composed/SgiltSearchResults.vue'

const { dateModel, categoryId, showOnboarding, currentSubcats, toggleSubcat } = useSearchUi()
const { results, loading, subcatCounts, error } = useSearchFetch()

// Catégorie "Tout" — plus lisible que categoryId !== '1'
const isAllCategories = computed(() => categoryId.value === APP_CATEGORIES[0]?.id)

watch(categoryId, () => {
  showOnboarding.value = false
})

// Hauteur dynamique du header pour la marge de compensation
const headerRef = ref<HTMLElement | null>(null)
const headerHeight = ref(0)

onMounted(() => {
  if (!headerRef.value) return
  const ro = new ResizeObserver(([entry]) => {
    headerHeight.value = entry!.contentRect.height
  })
  ro.observe(headerRef.value)
  onUnmounted(() => ro.disconnect())
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$background: white;

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
  position: fixed;
  z-index: 10;
  background-color: $background;
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

.skeleton-text {
  display: inline-block;
  min-width: 20rem;
  height: 1.2rem;
}

/* Fade + slide out/in */
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
