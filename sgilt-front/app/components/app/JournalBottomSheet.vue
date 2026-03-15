<template>
  <SgiltBottomSheet v-model:open="open" title="Journal des modifications">
    <div class="journal-sheet">
      <h2 class="journal-sheet__title">Journal des modifications</h2>

      <ul class="journal-list">
        <li v-for="entry in journal" :key="entry.id" class="journal-entry">
          <div class="journal-entry__meta">
            <span class="journal-entry__dot" aria-hidden="true" />
            <time class="journal-entry__date">{{ formatDate(entry.date) }}</time>
          </div>

          <ul class="journal-entry__mods">
            <li v-for="(mod, i) in entry.modifications" :key="i" class="journal-mod">
              <span class="journal-mod__champ">{{ mod.champ }}</span>
              <template v-if="entry.isCreation || mod.avant === null">
                <span class="journal-mod__arrow" aria-hidden="true">→</span>
                <span class="journal-mod__apres">{{ mod.apres }}</span>
              </template>
              <template v-else>
                <span class="journal-mod__avant">{{ mod.avant || '—' }}</span>
                <span class="journal-mod__arrow" aria-hidden="true">→</span>
                <span class="journal-mod__apres">{{ mod.apres }}</span>
              </template>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </SgiltBottomSheet>
</template>

<script setup lang="ts">
import { useEventJournalStore } from '~/stores/eventJournal'

const open = defineModel<boolean>('open', { required: true })

const store = useEventJournalStore()
const journal = computed(() => [...store.journal].reverse())

function formatDate(date: Date) {
  return new Date(date).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<style scoped lang="scss">
.journal-sheet {
  background: #fff;
  border-radius: $radius-lg $radius-lg 0 0;
  padding: $spacing-m $spacing-m calc($spacing-l + env(safe-area-inset-bottom, 0px));
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  min-height: 200px;
}

.journal-sheet__title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.2rem;
  font-weight: 600;
  color: $brand-primary;
  margin: 0;
}

.journal-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.journal-entry {
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;

  &__meta {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
  }

  &__dot {
    width: 3px;
    height: 3px;
    border-radius: 50%;
    background: $text-secondary;
    opacity: 0.4;
  }

  &__date {
    font-size: 0.75rem;
    color: $text-secondary;
    opacity: 0.7;
  }

  &__mods {
    list-style: none;
    margin: 0;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
}

.journal-mod {
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  gap: 4px;
  font-size: 0.82rem;

  &__champ {
    font-weight: 600;
    color: $text-secondary;
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    min-width: 0;
    flex-shrink: 0;
  }

  &__arrow {
    color: $text-secondary;
    opacity: 0.4;
    flex-shrink: 0;
  }

  &__avant {
    color: $text-secondary;
    text-decoration: line-through;
    opacity: 0.6;
  }

  &__apres {
    color: $text-primary;
  }
}
</style>
