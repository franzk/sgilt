<template>
  <div v-if="hasContent" class="recap">
    <p class="recap__title">Votre événement</p>
    <ul class="recap__list">
      <li v-if="eventTypeLabel" class="recap__item">
        <span class="recap__emoji">{{ eventTypeEmoji }}</span>
        <span>{{ eventTypeLabel }}</span>
      </li>
      <li v-if="ambianceLabel" class="recap__item">
        <span class="recap__emoji">{{ ambianceEmoji }}</span>
        <span>{{ ambianceLabel }}</span>
      </li>
      <li v-if="momentCleLabel" class="recap__item">
        <span class="recap__emoji">{{ momentCleEmoji }}</span>
        <span>{{ momentCleLabel }}</span>
      </li>
      <li v-if="date" class="recap__item">
        <span class="recap__emoji">📅</span>
        <span>{{ formatDate(date) }}</span>
      </li>
      <li v-if="ville" class="recap__item">
        <span class="recap__emoji">📍</span>
        <span>{{ ville }}</span>
      </li>
      <li v-if="nbInvites" class="recap__item">
        <span class="recap__emoji">👥</span>
        <span>{{ nbInvites }} invités</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  eventTypeLabel: string | null
  eventTypeEmoji: string
  ambianceLabel: string | null
  ambianceEmoji: string
  momentCleLabel?: string | null
  momentCleEmoji?: string
  date?: Date | null | undefined
  ville?: string
  nbInvites?: string
}>()

const hasContent = computed(
  () => !!props.eventTypeLabel || !!props.ambianceLabel || !!props.momentCleLabel,
)

function formatDate(d: Date) {
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}
</script>

<style scoped lang="scss">
.recap {
  margin-top: $spacing-l;
  padding: $spacing-m;
  background: $surface-soft;
  border: 1px solid $divider-color;
  border-radius: $radius-md;

  &__title {
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0 0 $spacing-xs;
  }

  &__list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__item {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-size: 0.88rem;
    color: $text-primary;
    line-height: 1.4;
  }

  &__emoji {
    font-size: 1rem;
    flex-shrink: 0;
    width: 1.4rem;
    text-align: center;
  }
}
</style>
