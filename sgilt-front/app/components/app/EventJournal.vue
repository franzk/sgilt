<template>
  <SgiltDialog v-model:open="open" :title="$t('journal.title')">
    <div class="journal-content">
      <JournalList :entries="entries" />
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import type { JournalEntry } from '~/types/event'
import JournalList from '~/components/app/JournalList.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'

const open = defineModel<boolean>('open', { required: true })

const props = defineProps<{ journal: JournalEntry[] }>()

const entries = computed(() => [...props.journal].reverse())
</script>

<style scoped lang="scss">
.journal-content {
  background: #fff;
  border-radius: $radius-lg $radius-lg 0 0;
  padding: $spacing-m $spacing-m calc($spacing-l + env(safe-area-inset-bottom, 0px));
  max-height: 60vh;
  overflow: auto;
}
</style>
