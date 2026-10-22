<template>
  <div class="parametres">
    <div class="column">
      <button class="back" type="button" @click="backToBoard">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.settings.back') }}
      </button>

      <!-- Chaque panneau validé s'applique aussitôt à l'événement local. Aucune information
           obligatoire ici : le nom ne peut pas être vidé (refusé dans son panneau), date, type et
           ville ne sont exigés qu'à l'envoi d'une demande. -->
      <EventInfoCards :event="localEvent" with-title @update="Object.assign(localEvent, $event)" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon } from '@remixicons/vue/line'
import EventInfoCards from '~/components/evenement/EventInfoCards.vue'

definePageMeta({ layout: 'evenement' })

const { t } = useI18n()
useHead({ title: t('evenement.settings.page-title') })

const { localEvent } = useLocalEvent()

// ── Navigation ────────────────────────────────────────────────────────────────
function backToBoard() {
  navigateTo('/evenement')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.parametres {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .back {
      display: inline-flex;
      align-items: center;
      align-self: flex-start;
      gap: $spacing-xxs;
      padding: 0;
      border: none;
      background: none;
      color: $text-primary;
      font-family: inherit;
      font-size: $font-size-sm;
      cursor: pointer;

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }
  }
}
</style>
