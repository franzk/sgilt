<template>
  <div class="parametres">
    <div class="column">
      <button class="back" type="button" @click="backToBoard">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.settings.back') }}
      </button>

      <!-- Les panneaux alimentent le brouillon ; l'événement n'est modifié qu'à l'enregistrement. -->
      <EventInfoCards :event="draft" with-title @update="Object.assign(draft, $event)" />

      <!-- ── Actions ──────────────────────────────────────────────────────────── -->
      <div class="actions">
        <SgiltButton variant="secondary" @click="backToBoard">
          {{ $t('common.cancel') }}
        </SgiltButton>
        <SgiltButton @click="save">{{ $t('common.save') }}</SgiltButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import EventInfoCards from '~/components/evenement/EventInfoCards.vue'
import type { EventInfoFields } from '~/composables/useLocalEvent'

definePageMeta({ layout: 'evenement' })

const { t } = useI18n()
useHead({ title: t('evenement.settings.page-title') })

const { localEvent } = useLocalEvent()

// ── Brouillon ─────────────────────────────────────────────────────────────────
// Les modifications ne touchent localEvent qu'à l'enregistrement : annuler ou quitter
// la page les abandonne.
const draft = reactive<EventInfoFields>({
  title: localEvent.title,
  eventType: localEvent.eventType,
  eventTypeAutre: localEvent.eventTypeAutre,
  date: localEvent.date,
  ville: localEvent.ville,
  lieu: localEvent.lieu,
  nbInvites: localEvent.nbInvites,
  ambiance: localEvent.ambiance,
  ambianceAutre: localEvent.ambianceAutre,
  momentCle: localEvent.momentCle,
  momentCleAutre: localEvent.momentCleAutre,
  description: localEvent.description,
})

// ── Actions ───────────────────────────────────────────────────────────────────
function backToBoard() {
  navigateTo('/evenement')
}

// Aucune information obligatoire ici : le nom ne peut pas être vidé (refusé dans son panneau),
// date, type et ville ne sont exigés qu'à l'envoi d'une demande.
function save() {
  Object.assign(localEvent, draft)
  backToBoard()
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

    .actions {
      display: flex;
      justify-content: flex-end;
      gap: $spacing-s;
    }
  }
}
</style>
