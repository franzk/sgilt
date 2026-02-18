<template>
  <DrawerRoot v-model:open="isOpen">
    <DrawerTrigger class="select-trigger">
      <div class="trigger-content">
        <slot name="left-icon" />
        <span :class="{ 'has-value': modelValue !== '-1' }" class="value">{{ selectedLabel }}</span>
      </div>
    </DrawerTrigger>

    <DrawerPortal>
      <DrawerOverlay class="sheet-overlay" />

      <DrawerContent class="sheet-content">
        <div class="sheet-handle" />

        <div class="sheet-body">
          <header class="sheet-header">
            <h3>Qu'est-ce qu'on fête ?</h3>
          </header>

          <div class="options-container">
            <button
              v-for="option in options"
              :key="option.value"
              class="option-item"
              :class="{ 'is-selected': modelValue === option.value }"
              @click="selectOption(option.value)"
            >
              <span class="option-label">{{ option.label }}</span>
              <div v-if="modelValue === option.value" class="check-mark">✓</div>
            </button>
          </div>
        </div>
      </DrawerContent>
    </DrawerPortal>
  </DrawerRoot>
</template>

<script setup lang="ts">
import { DrawerRoot, DrawerContent, DrawerOverlay, DrawerPortal, DrawerTrigger } from 'vaul-vue'

const modelValue = defineModel<string>()

interface Props {
  options: { value: string; label: string }[]
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
})

const emit = defineEmits(['update:modelValue'])

const isOpen = ref(false)

// On récupère le label pour l'afficher sur le bouton "déclencheur"
const selectedLabel = computed(() => {
  const option = props.options.find((o) => o.value === modelValue.value)
  return option ? option.label : ''
})

const selectOption = (val: string) => {
  modelValue.value = val
  emit('update:modelValue', val)
  isOpen.value = false // Fermeture automatique après le choix
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Style du bouton qui ouvre le menu
.select-trigger {
  width: 100%;
  display: flex;
  flex-direction: row;
  // align-items: center;
  // justify-content: space-between;
  // padding: 1rem 1.25rem;
  background: white;
  border: 1px solid $shadow-m; // ou ta variable
  border-radius: 0.875rem;
  font-size: inherit;
  letter-spacing: 0.02rem;
  height: 100%;

  box-shadow:
    0 0.0625rem 0 rgba(0, 0, 0, 0.04),
    0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);

  .trigger-content {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    color: $text-secondary;
    width: 100%;
    padding-left: 0.5rem;
    padding-right: 2rem;

    .value {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .has-value {
      color: $text-primary;
      font-weight: 500;
    }

    .left-icon {
      display: flex;
      flex-wrap: wrap;
      width: 2.5em;
      align-content: center;
      justify-content: center;
    }

    * {
      width: 1.5em;
      height: 1.5em;
    }
  }
}
// ==============================
// Sgilt Select Drawer (Vaul)
// ==============================

// Ajuste si tu as un accent ailleurs
$primary: $color-primary; // ton noir/charcoal Sgilt
$accent: $color-accent; // ton jaune Sgilt (ex: #ffbf00)
$sheet-radius: 28px;

$bg: #ffffff;
$muted: #f6f7f9;
$muted2: #eef0f3;

$shadow-sheet: 0 -28px 80px rgba(0, 0, 0, 0.28);
$shadow-pop: 0 10px 28px rgba(0, 0, 0, 0.1);

.select-trigger {
  width: 100%;
  border: 0;
  background: transparent;
  padding: 0;
  text-align: left;
  cursor: pointer;

  // iOS tap highlight off
  -webkit-tap-highlight-color: transparent;

  .value {
    flex: 1;
    font-weight: 650;
    color: rgba($primary, 0.55);

    &.has-value {
      color: $primary;
    }
  }

  &:active .trigger-content {
    transform: scale(0.987);
    box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
  }

  &:focus-visible .trigger-content {
    outline: none;
    border-color: rgba($accent, 0.55);
    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.06),
      0 0 0 4px rgba($accent, 0.22);
  }
}

// Overlay
.sheet-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;

  background: rgba(10, 10, 12, 0.48);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

// Content
.sheet-content {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1001;

  border-top-left-radius: $sheet-radius;
  border-top-right-radius: $sheet-radius;

  // Fond plus "chaud" + halo accent
  background:
    radial-gradient(
      1100px 520px at 50% -10%,
      rgba($color-accent, 0.22) 0%,
      rgba(255, 255, 255, 0) 55%
    ),
    linear-gradient(180deg, #fffdf6 0%, #ffffff 60%);

  // petit liseré + ombre
  border-top: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: $shadow-sheet;

  // safe area iOS
  padding-bottom: calc(1.1rem + env(safe-area-inset-bottom, 0px));
  outline: none;

  .sheet-handle {
    width: 52px;
    height: 6px;
    margin: 12px auto 10px;
    border-radius: 999px;
    background: rgba(0, 0, 0, 0.14);
  }

  .sheet-body {
    padding: 0.9rem 1.2rem 0.9rem;

    .sheet-header {
      margin: 0.25rem 0 1rem;

      h3 {
        margin: 0.25rem 0 0.9rem;
        text-align: center;

        font-size: 1.45rem;
        font-weight: 700;
        letter-spacing: -0.025em;
        line-height: 1.1;

        color: $color-primary;

        // petit boost de présence
        text-shadow:
          0 1px 0 rgba(255, 255, 255, 0.7),
          0 10px 26px rgba(0, 0, 0, 0.1);
      }

      // underline "signature" sous le titre (ça fait tout)
      h3::after {
        content: '';
        display: block;
        width: 72px;
        height: 4px;
        margin: 0.65rem auto 0;

        border-radius: 999px;
        background: linear-gradient(
          90deg,
          rgba($color-accent, 0) 0%,
          rgba($color-accent, 0.85) 20%,
          rgba($color-accent, 1) 50%,
          rgba($color-accent, 0.85) 80%,
          rgba($color-accent, 0) 100%
        );
        box-shadow: 0 10px 18px rgba($color-accent, 0.18);
      }
    }
  }
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

// Option button
.option-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 1.05rem 1rem 1.05rem 1.1rem;
  border-radius: 18px;

  background: linear-gradient(180deg, #ffffff 0%, #fbfbfb 100%);
  border: 1px solid rgba(0, 0, 0, 0.08);

  box-shadow:
    0 14px 34px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.85);

  color: $color-primary;
  font-weight: 750;
  font-size: 1rem;

  transition:
    transform 120ms ease,
    box-shadow 160ms ease,
    border-color 160ms ease;

  -webkit-tap-highlight-color: transparent;

  &:hover {
    border-color: rgba($color-accent, 0.22);
    box-shadow:
      0 14px 30px rgba(0, 0, 0, 0.08),
      0 0 0 3px rgba($color-accent, 0.1);
  }

  &:active {
    transform: scale(0.985);
    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.1),
      0 0 0 4px rgba($color-accent, 0.14);
  }

  &:focus-visible {
    outline: none;
    border-color: rgba($color-accent, 0.55);
    box-shadow:
      0 0 0 5px rgba($color-accent, 0.2),
      0 18px 40px rgba(0, 0, 0, 0.12);
  }

  .check-mark {
    min-width: 32px;
    height: 32px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;

    font-weight: 950;
    font-size: 0.95rem;

    background: rgba(0, 0, 0, 0.06);
    border: 1px solid rgba(0, 0, 0, 0.1);
    color: rgba($color-primary, 0.8);
  }

  &.is-selected {
    // Selected = "badge" assumé
    background: linear-gradient(180deg, rgba($color-accent, 1) 0%, rgba($color-accent, 0.86) 100%);
    border-color: rgba($color-accent, 0.65);

    color: #111;

    box-shadow:
      0 22px 50px rgba($color-accent, 0.26),
      0 14px 34px rgba(0, 0, 0, 0.1);

    &::before {
      background: rgba(0, 0, 0, 0.18);
      opacity: 1;
    }

    .check-mark {
      background: rgba(0, 0, 0, 0.14);
      border-color: rgba(0, 0, 0, 0.16);
      color: #111;
    }
  }
}

/* // L'overlay (fond sombre)
.sheet-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  z-index: 100;
}

// Le panneau blanc
.sheet-content {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  z-index: 101;
  border-top-left-radius: 32px; // L'arrondi de la maquette
  border-top-right-radius: 32px;
  padding-bottom: 2rem; // Espace pour la barre de navigation mobile
  outline: none;

  .sheet-handle {
    width: 40px;
    height: 5px;
    background: #e5e7eb;
    border-radius: 10px;
    margin: 12px auto;
  }

  .sheet-body {
    padding: 1rem 1.5rem;

    .sheet-header h3 {
      font-size: 1.25rem;
      font-weight: 800;
      text-align: center;
      margin-bottom: 1.5rem;
      color: $color-primary;
    }
  }
}

// Les items de la liste
.option-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem;
  margin-bottom: 0.5rem;
  border-radius: 1rem;
  background: #f9fafb;
  border: none;
  font-weight: 600;
  transition: all 0.2s;

  &.is-selected {
    background: $color-primary; // Ton jaune S'GILT
    color: white;
  }

  &:active {
    transform: scale(0.98);
  } 
} */
</style>
