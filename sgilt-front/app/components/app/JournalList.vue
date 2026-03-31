<template>
  <ul class="journal-list">
    <li v-for="entry in entries" :key="entry.id" class="journal-entry">
      <time class="journal-entry__date">{{ formatDateTime(entry.date) }}</time>
      <ul class="journal-entry__mods">
        <li v-for="(mod, i) in entry.modifications" :key="i" class="journal-mod">
          <span class="journal-mod__champ">{{ mod.champ }}&nbsp;:</span>
          <template v-if="mod.avant !== null && mod.avant !== ''">
            <span class="journal-mod__avant">{{ mod.avant }}</span>
            <span class="journal-mod__arrow" aria-hidden="true">→</span>
          </template>
          <span class="journal-mod__apres">{{ mod.apres }}</span>
        </li>
      </ul>
    </li>
  </ul>
</template>

<script setup lang="ts">
import type { JournalEntry } from '~/types/event'

defineProps<{ entries: JournalEntry[] }>()
</script>

<style scoped lang="scss">
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
    font-size: 0.75rem;
    font-weight: 600;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 0.04em;
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
