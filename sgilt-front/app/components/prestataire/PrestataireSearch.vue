<template>
  <div class="search-page">
    <section ref="headerRef" class="search-header">
      <div v-if="showDateFilter" class="date-filter">
        <SgiltDateFilter v-model="dateModel" />
      </div>

      <SgiltCategoryFilter v-model="categoryKey" />

      <SgiltSubCategoryFilter
        v-if="!isAllCategories"
        :category-key="categoryKey"
        :active-subcats="currentSubcats"
        :counts="subcatCounts"
        @toggle="toggleSubcat"
      />

      <div v-else class="results-meta" :class="{ 'is-onboarding': showOnboarding }">
        <Transition name="vibe-collapse">
          <h2 v-if="showOnboarding" class="vibe-text">{{ $t('search.vibe-text') }}</h2>
        </Transition>
        <div class="count">
          <span v-if="!loading && !error">
            {{ $t('search.results-count', { count: results.length }) }}
          </span>
          <span v-else-if="loading" class="skeleton-text" />
        </div>
      </div>
    </section>

    <div class="margin-for-header" :style="{ marginTop: headerHeight + 'px' }" />

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
import SgiltDateFilter from '~/components/composed/SgiltDateFilter.vue'
import SgiltCategoryFilter from '~/components/composed/SgiltCategoryFilter.vue'
import SgiltSubCategoryFilter from '~/components/composed/SgiltSubCategoryFilter.vue'
import SgiltSearchResults from '~/components/composed/SgiltSearchResults.vue'
import type { PrestataireCardDetail } from '~/types/prestataire'

const props = defineProps<{
  selectable?: boolean
  showDateFilter?: boolean
}>()

defineEmits<{ select: [provider: PrestataireCardDetail] }>()

// ── Search state ──────────────────────────────────────────────────────────────
const { dateModel, categoryKey, showOnboarding, currentSubcats, toggleSubcat } =
  useSearchUi()
const { results, loading, subcatCounts, error } = useSearchFetch()

const isAllCategories = computed(() => categoryKey.value === APP_CATEGORIES[0]?.key)

watch(categoryKey, () => {
  showOnboarding.value = false
})

// ── Header height dynamique ───────────────────────────────────────────────────
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
  background-color: #fff;
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
