<template>
  <div v-if="prestataireName || hasContent" class="recap-card">
    <!-- Cover -->
    <div v-if="prestataireImage" class="cover-wrap">
      <img class="cover" :src="prestataireImage" :alt="prestataireName ?? ''" />
    </div>

    <!-- Identité -->
    <div class="identity">
      <span v-if="prestataireName" class="name">{{ prestataireName }}</span>
      <div v-if="parsedDate" class="date">
        <span class="day">{{ parsedDate.day }}</span>
        <div class="date-right">
          <span class="month">{{ parsedDate.month }}</span>
          <span class="year">{{ parsedDate.year }}</span>
        </div>
      </div>
    </div>

    <!-- Séparateur + items -->
    <template v-if="hasContent">
      <div class="divider" />
      <ul class="items">
        <template v-for="(item, idx) in visibleItems" :key="item.key">
          <li class="item">
            <span class="emoji">{{ item.emoji }}</span>
            <span class="text">{{ item.text }}</span>
          </li>
          <div v-if="idx < visibleItems.length - 1" class="item-divider" />
        </template>
      </ul>
    </template>

    <!-- Teaser encouragement -->
    <div class="teaser">
      <p class="teaser-title">✨ Presque terminé !</p>
      <p class="teaser-body">
        Ajoutez quelques détails pour recevoir des propositions encore plus précises.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{
  prestataireName?: string
  prestataireImage?: string
  fullDetails?: boolean
}>()

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

const parsedDate = computed(() => {
  if (!state.date) return null
  const d = new Date(state.date)
  return {
    day: d.getDate(),
    month: d.toLocaleDateString('fr-FR', { month: 'long' }),
    year: d.getFullYear(),
  }
})

const visibleItems = computed(() => {
  const items: { key: string; emoji: string; text: string }[] = []
  if (eventTypeLabel.value)
    items.push({
      key: 'eventType',
      emoji: eventTypeEmoji.value || '🎉',
      text: eventTypeLabel.value,
    })
  if (ambianceLabel.value)
    items.push({ key: 'ambiance', emoji: ambianceEmoji.value || '✨', text: ambianceLabel.value })
  if (momentCleLabel.value)
    items.push({
      key: 'momentCle',
      emoji: momentCleEmoji.value || '⭐',
      text: momentCleLabel.value,
    })
  if (state.ville) items.push({ key: 'ville', emoji: '📍', text: state.ville })
  if (state.lieuDefini && state.lieu) items.push({ key: 'lieu', emoji: '🏛️', text: state.lieu })
  if (state.nbInvites) items.push({ key: 'nbInvites', emoji: '👥', text: state.nbInvites })
  if (props.fullDetails && state.email) items.push({ key: 'email', emoji: '📧', text: state.email })
  if (props.fullDetails && state.prestataireMessage)
    items.push({ key: 'message', emoji: '💬', text: state.prestataireMessage })
  return items
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.recap-card {
  margin-top: 48px;
  background: #fff;
  border-radius: $radius-lg;
  box-shadow:
    0 2px 8px $shadow-s,
    0 8px 28px $shadow-m;
  overflow: hidden;
}

// ─── Cover ────────────────────────────────────────────────────────────────────
.cover-wrap {
  width: 100%;
  height: 140px;
  overflow: hidden;
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

// ─── Identité ─────────────────────────────────────────────────────────────────
.identity {
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  align-items: center;
}

.name {
  text-align: center;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: $text-secondary;
}

.date {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  justify-content: center;
}

.day {
  font-size: 2.75rem;
  font-weight: 600;
  color: $text-primary;
  line-height: 1;
}

.date-right {
  display: flex;
  flex-direction: column;
  gap: 1px;
  padding-bottom: 3px;
}

.month {
  font-size: 0.875rem;
  font-weight: 600;
  color: $text-primary;
  text-transform: uppercase;
}

.year {
  font-size: 0.75rem;
  color: $text-secondary;
}

// ─── Séparateur ───────────────────────────────────────────────────────────────
.divider {
  height: 1px;
  background: $divider-color;
}

// ─── Items ────────────────────────────────────────────────────────────────────
.items {
  list-style: none;
  padding: $spacing-xxs 0 $spacing-s;
  margin: 0;
}

.item {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
  font-size: 0.875rem;
  color: $text-primary;
  line-height: 1.4;
}

.emoji {
  font-size: 1rem;
  flex-shrink: 0;
  width: 1.25rem;
  text-align: center;
  margin-top: 0.1rem;
}

.text {
  min-width: 0;
  word-break: break-word;
}

.item-divider {
  height: 1px;
  background: $divider-color;
  margin: 0 $spacing-m;
}

// ─── Teaser ───────────────────────────────────────────────────────────────────
.teaser {
  border-top: 1px solid $divider-color;
  padding: $spacing-m;
  background: $surface-soft;
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;
}

.teaser-title {
  font-size: 0.82rem;
  font-weight: 700;
  color: $text-primary;
  margin: 0;
}

.teaser-body {
  font-size: 0.78rem;
  color: $text-secondary;
  line-height: 1.5;
  margin: 0;
}
</style>
