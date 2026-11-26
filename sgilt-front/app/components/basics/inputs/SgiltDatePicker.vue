<!--
  Sélecteur de date Sgilt (VueDatePicker), en champ ou en calendrier inline. En mode consultation
  (`displayOnly`), les `bookedDates` sont affichées comme une sélection multiple non modifiable,
  avec une légende sous le calendrier.
-->
<template>
  <ClientOnly>
    <VueDatePicker
      :model-value="displayOnly ? bookedDates : date"
      :multi-dates="displayOnly"
      auto-apply
      :disabled="disabled"
      :time-config="{ enableTimePicker: false }"
      :locale="fr"
      :formats="format"
      class="sgilt-date-picker"
      :class="{
        'sgilt-date-picker--error': props.error,
        'sgilt-date-picker--fullwidth': props.fullwidth,
        'sgilt-date-picker--display-only': props.displayOnly,
      }"
      :placeholder="placeholder"
      :min-date="displayOnly ? undefined : new Date()"
      :disabled-dates="displayOnly ? disableEveryDay : undefined"
      :teleport="inline ? false : 'body'"
      :inline="inline"
      @update:model-value="onModelUpdate"
      @open="onOpen"
      @closed="onClosed"
      @update-month-year="emit('update-month-year', $event)"
    >
      <template v-if="showExtraInfo" #action-extra>
        <p class="extra-info">
          <span class="dot booked" />
          <span>{{ $t('common.date-picker.booked') }}</span>
        </p>
      </template>
    </VueDatePicker>

    <template #fallback>
      <div v-if="inline" class="calendar-skeleton">
        <div class="calendar-skeleton__header">
          <Sk width="1.75rem" height="1.75rem" radius="0.4rem" />
          <Sk width="8rem" height="1rem" radius="0.4rem" />
          <Sk width="1.75rem" height="1.75rem" radius="0.4rem" />
        </div>
        <div class="calendar-skeleton__weekdays">
          <Sk v-for="i in 7" :key="i" height="0.75rem" radius="0.3rem" />
        </div>
        <div class="calendar-skeleton__grid">
          <Sk v-for="i in 35" :key="i" radius="0.5rem" class="calendar-cell" />
        </div>
      </div>
      <div v-else class="select-skeleton">
        <Sk width="1.5rem" height="1.5rem" radius="6px" />
        <Sk />
      </div>
    </template>
  </ClientOnly>
</template>

<script setup lang="ts">
import { VueDatePicker } from '@vuepic/vue-datepicker'
import Sk from '~/components/basics/Sk.vue'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import { fr } from 'date-fns/locale/fr'
import { computed, nextTick } from 'vue'

const date = defineModel<Date>()

const emit = defineEmits<{
  'update-month-year': [value: { month: number; year: number }]
}>()

const format = { input: (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY') }

const props = defineProps<{
  /** Dates réservées via Sgilt — prises en compte uniquement en mode consultation (`displayOnly`). */
  bookedDates?: Date[]
  disabled?: boolean
  placeholder?: string
  inline?: boolean
  error?: boolean
  /** Calendrier inline qui remplit son conteneur au lieu de garder sa taille intrinsèque fixe. */
  fullwidth?: boolean
  /** Calendrier de consultation : les jours ne sont pas sélectionnables, seule la navigation entre mois reste active. */
  displayOnly?: boolean
}>()

// En consultation, tous les jours sont désactivés : la lib n'en laisse sélectionner aucun (clic ou
// clavier), tout en affichant la sélection et en gardant la navigation entre mois.
const disableEveryDay = (): boolean => true

// En consultation, la sélection n'est pas modifiable : rien n'est remonté.
function onModelUpdate(value: unknown): void {
  if (props.displayOnly) return
  date.value = value instanceof Date ? value : undefined
}

const showExtraInfo = computed(() => props.displayOnly)

// ── Focus trap pour navigation clavier ───────────────────────────────────────

const FOCUSABLE = 'button:not([disabled]), [tabindex]:not([tabindex="-1"])'

let _menuEl: HTMLElement | null = null
let _trapHandler: ((e: KeyboardEvent) => void) | null = null

function onOpen() {
  if (props.inline) return
  nextTick(() => {
    _menuEl = document.querySelector<HTMLElement>('.dp__menu')
    if (!_menuEl) return

    const first = _menuEl.querySelector<HTMLElement>(FOCUSABLE)
    ;(first ?? _menuEl).focus()

    _trapHandler = (e: KeyboardEvent) => {
      if (e.key !== 'Tab') return
      const els = Array.from(_menuEl!.querySelectorAll<HTMLElement>(FOCUSABLE)).filter(
        (el) => el.offsetParent !== null,
      )
      if (!els.length) return
      const first = els[0]!
      const last = els[els.length - 1]!
      if (e.shiftKey && document.activeElement === first) {
        e.preventDefault()
        last.focus()
      } else if (!e.shiftKey && document.activeElement === last) {
        e.preventDefault()
        first.focus()
      }
    }
    _menuEl.addEventListener('keydown', _trapHandler)
  })
}

function onClosed() {
  if (_menuEl && _trapHandler) {
    _menuEl.removeEventListener('keydown', _trapHandler)
  }
  _menuEl = null
  _trapHandler = null
}
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens ───────────────────────────────────────────────────────────────────

// input
$input-padding: 0.75rem;
$input-font-weight: 500;
$input-letter-spacing: 0.005rem;
$placeholder-text-opacity: 1;
$icon-color: $color-accent;
$input-text-color: rgba($color-primary, 0.8);
$icon-width: 2.5rem;
$icon-stroke-width: 1px;

// calendar
$calendar-text-color: $color-primary;
$today-border-color: $color-accent;
$cell-hover-color: rgba($color-accent, 0.14);
$cell-border-radius: 0.5em;
$cell-padding: 1.2rem;
$cell-padding-small: 1rem;
$cell-padding-fullwidth: 0.25rem;
$menu-border-radius: 1.4rem;
$menu-box-shadow: 0 2px 8px $shadow-s;
$menu-background: $surface-white;

// ─── Composant ────────────────────────────────────────────────────────────────

.sgilt-date-picker--error {
  --dp-border-color: #{$state-error};
  --dp-hover-color: #{rgba($state-error, 0.06)};
}

.sgilt-date-picker {
  width: 100%;
  * {
    font-size: inherit;
  }
}

// ─── Mode fullwidth (calendrier inline qui remplit son conteneur) ─────────────
// width:100% sur .dp__menu ne sert à rien : la lib le positionne en interne
// (logique de popup réutilisée même en mode inline) et son width résout en
// "auto" (shrink-to-fit) faute de containing block bien défini — c'est la
// taille des cellules qui détermine la taille du calendrier, pas l'inverse.
// Pour inverser cette dépendance sans halluciner une largeur en vw (qui
// décrocherait dès que le conteneur est plafonné par un max-width), on fait
// de .sgilt-date-picker un container de taille (container-type: inline-size)
// — il a un width:100% explicite et non shrink-wrap (règle juste au-dessus),
// donc pas de dépendance circulaire — puis on calcule chaque cellule en
// unités cqw (% de CE conteneur), littéralement 100cqw / 7 colonnes.
.sgilt-date-picker--fullwidth {
  container-type: inline-size;

  .dp__theme_light {
    --dp-cell-padding: #{$cell-padding-fullwidth};
  }

  .dp__calendar_header_item,
  .dp__cell_inner {
    // Largeur pilotée par le conteneur réel (cqw)
    width: calc((100cqw - 2 * var(--dp-menu-padding, 0px)) / 7);
  }
}

// ─── Mode consultation ───────────────────────────────────────────────────────
// Tous les jours y sont désactivés (disableEveryDay) : on retire l'apparence « désactivé » de la
// lib (texte grisé, curseur interdit) pour garder un calendrier normal à lire.
.sgilt-date-picker--display-only {
  .dp__cell_disabled {
    cursor: default;

    &:not(.dp__cell_offset) {
      color: var(--dp-text-color);
    }

    &.dp__active_date {
      color: var(--dp-primary-text-color);
    }
  }
}

.dp__theme_light {
  --dp-border-radius: #{$input-border-radius};
  --dp-border-color: #{$input-border-color};
  --dp-input-padding: #{$input-padding};
  --dp-font-size: inherit;
  --dp-text-color: #{$calendar-text-color};
  --dp-primary-color: #{$today-border-color};
  --dp-hover-color: #{$cell-hover-color};
  --dp-icon-color: #{$icon-color};
  --dp-success-color: #{$state-option};
  --dp-cell-border-radius: #{$cell-border-radius};
  --dp-cell-padding: #{$cell-padding};
}

@media (min-width: $breakpoint-desktop) {
  .dp__theme_light {
    --dp-cell-padding: #{$cell-padding-small};
  }
}
@media (max-width: 300px) {
  .dp__theme_light {
    --dp-cell-padding: #{$cell-padding-small};
  }
}

.dp__input {
  text-align: center;
  padding-right: 2.5rem;
  font-size: inherit;
  color: $input-text-color;
  font-weight: $input-font-weight;
  height: 100%;
  box-shadow: $input-box-shadow;
  font-family: inherit;
  line-height: 1;
  letter-spacing: $input-letter-spacing;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  &::placeholder {
    font-weight: $input-font-weight;
    font-family: inherit;
    opacity: $placeholder-text-opacity;
    font-size: inherit;
  }

  &:focus {
    border-color: $input-focus-border-color;
    box-shadow: $input-focus-box-shadow;
  }
}

.dp__input_icon {
  width: $icon-width;
  margin: 0;
  padding: 0;
  stroke-width: $icon-stroke-width;
}

.dp__input_wrap {
  font-size: inherit;
}

.dp__menu {
  border-radius: $menu-border-radius;
  box-shadow: $menu-box-shadow;
  background: $menu-background;
}

.dp__month_year_wrap {
  font-size: inherit;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.dp__calendar_header_item {
  padding-top: 0;
}
.dp__active_date {
  font-weight: bold;
}

// ─── Légende (action-extra) ───────────────────────────────────────────────────

.dot {
  display: inline-block;
  width: 0.55em;
  height: 0.55em;
  border-radius: 50%;
  margin: 0 0.3em;
  vertical-align: middle;

  &.booked {
    background: var(--dp-primary-color);
  }
}

.extra-info {
  color: $color-subtext;
  margin-left: $spacing-s;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 0.2em;
  font-size: 0.8rem;
}

// ─── Skeleton inline ──────────────────────────────────────────────────────────

.select-skeleton {
  width: 100%;
  height: 3.5rem;
  border-radius: 0.875rem;
  background: #f4f4f4;
  border: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0 1rem;
  box-shadow:
    0 0.0625rem 0 rgba(0, 0, 0, 0.04),
    0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.calendar-skeleton {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  width: 100%;
  // Remplit tout l'espace disponible dans le conteneur parent (utile pour le calendrier inline)
  flex: 1;
  height: 100%;
  box-sizing: border-box;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0.25rem;
  }

  &__weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 0.25rem;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    grid-template-rows: repeat(5, 1fr);
    gap: 0.25rem;
    flex: 1;
  }
}
</style>
