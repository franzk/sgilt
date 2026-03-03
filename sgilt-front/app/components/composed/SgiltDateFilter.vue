<template>
  <div class="sgilt-date-filter">
    <button class="nav-btn prev" @click="prevDay()" aria-label="Jour précédent">
      <span class="chevron"></span>
    </button>

    <span class="sgilt-date-filter_date">
      <SgiltDatePicker v-model="modelValue" />
    </span>

    <button class="nav-btn next" @click="nextDay()" aria-label="Jour suivant">
      <span class="chevron"></span>
    </button>
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import dayjs from 'dayjs'

const modelValue = defineModel<Date | undefined>()

const prevDay = () => {
  if (modelValue.value) modelValue.value = dayjs(modelValue.value).subtract(1, 'day').toDate()
}

const nextDay = () => {
  if (modelValue.value) modelValue.value = dayjs(modelValue.value).add(1, 'day').toDate()
}
</script>

<style scoped lang="scss">
.sgilt-date-filter {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  width: 100%;

  // On s'assure que le container ne squeeze pas les boutons
  padding: 2px 8px;

  .nav-btn {
    // Le bouton devient un cercle tactile de 2.5rem (idéal mobile)
    width: 2.5rem;
    height: 2.5rem;
    display: flex;
    align-items: center;
    justify-content: center;

    background: transparent;
    border: 1px solid #f1f5f9; // Bordure très discrète
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    color: #94a3b8; // Gris doux par défaut

    &:hover {
      background: #f8fafc;
      border-color: #e2e8f0;
      color: #1e293b; // Noir profond au survol
    }

    &:active {
      transform: scale(0.92); // Petit effet d'enfoncement au clic
      background: #f1f5f9;
    }

    // Dessin du chevron en CSS pur
    .chevron {
      display: block;
      width: 0.625rem;
      height: 0.625rem;
      border-top: 2px solid currentColor;
      border-left: 2px solid currentColor;
    }

    &.prev .chevron {
      transform: rotate(-45deg);
      margin-left: 0.25rem; // Compensation optique pour centrer
    }

    &.next .chevron {
      transform: rotate(135deg);
      margin-right: 0.25rem; // Compensation optique
    }
  }

  &_date {
    flex: 1;
    // On centre le texte du datepicker
    display: flex;
    justify-content: center;

    :deep(.sgilt-date-picker) .dp__input {
      // On retire les bordures internes pour que la date semble flotter
      border: none;
      text-align: center;
      font-weight: 600;
      font-size: 0.95rem;
      background: transparent;
    }
  }
}
</style>
