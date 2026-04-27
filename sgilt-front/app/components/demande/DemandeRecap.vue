<template>
  <div v-if="hasContent" class="demande-recap">
    <p class="title">{{ $t('tunnel.recap.title') }}</p>
    <ul class="list">
      <li v-if="state.date" class="item">
        <span class="emoji">📅</span>
        <span>{{ formatDate(state.date) }}</span>
      </li>

      <li v-if="eventTypeLabel" class="item">
        <span class="emoji">{{ eventTypeEmoji }}</span>
        <span>{{ eventTypeLabel }}</span>
      </li>

      <li v-if="ambianceLabel" class="item">
        <span class="emoji">{{ ambianceEmoji }}</span>
        <span>{{ ambianceLabel }}</span>
      </li>

      <li v-if="momentCleLabel" class="item">
        <span class="emoji">{{ momentCleEmoji }}</span>
        <span>{{ momentCleLabel }}</span>
      </li>

      <li v-if="state.ville" class="item">
        <span class="emoji">📍</span>
        <span>{{ state.ville }}</span>
      </li>

      <li v-if="state.lieuDefini && state.lieu" class="item">
        <span class="emoji">🏛️</span>
        <span>{{ state.lieu }}</span>
      </li>

      <li v-if="state.nbInvites" class="item">
        <span class="emoji">👥</span>
        <span>{{ state.nbInvites }}</span>
      </li>

      <template v-if="fullDetails">
        <li v-if="state.email" class="item">
          <span class="emoji">📧</span>
          <span>{{ state.email }}</span>
        </li>

        <li v-if="state.prestataireMessage" class="item item--message">
          <span class="emoji">💬</span>
          <span class="message">{{ state.prestataireMessage }}</span>
        </li>
      </template>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { useDemande } from '~/composables/useDemande'

defineProps<{ fullDetails?: boolean }>()

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
    align-items: flex-start;
    gap: $spacing-xs;
    font-size: 0.88rem;
    color: $text-primary;
    line-height: 1.4;

    &--message {
      align-items: flex-start;
    }
  }

  .emoji {
    font-size: 1rem;
    flex-shrink: 0;
    width: 1.4rem;
    text-align: center;
    margin-top: 0.05rem;
  }

  .message {
    white-space: pre-wrap;
    word-break: break-word;
  }
}
</style>
