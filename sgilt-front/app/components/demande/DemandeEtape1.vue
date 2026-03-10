<template>
  <div class="etape">
    <h2 class="etape__question">Quel événement préparez-vous&nbsp;?</h2>

    <DemandeOptionSelect
      :options="EVENT_TYPE_OPTIONS"
      :model-value="state.eventType"
      :autre-value="state.eventTypeAutre"
      autre-placeholder="Quel événement préparez-vous&nbsp;?"
      @update:model-value="state.eventType = $event"
      @update:autre-value="state.eventTypeAutre = $event"
      @change="$emit('change')"
    />

    <div class="flow">
      <p class="flow__title">Comment ça marche ?</p>
      <div class="flow__steps">
        <div class="flow__step">
          <span class="flow__dot">1</span>
          <span>Décrivez votre événement en quelques clics</span>
        </div>
        <div class="flow__step">
          <span class="flow__dot">2</span>
          <span>Validez votre demande par email</span>
        </div>
        <div class="flow__step">
          <span class="flow__dot">3</span>
          <span>Le prestataire vous répond</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { EVENT_TYPE_OPTIONS } from '~/types/demande'
import type { DemandeState } from '~/types/demande'

defineProps<{ state: DemandeState }>()

const emit = defineEmits<{
  (e: 'change'): void
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.etape {
  &__question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-l;
    line-height: 1.3;
  }
}

.flow {
  margin-top: $spacing-l;
  padding: $spacing-m $spacing-m;
  border-top: 1px solid $divider-color;

  &__title {
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0 0 $spacing-m;
  }

  &__steps {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  &__step {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    font-size: 0.9rem;
    color: $text-secondary;
  }

  &__dot {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 1.4rem;
    height: 1.4rem;
    border-radius: 50%;
    background: rgba($brand-accent, 0.15);
    color: $brand-accent;
    font-size: 0.7rem;
    font-weight: 700;
    flex-shrink: 0;
  }
}
</style>
