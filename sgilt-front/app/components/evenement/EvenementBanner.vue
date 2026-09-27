<template>
  <AppBanner tone="light" role="region" :aria-label="$t('evenement.banner.label')">
    <div class="layout">
      <div class="left">
        <NuxtLink
          to="/evenement"
          class="home"
          :title="$t('evenement.banner.board')"
          :aria-label="$t('evenement.banner.board')"
        >
          <Home2Icon class="icon" aria-hidden="true" />
        </NuxtLink>
        <span class="type">
          <component :is="typeIcon" class="type-icon" aria-hidden="true" />
          {{ typeLabel }}
        </span>
      </div>

      <div class="date-field">
        <SgiltDatePicker
          v-model="pickerDate"
          :placeholder="$t('evenement.banner.date-placeholder')"
        />
      </div>

      <div class="actions">
        <button
          class="save"
          type="button"
          :title="$t('evenement.banner.save')"
          :aria-label="$t('evenement.banner.save')"
          @click="onSave"
        >
          <Save3Icon class="save-icon" aria-hidden="true" />
          <span class="save-label">{{ $t('evenement.banner.save') }}</span>
        </button>
        <button
          class="reset"
          type="button"
          :aria-label="$t('evenement.banner.reset')"
          @click="resetDialogOpen = true"
        >
          <DeleteBin6Icon class="icon" aria-hidden="true" />
        </button>
      </div>
    </div>
  </AppBanner>

  <SgiltConfirmDialog
    v-model:open="resetDialogOpen"
    :title="$t('evenement.banner.reset-dialog.title')"
    :message="$t('evenement.banner.reset-dialog.message')"
    :confirm-label="$t('evenement.banner.reset-dialog.confirm')"
    :cancel-label="$t('evenement.banner.reset-dialog.cancel')"
    destructive
    max-width="400px"
    @confirm="onReset"
  />
</template>

<script setup lang="ts">
import { DeleteBin6Icon, Home2Icon, Save3Icon, SparklingIcon } from '@remixicons/vue/line'
import AppBanner from '~/components/app/AppBanner.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import { EVENT_TYPE_CATALOG } from '~/utils/eventTypes'

const { t } = useI18n()
const { localEvent, reset } = useLocalEvent()

// Le type n'est pas modifiable : simple rappel, affiché à partir du desktop.
const typeKey = computed(() => localEvent.eventType ?? 'autre')
const typeIcon = computed(
  () => EVENT_TYPE_CATALOG.find((type) => type.key === typeKey.value)?.icon ?? SparklingIcon,
)
const typeLabel = computed(() => t(`event-picker.types.${typeKey.value}.label`))

// L'effacement du sélecteur renvoie null : on le ramène à « pas de date ».
const pickerDate = computed<Date | undefined>({
  get: () => localEvent.date,
  set: (value) => {
    localEvent.date = value ?? undefined
  },
})

function onSave() {
  // TODO(brief séparé) : parcours d'inscription par email (création de l'espace utilisateur).
  console.log('stay tuned')
}

const resetDialogOpen = ref(false)

function onReset() {
  reset()
  navigateTo('/fete')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Trois colonnes : le contenu de gauche, le sélecteur de date, les actions. Les colonnes
// latérales ne descendent jamais sous la largeur de leur contenu (max-content) : quand la place
// manque, c'est le sélecteur qui rétrécit, il ne chevauche jamais les boutons. Avec assez de
// place, les deux côtés sont égaux et le sélecteur est exactement centré sur la barre.
.layout {
  display: grid;
  grid-template-columns: minmax(max-content, 1fr) minmax(6rem, 13rem) minmax(max-content, 1fr);
  align-items: center;
  column-gap: $spacing-xs;
  width: 100%;
}

.left {
  display: flex;
  align-items: center;
  justify-self: start;
  gap: $spacing-xs;

  .home {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2rem;
    height: 2rem;
    border: 1px solid $divider-color;
    border-radius: 50%;
    background: $surface-white;
    color: $text-primary;

    &.router-link-exact-active {
      border-color: $brand-accent;
    }

    .icon {
      width: 1rem;
      height: 1rem;
    }
  }

  .type {
    display: none;
    align-items: center;
    gap: $spacing-xxs;
    color: $text-primary;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    white-space: nowrap;

    @media (min-width: $breakpoint-desktop) {
      display: inline-flex;
    }

    .type-icon {
      width: 1.25rem;
      height: 1.25rem;
    }
  }
}

// Le sélecteur remplit sa colonne (width: 100%). Champ compacté (police et padding) pour
// laisser une marge verticale dans le bandeau.
.date-field {
  min-width: 0;
  font-size: 0.85rem;

  :deep(.dp__theme_light) {
    --dp-input-padding: 0.5rem 2.5rem 0.5rem 0.5rem;
  }
}

.actions {
  display: flex;
  align-items: center;
  justify-self: end;
  gap: 0.25rem;

  @media (min-width: $breakpoint-desktop) {
    gap: $spacing-xs;
  }

  // Mobile : bouton rond avec icône seule. Desktop : bouton pilule avec le libellé.
  .save {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2rem;
    height: 2rem;
    padding: 0;
    border: none;
    border-radius: 9999px;
    background: $brand-accent;
    color: $brand-primary;
    font-family: inherit;
    font-size: $font-size-xs;
    font-weight: $font-weight-bold;
    white-space: nowrap;
    cursor: pointer;

    .save-icon {
      width: 1rem;
      height: 1rem;
    }

    .save-label {
      display: none;
    }

    @media (min-width: $breakpoint-desktop) {
      width: auto;
      padding: 0 0.9rem;

      .save-icon {
        display: none;
      }

      .save-label {
        display: inline;
      }
    }
  }

  .reset {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2rem;
    height: 2rem;
    padding: 0;
    border: 1px solid $divider-color;
    border-radius: 50%;
    background: $surface-white;
    color: $text-secondary;
    cursor: pointer;

    .icon {
      width: 1rem;
      height: 1rem;
    }
  }
}
</style>
