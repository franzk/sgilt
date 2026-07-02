<template>
  <SgiltDialog v-model:open="open" :title="$t('journal.title')">
    <div class="journal-content">
      <JournalList :entries="journal" />
      <button
        v-if="hasMore"
        class="load-more"
        type="button"
        :disabled="loading"
        @click="emit('load-more')"
      >
        {{ loading ? $t('journal.loading') : $t('journal.load-more') }}
      </button>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import type { JournalEntry } from '~/data/evenement/domain/JournalEntry'
import JournalList from '~/components/app/JournalList.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'

const open = defineModel<boolean>('open', { required: true })

defineProps<{
  journal: JournalEntry[]
  hasMore?: boolean
  loading?: boolean
}>()

const emit = defineEmits<{ 'load-more': [] }>()
</script>

<style scoped lang="scss">
.journal-content {
  background: #fff;
  border-radius: $radius-lg $radius-lg 0 0;
  padding: $spacing-m $spacing-m calc($spacing-l + env(safe-area-inset-bottom, 0px));
  max-height: 60vh;
  overflow: auto;

  .load-more {
    display: block;
    width: 100%;
    margin-top: $spacing-m;
    padding: $spacing-s;
    background: none;
    border: 1px solid $divider-color;
    border-radius: $radius-md;
    font-family: inherit;
    font-size: 0.875rem;
    color: $text-secondary;
    cursor: pointer;
    transition: background 120ms ease;

    &:hover:not(:disabled) {
      background: $surface-soft;
    }
    &:disabled {
      opacity: 0.5;
      cursor: default;
    }
  }
}
</style>
