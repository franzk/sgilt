<template>
  <div class="reservation-form">
    <!-- Header Section -->
    <div class="reservation-header">
      <h2>Réservez maintenant</h2>
      <p class="subtitle">Validez votre réservation en quelques clics !</p>
    </div>

    <!-- Body Section -->
    <div class="reservation-body">
      <!-- Datepicker -->
      <div class="form-group">
        <p>Choisissez une date :</p>
        <SgiltDatePicker v-model="selectedDate" />
      </div>

      <!-- Select options -->
      <div class="form-group">
        <p>Choisissez un service :</p>
        <SgiltSelect :options="pricesOptions" v-model="selectedOption" />
      </div>

      <!-- Pricing -->
      <div class="pricing">
        <p>💰 Tarif estimé :</p>
        <p class="price">{{ calculatedPrice }} €</p>
      </div>
    </div>

    <!-- Footer Section -->
    <div class="reservation-footer">
      <SgiltSimpleButton @click="handleBooking">Réserver</SgiltSimpleButton>
      <div class="contact">Besoin d'assistance ?&nbsp;<u>Contactez-nous</u></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect, { type SgiltSelectOption } from '@/components/basics/inputs/SgiltSelect.vue'
import SgiltSimpleButton from '../basics/buttons/SgiltSimpleButton.vue'
import type { Price } from '@/domain/Partner'

const selectedDate = defineModel<Date>('selected-date')
const selectedPrice = defineModel<Price>('selected-price')

const props = defineProps<{
  prices: Price[]
}>()

const selectedOption = ref<SgiltSelectOption>()
selectedPrice.value = props.prices?.[0] || {}

const pricesOptions = props.prices.map((price) => ({
  value: price.id,
  label: price.title,
}))

watch(
  () => selectedOption.value,
  (newValue) => {
    selectedPrice.value = props.prices.find((price) => price.id === newValue?.value)
  },
)

// Computed property to calculate the price
const calculatedPrice = computed(() => {
  return selectedPrice.value?.price || 0
})

// Booking submission logic
function handleBooking() {
  console.log('Booking submitted!')
}
</script>

<style scoped lang="scss">
/* Main container */
.reservation-form {
  background: #ffffff;
  padding: 30px;
  border-radius: 15px; /* Rounded corners */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1); /* Subtle shadow */
  max-width: 450px;
  margin: $spacing-m auto; /* Centered */

  h2 {
    font-size: 24px;
    color: #333;
    margin: 0 0 20px 0;
  }

  /* Form groups */
  .form-group {
    margin-bottom: 20px;

    p {
      font-weight: bold;
      margin-bottom: 8px;
      display: block;
    }
  }

  /* Pricing section */
  .pricing {
    text-align: left;
    margin-bottom: 20px;

    .price {
      font-size: 24px;
      color: #fcb900;
      font-weight: bold;
    }
  }

  .reservation-footer {
    display: flex;
    flex-direction: column;
    align-items: center; /* Centre les boutons */
  }

  /* Contact link */
  .contact {
    margin-top: 10px;
    color: #333;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    font-style: italic;

    svg {
      margin-right: 5px;
    }
  }
}
</style>
