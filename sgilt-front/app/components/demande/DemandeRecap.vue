<template>
  <div v-if="hasContent" class="demande-recap">
    <p class="title">Votre événement</p>
    <ul class="list">
      <!-- Date -->
      <li v-if="state.date" class="item">
        <span class="emoji">📅</span>
        <span>{{ formatDate(state.date) }}</span>
      </li>

      <!-- Type d'événement -->
      <li v-if="eventTypeLabel" class="item">
        <span class="emoji">{{ eventTypeEmoji }}</span>
        <span>{{ eventTypeLabel }}</span>
      </li>

      <!-- Ambiance -->
      <li v-if="ambianceLabel" class="item">
        <span class="emoji">{{ ambianceEmoji }}</span>
        <span>{{ ambianceLabel }}</span>
      </li>

      <!-- Moment clé -->
      <li v-if="momentCleLabel" class="item">
        <span class="emoji">{{ momentCleEmoji }}</span>
        <span>{{ momentCleLabel }}</span>
      </li>

      <!-- Ville -->
      <li v-if="state.ville" class="item">
        <span class="emoji">📍</span>
        <span>{{ state.ville }}</span>
      </li>

      <!-- Nombre d'invités -->
      <li v-if="state.nbInvites" class="item">
        <span class="emoji">👥</span>
        <span>{{ state.nbInvites }} invités</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { useDemande } from '~/composables/useDemande'

const {
  state,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande()

const hasContent = computed(
  () => !!eventTypeLabel.value || !!ambianceLabel.value || !!momentCleLabel.value,
)

</script>

<style scoped lang="scss">
.demande-recap {
  margin-top: $spacing-l;
  padding: $spacing-m;
  background: $surface-soft;
  border: 1px solid $divider-color;
  border-radius: $radius-md;

  .title {
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0 0 $spacing-xs;
  }

  .list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  .item {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-size: 0.88rem;
    color: $text-primary;
    line-height: 1.4;
  }

  .emoji {
    font-size: 1rem;
    flex-shrink: 0;
    width: 1.4rem;
    text-align: center;
  }
}
</style>
