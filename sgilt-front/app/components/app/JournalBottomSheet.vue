<template>
  <SgiltBottomSheet v-model:open="open" :title="$t('journal.title')">
    <div class="journal-sheet">
      <h2 class="title">{{ $t('journal.title') }}</h2>

      <ul class="journal-list">
        <li v-for="entry in journal" :key="entry.id" class="journal-entry">
          <div class="meta">
            <span class="dot" aria-hidden="true" />
            <time class="date">{{ formatDateTime(entry.date) }}</time>
          </div>

          <ul class="mods">
            <li v-for="(mod, i) in entry.modifications" :key="i" class="journal-mod">
              <span class="champ">{{ mod.champ }}</span>
              <template v-if="entry.isCreation || mod.avant === null">
                <span class="arrow" aria-hidden="true">→</span>
                <span class="apres">{{ mod.apres }}</span>
              </template>
              <template v-else>
                <span class="avant">{{ mod.avant || '—' }}</span>
                <span class="arrow" aria-hidden="true">→</span>
                <span class="apres">{{ mod.apres }}</span>
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

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
  }
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

  .meta {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
  }

  .dot {
    width: 3px;
    height: 3px;
    border-radius: 50%;
    background: $text-secondary;
    opacity: 0.4;
  }

  .date {
    font-size: 0.75rem;
    color: $text-secondary;
    opacity: 0.7;
  }

  .mods {
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

  .champ {
    font-weight: 600;
    color: $text-secondary;
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    min-width: 0;
    flex-shrink: 0;
  }

  .arrow {
    color: $text-secondary;
    opacity: 0.4;
    flex-shrink: 0;
  }

  .avant {
    color: $text-secondary;
    text-decoration: line-through;
    opacity: 0.6;
  }

  .apres {
    color: $text-primary;
  }
}
</style>
